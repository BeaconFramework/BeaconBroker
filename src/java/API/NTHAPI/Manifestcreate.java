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

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import OSFFM_ORC.OrchestrationManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
/**
 * REST Web Service
 *
 * @author Giuseppe Tricomi, giu.tricomi@gmail.com
 */
public class Manifestcreate {

    private String name;

    /**
     * Creates a new instance of Manifestcreate
     */
    private Manifestcreate(String name) {
        this.name = name;
    }

    /**
     * Get instance of the Manifestcreate
     */
    public static Manifestcreate getInstance(String name) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of Manifestcreate class.
        return new Manifestcreate(name);
    }

    /**
     * Retrieves representation of an instance of API.NTHAPI.Manifestcreate
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of Manifestcreate
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
        OrchestrationManager og=new OrchestrationManager();
        try{
            JSONParser parser= new JSONParser();
            JSONObject input=(JSONObject) parser.parse(content);
            JSONObject input2=(JSONObject)input.get("manifest");
            og.addManifestToWorkf(this.name,new org.json.JSONObject(input2.toJSONString()));
        }
        catch(Exception e)
        {
            
        }
    }

    /**
     * DELETE method for resource Manifestcreate
     */
    @DELETE
    public void delete() {
    }
}
