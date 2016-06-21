/** Copyright 2016, University of Messina.
 * 
* Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
* http://www.apache.org/licenses/LICENSE-2.0
 * 
* Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package MDBInt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import org.yaml.snakeyaml.Yaml;

//BEACON>>> gtricomi comment: This class need to be reorganized
/**
 *
 * @author agalletta
 * @author gtricomi
 */
public class Splitter {

    DBMongo mongoAdapter;
    static final Logger LOGGER = Logger.getLogger(Splitter.class);
   
    
    /**
     * 
     * @param mongoAdapter 
     */
    public Splitter(DBMongo mongoAdapter) {
        this.mongoAdapter = mongoAdapter;
    }

    /*
     public void convertToJson(String yamlString) {
        

     Yaml yaml;
     Map<String,String> map;

     JSONObject jsonObject;
     JSONObject output=new JSONObject( (Map) jsonObject.get("outputs"));
     //Set s=output.keySet();
     String key=(String) output.keys().next();
     System.out.println(key);
     //  JSONObject j=(JSONObject) jsonObject.get("outputs");
     System.out.println( output.get(key).toString());
        
        
     yaml= new Yaml();
     map= (Map<String, String>) yaml.load(yamlString);
     jsonObject=new JSONObject(map);
     }
     */
    /**
     * return UUID of template or null if there is some issue
     *
     * @param path
     * @param tenant
     * @return
     * @author gtricomi
     */
    public String loadFromFile(String path, String tenant) {
        InputStream input;
        Yaml yamlManifest;
        Yaml yaml = new Yaml();
        Map<String, Object> map;
        JSONObject jsonManifest, outputs, parameters, resources;
        UUID masterKey = null;
        ArrayList out, par, res;

        try {
            input = new FileInputStream(path);
            yamlManifest = new Yaml();
            map = (Map<String, Object>) yamlManifest.load(input);

            jsonManifest = new JSONObject(map);
           // System.out.println(jsonManifest);

            masterKey = UUID.randomUUID();

            outputs = (JSONObject) jsonManifest.remove("outputs");
            parameters = (JSONObject) jsonManifest.remove("parameters");
            resources = (JSONObject) jsonManifest.remove("resources");
            jsonManifest.put("masterKey", masterKey.toString());

            out = this.splitManifest(tenant, outputs, "outputs");
            par = this.splitManifest(tenant, parameters, "parameters");
            res = this.splitManifest(tenant, resources, "resources");

            jsonManifest.put("parameters", par);
            jsonManifest.put("outputs", out);
            jsonManifest.put("resources", res);

            //  System.out.println(jsonManifest);
            mongoAdapter.insert(tenant, "master", jsonManifest.toString());

        } catch (FileNotFoundException | JSONException ex) {
            ex.printStackTrace();
            return null;
        }
        return masterKey.toString();
    }

    /**
     *
     * @param yamlString
     * @param tenant
     */
    public boolean loadFromYAMLString(String yamlString, String tenant,String username,String templatename,String templateRef) {
        Yaml yamlManifest;
        Map<String, Object> map;
        JSONObject jsonManifest, outputs, parameters, resources;
        UUID masterKey;
        ArrayList out, par, res;
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
"MANIFEST ANALYSIS STARTED \n" +
"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        try {
            yamlManifest = new Yaml();
            map = (Map<String, Object>) yamlManifest.load(yamlString);

            jsonManifest = new JSONObject(map);
            System.out.println(jsonManifest);

            masterKey = UUID.randomUUID();

            outputs = (JSONObject) jsonManifest.remove("outputs");
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
"OUTPUTS ELABORATED!\n" +
"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            parameters = (JSONObject) jsonManifest.remove("parameters");
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
"PARAMETERS ELABORATED! \n" +
"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            resources = (JSONObject) jsonManifest.remove("resources");
System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
"RESOURCES ELABORATED! \n" +
"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");            
            jsonManifest.put("masterKey", masterKey.toString());
System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
"MASTERKEY ASSIGNED: ed1291c0-a1fd-405b-a775-ab5e1c502858  \n" +
"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            out = this.splitManifest(tenant, outputs, "outputs");
            par = this.splitManifest(tenant, parameters, "parameters");
            res = this.splitManifest(tenant, resources, "resources");

