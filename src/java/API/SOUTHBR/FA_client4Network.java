/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package API.SOUTHBR;

import static API.SOUTHBR.FA_REST_Client.LOGGER;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Exception.WSException;
import utils.Exception.WSException500;

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
    public Response createNetTable(String TenantId, String faURL,String body)throws WSException{
        JSONObject jo=null;
        Response r=null;
        HttpBasicAuthFilter auth=new HttpBasicAuthFilter(this.getUserName(), this.getPassword());
        //System.out.println(jo.toString());
            r=this.createInsertingrequest("http://"+faURL+"/net-fa/tenants/"+TenantId+"/networks_table",body,auth,"put",MediaType.APPLICATION_JSON);
            try{
                this.checkResponse(r);//as answer we expect a status code 200
            }
            catch(WSException500 wse500){
                LOGGER.error("Exception occurred in createTenantFA method, the web service has answer with bad status!\n"+wse500.getMessage()+"\nAnyway the execution It will be continued.");
                return r;
            }
            catch(WSException wse){
                LOGGER.error("Exception occurred in createTenantFA method, the web service has answer with bad status!\n"+wse.getMessage());
                throw wse;
            }
            return r;
    }
    
    /*
    
            */
    
    
    /**
     * This function prepare the object for FA Create Network Table function.
     * @param sites
     * @return 
     * @author gtricomi
     */
    public String constructNetworkTableJSON(ArrayList<ArrayList<HashMap<String,Object>>> networks,int version){
        //>>>BEACON this String need to be reviewed
        String result="";
        String tmp="{\"table\": [";
        boolean infirst=true,first=true;
        JSONArray ja=new JSONArray();
        try {

            for (ArrayList<HashMap<String,Object>> superiorelem : networks) {
                if (!first) {
                        tmp = tmp + ", ";
                    }
                tmp=tmp+"[";
                for (HashMap elem : superiorelem) {
                    /*
                     {"table": 
                     [
                     [
                     {
                     "tenant_id": "ab6a28b9f3624f4fa46e78247848544e",
                     "site_name": "site1",
                     "name": "private",
                     "vnid": "c926e107-3292-48d4-a36b-f72fa81507dd"
                     },
                     {
                     "tenant_id": "0ce39f6ae8044445b31d5b7f9b34062b",
                     "site_name": "site2",
                     "name": "private",
                     "vnid": "d2c11d66-fb61-4438-819c-c562e108dbb5"
                     }
                     ]
                     ],
                     "version": 111}
                     */
                    if (!infirst) {
                        tmp = tmp + ", ";
                    }
                    tmp = tmp + "{";
                    tmp = tmp + ("\"tenant_id\": \"" + elem.get("tenant_id") + "\", ");
                    tmp = tmp + ("\"site_name\": \"" + elem.get("site_name") + "\", ");
                    tmp = tmp + ("\"name\": \"" + elem.get("name") + "\", ");
                    tmp = tmp + ("\"vnid\": \"" + elem.get("vnid") + "\"");
                    tmp = tmp + "}";
                    infirst = false;
                }
                tmp=tmp+"]";
                first = false;
            }
            tmp=tmp+"], \"version\": "+version+"}";
        } catch(Exception e){
            
        }
        
        return tmp;    
    }
}
