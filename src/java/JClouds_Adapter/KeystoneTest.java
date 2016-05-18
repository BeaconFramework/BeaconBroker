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
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;
import org.jclouds.ContextBuilder;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.keystone.v2_0.AuthenticationApi;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.domain.PasswordCredentials;
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.domain.Service;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.extensions.RoleAdminApi;
import org.jclouds.openstack.keystone.v2_0.extensions.ServiceAdminApi;
import org.jclouds.openstack.keystone.v2_0.extensions.TenantAdminApi;
import org.jclouds.openstack.keystone.v2_0.extensions.UserAdminApi;
import org.jclouds.openstack.keystone.v2_0.features.ServiceApi;
import org.jclouds.openstack.keystone.v2_0.features.TenantApi;
import org.jclouds.openstack.keystone.v2_0.features.TokenApi;
import org.jclouds.openstack.keystone.v2_0.features.UserApi;
import org.jclouds.openstack.keystone.v2_0.options.CreateTenantOptions;
import org.jclouds.openstack.keystone.v2_0.options.CreateUserOptions;
import org.jclouds.openstack.keystone.v2_0.options.UpdateUserOptions;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.v2_0.features.ExtensionApi;
import org.jclouds.openstack.v2_0.options.PaginationOptions;

/**
 *This class need to be reviewed.
 * @author agalletta
 * @author gtricomi
 */
public class KeystoneTest {
   



    private DBMongo mongo;
    private final KeystoneApi keystoneApi;
    private String provider ="openstack-keystone";
    private String endpoint,identity,credential;
    static final Logger LOGGER = Logger.getLogger(KeystoneTest.class);
     public KeystoneTest(DBMongo mongo) {
        Iterable<Module> modules = ImmutableSet.<Module>of( new SLF4JLoggingModule());
        //Iterable<Module> modules = ImmutableSet.<Module>of( );
        this.mongo=mongo;
        String provider = "openstack-keystone";
        String identity = "admin:admin"; // tenantName:userName
        String credential = "password";

        keystoneApi = ContextBuilder.newBuilder(provider)
                .endpoint("http://172.17.4.113:5000/v2.0")
                .credentials(identity, credential)
                .modules(modules)
                .buildApi(KeystoneApi.class);
        
        
         
        //regions = keystoneApi.getConfiguredRegions();
    }
        public KeystoneTest() {
        Iterable<Module> modules = ImmutableSet.<Module>of( new SLF4JLoggingModule());
        //Iterable<Module> modules = ImmutableSet.<Module>of( );
      //  this.mongo=mongo;
        String provider = "openstack-keystone";
        String identity = "admin:admin"; // tenantName:userName
        String credential = "password";

        keystoneApi = ContextBuilder.newBuilder(provider)
                .endpoint("http://172.17.4.113:5000/v2.0")
                .credentials(identity, credential)
                .modules(modules)
                .buildApi(KeystoneApi.class);
        
        
         
        //regions = keystoneApi.getConfiguredRegions();
    }
        /**
         * 
         * @param tenant
         * @param username
         * @param password
         * @param endpoint 
         * @author gtricomi
         */
        public KeystoneTest(String tenant,String  username,String password,String endpoint) {
        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());
        this.identity = tenant+":"+username; // tenantName:userName
        this.credential =password;
        this.endpoint=endpoint;
        keystoneApi = ContextBuilder.newBuilder(provider)
                .endpoint(this.endpoint)
                .credentials(identity, credential)
                .modules(modules)
                .buildApi(KeystoneApi.class);

