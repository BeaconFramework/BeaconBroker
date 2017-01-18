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

import JClouds_Adapter.KeystoneTest;
import MDBInt.FederatedUser;
import OSFFMIDM.SimpleIDM;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.jdom2.Element;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import utils.ParserXML;
import org.apache.log4j.Logger;
/**
 * REST Web Service
 *
 * @author Giuseppe Tricomi
 */
@Path("/fednet/northBr/site/{sitename}/{tenantname}/users")
public class UserssResource {

    @Context
    private UriInfo context;
    private String Config_file="/webapps/OSFFM/WEB-INF/Configuration_NTHBR_WS.xml";
    static final Logger LOGGER = Logger.getLogger(UserssResource.class);
    private ParserXML parser;
    private String fedSDNTarget; //it will be used to make request to web service with Client4WS class
    /**
     * Creates a new instance of UserssResource
     */
    public UserssResource() {
        String file=System.getenv("HOME");
        this.init(file+Config_file);
    }
         
    public void init(String file) {
        Element params;
        try {
            parser = new ParserXML(new File(file));
            params = parser.getRootElement().getChild("pluginParams");
            fedSDNTarget = params.getChildText("fedSDNTarget");
        } //init();
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Retrieves list of users for site and tenant
     * @param sitename resource URI parameter
     * @param tenantname resource URI parameter
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getusers_tenantInSite(@PathParam("sitename") String sitename, @PathParam("tenantname") String tenantname) {
        //TODO return proper representation object
        JSONObject reply=new JSONObject();
        //something TODO 4 get  logic
        String cmd_endpoint=null; //estrapolare da mongodb le info relative a questo elemento in funzione del sitename
        ArrayList response=this.listUsers(tenantname, cmd_endpoint);
        
        reply.put("returncode", 0); // or reply.put("returncode", 1);
        reply.put("errormesg", "None"); //or reply.put("errormesg", "Mesg");
        //Logica di inserimento elementi all'interno di un json array
        JSONArray jsonUserList= new JSONArray();
        reply.put("uesrList", jsonUserList);
        //reply.put("tenant",context.getPathParameters().getFirst("tenantname") ); // momentaneo da decidere se tenere o togliere
        //reply.put("site",context.getPathParameters().getFirst("sitename")); // momentaneo da decidere se tenere o togliere
        
        return reply.toJSONString();
    }
    
    
    /**
     * Retrieves list of users for site and tenant
     * @param sitename resource URI parameter
     * @param tenantname resource URI parameter
     * @return an instance of java.lang.String
     */
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public String getusers_tenant(String content) {
        JSONObject reply=new JSONObject();
        JSONParser parser= new JSONParser();
        JSONObject input=null;
        String tenant=null;
        String token=null;
        String cmp_endpoint=null;
        try 
        {
            input=(JSONObject) parser.parse(content);
            tenant=(String)input.get("tenant");
            token=(String)input.get("token");
            cmp_endpoint=(String)input.get("cmp_endpoint");
        }
        catch(ParseException pe)
        {
            //something TODO
            reply.put("returncode", 1); 
            reply.put("errormesg", "INPUT_JSON_UNPARSABLE: OPERATION ABORTED");
            reply.put("userList", null);
            return reply.toJSONString();
        }
        ArrayList userList=this.listUsers(tenant, cmp_endpoint);
        //Logica di inserimento elementi all'interno di un json array
        JSONArray jsonUserList= new JSONArray();
        reply.put("uesrList", jsonUserList);
        reply.put("returncode", 0); // or reply.put("returncode", 1);
        reply.put("errormesg", "None"); //or reply.put("errormesg", "Mesg");
        return reply.toJSONString();
    }
//funzione usata per recuperare la lista degli users di una cloud federata
    private ArrayList listUsers(String tenant,String cmd_endpoint){
        return new ArrayList();
    }
    
    /**
     * DELETE method for resource Users
     * @param userid
     * @return 
     */
    @DELETE
    @Produces("application/json")
    @Consumes("application/json")
    public String delete(String content) {
        JSONObject reply = new JSONObject();
        JSONObject input = null;
        JSONParser parser = new JSONParser();
        String username=null;
        String tenant=null;
        String token = null;
        String cmp_endpoint = null;
        try {
            input=(JSONObject) parser.parse(content);
            username=((String)input.get("username")).split("@")[0];
            tenant=((String)input.get("username")).split("@")[1];
            token=(String)input.get("token");
            cmp_endpoint=(String)input.get("cmp_endpoint");
        }
        catch(ParseException pe)
        {
            reply.put("returncode", 1); 
            reply.put("errormesg", "INPUT_JSON_UNPARSABLE: OPERATION ABORTED");
            return reply.toJSONString();
        }

        //something TODO 4 delete Logic
        
        reply.put("returncode", 0); // or reply.put("returncode", 1);
        reply.put("errormesg", "None"); //or reply.put("errormesg", "Mesg");
        return reply.toJSONString();
    }
    

    /**
     * Sub-resource locator method for {sitename}/{tenantname}
     */
    @Path("{sitename}/{tenantname}")
    public Users getUsers(@PathParam("sitename") String sitename, @PathParam("tenantname") String tenantname) {
        return Users.getInstance(sitename, tenantname);
    }
    
    
    
    
    
    @GET
    @Path("/token")
    @Produces("application/json")
    @Consumes("application/json")
    public String getFederationToken(String content) {
        JSONObject reply = new JSONObject();
        JSONObject input = null;
        JSONParser parser = new JSONParser();
        String username = null;
        String tenant = null;
        String token = null;
        String cmp_endpoint = null;
        SimpleIDM sidm;
        String cloud = null;
        String pass = null;

        try {
            input = (JSONObject) parser.parse(content);
            username = (String) input.get("username");
            tenant = (String) input.get("tenant");
            //token=(String)input.get("token");
            cmp_endpoint = (String) input.get("cmp_endpoint");
            pass = (String) input.get("passwordUser");
        } catch (ParseException pe) {
            reply.put("returncode", 1);
            reply.put("errormesg", "INPUT_JSON_UNPARSABLE: OPERATION ABORTED");
            return reply.toJSONString();
        }

  
        sidm = new SimpleIDM(); //>>>BEACON: VERIFY THIS POINT
        String dbName = sidm.retrieve_TenantDB("federationTenant", tenant);
        sidm.setDbName(dbName);  //>>>BEACON: FOR THE MOMENT OUR TESTING DB IS CALLED beacon
        cloud = sidm.getCloudID(username, tenant, cmp_endpoint);
        // add check for cloud endpoint
        FederatedUser fu = sidm.retr_infoes_fromfedsdn(tenant, cloud);

        if (!fu.getPassword().equals(pass.toString())) {
            LOGGER.debug("User not Valid!");
            reply.put("returncode", 1);
            reply.put("errormesg", "Password is not correct!");
            reply.put("token", "");
            reply.put("tenant_id", "");
            return reply.toString();
        }
        KeystoneTest key = new KeystoneTest(tenant, fu.getUser(), fu.getPassword(), cmp_endpoint);
        HashMap hm = key.getToken(tenant, fu.getUser(), fu.getPassword());
        token = (String) hm.get("ID");

        //When FEDSDN will take in count token expiration date we will use this token as
        ////output parameter that will be returned to it. For the moment we will return a static 
        //////token taken from MongoDb 
        token = sidm.getFederationToken(tenant, username);

        if ((String) hm.get("ID") == null) {
            LOGGER.debug("User not Valid!");
            reply.put("returncode", 1);
            reply.put("errormesg", "User not Valid!");
            reply.put("token", "");
            reply.put("tenant_id", "");
            return reply.toString();
        } else if (token == null) {
            LOGGER.debug("It is impossible retrieve token");
            reply.put("returncode", 1);
            reply.put("errormesg", "It is impossible retrieve token: OPERATION ABORTED");
            reply.put("token", "");
            reply.put("tenant_id", "");
            return reply.toString();
        }
        String tenant_id = key.getTenantId(tenant);
        reply.put("returncode", 0);
        reply.put("errormesg", "None");
        reply.put("token", token);
        reply.put("tenant_id", tenant_id);

        reply.put("returncode", 0); // or reply.put("returncode", 1);
        reply.put("errormesg", "None"); //or reply.put("errormesg", "Mesg");
        return reply.toJSONString();
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
