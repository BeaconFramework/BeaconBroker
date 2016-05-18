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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import utils.Exception.WSException;
import utils.Exception.WSException303;
import utils.Exception.WSException500;

/**
 * USED TO MANAGE INTERACTION BETWEEN DASHBOARD AND FEDSDN.
 * @author Giuseppe Tricomi
 */
public class Fednet extends EastBrRESTClient{
    
    JSONObject body;
    static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Fednet.class);
    public Fednet(String userName, String password) {
        super(userName, password);
    }
    /**
     * 
     * @param baseFEDSDNURL
     * @return
     * @throws WSException 
     */
    public Response getAllNet(String baseFEDSDNURL)throws WSException {
        body=new JSONObject();
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet", "", "get");
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
     * @param baseFEDSDNURL
     * @param fedtobemod_id, id of network inspected
     * @return
     * @throws WSException 
     */
    public Response getNetinfo(String baseFEDSDNURL,long fedtobemod_id)throws WSException {
        body=new JSONObject();
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/"+fedtobemod_id, "", "get");
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
     * This function create fednet instance on FEDSDN
     * @param name
     * @param linkType
     * @param type
     * @param baseFEDSDNURL
     * @return
     * @throws WSException 
     */
    public Response createFednet(String name,String linkType,String type,String baseFEDSDNURL) throws WSException {
        body=new JSONObject();
        try {
            
            body.put("name", name);
            body.put("type", type);
            body.put("linkType", linkType);
            System.out.println(body.toString());
        } catch (JSONException ex) {
            LOGGER.error(ex.getMessage());
        }
        
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet", this.constructBody(name, linkType, type), "post");
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
     * This function update fednet instance on FEDSDN
     * @param fedtobemod_id
     * @param name
     * @param linkType
     * @param type
     * @param baseFEDSDNURL
     * @return
     * @throws WSException 
     */
    public Response updateFednet(long fedtobemod_id,String name,String linkType,String type,String baseFEDSDNURL) throws WSException {
        
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/"+fedtobemod_id, this.constructBody(name, linkType, type), "put");
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
     * @param baseFEDSDNURL
     * @param fedtobemod_id, id of network inspected
     * @return
     * @throws WSException 
     */
    public Response delNetwork(String baseFEDSDNURL,long fedtobemod_id)throws WSException {
        body=new JSONObject();
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/"+fedtobemod_id, "", "delete");
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
     * This function update fednet instance on FEDSDN
     * @param fedtobemod
     * @param name
     * @param linkType
     * @param type
     * @param baseFEDSDNURL
     * @return 
     * @throws WSException/WSException303
     */
    public Response updateFednet(String fedtobemod,String name,String linkType,String type,String baseFEDSDNURL) throws WSException {
        
        long id;
        try {
            id = this.searchfedID(fedtobemod, baseFEDSDNURL);
        } catch (Exception ex) {
            throw new WSException303("SEE_OTHER! The action can't be completed");
        }
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/"+id, this.constructBody(name, linkType, type), "put");
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
     * @param baseFEDSDNURL
     * @param fedtobemod_id, id of network inspected
     * @return
     * @throws WSException/WSException303
     */
    public Response getNetinfo(String baseFEDSDNURL,String fedtobemod)throws WSException {
        body=new JSONObject();
        body.
        long id;
        try {
            id = this.searchfedID(fedtobemod, baseFEDSDNURL);
            if(id<0)
                throw new WSException303("SEE_OTHER! The action can't be completed");
        } catch (Exception ex) {
            throw new WSException303("SEE_OTHER! The action can't be completed\n"+ex.getMessage());
        }
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/"+id, "", "get");
        try{
                this.checkResponse(r);//as answer we expect a status code 200
            }
            catch(WSException wse){
                LOGGER.error("Exception occurred in createTenantFA method, the web service has answer with bad status!\n"+wse.getMessage());
                throw wse;
            }
        return r;
    }
    
    
    private long searchfedID(String name,String baseFEDSDNURL )throws Exception{
        long id=-1;
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet",null , "get");
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
                    id=(long)t.get("id");
                    
                }
            }
        }
        else if (obj instanceof org.json.simple.JSONObject) {
            if (((String) ((org.json.simple.JSONObject) obj).get("name")).equals(name)) {
               id= (long)((org.json.simple.JSONObject) obj).get("id");
            }
        }
        else
            throw new Exception("Information retrieved as answer from FEDSDN is not standard. Execution stopped!\n Id not Found!\n"+obj.toString());
        return id;

    }
    
    private String constructBody(String name,String linkType,String type){
        return "{\"name\" : \""+name+"\", \"type\" : \""+type+"\", \"linktype\" : \""+linkType+"\"}";
    }
}
