/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MDBInt;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author agalletta
 */
public class MapInfo {

    ArrayList <Shape> shapes;
    ArrayList <Collegamenti> collegamenti;

    public MapInfo() {
        shapes = new ArrayList();
        collegamenti = new ArrayList();
        
    }
    
    public void addShape(Shape s){
        shapes.add(s);
           
    }
    
    public Shape getShape(String idCloud){
        
        Shape s;
        String id;
        
        for(int i=0;i<shapes.size();i++){
            s=shapes.get(i);
            id=s.getIdCloud();
            if(idCloud.equals(id)){
                return s;
            
            }         
         }
        return  null;
    
    }
    
   public void addCollegamento(Collegamenti c){
        collegamenti.add(c);
           
    }
    
    public JSONObject toJson() {
        JSONArray zone = new JSONArray();
        JSONArray links = new JSONArray();
        JSONObject json = new JSONObject();
        int i =0;

        try {
            for( i =0; i<shapes.size();i++){
                zone.put(shapes.get(i).toJson());
            }
            for( i =0; i<collegamenti.size();i++){
                links.put(collegamenti.get(i).toJson());
            }
            
            
            json.put("Shapes", zone);
            json.put("Links", links);
            
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
