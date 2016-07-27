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

//<editor-fold defaultstate="collapsed" desc="Import Section">
//import JClouds_Adapter.Heat;
import API.EASTAPI.Clients.Fednet;
import API.EASTAPI.Clients.Site;
import JClouds_Adapter.NeutronTest;
import JClouds_Adapter.NovaTest;
import JClouds_Adapter.OpenstackInfoContainer;
import MDBInt.DBMongo;
import MDBInt.FederationUser;
import MDBInt.MDBIException;
import OSFFM_ORC.Utils.Exception.NotFoundGeoRefException;
import OSFFM_ORC.Utils.FednetsLink;
import OSFFM_ORC.Utils.MultiPolygon;
import com.edw.rmi.RMIServerInterface;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import org.jclouds.openstack.neutron.v2.domain.Port;
import org.jdom2.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openstack4j.model.heat.Resource;
import org.openstack4j.model.heat.Stack;

import org.yaml.snakeyaml.Yaml;
import utils.Exception.WSException;
import utils.ParserXML;
import utils.RunTimeInfo;
//</editor-fold>

/**
 *
 * @author Giuseppe Tricomi
 */
public class OrchestrationManager {
    
    //<editor-fold defaultstate="collapsed" desc="Variable Definition Section">
    private String ip="172.17.3.142";//default value for internal testing
    private int port=1099;//default value for internal testing
    private String fileConf="../webapps/OSFFM/WEB-INF/configuration_Orchestrator.xml";
    static HashMap<String,ManifestManager> mapManifestThr=new HashMap<String,ManifestManager>();//mappa che mantiene riferimenti manifest- manifest manager
    HashMap<String,ArrayList> globalTOfragmentsManif;//BEACON>>> this variable need to be used in splitting alghoritm
    static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(OrchestrationManager.class);
    //</editor-fold>
    
    public OrchestrationManager() {
        this.globalTOfragmentsManif=new HashMap<String,ArrayList>();
        //this.init();
    }
    
    public void init(){
        Element params;
        try {
            ParserXML parser = new ParserXML(new File(fileConf));
            params = parser.getRootElement().getChild("pluginParams");
            ip = params.getChildText("ip");
            port = Integer.parseInt(params.getChildText("port"));
        } 
        catch (Exception ex) {
            LOGGER.error("Error occurred in configuration ");
            ex.printStackTrace();
        }
    }   
    
   
    
    
    
    /**
     * 
     * @param tenant
     * @param template
     * @param endpoint
     * @param user
     * @param psw
     * @return 
     */
   /* public boolean OLDstackInstantiate(String template,OpenstackInfoContainer credential,DBMongo m,String templateId){
        Heat h=new Heat(credential.getEndpoint(), credential.getUser(),credential.getTenant(),credential.getPassword());
        try{
            Stack s=h.createStack(templateId, template);//restituirà un oggetto di tipo stack che all'interno possiede
                    // lo stato ottenuto , verificare se lo status (getStatus) è di tipo "CREATE_COMPLETE" o "CREATE_FAILED"
            List lr=h.getResource(s.getId());
            for(Object r : lr){
               RunTimeInfo rti=new RunTimeInfo();
               rti.setStackName(s.getName());
               rti.setStackUuid(s.getId());
               rti.setRegion(credential.getRegion());
               rti.setIdCloud(credential.getIdCloud()); 
               rti.setLocalResourceName(((Resource)r).getLocalReourceId());
               rti.setPhisicalResourceId(((Resource)r).getPhysicalResourceId());
               rti.setType(((Resource)r).getType());
               rti.setResourceName(((Resource)r).getResourceName());
               rti.setState(true);
               rti.setUuidTemplate(templateId);
               m.insertRuntimeInfo(credential.getTenant(), rti.toString());
            }
            if(s.getStatus().equals("CREATE_FAILED")){
                LOGGER.error("An error is occurred in stack creation phase.Verify Federated Openstack state.\n"
                        + " Stack creation Operation har returned CREATE_FAILED");
                return false;
            }
                
        }
        catch(Exception e){
            LOGGER.error("An error is occurred in stack creation phase.");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    */
    /**
     * 
     * @param stackName
     * @param endpoint
     * @param tenant
     * @param user
     * @param password
     * @param region
     * @param first
     * @param m
     * @return 
     */
   /* public HashMap<String,ArrayList<Port>> OLDsendShutSignalStack4DeployAction(String stackName,OpenstackInfoContainer credential,
            boolean first,DBMongo m)
    {
        try {
            NovaTest nova = new NovaTest(credential.getEndpoint(), credential.getTenant(), credential.getUser(), credential.getPassword(), credential.getRegion());
            NeutronTest neutron = new NeutronTest(credential.getEndpoint(), credential.getTenant(), credential.getUser(), credential.getPassword(), credential.getRegion());
            Heat heat = new Heat(credential.getEndpoint(), credential.getUser(), credential.getTenant(), credential.getPassword());
            HashMap<String, ArrayList<Port>> mapResNet = new HashMap<String, ArrayList<Port>>();
            List<? extends Resource> l = heat.getResource(stackName);
            Iterator it_res = l.iterator();
            while (it_res.hasNext()) {
                Resource r = (Resource) it_res.next();
                String id_res = r.getPhysicalResourceId();
                if (!first) {
                    nova.stopVm(id_res);
                    m.updateStateRunTimeInfo(credential.getTenant(), id_res, first);
                }
                ArrayList<Port> arPort = neutron.getPortFromDeviceId(id_res);
                //inserire in quest'array la lista delle porte di quella VM
                mapResNet.put(id_res, arPort);
                Iterator it_po = arPort.iterator();
                while (it_po.hasNext()) {
                    m.insertPortInfo(credential.getTenant(), neutron.portToString((Port) it_po.next()));
                }
            }
            return mapResNet;
        } catch (Exception e) {

            LOGGER.error("An error is occurred in stack creation phase.");
            e.printStackTrace();
            return null;
        }
    }
    */
    
    
            
    
    
