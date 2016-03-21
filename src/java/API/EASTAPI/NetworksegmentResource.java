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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
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

    /**
     * Creates a new instance of NetworksegmentResource
     */
    public NetworksegmentResource() {
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
        String network_segment_id=null;
        String cmp_endpoint=null;
        String token=null;
        try 
        {
            input=(JSONObject) parser.parse(content);
            token=(String)input.get("token");
            network_segment_id=(String)input.get("network_segment_id");
            cmp_endpoint=(String)input.get("cmp_endpoint");
        }
        catch(ParseException pe)
        {
            reply.put("returncode", 1); 
            reply.put("errormesg", "INPUT_JSON_UNPARSABLE: OPERATION ABORTED");
            reply.put("network_info", null);
            return reply.toJSONString();
        }
        
        
        
        JSONObject network_info=null;
        // TODO invocare funzione che ottiene token da keystone per username del tenant all'indirizzo cmp_endpoint
        
               
        
        reply.put("returncode", 0); // or reply.put("returncode", 1);
        reply.put("errormesg", "None"); //or reply.put("errormesg", "Mesg");
        reply.put("network_info", network_info);
        return reply.toJSONString();
    }

    /**
     * Sub-resource locator method for /eastBr/network/
     */
    /*@Path("/eastBr/network/")
    public NetworkSegmentResource getNetworkSegmentResource() {
        return NetworkSegmentResource.getInstance();
    }*/
}
