/**
 * Copyright 2016, University of Messina.
 * 
* Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
* http://www.apache.org/licenses/LICENSE-2.0
 * 
* Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package utils;


import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author agalletta
 */
public class RunTimeInfo {

    private String idCloud, stackName, phisicalResourceId, resourceName, localResourceName, type, stackUuid,region,uuidTemplate ;
    private boolean state;
    
    public RunTimeInfo() {
        
    }

    public RunTimeInfo(String idCloud, String stackName, String phisicalResourceId, String resourceName, String localResourceName, String type, String stackUuid,String region, boolean state, String uuidTemplate) {
        this.idCloud = idCloud;
        this.stackName = stackName;
        this.phisicalResourceId = phisicalResourceId;
        this.resourceName = resourceName;
        this.localResourceName = localResourceName;
        this.type = type;
        this.stackUuid=stackUuid;
        this.region=region;
        this.state=state;
        this.uuidTemplate=uuidTemplate;
    }
    
    public RunTimeInfo(String fromJsonString) {

        JSONObject json;
        try {
            json = new JSONObject(fromJsonString);
            this.idCloud = json.getString("idCloud");
            this.stackName = json.getString("stackName");
            this.phisicalResourceId = json.getString("phisicalResourceId");
            this.resourceName = json.getString("resourceName");
            this.localResourceName = json.getString("localResourceName");
            this.type = json.getString("type");
            this.stackUuid = json.getString("stackUuid");
            this.region=json.getString("region");
            this.state=json.getBoolean("state");
            this.uuidTemplate=json.getString("uuidTemplate");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
     }

    public String getIdCloud() {
        return idCloud;
    }

    public void setIdCloud(String idCloud) {
        this.idCloud = idCloud;
    }

    public String getStackName() {
        return stackName;
    }

    public void setStackName(String stackName) {
        this.stackName = stackName;
    }

    public String getPhisicalResourceId() {
        return phisicalResourceId;
    }

    public void setPhisicalResourceId(String phisicalResourceId) {
        this.phisicalResourceId = phisicalResourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getLocalResourceName() {
        return localResourceName;
    }

    public void setLocalResourceName(String localResourceName) {
        this.localResourceName = localResourceName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStackUuid() {
        return stackUuid;
    }

    public void setStackUuid(String stackUuid) {
        this.stackUuid = stackUuid;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getUuidTemplate() {
        return uuidTemplate;
    }

    public void setUuidTemplate(String uuidTemplate) {
        this.uuidTemplate = uuidTemplate;
    }
    
    
    
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("idCloud", idCloud);
            json.put("stackName", stackName);
            json.put("stackUuid", stackUuid);
            json.put("phisicalResourceId", phisicalResourceId);
            json.put("resourceName", resourceName);
            json.put("localResourceName", localResourceName);
            json.put("type", type);
            json.put("region", region);
            json.put("state", state);
            json.put("uuidTemplate", uuidTemplate);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return json;
    }
    
    @Override
    public String toString(){
        return this.toJson().toString();
    
    }

}