    /**
     * Testing function for analyzing Manifest and trasforming it in YAML.
     * @param mnam
     * @param root 
     
    public void test(String mnam,String root){
        ManifestManager mm=(ManifestManager)OrchestrationManager.mapManifestThr.get(mnam);
        this.manageYAMLcreation(mm, root);
    }
    */
   
   
    //<editor-fold defaultstate="collapsed" desc="Utility functions for orchestration">
    
    
    /**
     * Function called from API when web service is invocated.
     * @param Manifest
     */
    public void addManifestToWorkf(String nameMan,JSONObject manifest){
        ManifestManager mm=null;
        try {
            mm = new ManifestManager(nameMan,manifest);
        } catch (JSONException ex) {
            LOGGER.error(ex.getMessage());
        }
        mm.run();
    }
    
    /**
     * Function used to insert hte ManifestManager element inside the static Table.
     * @param name
     * @param thread 
     */
    public static void putEntryinTable(String name,ManifestManager thread){
        OrchestrationManager.mapManifestThr.put(name, thread);
    }
    
    /**
     * Function used to write Yaml manifest inside a specific file passed in resource nameMan.
     * @param nameMan
     * @param jobj 
     */
    private void writeManifestonFile(String nameMan,String jobj){
        ManifestManager mm=OrchestrationManager.mapManifestThr.get(nameMan);
        //LOGGER.debug(">>>>>>>>>>"+nameMan);
        File f=new File(nameMan);
        f.getParentFile().mkdirs();
        FileWriter w;
        try{
            w=new FileWriter(nameMan);//scegliere oppoortunamente il nome del file per il salvataggio
            BufferedWriter b=new BufferedWriter (w);
            b.write(jobj);
            b.flush();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Function used to manage the YAML files creation, one for each Service Group found inside original Manifest.
     * The naming procedure used to name the YAML files resulting is: rootName+_+"OS::Beacon::ServiceGroupManagement resource name".
     * @param mm
     * @param rootName 
     */
    private void manageYAMLcreation(ManifestManager mm,String rootName,String tenant){
        Set ks=mm.table_resourceset.get("OS::Beacon::ServiceGroupManagement").keySet();
        Object[] obj=ks.toArray();
        for(int index=0;index<ks.size();index++){
            String val="";
            try{
                val=mm.ComposeJSON4element((String)obj[index]);
            }
            catch(JSONException je){
                LOGGER.error("A JSONException is occurred"+je.getMessage());
            }
            catch(Exception e){
                LOGGER.error("A generic Exception is occurred"+e.getMessage());
            }
            //LOGGER.debug("QUI arrivo: "+"./subrepoTemplate/"+tenant+rootName+"_"+(String)obj[index]);
            String home=System.getProperty("java.home");
            String fs=System.getProperty("file.separator");
            //this.writeManifestonFile(home+fs+"subrepoTemplate"+fs+tenant+fs+tenant+rootName+"_"+(String)obj[index], val);
            this.writeManifestonFile("."+fs+"subrepoTemplate"+fs+tenant+fs+tenant+rootName+"_"+(String)obj[index], val);


//ArrayList<String> tmp=this.globalTOfragmentsManif.get(rootName);
            //tmp.add(rootName+"_"+(String)obj[index]);
            //this.globalTOfragmentsManif.put(rootName,tmp);
        }
    }
    
    
    /**
     * This function split the global federation Manifest in single Manifest for each stack 
     * ready to be deployed in federated cloud.
     * @param manName
     * @param tenant 
     */
    public void splitManifest(String manName,String tenant){
        ManifestManager mm=(ManifestManager)OrchestrationManager.mapManifestThr.get(manName);
        this.manageYAMLcreation(mm, manName, tenant);
    }   
    
    
    /**
     * It returns an HashMap that correlate ServiceGroup name with the ArrayList that contains 
     * the ArrayList of String representation of JSONObject of the Datacenter info.
     * These Datacenter set (represented by the inner ArrayList) is the dc, existent inside the shape
     * descript in geoshape; the external ArrayList is the set of all previous entity retrieved for each shape.
     * The external ArrayList is a priority ordered List.
     * @param manName
     * @param db
     * @param tenant
     * @return 
     */
    public HashMap<String,ArrayList<ArrayList<String>>> managementgeoPolygon(String manName,MDBInt.DBMongo db,String tenant){
        HashMap<String,ArrayList<ArrayList<String>>> tmp=new HashMap<String,ArrayList<ArrayList<String>>>();//mappa contenente associazione nome shape con Datacenter ID&info
//salvare questa mappa come oggetto dell'orchestrator
        ManifestManager mm=(ManifestManager)OrchestrationManager.mapManifestThr.get(manName);
        Set s=mm.table_resourceset.get("OS::Beacon::ServiceGroupManagement").keySet();
        Iterator it=s.iterator();
        boolean foundone =false;
        while(it.hasNext()){
            String serName=(String)it.next();
            SerGrManager sgm=(SerGrManager)mm.serGr_table.get(serName);
            ArrayList<MultiPolygon> ar=null;
            try{
                ar=(ArrayList<MultiPolygon>)mm.geo_man.retrievegeoref(sgm.getGeoreference());
            }catch(NotFoundGeoRefException ngrf){
                LOGGER.error("An error is occourred in retrievegeoref. The GeoManager doesn't contain the shape searched.\n"+ngrf.getMessage());
                ngrf.printStackTrace();
            }
            ArrayList dcInfoes=new ArrayList();
            for(int index=0;index<ar.size();index++){
                try{
                    ArrayList<String> dcInfo=db.getDatacenters(tenant,ar.get(index).toJSONString());
                    if(dcInfo.size()!=0){
                        dcInfoes.add(dcInfo);
                        foundone=true;
                    }
                }
                catch(org.json.JSONException je){
                    LOGGER.error("An error is occourred in MultiPolygon JSON creation.");
                }
            }
            tmp.put(serName, dcInfoes);
            if(!foundone)
                return null;
        }
        return tmp;
    }
    
    /**
     * It returns an HashMap that correlate ServiceGroup name with the ArrayList that contains 
     * the ArrayList of String representation of JSONObject of the user credential for each datacenter.
     * These Datacenter set (represented by the inner ArrayList) is the credential for datacenter existent inside the shape
     * descript in geoshape; the external ArrayList is the set of all previous entity retrieved for each shape.
     * The external ArrayList is a priority ordered List.
     * @param dcInfoesMap
     * @param db
     * @param tenant
     * @param username
     * @param password
     * @return 
     */
    public HashMap<String,ArrayList<ArrayList<OpenstackInfoContainer>>> managementRetrieveCredential(HashMap<String,ArrayList<ArrayList<String>>> dcInfoesMap,MDBInt.DBMongo db,String tenant,String username,String password,String region){
        HashMap<String,ArrayList<ArrayList<OpenstackInfoContainer>>> tmp=new HashMap<String,ArrayList<ArrayList<OpenstackInfoContainer>>>();//mappa contenente associazione nome shape con federatedUser credential for each Datacenter
        String serName="";
        Iterator it=dcInfoesMap.keySet().iterator();
        while(it.hasNext()){
            serName=(String)it.next();
            ArrayList<ArrayList<String>> tmp2=(ArrayList<ArrayList<String>>)dcInfoesMap.get(serName);
            ArrayList<ArrayList<OpenstackInfoContainer>> crtmp2=new ArrayList<ArrayList<OpenstackInfoContainer>>();
            for(int ind_ex=0;ind_ex<tmp2.size();ind_ex++){
                ArrayList<String> tmp3=(ArrayList<String>)dcInfoesMap.get(serName).get(ind_ex);
                ArrayList<OpenstackInfoContainer> crtmp3=new ArrayList<OpenstackInfoContainer>();
                for(int ind_int=0;ind_int<tmp3.size();ind_int++){
                    JSONObject j=null,jj=null;
                    OpenstackInfoContainer credential=null;
                    try{
                        j=new JSONObject((String)tmp3.get(ind_int));
                        jj=new JSONObject(db.getFederatedCredential(tenant, username, password,j.getString("cloudId")));
                        //LOGGER.debug(">>>>>>>>managementRetrieveCredential"+jj.toString());
                        credential=new OpenstackInfoContainer(j.getString("cloudId") ,j.getString("idmEndpoint"),tenant,jj.getString("federatedUser"),jj.getString("federatedPassword"),region);
                    }
                    catch(org.json.JSONException je){
                        LOGGER.error("An error is occourred in MultiPolygon JSON creation.");
                    }
                    crtmp3.add(credential);
                }
                crtmp2.add(crtmp3);
            }
            tmp.put(serName, crtmp2);
        }
        return tmp;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Management function for orchestration activity">
    
    
    /**
     * This function create Link starting from a request made from FEDSDN.
     * It retrieve info from parameter and MongoDB and construct the the table with all netsegments present on the indicated sites.
     * @param fednetId
     * @param federationTenant
     * @param netTables
     * @param m
     * @return 
     * @author gtricomi
     */
    public String makeLink(long fednetId, String federationTenant, ArrayList<JSONObject> netTables, DBMongo m) {
        //fase1: verificare che lo stato della fednet sia "unlink", altrimenti nothing to do
        JSONObject jo, tmp;
        try {
            jo = new JSONObject(m.getfedsdnFednet(federationTenant));
            if (((String) jo.get("status")).equals("linked")) {
                return "ok";
            }
        } catch (JSONException je) {
            LOGGER.error("Exception Occurred in makeLink function!" + je.getMessage());
            return "error";
        }
        //fase2: caso unlink: recuperare la lista dei netsegment della fednet per avviare il processo di link delle stesse cloud
        String fedsdnpassword = m.getFederationCredential(federationTenant, federationTenant, "federationUser");
        Fednet fClient = new Fednet(federationTenant, fedsdnpassword);
        Site sClient = new Site(federationTenant, fedsdnpassword);
        String fedsdnURL = m.getInfo_Endpoint("entity", "fedsdn");
        try {
            Response r = fClient.getNetinfo(fedsdnURL, jo.getString("name"));
            //// creare la tabella (da passare al FA) per i netsegment indicati dal fedsdn. Per ogni netsegment recuperare l'identificativo del "site"
            //// e da questo individure la cloud (il name = OSFFM cloudID) e per ognuno costruire la tabella che fà lo share di tutte le reti
            tmp = new JSONObject(r.readEntity(String.class));
            JSONArray ja = tmp.getJSONArray("netsegments");
            HashMap<String, ArrayList<ArrayList<OpenstackInfoContainer>>> tmpMapCred = new HashMap<>();
            for (int i = 0; i < ja.length(); i++) {
                JSONObject intmp = (JSONObject) ja.getJSONObject(i);
                String siteid = (String) intmp.get("site_id");
                JSONObject sitejson = new JSONObject(sClient.getSiteInfoes(fedsdnURL, new Long(siteid).longValue()).readEntity(String.class));
                ArrayList<ArrayList<OpenstackInfoContainer>> artmp = new ArrayList<ArrayList<OpenstackInfoContainer>>();
                JSONObject jj = new JSONObject(m.getFederatedCredential(federationTenant, m.getTenantToken("federationTenant", federationTenant), sitejson.getString("name")));
                JSONObject j = new JSONObject(m.getDatacenter(federationTenant, sitejson.getString("name")));
                OpenstackInfoContainer oic = new OpenstackInfoContainer(
                        sitejson.getString("name"),
                        j.getString("idmEndpoint"),
                        federationTenant,
                        jj.getString("federatedUser"),
                        jj.getString("federatedPassword"),
                        "RegionOne");//>>>BEACON: in this moment Region is an hardcoded field but future works could be manage the Region field inside Datacenters infos
                ArrayList<OpenstackInfoContainer> arintmp = new ArrayList<OpenstackInfoContainer>();
                arintmp.add(oic);
                artmp.add(arintmp);
                tmpMapCred.put(sitejson.getString("name"), artmp);
            }
            this.prepareNetTables4completeSharing(federationTenant, "", tmpMapCred, m);

        } catch (JSONException ex) {
            LOGGER.error("Exception Occurred in makeLink function!" + ex.getMessage());
            return "error";
        } catch (WSException ex) {
            LOGGER.error("Exception Occurred in makeLink function!" + ex.getMessage());
            return "error";
        } catch (MDBIException ex) {
            LOGGER.error("Exception Occurred in makeLink function!" + ex.getMessage());
            return "error";
        }
        return "ok";
    }
    
    /**
     * 
     * @param manName uuid manifest passed from dashboard
     */
    public void manifestinstatiation(String manName,String tenant){
        //for the moment this function is incomplete. To use it, programmer have to retrieve from mongoDb the right manifest.

        //retrieve Manifest from MongoDB, it is JSONObject.
        JSONObject manifest=null;
        //verifica della versione del manifest rimandata al futuro, per adesso lo rielaboro
        this.addManifestToWorkf(manName, manifest);
        ManifestManager mm=(ManifestManager)OrchestrationManager.mapManifestThr.get(manName);
        this.manageYAMLcreation(mm, manName,tenant);
        //lancio su heat i comandi per l'istanziazione degli stack.
    }
    
    /**
     * 
     * @param manName
     * @param manifest
     * @param tenant 
     */
    public void manifestinstatiation(String manName,JSONObject manifest,String tenant){
        //LOGGER.debug("MI 1");
        this.addManifestToWorkf(manName, manifest);
        //LOGGER.debug("MI 2");
        ManifestManager mm=(ManifestManager)OrchestrationManager.mapManifestThr.get(manName);
        //LOGGER.debug("MI 3");
        this.manageYAMLcreation(mm, manName,tenant);
        //LOGGER.debug("MI 4");
        //lancio su heat i comandi per l'istanziazione degli stack.
    }
    
    
    /**
     * Function invocated when an elasticity action have to be started; this function 
     * shutdown the VM with some problem and start one of the Twin VM of that.
     * This function could be improved with a better twin VM search algorithm, and to do this is needed 
     * modify this: "m.findResourceMate".
     * @param vm
     * @param tenant
     * @param userFederation
     * @param pswFederation
     * @param m
     * @param element
     * @param region
     * @author gtricomi
     */
    public void sufferingProcedure(String vm,String tenant,String userFederation,String pswFederation,DBMongo m,int element,String region){
        //spegnimento vm
        ////recupero runtimeinfo
        String runTime=m.getRunTimeInfo(tenant, vm);
        String idClo="",endpoint="",cred="", twinUUID="";
        OpenstackInfoContainer credential=null,credential2=null;
        JSONObject credJobj=null,runJobj=null;
        try{
            runJobj=new JSONObject(runTime);
            ////recupero idCloud
            idClo=runJobj.getString("idCloud");
            ////recupero le credenziali passando da quelle di federazione
            cred=m.getFederatedCredential(tenant, userFederation, pswFederation,idClo);
            credJobj=new JSONObject(cred);
            try {
                endpoint=(new JSONObject(m.getDatacenter(tenant, idClo))).getString("idmEndpoint");
            } catch (MDBIException ex) {
                Logger.getLogger(OrchestrationManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            credential=new OpenstackInfoContainer(idClo,endpoint,tenant,credJobj.getString("federatedUser"),credJobj.getString("federatedPassword"),region);
        }
        catch(JSONException je){
             LOGGER.error("An error is occourred in JSON crederntial manipulation.");
        }
        ////spengo la vm
        NovaTest nova=new NovaTest(credential.getEndpoint(),credential.getTenant(), credential.getUser(),credential.getPassword(),credential.getRegion());
        nova.stopVm(vm);
        // identificazione nuova vm
        ArrayList<String> vmTwins= m.findResourceMate(tenant, vm);
        try{
            JSONObject j=new JSONObject(vmTwins.get(element));
            twinUUID=j.getString("phisicalResourceId");
            ////recupero runtimeinfo
            runTime=m.getRunTimeInfo(tenant, twinUUID);
            runJobj=new JSONObject(runTime);
            ////recupero idCloud
            idClo=runJobj.getString("idCloud");
            ////recupero le credenziali passando da quelle di federazione
            cred=m.getFederatedCredential(tenant, userFederation, pswFederation,idClo);
            credJobj=new JSONObject(cred);
            try {
                endpoint=(new JSONObject(m.getDatacenter(tenant, idClo))).getString("idmEndpoint");
            } catch (MDBIException ex) {
                Logger.getLogger(OrchestrationManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            credential2=new OpenstackInfoContainer(idClo,endpoint,tenant,credJobj.getString("federatedUser"),credJobj.getString("federatedPassword"),region);
        }
        catch(JSONException je){
             LOGGER.error("An error is occourred in JSON crederntial manipulation.");
        }
        //accensione vm idenitificata
        nova=new NovaTest(credential2.getEndpoint(),credential2.getTenant(), credential2.getUser(),credential2.getPassword(),credential2.getRegion());
        nova.startVm(twinUUID);
        //restituzione dettagli vm spenta- rete, vm accesa- rete
        LOGGER.debug("Network infoes of the shutted down VM(identified by UUID:"+vm+"):");
        Iterator it_tmpar=m.getportinfoes(tenant, vm).iterator();
        while(it_tmpar.hasNext())
            LOGGER.debug((String)it_tmpar.next());
        LOGGER.debug("Network infoes of the twin VM started(identified by UUID:"+twinUUID+"):");
        it_tmpar=m.getportinfoes(tenant, twinUUID).iterator();
        while(it_tmpar.hasNext())
            LOGGER.debug((String)it_tmpar.next());
    }
     
    /**
     * 
     * @param tenant
     * @param template
     * @param endpoint
     * @param user
     * @param psw
     * @return
     * 
     * @author gtricomi
     */
    public boolean stackInstantiate(String template, OpenstackInfoContainer credential, DBMongo m, String templateId) {
        try {
            //System.out.println("axv"); 
            Registry myRegistry = LocateRegistry.getRegistry(ip, port);
            //System.out.println("asxx");
            boolean result;
            //System.out.println(" ccccc");
            RMIServerInterface impl = (RMIServerInterface) myRegistry.lookup("myMessage");

          //  System.out.println("aaax"+template);
            // result=impl.stackInstantiate(template, "idid", "http://172.17.1.217:5000/v2.0", "admin", "beacon", "password", "RegionOne", "UME");
            //System.out.println(credential.getEndpoint());
            result = impl.stackInstantiate(template, templateId, credential.getEndpoint(), credential.getUser(), credential.getTenant(), credential.getPassword(), credential.getRegion(), credential.getIdCloud());
            System.out.println(credential.getEndpoint());

            return result;

        } catch (Exception e) {
            LOGGER.error("An error is occurred in stack creation phase.");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 
     * @param stackName
     * @param endpoint
     * @param tenant
     * @param user
     * @param password
     * @param region
     * @param first
     * @param m
     * @return
     * 
     * @author gtricomi
     */
    public HashMap<String, ArrayList<Port>> sendShutSignalStack4DeployAction(String stackName, OpenstackInfoContainer credential,
            boolean first, DBMongo m) {
        try {
            Registry myRegistry = LocateRegistry.getRegistry(ip,port);
            RMIServerInterface impl = (RMIServerInterface) myRegistry.lookup("myMessage");
            ArrayList resources =impl.getListResource(credential.getEndpoint(), credential.getUser(), credential.getTenant(),credential.getPassword(), stackName);
            boolean continua=true;
            NovaTest nova = new NovaTest(credential.getEndpoint(), credential.getTenant(), credential.getUser(), credential.getPassword(), credential.getRegion());
            NeutronTest neutron = new NeutronTest(credential.getEndpoint(), credential.getTenant(), credential.getUser(), credential.getPassword(), credential.getRegion());
            HashMap<String, ArrayList<Port>> mapResNet = new HashMap<String, ArrayList<Port>>();
            Iterator it_res = resources.iterator();
            while (it_res.hasNext()) {
                          String id_res = (String) it_res.next();
                          LOGGER.debug("nome risorsaa "+id_res);
                    if (!first) {
                        nova.stopVm(id_res);
                        m.updateStateRunTimeInfo(credential.getTenant(), id_res, first);
                    }
                    ArrayList<Port> arPort = neutron.getPortFromDeviceId(id_res);
                    //inserire in quest'array la lista delle porte di quella VM
                    mapResNet.put(id_res, arPort);
                    Iterator it_po = arPort.iterator();
                    while (it_po.hasNext()) {
                        LOGGER.debug("insert port");
                        m.insertPortInfo(credential.getTenant(), neutron.portToString((Port) it_po.next()));
                    }
                }
            System.out.println("map res "+mapResNet.size());
            return mapResNet;

        } catch (Exception e) {

            LOGGER.error("An error is occurred in stack creation phase.");
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Function invoked in order to istantiate a stack from a manifest in all the federated cloud involved in federation for tenant deployer.
     * @param template
     * @param stack
     * @param tmpMapcred
     * @param tmpMap
     * @param m
     * @return 
     * @author gtricomi
     */
    public ArrayList<ArrayList<HashMap<String, ArrayList<Port>>>> deployManifest(
            String template,
            String stack,
            HashMap<String, ArrayList<ArrayList<OpenstackInfoContainer>>> tmpMapcred,
            HashMap<String,ArrayList<ArrayList<String>>> tmpMap,
            DBMongo m
    ){
        String stackName = stack.substring(stack.lastIndexOf("_") + 1 > 0 ? stack.lastIndexOf("_") + 1 : 0, stack.lastIndexOf(".yaml") >= 0 ? stack.lastIndexOf(".yaml") : stack.length());
        ArrayList arDC = (ArrayList<ArrayList<String>>) tmpMap.get(stackName);
        ArrayList arCr = (ArrayList<ArrayList<OpenstackInfoContainer>>) tmpMapcred.get(stackName);
        ArrayList<ArrayList<HashMap<String, ArrayList<Port>>>> arMapRes = new ArrayList<>();

        boolean skip = false, first = true;
        int arindex = 0;
        while (!skip) {
            ArrayList tmpArDC = (ArrayList<String>) arDC.get(arindex);
            ArrayList tmpArCr = (ArrayList<OpenstackInfoContainer>) arCr.get(arindex);
            ArrayList<HashMap<String, ArrayList<Port>>> arRes = new ArrayList<HashMap<String, ArrayList<Port>>>();
            //System.out.println("&&&&&&&&&&&&&&&&&&&&&"+tmpArCr.size());
            for (Object tmpArCrob : tmpArCr) {
                LOGGER.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\nISTANTION PHASE FOR THE CLOUD:" + ((OpenstackInfoContainer) tmpArCrob).getIdCloud() + "\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                boolean result = this.stackInstantiate(template, (OpenstackInfoContainer) tmpArCrob, m, stackName);//BEACON>>> in final version of OSFFM 
                LOGGER.debug("TEMPLATE ISTANTIATED ON CLOUD:" + ((OpenstackInfoContainer) tmpArCrob).getIdCloud());
                //we will use variable result to understand if the stack is deployed inside the federated cloud
                
                String region = "RegionOne";
                ((OpenstackInfoContainer) tmpArCrob).setRegion(region);
                HashMap<String, ArrayList<Port>> map_res_port = this.sendShutSignalStack4DeployAction(stackName, (OpenstackInfoContainer) tmpArCrob, first, m);
                if (true) {//result) {
                    first = false;//if first stack creation is successfully completed, the other stacks instantiated are not the First
                }                        //and need different treatment.
                arRes.add(map_res_port);
            }
            arindex++;
            arMapRes.add(arRes);
            if (arindex > tmpArCr.size()) {
                skip = true;
            }
        }
        return arMapRes;
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Management function for ServiceGroup">
    /**
     * This function returns the set of ServiceGroup(stack) name described inside manifest
     * and stored inside ManifestManager.
     * @param manifestName
     * @return Set, set of stack name described inside manifest. 
     */
    public Set<String> getSGList(String manifestName){
        ManifestManager mm=(ManifestManager)OrchestrationManager.mapManifestThr.get(manifestName);
        Set s=mm.serGr_table.keySet();
        return s;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Networks Management function">
    /**
     * 
     * @param fu
     * @param OSF_network_segment_id
     * @param params
     * @return 
     * @author gtricomi
     */
    public JSONObject networkSegmentAdd(String federationTenant,FederationUser fu,String OSF_network_segment_id,String OSF_cloud,HashMap params) throws Exception{
      //invocare qui funzioni del FederationActionManager
        FederationActionManager fam=new FederationActionManager();
        return fam.networkSegmentAdd(fu, OSF_network_segment_id,OSF_cloud,params,federationTenant);
    }
    
    public void prepareNetTables4completeSharing(
            String tenantname,
            String template,
            HashMap<String, ArrayList<ArrayList<OpenstackInfoContainer>>> tmpMapcred,
            DBMongo m
    ){
        //1 Retrieve NetMap version from Mongo for each DC and create LinkedHashMap for FederationActionManager
        FednetsLink mapcontainer=this.retrieveTablesStored(tenantname,tmpMapcred,m);
        //2 create new NetTables throught ?????? >>>>>>>>this action is forwarded to FederactionActionManager
        FederationActionManager fam=new FederationActionManager();
        if(template.equals(""))
            fam.prepareNetTables4completeSharing( tenantname,mapcontainer,tmpMapcred,m,false);
        else
            fam.prepareNetTables4completeSharing( tenantname,mapcontainer,tmpMapcred,m,true);
    }
    
    /**
     * This function retrieve NetTablesStored for all Datacenter involved. 
     * @param tenantname
     * @param tmpMapcred
     * @param m
     * @return 
     * @author gtricomi
     */
    private FednetsLink retrieveTablesStored(
            String tenantname,
            HashMap<String, ArrayList<ArrayList<OpenstackInfoContainer>>> tmpMapcred,
            DBMongo m){
        LinkedHashMap<String,JSONObject> netTablesMap= new LinkedHashMap<>();
        LinkedHashMap<String,JSONObject> siteTablesMap= new LinkedHashMap<>();
        LinkedHashMap<String,JSONObject> tenantTablesMap= new LinkedHashMap<>();
        Set<String> s=tmpMapcred.keySet();
        for(String stack : s){
            ArrayList arCr = (ArrayList<ArrayList<OpenstackInfoContainer>>) tmpMapcred.get(stack);
            boolean skip = false;
            int arindex = 0;
            while (!skip) {
                ArrayList<OpenstackInfoContainer> tmpArCr = (ArrayList<OpenstackInfoContainer>) arCr.get(arindex);
                for (OpenstackInfoContainer tmpArCrob : tmpArCr) {
                    if((!netTablesMap.containsKey(tmpArCrob.getIdCloud()))||(netTablesMap.get(tmpArCrob.getIdCloud())==null))
                    {
                        try {
                            netTablesMap.put(tmpArCrob.getIdCloud(),new JSONObject(m.getNetTables(tenantname,tmpArCrob.getIdCloud())));
                            
                        } catch (JSONException ex) {
                            LOGGER.error("Impossible parse NetTables for cloud :"+tmpArCrob.getIdCloud()+".\nException obtained:"+ex.getMessage());
                            netTablesMap.put(tmpArCrob.getIdCloud(),null);
                        }
                    }
                    if((!siteTablesMap.containsKey(tmpArCrob.getIdCloud()))||(siteTablesMap.get(tmpArCrob.getIdCloud())==null))
                    {
                        try {
                            siteTablesMap.put(tmpArCrob.getIdCloud(),new JSONObject(m.getSiteTables(tenantname,tmpArCrob.getIdCloud())));
                            
                        } catch (JSONException ex) {
                            LOGGER.error("Impossible parse SiteTables for cloud :"+tmpArCrob.getIdCloud()+".\nException obtained:"+ex.getMessage());
                            siteTablesMap.put(tmpArCrob.getIdCloud(),null);
                        }
                    }
                    if((!tenantTablesMap.containsKey(tmpArCrob.getIdCloud()))||(tenantTablesMap.get(tmpArCrob.getIdCloud())==null))
                    {
                        try {
                            tenantTablesMap.put(tmpArCrob.getIdCloud(),new JSONObject(m.getTenantTables(tenantname,tmpArCrob.getIdCloud())));
                            
                        } catch (JSONException ex) {
                            LOGGER.error("Impossible parse tenantTables for cloud :"+tmpArCrob.getIdCloud()+".\nException obtained:"+ex.getMessage());
                            tenantTablesMap.put(tmpArCrob.getIdCloud(),null);
                        }
                    }
                }
            }
        }
        FednetsLink f=new FednetsLink();
        f.setOldtenantTablesMap(tenantTablesMap);
        f.setOldsiteTablesMap(siteTablesMap);
        f.setOldnetTablesMap(netTablesMap);
        
        return f;
    }
    //</editor-fold>
    
}


/*

*/