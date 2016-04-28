/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package API.SOUTHBR;

import static API.SOUTHBR.FA_REST_Client.LOGGER;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Exception.WSException;

/**
 *
 * @author Giuseppe Tricomi
 */
public class FA_client4Network extends FA_REST_Client{
    private String siteName="",faIP="",faPort="";
    
    public FA_client4Network(String endpoint,String tenantName,String userName,String password){
        super(endpoint, tenantName,userName,password);
    }
    
    /**
     * Method invoked to create or update table on FA.
     * @param TenantId
     * @param faURL
     * @param body, This JSONObject contains a JSONArray that is composed by JSONObject made by 4 elements: name,tenant_id,fa_url,site_proxy. Last element is a JSONArray composed by ip and port of FA_datapath.
     * @return
     * @throws WSException
     * @author gtricomi
     */
    public Response createSiteTable(String TenantId, String faURL,String body,int version)throws WSException{
        JSONObject jo=null;
        Response r=null;
        HttpBasicAuthFilter auth=new HttpBasicAuthFilter(this.getUserName(), this.getPassword());
        try {
            
            jo=new JSONObject();
            jo.append("version", version);
            jo.append("table", body.substring(1, (body.length()-1)));
        } catch (JSONException ex) {
            LOGGER.error("It's impossible parse body received in JSONObject! error occurred in createSiteTable operation.\n"+ex.getMessage());
        }
        
        if(jo!=null)
        {
            r=this.createInsertingrequest("http://"+faURL+"/net-fa/tenants/"+TenantId+"/networks_table",jo,auth,"put");
            try{
                this.checkResponse(r);//as answer we expect a status code 200
            }
            catch(WSException wse){
                LOGGER.error("Exception occurred in createTenantFA method, the web service has answer with bad status!\n"+wse.getMessage());
                throw wse;
            }
            return r;
        }
        return r;
    }
}
