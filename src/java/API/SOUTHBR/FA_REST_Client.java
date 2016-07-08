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
//<editor-fold defaultstate="collapsed" desc="Import Section">
import API.SOUTHBR.Exception.FA_Exception;
import JClouds_Adapter.KeystoneTest;
import JClouds_Adapter.NeutronTest;
import java.net.URI;
import java.util.Iterator;
import java.util.logging.Level;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Exception.*;
//</editor-fold>
/**
 *
 * @author Giuseppe Tricomi
 */
public class FA_REST_Client {

    

    private String idsEndpoint="",tenantName="",userName="",password="",region="RegionOne";
    private KeystoneTest key;
    static final Logger LOGGER = Logger.getLogger(FA_REST_Client.class);
//<editor-fold defaultstate="collapsed" desc="Getter&Setter">
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
    
    protected String getIdsEndpoint() {
        return idsEndpoint;
    }

    protected void setIdsEndpoint(String idsEndpoint) {
        this.idsEndpoint = idsEndpoint;
    }

    protected String getTenantName() {
        return tenantName;
    }

    protected void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    protected String getUserName() {
        return userName;
    }

    protected void setUserName(String userName) {
        this.userName = userName;
    }

    protected String getPassword() {
        return password;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    public KeystoneTest getKey() {
        return key;
    }

    protected void setKey(KeystoneTest key) {
        this.key = key;
    }
    //</editor-fold>
    
    public FA_REST_Client(String endpoint,String tenantName,String userName,String password) {
        this.idsEndpoint=endpoint;
        this.password=password;
        this.tenantName=tenantName;
        this.userName=userName;
        key=new KeystoneTest(this.tenantName,this.userName,this.password,this.idsEndpoint);
    }
    

    /**
     * This function returns FA URL.
     * @param TenantId
     * @return 
     */
    public String getFA_Url(String TenantId) throws FA_Exception{
        String result= key.servicetGetEndpoint("Federation_Agent");
        if(result!=null)
            return result;
        else
            throw new FA_Exception("Exception is occurred because the service named Federation Agent is not present.\n"
                    + "It is impossible retrieve Federation Agent Endpoint");
    }
    
    /**
     * This function returns Network Controller.
     * @param TenantId
     * @return
     * @throws Exception
     * @author gtricomi
     */
    public String getNetController(String TenantId) throws Exception{
        String result= key.servicetGetEndpoint("Network_controller");//>>>BEACON verify this field and modify with correct field
        if(result!=null)
            return result;
        else
            throw new Exception("Exception is occurred because the service named ....is not present.\n"
            + "It is impossible retrieve Network Controller Endpoint");
    }
    
    /**
     * Function used to obtain from Jclouds adapter the collection iterator representing 
     * all networks available for the tenant.
     * @return Iterator<Network>
     * @author gtricomi
     */
    public Iterator getNetworkList(){
        NeutronTest nt=new NeutronTest(this.idsEndpoint,this.tenantName,this.userName,this.password,this.region);
        Iterator i=nt.listNetworks();
        return i;
    }
    
    /**
     * This function is used to create a post request for web service pointed by urlFA.
     * @param urlFA
     * @param jsonObject
     * @param auth
     * @return
     * @throws JSONException 
     * @author gtricomi
     */
    protected Response createInsertingrequest(String urlFA,JSONObject jsonObject,HttpBasicAuthFilter auth,String type){
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        WebTarget target;
        target = client.target(getBaseURI(urlFA));
        //Response plainAnswer =null; 
        target.register(auth);
        
        Invocation.Builder invocationBuilder =target.request(MediaType.APPLICATION_JSON);
        MultivaluedHashMap<String,Object> mm=new MultivaluedHashMap<String,Object>();
        mm.add("content-type", MediaType.APPLICATION_JSON);
        mm.add("Accept", "application/json");
        mm.add("charsets", "utf-8");
        invocationBuilder.headers(mm);
        //preliminary operation of request creation ended
        Response plainAnswer=null;
        switch(type){
            case "post":
            {
                plainAnswer=invocationBuilder
                    .post(Entity.entity(jsonObject.toString(), MediaType.APPLICATION_JSON_TYPE));
                break;
            }
            case "put":
            {
                plainAnswer =invocationBuilder
                    .put(Entity.entity(jsonObject.toString(), MediaType.APPLICATION_JSON));
                break;
            }
        }
        return plainAnswer;
    }
    
    protected Response createInsertingrequest(String urlFA,String stringjsonObject,HttpBasicAuthFilter auth,String type,String mt){
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        WebTarget target;
        target = client.target(getBaseURI(urlFA));
        //Response plainAnswer =null; 
        target.register(auth);
        
        Invocation.Builder invocationBuilder =target.request();
        MultivaluedHashMap<String,Object> mm=new MultivaluedHashMap<String,Object>();
        mm.add("content-type", "application/json");
        mm.add("Accept", "application/json");
        mm.add("charsets", "utf-8");
        invocationBuilder.headers(mm);
        //preliminary operation of request creation ended
        Response plainAnswer=null;
        switch(type){
            case "post":
            {
                plainAnswer=invocationBuilder
                    .post(Entity.entity(stringjsonObject, mt));
                break;
            }
            case "put":
            {
                plainAnswer =invocationBuilder
                    .put(Entity.entity(stringjsonObject, mt));
                break;
            }
        }
        return plainAnswer;
    }
    
    protected Response getElement(String url,HttpBasicAuthFilter auth){
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        WebTarget target;
        target = client.target(getBaseURI(url));
        target.register(auth);
        Invocation.Builder invocationBuilder =target.request(MediaType.APPLICATION_JSON);
        Response plainAnswer=null;
        plainAnswer=invocationBuilder.get();
        return plainAnswer;
    }
    
    protected Response deleteElement(String url,HttpBasicAuthFilter auth){
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        WebTarget target;
        target = client.target(getBaseURI(url));
        target.register(auth);
        Invocation.Builder invocationBuilder =target.request();
        Response plainAnswer=null;
        plainAnswer=invocationBuilder.delete();
        return plainAnswer;
    }
    
    /**
     * Uri contructor.
     * @param targetURI
     * @return 
     * @author gtricomi
     */
    protected static URI getBaseURI(String targetURI) {
        return UriBuilder.fromUri(targetURI).build();
    }
    /**
     * Internal function used to generate correct Exception related to answer status code.
     * @param plainAnswer
     * @return
     * @throws WSException
     * @author gtricomi
     */  
    protected Response checkResponse(Response plainAnswer) throws WSException{
        if(plainAnswer!=null)
            switch(plainAnswer.getStatus()){
                //good answers
                case 200: {
                    return plainAnswer;
                }//OK
                case 202: {
                    return plainAnswer;
                }//ACCEPTED 
                case 201: {
                    return plainAnswer;
                }//CREATED
                //To be evaluate
                case 204: {
                    return plainAnswer;
                }//NO_CONTENT
                //bad answers
                case 400: {
                    throw new WSException400("BAD REQUEST! The action can't be completed");
                }//BAD_REQUEST 
                case 409: {
                    throw new WSException409("CONFLICT! The action can't be completed");
                }//CONFLICT 
                case 403: {
                    throw new WSException403("FORBIDDEN!The action can't be completed");
                }//FORBIDDEN 
                case 410: {
                    throw new WSException410("GONE! The action can't be completed");
                }//GONE
                case 500: {
                    throw new WSException500("INTERNAL_SERVER_ERROR! The action can't be completed");
                }//INTERNAL_SERVER_ERROR 
                case 301: {
                    throw new WSException301("MOVED_PERMANENTLY! The action can't be completed");
                }//MOVED_PERMANENTLY 
                case 406: {
                    throw new WSException406("NOT_ACCEPTABLE! The action can't be completed");
                }//NOT_ACCEPTABLE
                case 404: {
                    throw new WSException404("NOT_FOUND! The action can't be completed");
                }//NOT_FOUND
                case 304: {
                    throw new WSException304("NOT_MODIFIED! The action can't be completed");
                }//NOT_MODIFIED 
                case 412: {
                    throw new WSException412("PRECONDITION_FAILED! The action can't be completed");
                }//PRECONDITION_FAILED 
                case 303: {
                    throw new WSException303("SEE_OTHER! The action can't be completed");
                }//SEE_OTHER
                case 503: {
                    throw new WSException503("SERVICE_UNAVAILABLE! The action can't be completed");
                }//SERVICE_UNAVAILABLE
                case 307: {
                    throw new WSException307("TEMPORARY_REDIRECT! The action can't be completed");
                }//TEMPORARY_REDIRECT 
                case 401: {
                    throw new WSException401("UNAUTHORIZED! The action can't be completed");
                }//UNAUTHORIZED 
                case 415: {
                    throw new WSException415("UNSUPPORTED_MEDIA_TYPE! The action can't be completed");
                }//UNSUPPORTED_MEDIA_TYPE 
            }
        return plainAnswer;
    }
}
