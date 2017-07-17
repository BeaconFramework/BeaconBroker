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
package OSFFM_ORC;

import API.EASTAPI.Clients.Fednet;
import API.EASTAPI.Clients.NetworkSegment;
import API.EASTAPI.Clients.Site;
import API.EASTAPI.Clients.Tenant;
import API.SOUTHBR.FA_ScriptInvoke;
import API.SOUTHBR.FA_client4Network;
import API.SOUTHBR.FA_client4Sites;
import JClouds_Adapter.FunctionResponseContainer;
import JClouds_Adapter.KeystoneTest;
import JClouds_Adapter.NeutronTest;
import JClouds_Adapter.OpenstackInfoContainer;
import MDBInt.DBMongo;
import MDBInt.FederatedUser;
import MDBInt.FederationAgentInfo;
import MDBInt.FederationUser;
import MDBInt.MDBIException;
import OSFFM_ORC.Utils.FANContainer;
import OSFFM_ORC.Utils.FednetsLink;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.domain.Networks;
import org.jclouds.openstack.neutron.v2.domain.Subnet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Exception.WSException;

/**
 * All action that will be done on each federated cloud is Managed inside this
 * Class.
 *
 * @author Giuseppe Tricomi
 */
public class FederationActionManager {

    static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(FederationActionManager.class);

    public FederationActionManager() {
    }

    /**
     * This function is prepared for future usage it has the scope to add a
     * network segment inside Openstack in order to absolve at a FEDSDN calls.
     *
     * @param fu
     * @param OSF_network_segment_id
     * @param OSF_cloud
     * @param netParameter
     * @param federationTenant
     * @return
     * @throws Exception
     */
    public JSONObject networkSegmentAdd(FederationUser fu, String OSF_network_segment_id, String OSF_cloud, HashMap netParameter, String federationTenant) throws Exception {
        DBMongo db = new DBMongo();
        JSONObject reply = new JSONObject();
        db.init();
        db.setDbName(federationTenant);
        db.connectLocale(db.getMdbIp());
        HashMap fum = this.getAllFederatedCloudCreds(fu);
        JSONObject network_info = new JSONObject(), network_infoGLOBAL = new JSONObject();
        JSONArray ja = new JSONArray();

        Set s = fum.keySet();
        OpenstackInfoContainer credential = null;
        NeutronTest neutron = null;
        Iterator i = s.iterator();
        FederatedUser fed_U = null;
        while (i.hasNext()) {
            try {
                //Momentaneamente viene implementata una gestione singola del cloud federato.
                //nel caso in cui si deciderà di gestire globalmente l'insieme dei cloud federati fare come indicato sotto, rimuovendo 
                ////l'if che controlla il cloud
                //////>>>BEACON: inserire gestione con array di thread ogni thread dovrà:
                JSONObject fdu = (JSONObject) fum.get(i.next());
                if (!((String) fdu.get("federatedCloud")).equals(OSF_cloud)) {
                    continue;
                }
                fed_U = new FederatedUser(fdu.toString());
                String federated_cmp_endp = (String) db.getDatacenterFromId(fu.getUser(), (String) fdu.get("federatedCloud")).get("idmEndpoint");
                credential = new OpenstackInfoContainer((String) fdu.get("federatedCloud"), federated_cmp_endp, fu.getUser(), (String) fdu.get("federatedUser"), (String) fdu.get("federatedPassword"), fed_U.getRegion());
                neutron = new NeutronTest(credential.getEndpoint(), credential.getTenant(), credential.getUser(), credential.getPassword(), credential.getRegion());
            } catch (Exception e) {
                reply.put("returncode", 1);
                reply.put("errormesg", "USER_AUTHENTICATION_EXCEPTION: OPERATION ABORTED");
                reply.put("network_info", "null");
                LOGGER.error("USER_AUTHENTICATION_EXCEPTION: OPERATION ABORTED >>>[Token:" + fu.getToken() + "]; No federated credential has found for selected parameters.");
                throw new Exception(reply.toString());
            }

            String cidr = "";
            boolean found = false, cidrPresent = false;
            String internalNetId = db.getInternalNetworkID(fu.getUser(), OSF_network_segment_id, fed_U.getCloud());
            if (internalNetId != null) {
                Networks N = neutron.listNetworks();
                Iterator<Network> iN = N.iterator();
                found = false;
                while (i.hasNext() && !found) {
                    Network n = iN.next();
                    if (internalNetId.equals(n.getId())) {
                        found = true;//nothing to do
                    }
                }
                if (!found) {
//this is the case when some info aren't aligned, the internal ID for netsegment are present but 
//// no network is retrieved for tenant inside the cloud match it.
                    //Inserire qui la chiamata relativa al Network Segment verso l'API "/fednet/{fednetId}/{site_id}/netsegment/{netsegment_id}"
                    ////da cui ritrovare tutte le varie 
                    //in alternativa creare le informazioni qui:
                    HashMap th = new HashMap();
                    th.put("netsegments", OSF_network_segment_id);
                    org.json.JSONObject j = db.getcidrInfoes(fu.getUser(), th);
                    if (j != null) {
//this is the case when a cidr is already defined for this fednet and FederationUser. That would mean that we have to create the 
////other netsegments of the fednet with ther same parameters.                         
                        th = new HashMap();
                        th.put("netsegments", OSF_network_segment_id);
                        th.put("cloudId", fed_U.getCloud());
                        org.json.JSONObject j2 = db.getcidrInfoes(fu.getUser(), th);
                        if (j2 == null) {
//on mongo the netsegments searched is not found for this cloud then we will take the federation cidr
                            cidr = (String) j.get("cidr");
                        } else {
                            //disallineamento è presente un cidr ma non c'è un internalId che corrisponda al netsegments
                            //sarebbe giusto creare un eccezione
                            throw new Exception("A problematic situation is founded! MongoDB information aren't alingned Contact the administrator!");
                        }
                    } else {
//this is the case when on mongo the cidr for netsegments indicated is not found. This case represent first istantiation of the
////netsegment for this fednet and FederationUser.   
                        cidrPresent = true;
                    }
                } else {
                    //match
                    //nothing to do
                }

            } else {
                internalNetId = java.util.UUID.randomUUID().toString();
            }
//prepare and retrieve information for creation of network
            Boolean dhcp = (Boolean) netParameter.get("dhcpEnable");
            if (dhcp == null) {
                dhcp = false;
            }
            Boolean shared = (Boolean) netParameter.get("shared");
            if (shared == null) {
                shared = false;
            }
            Boolean external = (Boolean) netParameter.get("external");
            if (external == null) {
                external = false;
            }
            Boolean adminStateUp = (Boolean) netParameter.get("adminStateUp");
            if (adminStateUp == null) {
                adminStateUp = false;
            }
            String subnetId = java.util.UUID.randomUUID().toString();
            if (!cidrPresent) {
                cidr = this.cidrgen();
            }
            HashMap allpo = this.calculateAllocationPool(cidr);
            SubnetUtils su = new SubnetUtils(cidr);
            SubnetInfo si = su.getInfo();
            String gwadd = si.getLowAddress();
            String fednets = "";//fednet;
            try {
                FunctionResponseContainer frc = neutron.createCompleteNetw(
                        fed_U.getRegion(),
                        internalNetId,
                        cidr,
                        (String) allpo.get("allocationPoolStart"),
                        (String) allpo.get("allocationPoolEnd"),
                        subnetId,
                        gwadd,
                        dhcp,
                        fu.getUser(),
                        shared,
                        external,
                        adminStateUp,
                        fednets
                );

                //da frc. si possono ottenere le informazioni qui presenti 
                /*
                 Network{id=258453f7-b655-4fbd-bee6-d1ac2d9fe7bc, 
                 status=ACTIVE, 
                 subnets=[], 
                 name=ert, 
                 adminStateUp=true, 
                 shared=true, 
                 tenantId=demo, 
                 networkType=null,
                 physicalNetworkName=null,
                 segmentationId=null, 
                 external=true, 
                 portSecurity=null, 
                 profileId=null,
                 multicastIp=null, 
                 segmentAdd=null, 
                 segmentDel=null, 
                 memberSegments=null, 
                 segments=null, 
                 networkFlavor=null}
                 */
            } catch (Exception e) {
                LOGGER.error("Exception occurred in network creation operation!");
            }
            db.storeInternalNetworkID(fu.getUser(), OSF_network_segment_id, fed_U.getCloud(), internalNetId);
            db.insertcidrInfoes(fu.getUser(), cidr, fednets, OSF_network_segment_id, fed_U.getCloud());
            network_info = new JSONObject();
            network_info.put("cloudId", fed_U.getCloud());
            network_info.put("internalId", internalNetId);
            network_info.put("FedSDN_netSegId", OSF_network_segment_id);
            network_info.put("network_address", cidr.subSequence(0, cidr.indexOf("/")));
            network_info.put("network_mask", si.getNetmask());
            network_info.put("size", si.getAddressCount());
            ja.put(network_info);

        }
        network_infoGLOBAL.put("ResponseArray", ja);
        ///DECIDERE COME GESTIRE LE INFORMAZIONI: COSA RESTITUIRE AL FEDSDN 
        ////momentaneamente viene restituito un JSONarray con un solo elemento, quello della cloud indicata da OSF_cloud
        reply.put("returncode", 0);
        reply.put("errormesg", "None");
        reply.put("network_info", network_infoGLOBAL);

        return reply;

    }

