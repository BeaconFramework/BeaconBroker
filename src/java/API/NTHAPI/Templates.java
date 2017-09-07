/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package API.NTHAPI;

import static API.NTHAPI.ManifestcreatesResource.LOGGER;
import static API.NTHAPI.SitesResource.LOGGER;
import MDBInt.DBMongo;
import MDBInt.Splitter;
import OSFFMIDM.SimpleIDM;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * REST Web Service
 *
 * @author gtricomi
 */
@Path("/os2os/{tenant}/templates/")
public class Templates {

    @Context
    private UriInfo context;
    static final Logger LOGGER = Logger.getLogger(Templates.class);
    DBMongo m;
    Splitter s;
    /**
     * Creates a new instance of ManifestcreatesResource
     */
    public Templates() {
       this.m=new DBMongo();
       //this.m.init();
       this.m.connectLocale("10.9.240.1");//this.m.connectLocale(this.m.getMdbIp());
       this.s=new Splitter(m);
    }

    /**
     * Retrieves list of all templates stored for the tenant.
     * @return an instance of java.lang.String
     * @author gtricomi
     */
    @GET
    @Path("")
    @Produces("application/json")
    public String listTemplates(@PathParam("tenant") String tenant){
        ArrayList arT=new ArrayList();
        arT=this.m.listTemplates(tenant);
        System.out.println("ART:"+arT.size());
        //Iterator i=arT.iterator();
        JSONParser p=new JSONParser();
        JSONArray ja=new JSONArray();
        JSONObject reply= new JSONObject();
        for(int i=0;i<arT.size();i++){
            try {
                JSONObject j=(JSONObject)p.parse((String)arT.get(i));
                System.out.println(j);
                ja.add(j);
                System.out.println(ja);
                
            } catch (ParseException ex) {
                LOGGER.error("Information retrieved from DB is not parsable.\n"+ex.getMessage());
                reply.put("returncode", 1); 
                reply.put("errormesg", "Information retrieved from DB is not parsable.\n"+ex.getMessage());
                return reply.toJSONString();
            }
        }
        reply.put("data", ja);
        reply.put("count",ja.size());
        reply.put("returncode", 0); 
        reply.put("errormesg", "None");
        return reply.toJSONString();
        
    }
    
    /**
     * Retrieves the template stored for the tenant pointed by uuidTemplate.
     * @param uuidTemplate 
     * @return an instance of java.lang.String
     * @author gtricomi
     */
    @GET
    @Path("/{uuidTemplate}")
    @Produces("application/json")
    public String getTemplate(@PathParam("uuidTemplate") String uuidTemplate,@PathParam("tenant") String tenant){
        JSONObject reply=new JSONObject();
        try{
            JSONParser p=new JSONParser();
            reply.put("templates",(JSONObject)p.parse(this.m.getTemplate(tenant, uuidTemplate)));
            reply.put("returncode", 0); 
            reply.put("errormesg", "None");
            return reply.toJSONString();
        }
        catch(Exception e){
            LOGGER.error("Exception is occurred in retrieve manifest template with UUID:"+uuidTemplate+".\n"+e.getMessage());
            reply.put("returncode", 1); 
            reply.put("errormesg", "Exception is occurred in retrieve manifest template with UUID:"+uuidTemplate+".\n"+e.getMessage());
            return reply.toJSONString();
        }
    }
    
    /**
     * Retrieves the template stored for the tenant pointed by uuidTemplate.
     * @param uuidTemplate 
     * @return an instance of java.lang.String
     * @author gtricomi
     */
    @GET
    @Path("/{uuidTemplate}/manifest")
    @Produces("application/json")
    public String getTemplateManifest(@PathParam("uuidTemplate") String uuidTemplate,@PathParam("tenant") String tenant){
        JSONObject reply=new JSONObject();
        try{
            JSONParser p=new JSONParser();
            reply.put("templates",new org.json.JSONObject(this.s.ricomponiJsonManifest(uuidTemplate, tenant)));//(JSONObject)p.parse(this.m.getTemplate(tenant, uuidTemplate)));
            reply.put("returncode", 0); 
            reply.put("errormesg", "None");
            return reply.toJSONString();
        }
        catch(Exception e){
            LOGGER.error("Exception is occurred in retrieve manifest template with UUID:"+uuidTemplate+".\n"+e.getMessage());
            reply.put("returncode", 1); 
            reply.put("errormesg", "Exception is occurred in retrieve manifest template with UUID:"+uuidTemplate+".\n"+e.getMessage());
            return reply.toJSONString();
        }
    }
    /**
     * Retrieves the template stored for the tenant pointed by uuidTemplate.
     * @param uuidTemplate 
     * @return an instance of java.lang.String
     * @author gtricomi
     */
    @GET
    @Path("/{uuidTemplate}/runTime")
    @Produces("application/json")
    public String getRunTime(@PathParam("uuidTemplate") String uuidTemplate,@PathParam("tenant") String tenant){
        JSONObject reply=new JSONObject();
        JSONParser p=new JSONParser();
        try{
            reply.put("templates",p.parse(this.m.getMapInfo(tenant, uuidTemplate)));
            reply.put("returncode", 0); 
            reply.put("errormesg", "None");
            return reply.toString();
        }
        catch(Exception e){
            LOGGER.error("Exception is occurred when OSFFM try to retrieve RunTimeInfo for stacks created from template with UUID:"+uuidTemplate+".\n "+e.getMessage());
            reply.put("returncode", 1); 
            reply.put("errormesg", "Exception is occurred when OSFFM try to retrieve RunTimeInfo for stacks created from template with UUID:"+uuidTemplate+".\n"+e.getMessage());
            return reply.toString();
        }
    }
}