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
import com.mongodb.AggregationOutput;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
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
    private String identityDB;
    private static String configFile="../webapps/OSFFM/WEB-INF/configuration_bigDataPlugin.xml";
    private String mdbIp;

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

    public String getIdentityDB() {
        return identityDB;
    }

    public void setIdentityDB(String identityDB) {
        this.identityDB = identityDB;
    }
   
    
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
    
    public void init() {
        String file=configFile;
        Element params;
        try {
            parser = new ParserXML(new File(file));
            params = parser.getRootElement().getChild("pluginParams");
            dbName = params.getChildText("dbName");
            user = params.getChildText("user");
            password = params.getChildText("password");
            //serverList = params.getChild("serversList");
            mdbIp = params.getChildText("serverip");
            

        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * 
     * @param collection
     * @param resQuery
     * @param sortQuery
     * @return 
     * @author agalletta
     */
    private String conditionedResearch(DBCollection collection,BasicDBObject resQuery,BasicDBObject sortQuery){
        DBCursor b= collection.find(resQuery).sort(sortQuery).limit(1);
       return b.next().toString();
    }
    //<editor-fold defaultstate="collapsed" desc="FAInfo Management Functions">
    /**
     * 
     * @param tenantName
     * @param faSite
     * @param faIP
     * @param faPort
     * @return 
     * @author gtricomi
     */
    public String getFAInfo(String tenantName, String faSite) {
        DB database = this.getDB(tenantName);
        DBCollection collection = database.getCollection("faInfo");
        BasicDBObject first = new BasicDBObject();
        first.put("SiteName", faSite);
        DBObject obj = null;
        obj = collection.findOne(first);
        return obj.toString();
    }
    /**
     * 
     * @param tenantName
     * @param faSite
     * @param faIP
     * @param faPort 
     * @author gtricomi
     */
    public void insertFAInfo(String tenantName, String faSite,String faIP,String faPort) {

        DB dataBase = this.getDB(tenantName);
        DBCollection collezione = this.getCollection(dataBase, "faInfo");
        BasicDBObject obj = new BasicDBObject();
        obj.append("SiteName", faSite);
        obj.append("Ip", faIP);
        obj.append("Port", faPort);
        collezione.save(obj);
    }
    
    //</editor-fold>
//<editor-fold defaultstate="collapsed" desc="NetTables Management Functions">
    /**
     * 
     * @param dbName
     * @param faSite, this is the cloud Id
     * @return 
     * @author gtricomi
     */
    public String getNetTables(String dbName, String faSite) {

        DB database = this.getDB(dbName);
        DBCollection collection = database.getCollection("netTables");
        
        BasicDBObject resQuery=new BasicDBObject("referenceSite",faSite);
        BasicDBObject sortQuery=new BasicDBObject("version",-1);
        return this.conditionedResearch(collection,resQuery,sortQuery);

    }
    /**
     * 
     * @param dbName
     * @param faSite
     * @param key
     * @param list
     * @return 
     * @author agalletta
     */
    public Boolean checkNetTables(String dbName, String faSite, String key, LinkedHashSet<String> list) {

        DB database = this.getDB(dbName);
        DBCollection collection = database.getCollection("netTables");
        //DBCollection collection = database.getCollection("testIn");

        BasicDBObject tableQuery, andQuery;
        BasicDBList and;
        double version;
        BasicDBObject resQuery = new BasicDBObject("referenceSite", faSite);
        BasicDBObject sortQuery = new BasicDBObject("version", -1);
        DBCursor b = collection.find(resQuery).sort(sortQuery).limit(1);
        Iterator<String> it;
        if (!b.hasNext()) {
            //System.out.println("tabella non trovata");
            return null;
        } else {
            version = (double) b.next().get("version");
            //System.out.println(version);

            //andQuery=new BasicDBObject("referenceSite",faSite);
            and= new BasicDBList();
            and.add(resQuery);
            and.add(new BasicDBObject("version", version));
            it = list.iterator();
            while (it.hasNext()) {
               // resQuery.append("table." + key, it.next());
                and.add(new BasicDBObject("table." + key, it.next()));
            }
            andQuery=new BasicDBObject("$and",and);
            //System.out.println("query:" + andQuery);
            b = collection.find(andQuery);
            if (!b.hasNext()) {
              //  System.out.println("tabella incompleta");
                return false;
            } else {
                return true;

            }

            
        }
    }
    
    /**
     * This update Federation User with element. 
     * @param dbName
     * @param tableName
     * @param faSite, this is the cloud Id
     * @param docJSON 
     * @author gtricomi
     */
    public void insertNetTables(String dbName,String faSite, String docJSON,double version) {

        DB dataBase = this.getDB(dbName);
        DBCollection collezione = this.getCollection(dataBase, "netTables");
        BasicDBObject obj = (BasicDBObject) JSON.parse(docJSON);
        obj.append("referenceSite", faSite);
        obj.append("insertTimestamp", System.currentTimeMillis());
        obj.append("version", version);
        collezione.save(obj);
    }
     //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="siteTables Management Functions">
    /**
     * 
     * @param dbName
     * @param faSite, this is the cloud Id
     * @return 
     * @author gtricomi
     */
    public String getSiteTables(String dbName, String faSite) {

        DB database = this.getDB(dbName);
        DBCollection collection = database.getCollection("siteTables");
        
        BasicDBObject resQuery=new BasicDBObject("referenceSite",faSite);
        BasicDBObject sortQuery=new BasicDBObject("version",-1);
        return this.conditionedResearch(collection,resQuery,sortQuery);

    }
    
    /**
     * 
     * @param dbName
     * @param faSite
     * @param key
     * @param list
     * @return 
     * @author agalletta
     */
    public Boolean checkSiteTables(String dbName, String faSite, String key, LinkedHashSet<String> list) {

        DB database = this.getDB(dbName);
        DBCollection collection = database.getCollection("siteTables");
        //DBCollection collection = database.getCollection("testIn");

        BasicDBObject tableQuery, andQuery;
        BasicDBList and;
        double version;
        BasicDBObject resQuery = new BasicDBObject("referenceSite", faSite);
        BasicDBObject sortQuery = new BasicDBObject("version", -1);
        DBCursor b = collection.find(resQuery).sort(sortQuery).limit(1);
        Iterator<String> it;
        if (!b.hasNext()) {
           // System.out.println("tabella non trovata");
            return null;
        } else {
            version = (double) b.next().get("version");
           // System.out.println(version);

            //andQuery=new BasicDBObject("referenceSite",faSite);
            and= new BasicDBList();
            and.add(resQuery);
            and.add(new BasicDBObject("version", version));
            it = list.iterator();
            while (it.hasNext()) {
               // resQuery.append("table." + key, it.next());
                and.add(new BasicDBObject("table" + key, it.next()));
            }
            andQuery=new BasicDBObject("$and",and);
           // System.out.println("query:" + andQuery);
            b = collection.find(andQuery);
            if (!b.hasNext()) {
             //   System.out.println("tabella incompleta");
                return false;
            } else {
                return true;

            }

            
        }
    }
    
    /**
     * This update Federation User with element. 
     * @param dbName
     * @param faSite, this is the cloud Id
     * @param docJSON 
     * @author gtricomi
     */
    public void insertSiteTables(String dbName,String faSite, String docJSON) {

        DB dataBase = this.getDB(dbName);
        DBCollection collezione = this.getCollection(dataBase, "siteTables");
        BasicDBObject obj = (BasicDBObject) JSON.parse(docJSON);
        obj.append("referenceSite", faSite);
        obj.append("insertTimestamp", System.currentTimeMillis());
        collezione.save(obj);
    }
     //</editor-fold>
    //BEACON>>>> Valutare se mantenere questa parte di informazioni su MongoDB
    //<editor-fold defaultstate="collapsed" desc="TenantTables Management Functions">
    /**
     * 
     * @param dbName
     * @param faSite, this is the cloud Id
     * @return 
     * @author gtricomi
     */
    public String getTenantTables(String dbName, String faSite) {

        DB database = this.getDB(dbName);
        DBCollection collection = database.getCollection("TenantTables");
        
        BasicDBObject resQuery=new BasicDBObject("referenceSite",faSite);
        BasicDBObject sortQuery=new BasicDBObject("version",-1);
        return this.conditionedResearch(collection,resQuery,sortQuery);

    }
    
    /**
     * This update Federation User with element. 
     * @param dbName
     * @param tableName
     * @param faSite, this is the cloud Id
     * @param docJSON 
     * @author gtricomi
     */
    public void insertTenantTables(String dbName,String tableName,String faSite, String docJSON) {

        DB dataBase = this.getDB(dbName);
        DBCollection collezione = this.getCollection(dataBase, "TenantTables");
        BasicDBObject obj = (BasicDBObject) JSON.parse(docJSON);
        obj.append("referenceSite", faSite);
        obj.append("insertTimestamp", System.currentTimeMillis());
        collezione.save(obj);
    }
     //</editor-fold>
    
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
           // System.out.println("password: "+this.toMd5(password));

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
     * This use only token. It will be
     *
     * @param dbName
     * @param token, this is an UUID generated from simple_IDM when a new
     * Federation user is added.
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
     *
     * @param dbName
     * @param token, this is an internal token?
     * @return
     * @author gtricomi
     */
    public String getFederationCredential(String dbName, String token) {
        DB dataBase = this.getDB(dbName);
        DBCollection collezione = this.getCollection(dataBase, "credentials");
        DBObject federationUser = null;
        BasicDBObject query = new BasicDBObject("token", token);
        federationUser = collezione.findOne(query);

        if (federationUser == null) {
            return null;
        }
        BasicDBObject bo = new BasicDBObject();
        bo.append("federationUser", (String) federationUser.get("federationUser"));
        bo.append("federationPassword", (String) federationUser.get("federationPassword"));
        return bo.toString();
    }

    public void connectLocale() {

        try {
            mongoClient = new MongoClient("172.17.2.32");//("172.17.3.142");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Function used for testing/prototype.
     *
     * @author gtricomi
     */
    public void connectLocale(String ip) {

        try {
            mongoClient = new MongoClient(ip);
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
    /**
     * This update Federation User with element 
     * @param dbName
     * @param collectionName
     * @param docJSON 
     */
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
     * function that returns all element in collection without
     * _id,credentialList, federationPassword
     *
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

    public String getObj(String dbName, String collName, String query) throws MDBIException {

        BasicDBObject constrains = null, results = null;
        DBCursor cursore;
        DB db = this.getDB(dbName);
        DBCollection coll = db.getCollection(collName);
        BasicDBObject obj = (BasicDBObject) JSON.parse(query);
        constrains = new BasicDBObject("_id", 0);
        constrains.put("insertTimestamp", 0);
        cursore = coll.find(obj, constrains);
        try {
            results = (BasicDBObject) cursore.next();
        } catch (NoSuchElementException e) {
            LOGGER.error("manifest non trovato!");
            throw new MDBIException("Manifest required is not found inside DB.");
        }
        return results.toString();
    }

    /**
     * Returns an ArraList<String> where each String is a JSONObject in String
     * version.
     *
     * @param tenant
     * @param geoShape
     * @return
     */
    public ArrayList<String> getDatacenters(String tenant, String geoShape) {
        DB database = this.getDB(tenant);
        DBCollection collection = database.getCollection("datacenters");

        BasicDBObject shape = (BasicDBObject) JSON.parse(geoShape);
        ArrayList<String> datacenters = new ArrayList();
        BasicDBObject geoJSON = (BasicDBObject) shape.get("geometry");
        BasicDBObject geometry = new BasicDBObject();
        BasicDBObject geoSpazialOperator = new BasicDBObject();
        BasicDBObject query = new BasicDBObject();
        BasicDBObject constrains = new BasicDBObject("_id", 0);
        Iterator<DBObject> it;
        DBCursor cursore;

        geometry.put("$geometry", geoJSON);
        geoSpazialOperator.put("$geoIntersects", geometry);
        query.put("geometry", geoSpazialOperator);
        cursore = collection.find(query, constrains);

        it = cursore.iterator();
        while (it.hasNext()) {
            datacenters.add(it.next().toString());

        }
        return datacenters;
    }

    public String getDatacenter(String tenant, String idCloud) throws MDBIException {
        DB database = this.getDB(tenant);
        DBCollection collection = database.getCollection("datacenters");

        BasicDBObject first = new BasicDBObject();
        first.put("cloudId", idCloud);

        DBObject obj = null;
        try {
            obj = collection.findOne(first);
        } catch (Exception e) {
            throw new MDBIException(e.getMessage());
        }
        return obj.toString();

    }
    
    /**
     * It returns OSFFM token assigned to federationUser.
     * @param tenant
     * @param user, this is federation UserName
     * @return
     * @throws MDBIException
     * @author gtricomi
     */
    public String getFederationToken(String tenant, String user) throws MDBIException {
        DB database = this.getDB(tenant);
        DBCollection collection = database.getCollection("credentials");

        BasicDBObject first = new BasicDBObject();
        first.put("federationUser", user);

        DBObject obj = null;
        try {
            obj = collection.findOne(first);
        } catch (Exception e) {
            throw new MDBIException(e.getMessage());
        }
        return (String)obj.get("token");

    }

    /**
     * Function used to retrieve cloudId from cmp_endpoint
     *
     * @param federationUser
     * @param cmp_endpoint
     * @return
     * @author gtricomi
     */
    public String getDatacenterIDfrom_cmpEndpoint(String federationUser, String cmp_endpoint) throws MDBIException {
        DB database = this.getDB(federationUser);
        DBCollection collection = database.getCollection("datacenters");

        BasicDBObject first = new BasicDBObject();
        first.put("idmEndpoint", cmp_endpoint);

        DBObject obj = null;
        try {
            obj = collection.findOne(first);
        } catch (Exception e) {
            throw new MDBIException("An Exception is generated by OSFFM DB connector, when getDatacenterIDfrom_cmpEndpoint is launched with [federationUser:\" " + federationUser + "\",cmp_endpoint: " + cmp_endpoint + "\"]\n" + e.getMessage());
        }
        return ((String) obj.get("cloudId"));

    }

    /**
     * Returns an ArraList<String> where each String is a JSONObject in String
     * version.
     *
     * @param tenant
     * @param deviceId, this is the Vm Name
     * @return
     */
    public ArrayList<String> getportinfoes(String tenant, String deviceId) {
        DB database = this.getDB(tenant);
        DBCollection collection = database.getCollection("portInfo");

        DBCursor cursore;
        BasicDBObject campi;
        Iterator<DBObject> it;
        campi = new BasicDBObject();
        campi.put("deviceId", deviceId);
        cursore = collection.find(new BasicDBObject(), campi);
        it = cursore.iterator();
        ArrayList<String> pI = new ArrayList<String>();
        while (it.hasNext()) {
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
            query.put("state", false);
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

    public boolean updateStateRunTimeInfo(String dbName, String phisicalResourceId, boolean newState) {
        boolean result = true;
        BasicDBObject first = new BasicDBObject();
        first.put("phisicalResourceId", phisicalResourceId);

        DB database = this.getDB(dbName);
        DBCollection collection = database.getCollection("runTimeInfo");
        BasicDBObject obj = null;

        obj = (BasicDBObject) collection.findOne(first);
        obj.append("state", newState);
        collection.save(obj);
        return result;
    }

    private String toMd5(String original) {
        /*MessageDigest md;
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
        return hashed;*/
        return utils.staticFunctionality.toMd5(original);
    }
//////////////////////////////////////////////////////////////////////////////////////7
    //funzioni da rivedere /eliminare

    /**
     * Returns ArrayList with all federatedUser registrated for the
     * federationUser
     *
     * @param dbName
     * @param collectionName
     * @param federationUserName
     * @return
     * @author gtricomi
     */
    public ArrayList<String> listFederatedUser(String dbName, String collectionName, String federationUserName) {

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
        ArrayList<String> als = new ArrayList();
        while (it.hasNext()) {
            als.add(it.next().get("crediantialList").toString());
        }
        return als;
    }

    public void insertTemplateInfo(String db, String id, String templateName, Float version, String user, String templateRef) {

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
        ArrayList<String> templatesInfo = new ArrayList();
        while (it.hasNext()) {
            templatesInfo.add(it.next().toString());
        }
        return templatesInfo;
    }

    /**
     * Returns a specific template istance.
     *
     * @param dbName
     * @return
     * @author gtricomi
     */
    public String getTemplate(String dbName, String uuidManifest) {

        DB dataBase;
        DBCollection collezione;
        dataBase = this.getDB(dbName);
        collezione = dataBase.getCollection("templateInfo");

        BasicDBObject first = new BasicDBObject();
        first.put("id", uuidManifest);
        DBObject j = collezione.findOne(first);
        return j.toString();
    }

    //BEACON>>> Function added for preliminaryDEMO. HAVE TO BE REMOVED
    /**
     * Returns generic federation infoes.
     *
     * @param dbName
     * @param token
     * @return
     * @author gtricomi
     */
    public String getFederationCredential(String dbName, String value, String type) {
        DB dataBase = this.getDB(dbName);
        DBCollection collezione = this.getCollection(dataBase, "credentials");
        DBObject federationUser = null;
        BasicDBObject query = new BasicDBObject(type, value);
        BasicDBList credList;
        Iterator it;
        BasicDBObject obj;
        String result = "{";
        federationUser = collezione.findOne(query);

        if (federationUser == null) {
            return null;
        }
        BasicDBObject bo = new BasicDBObject();
        bo.append("federationUser", (String) federationUser.get("federationUser"));
        bo.append("federationPassword", (String) federationUser.get("federationPassword"));
        /*String fu=(String)federationUser.get("federationUser");
         result+="\"federationUser\":\""+fu+"\",";
         String fp=(String)federationUser.get("federationPassword");
         result+="\"federationPassword\":\""+fp+"\"";
         result+="}";*/
        return bo.toString();//result;
    }

    private Iterable<DBObject> operate(String dbName, String collectionName, String campoMatch, String valoreMatch, String campoOperazione, String nomeOperazione, String operation) {

        DB database = this.getDB(dbName);
        DBCollection collezione;
        DBObject match, fields, project, groupFields, group, sort;
        AggregationOutput output;
        Iterable<DBObject> cursor;
        List<DBObject> pipeline;

        collezione = database.getCollection(collectionName);
        match = new BasicDBObject("$match", new BasicDBObject(campoMatch, valoreMatch));
        fields = new BasicDBObject(campoOperazione, 1);
        fields.put("_id", 0);

        project = new BasicDBObject("$project", fields);

        groupFields = new BasicDBObject("_id", campoOperazione);
        groupFields.put(nomeOperazione, new BasicDBObject(operation, "$" + campoOperazione));

        group = new BasicDBObject("$group", groupFields);
        sort = new BasicDBObject("$sort", new BasicDBObject(campoOperazione, -1));
        pipeline = Arrays.asList(match, project, group, sort);
        output = collezione.aggregate(pipeline);
        cursor = output.results();
        return cursor;

    }

    public float getVersion(String dbName, String collectionName, String templateRef) {

        String v = "version";
        Iterable<DBObject> cursor = this.operate(dbName, collectionName, "templateRef", templateRef, v, v, "$max");
        Iterator<DBObject> it;
        DBObject result, query;
        DBObject risultato;
        float versione = 0;

        it = cursor.iterator();

        if (it.hasNext()) {
            result = it.next();
            versione = (float) ((double) result.get(v));
        } else {
            query = new BasicDBObject("id", templateRef);
            risultato = this.find(dbName, collectionName, query);
            if (risultato != null) {
                versione = 0.1F;
            }
        }
        System.out.println("VERSIONE:" + versione);
        return versione;
//controlla che il cursore nn sia vuoto, se vuoto controlla se è presente come versione 0.1
        //altrimenti restituisci 0.1

    }

    public DBObject find(String dbName, String collName, DBObject obj) {
        DB dataBase = this.getDB(dbName);
        DBCollection collezione = dataBase.getCollection(collName);
        // BasicDBObject obj = (BasicDBObject) JSON.parse(query);
        DBObject risultato = collezione.findOne(obj);

        return risultato;

    }

    public DBObject getDatacenterFromId(String dbName, String id) {
        DB dataBase = this.getDB(dbName);
        DBCollection collezione = dataBase.getCollection("datacenters");
        BasicDBObject obj = new BasicDBObject("cloudId", id);
        DBObject risultato = collezione.findOne(obj);

        return risultato;
    }

    public String getMapInfo(String dbName, String uuidTemplate) {

        DB database = this.getDB(dbName);
        DBCollection collection = database.getCollection("runTimeInfo");
        DBObject obj = null, datacenter, punto, country, geoShape;
        BasicDBObject query = new BasicDBObject();
        DBCursor cursore = null;
        Set<String> attivi = new HashSet<String>();
        ArrayList<String> listCloud = new ArrayList();
        Iterator<DBObject> it;
        String idCloud, idmEndpoint, idRisorsa, nameRisorsa;
        boolean isPresent;
        MapInfo map;
        Shape s;
        boolean state;
        Risorse r;
        int i, j;
        Collegamenti link;
        Object array[];

        query.put("uuidTemplate", uuidTemplate);
        cursore = collection.find(query);
        map = new MapInfo();

        if (cursore != null) {
            it = cursore.iterator();
            while (it.hasNext()) {
                //oggetto map info
                obj = it.next();
                idCloud = (String) obj.get("idCloud");
                isPresent = listCloud.contains(idCloud);
                if (!isPresent) {

                    datacenter = this.getDatacenterFromId(dbName, idCloud);
                    idmEndpoint = (String) datacenter.get("idmEndpoint");
                    state = (boolean) obj.get("state");

                    punto = (DBObject) datacenter.get("geometry");
                    country = this.getFirstCountry(dbName, punto);
                    geoShape = (DBObject) country.get("geometry");
                    System.out.println("geogeo" + geoShape);
                    s = new Shape(idCloud, idmEndpoint, geoShape.toString(), state);
                    if (state) {
                        attivi.add(idCloud);
                        idRisorsa = (String) obj.get("phisicalResourceId");
                        nameRisorsa = (String) obj.get("resourceName");
                        r = new Risorse(idRisorsa, nameRisorsa);
                        s.addResource(r);
                    }

                    listCloud.add(idCloud);
                    map.addShape(s);

                } else {
                    s = map.getShape(idCloud);
                    state = (boolean) obj.get("state");
                    if (state) {
                        s.setState(state);
                        attivi.add(idCloud);

                        idRisorsa = (String) obj.get("phisicalResourceId");
                        nameRisorsa = (String) obj.get("resourceName");
                        r = new Risorse(idRisorsa, nameRisorsa);
                        s.addResource(r);
                    }
                }
            }
            array = attivi.toArray();
            for (i = 0; i < array.length; i++) {
                for (j = i + 1; j < array.length; j++) {
                    link = new Collegamenti((String) array[i], (String) array[j]);
                    map.addCollegamento(link);
                }
            }
        }
        return map.toString();
    }

    public DBObject getFirstCountry(String tenant, DBObject shape) {

        //{"geometry": {$geoIntersects: {$geometry: { "coordinates" : [  15.434675, 38.193164  ], "type" : "Point" }}}}
        DB database = this.getDB(tenant);
        DBCollection collection = database.getCollection("Countries");
        DBObject result;
        BasicDBObject geometry = new BasicDBObject("$geometry", shape);
        BasicDBObject geoSpazialOperator = new BasicDBObject("$geoIntersects", geometry);
        BasicDBObject query = new BasicDBObject("geometry", geoSpazialOperator);
        BasicDBObject constrains = new BasicDBObject("_id", 0);
        Iterator<DBObject> it;
        result = collection.findOne(query, constrains);
        return result;
    }

    public ArrayList getCountry(String tenant, DBObject shape) {

        //{"geometry": {$geoIntersects: {$geometry: { "coordinates" : [  15.434675, 38.193164  ], "type" : "Point" }}}}
        DB database = this.getDB(tenant);
        DBCollection collection = database.getCollection("Countries");
        ArrayList<String> datacenters = new ArrayList();

        BasicDBObject geometry = new BasicDBObject("$geometry", shape);
        BasicDBObject geoSpazialOperator = new BasicDBObject("$geoIntersects", geometry);
        BasicDBObject query = new BasicDBObject("geometry", geoSpazialOperator);
        BasicDBObject constrains = new BasicDBObject("_id", 0);
        Iterator<DBObject> it;
        DBCursor cursore;

        cursore = collection.find(query, constrains);

        it = cursore.iterator();
        while (it.hasNext()) {
            datacenters.add(it.next().toString());

        }

        return datacenters;
    }
   
//<editor-fold defaultstate="collapsed" desc="Federation Tenant Credential Management">
    /**
     * Method used for the retrieve Database name for Federation Tenant.
     * @param field
     * @param value
     * @return 
     */
    public String getTenantDBName(String field,String value){
       DB database = this.getDB(this.identityDB);
       DBCollection collection = database.getCollection("Federation_Credential");
       BasicDBObject researchField = new BasicDBObject(field, value);
       DBObject risultato = collection.findOne(researchField);
       String tenantDbName=(String)risultato.get("dbname");
       return tenantDbName;
    }
    
    
//</editor-fold>    
    //<editor-fold defaultstate="collapsed" desc="NetworkId Management">
    
    /**
     * This method is used to add inside "netIDMatch" collection, the document that represent netsegment infoes
     * make a correlation with internal OpenstackID.
     * @param dbName
     * @param fedsdnID
     * @param cloudId
     * @param internalNetworkId
     * @return 
     * @author gtricomi
     */
    public boolean storeInternalNetworkID(String dbName,String fedsdnID,String cloudId,String internalNetworkId ){
        
        DB dataBase = this.getDB(dbName);
        DBCollection collezione = this.getCollection(dataBase, "netIDMatch");
        BasicDBObject obj = new BasicDBObject();
        obj.put("fedsdnID", fedsdnID);
        obj.put("cloudId",cloudId);
        obj.put("internalNetworkId",internalNetworkId);
        obj.append("insertTimestamp", System.currentTimeMillis());
        collezione.save(obj);
        return true;
    }
    
    /**
     * This method is used to update inside "netIDMatch" collection, the document that represent netsegment infoes
     * make a correlation with internal OpenstackID.
     * @param dbName, tenant name
     * @param fedsdnID, netsegment id stored on FEDSDN
     * @param cloudId
     * @param internalNetworkId
     * @return 
     * @author gtricomi
     */
    public boolean updateInternalNetworkID(String dbName,String fedsdnID,String cloudId,String internalNetworkId ){
        
        DB dataBase = this.getDB(dbName);
        DBCollection collezione = this.getCollection(dataBase, "netIDMatch");
        BasicDBObject obj = new BasicDBObject();
        obj.put("fedsdnID", fedsdnID);
        obj.put("cloudId",cloudId);
        obj = (BasicDBObject) collezione.findOne(obj);
        if(obj==null){
            LOGGER.error("Document with fedsdnID:"+fedsdnID+" ,cloudId :"+cloudId+" for tenant :"+dbName+" isn't found!");
            return false;
        }
        obj.replace("internalNetworkId",internalNetworkId);
        obj.append("insertTimestamp", System.currentTimeMillis());
        collezione.save(obj);
        return true;
    }
    
    /**
     * 
     * @param fedsdnID
     * @param dbName, name of the Db where execute the operation
     * @return 
     * @author gtricomi
     */
    public String getInternalNetworkID(String dbName,String fedsdnID,String cloudId){
        DB dataBase = this.getDB(dbName);
        DBCollection collezione = dataBase.getCollection("netIDMatch");
        BasicDBObject obj = new BasicDBObject("fedsdnID", fedsdnID);
        obj.put("cloudId", cloudId);
        DBObject risultato = collezione.findOne(obj);
        if(risultato==null)
            return null;
        return risultato.get("internalNetworkId").toString();
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Cidr Information management">
    /**
     * 
     * @param tenantName
     * @param cidr
     * @param fednets
     * @param netsegments
     * @param cloudId 
     * @author gtricomi
     */
    public void insertcidrInfoes(
            String tenantName,
            String cidr,
            String fednets, 
            String netsegments,
            String cloudId
    ){
        DB dataBase = this.getDB(tenantName);
        DBCollection collezione = this.getCollection(dataBase, "cidrInfo");
        BasicDBObject obj = new BasicDBObject();
        obj.put("netsegments", netsegments);
        obj.put("cloudId",cloudId);
        obj.put("fednets",fednets);
        obj.put("cidr",cidr);
        obj.append("insertTimestamp", System.currentTimeMillis());
        collezione.save(obj);
        
    }
    
    /**
     * This function take from Collection "cidrInfo" the element that match with field contained inside hashmap researchParams.
     * @param tenantName, DbName where the collection is stored
     * @param researchParams, container for research params
     * @return JSONObject/null, JSONObject when the criteria match, null object in other cases.
     * @author gtricomi
     */
    public org.json.JSONObject getcidrInfoes(
            String tenantName,
            HashMap researchParams
    ) throws Exception{
        DB dataBase = this.getDB(tenantName);
        DBCollection collezione = dataBase.getCollection("cidrInfo");
        Iterator i=researchParams.keySet().iterator();
        BasicDBObject obj = new BasicDBObject();
        while(i.hasNext()){
            String tmpkey=(String)i.next();
            obj.put(tmpkey, researchParams.get(tmpkey));
        }
        DBObject risultato = collezione.findOne(obj);
        if(risultato==null)
            return null;
        try{
            return new org.json.JSONObject(risultato.toString());
        }
        catch(Exception e){
            LOGGER.error("Error occurred in DBObject parsing operation for getcidrInfoes.\n"+e.getMessage());
            throw e;
        }
     
    }
    
    //</editor-fold>
    
    
    
    /*
    This function it will be used in order to verify if the Network infos passed to construct the Fa table are corrected
    public String getNetTables(String tableName,String site_UUID_VM,String remote_UUID_VM,String tenant,DBMongo m){
        //HomeVM information retrieving
        BasicDBObject queryRunTime=new BasicDBObject("deviceId", site_UUID_VM);
        DBObject partial_RunTime_Result_H=m.find(tenant,"runTimeInfo" , queryRunTime);
        BasicDBObject queryPortInfo=new BasicDBObject("phisicalResourceId", site_UUID_VM);
        DBObject partial_PortInfo_Result_H=m.find(tenant,"runTimeInfo" , queryPortInfo);
        //RemoteVM information retrieving
        queryRunTime=new BasicDBObject("deviceId", remote_UUID_VM);
        DBObject partial_RunTime_Result_R=m.find(tenant,"runTimeInfo" , queryRunTime);
        queryPortInfo=new BasicDBObject("phisicalResourceId", remote_UUID_VM);
        DBObject partial_PortInfo_Result_R=m.find(tenant,"runTimeInfo" , queryPortInfo);
        
        
        BasicDBObject netTables_Object=new BasicDBObject(),netTable_Object=new BasicDBObject();
        
        if((tableName.equals(""))||(tableName==null))
            tableName=java.util.UUID.randomUUID().toString();
        
        //verificare presenza pregressa Table in tal caso recuperare versione
        ////serve nome collezione per questo elemento
        
        netTable_Object.put("Name",tableName);
        //inserire validazione per le stringe passate sotto????
        netTable_Object.put((String)partial_RunTime_Result_H.get("idCloud"),(String)partial_PortInfo_Result_H.get("networkId"));
        netTable_Object.put((String)partial_RunTime_Result_R.get("idCloud"),(String)partial_PortInfo_Result_R.get("networkId"));
        
        
    } */ 
    
    public boolean insertNetTable(String tenant,String jsonTable){
//        this.insert(tenant, "NetTablesInfo", jsonTable);
        return true;
    }
}