    /**
     *
     * @param fu
     * @return
     */
    private HashMap getAllFederatedCloudCreds(FederationUser fu) {
        //costruzione di un Mappa di FederatedUser
        HashMap fum = new HashMap();
        Iterator i = fu.getCredentials().iterator();
        while (i.hasNext()) {
            JSONObject jo = (JSONObject) i.next();
            try {
                fum.put(jo.get("federatedCloud"), jo);
            } catch (JSONException ex) {
                LOGGER.error("Could not foud federatedCloud field in JSON!" + ex.getMessage());
            }
        }
        return fum;
    }

    /**
     * Function used to create cidr for new network, when it is not provided
     * (Used for FEDSDN case).
     *
     * @return
     * @author gtricomi
     */
    private String cidrgen() {
        return "10." + (new Random().nextInt() % 255) + "." + (new Random().nextInt() % 255) + ".0/24";
    }

    /**
     *
     * @param cidr
     * @return
     * @throws Exception
     * @author gtricomi
     */
    private HashMap calculateAllocationPool(String cidr) throws Exception {
        HashMap hm = new HashMap();
        String allocationPoolStart = this.nextIpAddress(cidr);
        //we need the third IP of the allocation pool
        for (int i = 0; i < 2; i++) {
            allocationPoolStart = this.nextIpAddress(allocationPoolStart);
        }

        hm.put("allocationPoolStart", allocationPoolStart);
        SubnetUtils su = new SubnetUtils(cidr);
        SubnetInfo si = su.getInfo();
        hm.put("allocationPoolEnd", si.getHighAddress());
        return hm;
    }

    /**
     * Function to obtain the next IP of the input.
     *
     * @param input
     * @return
     */
    private String nextIpAddress(String input) {
        final String[] tokens = input.split("\\.");
        if (tokens.length != 4) {
            throw new IllegalArgumentException();
        }
        for (int i = tokens.length - 1; i >= 0; i--) {
            final int item = Integer.parseInt(tokens[i]);
            if (item < 255) {
                tokens[i] = String.valueOf(item + 1);
                for (int j = i + 1; j < 4; j++) {
                    tokens[j] = "0";
                }
                break;
            }
        }
        return new StringBuilder()
                .append(tokens[0]).append('.')
                .append(tokens[1]).append('.')
                .append(tokens[2]).append('.')
                .append(tokens[3])
                .toString();
    }

    /**
     * For future usage.
     *
     * @param fednetsLink
     * @param groupName
     */
    public void prepareNetTablesCustom(FednetsLink fednetsLink, String groupName) {
        //1 Non più necessario!

        //2 recupera la lista delle netTable presenti (se presenti) per i Federation Agent
        //3 compone la network table per la funzione di Link
        ////3.1 prende da Mongo la net table correntemente attiva sulla cloud
        ////3.2 analisi della struttura del fednetsLink per sapere quali VM collegare prendendo dalla
        ////collezione su mongoDB i vnid su cui le VM sono connesse nelle rispettive cloud di deploy
        //4 invoca la funzione di Link
        ////4.1 crea la nuova tabella (per ogni sito dovrà creare una tabella differente)
        ////4.2 invoca la funzione createNetTable della classe FA_client4Network
        //5 aggiorna lo stato delle FA netTable in memoria
        ////5.1 salva la nuova netTable incrementando la versione su mongo
        ////5.2 salva la versione aggirnata della cloudlinkstatus su mongo
    }

