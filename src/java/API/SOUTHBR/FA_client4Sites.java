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

/**
 *
 * @author Giuseppe Tricomi
 */
public class FA_client4Sites extends FA_REST_Client{
    private String siteName="",faIP="",faPort="";
    public FA_client4Sites(String endpoint,String tenantName,String userName,String password){
        super(endpoint, tenantName,userName,password);
    }
    
    /**
     * 
     * @param TenantId
     * @param faURL
     * @param body, This JSONObject contains a JSONArray that is composed by JSONObject made by 4 elements: name,tenant_id,fa_url,site_proxy. Last element is a JSONArray composed by ip and port of FA_datapath.
     * @return
     * @throws WSException
     * @author gtricomi
     */
    public Response createSiteTable(String TenantId, String faURL,String body)throws WSException{
        boolean result= true;
        HttpBasicAuthFilter auth=new HttpBasicAuthFilter(this.getUserName(), this.getPassword());
        Response r=this.createInsertingrequest("http://"+faURL+"/net-fa/tenants/"+TenantId+"/sites",body,auth,"put",MediaType.APPLICATION_JSON);
        
        
        try{
            this.checkResponse(r);//as answer we expect a status code 200
        }
        catch(WSException wse){
            LOGGER.error("Exception occurred in createSiteTable method, the web service has answer with bad status!\n"+wse.getMessage());
            result=false;
            throw wse;
        }
        return r;
    }
    
    /**
     * If we have a JSONArray this function is a wrapper to retrieve JSONArray.tostring(). 
     * @param ja
     * @param version
     * @return
     * @author gtricomi
     */
    public String constructSiteTableJSON(JSONArray ja){
        String tmp=ja.toString();
        return tmp;
    }
    
    
    /**
     * This function prepare the object for FA Create Site Table function.
     * @param sites
     * @return 
     * @author gtricomi
     */
    public String constructSiteTableJSON(ArrayList<HashMap<String,Object>> sites){
        //>>>BEACON this String need to be reviewed
        String result="";
        String tmp="[";
        boolean first=true;
        //JSONArray ja=new JSONArray();
        try{
        for(HashMap elem:sites){
            /*
            [
            {"tenant_id": "ab6a28b9f3624f4fa46e78247848544e",
            "name": "site1",
            "site_proxy": [{"ip": "10.0.0.33", "port": 4789}],
            "fa_url": "10.0.0.33:4567"},
            {"tenant_id": "0ce39f6ae8044445b31d5b7f9b34062b",
            "name": "site2",
            "site_proxy": [{"ip": "10.0.0.38", "port": 4789}],
            "fa_url": "10.0.0.38:4567"}
            ]
            */
            if(!first)
                tmp=tmp+", ";
            tmp=tmp+"{";
            tmp=tmp+("\"tenant_id\": \""+elem.get("tenant_id")+"\", ");
            tmp=tmp+("\"name\": \""+elem.get("name")+"\", ");
            tmp=tmp+("\"site_proxy\": [{\"ip\": \""+elem.get("site_proxyip")+"\", \"port\": "+elem.get("site_proxyport")+"}], ");
            tmp=tmp+("\"fa_url\": \""+elem.get("fa_url")+"\"");
            tmp=tmp+"}";
            first=false;
        }
        }
        catch(Exception e){
            
        }
        tmp=tmp+"]";
        return tmp;    
    }
    
    
    public boolean deleteSiteFA(String tenantID, String faURL)throws WSException{
        boolean result= true;
        HttpBasicAuthFilter auth=new HttpBasicAuthFilter(this.getUserName(), this.getPassword());
        Response r=this.deleteElement("http://"+faURL+"/net-fa/tenants/"+tenantID+"/sites",auth);
        try{
           this.checkResponse(r);
        }
        catch(WSException wse){
            LOGGER.error("Exception occurred in deleteSiteFA method, the web service has answer with bad status!\n"+wse.getMessage());
            result=false;
            throw wse;
        }
        return result;
    }
    
    public JSONObject getSiteList(String faURL,String tenantId)throws WSException{
        HttpBasicAuthFilter auth=new HttpBasicAuthFilter(this.getUserName(), this.getPassword());
        Response r=this.getElement("http://"+faURL+"/net-fa/tenants/"+tenantId+"/sites", auth);
        try{
           this.checkResponse(r);
        }
        catch(WSException wse){
            LOGGER.error("Exception occurred in getSiteList method, the web service has answer with bad status!\n"+wse.getMessage());
            throw wse;
        }
        try{
            return new JSONObject(r.readEntity(String.class));
        }
        catch(Exception e){
            LOGGER.error("Exception occurred in getSiteList method, cannot obtain JSONObject from Response!\n"+e.getMessage());
            return null;
        }
    }
    
}
