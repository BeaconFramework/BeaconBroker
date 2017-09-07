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





import JClouds_Adapter.NovaTest;
import JClouds_Adapter.OpenstackInfoContainer;
import MDBInt.DBMongo;
import MDBInt.MDBIException;
import MDBInt.Splitter;



//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;

/*import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import java.util.LinkedHashMap;
import java.util.Set;*/
import java.util.Iterator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;
import org.jclouds.openstack.neutron.v2.domain.Port;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.yaml.snakeyaml.Yaml;
//import utils.FileFunction;

/**
 *
 * @author Giuseppe Tricomi
 */

@Path("/os2os/orchestrator")
public class migrationVM {

    @Context
    private UriInfo context;
    static final Logger LOGGER = Logger.getLogger(migrationVM.class);
    Splitter spli;
    DBMongo m;
     /**
     * Creates a new instance of SitesResource
     */
    public migrationVM() {
        //LOGGER.error("test");
        //this.init("../webapps/OSFFM/WEB-INF/Configuration_NTHBR_WS.xml");
        
        this.m=new DBMongo();
        //this.m.init();
       // this.m.init("../webapps/OSFFM/WEB-INF/Configuration_bit");
        this.m.connectLocale("10.9.240.1");//this.m.getMdbIp());
        
    }
    
    /**
     * This function Istantiate all Stack described inside global Manifest.
     * @param tenant
     * @param jsonInput
     * @return 
     * @author gtricomi
     */
    @POST
    @Path("/migrateVMs/")
    @Consumes("application/json")
    public Response migrateVMs(String jsonInput)
    {
        //String vm,String tenant,String userFederation,String vmTwin,String pswFederation,String region
        String vm="";
        String tenant="";
        String userFederation="";
        String vmTwin="";
        String pswFederation="";
        String region="";
        JSONObject input=new JSONObject();
        org.json.simple.JSONObject reply=new org.json.simple.JSONObject();
        JSONParser jp=new JSONParser();
        try{
            input=(JSONObject)jp.parse(jsonInput);
            vm=(String)input.get("vm");
            if(vm==null){
                LOGGER.error("parameter vm cannot be a null");
                return this.createErrorAnswer("parameter vm cannot be a null");
            }
            userFederation=(String)input.get("userFederation");
            pswFederation=(String)input.get("pswFederation");
            tenant=(String)input.get("tenant");
            vmTwin=(String)input.get("vmTwin");
            if(vmTwin==null){
                LOGGER.error("parameter vmTwin cannot be a null");
                return this.createErrorAnswer("parameter vmTwin cannot be a null");
            }
               
        }catch(Exception e){
            LOGGER.error("JSON  input received for web service startTemplates is not parsable.\n"+e.getMessage());
            return this.createErrorAnswer("INPUT_JSON_UNPARSABLE: OPERATION ABORTED "+e.getMessage());
        }
        ///spegnimento vm
        ////recupero runtimeinfo
        String runTime=this.m.getRunTimeInfo(tenant, vm);
        String idClo="",endpoint="",cred="", twinUUID="";
        OpenstackInfoContainer credential=null,credential2=null;
        JSONObject credJobj=null,runJobj=null;
        try{
            runJobj=new JSONObject(runTime);
            ////recupero idCloud
            idClo=runJobj.getString("idCloud");
            ////recupero le credenziali passando da quelle di federazione
            cred=this.m.getFederatedCredential(tenant, userFederation, pswFederation,idClo);
            credJobj=new JSONObject(cred);
            try {
                endpoint=(new JSONObject(this.m.getDatacenter(tenant, idClo))).getString("idmEndpoint");
            } catch (MDBIException ex) {
                LOGGER.error( ex);
            }
            credential=new OpenstackInfoContainer(idClo,endpoint,tenant,credJobj.getString("federatedUser"),credJobj.getString("federatedPassword"),region);
        }
        catch(JSONException je){
             LOGGER.error("An error is occourred in JSON crederntial manipulation."+je.getMessage());
             return this.createErrorAnswer("An error is occourred in JSON crederntial manipulation."+je.getMessage());
        }
        ////spengo la vm
        NovaTest nova=new NovaTest(credential.getEndpoint(),credential.getTenant(), credential.getUser(),credential.getPassword(),credential.getRegion());
        nova.stopVm(vm);
        // identificazione nuova vm
        try{
            twinUUID=vmTwin;
            ////recupero runtimeinfo
            runTime=this.m.getRunTimeInfo(tenant, twinUUID);
            runJobj=new JSONObject(runTime);
            ////recupero idCloud
            idClo=runJobj.getString("idCloud");
            ////recupero le credenziali passando da quelle di federazione
            cred=this.m.getFederatedCredential(tenant, userFederation, pswFederation,idClo);
            credJobj=new JSONObject(cred);
            try {
                endpoint=(new JSONObject(this.m.getDatacenter(tenant, idClo))).getString("idmEndpoint");
            } catch (MDBIException ex) {
               LOGGER.error(ex);
            }
            credential2=new OpenstackInfoContainer(idClo,endpoint,tenant,credJobj.getString("federatedUser"),credJobj.getString("federatedPassword"),region);
        }
        catch(JSONException je){
             LOGGER.error("An error is occourred in JSON crederntial manipulation.");
             return this.createErrorAnswer("An error is occourred in JSON crederntial manipulation."+je.getMessage());
        }
        //accensione vm idenitificata
        nova=new NovaTest(credential2.getEndpoint(),credential2.getTenant(), credential2.getUser(),credential2.getPassword(),credential2.getRegion());
        nova.startVm(twinUUID);
        //restituzione dettagli vm spenta- rete, vm accesa- rete
        /*LOGGER.debug("Network infoes of the shutted down VM(identified by UUID:"+vm+"):");
        Iterator it_tmpar=this.m.getportinfoes(tenant, vm).iterator();
        while(it_tmpar.hasNext())
            LOGGER.debug((String)it_tmpar.next());
        LOGGER.debug("Network infoes of the twin VM started(identified by UUID:"+twinUUID+"):");
        it_tmpar=this.m.getportinfoes(tenant, twinUUID).iterator();
        while(it_tmpar.hasNext())
            LOGGER.debug((String)it_tmpar.next());
        */
        return this.createValidAnswer();
    }
    
