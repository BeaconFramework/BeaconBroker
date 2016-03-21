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

import java.util.HashMap;

/**
 * This class contains the property needed for alarm creation on federated Ceilometer.
 * @author Giuseppe Tricomi
 */
public class Alarms4Resource {
//<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Constructor for this class.
     * @param resourceID, it is a string that represent the ID of the monitored resource.
     * @param atyp, it is an element of an enumeration that is used to explain what kind of alarms it will be set.
     * @param timestamp, timestamp of last update
     * @param countername, name of the metric that will be defined
     * @param threshold, 
     * @param alarm_action. url where the OSFFM_MON is listening for incoming alarms
     * @param state,The initial alarm state (defaults to insufficient data).It could be a vaule between enum set for ceilometer API( ok, alarm, Insufficient Data) 
     * @param name, name of the alarm
     * @param enable,True if evaluation and actioning is to be enabled for this alarm (defaults to True).
     * @param repeat_action, True if actions should be repeatedly notified while the alarm remains in the target state (defaults to False).
     */
    public Alarms4Resource(
            boolean repeat_action,
            boolean enable,
            String name,
            String state,
            String resourceID,
            alarmsType atyp,
            String timestamp,
            String countername,
            String threshold,
            String alarm_action
    ){
        this.name= name;
        this.state= state; 
        this.resourceID=resourceID; 
        this.timestamp=timestamp;
        this.threshold=threshold;
        this.countername=countername;
        this.alarm_actions=alarm_actions;
    }
    
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Setter&Getter">
    public HashMap getMetadata() {
        return metadata;
    }

    public void setMetadata(HashMap metadata) {
        this.metadata = metadata;
    }

    public String getStatistic() {
        return statistic;
    }

    public void setStatistic(String statistic) {
        this.statistic = statistic;
    }

    public Integer getEvaluation_period() {
        return evaluation_period;
    }

    public void setEvaluation_period(Integer evaluation_period) {
        this.evaluation_period = evaluation_period;
    }

    public Float getAggregate_period() {
        return aggregate_period;
    }

    public void setAggregate_period(Float aggregate_period) {
        this.aggregate_period = aggregate_period;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatetimestamp() {
        return statetimestamp;
    }

    public void setStatetimestamp(String statetimestamp) {
        this.statetimestamp = statetimestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getResourceID() {
        return resourceID;
    }

    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public String getCountername() {
        return countername;
    }

    public void setCountername(String countername) {
        this.countername = countername;
    }

    public String getAlarm_actions() {
        return alarm_actions;
    }

    public void setAlarm_actions(String alarm_actions) {
        this.alarm_actions = alarm_actions;
    }

    public alarmsType getAtyp() {
        return atyp;
    }

    public void setAtyp(alarmsType atyp) {
        this.atyp = atyp;
    }
//</editor-fold>    
//<editor-fold defaultstate="collapsed" desc="Variable Defeinition">    
    //variable with default
    private HashMap metadata= new HashMap(); // the element inside metadata JSON used to match a sample or not "comparison_operator": "gt",
    private String statistic="avarage"; //this is an enum, for the moment I have not found the correct manual for our ceilometer version, then i don't know the enum value available
    private Integer evaluation_period= new Integer(1);
    private Float aggregate_period= new Float(0);
    private String description="No description provided for this alarm";
    private String statetimestamp=""; //this variable could be the same of the timestamp element
    //passed by constructor (no default)
    private String name;
    private String state; 
    private String resourceID; 
    private String timestamp;
    private String threshold;
    private String countername;
    private String alarm_actions;
    private alarmsType atyp;
    public enum alarmsType {ram,cpuUsage};//BEACON>>> the value could be changed with Ceilometer default value when we find it ion the documentation
//</editor-fold>    

    /*  Example of ceilometer alarm creation call found on internet
 ceilometer alarm-threshold-create --name cpu_hi \
  --description 'instance running hot' \
  --meter-name cpu_util --threshold 70.0 \
  --comparison-operator gt --statistic avg \
  --period 600 --evaluation-periods 3 \
  --alarm-action 'log://' \
  --query resource_id=INSTANCE_ID
    */
}
