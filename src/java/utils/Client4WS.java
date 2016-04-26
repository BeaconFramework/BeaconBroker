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


package utils;


import java.net.URI;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.ClientConfig;
//import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;
//import org.glassfish.jersey.client.ClientConfig;
import utils.Exception.*;

/**
 *
 * @author Giuseppe Tricomi
 */
public class Client4WS {

    private String uri;

    public Client4WS(String uri) {
        this.uri = uri;
    }

    private static URI getBaseURI(String targetURI) {
        return UriBuilder.fromUri(targetURI).build();
    }

    public Response make_request(
            String wsPath,
            String username,
            String password,
            String tenant,
            String function,
            String body
    ) throws Exception {

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target;
        //System.out.println(getBaseURI("http://10.9.0.10:6121/fednet/tenant"));
        target = client.target(getBaseURI(wsPath));
     //  target.register(new HttpBasicAuthFilter(username, password));
        //System.out.println(target.getUri());
        Response plainAnswer = null;
        switch (function) {
            case "get": {
                plainAnswer = target.request().accept(MediaType.APPLICATION_JSON).get();
                break;
            }
            case "put": {
                plainAnswer = target.request().accept(MediaType.APPLICATION_JSON).put(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
                break;
            }
            case "post": {
                plainAnswer = target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
                break;
            }
            case "delete": {
                plainAnswer = target.request().accept(MediaType.APPLICATION_JSON).delete();
                break;
            }
            default:
                throw new Exception("operazione non trovata");
        }

   //     System.out.println(plainAnswer.getStatus() + "\n" + plainAnswer.getStringHeaders().getFirst("Server") + "\n" + plainAnswer.getStringHeaders().getFirst("Content-Length") + "\n" + plainAnswer.getStringHeaders().getFirst("Date") + "\n" + plainAnswer.getStringHeaders().getFirst("Content-Type") + "\n" + plainAnswer.readEntity(String.class));
        return plainAnswer;
    }

    /**
     * This function make request to uri/path WebServices and returns the response when
     * the answer is positive, throws a kind of WSException in other case.
     * @param path
     * @return
     * @throws WSException 
     */
/*    public String contactGET_WS(String path)throws WSException{
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        WebTarget target= client.target(this.getBaseURI(this.uri));
        Response plainAnswer = target.path(path).request().accept(MediaType.TEXT_PLAIN).get();
        switch (plainAnswer.getStatus()) {
            //good answers
            case 200: {
                return plainAnswer.readEntity(String.class);
            }//OK
            case 202: {
                return plainAnswer.readEntity(String.class);
            }//ACCEPTED 
            case 201: {
                break;
            }//CREATED
            //To be evaluate
            case 204: {
                break;
            }//NO_CONTENT
            //bad answer
            case 400: {
                throw new WSException400();
            }//BAD_REQUEST 
            case 409: {
                throw new WSException409();
            }//CONFLICT 
            case 403: {
                throw new WSException403();
            }//FORBIDDEN 
            case 410: {
                throw new WSException410();
            }//GONE
            case 500: {
                throw new WSException500();
            }//INTERNAL_SERVER_ERROR 
            case 301: {
                throw new WSException301();
            }//MOVED_PERMANENTLY 
            case 406: {
                throw new WSException406();
            }//NOT_ACCEPTABLE
            case 404: {
                throw new WSException404();
            }//NOT_FOUND
            case 304: {
                throw new WSException304();
            }//NOT_MODIFIED 
            case 412: {
                throw new WSException412();
            }//PRECONDITION_FAILED 
            case 303: {
                throw new WSException303();
            }//SEE_OTHER
            case 503: {
                throw new WSException503();
            }//SERVICE_UNAVAILABLE
            case 307: {
                throw new WSException307();
            }//TEMPORARY_REDIRECT 
            case 401: {
                throw new WSException401();
            }//UNAUTHORIZED 
            case 415: {
                throw new WSException415();
            }//UNSUPPORTED_MEDIA_TYPE 
        }
        return "ERROR!";
    }
    
    private static URI getBaseURI(String targetURI) {
        return UriBuilder.fromUri(targetURI).build();
  }
   */ 
}
