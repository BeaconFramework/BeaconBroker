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
package API.EASTAPI.Clients;


import java.util.Iterator;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import utils.Exception.WSException;
import utils.Exception.WSException303;

/**
 *
 * @author Giuseppe Tricomi
 */
public class Site extends EastBrRESTClient{
    JSONObject body;
    static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Fednet.class);
    public Site(String userName, String password) {
        super(userName, password);
    }
    /**
     * Returns all site stored inside FEDSDN
     * @param baseFEDSDNURL
     * @return
     * @throws WSException 
     */
    public Response getAllSite(String baseFEDSDNURL)throws WSException {
        body=new JSONObject();
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/site", "", "get");
        try{
                this.checkResponse(r);//as answer we expect a status code 200
            }
            catch(WSException wse){
                LOGGER.error("Exception occurred in createTenantFA method, the web service has answer with bad status!\n"+wse.getMessage());
                throw wse;
            }
        return r;
    }
    
    /**
     * Returns all informations releted to selected site stored inside FEDSDN.
     * @param baseFEDSDNURL
     * @param siteId
     * @return
     * @throws WSException 
     */
    public Response getSiteInfoes(String baseFEDSDNURL,long siteId)throws WSException {
        body=new JSONObject();
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/site/"+siteId, "", "get");
        try{
                this.checkResponse(r);//as answer we expect a status code 200
            }
            catch(WSException wse){
                LOGGER.error("Exception occurred in createTenantFA method, the web service has answer with bad status!\n"+wse.getMessage());
                throw wse;
            }
        return r;
    }
    
    /**
     * This function create Site instance on FEDSDN
     * @param name
     * @param cmp_endpoint
     * @param type
     * @param baseFEDSDNURL
     * @return
     * @throws WSException 
     */
    public Response createSite(String name,String cmp_endpoint,String type,String baseFEDSDNURL) throws WSException {
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/site", this.constructBody(name, type, cmp_endpoint), "post");
        try{
                this.checkResponse(r);//as answer we expect a status code 200
            }
            catch(WSException wse){
                LOGGER.error("Exception occurred in createTenantFA method, the web service has answer with bad status!\n"+wse.getMessage());
                throw wse;
            }
        return r;
    }
    
    /**
     * This function update site instance on FEDSDN
     * @param siteId
     * @param name
     * @param cmp_endpoint
     * @param type
     * @param baseFEDSDNURL
     * @return
     * @throws WSException 
     */
    public Response updateSite(long siteId,String name,String cmp_endpoint,String type,String baseFEDSDNURL) throws WSException {
        
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/site/"+siteId, this.constructBody(name, type, cmp_endpoint), "put");
        try{
                this.checkResponse(r);//as answer we expect a status code 200
            }
            catch(WSException wse){
                LOGGER.error("Exception occurred in createTenantFA method, the web service has answer with bad status!\n"+wse.getMessage());
                throw wse;
            }
        return r;
    }
    
    /**
     * Delete the site with id siteId.
     * @param baseFEDSDNURL
     * @param siteId
     * @return
     * @throws WSException 
     */
    public Response delSite(String baseFEDSDNURL,long siteId)throws WSException {
        body=new JSONObject();
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/site/"+siteId, "", "delete");
        try{
                this.checkResponse(r);//as answer we expect a status code 200
            }
            catch(WSException wse){
                LOGGER.error("Exception occurred in createTenantFA method, the web service has answer with bad status!\n"+wse.getMessage());
                throw wse;
            }
        return r;
    }
    
    /**
     * Returns all informations releted to selected site stored inside FEDSDN.
     * @param baseFEDSDNURL
     * @param site
     * @return
     * @throws WSException/WSException303 
     */
    public Response getSiteInfoes(String baseFEDSDNURL,String site)throws WSException {
        body = new JSONObject();
        //logic to retrieve siteid
        long id;
        try {
            id = this.searchsiteID(site, baseFEDSDNURL);
        } catch (Exception ex) {
            throw new WSException303("SEE_OTHER! The action can't be completed");
        }
        Response r = this.makeSimpleRequest(baseFEDSDNURL + "/fednet/site/" + id, "", "get");
        try {
            this.checkResponse(r);//as answer we expect a status code 200
        } catch (WSException wse) {
            LOGGER.error("Exception occurred in createTenantFA method, the web service has answer with bad status!\n" + wse.getMessage());
            throw wse;
        }
        return r;
    }
    
    
    private long searchsiteID(String name,String baseFEDSDNURL )throws Exception{
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/site",null , "get");
        org.json.simple.parser.JSONParser p=new org.json.simple.parser.JSONParser();
        Object obj=null;  
        try {
            obj = p.parse(r.readEntity(String.class));
        } catch (ParseException ex) {
            LOGGER.error("Exception occurred in Parsing JSON returned from FEDSDN \n"+ ex.getMessage());
        }
        org.json.simple.JSONArray j;
        if (obj instanceof org.json.simple.JSONArray) {
            j = (org.json.simple.JSONArray) obj;
            Iterator i = j.iterator();
            while (i.hasNext()) {
                org.json.simple.JSONObject t = (org.json.simple.JSONObject) i.next();
                if (((String) t.get("name")).equals(name)) {
                    return (long)t.get("id");
                }
            }
        }
        if (obj instanceof org.json.simple.JSONObject) {
            if (((String) ((org.json.simple.JSONObject) obj).get("name")).equals(name)) {
               return (long)((org.json.simple.JSONObject) obj).get("id");
            }
        }
        throw new Exception("Information retrieved as answer from FEDSDN is not standard. Execution stopped!\n Id not Found!\n"+obj.toString());
    }
    
    private String constructBody(String name,String type,String cmp_endpoint){
        return "{\"name\" : \""+name+"\", \"cmp_endpoint\" : \""+cmp_endpoint+"\", \"type\" : \""+type+"\"}";
    }
}
