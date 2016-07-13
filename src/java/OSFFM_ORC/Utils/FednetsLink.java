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

package OSFFM_ORC.Utils;

import JClouds_Adapter.KeystoneTest;
import JClouds_Adapter.OpenstackInfoContainer;
import MDBInt.FederationAgentInfo;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 *
 * @author Giuseppe Tricomi
 */
public class FednetsLink {
    private LinkedHashMap<String, LinkedHashMap<String,SortedSet<String>>> linkedVMs; //structure prepared for next works
    private LinkedHashSet<String> dcInFednet;
    private LinkedHashMap<String,KeystoneTest> kMcloudId_To_Keystone;
    private LinkedHashMap<String,OpenstackInfoContainer>cloudId_To_OIC;
    private LinkedHashMap<String,FederationAgentInfo>endpoint_To_FAInfo;
    private LinkedHashMap<String,String> endpoint_to_tenantid;
    
    private LinkedHashMap<String,JSONObject> OldnetTablesMap;
    private LinkedHashMap<String,JSONObject> OldsiteTablesMap;
    private LinkedHashMap<String,JSONObject> OldtenantTablesMap;
    
    Logger LOGGER = Logger.getLogger(FednetsLink.class);
    
    //<editor-fold defaultstate="collapsed" desc="OldnetTablesMap Maanagement Functions">
    public LinkedHashMap<String, JSONObject> getOldnetTablesMap() {
        return OldnetTablesMap;
    }

    public void setOldnetTablesMap(LinkedHashMap<String, JSONObject> OldnetTablesMap) {
        this.OldnetTablesMap = OldnetTablesMap;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="OldsiteTablesMap Maanagement Functions">
    public LinkedHashMap<String, JSONObject> getOldsiteTablesMap() {
        return OldsiteTablesMap;
    }

    public void setOldsiteTablesMap(LinkedHashMap<String, JSONObject> OldsiteTablesMap) {
        this.OldsiteTablesMap = OldsiteTablesMap;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="OldtenantTablesMap Maanagement Functions">
    public LinkedHashMap<String, JSONObject> getOldtenantTablesMap() {
        return OldtenantTablesMap;
    }

    public void setOldtenantTablesMap(LinkedHashMap<String, JSONObject> OldtenantTablesMap) {
        this.OldtenantTablesMap = OldtenantTablesMap;
    }
    //</editor-fold>
    
    
    
    
    //<editor-fold defaultstate="collapsed" desc="endpoint_to_tenantid Management Functions">
    public LinkedHashMap<String, String> getEndpoint_to_tenantid() {
        return endpoint_to_tenantid;
    }

    public void setEndpoint_to_tenantid(LinkedHashMap<String, String> endpoint_to_tenantid) {
        this.endpoint_to_tenantid = endpoint_to_tenantid;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="KeystoneInfo Maanagement Functions">
    public LinkedHashMap<String, KeystoneTest> getkMcloudId_To_Keystone() {
        return kMcloudId_To_Keystone;
    }

    public void setkMcloudId_To_Keystone(LinkedHashMap<String, KeystoneTest> kMcloudId_To_Keystone) {
        this.kMcloudId_To_Keystone = kMcloudId_To_Keystone;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="OpenstackInfoContainer Management Functions">
    public LinkedHashMap<String, OpenstackInfoContainer> getCloudId_To_OIC() {
        return cloudId_To_OIC;
    }

    public void setCloudId_To_OIC(LinkedHashMap<String, OpenstackInfoContainer> cloudId_To_OIC) {
        this.cloudId_To_OIC = cloudId_To_OIC;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="FederationAgentInfo Maanagement Functions">
    public LinkedHashMap<String, FederationAgentInfo> getEndpoint_To_FAInfo() {
        return endpoint_To_FAInfo;
    }

    public void setEndpoint_To_FAInfo(LinkedHashMap<String, FederationAgentInfo> endpoint_To_FAInfo) {
        this.endpoint_To_FAInfo = endpoint_To_FAInfo;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="LinkedVMs Management Functions">
    public LinkedHashMap<String,  LinkedHashMap<String,SortedSet<String>>> getLinkedVMs() throws NullPointerException{
        if(this.linkedVMs==null)
            throw new NullPointerException();
        return linkedVMs;
    }
    
    /**
     * This function it will be used to add element inside this object in order to
     * prepare take from manifest all elements that will be connected. 
     * @param stack
     * @param set
     * @param linkedVM
     * @return 
     */
    public boolean createLinkedVMs(String stack, String set,String linkedVM){
        if(this.linkedVMs==null)
            this.linkedVMs=new LinkedHashMap<>();
        try{
            if(this.linkedVMs.containsKey(stack))
            {
                if(this.linkedVMs.get(stack).containsValue(set))
                {
                    if(!this.linkedVMs.get(stack).get(set).contains(linkedVM))
                        this.addNewItem(stack, set, linkedVM);
                }
                else{
                    this.addNewSet(stack, set);
                    this.addNewItem(stack, set, linkedVM);
                }
            }
            else{
                this.addNewStack(stack);
                this.addNewSet(stack, set);
                this.addNewItem(stack, set, linkedVM);
            }
        }
        catch(Exception e){
            LOGGER.error("Exception occurred in FedNetsLink createLinkedVMs!\n"+e.getMessage());
            return false;
        }
        return true;
    }
    
    private void addNewStack(String stack){
        this.linkedVMs.put(stack,new  LinkedHashMap<String,SortedSet<String>>());
    } 
    
    private void addNewSet(String stack,String set){
        this.linkedVMs.get(stack).put(set, new TreeSet());
    }
    private void addNewItem(String stack,String set,String linkedVM){
        this.linkedVMs.get(stack).get(set).add(linkedVM);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="DatacenterTree Maanagement Functions">
    public LinkedHashSet<String> getDcInFednet()throws NullPointerException {
        if(this.dcInFednet==null)
            throw new NullPointerException();
        return dcInFednet;
    }
    
    public void addDcInFednet(String dc){
        if(this.dcInFednet==null)
            this.dcInFednet=new LinkedHashSet<>();
        this.dcInFednet.add(dc);
    }
    
    //</editor-fold>
    
    public FednetsLink() {
        this.linkedVMs = null;
        this.dcInFednet= null;
    }
    
}
