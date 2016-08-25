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

/**
 * REST Web Service
 * TO BE REVIEWED
 * @author Giuseppe Tricomi
 */
public class NetworkResource {

    private String site_id, tenant_id;

    /**
     * Creates a new instance of NetworkResource
     */
    private NetworkResource(String site_id, String tenant_id) {
        this.site_id = site_id;
        this.tenant_id = tenant_id;
    }

    /**
     * Get instance of the NetworkResource
     */
    public static NetworkResource getInstance(String site_id, String tenant_id) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of NetworkResource class.
        return new NetworkResource(site_id, tenant_id);
    }

    /**
     * Retrieves representation of an instance of api.NetworkResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of NetworkResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }

    /**
     * DELETE method for resource NetworkResource
     */
    @DELETE
    public void delete() {
    }
}
