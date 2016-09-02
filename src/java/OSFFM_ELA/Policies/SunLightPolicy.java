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

import MDBInt.DBMongo;
import MDBInt.MDBIException;
import OSFFM_ELA.ElasticityPolicyException;
import OSFFM_ELA.Policy;
import com.google.common.collect.HashBiMap;
import java.io.File;
import java.time.Clock;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    private long granularityCheck=3600000;//default value is 1 hour
    private int actualDCGap;
    private int threshold=19;//default value is 19 (7pm)
    private DBMongo mongo;
    private String tenant;
    private HashBiMap<String,ArrayList> datacenterMap;
    private int minimumGap=-8;//default value is -8 hours
    
    public SunLightPolicy(HashMap<String,Object> paramsMap)throws ElasticityPolicyException {
        //paramsMap.get(this) // I need to understand which parameters need here
        this.tenant=(String)paramsMap.get("tenantName");
        this.constructDCMap((ArrayList<ArrayList<String>>)paramsMap.get("dcList"));
        this.mongo=(DBMongo)paramsMap.get("mongoConnector");
    }
    
    public void init(){
        Element params;
        try {
        String file=System.getenv("HOME");
        ParserXML parser = new ParserXML(new File(file+fileConf));
        params = parser.getRootElement().getChild("pluginParams");
        this.granularityCheck = Long.parseLong(params.getChildText("granularityCheck"));
        this.threshold =Integer.parseInt(params.getChildText("threshold"));
        this.minimumGap=Integer.parseInt(params.getChildText("minimumGap"));
                
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
                    JSONObject json=new JSONObject(this.mongo.getDatacenter(this.tenant, column));
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
    
    @Override
    public String selectNewDatacenter(int val){
        int searchedGap=0;
        if (val == 0)
            searchedGap = this.actualDCGap + minimumGap;
        else
            searchedGap = val - 1;
        if ((searchedGap < -12) || (searchedGap > 14)) {
            if (searchedGap < -12) {
                searchedGap = searchedGap + 24;
            } else {
                searchedGap = searchedGap - 24;
            }
        }
        if (this.datacenterMap.containsKey(searchedGap)) {
            //prendi array e trova il DC corretto
            return null;
        } else {
            return this.selectNewDatacenter(searchedGap);
        }
    }
    
    
    
    /**
     * The mission of this function is identify the moments when a VM or a group of VM needs to be "migrated"(shuttedoff in one site and activated in another
     * selected by selectNewDatacenter function).
     * @param params 
     */
    @Override
    public void migrationAlertManager(HashBiMap params){
        int gap;
        //ArrayList<ArrayList<String>> Datacenters  ---------> o meglio qualcosa che mi permetta di risalire a questa lista
        
        //before exit change internal information of the Actual DC whit the new datacenter infos.
    }
    
    
    
    /**
     * Take from Mongo the DCGap for selected Datacenter.
     */
    private void getDCtimeGap(){
        int dcgap=+0;
        //take from MongoDB DCgap info field
        this.actualDCGap=dcgap;
    }
    
    
    /**
     * This Stars and verify if the UTCtime+Datacenter gap is greater of the threshold.
     * Positive answer begin migration of the monitored VM, negative send thread in sleepmode for granularityCheck milliseconds.
     */
    @Override
    public synchronized void run(){
        boolean stop=false;
        while (stop) {
            this.getDCtimeGap();
            Clock clock = Clock.systemUTC();
            LocalTime osffmTime=LocalTime.now(clock);
            if(osffmTime.getHour()+this.actualDCGap>this.threshold){
                //StartMigration
                this.migrationAlertManager(null);
            }
            try {
                Thread.sleep(this.granularityCheck);
            } catch (InterruptedException ex) {
                LOGGER.error("InterruptedException with Thread.sleep inside a sunlight policy Thread!" + ex.getMessage());
            }
        }
    }
}
