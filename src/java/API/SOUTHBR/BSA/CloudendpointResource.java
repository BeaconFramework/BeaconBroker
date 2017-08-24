/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API.SOUTHBR.BSA;

import MDBInt.DBMongo;
import MDBInt.MDBIException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author giuseppe
 */
@Path("/bsa/cloudendpoint")
public class CloudendpointResource {

    @Context
    private UriInfo context;
    
    private DBMongo mongo;
    
    /**
     * Creates a new instance of CloudendpointResource
     */
    public CloudendpointResource() {
       this.mongo=new DBMongo(); 
       this.mongo.connectLocale("10.9.240.1");
    }

    /**
     * Retrieves representation of an instance of API.SOUTHBR.BSA.CloudendpointResource
     * @return an instance of java.lang.String
     */
    @GET
    @Path("/{tenant}/{uuidVM}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@PathParam("tenant") String tenant,@PathParam("uuidVM") String uuidVM) throws JSONException{
        ArrayList<String> twinVMs=mongo.findResourceMate(tenant,uuidVM);
        JSONObject result=new JSONObject();
        JSONArray ja=new JSONArray();
        Iterator i=twinVMs.iterator();
        int error=0;
        while(i.hasNext()){
            String tmp=(String)i.next();
            System.out.println(">>>>>"+tmp);
            
            try{
            JSONObject jo=new JSONObject(tmp);
            JSONObject dcEntry=new JSONObject(this.mongo.getDatacenter(tenant,(String)jo.get("idCloud")));
            jo.put("cloudEndpoint", (String)dcEntry.get("idmEndpoint") );
            jo.remove("_id");
            jo.remove("insertTimestamp");
            ja.put(jo);
            }
            catch(JSONException je){
                System.out.println("Exception occurred in retrieving TwinVms information");
                error++;
            }
            catch(MDBIException mbe ){
                System.out.println("Exception occurred in retrieving TwinVms information");
                error++;
            }
        }
        result.put("twinVMs", ja);
        result.put("errors", error);
        if(error>0)
            result.put("error_message","Information about "+error+" twin VMs are skipped because malformed");
        else
            result.put("error_message","");
        return result.toString(0);
        
    }

}



