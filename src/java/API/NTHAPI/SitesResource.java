/**.Copyright 2016, University of Messina.
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

//import API.NTHAPI.SiteResource;
import OSFFMIDM.SimpleIDM;
import java.io.File;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import utils.ParserXML;
/**
 * REST Web Service
 *
 * @author Giuseppe Tricomi
 */
@Path("/fednet/northBr/site")
public class SitesResource {

    @Context
    private UriInfo context;
    
    private ParserXML parser;
    private String fedSDNTarget; //it will be used to make request to web service with Client4WS class
    static final Logger LOGGER = Logger.getLogger(SitesResource.class);
        
    /**
     * Creates a new instance of SitesResource
     */
    public SitesResource() {
        LOGGER.error("test");
        this.init("../webapps/OSFFM/WEB-INF/Configuration_NTHBR_WS.xml");
        
        
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
     * Retrieves list of site OS federated
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getFederatedSites() {
        //TODO return proper representation object
        JSONArray arr=new JSONArray();
        //while(??){
        JSONObject element=new JSONObject();
        element.put("uuid", "Value1");
        element.put("name", "Value2");
        element.put("Available4Tenant",true); // or false
        arr.add(element);
        //}
        JSONObject reply=new JSONObject();
        reply.put("response", arr);
        reply.put("returncode", 0); // or reply.put("returncode", 1);
        reply.put("errormesg", "None"); //or reply.put("errormesg", "Mesg");
        return reply.toJSONString();
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
    
    /**
     * PUT method to insert a new site on Federation data space
     * @return 
     */
    @PUT
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_JSON)
    public String putSite(String value){
        //the logic and the information have to be designed
        JSONObject j=new JSONObject();
        JSONParser jp=new JSONParser();
        try{
            j=(JSONObject)jp.parse(value);
        }catch(Exception e){}
        
        return j.toJSONString();//questo è lo scheletro và modificato
    }
    
    
    /**
     * DELETE method for resource Users
     * @param userid
     * @return 
     */
    @DELETE
    @Path("/{site_id}")
    @Produces("application/json")
    public String deleteSite(@PathParam("siteid") String siteid) {
        JSONObject reply=new JSONObject();
        //something TODO 4 delete Logic
        reply.put("returncode", 0); // or reply.put("returncode", 1);
        reply.put("errormesg", "None"); //or reply.put("errormesg", "Mesg");
        return reply.toJSONString();
    }
    
    /**
     * Sub-resource locator method for site
     */
    @Path("site")
    public SiteResource getSiteResource() {
        return SiteResource.getInstance();
    }
}
