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
    DBMongo ident_db=null;
    private static String configFile="../webapps/OSFFM/WEB-INF/configuration_bigDataPlugin.xml";
    private String IDMdbName="simpleIDM";
    private ParserXML parser;
    private String mdbIp;
    private String dbName="beacon";//beacon is the DEFAULT VALUE
    private String identityDBname; //This is the name of the DB that contains the Credential infos of the Federation Tenant 
    static final Logger LOGGER = Logger.getLogger(SimpleIDM.class);

    public DBMongo getMdb() {
        return mdb;
    }

    public void setMdb(DBMongo mdb) {
        this.mdb = mdb;
    }

   /* public static String getConfigFile() {
        return configFile;
    }

    public static void setConfigFile(String configFile) {
        SimpleIDM.configFile = configFile;
    }*/

    public String getIDMdbName() {
        return IDMdbName;
    }

    public void setIDMdbName(String IDMdbName) {
        this.IDMdbName = IDMdbName;
    }

    public String getMdbIp() {
        return mdbIp;
    }

    public void setMdbIp(String mdbIp) {
        this.mdbIp = mdbIp;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getCollName() {
        return identityDBname;
    }

    public void setCollName(String collName) {
        this.identityDBname = collName;
    }
    
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
        this.ident_db=new DBMongo();
        this.ident_db.setIdentityDB(identityDBname);
        this.ident_db.connectLocale(this.mdbIp);
    }
    
    private void init(String file){
        Element params;
        try {
            parser = new ParserXML(new File(file));
            params = parser.getRootElement().getChild("SimpleIDMParams");
            mdbIp = params.getChildText("serverip");
            //dbName= params.getChildText("dbname"); Removed 
            identityDBname= params.getChildText("IdentityDBname");
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * This function is used in order to retrieve the Database name for the Federation Tenant.
     * @param field, parameter used as research key
     * @param value, paramenter used as research key value 
     * @return String
     */
    public String retrieve_TenantDB(String field,String value){
        return this.ident_db.getTenantDBName(field, value);
    }
    
    
    
    
    
    
    
    /**
     * Function used to Verify credential of a federation user
     * @param token 
     * @return 
     */
    public boolean verifyCredentials(String tenant,String token,String cloudID){
        //BEACON>>> it will be added token integrity check, the token will be inserted inside request as combination between several field
        ////like timestamp request user and password. This algorithm works like digital signature.
        
      
        if(this.retr_infoes_fromfedsdn(token, tenant, null, null, cloudID)==null)
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
            String cloudId)
    {
       /*  String cloudid;
        try {
            cloudid = this.mdb.getDatacenter(tenant, cloudId);
        } catch (MDBIException ex) {
           LOGGER.error(ex.getMessage());
           LOGGER.error("An exception is generated in Database request by SimpleIDM, in cloudId retrieving");
           return null;
        }*/
        String result=this.mdb.getFederatedCredential(this.dbName, token, cloudId);
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
            String cloudId
            )
    {
        String result="";
        try{
           //String cloudid=this.mdb.getDatacenterIDfrom_cmpEndpoint(tj.getString("federationUser"), cmp_endpoint);
            result=this.mdb.getFederatedCredential(this.dbName, token, cloudId);
            JSONObject tmp2=new JSONObject(result);
            return this.createFederatedU(tmp2.getString("federatedUser"), tmp2.getString("federatedCloud"), tmp2.getString("federatedPassword"));
        }
        catch(Exception e){
            LOGGER.error("An exception is generated in Database interrogation by SimpleIDM, in the JSONObject retrived analisys");
            return null;
        }
    }
    /**
     * 
     * @param token
     * @param cmp_endpoint,for future use
     * @return 
     * @author gtricomi
     */
    public FederationUser getFederationU(
            String token,
            String cmp_endpoint
            )
    {
        String tmp=this.mdb.getFederationCredential(this.dbName, token);
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
     * 
     * @param token
     * @param cmp_endpoint,for future use
     * @return 
     * @author gtricomi
     */
    public FederatedUser getFederatedU(
            String token,
            String cmp_endpoint
            )
    {
       /* String query="",result="";
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
        }*/
        return null;
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
        //This function need to be recreated
        JSONObject j=new JSONObject();
        /*try{
            j.put("federatedUser", username);
            j.put("federationPassword", password);
            j.put("cmp_endpoint", cmp_endpoint);
            j.put("federationUser", tenant);
        }
        catch(Exception e){
            return null;
        }
        this.mdb.insert(this.dbName, "credentials", j.toString());*/
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
    public String getCloudID(
            String username,
            String tenant,
            String cmp_endpoint){
        String query="";
        query="{\"username\":\""+username+"\",\"tenant\":\""+tenant+"\",\"cmp_endpoint\":\""+cmp_endpoint+"\"}";
        String cloudid=null;
        try {
            cloudid = this.mdb.getDatacenterIDfrom_cmpEndpoint(this.dbName, cmp_endpoint);
        } catch (MDBIException ex) {
            LOGGER.error(ex.getMessage());
            return null;
        }
        return cloudid;
    }
    
    public String getcmp_endpointFederated(
            String tenant,
            String cloudid)
    {
        String cloud_endpoint=null;
        try {
            cloud_endpoint = this.mdb.getDatacenter(this.dbName, cloudid);
        } catch (MDBIException ex) {
            LOGGER.error(ex.getMessage());
            return null;
        }
        return cloud_endpoint;
    }
    /**
     * IT returns Federation Token assigne from OSFFM.
     * @param tenant
     * @param user
     * @return 
     */
    public String getFederationToken(
            String tenant,
            String user)
    {
        String tokenFederation=null;
        try {
            tokenFederation = this.mdb.getFederationToken(this.dbName, user);
        } catch (MDBIException ex) {
            LOGGER.error(ex.getMessage());
            return null;
        }
        return tokenFederation;
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
