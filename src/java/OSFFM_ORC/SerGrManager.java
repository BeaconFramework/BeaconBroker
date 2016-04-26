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

package OSFFM_ORC;

//<editor-fold defaultstate="collapsed" desc="Import Section">
import MDBInt.DBMongo;
import OSFFM_ORC.Utils.SerGrInfoContainer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//</editor-fold> 

/**
 * This class is use to analyse "OS::Beacon::ServiceGroupManagement" Manifest fragment,
 * and to prepare the correct deploy instructions. 
 * It retrieve all information and create an instance class that is able to indentify all elements needed by 
 * servicegroup, extracting them from origina manifest and insert inside that Object. 
 * That Object need to create a JSONObject that contains all these information to create a valid YAML that will be passed 
 * to target HEAT.
 * @author Giuseppe Tricomi
 */
public class SerGrManager {
     
//<editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor.
     */ 
    public SerGrManager(){
        this.georeference= "";
        this.container=new SerGrInfoContainer();
        this.groupName="";
        this.serviceGroup=new JSONObject();
    }
//</editor-fold>   
    /**
     * Take JSONObject properties for service group and store it.
     * @param prop
     * @throws JSONException 
     */
    public void consumeSerGr(JSONObject prop)throws JSONException{
        this.groupName=prop.getString("name");
        this.setGeoreference(prop.getJSONObject("geo_deploy").getString("get_resource"));
        this.serviceGroup=prop.getJSONObject("resource");
    }
    /**
     * Return resource involved in service group.
     * @return
     * @throws JSONException 
     */
    public ArrayList<String> getInnerResource()throws JSONException{
        ArrayList<String> resources=new ArrayList<String>();
        Iterator it=this.serviceGroup.keys();
        while(it.hasNext()){
            resources.add(this.serviceGroup.getJSONObject((String)it.next()).getString("get_resource"));
        }
        return resources;
    }
    public void verifycorrelation(String resName,JSONObject outputelem,String outName){
        if(this.container.resourceIsPresent(resName)){
            this.container.addOutput(outputelem,outName);
        }
    }

   /* public void resourceRecursiveDeepInspection(JSONObject properties)throws JSONException{
        Iterator it=properties.keys();
        while(it.hasNext()){
            Object tmp=properties.get((String)it.next());
            if(tmp instanceof org.json.simple.JSONArray){
                //richiamo la funzione ricorsivamente verificando tutti gli elementi dell'array
            }
            else if(tmp instanceof JSONObject){
                //richiamo la funzione ricorsiva
            }
            else{
                if(((HashMap)tmp).containsKey("get_resource")){
                    
                }else if(((HashMap)tmp).containsKey("get_param")){
                    
                }else if(((HashMap)tmp).containsKey("get_attr")){
                    //prendere solo il primo elemento dell array
                    String resName=(String)((org.json.simple.JSONArray)((HashMap)tmp).get("get_attr")).get(0);//da restituire o inserire in lista
                }
                    
 
            }
        }
    }*/
    
    public void insertOutContainer(JSONObject r,String name){
        this.container.addOutput(r,name);
    } 
    public void insertResContainer(JSONObject r, String name){
        this.container.addResource(r,name);
    } 
    public void insertParContainer(JSONObject r,String name){
        this.container.addParameter(r,name);
    }
    public HashMap<String,JSONObject> getParContainer(){
        return this.container.returnStringParameters();
    }
    public HashMap<String,JSONObject> getResContainer(){
        return this.container.returnStringResources();
    }
    public HashMap<String,JSONObject> getOutContainer(){
        return this.container.returnStringOutputs();
    }
    
    public String getGeoreference() {
        return georeference;
    }
    
    private void setGeoreference(String georeference) {
        this.georeference = georeference;
    }
//<editor-fold defaultstate="collapsed" desc="Variable">
    private String georeference; //questo serve come parametro per la chiamta da inoltrare al manifest manger per ottenere il
                                //luoghi dove effettuare il deploy
    private String groupName;
    private JSONObject serviceGroup; //It contains the resource object that will be treated by osffm to manage this service group
    private SerGrInfoContainer container; //it contains the resource that will be composed in YAML file and passed to target HEAT
//</editor-fold>
    
    
}
