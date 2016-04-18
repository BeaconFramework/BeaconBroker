/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MDBInt;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author agalletta
 */
public class Risorse {
   private String id, name;

    public Risorse(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("id", id);
            json.put("nameRes", name);
            
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
