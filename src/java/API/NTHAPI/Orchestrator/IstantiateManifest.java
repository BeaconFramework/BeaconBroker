/**Copyright 2016, University of Messina.
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

package API.NTHAPI.Orchestrator;



import API.NTHAPI.SitesResource;
import JClouds_Adapter.OpenstackInfoContainer;
import MDBInt.DBMongo;
import MDBInt.MDBIException;
import MDBInt.Splitter;
import OSFFM_ORC.OrchestrationManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Level;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;
import org.jclouds.openstack.neutron.v2.domain.Port;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.yaml.snakeyaml.Yaml;
import utils.FileFunction;

/**
 *
 * @author Giuseppe Tricomi
 */

@Path("/os2os/orchestrator")
public class IstantiateManifest {

    @Context
    private UriInfo context;
    static final Logger LOGGER = Logger.getLogger(IstantiateManifest.class);
    Splitter s;
    DBMongo m;
     /**
     * Creates a new instance of SitesResource
     */
    public IstantiateManifest() {
        //LOGGER.error("test");
        //this.init("../webapps/OSFFM/WEB-INF/Configuration_NTHBR_WS.xml");
        
        this.m=new DBMongo();
        this.m.init();
       // this.m.init("../webapps/OSFFM/WEB-INF/Configuration_bit");
        this.m.connectLocale(this.m.getMdbIp());
        this.s=new Splitter(m);
    }
    
    /**
     * This function Istantiate all Stack described inside global Manifest.
     * @param tenant
     * @param jsonInput
     * @return 
     * @author gtricomi
     */
    @POST
    @Path("/{tenant}/startTemplates/")
    @Consumes("application/json")
    @Produces("application/json")
    public String istantiateManifest(
            @PathParam("tenant") String tenant,
            String jsonInput
    ) {
        //BEACON>>> INSIDE THIS FUNCTION WE NEED TO ADD SOME AUTHENTICATION STUFF, FOR THE MOMENT IS A 
        //SIMPLE UNAUTHENTICATING OPERATION
        String templatename="";//templatename="cc228189-0f2f-4aa4-8336-88db88e477d2";
        //String home=System.getProperty("java.home");      //Removed, unused
        //String fs=System.getProperty("file.separator");   //Removed, unused
        //String prepath=home+fs+"subrepoTemplate";
        String prepath="./subrepoTemplate/"+tenant;
        JSONObject input=new JSONObject(),reply=new JSONObject();
        JSONParser jp=new JSONParser();
        try{
            input=(JSONObject)jp.parse(jsonInput);
            templatename=(String)input.get("templateId");
        }catch(Exception e){
            LOGGER.error("JSON  input received for web service startTemplates is not parsable.\n"+e.getMessage());
            reply.put("returncode", 1); 
            reply.put("errormesg", "INPUT_JSON_UNPARSABLE: OPERATION ABORTED "+e.getMessage());
            
            return reply.toJSONString();
        }
        String tmpStr="";
        try{
            tmpStr = s.ricomponiYamlManifest(templatename, tenant);
        }
        catch(MDBIException e ){
            LOGGER.error(e.getMessage());
            reply.put("returncode", 1); 
            reply.put("errormesg", "OPERATION ABORTED "+e.getMessage());
           
            return reply.toJSONString();
        }
        Yaml yaml = new Yaml();
        LinkedHashMap<String, Object> list = (LinkedHashMap<String, Object>) yaml.load(tmpStr);
        org.json.JSONObject jo=new org.json.JSONObject(list);
        OrchestrationManager om=new OrchestrationManager();
        File f=new File(prepath);
        if (!f.exists()) {
            if(!f.mkdirs()){
                LOGGER.error("It's impossible create TMP file for manifest istantiation; OPERATION ABORTED.");
                reply.put("returncode", 1); 
                reply.put("errormesg", "It's impossible create TMP file for manifest istantiation; OPERATION ABORTED.");
                return reply.toJSONString();
            }
        }
        //BEACON>>> There is another om.manifestistantiation created for dashboard but it isn't complete, for the moment
        String manifestName=templatename;
        System.out.println("The Manifest "+manifestName+" analysis process started....");
        om.manifestinstatiation(manifestName,jo,tenant);
        HashMap<String,ArrayList<ArrayList<String>>> tmpMap=om.managementgeoPolygon(manifestName, this.m, tenant);
        //retrieve from MongoDb federation password for federation user
        if(tmpMap==null)
        {
            LOGGER.info("No one Datacenter is found for manifest instantiation.\n");
            reply=this.createErrorAnswer("No one Datacenter is found for manifest instantiation");
            return reply.toJSONString();
        }
        String tmp=this.m.getFederationCredential(tenant, "userFederation","federationUser");
        //System.out.println(tmp);
        try {
            org.json.JSONObject tj=new org.json.JSONObject(tmp);
        } catch (JSONException ex) {
            LOGGER.error("Error occurred in manifest istantiation; OPERATION ABORTED."+ex.getMessage());
            ex.printStackTrace();
            reply=this.createErrorAnswer("Error occurred in manifest istantiation; OPERATION ABORTED."+ex.getMessage());
            return reply.toJSONString();
        }
        
        HashMap<String, ArrayList<ArrayList<OpenstackInfoContainer>>> tmpMapcred = om.managementRetrieveCredential(tmpMap, m, tenant, "userFederation", "passwordFederation", "RegionOne");
        //////////////////////////////////////////////////////////////////////////////////
        //+tenant+rootName+"_"+(String)obj[index]
        
        Set<String> setStack = om.getSGList(manifestName);
        //BEACON>>> this step it will be substitude by a function that analize the manifest and retireve the ServiceManagementGroups 
            //stored inside global manifest
        for (String stack : setStack) {

            ///String stack = "federation2";
            FileFunction ff = new FileFunction();
            String template = ff.readFromFile(prepath + "/" + tenant + manifestName + "_" + stack);
            ArrayList<ArrayList<HashMap<String, ArrayList<Port>>>> arMapRes =om.deployManifest(template, stack, tmpMapcred, tmpMap, this.m);
            //BEACON:>>> It is needed decide what do with the info returned from om.deployManifest inside strucure arMapRes
        }
        om.prepareNetTables4completeSharing(tenant, manifestName, tmpMapcred, m);
        String[]entries = f.list();
        for(String s: entries){
            File currentFile = new File(f.getPath(),s);
            currentFile.delete();
        }
        f.delete();//at the end
        reply.put("returncode", 0);
        reply.put("errormesg", "");
        return reply.toJSONString();
    }
    
    private JSONObject createErrorAnswer(String errrorMesg){
        JSONObject reply=new JSONObject();
        reply.put("returncode", 1);
        reply.put("errormesg", errrorMesg);
        reply.put("templateuuid", null); 
        return reply;
    }
    
    
}
