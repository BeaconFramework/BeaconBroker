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

import MDBInt.DBMongo;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
//import java.util.Spliterator;
import org.apache.log4j.Logger;
import static org.jclouds.Constants.PROPERTY_LOGGER_WIRE_LOG_SENSITIVE_INFO;
import org.jclouds.ContextBuilder;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.io.payloads.FilePayload;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.glance.v1_0.GlanceApi;
import org.jclouds.openstack.glance.v1_0.domain.ContainerFormat;
import org.jclouds.openstack.glance.v1_0.domain.DiskFormat;
import org.jclouds.openstack.glance.v1_0.domain.Image;
import org.jclouds.openstack.glance.v1_0.domain.ImageDetails;
import org.jclouds.openstack.glance.v1_0.features.ImageApi;
import org.jclouds.openstack.glance.v1_0.options.CreateImageOptions;
import org.jclouds.openstack.glance.v1_0.options.ListImageOptions;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;

/**
 * This class need to be reviewed.
 * @author agalletta
 */
public class GlanceTest {
    private final GlanceApi glanceApi;
    private  Set<String> regions;
    private DBMongo mongo;
    Properties overrides;
    static final Logger LOGGER = Logger.getLogger(GlanceTest.class);
 
    
public GlanceTest(DBMongo mongo) {
        //Iterable<Module> modules = ImmutableSet.<Module>of( new SLF4JLoggingModule());
        Iterable<Module> modules = ImmutableSet.<Module>of( );
        this.mongo=mongo;
        String provider = "openstack-glance";
        String identity = "demo:admin"; // tenantName:userName
        String credential = "password";

        glanceApi = ContextBuilder.newBuilder(provider)
                .endpoint("http://172.17.4.113:5000/v2.0")
                .credentials(identity, credential)
                .modules(modules)
                .buildApi(GlanceApi.class);
        regions = glanceApi.getConfiguredRegions();
    }
 
 
    public GlanceTest() {
        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());
        //Iterable<Module> modules = ImmutableSet.<Module>of( );
        // this.mongo=mongo;
        String provider = "openstack-glance";
        String identity = "demo:admin"; // tenantName:userName
        String credential = "password";

        overrides = new Properties();
        overrides.setProperty(PROPERTY_LOGGER_WIRE_LOG_SENSITIVE_INFO, "true");

        glanceApi = ContextBuilder.newBuilder(provider)
               // .endpoint("http://172.17.1.217:5000/v2.0")
                .endpoint("http://172.17.3.142:8000/test")
                .credentials(identity, credential)
                .modules(modules)
                .overrides(overrides)
                .buildApi(GlanceApi.class);
        regions = glanceApi.getConfiguredRegions();
    }

 public void listImages(){//OK
     
    ImageApi imageApi= glanceApi.getImageApi("RegionOne");
    PagedIterable it=imageApi.list();
    PaginatedCollection<Image> it2=imageApi.list(new ListImageOptions());
    Image immagine;
    Iterator  iter=it2.iterator();
    ArrayList c =new ArrayList();
    
    while(iter.hasNext()){
////        System.out.println(it2.first());
     System.out.println(iter.next());
    }
 }
 
 public void listImagesdatails(){//ok
     
    ImageApi imageApi= glanceApi.getImageApi("RegionOne");
    PagedIterable it=imageApi.list();
    PaginatedCollection<ImageDetails> it2=imageApi.listInDetail(new ListImageOptions());
    Iterator iter=it2.iterator();

    while(iter.hasNext()){
       System.out.println(iter.next());
       
    }
 }

 
 
 ///home/agalletta/Scaricati/cirros-0.3.0-i386-disk.img
     
 public void createImage(){//ok
     
    ImageApi imageApi= glanceApi.getImageApi("RegionOne");
    FilePayload pay=new FilePayload(new File("/home/agalletta/Scaricati/cirros-0.3.0-i386-disk.img"));
    CreateImageOptions cios =new  CreateImageOptions();
    //cios.id("testId");
    cios.minRam(12345);
    cios.diskFormat( DiskFormat.QCOW2);
    cios.containerFormat(ContainerFormat.BARE);
    cios.isPublic(true);
    imageApi.create("newTestCirros", pay,cios);
     
    
 }
 
 
  public void deleteImage(){//ok
     
    ImageApi imageApi= glanceApi.getImageApi("RegionOne");

    imageApi.delete("55ebd5e7-89cf-4b2e-9ef6-1f019c8944ea");
    imageApi.delete("06c772eb-20f2-4858-9aa3-1328d574e8cd");
    imageApi.delete("67bcc296-7267-4216-af30-4ea43df41cce");
    imageApi.delete("604037fd-64b0-47c2-9615-a7d3c4d43670");
    
 } 
  
  private String toJSON (ImageDetails imm){
  
      JSONObject obj=new JSONObject();
      JSONArray links= new JSONArray();
      JSONObject properties=new JSONObject();
      obj.put("id", imm.getId());
      obj.put("name",imm.getName());
      links=this.linksToArray(imm.getLinks());
      obj.put("links", links );
      obj.put("containerFormat",imm.getContainerFormat().get().toString());
      obj.put("diskFormat",imm.getDiskFormat().get().toString());
      obj.put("size",imm.getSize().get());
      obj.put("checksum",imm.getChecksum().get());
      obj.put("minDisk",imm.getMinDisk());
      obj.put("minRam",imm.getMinRam());
      obj.put("location",imm.getLocation().toString());
      obj.put("owner",imm.getOwner().get());
      obj.put("updatedAt",imm.getUpdatedAt().toString());
      obj.put("createdAt",imm.getCreatedAt().toString());
      obj.put("deletedAt",imm.getDeletedAt().toString());
      obj.put("status",imm.getStatus().toString());
      obj.put("isPublic",imm.isPublic());
      properties.putAll(imm.getProperties());
      obj.put("properties",properties);
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
 
   
 
 public void listRegions(){
 
     Iterator <String> iter=regions.iterator();
     
     while(iter.hasNext()){
         
        
         System.out.println(iter.next());
        
   //  SLF4JLoggingModule log= new SLF4JLoggingModule();
     //log.
                  }
 }
 
   
 
  public void listImagesdatailsMongo(){
     
    ImageApi imageApi= glanceApi.getImageApi("RegionOne");
    PagedIterable it=imageApi.list();
    PaginatedCollection<ImageDetails> it2=imageApi.listInDetail(new ListImageOptions());
    Iterator<ImageDetails> iter=it2.iterator();
    ArrayList c =new ArrayList();
    String Json;
    
    while(iter.hasNext()){
        Json=this.toJSON(iter.next());
        System.out.println(Json);
      //  mongo.insertInCollection("RegionOne", "glance", Json);
    }
 }
 
  
  
}
