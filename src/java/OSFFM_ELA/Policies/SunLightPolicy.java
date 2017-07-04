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

package OSFFM_ELA.Policies;

import JClouds_Adapter.NovaTest;
import JClouds_Adapter.OpenstackInfoContainer;
import MDBInt.DBMongo;
import MDBInt.MDBIException;
import OSFFM_ELA.ElasticityPolicyException;
import OSFFM_ELA.Policy;
import OSFFM_ORC.OrchestrationManager;
import com.google.common.collect.HashBiMap;
import java.io.File;
import java.time.Clock;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.json.JSONException;
import org.json.JSONObject;
import utils.ParserXML;

/**
 * The objective of this class is implement the policy based on SunLight position. 
 * In few words who select this policy, choose to have activated VMs where there is sun-light.
 * @author Giuseppe Tricomi
 */
public class SunLightPolicy implements Policy,Runnable{
    
    final Logger LOGGER = Logger.getLogger(SunLightPolicy.class);
    private String fileConf="/webapps/OSFFM/WEB-INF/configuration_SunLightPolicy.xml";//this path starts from the tomcat home
    private long granularityCheck=20000;//3600000;//default value is 1 hour
    private int actualDCGap;
    private int threshold=17;//default value is 17 (5pm)
    private DBMongo mongo;
    private String tenant,userFederation,pswFederation;
    private HashMap<String,ArrayList<String>> datacenterMap;
    private int minimumGap=-8;//default value is -8 hours
    private ArrayList<String> monitoredVM;
    private OrchestrationManager om;
    private HashMap<String,Boolean> index;
    private String firstCloudID;
    private String templateName, stack;
    
    
    public SunLightPolicy(HashMap<String,Object> paramsMap)throws ElasticityPolicyException {
             //paramsMap.get(this) // I need to understand which parameters need here
        this.tenant=(String)paramsMap.get("tenantName");
        this.mongo=(DBMongo)paramsMap.get("mongoConnector");
        this.om=(OrchestrationManager)paramsMap.get("OrchestrationManager");
        this.userFederation=(String)paramsMap.get("userFederation");
        this.pswFederation=(String)paramsMap.get("pswFederation");
        String tmpminimumGap=(String)paramsMap.get("minimumGap");
        this.minimumGap=Integer.parseInt(tmpminimumGap);
        this.firstCloudID=(String)paramsMap.get("firstCloudID");
        this.templateName= (String)paramsMap.get("templateName");
        this.stack=(String)paramsMap.get("stack");
        this.monitoredVM=new ArrayList<String>();
        this.init();
        this.constructDCMap((ArrayList<ArrayList<String>>)paramsMap.get("dcList"));
    }
    
    public void init(){
        Element params;
        this.datacenterMap=new HashMap<String,ArrayList<String>>();
        try {
        /*String file=System.getenv("HOME");
        ParserXML parser = new ParserXML(new File(file+fileConf));
        params = parser.getRootElement().getChild("pluginParams");
        this.granularityCheck = Long.parseLong(params.getChildText("granularityCheck"));
        this.threshold =Integer.parseInt(params.getChildText("threshold"));*/
        //this.minimumGap=Integer.parseInt(params.getChildText("minimumGap"));
                
        } 
        catch (Exception ex) {
            LOGGER.error("Error occurred in configuration ");
            ex.printStackTrace();
        }
    }   

