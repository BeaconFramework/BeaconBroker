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
public class FA_ScriptInvoke extends FA_REST_Client{
    
    public FA_ScriptInvoke(String endpoint,String tenantName,String userName,String password){
        super(endpoint, tenantName,userName,password);
    }
    
    /**
     * This function is used to create Tenant element inside FA. 
     * @return 
     * @author gtricomi
     */
    public boolean FAScript(String faURL)throws WSException{
        boolean result= true;
        HttpBasicAuthFilter auth=new HttpBasicAuthFilter(this.getUserName(), this.getPassword());
        Response r=this.createInsertingrequest("http://"+faURL+"/sharingTables",new JSONObject(),auth,"post");
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
