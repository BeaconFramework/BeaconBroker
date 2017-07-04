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

package OSFFM_ORC.Utils;



import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import utils.Exception.OrchestrationException;
import utils.Exception.WSException;
import utils.Exception.WSException303;
import utils.Exception.WSException500;

/**
 * USED TO MANAGE INTERACTION BETWEEN Beacon Broker Orchestrator and OpenNebula modules.
 * @author Giuseppe Tricomi
 */
public class ONEClient extends RESTClient{
    
    JSONObject body;
    static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ONEClient.class);
    
    public ONEClient(String userName, String password) {
        super(userName, password);
    }
    
    /*public Response getAllNet(String baseFEDSDNURL)throws WSException {
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
    }*/
    
   
    /**
     * This function is used to create and instantiate the template on ONEFLOW.
     * It throws WSEXception when createOneSerTemp fails, OrchestrationException when somethings goes wrong in instantiateOneSerTemp action.
     * @param OneFlowTemplate
     * @param ONEFlowURL
     * @return
     * @throws WSException
     * @throws OrchestrationException 
     */
    public Response beaconDeployOperation(String OneFlowTemplate,String ONEFlowURL)throws WSException,OrchestrationException{
        Response r=null;
        String id;
        r=this.createOneSerTemp(OneFlowTemplate, ONEFlowURL);
        JSONObject tmp=r.readEntity(JSONObject.class);
        try {
            id=(String)((JSONObject)tmp.get("DOCUMENT")).get("ID");
            r=this.instantiateOneSerTemp(ONEFlowURL, id);
            return r;
        } catch (JSONException ex) {
            LOGGER.error("Error on function beaconDeployOperation when system extract id from ONEFLOW answer: "+ex.getMessage());
            throw new OrchestrationException("Error on function beaconDeployOperation when system extract id from ONEFLOW answer: "+ex.getMessage());
        } catch (WSException wse){
            LOGGER.error("Error on function beaconDeployOperation when instantiateOneSerTemp runs: "+wse.getMessage());
            throw new OrchestrationException("Error on function beaconDeployOperation when sinstantiateOneSerTemp runs: "+wse.getMessage());
        }
        
        
    }
    
    /**
     * Function used to create service template on OneFlow.
     * @param OneFlowTemplate
     * @param ONEFlowURL
     * @return
     * @throws WSException 
     */
    public Response createOneSerTemp(String OneFlowTemplate,String ONEFlowURL) throws WSException {
        try {
            body=new JSONObject(OneFlowTemplate);
        } catch (JSONException ex) {
            LOGGER.error("Error on function createOneSerTemp"+ex.getMessage());
        }
        
            System.out.println(body.toString());
       
        
        Response r=this.makeSimpleRequest(ONEFlowURL+"/service_template", body.toString(), "post");
        try{
                this.checkResponse(r);
                
            }
            catch(WSException wse){
                LOGGER.error("Exception occurred in service_template creation method on ONEFlow [reachable at "+ONEFlowURL+", the web service has answer with bad status!\n"+wse.getMessage());
                throw wse;
            }
        return r;
    }
    
    
    /**
     * Function used to instantiate service template on OneFlow.
     * @param ONEFlowURL
     * @param id
     * @return
     * @throws WSException 
     */
    public Response instantiateOneSerTemp(String ONEFlowURL,String id) throws WSException {
        try {
            body=new JSONObject("{\"action\":{\"perform\":\"instantiate\"}}");
        } catch (JSONException ex) {
            LOGGER.error(ex.getMessage());
        }
        
            System.out.println(body.toString());
       
        
        Response r=this.makeSimpleRequest(ONEFlowURL+"/service_template/"+id+"/action", body.toString(), "post");
        try{
                this.checkResponse(r);
                
            }
            catch(WSException wse){
                LOGGER.error("Exception occurred in service_template instantiation method on ONEFlow [reachable at "+ONEFlowURL+", the web service has answer with bad status!\n"+wse.getMessage());
                throw wse;
            }
        return r;
    }
   

    
    /*public Response delNetwork(String baseFEDSDNURL,long fedtobemod_id)throws WSException {
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
    }*/
    
    
    
  
   /* public Response updateFednet(long fedtobemod,String name,String linkType,String type,String baseFEDSDNURL,String action) throws WSException {
        if(action==null){
            Long id=new Long(fedtobemod);
            Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/"+id.toString(), this.constructBody(name, linkType, type), "put");
            try{
                    this.checkResponse(r);//as answer we expect a status code 200
                }
                catch(WSException wse){
                    LOGGER.error("Exception occurred in createTenantFA method, the web service has answer with bad status!\n"+wse.getMessage());
                    throw wse;
                }
            return r;
        }
        else{
            Long id=new Long(fedtobemod);
            Response r=this.makeSimpleRequest(baseFEDSDNURL+"/fednet/"+id.toString(), this.constructBody(name, linkType, type, action), "put");
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
*/
    
    
    
   /*
    private String constructBody(String name,String linkType,String type,String action){
        return "{\"name\" : \""+name+"\", \"type\" : \""+type+"\", \"linktype\" : \""+linkType+"\",\"action\":\""+action+"\"}";
    }*/
}