    private void constructDCMap(ArrayList<ArrayList<String>> dc)throws ElasticityPolicyException{
        for(ArrayList<String> row : dc){
            for(String column :row){
                try{
                    JSONObject infoJSON=new JSONObject(column);
                    String cloudTarget=infoJSON.getString("cloudId");
                    JSONObject json=new JSONObject(this.mongo.getDatacenter(this.tenant,cloudTarget ));
                    String gapIndex=(String)json.get("gap");
                    ArrayList<String> artmp=null;
                    if(!this.datacenterMap.containsKey(gapIndex)){
                        artmp=new ArrayList<String>();
                        artmp.add(column);
                    }
                    else{
                        artmp=this.datacenterMap.get(gapIndex);
                        artmp.add(column);
                    }
                    this.datacenterMap.put(gapIndex,artmp);
                }
                catch(JSONException je){
                    LOGGER.error("Impossible manage the Datacenter information Stored on MongoDb for the Tenant "+this.tenant+"; Datacenter information doesn't contain gap info for Datacenter!");
                    throw new ElasticityPolicyException("Impossible manage the Datacenter information Stored on MongoDb for the Tenant "+this.tenant+"; Datacenter information doesn't contain gap info for Datacenter!\n"+je);
                }
                catch(MDBIException me){
                    LOGGER.error("Impossible manage the Datacenter information Stored on MongoDb for the Tenant "+this.tenant+"; It's Impossible accede to Datacenter info for selected DC:"+column);
                    throw new ElasticityPolicyException("Impossible manage the Datacenter information Stored on MongoDb for the Tenant "+this.tenant+"; It's Impossible accede to Datacenter info for selected DC:"+column+"\n"+me);
                }
            }
        }
    }
    
    
    
