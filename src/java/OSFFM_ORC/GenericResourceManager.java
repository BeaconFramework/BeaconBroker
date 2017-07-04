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

import org.json.JSONObject;

/**
 *
 * @author Giuseppe Tricomi
 */
public abstract class GenericResourceManager  {
    private String georeference; //questo serve come parametro per la chiamata da inoltrare al manifest manger per ottenere il
                                //luoghi dove effettuare il deploy
    public String getGeoreference() {
        return georeference;
    }
    
    protected void setGeoreference(String georeference) {
        this.georeference = georeference;
    }

    public GenericResourceManager() {
        this.georeference="";
    }
    
    public abstract void insertOutContainer(JSONObject r,String name);
    public abstract void insertResContainer(JSONObject r, String name);
    public abstract void insertParContainer(JSONObject r,String name);
    
}
