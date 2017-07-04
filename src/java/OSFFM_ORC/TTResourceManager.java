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

import OSFFM_ORC.Utils.SFC_NFV.VNFDcontainer;
import OSFFM_ORC.Utils.SFC_NFV.VNFFGcontainer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.apache.log4j.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//</editor-fold> 

/**
 * Class used to extract Tacker Tosca Manifest from BEACON one.
 * This class is used to manage all information related to Tacker Tosca Manifest in order to support the reconstruction phase of the Tacker Manifest 
 * from the Beacon one.
 * @author Giuseppe Tricomi
 */
public class TTResourceManager extends GenericResourceManager {

    //<editor-fold defaultstate="collapsed" desc="Variable">
    private String groupName;
    private JSONObject toscaResourceGroup; //It contains the resource object that will be treated by osffm to manage this ToscaResourceManager
    private VNFDcontainer vnfd;
    private VNFFGcontainer vnffg;
    static Logger LOGGER = Logger.getLogger(TTResourceManager.class);
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Getter&Setter">
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public JSONObject getToscaResourceGroup() {
        return toscaResourceGroup;
    }

    public void setToscaResourceGroup(JSONObject toscaResourceGroup) {
        this.toscaResourceGroup = toscaResourceGroup;
    }

    public VNFDcontainer getVnfd() {
        return vnfd;
    }

    public void setVnfd(VNFDcontainer vnfd) {
        this.vnfd = vnfd;
    }

    public VNFFGcontainer getVnffg() {
        return vnffg;
    }

    public void setVnffg(VNFFGcontainer vnffg) {
        this.vnffg = vnffg;
    }
    
     //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor.
     */ 
    public TTResourceManager() throws JSONException{
        super();
        this.groupName="";
        this.toscaResourceGroup=new JSONObject();
        this.vnfd=new VNFDcontainer();
        this.vnffg=new VNFFGcontainer();
    }
//</editor-fold>   
    /**
     * Take JSONObject properties for tosca manifest resource and store it.
     * @param prop
     * @throws JSONException 
     */
    public void consumeTosman(JSONObject prop)throws JSONException{//RISISTEMARE IN FUNZIONE DEI PARAMETRI CHE COMPONGONO L?OGGETTO TOSCA SFC
        //this.groupName=prop.getString("name");
        this.setGeoreference(prop.getJSONObject("geo_deploy").getString("get_resource"));
        this.toscaResourceGroup = prop.getJSONObject("resource");
        this.vnfd.setTosca_definitions_version(prop.getString("vnfd_version"));
        this.vnfd.setDescription(prop.getString("vnfd_description"));
        HashMap<String,Object> tmp=new HashMap<String,Object>();
        tmp.put("template_name", prop.getString("vnfd_templatename"));
        this.vnfd.setMetadata(tmp);
        this.vnffg.setTosca_definitions_version(prop.getString("vnffg_version"));
        this.vnffg.setDescription(prop.getString("vnffg_description"));
        this.vnffg.setTTdescription(prop.getString("vnffg_toplogy_template_description"));
    }
    
    /**
     * Return resource involved in tosca manifest resource.
     * @return
     * @throws JSONException
     * TO BE VERIFIED
     */
    public ArrayList<String> getInnerResource()throws JSONException{
        ArrayList<String> resources=new ArrayList<String>();
        Iterator it=this.toscaResourceGroup.keys();
        while(it.hasNext()){
            Object ob=this.toscaResourceGroup.getJSONObject((String)it.next()).get("get_resource");
            if(ob.getClass()==String.class)
                resources.add((String)ob);
            else if(ob.getClass()==JSONArray.class){
                for(int r=0;r<((JSONArray)ob).length();r++)
                    resources.add(((JSONArray)ob).getString(r));
            }
        }
        return resources;
    }
 
    @Override
    public void insertOutContainer(JSONObject r, String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void insertResContainer(JSONObject r, String name){
        //questa funzione deve prendere la risorsa e capire se assegnarla al VNFD o al VNFFG
        String type="";
        try {
            type = r.getString("type");
        } catch (JSONException ex) {
            LOGGER.error("Exception occurred in resource \""+name+"\" analisys. It is impossible extract type property! \n"+ex.getMessage());
        }
        try{
            switch(type){
                case "tosca.nodes.nfv.VDU.Tacker":{
                    this.vnfd.addINnodetemplates(name, r);
                    break;
                }
                case "tosca.nodes.nfv.CP.Tacker":{
                    this.vnfd.addINnodetemplates(name, r);
                    break;
                }
                case "tosca.nodes.nfv.VL":{
                    this.vnfd.addINnodetemplates(name, r);
                    break;
                }
                case "tosca.groups.nfv.VNFFG":{
                    this.vnffg.addInGroups(name, r);
                    break;
                }
                case "tosca.nodes.nfv.FP.Tacker":{
                    this.vnffg.addINnodetemplates(name, r);
                    break;
                }
               /* case "tosca.information.Beacon":{//Sbagliato, questo non può esistere:MODIFICARE
                    JSONObject sup=r.getJSONObject("properties");
                    this.vnfd.setTosca_definitions_version(sup.getString("vnfd_version"));
                    this.vnfd.setDescription(sup.getString("vnfd_description"));
                    this.vnfd.setMetadata(new JSONObject().put("template_name",sup.getString("vnfd_templatename") ));
                    this.vnffg.setTosca_definitions_version(sup.getString("vnffg_version"));
                    this.vnffg.setDescription(sup.getString("vnffg_description"));
                    this.vnffg.setTTdescription(sup.getString("vnffg_toplogy_template_description"));
                    break;
                }*/
                default:{
                    LOGGER.info("[WARNING]: Unrecognized resources for VNF Manifest! Resource "+name+" it was dropped.");
                    break;
                }
            }
        }catch(JSONException ex) {
            LOGGER.error("Exception occurred in resource \""+name+"\" analisys. It is'nt possible add resource inside container! \n"+ex.getMessage());
        }
    }

    @Override
    public void insertParContainer(JSONObject r, String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
