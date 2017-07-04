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
package OSFFM_ORC.Utils.SFC_NFV;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Giuseppe Tricomi
 */
public class VNFDcontainer extends GenericVNFcontainer{
    //<editor-fold defaultstate="collapsed" desc="Variable">
    private HashMap<String,Object> metadata;
    //</editor-fold>

    public HashMap<String,Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(HashMap<String,Object> metadatavalue) throws JSONException{
        this.metadata =  metadatavalue;
    }

    public VNFDcontainer() {
        super();
        this.metadata = new HashMap<String,Object>();
    }
    
    
    @Override
    public void addINtopologytemplates(String key, Object node) throws JSONException{
        this.topologyTemplates.put(key, node); 
    }
    
    @Override
    public String getTemplate() throws JSONException {
        Yaml yaml = new Yaml();
        JSONObject template=new JSONObject();
        String outputti="";
        String prettyJSONString = "";
        template.put("tosca_definitions_version", this.tosca_definitions_version);
        template.put("description", this.description);
        template.put("metadata", this.metadata);
        template.put("topology_template",this.topologyTemplates);
        prettyJSONString =template.toString();
        Map<String, Object> tmpmap2 = (Map<String, Object>) yaml.load(prettyJSONString);
        outputti =yaml.dump(tmpmap2);
        return outputti;
        //return template.toString(1);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addINnodetemplates(String key, JSONObject node) throws JSONException {
        this.nodeTemplates.put(key, node);
    }
}
