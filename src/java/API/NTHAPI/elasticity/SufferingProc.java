/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package API.NTHAPI.elasticity;

import API.NTHAPI.SitesResource;
import MDBInt.DBMongo;
import MDBInt.Splitter;
import OSFFM_ELA.ElasticityManagerSimple;
import OSFFM_ORC.OrchestrationManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author Giuseppe Tricomi
 */
@Path("/os2os/elasticity")
public class SufferingProc {


    @Context
    private UriInfo context;
    static final Logger LOGGER = Logger.getLogger(SufferingProc.class);
    Splitter s;
    DBMongo m;
     /**
     * Creates a new instance of SitesResource
     */
    public SufferingProc() {
        //LOGGER.error("test");
        //this.init("../webapps/OSFFM/WEB-INF/Configuration_NTHBR_WS.xml");
        
        this.m=new DBMongo();
        this.m.init();
       // this.m.init("../webapps/OSFFM/WEB-INF/Configuration_bit");
        this.m.connectLocale(this.m.getMdbIp());
        this.s=new Splitter(m);
    }
    
   
    @GET
    @Path("/{tenant}/suffproc/{vm_id}")
    @Consumes("application/json")
    @Produces("application/json")
    public String startSuffProc(
            @PathParam("tenant") String tenant,
            @PathParam("vm_id") String vm_id
    ) {
        //.7 richimare il metodo simulateSuffers dell'elasticityManagerSimple per far si che si avvii la procedura di gestione opportuna
                ElasticityManagerSimple ems=new ElasticityManagerSimple();
                String vmName=vm_id;
                 OrchestrationManager om=new OrchestrationManager();
                ems.simulatesuffering(om,vmName,tenant,"userFederation","passwordFederation",m,0,"RegionOne");
//2.8 la funzione sopra restituisce le informazioni sulle vm in modo da far si che possano essere legate dai FA               
            
        JSONObject reply=new JSONObject();
        reply.put("returncode", 0);
        reply.put("errormesg", "");
        return reply.toJSONString();
    }
   
}
