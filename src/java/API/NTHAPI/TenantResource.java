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

//import MDBInt.*;
//import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;
/**
 * REST Web Service
 *
 * @author gtricomi
 */
@Path("/fednet/northBr/tenant")
public class TenantResource {

    @Context
    private UriInfo context;
    static final Logger LOGGER = Logger.getLogger(TenantResource.class);
    //private DBMongo db_co;
    /**
     * Creates a new instance of TenantResource
     */
    public TenantResource() {
       // db_co=new DBMongo();
        //db_co.init("cfg/configuration_bigDataPlugin.xml");
    }

    /**
     * Retrieves representation of an instance of JClouds_Adapter.TenantResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }
    
    /**
     * Retrieves list of all sites involved with the Tenant 
     * @return a String rappresentation of the List
     */
    @GET
    @Path("/{tenant_name}/sites_tenants")
    @Produces("application/json")
    public String getSites_tenant(@PathParam("tenant_name") String tenant_name) {
        //TODO return proper representation object
        
        //ArrayList<String> value=db_co.listFederatedUser("beacon", tenant_name);
        return "arrived";//value.get(0);
    }
    
    /**
     * Sub-resource locator method for /tenant
     */
    @Path("/tenant")
    public Tenant getTenant() {
        return Tenant.getInstance();
    }
}
