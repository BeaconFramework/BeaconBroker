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

import java.util.LinkedHashMap;
import java.util.TreeSet;
import java.util.SortedSet;
import org.apache.log4j.Logger;

/**
 *
 * @author Giuseppe Tricomi
 */
public class FednetsLink {
    private LinkedHashMap<String, LinkedHashMap<String,SortedSet<String>>> linkedVMs;
    Logger LOGGER = Logger.getLogger(FednetsLink.class);
    
    public LinkedHashMap<String,  LinkedHashMap<String,SortedSet<String>>> getLinkedVMs() {
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
    
    public FednetsLink() {
        this.linkedVMs = new LinkedHashMap<String,LinkedHashMap<String,SortedSet<String>>>();
    }
    
}
