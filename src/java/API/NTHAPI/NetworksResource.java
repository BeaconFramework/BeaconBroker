/** Copyright 2016, University of Messina.
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
package API.NTHAPI;

import MDBInt.DBMongo;
import MDBInt.FederationUser;
import OSFFMIDM.SimpleIDM;
import OSFFM_ORC.OrchestrationManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.jdom2.Element;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.ParserXML;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;

/**
 * REST Web Service
 *
 * @author Giuseppe Tricomi
 */
@Path("/fednet/northBr/network")
public class NetworksResource {

    @Context
    private UriInfo context;
    static final Logger LOGGER = Logger.getLogger(NetworksResource.class);
    private ParserXML parserXML;
    private String fedSDNTarget; //it will be used to make request to web service with Client4WS class

    /**
     * Creates a new instance of NetworksResource
     */
    public NetworksResource() {
        //String file = System.getenv("HOME");
        this.init("/home/beacon/beaconConf/Configuration_NTHBR_WS.xml");
    }

    public void init(String file) {

        Element params;
        try {
            parserXML = new ParserXML(new File(file));
            params = parserXML.getRootElement().getChild("pluginParams");
            fedSDNTarget = params.getChildText("fedSDNTarget");
        } //init();
        catch (Exception ex) {
            LOGGER.error("$$$$$$$$$$$$$" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves representation of the all Networks of the Site
     *
     * @param site_id resource URI parameter
     * @param tenant_id resource URI parameter
     * @return an instance of java.lang.String
     */
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public String getSiteNetworks(String content) {
        JSONObject input = null;
        JSONObject reply = new JSONObject();
        JSONParser parser = new JSONParser();
        String tenant = null;
        String region_name = null;
        String token = null;
        String cmp_endpoint = null;
        try {
            input = (JSONObject) parser.parse(content);
            tenant = (String) input.get("tenant");
            region_name = (String) input.get("region_name");
            cmp_endpoint = (String) input.get("cmp_endpoint");
            token = (String) input.get("token");
        } catch (ParseException pe) {
            reply.put("returncode", 1);
            reply.put("errormesg", "INPUT_JSON_UNPARSABLE: OPERATION ABORTED");
            reply.put("network_info", null);
            return reply.toJSONString();
        }

//TODO to return proper representation networking
        JSONArray arr = new JSONArray();
        //while(??){
        JSONObject element = new JSONObject();
        element.put("nid", "Value1");
        element.put("ProjectID", "ProjectID");
        element.put("name", "Value2");
        element.put("CIDRv4", "Value3");
        element.put("EXTERNAL", true);//or false
        element.put("shared", true);//or false
        element.put("AdminState", true);//or false
        element.put("state", true);//or false
        arr.add(element);
        //}

        reply.put("response", arr);
        reply.put("returncode", 0); // or reply.put("returncode", 1);
        reply.put("errormesg", "None"); //or reply.put("errormesg", "Mesg");
        return reply.toJSONString();
    }

    /**
     * Retrieves Information of the fednet_id: each network in sites involved
     * with federation network
     *
     * @param site_id resource URI parameter
     * @param tenant_id resource URI parameter
     * @return an instance of java.lang.String
     */
    @GET
    @Path("/{fednet_id}")
    @Produces("application/json")
    public String getFednetInfo(@PathParam("fednet_id") String fednet_id) {
        //TODO return proper representation networking
        JSONArray arr = new JSONArray();
        //while(per ogni sito della federazione){     //BEACON>>> INSERT OR CORRECT LOGIC
        JSONObject element = new JSONObject();
        element.put("site", "siteId");
        //while( per ogni network di un sito){
        JSONArray internalarr = new JSONArray();
        JSONObject element2 = new JSONObject();
        element2.put("nid", "Value1");
        element2.put("name", "Vnet_name");
        element2.put("CIDRv4", "Value3");
        element.put("siteNetworksArray", internalarr);
        //}
        arr.add(element);
        //}
        JSONObject reply = new JSONObject();
        reply.put("response", arr);
        reply.put("returncode", 0); // or reply.put("returncode", 1);
        reply.put("errormesg", "None"); //or reply.put("errormesg", "Mesg");
        return reply.toJSONString();
    }

    /**
     * DELETE method for resource federation network information. This function
     * have to delete all the information on the NoSQL DB related fednet_id
     *
     * @param fednet_id
     * @return
     */
    @DELETE
    @Path("/{fednet_id}")
    @Produces("application/json")
    public String delete(@PathParam("fednet_id") String fednet_id) {
        JSONObject reply = new JSONObject();
        //something TODO 4 delete Logic
        reply.put("returncode", 0); // or reply.put("returncode", 1);
        reply.put("errormesg", "None"); //or reply.put("errormesg", "Mesg");
        return reply.toJSONString();
    }

    /**
     * Sub-resource locator method for /{site_id}/{tenant_id}
     */
    @Path("/{site_id}/{tenant_id}")
    public NetworkResource getNetworkResource(@PathParam("site_id") String site_id, @PathParam("tenant_id") String tenant_id) {
        return NetworkResource.getInstance(site_id, tenant_id);
    }

    @POST
    @Path("/netsegment")
    @Consumes("application/json")
    @Produces("application/json")
    public String createNetSegment(String content) throws JSONException, IOException, Exception {
        /*String federationTenant,FederationUser fu,String OSF_network_segment_id,String OSF_cloud,HashMap params(nel json)*/
        String response = null;
        JSONObject input = null;
        JSONObject reply = new JSONObject();
        JSONParser parser = new JSONParser();
        String dbName = null;
        String OSF_token = null;
        String OSF_cmp_endpoint = null;
        String OSF_network_segment_id = null;
        String OSF_cloudName = null;
        HashMap params = new HashMap();
        SimpleIDM sidm = new SimpleIDM();

        try {
            org.json.JSONObject jin = new org.json.JSONObject();
            input = (JSONObject) parser.parse(content);
            dbName = (String) input.get("dbName");
            jin = new org.json.JSONObject((String) input.get("fedUser"));
            OSF_token = (String) jin.get("token");
            OSF_cmp_endpoint = (String) jin.get("endpoint");
            OSF_network_segment_id = (String) input.get("netSeg");
            OSF_cloudName = (String) input.get("CloudName");
            params = new ObjectMapper().readValue((String) input.get("hashMapParam"), HashMap.class);

        } catch (ParseException pe) {
            reply.put("returncode", 1);
            reply.put("errormesg", "INPUT_JSON_UNPARSABLE: OPERATION ABORTED");
            reply.put("network_info", null);

            return reply.toJSONString();
        }

        sidm.setDbName(dbName);

        FederationUser fu = sidm.getFederationU(OSF_token, OSF_cmp_endpoint);
        OrchestrationManager om = new OrchestrationManager();
        response = om.networkSegmentAdd(dbName, fu, OSF_network_segment_id, OSF_cloudName, params).toString();
        return null;
    }

    @POST
    @Path("/links")
    @Consumes("application/json")
    @Produces("application/json")
    public String makeNewLink(String content) throws JSONException, IOException, Exception {
        /*String federationTenant,FederationUser fu,String OSF_network_segment_id,String OSF_cloud,HashMap params(nel json)*/
        DBMongo m = new DBMongo();
        String response = null;
        JSONObject input = null;
        JSONObject reply = new JSONObject();
        JSONParser parser = new JSONParser();
        String fedUser = null;
        String fedId = null;
        ArrayList<JSONObject> netTables;
        JSONArray jArr = new JSONArray();
        // String OSF_cmp_endpoint = null;
        // String OSF_network_segment_id = null;
        //String OSF_cloudName= null;
        //HashMap params = new HashMap();
        //SimpleIDM sidm=new SimpleIDM();
        ArrayList<org.json.JSONObject> jSonList = new ArrayList<org.json.JSONObject>();

        try {
            Iterator it = null;
            org.json.JSONObject jtabs = new org.json.JSONObject();
            input = (JSONObject) parser.parse(content);
            fedId = (String) input.get("fedId");
            fedUser = (String) input.get("user");
            jArr = (JSONArray) input.get("network_tables");
            it = jArr.iterator();
            while (it.hasNext()) {
                LOGGER.error("TESTALFONSO1"+(String) it.next());
                jSonList.add(new org.json.JSONObject((String) it.next())); // BEACON>>CODIFICARE QUESTA ASSEGNAZIONE
                LOGGER.error("TESTALFONSO2"+new org.json.JSONObject((String) it.next()));

            }

            //OSF_token=(String) jin.get("token");
            //OSF_cmp_endpoint = (String) jin.get("endpoint");
            //OSF_network_segment_id = (String) input.get("netSeg");
            //OSF_cloudName=(String) input.get("CloudName");
            //params= new ObjectMapper().readValue((String)input.get("hashMapParam"), HashMap.class);
        } catch (ParseException pe) {
            reply.put("returncode", 1);
            reply.put("errormesg", "INPUT_JSON_UNPARSABLE: OPERATION ABORTED");
            reply.put("network_info", null);

            return reply.toJSONString();
        }

        //sidm.setDbName(dbName); 
        //FederationUser fu = sidm.getFederationU(OSF_token, OSF_cmp_endpoint);
        
//        OrchestrationManager om = new OrchestrationManager();
//        response = om.makeLink(Long.getLong(fedId).longValue(), fedUser, jSonList, m).toString();
        //return response;
        return null;
    }
}