        //regions = keystoneApi.getConfiguredRegions();
    }
 /*
 public void listRegions(){
 
     Iterator <String> iter=regions.iterator();
     
     while(iter.hasNext()){
         
        
         System.out.println(iter.next());
        
     
     
     }
 }
*/  
     
 public Tenant createTenant(String newTenantName) {//OK

     TenantAdminApi tenantAdminApi;
     CreateTenantOptions tenantOptions;
     Tenant tenant;
     
      Optional<? extends TenantAdminApi> tenantAdminApiExtension = keystoneApi.getTenantAdminApi();

      if (tenantAdminApiExtension.isPresent()) {

          tenantAdminApi = tenantAdminApiExtension.get();
          tenantOptions = CreateTenantOptions.Builder
               .description("My-New-Tenant").enabled(true);
          tenant = tenantAdminApi.create(newTenantName, tenantOptions);


         return tenant;
      } else {
         System.out.format("    TenantAdminApi is *not* present%n");
         System.exit(1);

         return null;
      }
   }
  
 public void servicetList(){
     
     
       Optional<? extends ServiceAdminApi> ServiceAdminApiExtension = keystoneApi.getServiceAdminApi();

       
       ServiceAdminApi ServiceAdminApi=ServiceAdminApiExtension.get();
       Service s;
       PaginatedCollection<Service> fluent=ServiceAdminApi.list(new PaginationOptions());
  
       Iterator <Service> it=fluent.iterator();
       while(it.hasNext()){
           s=it.next();
           System.out.println(s.getId());
           System.out.println(s.iterator().hasNext());
       
       }
     
 }
 
 
    /**
     * It returns service endpoint for Service named with serviceName value.
     * @param serviceName, name of the service.
     * @author gtricomi
     */
    public String servicetGetEndpoint(String serviceName) {
        Optional<? extends ServiceAdminApi> ServiceAdminApiExtension = keystoneApi.getServiceAdminApi();
        ServiceAdminApi ServiceAdminApi = ServiceAdminApiExtension.get();
        Service s;
        PaginatedCollection<Service> fluent = ServiceAdminApi.list(new PaginationOptions());

        Iterator<Service> it = fluent.iterator();
        while (it.hasNext()) {
            s = it.next();
            if (s.getName().equals(serviceName)) {
                if (s.iterator().hasNext()) {
                    return s.iterator().next().getPublicURL().toString();
                } else {
                    return null;
                }
            }

        }
        return null;

    }

    
   public void serviceGet(String serviceId){
     
     
       Optional<? extends ServiceAdminApi> ServiceAdminApiExtension = keystoneApi.getServiceAdminApi();

       ServiceAdminApi ServiceAdminApi=ServiceAdminApiExtension.get();
       Service s;
       PaginatedCollection<Service> fluent=ServiceAdminApi.list(new PaginationOptions());
  
       Iterator <Service> it=fluent.iterator();
       System.out.println("www"+ServiceAdminApi.get(serviceId).toString());
 }
 
 

 
 
 
  public User createUser(Tenant tenant,String newUserName,String userPsw) {//OK,forse non funziona
      
      UserAdminApi userAdminApi;
      CreateUserOptions userOptions;
      User user =null;
      Optional<? extends UserAdminApi> userAdminApiExtension = keystoneApi.getUserAdminApi();

      if (userAdminApiExtension.isPresent()) {
          userAdminApi = userAdminApiExtension.get();
          userOptions = CreateUserOptions.Builder
               .tenant(tenant.getId())
               .email("newess.email@example.com")
               .enabled(true);
          System.out.println(tenant.getId());
          user = userAdminApi.create(newUserName, userPsw, userOptions);
         

      } else {
         System.out.format("    UserAdminApi is *not* present%n");
         System.exit(1);
      }
      
      return user;
      
   }

  
  public void listRole(){//OK
      
     
       Optional<? extends RoleAdminApi> roleAdminApiExtension = keystoneApi.getRoleAdminApi();

       
       System.out.println(roleAdminApiExtension.isPresent());
       RoleAdminApi roleAdminApi=roleAdminApiExtension.get();
       
       FluentIterable<? extends Role> fluent=roleAdminApi.list();
  
       Iterator <? extends Role> it=fluent.iterator();
       while(it.hasNext()){
       
           System.out.println(it.next().toString());
       
       }
      
  
  }
  
   public void listUser(){//OK
   
    Optional<? extends UserApi> userExt=  keystoneApi.getUserApi();
    UserApi userApi=userExt.get();
    PaginatedCollection<User> it2=userApi.list(new PaginationOptions());
    Iterator  iter=it2.iterator();
    User user;
    
    while(iter.hasNext()){
        user=(User) iter.next();
        System.out.println(user.toString());
        System.out.println(user.getEmail());
        System.out.println(user.getName());
        System.out.println(user.getTenantId());
    }
     
    
       
   }

     public void enableUser(){//OK
   
    Optional<? extends UserApi> userExt=  keystoneApi.getUserApi();
    UserApi userApi=userExt.get();
        Optional<? extends UserAdminApi> userAdminExt=  keystoneApi.getUserAdminApi();
    UserAdminApi userAdminApi=userAdminExt.get();
    User user;
    UpdateUserOptions userOptions = UpdateUserOptions.Builder
               .enabled(true);
    String iduser;
    
   // while(iter.hasNext()){
        user=userApi.getByName("newUser");
        iduser=user.getId();
        System.out.println(iduser);
        
        userAdminApi.update(iduser, userOptions);
        
////        System.out.println(it2.first());
   //  System.out.println(iter.next());
   // }
     
   }
   
 public void listTenant(){//OK
   
       Optional<? extends TenantApi> tenantExt=  keystoneApi.getTenantApi();
      TenantApi tenantApi=tenantExt.get();
    PaginatedCollection<Tenant> it2=tenantApi.list(new PaginationOptions());
    Iterator  iter=it2.iterator();
    
    while(iter.hasNext()){
////        System.out.println(it2.first());
     System.out.println(iter.next());
    }
     
    
       
   }
           
