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

package OSFFM_ELA;

import MDBInt.DBMongo;
import OSFFM_ORC.OrchestrationManager;
import org.apache.log4j.Logger;
/**
 * Simple elasticity module, It provides a function that simulate Vm suffer.
 * As answer for this event the VM it will be shutdown and start on another cloud.
 * @author Giuseppe Tricomi
 */
public class ElasticityManagerSimple {
     static final Logger LOGGER = Logger.getLogger(ElasticityManagerSimple.class);
    public ElasticityManagerSimple() {
        
    }
    
    /**
     * Simulate an issue with VM "vm", Ask to OrchestrationManager to stop it in cloud A, and active it in other cloud.
     * @param vm 
     */
    public void simulatesuffering(OrchestrationManager om, String vm,String tenant,String userFederation,String pswFederation,DBMongo m,int element,String region){
        
        om.sufferingProcedure(vm,tenant,userFederation,pswFederation,m,element,region);
    }
    
}
