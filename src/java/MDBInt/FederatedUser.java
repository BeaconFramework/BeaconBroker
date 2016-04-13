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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author agalletta
 */
public class FederatedUser {

    private String user, cloud, password,region="RegionOne";

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public FederatedUser(String user, String cloud, String password) {
        this.user = user;
        this.cloud = cloud;
        this.password = password;
    }

    public FederatedUser(String fromJson) throws ParseException {
        JSONParser parser;
        JSONObject obj;

        parser = new JSONParser();
        obj = (JSONObject) parser.parse(fromJson);
        this.user = (String) obj.get("federatedUser");
        this.cloud = (String) obj.get("federatedCloud");
        this.password = (String) obj.get("federatedPassword");
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCloud() {
        return cloud;
    }

    public void setCloud(String cloud) {
        this.cloud = cloud;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public JSONObject toJSON() {

        JSONObject obj = new JSONObject();

        obj.put("federatedUser", user);
        obj.put("federatedCloud", cloud);
        obj.put("federatedPassword", password);

        return obj;
    }

    @Override
    public String toString() {

        JSONObject obj = new JSONObject();

        obj.put("federatedUser", user);
        obj.put("federatedCloud", cloud);
        obj.put("federatedPassword", password);

        return obj.toString();
    }

}