    /**
     *
     * @param tenantname
     * @param mapContainer
     * @param tmpMapcred ,mappa stack arraylist(di arrayList di OpenStack
     * Container)
     * @param m
     * @author gtricomi
     */
    public void prepareNetTables4completeSharing(
            String tenantname,
            FednetsLink mapContainer,
            HashMap<String, ArrayList<ArrayList<OpenstackInfoContainer>>> tmpMapcred,
            DBMongo m,
            boolean startfromTemplate) {
        HashMap<String, HashMap<String, Object>> al = new HashMap<String, HashMap<String, Object>>();
        FednetsLink fe = this.createCredMapwithoutDuplicate(tmpMapcred, mapContainer);
        Set<String> s = mapContainer.getCloudId_To_OIC().keySet();//getOldnetTablesMap().keySet();-->>verificare intanto modificato
        LinkedHashMap<String, FederationAgentInfo> fa4cloud = new LinkedHashMap<>();
        for (String idcloud : s) {
            try {
                String endpoint = fe.getCloudId_To_OIC().get(idcloud).getEndpoint();
                fa4cloud.put(endpoint, new FederationAgentInfo(m.getFAInfo(tenantname, idcloud)));
            } catch (JSONException e) {
                LOGGER.error("Exception occurred in Parsing JSON retrieved from MongoDB in FAInfo Retrieve for Datacenter " + idcloud + "!" + "\nException message:" + e.getMessage());
            }
        }
        fe.setEndpoint_To_FAInfo(fa4cloud);
        fe = this.prepareKeystoneMap(fe);
        try {
            al = this.prepareTables4link(fe, m);

//02/07/2017 gt: al è una HashMap<String,HashMap<String,Object>>, la più esterna contiene l'associazione sito - Mappa , 
//quella interna contiene negli  Object le tabelle da passare al FA per quel che riguarda Openstack
//a questa mappa dovranno essere aggiunti gli elementi provenienti dal FEDSDN
        } catch (Exception e) {
            LOGGER.error("Exception occurred in the function prepareTables4link");
        }
//04/07/2017 gt: la seguente funzione salva all'interno di MongoDB le tables ottenute che verranno usate durante la fase di instaurazione del link
        this.saveTablesOnMongo(al, tenantname, m);
        //if(startfromTemplate)
        this.updatestateOnFEDSDN(tenantname,al, fe, m);
    }

    private void saveTablesOnMongo(HashMap<String, HashMap<String, Object>> resultingTables, String tenant, DBMongo m) {
        Set<String> s = resultingTables.keySet();
        JSONObject tenantTab = null;
        JSONArray siteTab = null;
        JSONObject netTab = null;
        JSONObject bnaSegTab = null;
       
        for (String idCloud : s) { //reference site

            HashMap<String, Object> innerMap = (HashMap<String, Object>) resultingTables.get(idCloud);
            try {
                tenantTab = new JSONObject((String) innerMap.get("tenantTable"));
                siteTab = new JSONArray((String) innerMap.get("siteTable"));

                tenantTab.append("version", (Integer) netTab.get("version"));
                
                netTab = new JSONObject((String) innerMap.get("netTable"));
                bnaSegTab = bnaNetSegCreate(netTab, m, idCloud, tenant);
                
                
                
                m.insertTenantTables(tenant, idCloud, tenantTab.toString(0));
                m.insertSiteTables(tenant, idCloud, "{" + siteTab.toString(0) + "}");
                // m.insertNetTable(tenant, idCloud,netTab.toString(0));
            } catch (JSONException ex) {
                LOGGER.error("Exception occurred in the function saveTablesOnMongo, it is not possible cast tables calculated for BNA from String to JSON.\nException Message:" + ex.getMessage());
            }

        }
    }

    private JSONObject bnaNetSegCreate(JSONObject tables, DBMongo m, String refSite, String tenant) {

        JSONObject bnaSegTab = new JSONObject();
        JSONArray segRow = null;
        JSONObject subJSON = null;
        String version = "";
        UUID uuid = null;
        String fedNet = "";
        //boolean resultIns = false;

        try {
            fedNet = tables.getString("name");
            version = tables.getString("version");
            segRow = (JSONArray) tables.get("table");
            //uuid=UUID.randomUUID();

            for (int i = 0; i < segRow.length(); i++) {

                uuid = UUID.randomUUID();
                JSONArray innerArray = (JSONArray) segRow.get(i);
                for (int j = 0; j < innerArray.length(); j++) {
                    JSONObject objectJson = (JSONObject) innerArray.get(j);
                    bnaSegTab.put("FK", uuid.toString());
                    // bnaSegTab.put("fedNet",objectJson.get("name"));
                    bnaSegTab.put("netEntry", objectJson);
                    m.insertNetTables(tenant, bnaSegTab.toString(0));

                }
                m.insertTablesData(uuid.toString(), tenant, version, refSite, fedNet);
            }

        } catch (JSONException ex) {
            LOGGER.error("-___-' Error: " + ex.getMessage());
        } catch (MDBIException ex) {
            LOGGER.error("Error : "+ex.getMessage());
        }

        return bnaSegTab;
}

    //<editor-fold defaultstate="collapsed" desc="FedSDN Interaction and Management Functions">
    /**
     * This function store elements useful for federated Network creation by the BNM.
     * It will use EASTAPI clients to contact BNM.
     * @param tenant
     * @param mapContainer
     * @param m
     * @author gtricomi
     */
    public void updatestateOnFEDSDN(String tenant,HashMap<String, HashMap<String, Object>> resultingTables, FednetsLink mapContainer, DBMongo m) {
        String fedsdnURL = m.getInfo_Endpoint("entity", "fedsdn");//"http://10.9.0.14:6121";
        String fedsdnpassword = m.getFederationCredential(tenant, tenant, "federationUser");
        Site sClient = new Site(tenant, fedsdnpassword);//Modificare le info dentro il fedsdn che al momento sono inserite sotto l'utente root e password fedsdn
        NetworkSegment nClient = new NetworkSegment(tenant, fedsdnpassword);
        Fednet fClient = new Fednet(tenant, fedsdnpassword);

        try {
            if(!this.checkSiteandInsertFEDSDN(mapContainer, sClient, fedsdnURL,m)){
                LOGGER.error("It was not possible to insert the site inside the BNM! \n");
            }
        } catch (WSException ex) {
            LOGGER.error("Exception is occurred in checkSiteFEDSDN! \n" + ex);
        } catch (JSONException ex) {
            LOGGER.error("Exception is occurred in checkSiteFEDSDN! \n" + ex);
        }
      
        try {
            this.checkTenantandInsertFEDSDN(mapContainer, sClient, fedsdnURL,m);
        } catch (WSException ex) {
            LOGGER.error("Exception is occurred in checkTenantFEDSDN! \n" + ex);
        } catch (JSONException ex) {
            LOGGER.error("Exception is occurred in checkTenantFEDSDN! \n" + ex);
        }
//17/07/2017 gt: continuare da qui
        try {
            this.checkFednetandInsertFEDSDN(mapContainer,sClient, nClient, fedsdnURL,tenant, m);
        } catch (WSException ex) {
            LOGGER.error("Exception is occurred in checkNetSegmentFEDSDN! \n" + ex);
        } catch (JSONException ex) {
            LOGGER.error("Exception is occurred in checkNetSegmentFEDSDN! \n" + ex);
        }
        try {
            this.checkNetSegmentandInsertFEDSDN(mapContainer,sClient, nClient, fedsdnURL,tenant, m);
        } catch (WSException ex) {
            LOGGER.error("Exception is occurred in checkNetSegmentFEDSDN! \n" + ex);
        } catch (JSONException ex) {
            LOGGER.error("Exception is occurred in checkNetSegmentFEDSDN! \n" + ex);
        }        
//14/07/2017 gt: valutare possibili modifiche
        try {
            this.makeLinkOnFednet(fClient, tenant, fedsdnURL, m);
        } catch (WSException ex) {
            LOGGER.error("Exception is occurred in makeLinkOnFednet! \n" + ex);
        } catch (JSONException ex) {
            LOGGER.error("Exception is occurred in makeLinkOnFednet! \n" + ex);
        }
    }

