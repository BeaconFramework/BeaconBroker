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

package API.NTHAPI;

import static API.NTHAPI.SitesResource.LOGGER;
import MDBInt.DBMongo;
import MDBInt.Splitter;
import OSFFMIDM.SimpleIDM;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * REST Web Service
 *
 * @author gtricomi
 */
@Path("/os2os/notrhBr/manifest")
public class ManifestcreatesResource {

    @Context
    private UriInfo context;
    static final Logger LOGGER = Logger.getLogger(ManifestcreatesResource.class);
    DBMongo m;
    Splitter s;
    /**
     * Creates a new instance of ManifestcreatesResource
     */
    public ManifestcreatesResource() {
       this.m=new DBMongo(); 
       this.s=new Splitter(m);
    }

    /**
     * Retrieves representation of an instance of API.NTHAPI.ManifestcreatesResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * Sub-resource locator method for {name}
     */
    @Path("{name}")
    public Manifestcreate getManifestcreate(@PathParam("name") String name) {
        return Manifestcreate.getInstance(name);
    }
    
    /**
     * 
     * @param siteid
     * @return 
     * @author gtricomi
     */
    @GET
    @Path("/{site_id}")
    @Produces("application/json")
    public String getSiteInfo(@PathParam("site_id") String siteid) {
        JSONObject reply=new JSONObject();
        //TODO return proper representation object
        LOGGER.error("This is a logging statement from log4j");
        try{
        SimpleIDM si=new SimpleIDM();
        }
        catch(Exception ec){
            LOGGER.error(ec.getMessage());
            ec.printStackTrace();
        }
        reply.put("uuid", "Value1_"+siteid);
        reply.put("name", "Value2");
        reply.put("location", "Value3");
        reply.put("Available4Tenant",true); // or false
        
        reply.put("returncode", 0);     //or reply.put("returncode", 1);
        reply.put("errormesg", "None"); //or reply.put("errormesg", "Mesg");
        return reply.toJSONString();
    }
    
    /**
     * Web Service called to insert inside MongoDB 
     * @param msg
     * @param tenant
     * @return 
     * @author gtricomi
     */
    @POST
    @Path("/{tenant}/templates/")
    @Produces("application/json")
    @Consumes("application/json")
    public String insertManifestonMongoDB( 
            String msg,
            @PathParam("tenant") String tenant
    ) {
        String username="",templateName="",templateRef="",templates="";
        JSONObject reply=new JSONObject();
        //retrieve input from JSONObject
        try{
            JSONParser parser=new JSONParser();
            JSONObject input=(JSONObject)parser.parse(msg);
            username=(String)input.get("username");
            templateName=(String)input.get("templateName");
            templateRef=(String)input.get("templateRef");
            templates=(String)input.get("templates");
        }
        catch(Exception e){
            LOGGER.error("JSON  input received for web service startTemplates is not parsable.\n"+e.getMessage());
            reply.put("returncode", 1); 
            reply.put("errormesg", "INPUT_JSON_UNPARSABLE: OPERATION ABORTED "+e.getMessage());
            return reply.toJSONString();
        }
        if(!s.loadFromYAMLString(templates, tenant,username, templateName,templateRef))
        {
            LOGGER.error("Something going wrong in saving Manifest on MONGODB operation");
            reply.put("returncode", 1); 
            reply.put("errormesg", "Something going wrong in saving Manifest on MONGODB operation");
            return reply.toJSONString();
        }
        reply.put("returncode", 0);     //or reply.put("returncode", 1);
        reply.put("errormesg", "None"); //or reply.put("errormesg", "Mesg");
        return reply.toJSONString();
    }
}
