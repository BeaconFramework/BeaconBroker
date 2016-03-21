/** Copyright 2016, University of Messina.
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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.inject.Module;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Quota;
//import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerExtendedAttributes;
import org.jclouds.openstack.nova.v2_0.domain.ServerExtendedStatus;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaApi;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
//import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.v2_0.domain.Extension;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.features.ExtensionApi;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import MDBInt.DBMongo;
/**
 * This class need to be reviewed.
 * @author agalletta
 * @author gtricomi
 */
public class NovaTest {

    private final NovaApi novaApi;
    private final Set<String> regions;
    private final String regionName;
    private DBMongo mongo;
   // private final ComputeService computeService;

    public NovaTest(DBMongo mongo, String keyEndpoint) {
        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());
     //    Iterable<Module> modules = ImmutableSet.<Module>of( );

        String provider = "openstack-nova";
        String identity = "admin:admin"; // tenantName:userName
        String credential = "password";
        this.mongo = mongo;
        this.regionName="RegionOne";
        
        novaApi = ContextBuilder.newBuilder(provider)
                .endpoint(keyEndpoint)//.endpoint("http://172.17.4.113:35357/v2.0")
                .credentials(identity, credential)
                .modules(modules)
                .buildApi(NovaApi.class);
        regions = novaApi.getConfiguredRegions();
        /*
         ComputeServiceContext context = ContextBuilder.newBuilder("openstack-nova")
         .endpoint("http://172.17.4.93:5000/v2.0")
         .credentials(identity, credential)
         .modules(modules)
         .buildView(ComputeServiceContext.class);
        
         computeService = context.getComputeService(); 
         */

    }
    
    
    public NovaTest(String endpoint, String tenant, String user, String password, String regionName) {
        // Iterable<Module> modules = ImmutableSet.<Module>of( new SLF4JLoggingModule());
        Iterable<Module> modules = ImmutableSet.<Module>of();

        String provider = "openstack-neutron";
        String identity = tenant + ":" + user; // tenantName:userName
        this.regionName = regionName;

        novaApi = ContextBuilder.newBuilder(provider)
                //   neutronApi = ContextBuilder.newBuilder(new NeutronApiMetadata())
                .endpoint(endpoint)
                .credentials(identity, password)
                .modules(modules)
                .buildApi(NovaApi.class);
        regions = novaApi.getConfiguredRegions();

    }

    public void listServers() {//okk
        for (String region : regions) {
            ServerApi serverApi = novaApi.getServerApi(region);

            System.out.println("Servers in " + region);

            for (Server server : serverApi.listInDetail().concat()) {
                System.out.println("  " + server);
                System.out.println(this.toJSON(server));
            }
        }
    }

    public ArrayList<Server> listServer2() {//ok
        ArrayList result = new ArrayList();
        ServerApi serverApi = novaApi.getServerApi("RegionOne");
        PaginatedCollection<Server> it2 = serverApi.listInDetail(new PaginationOptions());
        Iterator iter = it2.iterator();
        while (iter.hasNext()) {
////        System.out.println(it2.first());
            //System.out.println(iter.next());
            result.add((Server) iter.next());
        }
        return result;
    }
/*
    public void listServer2Mongo() {//ok

        ServerApi serverApi = novaApi.getServerApi("RegionOne");
        PaginatedCollection<Server> it2 = serverApi.listInDetail(new PaginationOptions());
        Iterator<Server> iter = it2.iterator();
        String Json;

        while (iter.hasNext()) {

            Json = this.toJSON(iter.next());
            System.out.println(Json);
            mongo.insertInCollection("RegionOne", "nova", Json);
////        System.out.println(it2.first());
            System.out.println(iter.next());
        }
    }
*/
    public void stopVm(String vmUuid) {

        ServerApi serverApi = novaApi.getServerApi(regionName);
        serverApi.stop(vmUuid);
    }

    public void startVm(String vmUuid) {

        ServerApi serverApi = novaApi.getServerApi(regionName);
        serverApi.start(vmUuid);
    }

