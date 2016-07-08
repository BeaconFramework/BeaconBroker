/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package API.SOUTHBR;

import JClouds_Adapter.NeutronTest;
import MDBInt.DBMongo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.ws.rs.core.Response;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class used to test FA API. 
 * @author Giuseppe Tricomi
 */
public class test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //testing activity use hardcoded parameter that could be retrieved from federated Cloud
        String t1name,t1Id,t2name,t2Id;
        t1name ="demo";
        t2name = "demo";
        t1Id="ab6a28b9f3624f4fa46e78247848544e";
        t2Id="0ce39f6ae8044445b31d5b7f9b34062b";
        //FAclient4 tenant istantiation. 
  ///*   
        FA_client4Tenant fat1=new FA_client4Tenant("http://10.9.240.20:5000/v2.0",t1name,"admin","password");
        FA_client4Tenant fat2=new FA_client4Tenant("http://10.9.240.11:5000/v2.0",t2name,"admin","password");
        try{
            //System.out.println("tid1: "+fat1.getID()+"\ntid2: "+fat2.getID());
            boolean res=fat1.createTenantFA(t1Id, "10.9.240.21:4567");
            System.out.println("Result of tenant 1 inserting operation on FA is: "+res);
            res=fat2.createTenantFA(t2Id, "10.9.240.7:4567");
            
            System.out.println("Result of tenant 2 inserting operation on FA is: "+res);
            System.out.println(fat1.getTenantList("10.9.240.21:4567").toString());
        }
        catch(Exception e ){
            System.out.println("Error1");
            e.printStackTrace();
        }
   //   */
        //FAclient4sites istantiation. 
        ////what is missed is how to retrieve siteName
  // /*
        FA_client4Sites fas1=new FA_client4Sites("http://10.9.240.20:5000/v2.0",t1name,"admin","password");
        FA_client4Sites fas2=new FA_client4Sites("http://10.9.240.11:5000/v2.0",t2name,"admin","password");
        //create MAP in hardcoded way for testing api
        try{
        ArrayList<HashMap<String,Object>> sites=new ArrayList<HashMap<String,Object>>();
        HashMap<String,Object> site1=new HashMap<String,Object>();
        site1.put("name", "site1");
        site1.put("tenant_id", "ab6a28b9f3624f4fa46e78247848544e");
        site1.put("fa_url", "10.0.0.33:4567");
        site1.put("site_proxyip","10.0.0.33");
        site1.put("site_proxyport", 4789);
        //site1.put("site_proxy", "[{\"ip\": \"10.0.0.33\", \"port\": 4789}]");
        HashMap<String,Object> site2=new HashMap<String,Object>();
        site2.put("name", "site2");
        site2.put("tenant_id", "0ce39f6ae8044445b31d5b7f9b34062b");
        site2.put("fa_url", "10.0.0.38:4567");
        
        site2.put("site_proxyip","10.0.0.38");
        site2.put("site_proxyport", 4789);
        
        sites.add(site1);
        sites.add(site2);
        //end
        
            String body=fas1.constructSiteTableJSON(sites);
            System.out.println(body);
            boolean res=fas1.createSiteTable(t1Id, "10.9.240.21:4567", body);
            System.out.println("Result of sites 1 inserting operation on FA is: "+res);
            //res=fat2.createTenantFA(t2Id, "10.9.240.7:4567");
            res=fas2.createSiteTable(t2Id, "10.9.240.7:4567", body);
            System.out.println("Result of sites 2 inserting operation on FA is: "+res);
        }
        catch(Exception e ){
            System.out.println("Error1");
            e.printStackTrace();
        }
   // */ 
        //FAclient4networks istantiation.
        FA_client4Network fan1=new FA_client4Network("http://10.9.240.20:5000/v2.0",t1name,"admin","password");
        FA_client4Network fan2=new FA_client4Network("http://10.9.240.11:5000/v2.0",t2name,"admin","password");
        //neutron retrieving networks
        NeutronTest neu1=new NeutronTest("http://10.9.240.20:5000/v2.0",t1name,"admin","password","RegionOne");
        NeutronTest neu2=new NeutronTest("http://10.9.240.11:5000/v2.0",t2name,"admin","password","RegionOne");
        /*
        Iterator<Network>itn1=neu1.listNetworks();
        Iterator<Network>itn2=neu2.listNetworks();
        */
        //body for request creations
      /*
        JSONArray jaexternal=new JSONArray();
        JSONArray ja=new JSONArray();
        while(itn1.hasNext()){
            Network n1=itn1.next();
            boolean first=true;
            while(itn2.hasNext()){
                Network n2=itn2.next();
                if(n1.getName().equals(n2.getName()))
                {       
                    JSONObject j1=new JSONObject();
                    j1.put("name", n1.getName());
                    j1.put("vnid", n1.getId());
                    j1.put("site_name", "site1");
                    j1.put("tenant_id", n1.getTenantId());
                    ja.put(j1);
                    JSONObject j2=new JSONObject();
                    j2.put("name", n2.getName());
                    j2.put("vnid", n2.getId());
                    j2.put("site_name", "site2");
                    j2.put("tenant_id",n2.getTenantId());
                    ja.put(j2);
          
                }
            }
        }
        jaexternal.put(ja);
      */
    //  /*
        try{
        ArrayList<ArrayList<HashMap<String,Object>>> networks=new ArrayList<ArrayList<HashMap<String,Object>>>();
        ArrayList<HashMap<String,Object>> matchingNets=new ArrayList<HashMap<String,Object>>();
        HashMap<String,Object> network1=new HashMap<String,Object>();
        network1.put("site_name", "site1");
        network1.put("tenant_id", "ab6a28b9f3624f4fa46e78247848544e");
        network1.put("name", "private");
        network1.put("vnid","c926e107-3292-48d4-a36b-f72fa81507dd");
        HashMap<String,Object> network2=new HashMap<String,Object>();
        network2.put("site_name", "site2");
        network2.put("tenant_id", "0ce39f6ae8044445b31d5b7f9b34062b");
        network2.put("name", "private");
        network2.put("vnid","d2c11d66-fb61-4438-819c-c562e108dbb5");
        
        matchingNets.add(network1);
        matchingNets.add(network2);
        networks.add(matchingNets);
        String body=fan1.constructNetworkTableJSON(networks, 111);
        //prima di passare le tabelle al FA si potrebbero scrivere su Mongo in modo da capire se sono correttamente aggiornate
        DBMongo m=new DBMongo();
        m.init();
        m.connectLocale(m.getMdbIp());
        //qui si stà condividendo la stessa table con entrambe le cloud quindi si dovrà memorizzare la stessa tabella per entrambe le cloud federate. 
        ////Il caso genericon prevede che si inserisca per ogni sito una tabella diversa che può ontenere anche riferimenti precedentemente presenti riferiti a link verso altre cloud
        m.insertNetTable(t1name, body);
        m.insertNetTable(t2name, body);
            System.out.println(body);
        Response r=fan1.createNetTable(t1Id, "10.9.240.21:4567", body);
            System.out.println(r.toString());
            r=fan2.createNetTable(t2Id, "10.9.240.7:4567", body);
            System.out.println(r.toString());
        
        }
        catch(Exception e){
            System.out.println("Error1");
            e.printStackTrace();
            
        }
   // */              
    }
    
}
