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
//<editor-fold defaultstate="collapsed" desc="Import Section">
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;
//</editor-fold> 

/**
 *
 * @author Giuseppe Tricomi
 */
public class SerGrInfoContainer {

//<editor-fold defaultstate="collapsed" desc="Constructor">
    public SerGrInfoContainer(){
        this.newManifestElemets = new HashMap<String,HashMap<String,JSONObject>>();//valutare utilizzo HashMap
        this.newManifestElemets.put("resources", new HashMap<String,JSONObject>());
        this.newManifestElemets.put("parameters", new HashMap<String,JSONObject>());
        this.newManifestElemets.put("outputs", new HashMap<String,JSONObject>());
        this.resourceset=new LinkedHashSet<String>();
    }
//</editor-fold> 
//<editor-fold defaultstate="collapsed" desc="Method Functions">
    public void addResource(JSONObject resource,String name) {
        ((HashMap<String,JSONObject>)this.newManifestElemets.get("resources")).put(name,resource);
        //this.resourceList.add(name);
        this.resourceset.add(name);
    }
    public void addParameter(JSONObject parameter,String name){
        ((HashMap<String,JSONObject>)this.newManifestElemets.get("parameters")).put(name,parameter);
    }
    public void addOutput(JSONObject output,String name){
        ((HashMap<String,JSONObject>)this.newManifestElemets.get("outputs")).put(name,output);
    }
    public boolean resourceIsPresent(String resName){
        return this.resourceset.contains(resName);
    }
    //public 
    //BEACON>>> it is needs method toJSONObject
    
    
    public HashMap<String,JSONObject> returnStringParameters(){
        return this.newManifestElemets.get("parameters");
    }
    public HashMap<String,JSONObject> returnStringResources(){
        return this.newManifestElemets.get("resources");
    }
    public HashMap<String,JSONObject> returnStringOutputs(){
        return this.newManifestElemets.get("outputs");
    }
    
   
//</editor-fold> 
    
//<editor-fold defaultstate="collapsed" desc="Variable">
private HashMap<String,HashMap<String,JSONObject>> newManifestElemets;
//private ArrayList<String> resourceList;
private LinkedHashSet<String> resourceset;
//</editor-fold> 
}
