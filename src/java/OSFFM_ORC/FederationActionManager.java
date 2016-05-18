/*7
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OSFFM_ORC;

import JClouds_Adapter.FunctionResponseContainer;
import JClouds_Adapter.NeutronTest;
import JClouds_Adapter.OpenstackInfoContainer;
import MDBInt.DBMongo;
import MDBInt.FederatedUser;
import MDBInt.FederationUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

/**
 * All action that will be done on each federated cloud is Managed inside this Class. 
 * @author Giuseppe Tricomi
 */
public class FederationActionManager {
    
    
    
    static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(FederationActionManager.class);
    DBMongo db=new DBMongo();
    
    public JSONObject networkSegmentAdd(FederationUser fu, String OSF_network_segment_id,String OSF_cloud,HashMap netParameter) throws Exception {
        JSONObject reply = new JSONObject();
        HashMap fum = this.getAllFederatedCloudCreds(fu);
        JSONObject network_info=new JSONObject(),network_infoGLOBAL=new JSONObject();
        JSONArray ja=new JSONArray();
        
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
                if(!((String)fdu.get("federatedCloud")).equals(OSF_cloud))
                    continue;
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
            
            String cidr="";
            boolean found = false,cidrPresent=false;
            String internalNetId = this.db.getInternalNetworkID(fu.getUser(), OSF_network_segment_id, fed_U.getCloud());
            if (internalNetId != null) 
            {
                Iterator<Network> iN = neutron.listNetworks();
                found = false;
                while (i.hasNext() && !found) {
                    Network n = iN.next();
                    if (internalNetId.equals(n.getId())) {
                        found = true;//nothing to do
                    }
                }
                if (!found) 
                {
//this is the case when some info aren't aligned, the internal ID for netsegment are present but 
//// no network is retrieved for tenant inside the cloud match it.
                    //Inserire qui la chiamata relativa al Network Segment verso l'API "/fednet/{fednetId}/{site_id}/netsegment/{netsegment_id}"
                    ////da cui ritrovare tutte le varie 
                    //in alternativa creare le informazioni qui:
                    HashMap th = new HashMap();
                    th.put("netsegments", OSF_network_segment_id);
                    org.json.JSONObject j = this.db.getcidrInfoes(fu.getUser(), th);
                    if (j != null) {
//this is the case when a cidr is already defined for this fednet and FederationUser. That would mean that we have to create the 
////other netsegments of the fednet with ther same parameters.                         
                        th = new HashMap();
                        th.put("netsegments", OSF_network_segment_id);
                        th.put("cloudId", fed_U.getCloud());
                        org.json.JSONObject j2 = this.db.getcidrInfoes(fu.getUser(), th);
                        if (j2 == null) {
//on mongo the netsegments searched is not found for this cloud then we will take the federation cidr
                            cidr = (String) j.get("cidr");
                        } 
                        else 
                        {
                            //disallineamento è presente un cidr ma non c'è un internalId che corrisponda al netsegments
                            //sarebbe giusto creare un eccezione
                            throw new Exception("A problematic situation is founded! MongoDB information aren't alingned Contact the administrator!");
                        }
                    } 
                    else
                    {
//this is the case when on mongo the cidr for netsegments indicated is not found. This case represent first istantiation of the
////netsegment for this fednet and FederationUser.   
                        cidrPresent=true;
                    }
                }
                else{
                //match
                //nothing to do
                }
                
            }
            else
                internalNetId=java.util.UUID.randomUUID().toString();
//prepare and retrieve information for creation of network
            Boolean dhcp=(Boolean)netParameter.get("dhcpEnable");
            if(dhcp==null)
                dhcp=false;
            Boolean shared=(Boolean)netParameter.get("shared");
            if(shared==null)
                shared=false;
            Boolean external=(Boolean)netParameter.get("external");
            if(external==null)
                external=false;
            Boolean adminStateUp=(Boolean)netParameter.get("adminStateUp");
            if(adminStateUp==null)
                adminStateUp=false;
            String subnetId=java.util.UUID.randomUUID().toString();    
            if(!cidrPresent)    
                cidr = this.cidrgen();
            HashMap allpo=this.calculateAllocationPool(cidr);
            SubnetUtils su = new SubnetUtils(cidr);
            SubnetInfo si = su.getInfo();
            String gwadd= si.getLowAddress();
            String fednets="";//fednet;
            try{
            FunctionResponseContainer frc=neutron.createCompleteNetw(
                    fed_U.getRegion(),
                    internalNetId,
                    cidr,
                    (String)allpo.get("allocationPoolStart"),
                    (String)allpo.get("allocationPoolEnd"),
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
            }
            catch(Exception e){
                LOGGER.error("Exception occurred in network creation operation!");
            }
            this.db.storeInternalNetworkID(fu.getUser(), OSF_network_segment_id, fed_U.getCloud(), internalNetId);
            this.db.insertcidrInfoes(fu.getUser(), cidr, fednets, OSF_network_segment_id, fed_U.getCloud());
            network_info=new JSONObject();
            network_info.put("cloudId",fed_U.getCloud());
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
    private HashMap getAllFederatedCloudCreds(FederationUser fu){
        //costruzione di un Mappa di FederatedUser
        HashMap fum=new HashMap();
        Iterator i=fu.getCredentials().iterator();
        while(i.hasNext()){
            JSONObject jo=(JSONObject)i.next();
            try {
                fum.put(jo.get("federatedCloud"),jo);
            } catch (JSONException ex) {
               LOGGER.error("Could not foud federatedCloud field in JSON!"+ex.getMessage());
            }
        }
        return fum;
    }
    /**
     * Function used to create cidr for new network, when it is not provided (Used for FEDSDN case).
     * @return
     * @author gtricomi
     */
    private String cidrgen(){
        return "10."+(new Random().nextInt() % 255)+"."+(new Random().nextInt() % 255)+".0/24";
    }
    
    /**
     * 
     * @param cidr
     * @return
     * @throws Exception 
     * @author gtricomi
     */
    private HashMap calculateAllocationPool(String cidr)throws Exception{
        HashMap hm=new HashMap();
        String allocationPoolStart=this.nextIpAddress(cidr);
        //we need the third IP of the allocation pool
        for(int i=0;i<2;i++)
            allocationPoolStart=this.nextIpAddress(allocationPoolStart);
            
        hm.put("allocationPoolStart", allocationPoolStart);
        SubnetUtils su = new SubnetUtils(cidr);
        SubnetInfo si = su.getInfo();
        hm.put("allocationPoolEnd", si.getHighAddress());
        return hm;
    }
    
    /**
     * Function to obtain the next IP of the input.
     * @param input
     * @return 
     */
    private String nextIpAddress(String input) {
    final String[] tokens = input.split("\\.");
    if (tokens.length != 4)
        throw new IllegalArgumentException();
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
}
