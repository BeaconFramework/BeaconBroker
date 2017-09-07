/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API.WESTBR;

import API.EASTAPI.Clients.EastBrRESTClient;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Exception.WSException;

/**
 *
 * @author gtricomi
 */
public class BB_elaClient extends EastBrRESTClient{
    
    JSONObject body;
    static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(BB_elaClient.class);
    
    public BB_elaClient(String userName, String password) {
        super(userName, password);
    }
    
    public Response activateElaManager(String BB_elaURL,JSONObject content)throws WSException, JSONException {
        
        Response r=this.makeSimpleRequest(BB_elaURL+"/elaman", content.toString(0), "post");
        try{
                this.checkResponse(r);//as answer we expect a status code 200
            }
            catch(WSException wse){
                LOGGER.error("Exception occurred in createTenantFA method, the web service has answer with bad status!\n"+wse.getMessage());
                throw wse;
            }
        return r;
    }
    
}
