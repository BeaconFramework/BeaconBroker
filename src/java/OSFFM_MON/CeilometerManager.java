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

package OSFFM_MON;

/**
 * This class is used to Manage interaction with ceilometer. 
 * Its activities are listed below: 
 *    - Create a Monitor for a resource, when the resource is created,
 *    - Open a socket (1 for all resource or 1 for each resource? Probably the first) and wait for an alerts coming from a federated cloud 
 *    - Extract resource information from alterts received and matching it to identify the correct federation resource (it will be done by an external element and not dirctly from this class) 
 * 
 * @author Giuseppe Tricomi
 */
public class CeilometerManager {
//<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Generic constructor does'nt do any particular action.
     */
    public CeilometerManager(){
        
    }
    
//</editor-fold>    
//<editor-fold defaultstate="collapsed" desc="internal function">     
    /**
     * Internal function used to find the URL for ceilometer instance of the cloud federated.
     * @param String federated tenant
     * @param String federated cloud
     * @return String representig federated Ceilometer.
     */
    private String retrieveTargetAddress(String tenant, String cloudId){
        String url="";
        //BEACON>>> qui andrà richiamata la funzione che recupera da MongoDB 
        return url;
    }
    
    /**
     * This function set an alarm on a ceilometer on federated OpenStack
     * @param tenant
     * @param cloudId
     * @param propertyMonitored
     * @return 
     */
    public boolean setAlarms(String tenant, String cloudId,Alarms4Resource propertyMonitored){
        String url=this.retrieveTargetAddress(tenant, cloudId);
        //BEACON>>>  qui andrebbe aggiunta la funzione di creazione 
           /*  Example of ceilometer alarm creation call found on internet
 ceilometer alarm-threshold-create --name cpu_hi \
  --description 'instance running hot' \
  --meter-name cpu_util --threshold 70.0 \
  --comparison-operator gt --statistic avg \
  --period 600 --evaluation-periods 3 \
  --alarm-action 'log://' \
  --query resource_id=INSTANCE_ID
    */
        return true;
    }
//</editor-fold>    
    
    
    
    
    
    
}
