/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OSFFM_ORC;


//<editor-fold defaultstate="collapsed" desc="Import Section">
//import MDBInt.DBMongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//</editor-fold> 

/**
 * Questa classe deve gestire i template di OneFlow di un service Manifest tenendo in conto anche la georeferenziazione
 * @author gtricomi
 */
public class OneTemplateManager {
//<editor-fold defaultstate="collapsed" desc="Constructor">    
    /**
     * Constructor.
     */ 
    public OneTemplateManager(){
        this.georeference= "";
        this.oTempName="";
        this.oTempGroup=new JSONObject();
    }
//</editor-fold>    
     /**
     * Take JSONObject properties for service group and store it.
     * @param prop
     * @throws JSONException 
     */
    public void consumeOneTemp(JSONObject prop)throws JSONException{
        this.oTempName=prop.getString("name");
        this.setGeoreference(prop.getJSONObject("geo_deploy").getString("get_resource"));
        this.oTempGroup=prop.getJSONObject("onetemplate");
    }
    
    /**
     * Return resource involved in service group.
     * @return
     * @throws JSONException 
     */
  /*  public ArrayList<String> getInnerResource()throws JSONException{
        ArrayList<String> resources=new ArrayList<String>();
        Iterator it=this.oTempGroup.keys();
        while(it.hasNext()){
            resources.add(this.oTempGroup.getJSONObject((String)it.next()).getString("get_resource"));
        }
        return resources;
    }
   */ 
    
     public String getGeoreference() {
        return georeference;
    }
    
    private void setGeoreference(String georeference) {
        this.georeference = georeference;
    }
//<editor-fold defaultstate="collapsed" desc="Variable">
    private String georeference; //questo serve come parametro per la chiamata da inoltrare al manifest manger per ottenere il
                                //luoghi dove effettuare il deploy
    private String oTempName;
    private JSONObject oTempGroup; //It contains the Template object that will be forwarded to OneFlow
//</editor-fold>
    
    
    
    
}
