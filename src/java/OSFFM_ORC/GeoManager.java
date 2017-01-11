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
//import MDBInt.DBMongo;
import OSFFM_ORC.Utils.Exception.NotFoundGeoRefException;
import OSFFM_ORC.Utils.MultiPolygon;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.json.JSONArray;
import org.json.JSONObject;

//</editor-fold>        
/**
 * This class is use to analyse "OS::Beacon::Georeferenced_deploy" Manifest fragment,
 * and to prepare the correct deploy instructions.
 * @author Giuseppe Tricomi
 */
public class GeoManager {
    /**
     * Constructor.
     */ 
    public GeoManager(){
        reference= new HashMap<String,ArrayList<MultiPolygon>>();
    }
    /**
     * This function returns georeference stored in reference HashMap or throws a 
     * NotFoundGeoRefException.
     * @param name
     * @param priority
     * @return
     * @throws NotFoundGeoRefException 
     */
    public String retrievegeoref(String name,int priority)throws NotFoundGeoRefException{
        String jsonPolygon="";
        MultiPolygon pol=this.get_shape(name, priority);
        if (pol==null)
            throw new NotFoundGeoRefException("Element OS::Beacon::Georeferenced_deploy named "+name+" with priority "+priority+"is not found inside GeoManager reference HashMap");
        else
            try{
                jsonPolygon=pol.toJSONString();
            }catch(Exception e){}
        return jsonPolygon;
    }
    
    /**
     * This function returns georeference stored in reference HashMap or throws a 
     * NotFoundGeoRefException.
     * @param name
     * @param priority
     * @return
     * @throws NotFoundGeoRefException 
     */
    public ArrayList<MultiPolygon> retrievegeoref(String name)throws NotFoundGeoRefException{
        if(this.reference.containsKey(name))
            return (ArrayList<MultiPolygon>)this.reference.get(name);
        else
            throw new NotFoundGeoRefException("Element OS::Beacon::Georeferenced_deploy named "+name+"is not found inside GeoManager reference HashMap");
    }
    /**
     * This is invoked to store and elaborate a "OS::Beacon::Georeferenced_deploy" resource.
     * 
     * @param name, name of the resource,
     * @param resource, LinkedHashMap<String,Object> element stored. 
     * @return boolean, True or False
     */
    public boolean consume_georeference(String name,org.json.JSONObject resource){
        //System.out.println("test");
        try{
            org.json.JSONArray elements=((org.json.JSONObject)resource.getJSONObject("properties")).getJSONArray("shapes");
            ArrayList<MultiPolygon> shapelist=new ArrayList<MultiPolygon>();
            for(int index =0;index<elements.length();index++){
                JSONObject shape=elements.getJSONObject(index);
                MultiPolygon pol=new MultiPolygon(shape);
                pol.setPriority(index);
                shapelist.add(pol);
            }
            this.add_shape(name,shapelist);
        }
        catch(Exception e){
            System.err.println("Exception occourred in consume_georeference"+e.getMessage());
            return false;
        }
        return true;
    }
    /**
     * Add inside internal Map the shape took from Manifest.
     * @param name
     * @param shape 
     */
    private void add_shape(String name,ArrayList<MultiPolygon> shapeArr){
        reference.put(name, shapeArr);
    }
    /**
     * This function returns the shape addressed by name, and by priority
     * @param name, represent ID of MultyPoligon
     * @param priority, represent priority for that shape, it is also the index of ArrayList<MultiPolygon> stored on reference
     * @return MultyPoligon or null if the MultyPoligon isn't present in reference Map
     */
    private MultiPolygon get_shape(String name,int priority){
        try{
            return this.reference.get(name).get(priority);
        }
        catch(Exception e){
            return null;
        }
    }
//<editor-fold defaultstate="collapsed" desc="Variable">
    private HashMap<String,ArrayList<MultiPolygon>> reference;
    //private HashMap<String,ArrayList> priority_reference;
//</editor-fold>
    
    
}
