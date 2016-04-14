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

package OSFFMIDM;

import java.util.ArrayList;
import java.util.HashMap;
import MDBInt.*;
import java.io.File;
import org.jdom2.Element;
import utils.ParserXML;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.UUID;
import java.util.logging.Level;
import org.apache.log4j.Logger;
/**
 *
 * @author Giuseppe Tricomi
 */
public class SimpleIDM {
    
    DBMongo mdb=null;
    private static String configFile="../webapps/OSFFM/WEB-INF/configuration_bigDataPlugin.xml";
    private String IDMdbName="simpleIDM";
    private ParserXML parser;
    private String mdbIp;
    private String dbName;
    private String collName;
    static final Logger LOGGER = Logger.getLogger(SimpleIDM.class);
    
    public SimpleIDM() {
        this.init(configFile);
        mdb=new DBMongo();
        mdb.connectLocale(this.mdbIp);
        Logger logger;
        
    }
    
    public SimpleIDM(String cf) {
        this.init(cf);
        mdb=new DBMongo();
        mdb.connectLocale(this.mdbIp);
    }
    
    private void init(String file){
        Element params;
        try {
            parser = new ParserXML(new File(file));
            params = parser.getRootElement().getChild("SimpleIDMParams");
            mdbIp = params.getChildText("serverip");
            dbName= params.getChildText("dbname");
            collName= params.getChildText("collname");
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * 
     * @param token 
     * @return 
     */
    public boolean verifyCredentials(String token){
        //BEACON>>> it will be added token integrity check, the token will be inserted inside request as combination between several field
        ////like timestamp request user and password. This algorithm works like digital signature.
        
      
        if(this.retr_infoes_fromfedsdn(token, null, null, null, null)==null)
            return false;
        else
            return true;
    }
 
///// RETRIEVE INFORMATION SECTION    
    
    
    /**
     * This function returns json representation of the interrogation result.  
     * @param token
     * @return JSONObject or null.
     */
    public String retr_infoes_fromfedsdn(
            String token,
            String tenant,
            String username,
            String password,
            String cmp_endpoint)
    {
        String query="";
        query="{\"username\":\""+username+"\",\"tenant\":\""+tenant+"\",\"cmp_endpoint\":\""+cmp_endpoint+"\"}";
        String cloudid;
        try {
            cloudid = this.mdb.getDatacenterIDfrom_cmpEndpoint(tenant, cmp_endpoint);
        } catch (MDBIException ex) {
           LOGGER.error(ex.getMessage());
           LOGGER.error("An exception is generated in Database request by SimpleIDM, in cloudId retrieving");
           return null;
        }
        String result=this.mdb.getFederatedCredential(this.dbName, token, cloudid);
        try{
            JSONObject j=new JSONObject(result);
            return j.toString();
        }
        catch(Exception e){
            LOGGER.error("An exception is generated in Database interrogation by SimpleIDM,in the JSONObject retrived analisys");
            return null;
        }
        
    }
    /**
     * This function returns json representation of the interrogation result.  
     * @param token
     * @param cmp_endpoint
     * @return JSONObject or null.
     */
    public FederatedUser retr_infoes_fromfedsdn(
            String token,
            String cmp_endpoint
            )
    {
        String query="",result="";
        query="{\"token\":\""+token+"\"}";
        String tmp=this.mdb.getFederationCredential(dbName, token);
        try{
            JSONObject tj=new JSONObject(tmp);
            String cloudid=this.mdb.getDatacenterIDfrom_cmpEndpoint(tj.getString("federationUser"), cmp_endpoint);
            result=this.mdb.getFederatedCredential(this.dbName, token, cloudid);
            JSONObject tmp2=new JSONObject(result);
            return this.createFederatedU(tmp2.getString("federatedUser"), tmp2.getString("federatedCloud"), tmp2.getString("federatedPassword"));
        }
        catch(Exception e){
            LOGGER.error("An exception is generated in Database interrogation by SimpleIDM, in the JSONObject retrived analisys");
            return null;
        }
    }
    
    public FederationUser getFederationU(
            String token,
            String cmp_endpoint
            )
    {
        String query="",result="";
        query="{\"token\":\""+token+"\"}";
        String tmp=this.mdb.getFederationCredential(dbName, token);
        try{
            JSONObject tj=new JSONObject(tmp);
            JSONArray ta=tj.getJSONArray("crediantialList");
            ArrayList ar=new ArrayList();
            for(int i =0;i<ta.length();i++){
                ar.add((JSONObject) ta.get(i));
            }
            return this.createFederationU(tj.getString("userFederation"), tj.getString("federationPassword"), ar);
        }
        catch(Exception e){
            LOGGER.error("An exception is generated in Database interrogation by SimpleIDM, in the JSONObject retrived analisys");
            return null;
        }
    }
    
    
    /**
     * Function used to store the information inside Simple IDM collection.
     * @param token
     * @param tenant
     * @param username
     * @param password
     * @param cmp_endpoint
     * @return JSONObject or null.
     */
    public JSONObject insert_infoes_4IDM(
            String token,
            String tenant,
            String username,
            String password,
            String cmp_endpoint)
    {
        JSONObject j=new JSONObject();
        try{
            j.put("token", this.generate_token());
            j.put("username", username);
            j.put("password", password);
            j.put("cmp_endpoint", cmp_endpoint);
            j.put("tenant", tenant);
        }
        catch(Exception e){
            return null;
        }
        this.mdb.insert(this.dbName, this.collName, j.toString());
        return j;
    }
    /**
     * 
     * @param username
     * @param tenant
     * @param cmp_endpoint
     * @return 
     * @author gtricomi
     */
    private String getCloudID(
            String username,
            String tenant,
            String cmp_endpoint){
        String query="";
        query="{\"username\":\""+username+"\",\"tenant\":\""+tenant+"\",\"cmp_endpoint\":\""+cmp_endpoint+"\"}";
        String cloudid=null;
        try {
            cloudid = this.mdb.getDatacenterIDfrom_cmpEndpoint(tenant, cmp_endpoint);
        } catch (MDBIException ex) {
            LOGGER.error(ex.getMessage());
            return null;
        }
        return cloudid;
    }
    
    private String getcmp_endpointFederated(
            String tenant,
            String cloudid)
    {
        String cloud_endpoint=null;
        try {
            cloud_endpoint = this.mdb.getDatacenter(tenant, cloudid);
        } catch (MDBIException ex) {
            LOGGER.error(ex.getMessage());
            return null;
        }
        return cloud_endpoint;
    }
    
    
    /**
     * 
     * @return 
     * @author gtricomi
     */
    private String generate_token(){
        return UUID.randomUUID().toString();
    }
    
    ////CREATION FEDERATED AND FEDERATION ISTANCE OBJECTS
    /**
     * 
     * @param user
     * @param password
     * @param credentials
     * @return 
     * @author gtricomi
     */
    private FederationUser createFederationU(
            String user,
            String password,
            ArrayList<org.json.simple.JSONObject> credentials
    ){
        return new FederationUser(user,password,credentials);
    }
    /**
     * 
     * @param user
     * @param cloud
     * @param password
     * @return 
     * @author gtricomi
     */
    private FederatedUser createFederatedU(String user, String cloud, String password){
        return new FederatedUser(user,cloud,password);
    }
}
