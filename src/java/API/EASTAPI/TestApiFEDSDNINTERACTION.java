/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package API.EASTAPI;

import API.EASTAPI.Clients.*;
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
       
            EastBrRESTClient ea= new EastBrRESTClient("root","fedsdn");
            String fedsdnURL="http://10.9.0.14:6121";
            String user="root",password="fedsdn";
//FEDNET TESTING SECTION
            Fednet f=new Fednet(user,password);
            
            try {
                //System.out.println(f.getAllNet(fedsdnURL).readEntity(String.class));   //TESTED
                String fednetname="MyFirstFN";
                System.out.println(f.getNetinfo(fedsdnURL,fednetname ).readEntity(String.class));    //TESTED
                long fedid=1;
                //System.out.println(f.getNetinfo(fedsdnURL,fedid ).readEntity(String.class));     //TESTED
                fednetname="MyFirstFNbc";
                String linkType="FullMesh";
                String type="L2";
                System.out.println(f.createFednet(fednetname, linkType, type, fedsdnURL).readEntity(String.class));//CREATE        //TESTED
                fedid=3;
                //System.out.println(f.updateFednet(fedid, fednetname, linkType, type, fedsdnURL).readEntity(String.class));//UPDATE        //TESTED
                fedid=3;
                //System.out.println(f.delNetwork(fedsdnURL, fedid).readEntity(String.class));//DELETE        //TESTED
            } catch (WSException ex) {
                Logger.getLogger(TestApiFEDSDNINTERACTION.class.getName()).log(Level.SEVERE, null, ex);
            }
//SITE TESTING SECTION
            Site s=new Site(user,password);
            try {
                JSONArray ja=new JSONArray(s.getAllSite(fedsdnURL).readEntity(String.class));
                System.out.println("\n\n\n\n\nGETALLSTITE"+ja.toString()+"\n\n\n\n"+((JSONObject)ja.get(1)).toString());        //TESTED
                String sitename="MyFirstSite2";
                //System.out.println(s.getSiteInfoes(fedsdnURL, sitename).readEntity(String.class));        //TESTED
                long siteid=2;
                //System.out.println(s.getSiteInfoes(fedsdnURL, siteid).readEntity(String.class));        //TESTED
                String cmp_endpoint="http://opennebula.cloud.org:2633/RPC2";
                String type="openstack";
                sitename="MyFirstSiteTEst";
                System.out.println("CREATE\n\n"+s.createSite(sitename, cmp_endpoint, type, fedsdnURL).readEntity(String.class));//CREATE        //TESTED
                siteid=3;
                //System.out.println(s.updateSite(siteid, sitename, cmp_endpoint, type, fedsdnURL).readEntity(String.class));//UPDATE        //TESTED
                //System.out.println(s.delSite(fedsdnURL, siteid).readEntity(String.class));//DELETE        //TESTED
            } catch (WSException ex) {
                Logger.getLogger(TestApiFEDSDNINTERACTION.class.getName()).log(Level.SEVERE, null, ex);
            }
//TENANT TESTING SECTION            
            Tenant t=new Tenant(user,password);
            try {
                System.out.println(t.getAllTenant(fedsdnURL).readEntity(String.class));        //TESTED
                String tenantname="MyFirstSite2";
                System.out.println(t.getTenantInfoes(fedsdnURL, tenantname).readEntity(String.class));
                long tenantid=1;
                System.out.println(t.getInofesTenant(fedsdnURL, tenantid).readEntity(String.class));    
                String cmp_endpoint="http://opennebula.cloud.org:2633/RPC2";
                String type="openstack";
                tenantname="MyFirstSiteTEst";
                JSONObject j=new JSONObject("{\"name\" : \"root\"," +
                    " \"password\": \"fedsdn\"," +
                    " \"type\": \"admin\"," +
                    " \"valid_sites\": [{\"site_id\" : \"3\",\"user_id_in_site\": \"1\", \"credentials\": \"oneadmin:opennebula\"}]}");
                System.out.println(t.createTen(j, fedsdnURL).readEntity(String.class));//CREATE    
                tenantid=3;
                System.out.println(t.updateTen(j, fedsdnURL, tenantid).readEntity(String.class));//UPDATE     
                System.out.println(t.delTen(fedsdnURL, tenantid).readEntity(String.class));//DELETE     
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
