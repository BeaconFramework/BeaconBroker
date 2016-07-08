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


package API.SOUTHBR;

import static API.SOUTHBR.FA_REST_Client.LOGGER;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Exception.WSException;

/**
 *
 * @author Giuseppe Tricomi
 */
public class FA_client4Tenant extends FA_REST_Client{
    
    public FA_client4Tenant(String endpoint,String tenantName,String userName,String password){
        super(endpoint, tenantName,userName,password);
    }
    
    /**
     * This function returns the tenant ID linked to tenantName for pointed cloud.
     * @return String, Tenant UUID.
     * @author gtricomi
     */
    public String getID(){
        return this.getKey().getTenantId(this.getTenantName());
    }
    
     
    /**
     * This function is used to create Tenant element inside FA. 
     * @return 
     * @author gtricomi
     */
    public boolean createTenantFA(String TenantId, String faURL)throws WSException{
        boolean result= true;
        JSONObject jo=new JSONObject();
        try {
            jo.put("name",this.getTenantName());
            jo.put("id",TenantId );
        } catch (JSONException ex) {
            LOGGER.error(ex.getMessage());
            result= false;
        }
        HttpBasicAuthFilter auth=new HttpBasicAuthFilter(this.getUserName(), this.getPassword());
        Response r=this.createInsertingrequest("http://"+faURL+"/net-fa/tenants",jo,auth,"post");
        try{
           this.checkResponse(r);
        }
        catch(WSException wse){
            LOGGER.error("Exception occurred in createTenantFA method, the web service has answer with bad status!\n"+wse.getMessage());
            result=false;
            throw wse;
        }
        return result;
    }
   
    public boolean deleteTenantFA(String tenantID, String faURL)throws WSException{
        boolean result= true;
        HttpBasicAuthFilter auth=new HttpBasicAuthFilter(this.getUserName(), this.getPassword());
        Response r=this.deleteElement("http://"+faURL+"/net-fa/tenants/"+tenantID,auth);
        try{
           this.checkResponse(r);
        }
        catch(WSException wse){
            LOGGER.error("Exception occurred in deleteTenantFA method, the web service has answer with bad status!\n"+wse.getMessage());
            result=false;
            throw wse;
        }
        return result;
    }
    
    
    public JSONObject getTenantList(String faURL)throws WSException{
        HttpBasicAuthFilter auth=new HttpBasicAuthFilter(this.getUserName(), this.getPassword());
        Response r=this.getElement("http://"+faURL+"/net-fa/tenants", auth);
        try{
           this.checkResponse(r);
        }
        catch(WSException wse){
            LOGGER.error("Exception occurred in getTenantlist method, the web service has answer with bad status!\n"+wse.getMessage());
            throw wse;
        }
        try{
            return new JSONObject(r.readEntity(String.class));
        }
        catch(Exception e){
            LOGGER.error("Exception occurred in getTenantlist method, cannot obtain JSONObject from Response!\n"+e.getMessage());
            return null;
        }
    }
    
    public JSONObject getTenant(String faURL,String tenantID)throws WSException{
        HttpBasicAuthFilter auth=new HttpBasicAuthFilter(this.getUserName(), this.getPassword());
        Response r=this.getElement("http://"+faURL+"/net-fa/tenants/"+tenantID, auth);
        try{
           this.checkResponse(r);
        }
        catch(WSException wse){
            LOGGER.error("Exception occurred in getTenantlist method, the web service has answer with bad status!\n"+wse.getMessage());
            throw wse;
        }
        try{
            return new JSONObject(r.readEntity(String.class));
        }
        catch(Exception e){
            LOGGER.error("Exception occurred in getTenantlist method, cannot obtain JSONObject from Response!\n"+e.getMessage());
            return null;
        }
    }
}