            jsonManifest.put("parameters", par);
            jsonManifest.put("outputs", out);
            jsonManifest.put("resources", res);

            //    System.out.println(jsonManifest);
            mongoAdapter.insert(tenant, "master", jsonManifest.toString());
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
"MANIFEST IS INSERTED INSIDE MONGODB\n" +
"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            Float version=null;
            if(templateRef.equals("null"))
                version=new Float(0.0);
            else{
                //System.out.println("Cerchiamo Versione");
                Float tmpfloat=mongoAdapter.getVersion(tenant, "templateInfo", templateRef);
                version=new Float(tmpfloat.floatValue()+0.1);
            }
           // System.out.println("Inserimento");
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
"MANAGEMENT INFORMATION ARE INSERTED INSIDE MONGODB\n" +
"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            mongoAdapter.insertTemplateInfo(tenant, masterKey.toString(), templatename, version, username, templateRef);
            //BEACON>>> inserire tutti i metadati all'interno di una nuova collezione , 
            //per poi restituirli alla dashboard per il management ed il retrieving delle informazioni
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     *
     * @param jsonString
     * @param tenant
     */
    public void loadFromJsonManifest(String jsonString, String tenant) {

        JSONObject jsonManifest, outputs, parameters, resources;
        UUID masterKey;
        ArrayList out, par, res;

        try {

            jsonManifest = new JSONObject(jsonString);
            //System.out.println(jsonManifest);

            masterKey = UUID.randomUUID();

            outputs = (JSONObject) jsonManifest.remove("outputs");
            parameters = (JSONObject) jsonManifest.remove("parameters");
            resources = (JSONObject) jsonManifest.remove("resources");
            jsonManifest.put("masterKey", masterKey.toString());

            out = this.splitManifest(tenant, outputs, "outputs");
            par = this.splitManifest(tenant, parameters, "parameters");
            res = this.splitManifest(tenant, resources, "resources");

            jsonManifest.put("parameters", par);
            jsonManifest.put("outputs", out);
            jsonManifest.put("resources", res);

            // System.out.println(jsonManifest);
            mongoAdapter.insert(tenant, "master", jsonManifest.toString());

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param tenant
     * @param jsonObject
     * @param type
     * @return
     */
    private ArrayList splitManifest(String tenant, JSONObject jsonObject, String type) {

        Iterator<String> keysIterator;
        JSONObject figlio, risorsaNipote, properties;
        String key;
        UUID uuid;
        ArrayList uuidsList = new ArrayList();
        ArrayList children;

        //keys=jsonObject.entrySet();
        keysIterator = jsonObject.keys();

        while (keysIterator.hasNext()) {
            key = keysIterator.next();
            uuid = UUID.randomUUID();
            uuidsList.add(uuid);
            try {
                figlio = jsonObject.getJSONObject(key);

                figlio.put("nome", key);
                figlio.put("uuid", uuid);

                if (type.equalsIgnoreCase("resources")) {
                    properties = figlio.getJSONObject("properties");
                    risorsaNipote = (JSONObject) properties.remove("resource");
                    if (risorsaNipote != null) {
                        children = this.splitManifest(tenant, risorsaNipote, "resource");
                        figlio.put("resource", children);
                    }
                }
                mongoAdapter.insert(tenant, type, figlio.toString());
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return uuidsList;
    }

    /* 
     private void insert(String dbName,String collName, String document){
     DB db= mongo.getDB(dbName);
     DBCollection coll= db.getCollection(collName);
     BasicDBObject obj=(BasicDBObject) JSON.parse(document);
     coll.insert(obj);
     }
     */
    /*
     private String getObj(String dbName,String collName, String query){
    
        
     BasicDBObject constrains= null, results=null;
     DBCursor cursore;
     DB db= mongo.getDB(dbName);
     DBCollection coll= db.getCollection(collName);
     BasicDBObject obj=(BasicDBObject) JSON.parse(query);
     constrains=new BasicDBObject("_id",0);
     cursore=coll.find(obj,constrains);
     try{
     results= (BasicDBObject) cursore.next();
     }
     catch(NoSuchElementException e){
     System.out.println("manifest non trovato!");
     return null;
     }
     return results.toString(); 
     }
     */
    /**
     *
     * @param uuid
     * @param tenant
     * @return
     */
    public String ricomponiJsonManifest(String uuid, String tenant) {
        String elemento;
        String parameters = "parameters";
        String resources = "resources";
        String output = "outputs";
        JSONObject query = new JSONObject();
        JSONObject manifest = null;
        JSONArray resUuidList, parUuidList, outUuidList;

        try {
            query.put("masterKey", uuid);
            try {
                manifest = new JSONObject(mongoAdapter.getObj(tenant, "master", query.toString()));
            } catch (MDBIException ex) {
                LOGGER.error(ex.getMessage());
            }
            manifest.remove("masterKey");

            resUuidList = (JSONArray) manifest.remove(resources);
            elemento = this.ricomponiFigli(resUuidList, resources, tenant);
            manifest.put(resources, new JSONObject(elemento));

            parUuidList = (JSONArray) manifest.remove(parameters);
            elemento = this.ricomponiFigli(parUuidList, parameters, tenant);
            manifest.put(parameters, new JSONObject(elemento));

            outUuidList = (JSONArray) manifest.remove(output);
            elemento = this.ricomponiFigli(outUuidList, output, tenant);
            manifest.put(output, new JSONObject(elemento));

            Yaml yaml = new Yaml();
            String prettyJSONString = manifest.toString();
            Map<String, Object> map2 = (Map<String, Object>) yaml.load(prettyJSONString);
            String outputti = yaml.dump(map2);

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return manifest.toString();
    }

    /**
     *
     * @param uuid
     * @param tenant
     * @return
     */
    public String ricomponiYamlManifest(String uuid, String tenant) throws MDBIException {
        String elemento;
        String parameters = "parameters";
        String resources = "resources";
        String output = "outputs";
        JSONObject query = new JSONObject();
        JSONObject manifest = null;
        JSONArray resUuidList, parUuidList, outUuidList;
        String outputti = null;

        try {
            query.put("masterKey", uuid);
            try{
                manifest = new JSONObject(mongoAdapter.getObj(tenant, "master", query.toString()));
            }
            catch(MDBIException e){
                throw e;
            }
            manifest.remove("masterKey");

            resUuidList = (JSONArray) manifest.remove(resources);
            elemento = this.ricomponiFigli(resUuidList, resources, tenant);
            manifest.put(resources, new JSONObject(elemento));

            parUuidList = (JSONArray) manifest.remove(parameters);
            elemento = this.ricomponiFigli(parUuidList, parameters, tenant);
            manifest.put(parameters, new JSONObject(elemento));

            outUuidList = (JSONArray) manifest.remove(output);
            elemento = this.ricomponiFigli(outUuidList, output, tenant);
            manifest.put(output, new JSONObject(elemento));

            Yaml yaml = new Yaml();
            String prettyJSONString = manifest.toString();
            Map<String, Object> map2 = (Map<String, Object>) yaml.load(prettyJSONString);
            outputti = yaml.dump(map2);

        } catch (JSONException ex) {
            //BEACON>>> inserire logger
            ex.printStackTrace();
        }
        System.out.println(outputti);
        return outputti;
    }

    /**
     *
     * @param elem
     * @param type
     * @param tenant
     * @return
     */
    private String ricomponiFigli(JSONArray elem, String type, String tenant) {

        JSONObject elemToRetrive, elemFromDb, query, properties;
        int count;
        String uuid;
        JSONArray resUuidList = null;
        String nome, resource;

        elemToRetrive = new JSONObject();
        query = new JSONObject();

        for (count = 0; count < elem.length(); count++) {
            try {
                uuid = elem.getString(count);
                query.put("uuid", uuid);
                elemFromDb = new JSONObject(mongoAdapter.getObj(tenant, type, query.toString()));

                if (type.equals("resources")) {
                    resUuidList = (JSONArray) elemFromDb.remove("resource");

                    if (resUuidList != null) {
                        resource = this.ricomponiFigli(resUuidList, "resource", tenant);
                        properties = elemFromDb.getJSONObject("properties");
                        properties.put("resource", new JSONObject(resource));
                        //appendi i figli
                    }
                }
                elemFromDb.remove("uuid");
                nome = (String) elemFromDb.remove("nome");
                // System.out.println("nome:" +nome);
                elemToRetrive.put(nome, elemFromDb);
                // System.out.println(elemToRetrive);
            } catch (JSONException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return elemToRetrive.toString();
    }

    /* 
     public void intersectPoint(long latitude, long longitude,String tenant){
    
     DB database=mongo.getDB(tenant);
     DBCollection collection=database.getCollection("resources");
     BasicDBObject geoJSON=new BasicDBObject();
     BasicDBList arrayCoordinate=new BasicDBList();
     BasicDBObject geometry=new BasicDBObject();
     BasicDBObject geoSpazialOperator=new BasicDBObject();
     BasicDBObject query=new BasicDBObject();
     BasicDBObject constrains=new BasicDBObject("_id",0);

     arrayCoordinate.add(longitude);
     arrayCoordinate.add(latitude);
     geoJSON.put("type", "Point");
     geoJSON.put("coordinates", arrayCoordinate);
     //System.out.println("geoJSON: "+geoJSON);
        
     geometry.put("$geometry", geoJSON);
     //System.out.println(geometry);
        
     geoSpazialOperator.put("$geoIntersects", geometry);
     //System.out.println(geoSpazialOperator);
        
     query.put("properties.points.geometry", geoSpazialOperator);
     // System.out.println("query: "+query);
        
     DBCursor cursore=collection.find(query,constrains);
        
     Iterator it=cursore.iterator();
     while(it.hasNext()){
        
     System.out.println("result: "+it.next());
        
     }
        
     //collection.find()
    
    
    
     }
 
      
     public void intersectPolygon(){
    
     DB database=mongo.getDB("yamlTest");
     DBCollection collection=database.getCollection("resources");
     BasicDBObject geoJSON=new BasicDBObject();
     BasicDBList arrayCoordinate=new BasicDBList();
     BasicDBList poligono=new BasicDBList();
     BasicDBList point=new BasicDBList();
     BasicDBObject geometry=new BasicDBObject();
     BasicDBObject geoSpazialOperator=new BasicDBObject();
     BasicDBObject query=new BasicDBObject();
     BasicDBObject constrains=new BasicDBObject("_id",0);

     point.add(70.601407);
     point.add(40.218527);
     arrayCoordinate.add(point);
        
     point=new BasicDBList();
     point.add(51.064453);
     point.add(40.478292);
     arrayCoordinate.add(point);
        
     point=new BasicDBList();
     point.add(60.732422);
     point.add(32.581535);
     arrayCoordinate.add(point);
        
     point=new BasicDBList();
     point.add(70.601407);
     point.add(40.218527);
     arrayCoordinate.add(point);
        
     poligono.add(arrayCoordinate);
        
     geoJSON.put("type", "Polygon");
     geoJSON.put("coordinates", poligono);
     //System.out.println("geoJSON: "+geoJSON);
        
     geometry.put("$geometry", geoJSON);
     //System.out.println(geometry);
        
     geoSpazialOperator.put("$geoIntersects", geometry);
     //System.out.println(geoSpazialOperator);
        
     query.put("properties.points.geometry", geoSpazialOperator);
     System.out.println("query: "+query);
        
     DBCursor cursore=collection.find(query,constrains);
        
     Iterator it=cursore.iterator();
     while(it.hasNext()){
        
     System.out.println("result: "+it.next());
        
     }
        
     //collection.find()
    
    
    
     }
     */
}
