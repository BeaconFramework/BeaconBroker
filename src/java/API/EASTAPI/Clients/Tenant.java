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
public class Tenant extends EastBrRESTClient{
//NEED TO BE COMPLETED
    JSONObject body;
    static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Tenant.class);
    
    public Tenant(String userName, String password) {
        super(userName, password);
    }
    
    /**
     * Returns all Tenants stored inside FEDSDN
     * @param baseFEDSDNURL
     * @return
     * @throws WSException 
     */
    public Response getAllTenant(String baseFEDSDNURL)throws WSException {
        body=new JSONObject();
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/tenant", body, "get");
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
     * Returns informations related to Tenant with "id=tenId" stored inside FEDSDN
     * @param baseFEDSDNURL
     * @param tenId
     * @return
     * @throws WSException 
     */
    public Response getInofesTenant(String baseFEDSDNURL,long tenId)throws WSException {
        body=new JSONObject();
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/"+tenId, body, "get");
        try{
                this.checkResponse(r);
            }
            catch(WSException wse){
                LOGGER.error("Exception occurred in createTenantFA method, the web service has answer with bad status!\n"+wse.getMessage());
                throw wse;
            }
        return r;
    }
    
    /**
     * 
     * @param params, this is a JSONObject with the following mandatory elements: name,fa_endpoint, cmp_net_id, network_address, network_mask,size, vlan_id
     * @param baseFEDSDNURL
     * @return
     * @throws WSException 
     */
    public Response createTen(JSONObject params,String baseFEDSDNURL) throws WSException {
        body=params;
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/tenant", body, "post");
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
     * 
     * @param params
     * @param baseFEDSDNURL
     * @param fedId
     * @param siteId
     * @param netsegId
     * @return
     * @throws WSException 
     */
    public Response updateNetSeg(JSONObject params,String baseFEDSDNURL,long tenId) throws WSException {
        body=params;
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/tenant/"+tenId, body, "put");
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
     * Delete tenant with selected id.
     * @param baseFEDSDNURL
     * @param tenantId
     * @return
     * @throws WSException 
     */
    public Response delSite(String baseFEDSDNURL,long tenantId)throws WSException {
        body=new JSONObject();
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/tenant/"+tenantId, body, "delete");
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
     * Returns all informations releted to selected tenant stored inside FEDSDN.
     * @param baseFEDSDNURL
     * @param tenantname
     * @return
     * @throws WSException 
     */
    public Response getTenantInfoes(String baseFEDSDNURL,String tenantname)throws WSException {
        body=new JSONObject();
        //logic to retrieve tenantid
        long id;
        try {
            id = this.searchTenantID(tenantname, baseFEDSDNURL);
        } catch (Exception ex) {
            throw new WSException303("SEE_OTHER! The action can't be completed");
        }
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/tenant/"+id, body, "get");
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
     * 
     * @param params
     * @param tenantname
     * @param baseFEDSDNURL
     * @return
     * @throws WSException/WSException303 
     */
    public Response updateFednet(JSONObject params,String tenantname,String baseFEDSDNURL) throws WSException {
        //logic to retrieve ID
        long id;
        try {
            id = this.searchTenantID(tenantname, baseFEDSDNURL);
        } catch (Exception ex) {
            throw new WSException303("SEE_OTHER! The action can't be completed");
        }
        body=params;
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/tenant/"+id, body, "put");
        try{
                this.checkResponse(r);//as answer we expect a status code 200
            }
            catch(WSException wse){
                LOGGER.error("Exception occurred in createTenantFA method, the web service has answer with bad status!\n"+wse.getMessage());
                throw wse;
            }
        return r;
    }
    
    
    private long searchTenantID(String name,String baseFEDSDNURL )throws Exception{
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/tenant",null , "get");
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
    
    
    //TO BE COMPLETED 
    private String constructBody(JSONObject params) throws JSONException{
        return "{\"name\" : \""+params.getString("name")+"\", \"fa_endpoint\" : \""+params.getString("fa_endpoint")+"\", \"network_address\" : \""+params.getString("network_address")+"\", \"network_mask\" : \""+params.getString("network_mask")+"\", \"size\" : \""+params.getString("size")+"\", \"vlan_id\" : \""+params.getString("vlan_id")+"\", \"cmp_net_id\" : \""+params.getString("cmp_net_id")+"\"}";
    }
}
