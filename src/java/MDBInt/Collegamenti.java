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
public class Collegamenti {
    private String src,dst;

    public Collegamenti(String src, String dst) {
        this.src = src;
        this.dst = dst;
    }
    
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("src", src);
            json.put("dst", dst);
            
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
