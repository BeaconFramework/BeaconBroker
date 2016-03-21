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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.json.JSONObject;

/**
 *
 * @author Giuseppe Tricomi
 */
public class MultiPolygon {
//<editor-fold defaultstate="collapsed" desc="Variable&Setter/Getter">    
    private String type;
    private String id;
    private org.json.JSONObject properties;
    private org.json.JSONObject geometry;
    private int priority;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public org.json.JSONObject getProperties() {
        return properties;
    }

    public void setProperties(org.json.JSONObject properties) {
        this.properties = properties;
    }

    public org.json.JSONObject getGeometry() {
        return geometry;
    }

    public void setGeometry(org.json.JSONObject geometry) {
        this.geometry = geometry;
    }
  
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Constructor">    
    /**
     * Public constructor for element Polygon, It take a LinkedHashMap that represent the Multipolygon.
     * @param shape 
     */
    public MultiPolygon(LinkedHashMap shape)throws org.json.JSONException {
        org.json.simple.JSONObject tmp = new org.json.simple.JSONObject(shape);
        org.json.JSONObject tmp2=new org.json.JSONObject(tmp.toString());
        this.setGeometry(tmp2.getJSONObject("geometry"));
        this.setType(((String) shape.get("type")));
        this.setId(((String) shape.get("id")));
        this.setProperties(tmp2.getJSONObject("properties"));
    }
    
    public MultiPolygon(org.json.JSONObject shape) throws org.json.JSONException {
        this.setGeometry(shape.getJSONObject("geometry"));
        this.setType(((String) shape.get("type")));
        this.setId(((String) shape.get("id")));
        this.setProperties(shape.getJSONObject("properties"));
    }
//</editor-fold>
    public String toJSONString() throws org.json.JSONException{
        String jsonPolygon="";
        //LinkedHashMap<String, Object> tmp=new LinkedHashMap<String, Object>();
        jsonPolygon="{\"type\":\""+this.type+"\",\"id\":\""+this.id+"\",\"properties\":"+this.properties.toString()+
                ",\"geometry\":"+this.geometry.toString()+"}";
        return jsonPolygon;
    }
}
