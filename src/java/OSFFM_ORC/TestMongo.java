/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OSFFM_ORC;
import MDBInt.DBMongo;
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
import static org.jclouds.openstack.keystone.v2_0.options.CreateUserOptions.Builder.tenant;
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
import org.json.JSONException;
import com.mongodb.AggregationOutput;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 *
 * @author apanarello
 */
public class TestMongo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DBMongo db= new DBMongo();
        db.init("/home/apanarello/BeaconProject/newBBP/BB/web/WEB-INF/configuration_bigDataPlugin.xml");

        db.connectLocale("10.9.240.1");
        // TODO code application logic here
        DB dataBase = db.getDB_("review");
        DBCollection collezione = db.getCollection(dataBase, "provaa");
        System.out.println("DOPO COLLEZIONE"+collezione.toString());
        if(collezione==null){
            DBObject options;
            collezione=dataBase.createCollection("provaa",null);
           
    
    }
        else{
        BasicDBObject obj = (BasicDBObject) JSON.parse("{\"FK\":783487643,\"netEnt\":{ \"tenant_id\" : \"b0edb3a0ae3842b2a3f3969f07cd82f2\", \n" +
"					\"site_name\" : \"CETIC\", \n" +
"					\"vnid\" : \"d46a55d4-6cca-4d86-bf25-f03707680795\",\n" +
"					\"name\" : \"provider\" }}");
        obj.append("referenceSite", "aaabbbcccchiddu");
        obj.append("insertTimestamp", System.currentTimeMillis());
        collezione.save(obj);
        }
        //this.insert(tenant, "NetTablesInfo", josnTable);
              
        
    }

       
}
