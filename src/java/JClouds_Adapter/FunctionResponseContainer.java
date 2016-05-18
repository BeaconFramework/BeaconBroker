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

package JClouds_Adapter;

import java.util.HashMap;

/**
 *
 * @author Giuseppe Tricomi/gtricomi
 */
public class FunctionResponseContainer {
    boolean responseCode=false;

    Object responseObject=null;
    String responseMessage="";
    String responseObjectType="";
    
    HashMap mapContainer=null;

    public HashMap getMapContainer() {
        return mapContainer;
    }

    public void insertOnMapContainer(String key,Object elem) {
        if(this.mapContainer==null)
            this.mapContainer=new HashMap();
        this.mapContainer.put(key, elem);
    }
    public boolean isResponseCode() {
        return responseCode;
    }

    public void setResponseCode(boolean responseCode) {
        this.responseCode = responseCode;
    }

    public Object getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(Object responseObject,String responseObjectType) {
        this.responseObject = responseObject;
        this.responseObjectType=responseObjectType;
    }
    
    public String getResponseObjectType() {
        return responseObjectType;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public FunctionResponseContainer() {
       
    }
}
