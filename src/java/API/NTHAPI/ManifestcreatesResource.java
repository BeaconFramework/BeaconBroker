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
import OSFFMIDM.SimpleIDM;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * REST Web Service
 *
 * @author giusimone
 */
@Path("/fednet/northBr/manifest")
public class ManifestcreatesResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ManifestcreatesResource
     */
    public ManifestcreatesResource() {
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
    @POST
    @Path("/aaaa")
    @Produces("application/json")
    public String testPost( String msg) {
        JSONObject reply=new JSONObject();
        try {
            JSONParser parser=new JSONParser();
            JSONObject in=(JSONObject) parser.parse(msg);
            //TODO return proper representation object
            
            reply.put("location", in.get("ciao"));
            reply.put("Available4Tenant",true); // or false
            
            reply.put("returncode", 0);     //or reply.put("returncode", 1);
            reply.put("errormesg", "None"); //or reply.put("errormesg", "Mesg");
        } catch (ParseException ex) {
            LOGGER.error(ex);
        }
         return reply.toJSONString();

    }
}
