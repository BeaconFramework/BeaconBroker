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
 * Used to contact BNM netsegments APIs.
 * @author Giuseppe Tricomi
 */
public class NetworkSegment extends EastBrRESTClient{

    JSONObject body;
    static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(NetworkSegment.class);
    
    public NetworkSegment(String userName, String password) {
        super(userName, password);
    }
    
    /**
     * Returns all Network Segment stored inside FEDSDN
     * @param baseFEDSDNURL
     * @param fedId
     * @param siteId
     * @return
     * @throws WSException 
     */
    public Response getAllNetSegm(String baseFEDSDNURL,long fedId,long siteId)throws WSException {
        body=new JSONObject();
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/"+fedId+"/"+siteId+"/netsegment", "", "get");
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
     * Returns informations related to Network Segment with "id=netsegId" stored inside FEDSDN
     * @param baseFEDSDNURL
     * @param fedId
     * @param siteId
     * @param netsegId
     * @return
     * @throws WSException 
     */
    public Response getInofesNetSegm(String baseFEDSDNURL,long fedId,long siteId,long netsegId)throws WSException {
        body=new JSONObject();
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/"+fedId+"/"+siteId+"/netsegment/"+netsegId, "", "get");
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
     * @param params, this is a JSONObject with the following mandatory elements: name,fa_endpoint, cmp_net_id, network_address, network_mask,size, vlan_id
     * @param baseFEDSDNURL
     * @param fedId
     * @param siteId
     * @return
     * @throws WSException 
     */
    public Response createNetSeg(JSONObject params,String baseFEDSDNURL,long fedId,long siteId) throws WSException, JSONException {
        body=params;
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/"+fedId+"/"+siteId+"/netsegment", this.constructBody(params), "post");
        
        try{
                this.checkResponse(r);//as answer we expect a status code 200
            }
            catch(WSException wse){
                System.out.println(r.readEntity(String.class));
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
    public Response updateNetSeg(JSONObject params,String baseFEDSDNURL,long fedId,long siteId,long netsegId) throws WSException, JSONException {
        body=params;
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/"+fedId+"/"+siteId+"/netsegment/"+netsegId, this.constructBody(params), "put");
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
     * @param fedId
     * @param siteId
     * @param netsegId
     * @return
     * @throws WSException 
     */
    public Response delNetSeg(String baseFEDSDNURL,long fedId,long siteId,long netsegId)throws WSException {
        body=new JSONObject();
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/"+fedId+"/"+siteId+"/netsegment/"+netsegId, "", "delete");
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
     * @param netseg
     * @return
     * @throws WSException 
     */
    public Response updateNetSeg(JSONObject params,String baseFEDSDNURL,long fedId,long siteId,String netseg) throws WSException, JSONException {
        body=params;
        //logic to get id
        long id;
        try {
            id = this.searchNetSegID(netseg, baseFEDSDNURL, fedId, siteId);
        } catch (Exception ex) {
            throw new WSException303("SEE_OTHER! The action can't be completed");
        }
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/"+fedId+"/"+siteId+"/netsegment/"+id, this.constructBody(params), "put");
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
     * Returns informations related to Network Segment with "name=netseg" stored inside FEDSDN
     * @param baseFEDSDNURL
     * @param fedId
     * @param siteId
     * @param netseg
     * @return
     * @throws WSException/ WSException303
     */
    public Response getInofesNetSegm(String baseFEDSDNURL,long fedId,long siteId,String netseg)throws WSException {
        body=new JSONObject();
        //logic to get id
        long id;
        try {
            id = this.searchNetSegID(netseg, baseFEDSDNURL, fedId, siteId);
        } catch (Exception ex) {
            throw new WSException303("SEE_OTHER! The action can't be completed");
        }
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/"+fedId+"/"+siteId+"/netsegment/"+id, "", "get");
        try{
                this.checkResponse(r);//as answer we expect a status code 200
            }
            catch(WSException wse){
                LOGGER.error("Exception occurred in createTenantFA method, the web service has answer with bad status!\n"+wse.getMessage());
                throw wse;
            }
        return r;
    }
    
    
    private long searchNetSegID(String name,String baseFEDSDNURL,long fednetId, long siteId )throws Exception{
        Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/"+fednetId+"/"+siteId+"/netsegment",null , "get");
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
    
    
    private String constructBody(JSONObject params) throws JSONException{
        String name=(String)params.get("name");
        String fa_endpoint=(String)params.get("fa_endpoint");
        String network_address=(String)params.get("network_address");
        String network_mask=(String)params.get("network_mask");
        String size=((Integer)params.get("size")).toString();
        String vlan_id=(String)params.get("vlan_id");
        String cmp_net_id=(String)params.get("cmp_net_id");
        return "{\"name\" : \""+name+"\", \"fa_endpoint\" : \""+fa_endpoint+"\", \"network_address\" : \""+network_address+"\", \"network_mask\" : \""+
                network_mask+"\", \"size\" : \""+size+"\", \"vlan_id\" : \""+vlan_id+"\", \"cmp_net_id\" : \""+cmp_net_id+"\"}";
    }
}
