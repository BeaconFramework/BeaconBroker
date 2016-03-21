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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author agalletta
 */
public class FederatedCloud {

    private String cloudId, idmEndpoint, netManagerEndpoint;
    private ArrayList<JSONObject> fedAgentList, networks;
    private JSONObject netBlueprint;

    public FederatedCloud(String cloudId, String idmEndpoint) {
        this.cloudId = cloudId;
        this.idmEndpoint = idmEndpoint;
    }

    public FederatedCloud(String cloudId, String idmEndpoint, String netManagerEndpoint, ArrayList<JSONObject> fedAgentList, ArrayList<JSONObject> networks, JSONObject netBlueprint) {
        this.cloudId = cloudId;
        this.idmEndpoint = idmEndpoint;
        this.netManagerEndpoint = netManagerEndpoint;
        this.fedAgentList = fedAgentList;
        this.networks = networks;
        this.netBlueprint = netBlueprint;
    }

    public FederatedCloud(String fromJson) throws ParseException {
        JSONParser parser;
        JSONObject fedCloud;
        JSONArray federationAgentList;
        JSONArray networkList;

        parser = new JSONParser();
        fedCloud = (JSONObject) parser.parse(fromJson);
        this.cloudId = (String) fedCloud.get("cloudId");
        System.out.println("ssssssssss "+fedCloud.get("idmEndpoint"));
        this.idmEndpoint = (String) fedCloud.get("idmEndpoint");
        System.out.println("vvvvvvvvvv "+idmEndpoint);
        federationAgentList = (JSONArray) fedCloud.get("federationAgentList");
        if (federationAgentList != null) {
            this.fedAgentList = new ArrayList<JSONObject>(federationAgentList.subList(0, federationAgentList.size()));
        }
        networkList = (JSONArray) fedCloud.get("networkList");
        if (networkList != null) {
            this.networks = new ArrayList<JSONObject>(networkList.subList(0, networkList.size()));
        }
        this.netManagerEndpoint = (String) fedCloud.get("netManagerEndpoint");
        this.netBlueprint = (JSONObject) fedCloud.get("netBlueprint");

    }

    public String getCloudId() {
        return cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    public String getIdmEndpoint() {
        return idmEndpoint;
    }

    public void setIdmEndpoint(String idmEndpoint) {
        this.idmEndpoint = idmEndpoint;
    }

    public String getNetManagerEndpoint() {
        return netManagerEndpoint;
    }

    public void setNetManagerEndpoint(String netManagerEndpoint) {
        this.netManagerEndpoint = netManagerEndpoint;
    }

    public ArrayList<JSONObject> getFedAgentList() {
        return fedAgentList;
    }

    public void setFedAgentList(ArrayList<JSONObject> fedAgentList) {
        this.fedAgentList = fedAgentList;
    }

    public ArrayList<JSONObject> getNetworks() {
        return networks;
    }

    public void setNetworks(ArrayList<JSONObject> networks) {
        this.networks = networks;
    }

    public JSONObject getNetBlueprint() {
        return netBlueprint;
    }

    public void setNetBlueprint(JSONObject netBlueprint) {
        this.netBlueprint = netBlueprint;
    }

    public JSONObject toJSON() {

        JSONObject fedCloud = new JSONObject();
        JSONArray federationAgentList = new JSONArray();
        JSONArray networkList = new JSONArray();

        fedCloud.put("cloudId", cloudId);
        System.out.println("tre "+idmEndpoint);
        fedCloud.put("idmEndpoint", idmEndpoint);

        if (fedAgentList != null) {
            federationAgentList.addAll(this.fedAgentList);
            fedCloud.put("federationAgentList", federationAgentList);
        }
        if (networks != null) {
            networkList.addAll(this.networks);
            fedCloud.put("networkList", networkList);
        }
        if (netManagerEndpoint != null) {
            fedCloud.put("netManagerEndpoint", netManagerEndpoint);
        }
        if (netManagerEndpoint != null) {
            fedCloud.put("netBlueprint", netBlueprint);
        }

        return fedCloud;
    }

    @Override
    public String toString() {

        JSONObject fedCloud = new JSONObject();
        JSONArray federationAgentList = new JSONArray();
        JSONArray networkList = new JSONArray();

        fedCloud.put("cloudId", cloudId);
        fedCloud.put("idmEndpoint", idmEndpoint);

        if (fedAgentList != null) {
            federationAgentList.addAll(this.fedAgentList);
            fedCloud.put("federationAgentList", federationAgentList);
        }
        if (networks != null) {
            networkList.addAll(this.networks);
            fedCloud.put("networkList", networkList);
        }
        if (netManagerEndpoint != null) {
            fedCloud.put("netManagerEndpoint", netManagerEndpoint);
        }
        if (netManagerEndpoint != null) {
            fedCloud.put("netBlueprint", netBlueprint);
        }

        return fedCloud.toString();
    }

}
