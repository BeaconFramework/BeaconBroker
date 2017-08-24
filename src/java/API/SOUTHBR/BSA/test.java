/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API.SOUTHBR.BSA;

import MDBInt.DBMongo;
import MDBInt.MDBIException;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author giuseppe
 */
public class test {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws MDBIException{
        DBMongo mongo=new DBMongo(); 
        mongo.connectLocale("10.9.240.1");
        ArrayList<String> twinVMs=mongo.findResourceMate("review", "284ab3e8-f7a9-484c-bd3f-eb468bbee3ef");
        JSONObject result=new JSONObject();
        JSONArray ja=new JSONArray();
        Iterator i=twinVMs.iterator();
        int error=0;
        while(i.hasNext()){
            String tmp=(String)i.next();
            System.out.println(">>>>>"+tmp);
            
                try{
            JSONObject jo=new JSONObject(tmp);
            JSONObject dcEntry=new JSONObject(mongo.getDatacenter("review",(String)jo.get("idCloud")));
            jo.put("cloudEndpoint", (String) dcEntry.get("idmEndpoint") );
            jo.remove("_id");
            jo.remove("insertTimestamp");
            ja.put(jo);
            }
            catch(JSONException je){
                System.out.println("Exception occurred in retrieving TwinVms information");
                error++;
            }
            
         
        }
         try{
        result.put("twinVMs", ja);
        result.put("errors", error);
        if(error>0)
            result.put("error_message","Information about "+error+" twin VMs are skipped because malformed");
             System.out.println(result.toString(0));
         }
         catch(JSONException je){
                System.out.println("errr");
   
            }
    }
    
}