    /**
     * This function Istantiate all Stack described inside global Manifest.
     * @param tenant
     * @param jsonInput
     * @return 
     * @author gtricomi
     */
    @POST
    @Path("/activatetwin/")
    @Consumes("application/json")
    public Response activatetwin(String jsonInput)
    {
        //String vm,String tenant,String userFederation,String vmTwin,String pswFederation,String region
        String tenant="";
        String userFederation="";
        String vmTwin="";
        String pswFederation="";
        String region="RegionOne";
        JSONObject input=new JSONObject();
        org.json.simple.JSONObject reply=new org.json.simple.JSONObject();
        JSONParser jp=new JSONParser();
        try{
            input=(JSONObject)jp.parse(jsonInput);
            userFederation=(String)input.get("userFederation");
            pswFederation=(String)input.get("pswFederation");
            tenant=(String)input.get("tenant");
            vmTwin=(String)input.get("vmTwin");
            if(vmTwin==null){
                LOGGER.error("parameter vmTwin cannot be a null");
                return this.createErrorAnswer("parameter vmTwin cannot be a null");
            }
               
        }catch(Exception e){
            LOGGER.error("JSON  input received for web service activateTwin is not parsable.\n"+e.getMessage());
            return this.createErrorAnswer("INPUT_JSON_UNPARSABLE: OPERATION ABORTED "+e.getMessage());
        }
        String idClo="",endpoint="",cred="", twinUUID="";
        JSONObject credJobj=null,runJobj=null;
      
        OpenstackInfoContainer credential2=null;
        // identificazione nuova vm
        try{
            twinUUID=vmTwin;
            ////recupero runtimeinfo
            String runTime=this.m.getRunTimeInfo(tenant, twinUUID);
            runJobj=new JSONObject(runTime);
            ////recupero idCloud
            idClo=runJobj.getString("idCloud");
            ////recupero le credenziali passando da quelle di federazione
            cred=this.m.getFederatedCredential(tenant, userFederation, pswFederation,idClo);
            credJobj=new JSONObject(cred);
            try {
                endpoint=(new JSONObject(this.m.getDatacenter(tenant, idClo))).getString("idmEndpoint");
            } catch (MDBIException ex) {
               LOGGER.error(ex);
            }
            credential2=new OpenstackInfoContainer(idClo,endpoint,tenant,credJobj.getString("federatedUser"),credJobj.getString("federatedPassword"),region);
        }
        catch(JSONException je){
             LOGGER.error("An error is occourred in JSON crederntial manipulation.");
             return this.createErrorAnswer("An error is occourred in JSON crederntial manipulation."+je.getMessage());
        }
        //accensione vm idenitificata
        NovaTest nova=new NovaTest(credential2.getEndpoint(),credential2.getTenant(), credential2.getUser(),credential2.getPassword(),credential2.getRegion());
        nova.startVm(twinUUID);
        return this.createValidAnswer();
    }
    
    private Response createErrorAnswer(String errorMess){
        org.json.simple.JSONObject reply=new org.json.simple.JSONObject();
        reply.put("returncode", 1);
        reply.put("errormesg", errorMess);
        return this.createErrorAnswer(reply);
    }
    private Response createErrorAnswer(org.json.simple.JSONObject errorMess){
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorMess)
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }
    
    private org.json.simple.JSONObject createErrorAnswer(String errrorMesg,String customkey,Object customValue){
        org.json.simple.JSONObject reply=new org.json.simple.JSONObject();
        reply.put("returncode", 1);
        reply.put("errormesg", errrorMesg);
        reply.put(customkey, customValue); 
        return reply;
    }
    
    private Response createValidAnswer(){
        org.json.simple.JSONObject reply=new org.json.simple.JSONObject();
        reply.put("returncode", 0);
        reply.put("errormesg", "");
        return Response.status(Response.Status.ACCEPTED)
                .entity(reply)
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }
    
 /*   private void take_time_instant(FileWriter fw, String begin) throws Exception
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(begin+sdf.format(cal.getTime()));
        bw.close();
    }*/
}