/*
 public void createvm(){
      String groupName = "default";
 
     TemplateBuilder tb = computeService.templateBuilder();  
     tb.minRam(Integer.parseInt("512"));  
     tb.imageId("RegionOne/5d9a1799-5c5b-42a1-b257-38df0677e659");  
     NodeMetadata node1=null;  
     try {  
     node1 = getOnlyElement(computeService.createNodesInGroup(groupName, 1, tb.build()));  
     } catch (RunNodesException e) {  
     e.printStackTrace();  
     }  
     System.out.printf("<< node %s: %s%n", node1.getId(),  
     concat(node1.getPrivateAddresses(), node1.getPublicAddresses()));
     }
     */
    public void listFlavors() {

        FlavorApi flavorApi = this.novaApi.getFlavorApi(regionName);
        PaginatedCollection<Flavor> it2 = flavorApi.listInDetail(new PaginationOptions());
        Iterator iter = it2.iterator();
        while (iter.hasNext()) {
////        System.out.println(it2.first());
            System.out.println(iter.next());
        }
    }
  
    public void createvm(){//Ok
        ServerApi serverApi=novaApi.getServerApi("RegionOne");
        CreateServerOptions cs=new CreateServerOptions();
        cs.networks("927918d7-d470-40fd-aa12-ebe32bfd90f9");
        serverApi.create("jcloud-newtest", "RegionOne/9cae55d5-cd56-413e-bb11-29830150db4f", "RengionOne/84", cs);
    }


    public void test(){

          Optional<FloatingIPApi> floading=novaApi.getFloatingIPApi("RegionOne");
          System.out.println(floading.isPresent());


    }

  
   
    private String toJSON (Server server){
        JSONObject obj=new JSONObject();
        JSONObject metadata=new JSONObject();
        obj.put("id", server.getId());
        obj.put("name",server.getName());
        obj.put("links", this.linksToArray(server.getLinks()) );
        obj.put("uuid",server.getUuid());
        obj.put("tenantId",server.getTenantId());
        obj.put("userId",server.getUserId());
        obj.put("updatedAt",server.getUpdated().toString());
        obj.put("createdAt",server.getCreated().toString());
        obj.put("hostId",server.getHostId());
        obj.put("accessIPv4",server.getAccessIPv4());
        obj.put("accessIPv6",server.getAccessIPv6());
        obj.put("status",server.getStatus().toString());
        obj.put("image",resourceToJSON(server.getImage()));
        obj.put("flavor",resourceToJSON(server.getFlavor()));
        obj.put("keyName",server.getKeyName());
        obj.put("configDrive",server.getConfigDrive());
        obj.put("addresses",adressToJSON(server.getAddresses()));
        metadata.putAll(server.getMetadata());
        obj.put("metadata",metadata);
        obj.put("extendedStatus",ServerExtendedStatusToJson(server.getExtendedStatus().asSet()));
        obj.put("extendedAttributes",ServerExtendedAttributesToJson(server.getExtendedAttributes().asSet()));
        obj.put("diskConfig",server.getDiskConfig().get());
        obj.put("availabilityZone",server.getAvailabilityZone().get());
        return obj.toJSONString();
    }

    private JSONArray linksToArray(Set<Link> s){

      JSONArray links= new JSONArray();
      JSONObject link;
      Iterator<Link> i;
      Link successivo;
      i=s.iterator();

      while(i.hasNext()){
          link=new JSONObject();
          successivo=i.next();
          link.put("relation", successivo.getRelation().value());
          link.put("href", successivo.getHref().toString());
          links.add(link);
      }
      return links;
    }

    private JSONObject resourceToJSON(Resource risorsa){

        JSONObject obj=new JSONObject();

        obj.put("id", risorsa.getId());
        obj.put("name", risorsa.getName());
        obj.put("links", this.linksToArray(risorsa.getLinks()));

        return obj;
    }

    private JSONObject adressToJSON(Multimap <String,Address> map){
      
      JSONObject obj=new JSONObject();
      JSONObject indirizzoJson;
      JSONArray arrayIndirizzi=new JSONArray();
      Set<String> set=map.keySet();
      Iterator<String> itKeys=set.iterator();
      Iterator<Address> itIndirizzi;
      String key;
      Collection<Address> collIndirizzi;
      Address indirizzo;
      
      while(itKeys.hasNext()){
          
          key= itKeys.next();
          collIndirizzi=map.get(key);
          itIndirizzi=collIndirizzi.iterator();
          
          while(itIndirizzi.hasNext()){
              
              indirizzo=itIndirizzi.next();
              indirizzoJson=new JSONObject();
              indirizzoJson.put("addr", indirizzo.getAddr());
              indirizzoJson.put("version", indirizzo.getVersion());
              arrayIndirizzi.add(indirizzoJson);
          }
          
          obj.put(key, arrayIndirizzi);
      }
      return obj;
    }
 
    private JSONObject ServerExtendedStatusToJson(Set <ServerExtendedStatus> ses){

    JSONObject status=new JSONObject();
    Iterator<ServerExtendedStatus> i;
    ServerExtendedStatus successivo;
    i=ses.iterator();
    
    while(i.hasNext()){
        successivo=i.next();
        status.put("taskState", successivo.getTaskState());
        status.put("vmState", successivo.getVmState());
        status.put("powerState", successivo.getPowerState());
    }
    return status;
    }
    
    private JSONObject ServerExtendedAttributesToJson(Set <ServerExtendedAttributes> ses){

    JSONObject attributes=new JSONObject();
    Iterator<ServerExtendedAttributes> i;
    ServerExtendedAttributes successivo;
    i=ses.iterator();
    
    while(i.hasNext()){
        successivo=i.next();
        attributes.put("instanceName", successivo.getInstanceName());
        attributes.put("hostName", successivo.getHostName());
        attributes.put("hypervisorHostName", successivo.getHypervisorHostName());
    }
    return attributes;
    }
    
     
  public void getStatus(){
      ServerApi serverApi=novaApi.getServerApi("RegionOne");
      Server server=serverApi.get("e7270b60-7d21-4e53-8755-e31bc77fef24");
      System.out.println(server);
     
  }
    
    
  
    public void createFlavor() {

        FlavorApi flavorApi = this.novaApi.getFlavorApi(regionName);
        Flavor.Builder flavorBuilder = Flavor.builder();
        flavorBuilder.id("125");
        flavorBuilder.name("mytest");
        flavorBuilder.ram(244);
        flavorBuilder.disk(0);
        flavorBuilder.vcpus(1);
        flavorBuilder.swap("");
        flavorBuilder.rxtxFactor(1.0);
        flavorBuilder.ephemeral(1);

        Set<Link> set = new HashSet<Link>();
        URI uriSelf = null;
        Link l;
        try {
            uriSelf = new URI("http://172.17.4.93:8774/v2.1/77dcf0b054be45cbb936ecafcb258370/flavors/125");
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        l = Link.create(Link.Relation.SELF, "test", uriSelf);

        set.add(l);

        URI uriBookmark = null;
        try {
            uriBookmark = new URI("http://172.17.4.93:8774/77dcf0b054be45cbb936ecafcb258370/flavors/125");
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }

        l = Link.create(Link.Relation.BOOKMARK, "test", uriBookmark);
        set.add(l);

        flavorBuilder.links(set);
        Flavor flavor = flavorBuilder.build();
        System.out.println(flavor.getVcpus());
        flavorApi.create(flavor);
    }
    
    public void testQuota(){
        
        
  
    }
    
}
