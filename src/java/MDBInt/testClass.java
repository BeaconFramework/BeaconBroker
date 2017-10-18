/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MDBInt;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Giuseppe Tricomi
 */

public class testClass {
     public static void main(String[] args) throws JSONException {
         DBMongo m=new DBMongo();
         m.connectLocale("10.9.240.1");
         m.getfedsdnFednetIDs("demo");//getSiteTables(String dbName, String faSite, Integer version)
         System.out.println(m.getSiteTables("review","CETIC",24));
         DB database = m.getDB_(m.getIdentityDB());
       DBCollection collection = database.getCollection("Federation_Credential");
       BasicDBObject researchField = new BasicDBObject("federationTenant", "review").append("ff","eeee").append("eeee",21);
       BasicDBObject d=new BasicDBObject();
       d.put("token", 1);
       DBCursor risultato = collection.find( researchField,d);
       Iterator i=risultato.iterator();
       while(i.hasNext())
             System.out.println(i.next().toString());
        // String tmp=m.getUser("beacon","credentials", "userFederation", "20306e7ca1d77c289011e7683797cb48");
         
         
     }
}
/*
{ "_id" : "userFederation", "federationUser" : "userFederation", "federationPassword" : "20306e7ca1d77c289011e7683797cb48", "crediantialList" : [ 	{ 	"federatedUser" : "admin", 	"federatedCloud" : "UME", 	"federatedPassword" : "password" }, 	{ 	"federatedUser" : "admin", 	"federatedCloud" : "CETIC", 	"federatedPassword" : "password" }, 	{ 	"federatedUser" : "admin", 	"federatedCloud" : "UCM", 	"federatedPassword" : "password" } ], "insertTimestamp" : NumberLong("1456747122723") }
*/
