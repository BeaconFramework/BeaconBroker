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

package MDBInt;

import java.util.ArrayList;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author agalletta
 * @author gtricomi
 */
public class FederationUser {
//<editor-fold defaultstate="collapsed" desc="Variable definitions"> 
    private String token; //added for better information management
    private String user, password;
    private ArrayList <JSONObject> credentials;
//</editor-fold>
    public FederationUser(String user, String password) {
        this.user = user;
        this.password = utils.staticFunctionality.toMd5(password);
        this.credentials = new ArrayList();
        
    }

    public FederationUser(String user, String password, ArrayList<JSONObject> credentials) {
        this.user = user;
        this.password = utils.staticFunctionality.toMd5(password);
        this.credentials = credentials;
        this.token=this.generate_token();
    }
    /**
     * Used when FEderation user already exist on MongoDB
     * @param fromJson
     * @throws ParseException 
     */
    public FederationUser(String fromJson) throws ParseException {
        JSONParser parser;
        JSONObject obj;
        JSONArray array;

        parser = new JSONParser();
        obj = (JSONObject) parser.parse(fromJson);
        array = (JSONArray) obj.get("crediantialList");
        this.user = (String) obj.get("federationUser");
        this.password = (String) obj.get("federationPassword");
        this.credentials = new ArrayList<JSONObject>(array.subList(0, array.size()));
        if((String) obj.get("token")==null)
            this.token=this.generate_token();
        else
           this.token=(String) obj.get("token");
    }

    public void addCredentials(FederatedUser credential){
        this.credentials.add(credential.toJSON());    
    }
    
    public JSONObject toJSON() {

        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();

        array.addAll(this.credentials);
        obj.put("federationUser", user);
        obj.put("federationPassword", password);
        obj.put("crediantialList", array);
        obj.put("token",this.token);
        return obj;
    }
    
    @Override
    public String toString() {
        return this.toJSON().toString();
    }
    
    public boolean deleteCredential(String cloud) {

        JSONObject elem;
        boolean deleted = false;

        for (int count = 0; count < credentials.size(); count++) {
            elem = credentials.get(count);
            if (cloud.equalsIgnoreCase((String) elem.get("federatedCloud"))) {
                credentials.remove(count);
                deleted = true;
                break;
            }
        }
        return deleted;
    }
    
    public boolean updateFederatedPassword(String cloud, String newPassword) {

        JSONObject elem;
        boolean updated = false;

        for (int count = 0; count < credentials.size(); count++) {
            elem = credentials.get(count);
            if (cloud.equalsIgnoreCase((String) elem.get("federatedCloud"))) {
                elem.put("federatedPassword", newPassword);
                updated = true;
                break;
            }
        }
        return updated;
    }
    
    public JSONObject getCredentialForCloud(String cloud) {

        JSONObject elem;

        for (int count = 0; count < credentials.size(); count++) {
            elem = credentials.get(count);
            if (cloud.equalsIgnoreCase((String) elem.get("federatedCloud"))) {
                return elem;
            }
        }
        return null;
    }

     /**
     * 
     * @return 
     * @author gtricomi
     */
    private String generate_token(){
        return UUID.randomUUID().toString();
    }
    
    
 //<editor-fold defaultstate="collapsed" desc="Variable&Setter/Getter">    
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }
    /**
     * Use this function change federation user password. 
     * Pay attention when use it. 
     * @param password 
     */
    public void setPassword(String password) {
        this.password = utils.staticFunctionality.toMd5(password);
    }

    public ArrayList<JSONObject> getCredentials() {
        return credentials;
    }

    public void setCredentials(ArrayList<JSONObject> credentials) {
        this.credentials = credentials;
    }
    //</editor-fold>
}
