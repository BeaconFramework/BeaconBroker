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

import JClouds_Adapter.NeutronTest;
import JClouds_Adapter.OpenstackInfoContainer;
import MDBInt.FederatedUser;
import MDBInt.FederationUser;
import OSFFMIDM.SimpleIDM;
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
 * @author giusimone
 */
@Path("/eastBr/network")
public class NetworksegmentResource {

    @Context
    private UriInfo context;
    private SimpleIDM sidm;
    static final Logger LOGGER = Logger.getLogger(NetworksegmentResource.class);
    /**
     * Creates a new instance of NetworksegmentResource
     */
    public NetworksegmentResource() {
        sidm=new SimpleIDM();
    }

    /**
     * Retrieves representation of an instance of EASTAPI.NetworksegmentResource
     * @return an instance of java.lang.String
     */
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public String add_netSegment(String content) {
        //TODO return proper representation object
        //throw new UnsupportedOperationException();
        JSONObject reply=new JSONObject();
        JSONParser parser= new JSONParser();
        JSONObject input=null;
        String OSF_network_segment_id=null;
        String OSF_cmp_endpoint=null;
        String OSF_token=null;
        String OSF_tenant=null;
        String OSF_user=null;
        String OSF_region=null;
        try 
        {
            //retrieve JSON value from REST request
            input=(JSONObject) parser.parse(content);
            OSF_tenant=(String)input.get("tenant");
            OSF_user=(String)input.get("user");
            OSF_token=(String)input.get("token");
            OSF_network_segment_id=(String)input.get("network_segment_id");
            OSF_cmp_endpoint=(String)input.get("cmp_endpoint");
            //ricavare dal simple IDM gli elementi che mi mancano ovvero:
            //String endpoint, String tenant, String user, String password, String region
            FederationUser fu=sidm.getFederationU(OSF_token, OSF_cmp_endpoint);
            FederatedUser tmp=sidm.retr_infoes_fromfedsdn(OSF_token, OSF_cmp_endpoint);
            OpenstackInfoContainer credential=null;
            NeutronTest neutron=null;
            if(tmp!=null && fu!=null)
            {
                credential=new OpenstackInfoContainer(OSF_cmp_endpoint,fu.getUser(),tmp.getUser(),tmp.getPassword(),tmp.getRegion());
                neutron=new NeutronTest(credential.getEndpoint(),credential.getTenant(), credential.getUser(),credential.getPassword(),credential.getRegion());
            }
            else
            {
                reply.put("returncode", 1); 
                reply.put("errormesg", "USER_AUTHENTICATION_EXCEPTION: OPERATION ABORTED");
                reply.put("network_info", null);
                LOGGER.error("USER_AUTHENTICATION_EXCEPTION: OPERATION ABORTED >>>[Token:"+OSF_token+",cmp_endpoint:"+OSF_cmp_endpoint+"]; No federated credential has found for selected parameters.");
                return reply.toJSONString();
            }
            neutron.listNetworks();
        
            
        }
        catch(ParseException pe)
        {
            reply.put("returncode", 1); 
            reply.put("errormesg", "JSON_INPUT_UNPARSABLE: OPERATION ABORTED");
            reply.put("network_info", null);
            return reply.toJSONString();
        }
        JSONObject network_info=null;
        // TODO invocare funzione che ottiene token da keystone per username 
        // del tenant all'indirizzo cmp_endpoint
        reply.put("returncode", 0); // or reply.put("returncode", 1);
        reply.put("errormesg", "None"); //or reply.put("errormesg", "Mesg");
        reply.put("network_info", network_info);
        return reply.toJSONString();
      /*  
        neutron.
        
        */
    }

    /**
     * Sub-resource locator method for /eastBr/network/
     */
    /*@Path("/eastBr/network/")
    public NetworkSegmentResource getNetworkSegmentResource() {
        return NetworkSegmentResource.getInstance();
    }*/
}
