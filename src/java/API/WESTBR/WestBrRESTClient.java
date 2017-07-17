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

package API.WESTBR;

//<editor-fold defaultstate="collapsed" desc="Import Section">
import API.EASTAPI.Clients.*;
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
public class WestBrRESTClient {

    

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
    
    public WestBrRESTClient(String userName,String password) {
        this.password=password;
        this.userName=userName;
    }
    

    
   public Response makeSimpleRequest(String bbEla,String body,String type){
    HttpBasicAuthFilter auth=new HttpBasicAuthFilter(this.userName,this.password);
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        WebTarget target;
        client.register(auth);
        target = client.target(getBaseURI(bbEla));
        
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
