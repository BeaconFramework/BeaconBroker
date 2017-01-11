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
package JClouds_Adapter;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.List;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.domain.NetworkSegment;
import org.jclouds.openstack.neutron.v2.domain.Networks;
import org.jclouds.openstack.neutron.v2.domain.Subnet;

/**
 * This class need to be reviewed.
 * @author agalletta
 */
public class JcloudTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        
     //   DBMongo mongo=new DBMongo();
     //   mongo.init("./cfg/configuration_bigDataPlugin.xml");
        
        //       KeystoneTest key=new KeystoneTest(mongo);

        KeystoneTest key=new KeystoneTest("admin","admin","0penstack","http://10.9.1.155:5000/v2.0");
     //   System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        key.autenticate("demo", "admin", "0penstack");
//key.serviceGet("test");
   //     key.servicetList();
    //    key.listRole();
   //   Tenant t=key.createTenant();
   //   key.createUser(t);
    //  key.listUser();
   //   key.listRole();
        key.listTenant();
        System.out.println(key.getTenantId("demo"));
   
             //   GlanceTest glance=new GlanceTest();

       //    GlanceTest glance=new GlanceTest(mongo);
        
    //    glance.listImagesdatailsMongo();
       // List l=mongo.getListDB();
       // for(int i =0;i<l.size();i++){
       //     System.out.println(l.get(i));
       // }
  
   //   GlanceTest glance=new GlanceTest(mongo);
  //     glance.listRegions();
 //       glance.createImage();
      //  glance.deleteImage();
     //   glance.listImagesdatails();
   // glance.listImages();
        
  //      NovaTest nova=new NovaTest(mongo);
  //      nova.testQuota();
        //  nova.listServer2();
      //  nova.listServer2Mongo();
       // nova.stopVm();
      //  nova.startVm();
        
      // // nova.createFlavor();
     //   nova.listFlavors();
      //   nova.createvm();
    //   nova.getStatus();
        
       
        
     //NeutronTest neutron = new NeutronTest("http://172.17.1.217:35357/v2.0","demo","admin","password","RegionOne");
     // neutron.listNetworks();
//neutron.listRegions();
        //neutron.createRouter("prova","RegionOne","f225b4d8-9da8-422f-a637-9427a4f64a7a","62da5ef7-87ed-4e49-8cd7-1bad78257080");
       // neutron.deleteNetworks();
       //neutron.createNetwork();
      // neutron.updateNetwork();
      // neutron.createNetwork2();
      // neutron.testListRouters();
   //    neutron.createSubnet();
       
       //neutron.listNetworks();
     
  /*   neutron.listSubnet();
     Networks ns=neutron.listNetworks();
    Iterator<Network> itNet = ns.iterator();
    while(itNet.hasNext()){
                Network n=itNet.next();
                Iterator<String> ti=n.getSubnets().iterator();
                
                while(ti.hasNext()){
                    Subnet s=neutron.getSubnet((String)ti.next());
                    if( s!=null){
                        System.out.println(s.getCidr());
                        System.out.println(s.toString());
                    }
                }
             
    }

        neutron.getStatus("private");
   //    neutron.listRegions();
 // neutron.printListExtension();
       //neutron.createFloading();
        //NovaTest nova=new NovaTest();
        //nova.test();
             */
            
    }
    
}
