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
import org.json.simple.*;

/**
 * REST Web Service
 *
 * @author Giuseppe Tricomi
 */
public class SiteResource {

    /**
     * Creates a new instance of SiteResource
     */
    private SiteResource() {
        
    }

    /**
     * Get instance of the SiteResource
     */
    public static SiteResource getInstance() {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of SiteResource class.
        return new SiteResource();
    }

    /**
     * Retrieves representation of an instance of api.site.SiteResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public JSONObject getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of SiteResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }

    /**
     * DELETE method for resource SiteResource
     */
    @DELETE
    public void delete() {
    }
}
