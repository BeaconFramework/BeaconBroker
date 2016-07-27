/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MDBInt;

import org.json.JSONObject;

/**
 *
 * @author Giuseppe Tricomi
 */
public class testClass {
     public static void main(String[] args) {
         DBMongo m=new DBMongo();
         m.connectLocale("172.17.2.32");
         String tmp=m.getUser("beacon","credentials", "userFederation", "20306e7ca1d77c289011e7683797cb48");
         try{
             JSONObject l= new JSONObject(tmp);
             FederationUser fu=new FederationUser(l.toString());
            // m.insertUser("beacon", "credentials", fu.toString());
             
         }
         catch(Exception e){}
         
     }
}
/*
{ "_id" : "userFederation", "federationUser" : "userFederation", "federationPassword" : "20306e7ca1d77c289011e7683797cb48", "crediantialList" : [ 	{ 	"federatedUser" : "admin", 	"federatedCloud" : "UME", 	"federatedPassword" : "password" }, 	{ 	"federatedUser" : "admin", 	"federatedCloud" : "CETIC", 	"federatedPassword" : "password" }, 	{ 	"federatedUser" : "admin", 	"federatedCloud" : "UCM", 	"federatedPassword" : "password" } ], "insertTimestamp" : NumberLong("1456747122723") }
*/