    private void checkFednetandInsertFEDSDN(FednetsLink  mapContainer,Site sClient,NetworkSegment nClient,String fedsdnURL,String federationTenant, DBMongo m)throws WSException, JSONException{
        
    }
    
    /**
     *
     * @param mapContainer
     * @param sClient
     * @param nClient
     * @param fedsdnURL
     * @param federationTenant
     * @param m
     * @throws WSException
     * @throws JSONException
     * @author gtricomi
     */
    private void checkNetSegmentandInsertFEDSDN(FednetsLink  mapContainer,Site sClient,NetworkSegment nClient,String fedsdnURL,String federationTenant, DBMongo m)throws WSException, JSONException{
        Response r=sClient.getAllSite(fedsdnURL);
        JSONArray ja=new JSONArray(r.readEntity(String.class));
        FederationAgentInfo fa=null;
        for(int i = 0; i < ja.length(); i++) {
            JSONObject jo = (JSONObject) ja.get(i);
            String siteNameToCheck = (String) jo.get("name");
            if (siteNameToCheck != null) {
                fa = mapContainer.getEndpoint_To_FAInfo().get(mapContainer.getCloudId_To_OIC().get(siteNameToCheck));
                NeutronTest neutron = new NeutronTest(
                        mapContainer.getCloudId_To_OIC().get(siteNameToCheck).getEndpoint(),
                        mapContainer.getCloudId_To_OIC().get(siteNameToCheck).getTenant(),
                        mapContainer.getCloudId_To_OIC().get(siteNameToCheck).getUser(),
                        mapContainer.getCloudId_To_OIC().get(siteNameToCheck).getPassword(),
                        mapContainer.getCloudId_To_OIC().get(siteNameToCheck).getRegion()
                );
                Networks ns = neutron.listNetworks();
                Iterator<Network> itNet = ns.iterator();
                while (itNet.hasNext()) {
                    Network n = itNet.next();
                    UnmodifiableIterator<String> ti = n.getSubnets().iterator();
                    while (ti.hasNext()) {
                        Subnet s = neutron.getSubnet((String) ti.next());
                        if ((s != null) && (s.getIpVersion() == 4)) {//We want manage only ipv4 network
                            SubnetInfo si = new SubnetUtils(s.getCidr()).getInfo();
                            boolean ok = false;
                            int siteid = m.getfedsdnSiteID(siteNameToCheck);
                            int federationTenantID = m.getfedsdnFednetID(federationTenant);
                            for (int k = 0; k < 3; k++) {

                                ok = this.addNetSegOnFedSDN(
                                        s.getName(),
                                        m.getInfo_Endpoint("entity", "osffm") + "/fednet/eastBr/network",//sostituire con il FA del sito??
                                        si.getAddress(),
                                        si.getNetmask(),
                                        si.getAddressCount(),
                                        nClient,
                                        fedsdnURL,
                                        federationTenantID,
                                        siteid,
                                        m,
                                        federationTenant
                                );
                                if (ok) {
                                    break;
                                } else if (k == 3) {
                                    LOGGER.error("Something going wrong! It's Impossible add Networksegment on FEDSDN");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param name
     * @param endpoint_OSFFM
     * @param network_address
     * @param network_mask
     * @param size
     * @param nClient
     * @param fedsdnURL
     * @param fedTenantIDFEDSDN
     * @param siteIdFEDSDN
     * @param m
     * @return
     * @throws JSONException
     * @throws WSException
     * @author gtricomi
     */
    private boolean addNetSegOnFedSDN(
            String name,
            String endpoint_OSFFM,
            String network_address,
            String network_mask,
            int size,
            NetworkSegment nClient,
            String fedsdnURL,
            long fedTenantIDFEDSDN,
            int siteIdFEDSDN,
            DBMongo m,
            String tenant
    ) throws JSONException, WSException {
        JSONObject jo = new JSONObject();
        jo.put("name", name);
        jo.put("fa_endpoint", endpoint_OSFFM);
        jo.put("network_address", network_address);
        jo.put("network_mask", network_mask);
        jo.put("size", size);

        try {
            Response r = nClient.createNetSeg(jo, fedsdnURL, fedTenantIDFEDSDN, siteIdFEDSDN);
            m.insertfedsdnNetSeg(r.readEntity(String.class), tenant);//Bsogna verificare realmente cosa viene restituito

        } catch (WSException ex) {
            LOGGER.error("Exception is occurred in addSiteOnFedSDN for NetSegment: " + name + " on site with ID:" + siteIdFEDSDN + " for the tenant: " + fedTenantIDFEDSDN + "\n" + ex);
            return false;
        }
        return true;
    }

    /**
     * This function verify if the site is present and if it isn't it adds it.
     * @param mapContainer
     * @param sClient
     * @param fedsdnURL
     * @return
     * @throws WSException
     * @throws JSONException
     * @author gtricomi
     */
    private boolean checkSiteandInsertFEDSDN(FednetsLink  mapContainer,Site sClient,String fedsdnURL, DBMongo m) throws WSException, JSONException{
        Response r=sClient.getAllSite(fedsdnURL);
        JSONArray ja=new JSONArray(r.readEntity(String.class));
        LinkedHashMap<String,OpenstackInfoContainer> CloudId_To_OIC=mapContainer.getCloudId_To_OIC();
        for(int i=0;i<ja.length();i++){
            JSONObject jo=(JSONObject)ja.get(i);
            String siteNameToCheck=(String)jo.get("name");
            if(!(CloudId_To_OIC.containsKey(siteNameToCheck))){
                boolean ok=false;
                for(int k=0;k<3;k++){
                
                    ok=this.addSiteOnFedSDN(siteNameToCheck,sClient,CloudId_To_OIC.get(siteNameToCheck).getEndpoint(),m, CloudId_To_OIC.get(siteNameToCheck).getTenant());
                    if (ok){
                        break;
                    }
                    else if(k==3){
                        LOGGER.error("Something going wrong! It's Impossible add site on FEDSDN"); 
                        return false; //03/07/2017: inserito per bloccare il flusso nel caso in cui qualche sito non venga inserito !!!
                    }
                }
            }
        }
        return true;
    }

    /**
     *
     * @param mapContainer
     * @param sClient
     * @param fedsdnURL
     * @return
     * @throws WSException
     * @throws JSONException
     * @author gtricomi
     */
    private boolean checkTenantandInsertFEDSDN(FednetsLink  mapContainer,Site sClient,String fedsdnURL, DBMongo m) throws WSException, JSONException{
        Response r=sClient.getAllSite(fedsdnURL);
        JSONArray ja=new JSONArray(r.readEntity(String.class));
        LinkedHashMap<String,OpenstackInfoContainer> CloudId_To_OIC=mapContainer.getCloudId_To_OIC();
        
        JSONArray inner = new JSONArray();
        String tenant = null;
        String tenant_password = null;

        for (int i = 0; i < ja.length(); i++) {
            JSONObject jo = (JSONObject) ja.get(i);

            String siteIdToCheck = (String) jo.get("id");
            String siteNameToCheck = (String) jo.get("name");

            if (!(CloudId_To_OIC.containsKey(siteNameToCheck))) {

                OpenstackInfoContainer oik = (OpenstackInfoContainer) CloudId_To_OIC.get(siteNameToCheck);

                String siteUsernameToCheck = oik.getUser();
                String sitePasswordToCheck = oik.getPassword();

                String credentials = siteUsernameToCheck + ":" + sitePasswordToCheck;

                //03/07/2017: statically insert 0, but need to be checked !!!
                String user_id_insite = "0";

                JSONObject inner_jo = new JSONObject("{\"site_id\" :\"" + siteIdToCheck + "\",\"user_id_in_site\":\"" + user_id_insite + "\" ,\"credentials\":\"" + credentials + "\"}");
                inner.put(inner_jo);

                tenant = oik.getTenant();

                //03/07/2017: federation password for the tenant is set equal to the openstack site !!!
                tenant_password = sitePasswordToCheck;
            }
        }

        //03/07/2017: verify username management like structure "NotManagedUser@userFederation@UME" !!!
        JSONObject tenant_jo = new JSONObject("{\"name\" :\"" + tenant + "\",\"password\":\"" + tenant_password + "\" ,\"type\":\"user\",\"valid_sites\": \"" + inner + "\"}");
        m.insertTenantTables(tenant, tenant_jo.toString());

        //03-07-2017 : hardcoded credential for tenant
        Tenant t = new Tenant("root", "fedsdn");
        boolean ok = false;
        for (int k = 0; k < inner.length(); k++) {
            try {
                r = t.createTen(tenant_jo, fedsdnURL);
                JSONObject resp = new JSONObject(r.readEntity(String.class));
                JSONObject entry = new JSONObject();
                entry.put("tenantID", (String) resp.remove("id"));
                entry.put("tenantEntry", resp);
                m.insertfedsdnSite(entry.toString(0), tenant);
            } catch (WSException ex) {
                LOGGER.error("Exception is occurred in checkTenantFEDSDN for tenant: " + tenant + "\n" + ex);
                ok = false;
            }
            ok = true;
            if (ok) {
                break;
            } else if (k == 3) {
                LOGGER.error("Something going wrong! It's Impossible add site on FEDSDN");
                return false; //03/07/2017: inserito per bloccare il flusso nel caso in cui qualche sito non venga inserito !!!
            }
        }

        return true;
    }

    /**
     *
     * @param siteName
     * @param sClient
     * @param fedsdnURL
     * @return
     * @author gtricomi
     */
    private boolean addSiteOnFedSDN(String siteName,Site sClient,String cmp_endpoint,DBMongo m, String tenant)throws JSONException{
        String type = "";
        try {
            if (m.getDatacenter(tenant, siteName) != null) {
                type = "openstack";
            } else {
                type = "opennebula";
            }
        } catch (MDBIException mbe) {
            type = "opennebula";
        }
        try {
            String fedsdnURL = m.getInfo_Endpoint("entity", "fedsdn");
            Response r = sClient.createSite(siteName, cmp_endpoint, type, fedsdnURL);
            JSONObject resp=new JSONObject(r.readEntity(String.class));
            JSONObject entry=new JSONObject();
            entry.put("siteID", (String)resp.remove("id"));
            entry.put("siteEntry",resp);
            m.insertfedsdnSite(entry.toString(0), tenant); 
        } catch (WSException ex) {
            LOGGER.error("Exception is occurred in addSiteOnFedSDN for site: " + siteName + "\n" + ex);
            return false;
        }
        return true;
    }

    private boolean makeLinkOnFednet(Fednet fClient, String federtenant, String fedsdnURL, DBMongo m) throws WSException, JSONException {//TBD
        //Sembrerebbe necessario dover richiamare il client fednet (con l'opportuno riferimento al fednet corretto PUT /fednet/fedentID con parametri (action=link, linktype=full_mesh)
        JSONObject jo = new JSONObject(m.getfedsdnFednet(federtenant));
        try {

            long l = ((Double) jo.get("id")).longValue();

            Response r = fClient.updateFednet(l, jo.getString("name"), jo.getString("linkType"), jo.getString("type"), fedsdnURL, "link");
            //SIstemare questo pezzo, se non ci sono eccezioni deve essere salvato quello restituito da una chiamata f.getNetinfo(fedsdnURL,fednetname )
            r = fClient.getNetinfo(fedsdnURL, jo.getString("name"));
            JSONObject tmp = new JSONObject(r.readEntity(String.class));
            tmp.append("federationTenantName", federtenant);
            //m.insertfedsdnFednet(tmp.toString());
            m.insertfedsdnFednet(tmp.toString(), federtenant);
        } catch (WSException ex) {
            LOGGER.error("Exception is occurred in makeLinkOnFednet for fedenetID: " + ((Double) jo.get("id")).toString() + " owned from federation tenant: " + federtenant + "\n" + ex);
            return false;
        }
        return true;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Openstack Federation Agent Tables Management & Elaboration Functions">  
    /**
     *
     * @param tmpMapcred
     * @return
     */
    private FednetsLink createCredMapwithoutDuplicate(HashMap<String, ArrayList<ArrayList<OpenstackInfoContainer>>> tmpMapcred, FednetsLink fe) {
        LinkedHashMap<String, OpenstackInfoContainer> cleanMap = new LinkedHashMap<>();
        Set<String> s = tmpMapcred.keySet();
        for (String st : s) {
            ArrayList<ArrayList<OpenstackInfoContainer>> tmpar = tmpMapcred.get(st);
            for (ArrayList<OpenstackInfoContainer> at : tmpar) {
                for (OpenstackInfoContainer oic : at) {
                    if (!cleanMap.containsKey(oic.getIdCloud())) {
                        cleanMap.put(oic.getIdCloud(), oic);
                        fe.addDcInFednet(oic.getIdCloud());
                    }
                }
            }
        }

        fe.setCloudId_To_OIC(cleanMap);
        return fe;
    }

    /* private FednetsLink prepareFAMAp(FednetsLink fednetContainer){
        Set<String> s=fednetContainer.getCloudId_To_OIC().keySet();
        LinkedHashMap<String,String> endpoint_to_FAInfo= new LinkedHashMap<>();
        
        for(String idcloud: s){
            this.(idcloud);
        }
    }*/
    /**
     * Create LinkedHashMap with KeystoneManager for each site.
     *
     * @param cleanMap
     * @return
     * @author gtricomi
     */
    private FednetsLink prepareKeystoneMap(FednetsLink fednetContainer) {
        Set<String> s = fednetContainer.getCloudId_To_OIC().keySet();
        LinkedHashMap<String, KeystoneTest> tmp = new LinkedHashMap<>();
        LinkedHashMap<String, String> endpoint_to_tenantid = new LinkedHashMap<>();

        for (String idcloud : s) {
            OpenstackInfoContainer oictmp = fednetContainer.getCloudId_To_OIC().get(idcloud);
            KeystoneTest kt = new KeystoneTest(oictmp.getTenant(), oictmp.getUser(), oictmp.getPassword(), oictmp.getEndpoint());
            tmp.put(idcloud, kt);
            //endpoint_to_tenantid.put(oictmp.getEndpoint(),kt.getTenantId(oictmp.getTenant()));
            //PEZZO INTRODOTTO DA ELIMINARE
            if (idcloud.equals("UME")) {
                endpoint_to_tenantid.put(fednetContainer.getCloudId_To_OIC().get("UME").getEndpoint(), "3029a98f60c24ac1b4ef4636c4ee3006");
            } else if (idcloud.equals("CETIC")) {
                endpoint_to_tenantid.put(fednetContainer.getCloudId_To_OIC().get("CETIC").getEndpoint(), "3029a98f60c24ac1b4ef4636c4ee3006");
            }
            //FINE PEZZO     
        }
        fednetContainer.setkMcloudId_To_Keystone(tmp);
        fednetContainer.setEndpoint_to_tenantid(endpoint_to_tenantid);
        return fednetContainer;
    }

    /**
     *
     * @param fednetContainer
     * @param m
     * @throws JSONException
     * @author gtricomi
     */
    private HashMap prepareTables4link(FednetsLink fednetContainer, DBMongo m) throws JSONException {//aggiungere gestione delle tabelle dei siti
        Collection<KeystoneTest> kar = null;
        try {
            kar = (Collection<KeystoneTest>) fednetContainer.getkMcloudId_To_Keystone().values();//inutile?

        } catch (Exception e) {
            LOGGER.error("Something is going wrong. " + e.getMessage());
        }
        ArrayList<String> Site = new ArrayList<>();

        HashMap resultingTable = new HashMap();

        Set<String> s = fednetContainer.getCloudId_To_OIC().keySet();
        JSONArray siteUpdatedTable = null;
        JSONArray netUpdatedTable = null;
        LinkedHashMap<String, Object> mapsitenetbody = new LinkedHashMap<String, Object>();
        for (String cloudID : s) {//scorre il set per selezionare la home
            resultingTable.put(cloudID, new HashMap());
            /**
             * ***********************************
             * Pre ogni sito servono tre tabelle da mandare ai FA di OpenStack
             * TENANT: {'name': u'admin', 'id':
             * u'aa146d1022fe4dd1a29042c2f234d84b'} SITO: [ { 'name' :
             * site2_name, 'tenant_id' : u'aa146d1022fe4dd1a29042c2f234d84b',
             * 'fa_url' : 10.9.1.169, 'site_proxy' : [{'ip' : 10.9.1.169, 'port'
             * : 4897}] }, { 'name' : site1_name, 'tenant_id' :
             * u'aa146d1022fe4dd1a29042c2f234d847', 'fa_url' : 10.9.1.159,
             * 'site_proxy' : [{'ip' : 10.9.1.159, 'port' : 4897}] }, ]
             * NETTABLES: { 'table' : [ {'tenant_id':
             * u'aa146d1022fe4dd1a29042c2f234d84b','site_name': 'site2', 'name':
             * u'private', 'vnid': u'7fdb464c-11db-4b7f-9f60-4382ed9a76e8'},
             * {'tenant_id': u'aa146d1022fe4dd1a29042c2f234d847', 'site_name':
             * 'site1', 'name': u'private', 'vnid':
             * u'b906abbd-ed90-4cd0-bb3a-bd7c9119dfb9'} ], 'version' : 115 }
             * *************************************
             */
            String ten = fednetContainer.getCloudId_To_OIC().get(cloudID).getTenant();

            NeutronTest neutronhome = new NeutronTest(
                    fednetContainer.getCloudId_To_OIC().get(cloudID).getEndpoint(),
                    fednetContainer.getCloudId_To_OIC().get(cloudID).getTenant(),
                    fednetContainer.getCloudId_To_OIC().get(cloudID).getUser(),
                    fednetContainer.getCloudId_To_OIC().get(cloudID).getPassword(),
                    fednetContainer.getCloudId_To_OIC().get(cloudID).getRegion()
            );
            JSONObject updatedSiteTable = fednetContainer.getOldsiteTablesMap().get(cloudID);
            JSONObject updatedNetTable = fednetContainer.getOldnetTablesMap().get(cloudID);
            ////Aggringere dei controlli per verificare se l'ogg restituito è null, in tal caso non è presente una vecchia tabella per il sito in questione
            try {
//02/07/2017 gt: verificare questo controllo non sono sicuro che il valore sia null nel caso sia vuoto                
                if (updatedSiteTable == null) {
                    siteUpdatedTable = new JSONArray();
                } else {
                    siteUpdatedTable = updatedSiteTable.getJSONArray("table");
                }
            } catch (Exception eSite) {
                LOGGER.error("Something is going wrong. " + eSite.getMessage());
            }
            try {
//02/07/2017 gt: verificare questo controllo non sono sicuro che il valore sia null nel caso sia vuoto                 
                if (updatedNetTable == null) {
                    netUpdatedTable = new JSONArray();
                } else {
                    netUpdatedTable = updatedNetTable.getJSONArray("table");
                }
            } catch (Exception eNet) {
                LOGGER.error("Something is going wrong. " + eNet.getMessage());
            }
            String tmp = fednetContainer.getCloudId_To_OIC().get(cloudID).getEndpoint();
            FederationAgentInfo fah = fednetContainer.getEndpoint_To_FAInfo().get(tmp);
            String tenantID = fednetContainer.getEndpoint_to_tenantid().get(fednetContainer.getCloudId_To_OIC().get(cloudID).getEndpoint());
            String siteEntryhome = this.createSiteTableEntry(
                    cloudID,
                    tenantID,
                    fah.getSite_proxyip(),
                    fah.getIp() + ":" + fah.getPort(),
                    fah.getSite_proxyport()
            );
            JSONObject sEh = new JSONObject(siteEntryhome);
            siteUpdatedTable = this.insertEntry_in_SiteTable(siteUpdatedTable, sEh);
            System.out.println("$$$$$s1");
            for (String cloudID2 : s) {//scorre il set per la creazione tabella 
                if (!cloudID2.equals(cloudID)) {
                    NeutronTest neutron = new NeutronTest(
                            fednetContainer.getCloudId_To_OIC().get(cloudID2).getEndpoint(),
                            fednetContainer.getCloudId_To_OIC().get(cloudID2).getTenant(),
                            fednetContainer.getCloudId_To_OIC().get(cloudID2).getUser(),
                            fednetContainer.getCloudId_To_OIC().get(cloudID2).getPassword(),
                            fednetContainer.getCloudId_To_OIC().get(cloudID2).getRegion()
                    );
                    Networks ns = neutron.listNetworks();
                    Iterator<Network> itNet = ns.iterator();
                    while (itNet.hasNext()) {//per ogni network del sito 
                        Network n = itNet.next();
                        Network nHome = neutronhome.getNetwork(n.getName());
                        if (nHome == null) {
                            LOGGER.error("Something is going wrong in preparation JSONObject for cloud " + cloudID + ". It's impossible found the Network Named " + n.getName());
                            continue;
                        }
                        String tenIdForeign = n.getTenantId();
                        String entryCreated = this.createNetTableEntry(cloudID2, tenIdForeign, n.getName(), n.getId());
                        String entryHome = null;
                        System.out.println("$$$$$s1A");
                        if (nHome != null) {
                            //entryHome=this.createNetTableEntry(cloudID,nHome.getTenantId(), nHome.getName(), nHome.getId());
                            String tmp3 = "{";
                            tmp3 = tmp3 + ("\"tenant_id\": \"" + nHome.getTenantId() + "\", ");
                            tmp3 = tmp3 + ("\"site_name\": \"" + cloudID + "\", ");
                            tmp3 = tmp3 + ("\"name\": \"" + nHome.getName() + "\", ");
                            tmp3 = tmp3 + ("\"vnid\": \"" + nHome.getId() + "\"");
                            tmp3 = tmp3 + "}";
                            entryHome = tmp3;
                        }
                        try {
                            JSONObject j = new JSONObject(entryCreated);
                            //JSONArray ja=netUpdatedTable;
                            netUpdatedTable = this.insertEntry_in_NetTable(netUpdatedTable, j, new JSONObject(entryHome));
                            //JSONArray ja=netUpdatedTable;
                            System.out.println("$$$$$s1B");
                            String tmp2 = fednetContainer.getCloudId_To_OIC().get(cloudID2).getEndpoint();
                            FederationAgentInfo fa = fednetContainer.getEndpoint_To_FAInfo().get(tmp2);
                            String siteEntry = this.createSiteTableEntry(cloudID2, tenIdForeign, fa.getSite_proxyip(), fa.getIp() + ":" + fa.getPort(), fa.getSite_proxyport());
                            siteUpdatedTable = this.insertEntry_in_SiteTable(siteUpdatedTable, new JSONObject(siteEntry));//rimuovere home entry da qui e mettere all'inizio?
                        } catch (JSONException je) {

                        }
                    }
                }
            }
            System.out.println("$$$$$s2");
            KeystoneTest homeKey = (KeystoneTest) fednetContainer.getkMcloudId_To_Keystone().get(cloudID);
            FA_ScriptInvoke fi = new FA_ScriptInvoke(homeKey.getVarEndpoint(), fednetContainer.getEndpoint_to_tenantid().get(homeKey.getVarEndpoint()), "admin", "0penstack");

//02/07/2017 gt: questo comanda richiama lo script che fà lo share completo tra i testbed OpenStack modificato con una funzione che invochi le funzioni del FA;
//ad ogni modo tutta la logica di interazione con il FA è stata spostata al di fuori di questa funzione quindi qst pezzo viene commentato
/*            try {
                fi.FAScript(fah.getIp() + ":5051");

            } catch (WSException wse) {
                //something here

            }
             */
            FA_client4Sites fas = new FA_client4Sites(homeKey.getVarEndpoint(), fednetContainer.getEndpoint_to_tenantid().get(homeKey.getVarEndpoint()), "admin", "0penstack");
            FA_client4Network fan = new FA_client4Network(
                    homeKey.getVarEndpoint(),
                    fednetContainer.getEndpoint_to_tenantid().get(homeKey.getVarEndpoint()),
                    homeKey.getVarIdentity().split(":")[1],
                    homeKey.getVarCredential()
            );
            String body = fas.constructSiteTableJSON(siteUpdatedTable);

            ((HashMap) resultingTable.get(cloudID)).put("tenantTable", "{ \"name\": \"" + ten + "\",\"tenant_id\": \"" + tenantID + "\" }");
            ((HashMap) resultingTable.get(cloudID)).put("siteTable", body);

//FederationAgentInfo fai=fednetContainer.getEndpoint_To_FAInfo(siteUpdatedTable).get(fednetContainer.getEndpoint_to_tenantid().get(homeKey.getVarEndpoint()));
//02/07/2017 GT:
/*PARTE COMMENTATA IN QUANTO LA FUNZIONALITA' DOVRA' ESSERE SPOSTATA ALTROVE, QUESTA FUNZIONE DEVE PREPARARE LE TABELLE PER LA PARTE OPENSTACK
             try{
             fas.createSiteTable(fednetContainer.getEndpoint_to_tenantid().get(homeKey.getVarEndpoint()), fah.getIp()+":"+fah.getPort(), body);
             m.insertSiteTables(homeKey.getVarIdentity().split(":")[0], cloudID, updatedSiteTable.put("table", siteUpdatedTable).toString());
                    
                    
             }
             catch(WSException wse){
             //something here
                    
             }
*/
            if (updatedNetTable == null) {
                body = fan.constructNetworkTableJSON(netUpdatedTable, 114);//dovrebbe essere 1 è diventato 114 per testing
            } else {
//12/07/2017 gt: aggiungere funzione che crea la tabella tenendo conto della tabella preesistente               
                body = fan.constructNetworkTableJSON(netUpdatedTable, (updatedNetTable.getInt("version")) + 1);
            }
            mapsitenetbody.put(cloudID, new FANContainer(fan, body, fah.getIp(), fah.getPort(), fednetContainer.getEndpoint_to_tenantid().get(homeKey.getVarEndpoint())));

            ((HashMap)resultingTable.get(cloudID)).put("netTable", body);

//02/07/2017 GT:
/*PARTE COMMENTATA IN QUANTO LA FUNZIONALITA' DOVRA' ESSERE SPOSTATA ALTROVE, QUESTA FUNZIONE DEVE PREPARARE LE TABELLE PER LA PARTE OPENSTACK
             try{
             Response r=fan.createNetTable(fednetContainer.getEndpoint_to_tenantid().get(homeKey.getVarEndpoint()), fah.getIp()+":"+fah.getPort(), body);
             //JSONObject answer=r.readEntity(JSONObject.class);
             //m.insertNetTables(homeKey.getVarIdentity().split(":")[0], cloudID, updatedNetTable.put("table", netUpdatedTable).toString(),(double)answer.get("version"));//rivedere entrambe
                    
             }
             catch(WSException wse){
             //something here
                    
             }
             */
            //Inserimento mappe dentro FednetContainer
            //BEACON>>>Add Management of structure to update fedsdn information
        }
//02/07/2017 GT:
/*PARTE COMMENTATA IN QUANTO LA FUNZIONALITA' DOVRA' ESSERE SPOSTATA ALTROVE, QUESTA FUNZIONE DEVE PREPARARE LE TABELLE PER LA PARTE OPENSTACK
         for(String cloudID: s) {
         try{
            
         FANContainer fanc= (FANContainer)mapsitenetbody.get(cloudID);
         FA_client4Network fan=(FA_client4Network)fanc.getFan();
         Response r=fan.createNetTable(fanc.getTenantid(), fanc.getFahIP()+":"+fanc.getFahPORT(), fanc.getBody());
         //JSONObject answer=r.readEntity(JSONObject.class);
         //m.insertNetTables(homeKey.getVarIdentity().split(":")[0], cloudID, updatedNetTable.put("table", netUpdatedTable).toString(),(double)answer.get("version"));//rivedere entrambe
                    
         }
         catch(WSException wse){
                    //something here
                    
                }
        }
         */
        return resultingTable;
    }

    /**
     *
     * @param ja
     * @param entry
     * @param homeEntry
     * @return
     * @throws JSONException
     * @author gtricomi
     */
    private JSONArray insertEntry_in_NetTable(JSONArray ja, JSONObject entry, JSONObject homeEntry) throws JSONException {
        try {
            boolean foundElem = false;
            boolean foundArray = false;
            if (ja == null) {
                ja = new JSONArray();
            }
            for (int i = 0; i < ja.length(); i++) {
                JSONArray int_ja = ja.getJSONArray(i);
                for (int j = 0; j < ja.length(); j++) {
                    JSONObject tmpjo = (JSONObject) int_ja.get(j);
                    if (((String) tmpjo.get("name")).equals(entry.get("name"))) {
                        foundArray = true;
                    } else {
                        break;
                    }
                    if ((((String) tmpjo.get("site_name")).equals(entry.get("site_name")))) {
                        foundElem = true;
                        break;
                    }
                }
                if (!foundElem && foundArray) {
                    int_ja.put(entry);
                    ja.put(i, int_ja);
                    return ja;
                }
                if (foundElem) {
                    return ja;
                }

            }
            if (!foundElem && !foundArray) {
                JSONArray int_ja = new JSONArray();
                int_ja.put(homeEntry);
                int_ja.put(entry);
                ja.put(int_ja);
                return ja;
            }
            return ja;
        } catch (JSONException je) {
            LOGGER.error("Exception Occurred in Updating NetTables process");
            throw je;

        }
    }

    /**
     *
     * @param ja
     * @param entry
     * @return
     * @throws JSONException
     */
    private JSONArray insertEntry_in_SiteTable(JSONArray ja, JSONObject entry) throws JSONException {
        try {
            boolean foundElem = false;
            boolean foundArray = false;
            if (ja == null) {
                ja = new JSONArray();

            }
            for (int i = 0; i < ja.length(); i++) {
                //JSONArray int_ja = ja.getJSONArray(i);
                JSONObject tmpjo = (JSONObject) ja.get(i);
                if (((String) tmpjo.get("name")).equals(entry.get("name"))) {
                    foundElem = true;
                    break;
                }
            }
            if (!foundElem) {
                ja.put(entry);
                return ja;
            }
            return ja;
        } catch (JSONException je) {
            LOGGER.error("Exception Occurred in Updating NetTables process");
            throw je;

        }
    }

    /**
     *
     * @param site_name
     * @param tenant_id
     * @param name
     * @param vnid
     * @return
     */
    private String createNetTableEntry(String site_name, String tenant_id, String name, String vnid) {
        String tmp = "{";
        tmp = tmp + ("\"tenant_id\": \"" + tenant_id + "\", ");
        tmp = tmp + ("\"site_name\": \"" + site_name + "\", ");
        tmp = tmp + ("\"name\": \"" + name + "\", ");
        tmp = tmp + ("\"vnid\": \"" + vnid + "\"");
        tmp = tmp + "}";
        System.out.println(tmp);
        return tmp;
    }

    /**
     *
     * @param site_name
     * @param tenant_id
     * @param ip
     * @param fa_url
     * @param port
     * @return
     */
    private String createSiteTableEntry(String site_name, String tenant_id, String ip, String fa_url, String port) {
        String tmp = "{";
        tmp = tmp + ("\"tenant_id\": \"" + tenant_id + "\", ");
        tmp = tmp + ("\"name\": \"" + site_name + "\", ");
        tmp = tmp + ("\"site_proxy\": [{\"ip\":\"" + ip + "\",\"port\": " + port + "}],");
        tmp = tmp + ("\"fa_url\": \"" + fa_url + "\"");
        tmp = tmp + "}";
        return tmp;
    }
    //</editor-fold>

}
