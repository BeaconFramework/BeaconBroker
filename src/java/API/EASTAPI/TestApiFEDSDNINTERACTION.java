/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package API.EASTAPI;

import API.EASTAPI.Clients.*;
import MDBInt.DBMongo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Exception.WSException;

/**
 *
 * @author Giuseppe Tricomi
 */
public class TestApiFEDSDNINTERACTION {

    /**
     * This Main is used to test FEDSDN API.
     * @param args the command line arguments
     */
    public static void main(String[] args) throws JSONException {

            String user="prova1";
            String password="pass1";
        
            //EastBrRESTClient ea= new EastBrRESTClient("NotManagedUser@userFederation@UME","passwordFederation");//("root","fedsdn");
            //EastBrRESTClient ea= new EastBrRESTClient("prova1","pass1");
            String fedsdnURL="http://10.9.0.26:6121";
            //String user="NotManagedUser@userFederation@UME",password="passwordFederation";

            
            //SITE TESTING SECTION
            Site s=new Site(user,password);
            try {
                JSONArray ja=new JSONArray(s.getAllSite(fedsdnURL).readEntity(String.class));
                //System.out.println("\n\n\n\n\nGETALLSTITE"+ja.toString()+"\n\n\n\n"+((JSONObject)ja.get(1)).toString());        //TESTED
                String sitename="MyFirstSite2";
                //System.out.println(s.getSiteInfoes(fedsdnURL, sitename).readEntity(String.class));        //TESTED
                long siteid=2;
                //System.out.println(s.getSiteInfoes(fedsdnURL, siteid).readEntity(String.class));        //TESTED
                String cmp_endpoint="http://172.17.1.217:35357/v2.0";
               // String type="openstack";
                sitename="realTest";
                //System.out.println("CREATE\n\n"+s.createSite(sitename, cmp_endpoint, type, fedsdnURL).readEntity(String.class));//CREATE        //TESTED
                siteid=3;
                //System.out.println(s.updateSite(siteid, sitename, cmp_endpoint, type, fedsdnURL).readEntity(String.class));//UPDATE        //TESTED
                //System.out.println(s.delSite(fedsdnURL, siteid).readEntity(String.class));//DELETE        //TESTED
            } catch (WSException ex) {
                Logger.getLogger(TestApiFEDSDNINTERACTION.class.getName()).log(Level.SEVERE, null, ex);
            }
            //FEDNET TESTING SECTION
            Fednet f=new Fednet(user,password);
            try {
                //System.out.println(f.getAllNet(fedsdnURL).readEntity(String.class));   //TESTED
                String fednetname="FNPr55555ova";
                System.out.println("$$$$$$$$$$$");
              //  System.out.println(f.getNetinfo(fedsdnURL,fednetname ).readEntity(String.class));    //TESTED
                long fedid=1;
                //System.out.println(f.getNetinfo(fedsdnURL,fedid ).readEntity(String.class));     //TESTED
                fednetname="stanco";
                String linkType="FullMesh";
                String type="L2";
                String test=f.createFednet(fednetname, linkType, type, fedsdnURL).readEntity(String.class);
                System.out.println(test);//CREATE        //TESTED
                JSONObject jjjj=new JSONObject(test);
                fedid=2;
                //fednetname="ModifiedMyFirstFNbc";
                //System.out.println(f.updateFednet(fedid, fednetname, linkType, type, fedsdnURL,null).readEntity(String.class));//UPDATE        //TESTED
                //fedid=3;
                System.out.println(f.getAllNet(fedsdnURL).readEntity(String.class));   //TESTED
                //System.out.println(f.delNetwork(fedsdnURL, fedid).readEntity(String.class));//DELETE        //TESTED
            } catch ( utils.Exception.WSException500 wse){
                System.out.println("$$$$$$$$$$$");
                System.out.println(wse.getMessage());
            }
            catch (WSException ex) {
                Logger.getLogger(TestApiFEDSDNINTERACTION.class.getName()).log(Level.SEVERE, null, ex);
            }
            //NETSEGMENT SECTION
            NetworkSegment nt=new NetworkSegment(user,password);
            try {
                System.out.println("NETSEGALL: \n"+nt.getAllNetSegm(fedsdnURL,5,8).readEntity(String.class));        //TESTED
                String tenantname="MyFirstSite2";
                //System.out.println(nt.getInofesNetSegm(fedsdnURL,1,4,2).readEntity(String.class));
                long tenantid=1;
                /*System.out.println(nt.getInofesTenant(fedsdnURL, tenantid).readEntity(String.class));    
                String cmp_endpoint="http://opennebula.cloud.org:2633/RPC2";
                String type="openstack";
                tenantname="MyFirstSiteTEst";*/
                JSONObject j=new JSONObject("{\"name\" : \"prova\", \"fa_endpoint\" : \"http://10.0.0.1:4054\",\"network_address\" : \"10.0.0.1\",\"network_mask\" : \"255.255.255.0\", \"size\": \"255\", \"vlan_id\": \"908\", \"cmp_net_id\": \"55b24c84-b96a-45ab-b007-9eee9c487c31\" }");

                /*JSONObject j=new JSONObject("{\"name\" : \"root\"," +
                    " \"password\": \"fedsdn\"," +
                    " \"type\": \"admin\"," +
                    " \"valid_sites\": [{\"site_id\" : \"3\",\"user_id_in_site\": \"1\", \"credentials\": \"oneadmin:opennebula\"}]}");*/
                
                //System.out.println(nt.createNetSeg(j, fedsdnURL,5,8).readEntity(String.class));//CREATE    
            } 
            catch (WSException ex) {
                Logger.getLogger(TestApiFEDSDNINTERACTION.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (Exception e){
                Logger.getLogger(TestApiFEDSDNINTERACTION.class.getName()).log(Level.SEVERE, null, e);
            }


            //TENANT TESTING SECTION            
            Tenant t=new Tenant(user,password);
            try {
                System.out.println(t.getAllTenant(fedsdnURL).readEntity(String.class));        //TESTED
                String tenantname=user;
                System.out.println(t.getTenantInfoes(fedsdnURL, tenantname).readEntity(String.class));
                long tenantid=1;
                System.out.println(t.getInfoTenant(fedsdnURL, tenantid).readEntity(String.class));    
                String cmp_endpoint="http://opennebula.cloud.org:2633/RPC2";
                //String type="openstack";
                //tenantname="MyFirstSiteTEst";
                JSONObject j=new JSONObject("{\"name\" : \""+user+"\"," +
                    " \"password\": \""+password+"\"," +
                    " \"type\": \"admin\"," +
                    " \"valid_sites\": [{\"site_id\" : \"8\",\"user_id_in_site\": \"26\", \"credentials\": \"admin@admin:prova\"}]}");
                /*
                System.out.println(t.createTen(j, fedsdnURL).readEntity(String.class));//CREATE    
                tenantid=5;
                System.out.println(t.updateTen(j, fedsdnURL, tenantid).readEntity(String.class));//UPDATE     
                //System.out.println(t.delTen(fedsdnURL, tenantid).readEntity(String.class));//DELETE     
                */
            } 
            catch (WSException ex) {
                Logger.getLogger(TestApiFEDSDNINTERACTION.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (Exception e){
                Logger.getLogger(TestApiFEDSDNINTERACTION.class.getName()).log(Level.SEVERE, null, e);
            }
            
            
            
            
            
            /*
            
            Response makeSimpleRequest = ea.makeSimpleRequest("http://10.9.0.14:6121/fednet/site", null, "get");
            try {
            System.out.println(makeSimpleRequest.getStatus());
            String tmp=makeSimpleRequest.readEntity(String.class);
            System.out.println(tmp);
            org.json.simple.parser.JSONParser p=new org.json.simple.parser.JSONParser();
            Object obj=p.parse(tmp);
            org.json.simple.JSONArray j;
            String name="MyFirstSite2";
            if(obj instanceof org.json.simple.JSONArray){
            j=(org.json.simple.JSONArray)obj;
            Iterator i=j.iterator();
            while(i.hasNext()){
            org.json.simple.JSONObject t=(org.json.simple.JSONObject)i.next();
            if(((String)t.get("name")).equals(name))
            System.out.println(t.get("id"));
            }
            }
            if(obj instanceof org.json.simple.JSONObject){
            if(((String)((org.json.simple.JSONObject)obj).get("name")).equals(name))
            System.out.println(((org.json.simple.JSONObject)obj).get("id"));
            }
            }
            /*tmp=tmp.substring(1,tmp.lastIndexOf("]"));
            
            String [] arstr=tmp.split("{");
            ArrayList<org.json.JSONObject> ar=new ArrayList<org.json.JSONObject>();
            for(String substr : arstr){
            ar.add(new org.json.JSONObject(substr));
            
            }
            for(org.json.JSONObject j : ar)
            System.out.println(j.toString());
            
            //       org.json.JSONArray ja=new org.json.JSONArray(makeSimpleRequest.readEntity(String.class));
            catch (Exception ex) {
            Logger.getLogger(TestApiFEDSDNINTERACTION.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        
    }

}
