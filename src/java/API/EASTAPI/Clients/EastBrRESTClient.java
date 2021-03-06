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

//<editor-fold defaultstate="collapsed" desc="Import Section">
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Iterator;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.net.util.Base64;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;
import org.jclouds.domain.Credentials;
import org.jclouds.rest.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Exception.*;
//</editor-fold>

/**
 * This CLASS NEED TO BE MODIFIED.
 * This CLASS IS USED TO MAKE REQUESTS FORFEDSDN
 * @author Giuseppe Tricomi
 */
public class EastBrRESTClient {

    

    private String userName="",password="";//,region="RegionOne";
    static final Logger LOGGER = Logger.getLogger(EastBrRESTClient.class);
//<editor-fold defaultstate="collapsed" desc="Getter&Setter">
    /*public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
 */
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

    //</editor-fold>
    
    public EastBrRESTClient(String userName,String password) {
        this.password=password;
        this.userName=userName;
    }
    

    
   public Response makeSimpleRequest(String urlFEDSDN,String body,String type){
        HttpBasicAuthFilter auth=new HttpBasicAuthFilter(this.userName,this.password);
        //HttpAuthenticationFeature feature = HttpAuthenticationFeature.universal(this.getUserName(), this.getPassword());
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        WebTarget target;
        client.register(auth);
        target = client.target(getBaseURI(urlFEDSDN));
        
        Invocation.Builder invocationBuilder =target.request();
        MultivaluedHashMap<String,Object> mm=new MultivaluedHashMap<String,Object>();
        mm.add("content-type", "application/json");
        mm.add("Accept", "application/json");
        mm.add("charsets", "utf-8");
        String auth1 = this.getUserName() + ":" + this.getPassword();
        byte[] encodedAuth = Base64.encodeBase64(auth1.getBytes(Charset.forName("ISO-8859-1")));
        String authHeader = "Basic " + new String(encodedAuth);
        mm.add("Authorization", authHeader);
        invocationBuilder.headers(mm);
        Response plainAnswer = null;
        switch (type) {
            case "post": {
                plainAnswer = invocationBuilder
                        .post(Entity.entity(body, MediaType.APPLICATION_JSON));
                break;
            }
            case "put": {
                plainAnswer = invocationBuilder
                        .put(Entity.entity(body, MediaType.APPLICATION_JSON));
                break;
            }
            case "delete": {
                plainAnswer = invocationBuilder
                        .delete();
                break;
            }
            case "get": {
                plainAnswer = invocationBuilder
                        .get();
                break;
            }
            default :{
                //nothing to do
            }
        }
        return plainAnswer;
    }
    
    
    /**
     * Uri contructor.
     * @param targetURI
     * @return 
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
        if(plainAnswer!=null){
            String error_message = null;
            
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
                    error_message = plainAnswer.readEntity(String.class);
                    throw new WSException400("BAD REQUEST! The action can't be completed\n" +error_message);
                }//BAD_REQUEST 
                case 409: {
                    error_message = plainAnswer.readEntity(String.class);
                    throw new WSException409("CONFLICT! The action can't be completed\n" +error_message);
                }//CONFLICT 
                case 403: {
                    error_message = plainAnswer.readEntity(String.class);
                    throw new WSException403("FORBIDDEN!The action can't be completed\n" +error_message);
                }//FORBIDDEN 
                case 410: {
                    error_message = plainAnswer.readEntity(String.class);
                    throw new WSException410("GONE! The action can't be completed\n" +error_message);
                }//GONE
                case 500: {
                    error_message = plainAnswer.readEntity(String.class);
                    throw new WSException500("INTERNAL_SERVER_ERROR! The action can't be completed\n" +error_message);
                }//INTERNAL_SERVER_ERROR 
                case 301: {
                    error_message = plainAnswer.readEntity(String.class);
                    throw new WSException301("MOVED_PERMANENTLY! The action can't be completed\n" +error_message);
                }//MOVED_PERMANENTLY 
                case 406: {
                    error_message = plainAnswer.readEntity(String.class);
                    throw new WSException406("NOT_ACCEPTABLE! The action can't be completed\n" +error_message);
                }//NOT_ACCEPTABLE
                case 404: {
                    error_message = plainAnswer.readEntity(String.class);
                    throw new WSException404("NOT_FOUND! The action can't be completed\n" +error_message);
                }//NOT_FOUND
                case 304: {
                    error_message = plainAnswer.readEntity(String.class);
                    throw new WSException304("NOT_MODIFIED! The action can't be completed\n" +error_message);
                }//NOT_MODIFIED 
                case 412: {
                    error_message = plainAnswer.readEntity(String.class);
                    throw new WSException412("PRECONDITION_FAILED! The action can't be completed\n" +error_message);
                }//PRECONDITION_FAILED 
                case 303: {
                    error_message = plainAnswer.readEntity(String.class);
                    throw new WSException303("SEE_OTHER! The action can't be completed\n" +error_message);
                }//SEE_OTHER
                case 503: {
                    error_message = plainAnswer.readEntity(String.class);
                    throw new WSException503("SERVICE_UNAVAILABLE! The action can't be completed\n" +error_message);
                }//SERVICE_UNAVAILABLE
                case 307: {
                    error_message = plainAnswer.readEntity(String.class);
                    throw new WSException307("TEMPORARY_REDIRECT! The action can't be completed\n" +error_message);
                }//TEMPORARY_REDIRECT 
                case 401: {
                    error_message = plainAnswer.readEntity(String.class);
                    throw new WSException401("UNAUTHORIZED! The action can't be completed\n" +error_message);
                }//UNAUTHORIZED 
                case 415: {
                    error_message = plainAnswer.readEntity(String.class);
                    throw new WSException415("UNSUPPORTED_MEDIA_TYPE! The action can't be completed\n" +error_message);
                }//UNSUPPORTED_MEDIA_TYPE 
            }
        }
        return plainAnswer;
    }
}
