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

package API.EASTAPI;

import JClouds_Adapter.KeystoneTest;
import JClouds_Adapter.OpenstackInfoContainer;
import MDBInt.FederatedCloud;
import MDBInt.FederatedUser;
import MDBInt.FederationUser;
import OSFFMIDM.SimpleIDM;
import java.util.HashMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
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
@Path("/fednet/eastBr/user")
public class UsersResource {

    @Context
    private UriInfo context;
    private SimpleIDM sidm;
    static final Logger LOGGER = Logger.getLogger(UsersResource.class);
    /**
     * Creates a new instance of UsersResource
     */
    public UsersResource() {
        sidm=new SimpleIDM();
    }

    /**
     * Retrieves representation of an instance of EASTAPI.UsersResource
     * @return an instance of java.lang.String
     * 
     */
    @PUT
    @Path("/validate_user")
    @Consumes("application/json")
    @Produces("application/json")
    public String validate_user(String content) {
        //VERIFICARE L'APPROCCIO NELL'INTERAZIONE CON IL FEDSDN
        JSONObject reply=new JSONObject();
        JSONParser parser= new JSONParser();
        JSONObject input=null;
        String username=null;
        String tenant=null;
        String cloud=null;
        String pass=null;
        String cmp_endpoint=null;
        String region=null;
        try 
        {
            input=(JSONObject) parser.parse(content);
            username=((String)input.get("username")).split("@")[0];
            tenant=((String)input.get("username")).split("@")[1];
            cloud=((String)input.get("username")).split("@")[2];
            pass=(String)input.get("password");
            region=(String)input.get("region");
        }
        catch(ParseException pe)
        {
            //something TODO
            LOGGER.error("INPUT_JSON_UNPARSABLE: OPERATION ABORTED");
            reply.put("returncode", 1); 
            reply.put("errormesg", "INPUT_JSON_UNPARSABLE: OPERATION ABORTED");
            reply.put("token",null);
            reply.put("tenant_id", null);
            return reply.toJSONString();
        }
        cmp_endpoint=sidm.getcmp_endpointFederated(tenant, cloud);
        OpenstackInfoContainer oic=new OpenstackInfoContainer(cloud,cmp_endpoint,tenant,username,pass,region);
        //costruzione oggetto Openstackinfocontainer, e verifica delle credenziali attraverso il modulo di keystone 
        //fornito da jclouds
        KeystoneTest key=new KeystoneTest(tenant,username,pass,cmp_endpoint);
        HashMap hm=key.getToken(tenant, username, pass);
        String token=(String)hm.get("ID");
        //When FEDSDN will take in count token expiration date we will use this token as
        ////output parameter that will be returned to it. For the moment we will return a static 
        //////token taken  from MongoDb 
        token=sidm.getFederationToken(tenant, username);
        if((String)hm.get("ID")==null)
        {
            LOGGER.debug("User not Valid!");
            reply.put("returncode", 1); 
            reply.put("errormesg", "User not Valid!");
            reply.put("token",null);
            reply.put("tenant_id", null);
            return reply.toJSONString();
        }
        else if(token==null)
        {
            LOGGER.error("It is impossible retrieve token");
            reply.put("returncode", 1); 
            reply.put("errormesg", "It is impossible retrieve token: OPERATION ABORTED");
            reply.put("token",null);
            reply.put("tenant_id", null);
            return reply.toJSONString();
        }
        String tenant_id=key.getTenantId(tenant);
        reply.put("returncode", 0); // or reply.put("returncode", 1);
        reply.put("errormesg", "None"); //or reply.put("errormesg", "Mesg");
        reply.put("token",token);
        reply.put("tenant_id", tenant_id);
        return reply.toJSONString();
    }
    
    /**
     * Sub-resource locator method for validate_user
    */
    /*@Path("{user_id}/info")
    
    public UserResource getUserResource(@PathParam("user_id") String userid)) {
        return UserResource.getInstance(userid);
    }*/
}
