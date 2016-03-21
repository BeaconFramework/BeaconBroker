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
@Path("/eastBr/FA_Management/")
public class LinksResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of LinksResource
     */
    public LinksResource() {
    }

    /**
     * Retrieves representation of an instance of WSTAPI.LinksResource
     * @return an instance of java.lang.String
     */
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public String LinkFuncitons(String content) {
        JSONObject reply=new JSONObject();
        JSONParser parser= new JSONParser();
        JSONObject input=null;
        try 
        {
            input=(JSONObject) parser.parse(content);
        }
        catch(ParseException pe)
        {
            reply.put("returncode", 1); 
            reply.put("errormesg", "INPUT_JSON_UNPARSABLE: OPERATION ABORTED");
            return reply.toJSONString();
        }
        reply.put("returncode", 0);
        reply.put("errormesg", "None");
        return reply.toJSONString();
    }

    /**
     * Sub-resource locator method for {name}
     */
    @Path("{name}")
    public Link getLink(@PathParam("name") String name) {
        return Link.getInstance(name);
    }
}
