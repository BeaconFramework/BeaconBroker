/** Copyright 2016, University of Messina.
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

package JClouds_Adapter;

/**
 *
 * @author Giuseppe Tricomi
 */
public class OpenstackInfoContainer {

    public OpenstackInfoContainer(String idCloud,String endpoint, String tenant, String user, String password, String region) {
        this.endpoint = endpoint;
        this.tenant = tenant;
        this.user = user;
        this.password = password;
        this.region = region;
        this.idCloud=idCloud;
    }
       
//<editor-fold defaultstate="collapsed" desc="Variable">
    private String idCloud;
    private String endpoint;
    private String tenant;
    private String user;
    private String password;
    private String region;
    
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Getter&Setter">
    public String getEndpoint() {
        return endpoint;
    }

    public String getIdCloud() {
        return idCloud;
    }

    public void setIdCloud(String idCloud) {
        this.idCloud = idCloud;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
//</editor-fold>    
}
