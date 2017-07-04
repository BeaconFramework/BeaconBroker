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



import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;


/**
 *
 * @author giuseppe
 */
public class TackerClient  extends RESTClient{
    
    static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(TackerClient.class);
    private String tenant="", url="";
    private final String tokenPath="/tokens";
    
    public TackerClient(String userName, String password) {
        super(userName, password);
    }
    
    public TackerClient(String userName, String password,String tenant,String url){
        super(userName, password);
        this.tenant=tenant;
        this.url=url;
    }
    //String body="{\"auth\": {\"tenantName\": \"admin\", \"passwordCredentials\": {\"username\": \"admin\", \"password\": \"0penstack!\"}}}";
    private String getToken(JSONObject body)throws JSONException{
        Response r;
        r=this.request4Keystone(this.url+this.tokenPath, body.toString(), "post");
        
        
        String j=(String)r.readEntity(String.class);
        JSONObject p=new JSONObject(j);
        String token= (String)((JSONObject)((JSONObject)p.get("access")).get("token")).get("id");
        return token;
    }
    
    
    public static void main(String[] args) throws JSONException {
        TackerClient t=new TackerClient("admin","0penstack!","admin","http://10.9.1.211:5000/v2.0");
        String body="{\"auth\": {\"tenantName\": \"admin\", \"passwordCredentials\": {\"username\": \"admin\", \"password\": \"0penstack!\"}}}";
        Response r=t.request4Keystone("http://10.9.1.224:5000/v2.0/tokens", body, "post");
        String j=(String)r.readEntity(String.class);
        JSONObject p=new JSONObject(j);
        try {
            System.out.println(j);
           String token= (String)((JSONObject)((JSONObject)p.get("access")).get("token")).get("id");
           r=t.request4OSgenericService("http://10.9.1.224:5000/v3/users", token, "get");
           j=(String)r.readEntity(String.class);
            System.out.println(j);
        } catch (Exception ex) {
            Logger.getLogger(TackerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
    
