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
import OSFFM_ORC.SerGrManager;
import OSFFM_ORC.Utils.Exception.NotFoundGeoRefException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

//</editor-fold>  
/**
 *
 * @author Giuseppe Tricomi
 */
public class ManifestManager implements Runnable{
    //<editor-fold defaultstate="collapsed" desc="Variable">
    JSONObject manifest;
    String nameSetRes;
    JSONObject resource,outputs, parameters;
    LinkedHashMap<String,LinkedHashMap> table_resourceset;
    GeoManager geo_man;
    HashMap<String,Object> georef_table,serGr_table;
    String tempVers,description;

    //</editor-fold>
 //<editor-fold defaultstate="collapsed" desc="Getter&Setter">   
    public String getTempVers() {
        return tempVers;
    }

    public void setTempVers(String tempVers) {
        this.tempVers = tempVers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public ManifestManager(String name,JSONObject manifest){
        this.manifest=manifest;
        this.nameSetRes=name;
        this.table_resourceset=new LinkedHashMap<String,LinkedHashMap>();
        this.table_resourceset.put("OS::Beacon::ServiceGroupManagement",new LinkedHashMap<String,JSONObject>());
        this.table_resourceset.put("OS::Beacon::fedNetManagement",new LinkedHashMap<String,JSONObject>());
        this.table_resourceset.put("OS::Beacon::PoliciesAccManagement",new LinkedHashMap<String,JSONObject>());
        this.table_resourceset.put("OS::Beacon::fedSecManagement",new LinkedHashMap<String,JSONObject>());
        this.table_resourceset.put("OS::Beacon::ScalingPolicy",new LinkedHashMap<String,JSONObject>());
        this.table_resourceset.put("OS::Beacon::Georeferenced_deploy",new LinkedHashMap<String,JSONObject>());
        this.geo_man=new GeoManager();
        this.serGr_table=new HashMap<String,Object>();
    }
    //</editor-fold>
    /**
     * this function read manifest identifing the resources for BEACON starting MON calls,
     * identifing Elasticity policy for that resource and managing informaion for deploy actions.
     */
    public void analizeGlobalManifest() throws org.json.JSONException{//BEACON>>> da testare
        //BEACON>>> inserire chiamate per riempimento tempVersion+ Description
        this.parameters=this.manifest.getJSONObject("parameters");
        this.resource=this.manifest.getJSONObject("resources");
        this.outputs=this.manifest.getJSONObject("outputs");
        //BEACON Resource Extraction
        this.extractResourcefromManifest(this.resource);
        //GEOReference Analisys
        this.georef_table=this.elaborateGeoRef();
        //Service group Analisys
        Iterator it_keyset=this.table_resourceset.get("OS::Beacon::ServiceGroupManagement").keySet().iterator();
        while(it_keyset.hasNext()){
            SerGrManager sgObj=new SerGrManager();
            String resName=(String)it_keyset.next();
            this.elaborateSerGr(sgObj,(JSONObject)this.table_resourceset.get("OS::Beacon::ServiceGroupManagement").get(resName), resName);
            this.serGr_table.put(resName, sgObj);
        }
        //BEACON>>> verificare se sono state richiamate tutte le funzioni
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Resource Management functions">    
    /**
     * Extract resource from manifest and split Beacon element in right LinkedHashMap.
     * @param res 
     */
    private void extractResourcefromManifest(JSONObject res)throws JSONException{
        String str = null;
        Iterator it_res=res.keys();
        JSONObject tmp=null;
        //new org.json.JSONObject(res.toString());
        while(it_res.hasNext()){
            String key=(String)it_res.next();
            tmp=res.getJSONObject(key);
            str=tmp.getString("type");
            switch(str){
                case "OS::Beacon::ServiceGroupManagement":{
                    this.table_resourceset.get("OS::Beacon::ServiceGroupManagement").put(key, tmp);
                    break;
                }
                case "OS::Beacon::fedNetManagement":{//BEACON>>> questo deve essere gestito anche per febbraio? NO
                    this.table_resourceset.get("OS::Beacon::fedNetManagement").put(key, tmp);
                    break;
                }
                case "OS::Beacon::PoliciesAccManagement":{//Not used in this moment
                    this.table_resourceset.get("OS::Beacon::PoliciesAccManagement").put(key, tmp);
                    break;
                }
                case "OS::Beacon::fedSecManagement":{//Not used in this moment
                    this.table_resourceset.get("OS::Beacon::fedSecManagement").put(key, tmp);
                    break;
                }
                case "OS::Beacon::ScalingPolicy":{//Not used in this moment
                    this.table_resourceset.get("OS::Beacon::ScalingPolicy").put(key, tmp);
                    break;
                }
                case "OS::Beacon::Georeferenced_deploy":{
                    this.table_resourceset.get("OS::Beacon::Georeferenced_deploy").put(key, tmp);
                    break;
                }
                default :{
                    
                    break;
                }
            }
        } 
    }
//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="Service Group management Functions">    
    /**
     * This function is called to analyze the manifest in order to identify and pass the resource/parameter/output 
     * elements connected at the service group to SerGrManager Object.
     * @param sgObj, SerGrManager that will be used to manage the information of the service group
     * @param sg, JSONObject took from manifest that describe service group
     * @param sgName, String that represent name of service group
     */
    private void elaborateSerGr(SerGrManager sgObj,JSONObject sg,String sgName)throws JSONException{
        JSONObject properties=sg.getJSONObject("properties");
        sgObj.consumeSerGr(properties);
        ArrayList<String> innres=sgObj.getInnerResource();
        for(int index=0;index<innres.size();index++){
            String tmpA=innres.get(index);
            if(this.resource.has(tmpA))
            {
                JSONObject tmp=this.resource.getJSONObject(tmpA);//verificare se c'è oggetto oppure no e cosa restituisce se non trova nulla
                sgObj.insertResContainer(tmp,tmpA);
                this.resourceRecursiveDeepInspection(tmp.getJSONObject("properties"),sgObj);
                //questo inserisce parameter e risorse nel container se richiamati dalla risorsa, 
                //bisogna aggiungere la gestione dell'output
            }
        }
        this.outputAnalisys(sgObj);
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Parameter Management functions">
    public JSONObject getParameterbyName(String parName)throws JSONException{
        String key = null;
        Iterator it_par=this.parameters.keys();
        while(it_par.hasNext()){
            key=(String)it_par.next();
            if(parName.equals(key))
                return this.parameters.getJSONObject(key);
        }
        return null;
    }
//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="Common Manifest Elaboration Functions">    
    /**
     * This element make analisys of all output elements present in manifest to verify if one of this is related to
     * one of resource connected to servicegroup element analized. 
     * @param sgObj
     * @throws JSONException 
     */
    private void outputAnalisys(SerGrManager sgObj)throws JSONException{
        //verificare cosa c'è nell'elemento outputs
        Iterator it_out=this.outputs.keys();
        JSONObject tmp=null;
        while(it_out.hasNext()){
            String key=(String)it_out.next();
            tmp=this.outputs.getJSONObject(key);
            tmp=tmp.getJSONObject("value");
            JSONArray attr=tmp.getJSONArray("get_attr");
            String resName=attr.getString(0);
            sgObj.verifycorrelation(resName, this.outputs.getJSONObject(key));
        }
    }
    
    
    /**
     * Used for final part of deep manifest inspection.
     * @param key
     * @param val
     * @return 
     */
    private ArrayList<String> leafcheck(String key,Object val){
        ArrayList<String> result= new ArrayList<String>();
        switch (key) {
            case "get_resource":
                result.add("resource");
                result.add(val.toString());
                return result;
            case "get_param":
                result.add("parameter");
                result.add(val.toString());
                return result;
            case "get_attr":
                //prendere solo il primo elemento dell array
                //String resName=(String)((org.json.JSONArray)((HashMap)tmp).get("get_attr")).get(0);//da restituire o inserire in lista
                //System.out.println(resName);
                result.add("resource");//from this element it is extracted a resource
                result.add(val.toString());
                return result; //verificare bene
            default:
                 return null;
        }
    }
    
    
    
    /**
     * 
     * @param serGrName
     * @return 
     */
    private String manageYAMLcreation(String serGrName){
        SerGrManager sgm=(SerGrManager) this.serGr_table.get(serGrName);
        JSONObject jo=null;
        String tmp,resource="",output="",param="";
        HashMap<String,JSONObject> hsm=null;
        Iterator it;
        String key="";
        try{
            hsm=(HashMap<String,JSONObject>)sgm.getParContainer();
            it=hsm.keySet().iterator();
            param="parameters:{";
            while(it.hasNext()){
                key=(String)it.next();
                tmp=((JSONObject)hsm.get(key)).toString();
                param=param+"\""+key+"\": "+tmp+",";
            }
            param=param+"},";
        }
        catch(Exception e){
            
        }
        try{
            hsm=(HashMap<String,JSONObject>)sgm.getResContainer();
            it=hsm.keySet().iterator();
            key="";
            resource="resources:{";
            while(it.hasNext()){
                key=(String)it.next();
                tmp=((JSONObject)hsm.get(key)).toString();
                resource=resource+"\""+key+"\": "+tmp+",";
            }    
            resource=resource+"},";
        }
        catch(Exception e){
            
        }
        try{
            hsm=(HashMap<String,JSONObject>)sgm.getOutContainer();
            it=hsm.keySet().iterator();
            key="";
            output="outputs:{";
            while(it.hasNext()){
                key=(String)it.next();
                tmp=((JSONObject)hsm.get(key)).toString();
                output=output+"\""+key+"\": "+tmp+",";
            }    
            output=output+"}";
        }
        catch(Exception e){
        }
        return param+resource+output;
    }
    /**
     * 
     * @param jo
     * @return 
     */
    private String convertJSONtoYAML(JSONObject jo){
        String output="";
        try{
            // get json string
            String prettyJSONString = jo.toString(2);
            // mapping
            Yaml yaml = new Yaml();
            HashMap<String,Object> map = (HashMap<String, Object>) yaml.load(prettyJSONString);
            // convert to yaml string (yaml formatted string)
            output = yaml.dump(map);
        }
        catch(Exception e)
        {
        
        }
        return output;
    }    
        
        
        
    /**
     * 
     * @param manElem
     * @return 
     */
    public String ComposeJSON4element(String serGrName)throws JSONException{
        String joStr="{";
        joStr=joStr+"\"heat_template_version\":\""+this.getTempVers()+"\",";
        joStr=joStr+"\"description\":\""+this.getDescription()+"\",";
        joStr=joStr+this.manageYAMLcreation(serGrName)+"}";
        return convertJSONtoYAML(new JSONObject(joStr));
    }
    
    /**
     * This function analize JsonObject connected with properties field of the resource analized for retrieving the 
     * resources, parameters, attributes, connected to properties JSONObject, in order to retrieve all elements needed 
     * for manifest creation for target HEAT.
     * @param properties
     * @param sgObj
     * @throws JSONException 
     */
    private void resourceRecursiveDeepInspection(JSONObject properties,SerGrManager sgObj)throws JSONException{
        //analizzo l'elemento(di certo bisogna verificare nella key della mappa) alla ricerca di get_param, get_resource,get_attr e passandoli all'opportuna funzione di management 
        Iterator it=properties.keys();
        while(it.hasNext()){
            String key=(String)it.next();
            Object tmp=properties.get(key);
            if(tmp instanceof String){
                ArrayList<String> res=this.leafcheck(key, tmp);
                if (res!=null)
                {
                    boolean chk1,chk2;
                    chk1=res.get(0).equals("resource");
                    chk2=(this.resource.has(res.get(1)));
                    if(chk1&&chk2)
                    {
                        sgObj.insertResContainer(this.resource.getJSONObject(res.get(1)),res.get(1));
                        this.resourceRecursiveDeepInspection(this.resource.getJSONObject(res.get(1)) ,sgObj);
                    }
                    else{
                        chk1=res.get(0).equals("parameter");
                        chk2=(this.parameters.has(res.get(1)));
                        if(chk1&&chk2)
                        sgObj.insertParContainer(this.parameters.getJSONObject(res.get(1)),res.get(1));
                       // System.out.println("qui");
                    }
                }
            }
            else if(tmp instanceof JSONObject){
                //richiamo la funzione ricorsiva
                Iterator it2=((JSONObject)tmp).keys();
                while(it2.hasNext()){
                    String key2=(String)it2.next();
                    Object tmp2=((JSONObject)tmp).get(key2);
                    if(tmp2 instanceof JSONObject){
                        if(key.equals("resource")){
                   //sgObj.insertResContainer((JSONObject)tmp2,key2); //mettere una verifica per il get_resource?
                        }
                        this.resourceRecursiveDeepInspection((JSONObject) tmp2,sgObj);
                    }
                    else if(tmp2 instanceof org.json.JSONArray){
                        for(int index=0;index<((JSONArray)tmp2).length();index++){
                            if(((JSONArray)tmp2).get(index) instanceof JSONObject){
                                this.resourceRecursiveDeepInspection((JSONObject) ((JSONArray)tmp2).get(index) ,sgObj);
                            }
                            else{
                                ArrayList<String> res=this.leafcheck(key2, ((org.json.JSONArray)tmp2).get(0));
                                if (res!=null)
                                {
                                    boolean chk1,chk2;
                                    chk1=res.get(0).equals("resource");
                                    chk2=(this.resource.has(res.get(1)));
                                    if(chk1&&chk2)
                                    {
                                        sgObj.insertResContainer(this.resource.getJSONObject(res.get(1)),res.get(1));
                                        this.resourceRecursiveDeepInspection(this.resource.getJSONObject(res.get(1)) ,sgObj);
                                    }
                                    else 
                                    {
                                        chk1=res.get(0).equals("parameter");
                                        chk2=(this.parameters.has(res.get(1)));
                                        if(chk1&&chk2)
                                        sgObj.insertParContainer(this.parameters.getJSONObject(res.get(1)),res.get(1));
                                    }
                                }
                            }
                        }
                    }
                    else if(tmp2 instanceof String){
                        ArrayList<String> res=this.leafcheck(key2, tmp2);
                        if (res!=null)
                        {
                            boolean chk1,chk2;
                            chk1=res.get(0).equals("resource");
                            chk2=(this.resource.has(res.get(1)));
                            if(chk1&&chk2){
                                sgObj.insertResContainer(this.resource.getJSONObject(res.get(1)),res.get(1));
                                this.resourceRecursiveDeepInspection(this.resource.getJSONObject(res.get(1)) ,sgObj);
                            }
                            else 
                            {
                                chk1=res.get(0).equals("parameter");
                                chk2=(this.parameters.has(res.get(1)));
                                if(chk1&&chk2)
                                sgObj.insertParContainer(this.parameters.getJSONObject(res.get(1)),res.get(1));
                            }
                        }
                    }
                }
            } else if (tmp instanceof org.json.JSONArray) {
                for (int index = 0; index < ((JSONArray) tmp).length(); index++) {
                    if (((JSONArray) tmp).get(index) instanceof JSONObject) {
                        this.resourceRecursiveDeepInspection((JSONObject) ((JSONArray) tmp).get(index), sgObj);
                    } else {
                        ArrayList<String> res = this.leafcheck(key, ((org.json.JSONArray) tmp).get(0));
                        if (res != null) {
                            boolean chk1, chk2;
                            chk1 = res.get(0).equals("resource");
                            chk2 = (this.resource.has(res.get(1)));
                            if (chk1 && chk2) {
                                sgObj.insertResContainer(this.resource.getJSONObject(res.get(1)), res.get(1));
                                this.resourceRecursiveDeepInspection(this.resource.getJSONObject(res.get(1)), sgObj);
                            } else {
                                chk1 = res.get(0).equals("parameter");
                                chk2 = (this.parameters.has(res.get(1)));
                                if (chk1 && chk2) {
                                    sgObj.insertParContainer(this.parameters.getJSONObject(res.get(1)), res.get(1));
                                }
                            }
                        }
                    }
                }
            }
        }

    }
//</editor-fold>
    
    
//<editor-fold defaultstate="collapsed" desc="GeoReference management Functions">
    /**
     * This function is used to populate object GeoManager.
     * @return 
     */
    private HashMap<String,Object> elaborateGeoRef(){
        boolean completed=true;
        String error="";
        LinkedHashMap<String,Object> result=new LinkedHashMap<String,Object>();
        result.put("error", error);
        String name="";//Name of the geoshape
        Iterator itkey=((LinkedHashMap)this.table_resourceset.get("OS::Beacon::Georeferenced_deploy")).keySet().iterator();
        while(itkey.hasNext()){
            name=(String)itkey.next();
            try{
                if(geo_man.consume_georeference(name, (JSONObject)((LinkedHashMap)this.table_resourceset.get("OS::Beacon::Georeferenced_deploy")).get(name)))
                    throw new Exception("Generic Exception generated in elaborateGeoRef");
            }
            catch(Exception e){
                completed=false;
            }
        }
        result.put("completed", completed);
        return result;
    }
//</editor-fold>
    
    
    @Override
    public void run() {
        try{
            this.analizeGlobalManifest();
            OrchestrationManager.putEntryinTable(this.nameSetRes, this);
        }
        catch(Exception ex){
            
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