    /**
     * The mission of this function is identify the moments when a VM or a group of VM needs to be "migrated"(shuttedoff in one site and activated in another
     * selected by selectNewDatacenter function).
     * @param params 
     */
    @Override
    public void migrationAlertManager(HashMap params){
        HashMap elem = this.selectNewDatacenter(null);
        String tmpDCID=(String)elem.get("dcID");
        try{
        if(tmpDCID!=null){
            ArrayList<String> newMonitoredVMs=new ArrayList<String>();
            for (String targetVM : this.monitoredVM) {
                
                JSONObject jo=new JSONObject(tmpDCID);
                String twinVM = this.mongo.findResourceMate(this.tenant, targetVM, jo.getString("cloudId"));
                System.out.println("TwinVM: "+twinVM);
                if (twinVM == null) {
                    LOGGER.error("Something going wrong it's imppossible find a twinVM for: " + targetVM + ".The migration for that VM is aborted!");
                    newMonitoredVMs.add(targetVM);
                    continue;
                } else {
                    System.out.println("TwinVM FOUND");
                    HashMap<String, Object> param = new HashMap<String, Object>();
                    param.put("vm2shut", targetVM);
                    String twinVMUUID=(new JSONObject(twinVM)).getString("phisicalResourceId");
                    param.put("vm2Act", twinVMUUID);
                    if (!this.moveVM(param)) {
                        LOGGER.error("error occurred in migration VM " + targetVM);//sistemare qst logger
                        newMonitoredVMs.add(targetVM);
                        
                    }
                    newMonitoredVMs.add(twinVMUUID);
                }
            }
            this.actualDCGap=(Integer)elem.get("newgap");
            this.monitoredVM=newMonitoredVMs;
        }
        else{
            LOGGER.error("Something going wrong it's impossible find a twinVM for VMs monitored.The migration aborted");
        }
        }catch(JSONException je){
           LOGGER.error("Something going wrong it's impossible parse Datacenter inofo JSON");
       }
    }
    /**
     * This function is based on the workflow of the sunlight demo for beacon.
     * All tenant in an Openstack cloud in federation is a clone of the other instance of the tenant, then it has deployed on it the same VM, 
     * and for each VM monitored we can found a twinVM inside the DC chose from the algorithm.
     * @param val
     * @return 
     */
    @Override
    public HashMap selectNewDatacenter(Integer val) {
        try {
            String tmpGap="";
            HashMap<String,Object> element=new HashMap<String,Object>();
            Integer searchedGap;
            if (val == null) {
                searchedGap = this.actualDCGap + minimumGap;
            } else {
                searchedGap = val - 1;
            }
            if ((searchedGap < -12) || (searchedGap > 14)) {
                if (searchedGap < -12) {
                    searchedGap = searchedGap + 24;
                } else {
                    searchedGap = searchedGap - 24;
                }
            }
            if (index != null) {
                if (!index.containsKey(searchedGap.toString())) {
                    System.out.println("QUIT" + searchedGap + this.firstCloudID);
                    index.put(searchedGap.toString(), Boolean.FALSE);

                } else {
                    return null;
                }
            } else {
                index = new HashMap<String, Boolean>();
                index.put(searchedGap.toString(), Boolean.FALSE);
            }
            if (searchedGap > 0) {
                tmpGap = "+" + searchedGap.toString();
            } else {
                tmpGap = searchedGap.toString();
            }
            if (this.datacenterMap.containsKey(tmpGap)) {
                ArrayList<String> ar = this.datacenterMap.get(tmpGap);//Take array and and findcorrect DC
                Iterator i = ar.iterator();
                boolean end = false;
                while (i.hasNext() && (!end)) {
                    String tmpDCID = (String) i.next();
                    //07/09/2016 basandosi sulla sunlight demo di BEACON basta identificare il DC su cui è presente una VM del gruppo e tutte le altre saranno spostate(attivate) di conseguenza
                    ////in alternativa si dovrebbe prendere il datacenter adatto per ogni VM da monitorare
                    for (String targetVM : this.monitoredVM) {
                        JSONObject jo=new JSONObject(tmpDCID);
                        String twinVM = this.mongo.findResourceMate(this.tenant, targetVM, jo.getString("cloudId"));
                        if (twinVM == null) {
                            break;
                        } else {
                            end = true;
                            element.put("dcID", tmpDCID);
                            element.put("newgap", searchedGap);
                            index.replace(tmpGap,true);
                            return element;
                        }
                    }
                    if (!end) {
                        LOGGER.error("Something going wrong it's impossible find a twinVM for VMs monitored.The migration for that VM is moved on another DC");
                        index.put(searchedGap.toString(), false);
                        return this.selectNewDatacenter(searchedGap);
                    }
                }
            } else {
                return this.selectNewDatacenter(searchedGap);
            }
        } catch (Exception e) {
            System.err.println("EXCEPTION" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    
    /**
     * This function is used to move a VM
     * @param params
     * @return 
     */
    public boolean moveVM(HashMap params){
        //this function have to invoke OrchestrationManager.migrationProcedure
        try {
            // this.om.migrationProcedure((String)params.get("vm2shut"), this.tenant, this.userFederation, (String)params.get("vm2Act"), this.pswFederation, this.mongo, "RegionOne");//BEACON>>> Region field need to be managed?
            //System.out.println("I WILL MIGRATE VM : " + (String) params.get("vm2shut"));
            this.migrationProcedure((String) params.get("vm2shut"), this.tenant, this.userFederation, (String) params.get("vm2Act"), this.pswFederation, "RegionOne");//BEACON>>> Region field need to be managed?
            //System.out.println("I HAVE ACTIVATED THE TWINVM ");
        } catch (Exception e) {
            LOGGER.error("Error occurred in moveVM:" + e.getMessage());
            return false;
        }
        return true;
    }
    
    
    private void takeMonVMs(){
        try{
            ArrayList<String> tmp=this.mongo.getRunTimeInfos(this.tenant,this.firstCloudID , this.templateName, this.stack);
            for(String obj:tmp){
                JSONObject jo=new JSONObject(obj);
                this.monitoredVM.add((String)jo.get("phisicalResourceId"));
            }
        }
        catch(Exception e){
            LOGGER.error("Something going wrong it's create monitoredVM ArrayList.");
        }
    }
    
    /**
     * Take from Mongo the DCGap for selected Datacenter.
     */
    private void getDCtimeGap() {
        Integer dcgap=+0;
        try{
            JSONObject json=new JSONObject(this.mongo.getDatacenter(this.tenant,this.firstCloudID ));
            String gapIndex=(String)json.get("gap");
            dcgap=Integer.parseInt(gapIndex);
        }
                catch(JSONException je){
                    LOGGER.error("Impossible manage the Datacenter information Stored on MongoDb for the Tenant "+this.tenant+"; Datacenter information doesn't contain gap info for Datacenter!");
                //    throw new ElasticityPolicyException("Impossible manage the Datacenter information Stored on MongoDb for the Tenant "+this.tenant+"; Datacenter information doesn't contain gap info for Datacenter!\n"+je);
                }
                catch(MDBIException me){
                    LOGGER.error("Impossible manage the Datacenter information Stored on MongoDb for the Tenant "+this.tenant+"; It's Impossible accede to Datacenter info for selected DC:");
                 //   throw new ElasticityPolicyExceptionslp("Impossible manage the Datacenter information Stored on MongoDb for the Tenant "+this.tenant+"; It's Impossible accede to Datacenter info for selected DC:\n"+me);
                }
        this.actualDCGap=dcgap;
    }
    
    
    /**
     * This Stars and verify if the UTCtime+Datacenter gap is greater of the
     * threshold. Positive answer begin migration of the monitored VM, negative
     * send thread in sleepmode for granularityCheck milliseconds.
     */
    @Override
    public synchronized void run() {
        boolean stop = true;
        this.takeMonVMs();
        while (stop) {
            this.getDCtimeGap();
            Clock clock = Clock.systemUTC();
            LocalTime osffmTime = LocalTime.now(clock);
            System.out.println("checked time in : " + this.firstCloudID + " is " + osffmTime.getHour() + this.actualDCGap);
            //System.out.println(osffmTime.getHour()+this.actualDCGap);
            //System.out.println(osffmTime.getHour()+this.actualDCGap>this.threshold);
            if ((osffmTime.getHour() + this.actualDCGap) > (this.threshold + this.actualDCGap)) {
                //StartMigration
                System.out.println("I am starting migration");
                this.migrationAlertManager(null);
                /*         JSONObject j;
                String targetVM="";
                String twinVM="";
                try {
                    j = new JSONObject(this.mongo.getRunTimeInfo(this.tenant, "CETIC", this.templateName, this.stack));
                    targetVM=j.getString("phisicalResourceId");
                    j= new JSONObject(this.mongo.findResourceMate(this.tenant, targetVM,"UME"));
                    twinVM= j.getString("phisicalResourceId");
                } catch (JSONException ex) {
                    java.util.logging.Logger.getLogger(SunLightPolicy.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                System.out.println("target and twin: "+targetVM+" "+twinVM);
                HashMap param=new HashMap();
                param.put("vm2shut", targetVM);
                    param.put("vm2Act", twinVM);
                this.moveVM(param);
                stop=false;
                 */
            }
            try {
                Thread.sleep(this.granularityCheck);
            } catch (InterruptedException ex) {
                LOGGER.error("InterruptedException with Thread.sleep inside a sunlight policy Thread!" + ex.getMessage());
                stop=false;
            }
        }
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
    public void migrationProcedure(String vm,String tenant,String userFederation,String vmTwin,String pswFederation,String region){
        //spegnimento vm
        ////recupero runtimeinfo
        String runTime=mongo.getRunTimeInfo(tenant, vm);
        String idClo="",endpoint="",cred="", twinUUID="";
        OpenstackInfoContainer credential=null,credential2=null;
        JSONObject credJobj=null,runJobj=null;
        try{
            runJobj=new JSONObject(runTime);
            ////recupero idCloud
            idClo=runJobj.getString("idCloud");
            ////recupero le credenziali passando da quelle di federazione
            cred=mongo.getFederatedCredential(tenant, userFederation, pswFederation,idClo);
            credJobj=new JSONObject(cred);
            try {
                endpoint=(new JSONObject(mongo.getDatacenter(tenant, idClo))).getString("idmEndpoint");
            } catch (MDBIException ex) {
                LOGGER.error( ex);
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
        try{
            twinUUID=vmTwin;
            ////recupero runtimeinfo
            runTime=mongo.getRunTimeInfo(tenant, twinUUID);
            runJobj=new JSONObject(runTime);
            ////recupero idCloud
            idClo=runJobj.getString("idCloud");
            ////recupero le credenziali passando da quelle di federazione
            cred=mongo.getFederatedCredential(tenant, userFederation, pswFederation,idClo);
            credJobj=new JSONObject(cred);
            try {
                endpoint=(new JSONObject(mongo.getDatacenter(tenant, idClo))).getString("idmEndpoint");
            } catch (MDBIException ex) {
               LOGGER.error(ex);
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
        Iterator it_tmpar=mongo.getportinfoes(tenant, vm).iterator();
        while(it_tmpar.hasNext())
            LOGGER.debug((String)it_tmpar.next());
        LOGGER.debug("Network infoes of the twin VM started(identified by UUID:"+twinUUID+"):");
        it_tmpar=mongo.getportinfoes(tenant, twinUUID).iterator();
        while(it_tmpar.hasNext())
            LOGGER.debug((String)it_tmpar.next());
    }
    
}
