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
package MDBInt;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.util.JSON;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.jdom2.Element;
import utils.*;
import MDBInt.MDBIException;
import org.apache.log4j.Logger;
/**
 * @author agalletta
 * @author gtricomi
*/
public class DBMongo {

    private String serverURL;
    private String dbName;
    private String user;
    private String password;
    private int port;
    private MongoClient mongoClient;
    private HashMap map;
    private boolean connection;
    private Element serverList;
    private ParserXML parser;
    private MessageDigest messageDigest;
    static final Logger LOGGER = Logger.getLogger(DBMongo.class);
    public DBMongo() {

        map = new HashMap();
        connection = false;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

    }

    public void init(String file) {
        Element params;
        try {
            parser = new ParserXML(new File(file));
            params = parser.getRootElement().getChild("pluginParams");
            dbName = params.getChildText("dbName");
            user = params.getChildText("user");
            password = params.getChildText("password");
            serverList = params.getChild("serversList");
            //this.connectReplication();
            
        } //init();
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public String getRunTimeInfo(String dbName, String uuid) {

        BasicDBObject first = new BasicDBObject();
        first.put("phisicalResourceId", uuid);

        DB database = this.getDB(dbName);
        DBCollection collection = database.getCollection("runTimeInfo");
        DBObject obj = null;

        obj = collection.findOne(first);

       
        return obj.toString();

    }
    
    private void connectReplication() {

        MongoCredential credential;
        ArrayList<ServerAddress> lista = new ArrayList();
        List<Element> servers = serverList.getChildren();
      //  System.out.println("dentro connect");
        String ip;
        int porta;
        try {
            for (int s = 0; s < servers.size(); s++) {
                ip = servers.get(s).getChildText("serverUrl");
                porta = Integer.decode(servers.get(s).getChildText("port"));
                //    lista.add(new ServerAddress(servers.get(s).getChildText("serverUrl"),Integer.decode(servers.get(s).getChildText("port"))));
                lista.add(new ServerAddress(ip, porta));
            }

            credential = MongoCredential.createMongoCRCredential(user, dbName, password.toCharArray());
            mongoClient = new MongoClient(lista, Arrays.asList(credential));
            connection = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    
      /**
     *
     * @param dbName
     * @param userName
     * @param password
     * @param cloudID
     * @return jsonObject that contains credential for a specified cloud or null
     */
    public String getFederatedCredential(String dbName, String userName, String password, String cloudID) {

        DB dataBase = this.getDB(dbName);
        DBCollection collezione = this.getCollection(dataBase, "credentials");
        DBObject federationUser = null;
        BasicDBObject query = new BasicDBObject("federationUser", userName);
        BasicDBList credList;
        Iterator it;
        BasicDBObject obj;

        query.append("federationPassword", this.toMd5(password));
        federationUser = collezione.findOne(query);

        if (federationUser == null) {
            return null;
        }
        credList = (BasicDBList) federationUser.get("crediantialList");

        it = credList.iterator();
        while (it.hasNext()) {
            obj = (BasicDBObject) it.next();
            if (obj.containsValue(cloudID)) {
                return obj.toString();
            }
        }
        return null;
    }
    /**
     * This use only token. 
     * It will be 
     * @param dbName
     * @param token, this is an UUID generated from simple_IDM when a new Federation user is added. 
     * @param cloudID
     * @return
     * @author gtricomi
     */
    public String getFederatedCredential(String dbName, String token, String cloudID) {
        DB dataBase = this.getDB(dbName);
        DBCollection collezione = this.getCollection(dataBase, "credentials");
        DBObject federationUser = null;
        BasicDBObject query = new BasicDBObject("token", token);
        BasicDBList credList;
        Iterator it;
        BasicDBObject obj;

        federationUser = collezione.findOne(query);

        if (federationUser == null) {
            return null;
        }
        credList = (BasicDBList) federationUser.get("crediantialList");

        it = credList.iterator();
        while (it.hasNext()) {
            obj = (BasicDBObject) it.next();
            if (obj.containsValue(cloudID)) {
                return obj.toString();
            }
        }
        return null;
    }
    /**
     * Returns generic federation infoes.
     * @param dbName
     * @param token
     * @return 
     * @author gtricomi
     */
    public String getFederationCredential(String dbName, String token) {
        DB dataBase = this.getDB(dbName);
        DBCollection collezione = this.getCollection(dataBase, "credentials");
        DBObject federationUser = null;
        BasicDBObject query = new BasicDBObject("token", token);
        BasicDBList credList;
        Iterator it;
        BasicDBObject obj;
        String result="{";
        federationUser = collezione.findOne(query);

        if (federationUser == null) {
            return null;
        }
        String fu=(String)federationUser.get("federationUser");
        result+="\"federationUser\":\""+fu+"\",";
        String fp=(String)federationUser.get("federationPassword");
        result+=",\"federationPassword\":\""+fp+"\"";
        result+="}";
        return result;
    }
    
    public void connectLocale() {

        try {
            mongoClient= new MongoClient("172.17.3.142");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    
    /**
     * Function used for testing/prototype. 
     * @author gtricomi
     */
    public void connectLocale(String ip) {

        try {
            mongoClient= new MongoClient(ip);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void close() {

        try {

            mongoClient.close();
            map = new HashMap();
            connection = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private DB getDB(String name) {

        DB database = null;
        database = (DB) map.get(name);

        if (database == null) {
            database = mongoClient.getDB(name);
            map.put(name, database);
        }

        return database;

    }

    public void insertUser(String dbName, String collectionName, String docJSON) {

        DB dataBase = this.getDB(dbName);
        DBCollection collezione = this.getCollection(dataBase, collectionName);
        BasicDBObject obj = (BasicDBObject) JSON.parse(docJSON);
        String userName;
        userName = obj.getString("federationUser");
        obj.append("_id", userName);
        obj.append("insertTimestamp", System.currentTimeMillis());
        collezione.save(obj);
    }
    
        public void insert(String dbName, String collectionName, String docJSON) {

        DB dataBase = this.getDB(dbName);
        DBCollection collezione = this.getCollection(dataBase, collectionName);
        BasicDBObject obj = (BasicDBObject) JSON.parse(docJSON);
        obj.append("insertTimestamp", System.currentTimeMillis());
        collezione.save(obj);
    }


    public DBCollection getCollection(DB nameDB, String nameCollection) {

        nameCollection = nameCollection.replaceAll("-", "__");

        return nameDB.getCollection(nameCollection);

    }

    public List getListDB() {

        return mongoClient.getDatabaseNames();

    }

    public String getUser(String dbName, String collectionName, String userName, String password) {

        DB dataBase = this.getDB(dbName);
        DBCollection collezione = this.getCollection(dataBase, collectionName);
        DBObject federationUser = null;
        BasicDBObject query = new BasicDBObject("federationUser", userName);

        query.append("federationPassword", password);
        federationUser = collezione.findOne(query);

        if (federationUser != null) {
            return federationUser.toString();
        }

        return null;
    }

    public void updateUser(String dbName, String collectionName, String docJSON) {

        DB dataBase = this.getDB(dbName);
        DBCollection collezione = this.getCollection(dataBase, collectionName);
        BasicDBObject obj = (BasicDBObject) JSON.parse(docJSON);
        String userName;
        userName = obj.getString("federationUser");
        obj.append("_id", userName);

        collezione.save(obj);
    }
    /**
     * function that returns all element in collection without _id,credentialList, federationPassword
     * @param dbName
     * @param collectionName 
     */
    public void listFederatedUser(String dbName, String collectionName) {

        DBCursor cursore;
        DB dataBase;
        DBCollection collezione;
        BasicDBObject campi;
        Iterator<DBObject> it;
        dataBase = this.getDB(dbName);
        collezione = this.getCollection(dataBase, collectionName);
        campi = new BasicDBObject();
        campi.put("_id", 0);
        campi.put("crediantialList", 0);
        campi.put("federationPassword", 0);
        cursore = collezione.find(new BasicDBObject(), campi);
        it = cursore.iterator();

        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    public void insertFederatedCloud(String dbName, String collectionName, String docJSON) {

        DB dataBase = this.getDB(dbName);
        DBCollection collezione = this.getCollection(dataBase, collectionName);
        BasicDBObject obj = (BasicDBObject) JSON.parse(docJSON);
        String userName;
        userName = obj.getString("cloudId");
        obj.append("_id", userName);
        obj.append("insertTimestamp", System.currentTimeMillis());
        collezione.save(obj);
    }
    
    public String getFederateCloud(String dbName, String collectionName, String cloudId) {

        DB dataBase = this.getDB(dbName);
        DBCollection collezione = this.getCollection(dataBase, collectionName);
        DBObject federationUser = null;
        BasicDBObject query = new BasicDBObject("cloudId", cloudId);

        federationUser = collezione.findOne(query);

        if (federationUser != null) {
            return federationUser.toString();
        }

        return null;
    }

    public String getObj(String dbName,String collName, String query)throws MDBIException{
    
        BasicDBObject constrains= null, results=null;
        DBCursor cursore;
        DB db= this.getDB(dbName);
        DBCollection coll= db.getCollection(collName);
        BasicDBObject obj=(BasicDBObject) JSON.parse(query);
        constrains=new BasicDBObject("_id",0);
        constrains.put("insertTimestamp", 0);
        cursore=coll.find(obj,constrains);
        try{
        results= (BasicDBObject) cursore.next();
        }
        catch(NoSuchElementException e){
            LOGGER.error("manifest non trovato!");
            throw new MDBIException("Manifest required is not found inside DB.");
        }
        return results.toString(); 
    }
    
    /**
     * Returns an ArraList<String> where each String is a JSONObject in String version.
     * @param tenant
     * @param geoShape
     * @return 
     */
    public ArrayList<String> getDatacenters(String tenant,String geoShape){
        DB database=this.getDB(tenant);
        DBCollection collection=database.getCollection("datacenters");
        
        BasicDBObject shape=(BasicDBObject) JSON.parse(geoShape);
        ArrayList<String> datacenters=new ArrayList();
        BasicDBObject geoJSON=(BasicDBObject)shape.get("geometry");
        BasicDBObject geometry=new BasicDBObject();
        BasicDBObject geoSpazialOperator=new BasicDBObject();
        BasicDBObject query=new BasicDBObject();
        BasicDBObject constrains=new BasicDBObject("_id",0);
        Iterator <DBObject> it;
        DBCursor cursore;

        geometry.put("$geometry", geoJSON);
        geoSpazialOperator.put("$geoIntersects", geometry);
        query.put("geometry", geoSpazialOperator);
        cursore=collection.find(query,constrains);
       
        it=cursore.iterator();
        while(it.hasNext()){
        datacenters.add(it.next().toString());
        
        }
    return datacenters;
    }
    
    public String getDatacenter(String tenant,String idCloud) throws MDBIException{
        DB database=this.getDB(tenant);
        DBCollection collection=database.getCollection("datacenters");
        
        BasicDBObject first = new BasicDBObject();
        first.put("cloudId", idCloud);

        DBObject obj = null;
        try{
            obj = collection.findOne(first);
        }
        catch(Exception e){
            throw new MDBIException(e.getMessage());
        }
        return obj.toString();

    }
    
    
    /**
     * Function used to retrieve cloudId from cmp_endpoint
     * @param federationUser
     * @param cmp_endpoint
     * @return 
     * @author gtricomi
     */
    public String getDatacenterIDfrom_cmpEndpoint(String federationUser,String cmp_endpoint )throws MDBIException{
        DB database=this.getDB(federationUser);
        DBCollection collection=database.getCollection("datacenters");
        
        BasicDBObject first = new BasicDBObject();
        first.put("idmEndpoint", cmp_endpoint);

        DBObject obj = null;
        try{
            obj = collection.findOne(first);
        }
        catch(Exception e){
            throw new MDBIException("An Exception is generated by OSFFM DB connector, when getDatacenterIDfrom_cmpEndpoint is launched with [federationUser:\" "+federationUser+"\",cmp_endpoint: "+cmp_endpoint+"\"]\n"+e.getMessage());
        }
        return ((String)obj.get("cloudId"));

    }
    
    /**
     * Returns an ArraList<String> where each String is a JSONObject in String version.
     * @param tenant
     * @param deviceId, this is the Vm Name
     * @return 
     */
    public ArrayList<String> getportinfoes(String tenant,String deviceId){
        DB database=this.getDB(tenant);
        DBCollection collection=database.getCollection("portInfo");
        
        DBCursor cursore;
        BasicDBObject campi;
        Iterator<DBObject> it;
        campi = new BasicDBObject();
        campi.put("deviceId", deviceId);
        cursore = collection.find(new BasicDBObject(), campi);
        it = cursore.iterator();
        ArrayList<String> pI=new ArrayList<String>();
        while(it.hasNext()){
            pI.add(it.next().toString());
        
        }
    return pI;
    }
    
    
    public void insertStackInfo(String dbName, String docJSON) {

        this.insert(dbName, "stackInfo", docJSON);
    }

    public void insertResourceInfo(String dbName, String docJSON) {

        this.insert(dbName, "resourceInfo", docJSON);

    }

    public void insertPortInfo(String dbName, String docJSON) {

        this.insert(dbName, "portInfo", docJSON);

    }

    public void insertRuntimeInfo(String dbName, String docJSON) {

        this.insert(dbName, "runTimeInfo", docJSON);

    }

    public ArrayList<String> findResourceMate(String dbName, String uuid) {

        BasicDBObject first = new BasicDBObject();
        first.put("phisicalResourceId", uuid);

        DB database = this.getDB(dbName);
        DBCollection collection = database.getCollection("runTimeInfo");
        DBObject obj = null;
        BasicDBObject query = new BasicDBObject();
        DBCursor cursore = null;
        ArrayList<String> mates = new ArrayList();
        Iterator<DBObject> it;

        obj = collection.findOne(first);

        if (obj != null) {
            query.put("localResourceName", obj.get("localResourceName"));
            query.put("stackName", obj.get("stackName"));
            query.put("resourceName", obj.get("resourceName"));
            query.put("type", obj.get("type"));
            query.put("idCloud", new BasicDBObject("$ne", obj.get("idCloud")));

            System.out.println(query);
            cursore = collection.find(query);

        }
        if (cursore != null) {
            it = cursore.iterator();
            while (it.hasNext()) {
                mates.add(it.next().toString());

            }
        }
        return mates;

    }
    
    private String toMd5(String original) {
        MessageDigest md;
        byte[] digest;
        StringBuffer sb;
        String hashed = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(original.getBytes());
            digest = md.digest();
            sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            hashed = sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return hashed;
    }
//////////////////////////////////////////////////////////////////////////////////////7
    //funzioni da eliminare
    
    
    
    /**
     * Returns ArrayList with all federatedUser registrated for the federationUser
     * @param dbName
     * @param collectionName
     * @param federationUserName
     * @return 
     * @author gtricomi
     */
    public ArrayList<String> listFederatedUser(String dbName, String collectionName,String federationUserName) {

        DBCursor cursore;
        DB dataBase;
        DBCollection collezione;
        BasicDBObject campi;
        Iterator<DBObject> it;
        dataBase = this.getDB(dbName);
        collezione = this.getCollection(dataBase, collectionName);
        campi = new BasicDBObject();
        campi.put("federationUser", federationUserName);
        cursore = collezione.find(campi);
        it = cursore.iterator();
        ArrayList<String> als=new ArrayList();
        while (it.hasNext()) {
            als.add(it.next().get("crediantialList").toString());
        }
        return als;
    }
       
    public void insertTemplateInfo(String db, String id, String templateName, Float version, String user, String templateRef){
    

        BasicDBObject obj;
        
        obj = new BasicDBObject();
        
        obj.append("id", id);
        obj.append("templateName", templateName);
        obj.append("version", version);
        obj.append("user", user);
        obj.append("templateRef", templateRef);
        
        this.insert(db, "templateInfo", obj.toString());
    }
    
    
     public ArrayList<String> listTemplates(String dbName) {

        DBCursor cursore;
        DB dataBase;
        DBCollection collezione;
        Iterator<DBObject> it;
        dataBase = this.getDB(dbName);
        collezione = dataBase.getCollection("templateInfo");
        cursore = collezione.find();
        it = cursore.iterator();
        ArrayList<String> templatesInfo=new ArrayList();
        while (it.hasNext()) {
            templatesInfo.add(it.next().toString());
        }
        return templatesInfo;
    }

    //BEACON>>> Function added for preliminaryDEMO. HAVE TO BE REMOVED
    /**
     * Returns generic federation infoes.
     * @param dbName
     * @param token
     * @return 
     * @author gtricomi
     */
    public String getFederationCredential(String dbName, String value,String type) {
        DB dataBase = this.getDB(dbName);
        DBCollection collezione = this.getCollection(dataBase, "credentials");
        DBObject federationUser = null;
        BasicDBObject query = new BasicDBObject(type, value);
        BasicDBList credList;
        Iterator it;
        BasicDBObject obj;
        String result="{";
        federationUser = collezione.findOne(query);


        if (federationUser == null) {
            return null;
        }
        String fu=(String)federationUser.get("federationUser");
        result+="\"federationUser\":\""+fu+"\",";
        String fp=(String)federationUser.get("federationPassword");
        result+=",\"federationPassword\":\""+fp+"\"";
        result+="}";
        return result;
    }
}


