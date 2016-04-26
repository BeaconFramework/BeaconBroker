/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        Response r=this.createPostrequest("http://"+faURL+"/net-fa/tenants",jo,auth);
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
    
    
}
