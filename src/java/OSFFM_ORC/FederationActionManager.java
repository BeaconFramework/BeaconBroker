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

package OSFFM_ORC;

import API.SOUTHBR.FA_client4Network;
import API.SOUTHBR.FA_client4Sites;
import API.SOUTHBR.FA_client4Tenant;
import JClouds_Adapter.FunctionResponseContainer;
import JClouds_Adapter.KeystoneTest;
import JClouds_Adapter.NeutronTest;
import JClouds_Adapter.OpenstackInfoContainer;
import MDBInt.DBMongo;
import MDBInt.FederatedUser;
import MDBInt.FederationAgentInfo;
import MDBInt.FederationUser;
import OSFFM_ORC.Utils.FednetsLink;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;
import org.jclouds.openstack.neutron.v2.domain.Network;
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
                Iterator<Network> iN = neutron.listNetworks();
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
     * @param fednetsLink
     * @param groupName 
     */
    public void prepareNetTablesCustom(FednetsLink fednetsLink, String groupName){
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
     * @param netTablesVersionforDC, mappa stack Nettable
     * @param tmpMapcred, mappa stack arraylist(di arrayList di OpenStack Container)
     * @param m 
     */
    public void prepareNetTables4completeSharing(
            String tenantname,
            FednetsLink  mapContainer,
            HashMap<String, ArrayList<ArrayList<OpenstackInfoContainer>>> tmpMapcred,
            DBMongo m)
    {
        //1 Non più necessario!
          //&&&&&&&&&&&&&& REPLICARE COMPORTAMENTO test.java SOUTHBRIDGEAPI.
        FednetsLink fe=this.createCredMapwithoutDuplicate(tmpMapcred,mapContainer);
        Set<String> s=mapContainer.getOldnetTablesMap().keySet();
        LinkedHashMap<String,FederationAgentInfo> fa4cloud=new LinkedHashMap<>();
        for(String idcloud: s){
            try{
                String endpoint=fe.getCloudId_To_OIC().get(idcloud).getEndpoint();
                fa4cloud.put(endpoint, new FederationAgentInfo(m.getFAInfo(tenantname,idcloud )));
            }catch(JSONException e){
                LOGGER.error("Exception occurred in Parsing JSON retrieved from MongoDB in FAInfo Retrieve for Datacenter "+idcloud+"!"+"\nException message:"+e.getMessage() );
            }
        }
        fe.setEndpoint_To_FAInfo(fa4cloud);
        fe=this.prepareKeystoneMap(fe);//prepareTables4link deve partire sempre dopo 
        
        //4 invoca la funzione di Link
        ////4.1 le tabelle saranno passate dalla funzione dell'orchestrator prepareNetTables4completeSharing
        ////4.2 invoca la funzione createNetTable della classe FA_client4Network
        
        //5 aggiorna lo stato delle FA netTable in memoria
        ////5.1 salva la nuova netTable incrementando la versione su mongo
        ////5.2 salva la versione aggirnata della cloudlinkstatus su mongo
    }
    
    /**
     * 
     * @param tmpMapcred
     * @return 
     */
    private FednetsLink createCredMapwithoutDuplicate(HashMap<String, ArrayList<ArrayList<OpenstackInfoContainer>>> tmpMapcred,FednetsLink fe){
        LinkedHashMap<String,OpenstackInfoContainer>cleanMap= new LinkedHashMap<>();
        Set<String> s=tmpMapcred.keySet();
        for(String st:s){
            ArrayList<ArrayList<OpenstackInfoContainer>> tmpar=tmpMapcred.get(st);
            for(ArrayList<OpenstackInfoContainer> at:tmpar){
                for(OpenstackInfoContainer oic:at){
                    if(!cleanMap.containsKey(oic.getIdCloud()))
                    {
                        cleanMap.put(oic.getIdCloud(), oic);
                        fe.addDcInFednet(oic.getIdCloud());
                    }
                }
            }
        }
        
        fe.setCloudId_To_OIC(cleanMap);
        return fe;
    }
    
    /**
     * Create LinkedHashMap with KeystoneManager for each site.
     * @param cleanMap
     * @return 
     * @author gtricomi
     */
    private FednetsLink prepareKeystoneMap(FednetsLink fednetContainer){
        Set<String> s=fednetContainer.getCloudId_To_OIC().keySet();
        LinkedHashMap<String,KeystoneTest> tmp=new LinkedHashMap<>();
        LinkedHashMap<String,String> endpoint_to_tenantid= new LinkedHashMap<>();
        
        for(String idcloud: s){
            OpenstackInfoContainer oictmp=fednetContainer.getCloudId_To_OIC().get(idcloud);
            KeystoneTest kt =new KeystoneTest(oictmp.getTenant(),oictmp.getUser(),oictmp.getPassword(),oictmp.getEndpoint());
            tmp.put(idcloud, kt);
            endpoint_to_tenantid.put(oictmp.getEndpoint(),kt.getTenantId(oictmp.getTenant()));
        }
        fednetContainer.setkMcloudId_To_Keystone(tmp);
        fednetContainer.setEndpoint_to_tenantid(endpoint_to_tenantid);
        return fednetContainer;
    }
    
    private void prepareTables4link(FednetsLink fednetContainer,DBMongo m) throws JSONException{//aggiungere gestione delle tabelle dei siti
        KeystoneTest[] kar=(KeystoneTest[])fednetContainer.getkMcloudId_To_Keystone().values().toArray();
        ArrayList<String> tmpListupdatingNet= new ArrayList<>();
        Set<String> s=fednetContainer.getCloudId_To_OIC().keySet();
        
        for(String cloudID: s){//scorre il set per selezionare la home
            String ten=fednetContainer.getCloudId_To_OIC().get(cloudID).getTenant();
            NeutronTest neutronhome=new NeutronTest(
                    fednetContainer.getCloudId_To_OIC().get(cloudID).getEndpoint(),
                    fednetContainer.getCloudId_To_OIC().get(cloudID).getTenant(),
                    fednetContainer.getCloudId_To_OIC().get(cloudID).getUser(),
                    fednetContainer.getCloudId_To_OIC().get(cloudID).getPassword(),
                    fednetContainer.getCloudId_To_OIC().get(cloudID).getRegion());
            JSONObject updatedNetTable=fednetContainer.getOldnetTablesMap().get(cloudID);
            JSONArray updatedTable=updatedNetTable.getJSONArray("table");        
                for(String cloudID2: s){//scorre il set per la creazione tabella
                    if(!cloudID2.equals(cloudID))
                    {
                        NeutronTest neutron=new NeutronTest(
                                fednetContainer.getCloudId_To_OIC().get(cloudID2).getEndpoint(),
                                fednetContainer.getCloudId_To_OIC().get(cloudID2).getTenant(),
                                fednetContainer.getCloudId_To_OIC().get(cloudID2).getUser(),
                                fednetContainer.getCloudId_To_OIC().get(cloudID2).getPassword(),
                                fednetContainer.getCloudId_To_OIC().get(cloudID2).getRegion());
                        Iterator<Network> itNet=neutron.listNetworks();
                        
                        while(itNet.hasNext()){//per ogni network del sito 
                            Network n=itNet.next();
                            Network nHome=neutronhome.getNetwork(n.getName());
                            if(nHome==null)
                                LOGGER.error("Something is going wrong in preparation JSONObject for cloud "+cloudID+". It's impossible found the Network Named "+n.getName());
                            String entryCreated=this.createNetTableEntry(cloudID2,n.getTenantId(), n.getName(), n.getId());
                            String entryHome="";
                            if(nHome!=null)
                                entryHome=this.createNetTableEntry(cloudID2,nHome.getTenantId(), nHome.getName(), nHome.getId());
                            try{
                                JSONObject j=new JSONObject(entryCreated);
                                JSONArray ja=updatedTable;
                                ja=this.insertEntry_in_NetTable(ja, j,new JSONObject(entryHome));
                            }
                            catch(JSONException je){
                                
                            }
                        }
                    }
                }
                KeystoneTest homeKey=(KeystoneTest)fednetContainer.getkMcloudId_To_Keystone().get(cloudID);
                FA_client4Network fan1=new FA_client4Network(homeKey.getVarEndpoint(),fednetContainer.getEndpoint_to_tenantid().get(homeKey.getVarEndpoint()),homeKey.getVarIdentity().split(":")[1],homeKey.getVarCredential());
                String body=fan1.constructNetworkTableJSON(updatedTable, (updatedNetTable.getInt("version"))+1);
                FederationAgentInfo fai1=fednetContainer.getEndpoint_To_FAInfo().get(fednetContainer.getEndpoint_to_tenantid().get(homeKey.getVarEndpoint()));
                try{
                    fan1.createNetTable(fednetContainer.getEndpoint_to_tenantid().get(homeKey.getVarEndpoint()), fai1.getIp()+":"+fai1.getPort(), body);
                }
                catch(WSException wse){
                    //something here
                    
                }
                m.insertNetTables(homeKey.getVarIdentity().split(":")[0], ten, ten, ten);
            //}
            //funzione che aggiorna le tabelle di NetTables,verifica le site tables per quella cloud aggiornandola se serve e le mette nel container
            
        }
        //funzione che invia ad ogni cloud la propria tables aggiornata e poi la scrive su Mongo nella collezione corretta aggiornando la versione
        
        
        /*TO BE DELETE
        for(int i=0;i<kar.length;i++){
            for(int j=i+1;j<kar.length;j++){
                FA_client4Tenant fat1=new FA_client4Tenant(kar[i].getVarEndpoint(),fednetContainer.getEndpoint_to_tenantid().get(kar[i].getVarEndpoint()),kar[i].getVarIdentity().split(":")[1],kar[i].getVarCredential());
                FA_client4Tenant fat2=new FA_client4Tenant(kar[j].getVarEndpoint(),fednetContainer.getEndpoint_to_tenantid().get(kar[j].getVarEndpoint()),kar[j].getVarIdentity().split(":")[1],kar[j].getVarCredential());
                FederationAgentInfo fai1=fednetContainer.getEndpoint_To_FAInfo().get(kar[i].getVarEndpoint());
                FederationAgentInfo fai2=fednetContainer.getEndpoint_To_FAInfo().get(kar[j].getVarEndpoint());
                try{
                    fat1.createTenantFA(kar[i].getTenantId(kar[i].getVarIdentity().split(":")[0]),fai1.getIp()+":"+fai1.getPort() );
                    }
                catch(WSException wse){
                    LOGGER.error("Exception occurred in Create Tenant Table on FA:<"+fai1.getIp()+":"+fai1.getPort()+">\n"+wse.getMessage());
                    //invoke function that mark this link as not working
                }
                try{
                    fat2.createTenantFA(kar[j].getTenantId(kar[j].getVarIdentity().split(":")[0]), fai2.getIp()+":"+fai2.getPort() );
                    }
                catch(WSException wse){
                    LOGGER.error("Exception occurred in Create Tenant Table on FA:<"+fai2.getIp()+":"+fai2.getPort()+">\n"+wse.getMessage());
                    //invoke function that mark this link as not working
                }
                FA_client4Sites fas1=new FA_client4Sites(kar[i].getVarEndpoint(),fednetContainer.getEndpoint_to_tenantid().get(kar[i].getVarEndpoint()),kar[i].getVarIdentity().split(":")[1],kar[i].getVarCredential());
                FA_client4Sites fas2=new FA_client4Sites(kar[j].getVarEndpoint(),fednetContainer.getEndpoint_to_tenantid().get(kar[j].getVarEndpoint()),kar[j].getVarIdentity().split(":")[1],kar[j].getVarCredential());
                
                try{
                    //preparare la tabella confrontando le entry della precedente con le entry da aggiungere per la attuale, facendo attenzione a memorizzare le entry sulla nuova tabella temporanea di lavoro 
                    //che verrà salvata sempre dentro FednetsLink. Dopodichè invovcare al createSiteTable
                    fas1.createTenantFA(kar[i].getTenantId(kar[i].getVarIdentity().split(":")[0]),fai1.getIp()+":"+fai1.getPort() );
                    }
                catch(WSException wse){
                    LOGGER.error("Exception occurred in Create Tenant Table on FA:<"+fai1.getIp()+":"+fai1.getPort()+">\n"+wse.getMessage());
                    //invoke function that mark this link as not working
                }
                try{
                    fas2.createTenantFA(kar[j].getTenantId(kar[j].getVarIdentity().split(":")[0]), fai2.getIp()+":"+fai2.getPort() );
                    }
                catch(WSException wse){
                    LOGGER.error("Exception occurred in Create Tenant Table on FA:<"+fai2.getIp()+":"+fai2.getPort()+">\n"+wse.getMessage());
                    //invoke function that mark this link as not working
                }
                //BEACON>>>: manca gestione eventuali problemi qui dopo la creazione di entrambe le informazioni
            }
        }*/
    }
        
    private JSONArray insertEntry_in_NetTable(JSONArray ja,JSONObject entry,JSONObject homeEntry)throws JSONException{
        try {
            boolean foundElem = false;
            boolean foundArray = false;
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
            if(!foundElem && !foundArray){
                JSONArray int_ja =new JSONArray();
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
        
    private String createNetTableEntry(String site_name, String tenant_id, String name, String vnid) {
        String tmp = "{";
        tmp = tmp + ("\"tenant_id\": \"" + tenant_id + "\", ");
        tmp = tmp + ("\"site_name\": \"" + site_name + "\", ");
        tmp = tmp + ("\"name\": \"" + name + "\", ");
        tmp = tmp + ("\"vnid\": \"" + vnid + "\"");
        tmp = tmp + "}";
        return tmp;
        
    }
        
        
        
    /*
    private FednetsLink prepareSiteTable(FednetsLink fe){
        LinkedHashMap<String,JSONObject> old= fe.getOldsiteTablesMap();
        LinkedHashMap<String,String> nerRef= fe.getEndpoint_to_tenantid();
        //LinkedHashMap endRef= fe.getCloudId_To_OIC();
        Set<String> s=nerRef.keySet();
        for(String end:s){
            String ten_id=nerRef.get(end);
        }
        //TO BE Complete
    }*/
}
