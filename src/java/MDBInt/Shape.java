/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MDBInt;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author agalletta
 */
public class Shape {
    private final String idCloud, endPoint ;
    private boolean state;
    ArrayList <Risorse> risorse;
    JSONObject geoShape;

    public Shape(String idCloud, String endPoint, String geoShape, boolean state, ArrayList<Risorse> risorse) {
        this.idCloud = idCloud;
        this.endPoint = endPoint;
        try {
            this.geoShape = new JSONObject(geoShape);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        this.state = state;
        this.risorse = risorse;
    }
    
    public Shape(String idCloud, String endPoint, String geoShape, boolean state) {
        this.idCloud = idCloud;
        this.endPoint = endPoint;
         try {
            this.geoShape = new JSONObject(geoShape);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        this.state = state;
        this.risorse = new ArrayList();
    }

    public String getIdCloud() {
        return idCloud;
    }
    
    
    
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray resources = new JSONArray();

        try {
            for(int i =0; i<risorse.size();i++){
                resources.put(risorse.get(i).toJson());
            }
            
            json.put("idCloud", idCloud);
            json.put("endPoint", endPoint);
            json.put("geoShape", geoShape);
            json.put("state", state);
            json.put("resources", resources);
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return json;
    }
    
    @Override
    public String toString(){
        return this.toJson().toString();
    }
    
    public void addResource(Risorse r){
        risorse.add(r);     
    }
    
    public void setState(boolean state){
        this.state=state;
    }
    
    
}