public void getUser(String userName){
   
    Optional<? extends UserApi> userExt=  keystoneApi.getUserApi();
    UserApi userApi=userExt.get();
    User user=userApi.getByName(userName);
    
    System.out.println(user.toString());
     
    Set<Role> s =userApi.listRolesOfUserOnTenant(user.getId(), user.getTenantId());
Iterator i=s.iterator();
while(i.hasNext()){

    System.out.println(i.next());
}
       
   }


 public void listTenants(){
   
       Optional<? extends TenantApi> tenantExt=  keystoneApi.getTenantApi();
      TenantApi tenantApi=tenantExt.get();
    Tenant t=tenantApi.getByName("demo");
    
    
       
   }
 /**
  * Returns tenant ID.
  * @param tenantName
  * @return 
  * @author gtricomi
  */
 public String getTenantId(String tenantName){
     Optional<? extends TenantApi> tenantExt=  keystoneApi.getTenantApi();
      TenantApi tenantApi=tenantExt.get();
      Tenant t=tenantApi.getByName(tenantName);
      return t.getId();
 }
  
 
 public void endpointLists(){
     
      ServiceApi ServiceAdminApi = keystoneApi.getServiceApi();
      IterableWithMarker<Service> s;
      Set<Tenant> fluent=ServiceAdminApi.listTenants();
      Iterator <Tenant> it=fluent.iterator();
      
      while(it.hasNext()){
          System.out.println(it.next());
      }
      
 }
 
    public HashMap getToken(String tenantname, String username, String password) {
        ContextBuilder contextBuilder = ContextBuilder.newBuilder("openstack-nova")
                .credentials(tenantname + ":" + username, password);
        contextBuilder.endpoint(this.endpoint);
        ComputeServiceContext context = contextBuilder.buildView(ComputeServiceContext.class);
        Function<Credentials, Access> auth = context.utils().injector().getInstance(Key.get(new TypeLiteral<Function<Credentials, Access>>() {
        }));
        Access access = auth.apply(new Credentials.Builder<Credentials>().identity(tenantname + ":" + username).credential(password).build());
        HashMap hm=new HashMap();
        hm.put("ID", access.getToken().getId());
        hm.put("Expires",access.getToken().getExpires());
        return hm;
    }
    /**
     * Validates token
     * @param tokenToValidate
     * @return true/false.
     */
    public boolean validateToken(String tokenToValidate){
        TokenApi t=this.keystoneApi.getTokenApi().get();
        return t.isValid(tokenToValidate);
    }
    
 
 /**
  * This function need to be modified.
  * @param tenantname
  * @param username
  * @param password
  * @return 
  * @author gtricomi
  */
 public String autenticate(String tenantname,String username, String password){
     ContextBuilder contextBuilder = ContextBuilder.newBuilder("openstack-nova")
            .credentials(tenantname+":"+username, password);

         contextBuilder.endpoint(this.endpoint);

         
         ComputeServiceContext context = contextBuilder.buildView(ComputeServiceContext.class);
           Function<Credentials, Access> auth = context.utils().injector().getInstance(Key.get(new TypeLiteral<Function<Credentials, Access>>(){}));      
      Access access = auth.apply(new Credentials.Builder<Credentials>().identity(tenantname+":"+username).credential(password).build());
      
      System.out.println(access);      
      System.out.println("  User Name = " + access.getUser().getName());
      System.out.println("  User ID = " + access.getUser().getId());
      System.out.println("  Tenant Name = " + access.getToken().getTenant().get().getName());
      System.out.println("  Tenant ID = " + access.getToken().getTenant().get().getId());
      System.out.println("  Token ID = " + access.getToken().getId());
      System.out.println("  Token Expires = " + access.getToken().getExpires());
         
         return null;
 }    
     
}
