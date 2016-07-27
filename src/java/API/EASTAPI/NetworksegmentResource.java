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

package API.EASTAPI;

import JClouds_Adapter.NeutronTest;
import JClouds_Adapter.OpenstackInfoContainer;
import MDBInt.DBMongo;
import MDBInt.FederatedUser;
import MDBInt.FederationUser;
import OSFFMIDM.SimpleIDM;
import OSFFM_ORC.OrchestrationManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 * REST Web Service
 *
 * @author gtricomi
 */
@Path("/fednet/eastBr/network")
public class NetworksegmentResource {

    @Context
    private UriInfo context;
    private SimpleIDM sidm;
    private DBMongo db;
    static final Logger LOGGER = Logger.getLogger(NetworksegmentResource.class);
    /**
     * Creates a new instance of NetworksegmentResource
     */
    public NetworksegmentResource() {
        sidm=new SimpleIDM();
        this.db=new DBMongo();
    }

    /**
     * Retrieves representation of an instance of EASTAPI.NetworksegmentResource
     * @return an instance of java.lang.String
     * @author gtricomi
     */
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public String add_netSegment(String content) {
        JSONObject reply=new JSONObject();
        JSONParser parser= new JSONParser();
        JSONObject input=null;
        String OSF_network_segment_id=null;
        String OSF_cmp_endpoint=null;
        String OSF_token=null;
        String OSF_tenant=null;
        String OSF_user=null;
        String OSF_region=null;
        String OSF_cloud=null;
        String response="";
        JSONObject network_info=null;
        try 
        {
            //retrieve JSON value from REST request
            input=(JSONObject) parser.parse(content);
            //LOGGER.error("NETWORK::::"+input);
            OSF_user=((String)input.get("username")).split("@")[0];
            OSF_tenant=((String)input.get("username")).split("@")[1];
            OSF_cloud=((String)input.get("username")).split("@")[2];
            HashMap params= new HashMap();
            String dhcpEnable=(String)input.get("dhcpEnable");
            params.put("dhcpEnable", dhcpEnable);
            String shared=(String)input.get("shared");
            params.put("shared", shared);
            String external=(String)input.get("external");
            params.put("external", external);
            String adminStateUp=(String)input.get("adminStateUp");
            params.put("adminStateUp", adminStateUp);
            OSF_token=(String)input.get("token");
            OSF_network_segment_id=(String)input.get("network_segment_id");
            OSF_cmp_endpoint=(String)input.get("cmp_endpoint");
            
            //verrà restituito l'OSFFM endpoint
            //ricavare dal simple IDM gli elementi che mi mancano ovvero:
            //String endpoint, String tenant, String user, String password, String region
            sidm=new SimpleIDM(); //>>>BEACON: VERIFY THIS POINT
            String dbName=sidm.retrieve_TenantDB("federationTenant",OSF_tenant );
            
            sidm.setDbName(dbName);  //>>>BEACON: FOR THE MOMENT OUR TESTING DB IS CALLED beacon
            //sidm.setDbName("beacon");
            
            FederationUser fu=sidm.getFederationU(OSF_token, OSF_cmp_endpoint);//OSF_cmp_endpoint questo non è usato
            
            OrchestrationManager om=new OrchestrationManager();
            response=om.networkSegmentAdd(dbName,fu, OSF_network_segment_id,OSF_cloud, params).toString();
            JSONObject output=(JSONObject) parser.parse(response);
            reply.put("returncode", output.get("returncode"));
            reply.put("errormesg", output.get("errormesg"));
            network_info.put("internalId",(String)((org.json.JSONArray)output.get("ResponseArray")).getJSONObject(0).get("internalId") );
            network_info.put("FedSDN_netSegId",(String)((org.json.JSONArray)output.get("ResponseArray")).getJSONObject(0).get("FedSDN_netSegId") );
            network_info.put("network_address",(String)((org.json.JSONArray)output.get("ResponseArray")).getJSONObject(0).get("network_address") );
            network_info.put("network_mask",(String)((org.json.JSONArray)output.get("ResponseArray")).getJSONObject(0).get("network_mask") );
            network_info.put("size",(String)((org.json.JSONArray)output.get("ResponseArray")).getJSONObject(0).get("size") );
            reply.put("network_info",network_info);
            return reply.toJSONString();
        }
        catch(ParseException pe)
        {
            reply.put("returncode", 1); 
            reply.put("errormesg", "JSON_INPUT_UNPARSABLE: OPERATION ABORTED");
            reply.put("network_info", null);
            return reply.toJSONString();
        }
        catch(Exception e){
            reply.put("returncode", 1); 
            reply.put("errormesg", "Generic Exception occurred! Contact Administrator.");
            reply.put("network_info", null);
            LOGGER.error(e.getMessage());
            return reply.toJSONString();
        }
        //Momentaneamente si lavora con una approccio end to end nei confronti del fedsdn quindi questa parte non và tenuta in considerazione e viene lasciata per futuri sviluppi 
        ////L'APPROCCIO (di gestione di tutte le cloud da parte del fedsdn con un solo comando potrebbe essere corretto). UPDATE: È CORRETTO MA SONO COSTRETTO A RIEMPIRE MANUALMENTE LE INFORMAZIONI SUL FEDSDN
        ////FACENDO SI CHE LE RICHIESTE SIANO DIROTTATE SUL OSSFM(probabilmente l'approccio corretto è quello di aggiungere 
        //////manualmente attraverso chiamate ai REST webservice del fedsdn le informazioni sui vari cloud passando però come cmp_endpoint 
        //////l'indirizzo del OSFFM
        //return reply.toJSONString();
    }

    /**
     * Sub-resource locator method for /eastBr/network/
     */
    /*@Path("/eastBr/network/")
    public NetworkSegmentResource getNetworkSegmentResource() {
        return NetworkSegmentResource.getInstance();
    }*/
}
