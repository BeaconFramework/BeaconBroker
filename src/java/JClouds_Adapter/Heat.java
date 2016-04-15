/**
* Copyright 2016, University of Messina.
* 
* Licensed under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License. You may obtain a copy of
* the License at
* 
* http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations under
* the License.
*/
package JClouds_Adapter;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.heat.HeatService;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.common.Link;
import org.openstack4j.model.heat.Resource;
import org.openstack4j.model.heat.Stack;
import org.openstack4j.openstack.OSFactory;

/**
 * This class need to be reviewed.
 * @author agalletta
 */
public class Heat {

    String endpoint, user, tenant, password;
    OSClient os;
    HeatService heat;
    static final Logger LOGGER = Logger.getLogger(Heat.class);
    public Heat(String endpoint, String user, String tenant, String password) {
        this.endpoint = endpoint;
        this.user = user;
        this.tenant = tenant;
        this.password = password;
        OSFactory.enableHttpLoggingFilter(false);//true to activate debug
        os = OSFactory.builder()
                .endpoint(endpoint)
                //.token("ebd8e2c30e71466a9fc38581ffada8cb")
                .credentials(user, password)
                .perspective(Facing.PUBLIC)
                .tenantName(tenant)
                .authenticate();
        //  os.useRegion("RegionOne");
        heat = os.heat();

    }

    public void printStacks() {
        List<? extends org.openstack4j.model.heat.Stack> l = heat.stacks().list();
        Iterator<? extends org.openstack4j.model.heat.Stack> it = l.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    public String getStatus(String stackName, String stackID) {
        Stack s = this.getStack(stackName, stackID);
        return s.getStatus();
    }

    public String getStackId(String name) {
        String id;
        Stack s = heat.stacks().getStackByName(name);
        id = s.getId();
        //System.out.println(s.toString());
        return id;
    }

    public Stack getStack(String name, String stackID) {
        Stack s = heat.stacks().getDetails(name, stackID);
        // System.out.println(s.toString());
        return s;
    }

    public Stack getStackByName(String name) {
        Stack s = heat.stacks().getStackByName(name);
        //System.out.println(s.toString());
        return s;
    }

  /*  public Stack createStack(String nameStack, String templateYML, Map<String, String> parameters) {

        Stack stack = os.heat().stacks().create(Builders.stack()
                .name(nameStack)
                .template(templateYML)
                .parameters(parameters)
                .timeoutMins(35L).build());

        return stack;
    }

    public Stack createStack(String nameStack, String templateYML) {
        Stack stack = os.heat().stacks().create(Builders.stack()
                .name(nameStack)
                .template(templateYML)
                .timeoutMins(35L).build());

        return stack;
    }*/
    
      public synchronized Stack createStack(String nameStack, String templateYML, Map<String, String> parameters) {

        Stack stack = null;
        boolean check = false;
        String status;

        stack = os.heat().stacks().create(Builders.stack()
                .name(nameStack)
                .template(templateYML)
                .parameters(parameters)
                .timeoutMins(35L).build());

        status = stack.getStatus();

        if (status != null) {
            check = status.equals("CREATE_COMPLETE");
        }

        while (!check) {
            try {
                this.wait(50000L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            stack = this.getStack(nameStack, stack.getId());

            status = stack.getStatus();
            if (status != null) {
                check = (status.equals("CREATE_COMPLETE") | status.equals("CREATE_FAILED"));
            }

        }
        return stack;
    }

    public synchronized Stack createStack(String nameStack, String templateYML) {
        Stack stack = null;
        boolean check = false;
        String status;

        stack = os.heat().stacks().create(Builders.stack()
                .name(nameStack)
                .template(templateYML)
                .timeoutMins(35L).build());

        status = stack.getStatus();

        if (status != null) {
            check = status.equals("CREATE_COMPLETE");
            System.out.println("stat: " + stack.getStatus());
        }

        while (!check) {
            try {
                this.wait(50000L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();

            }
            stack = this.getStack(nameStack, stack.getId());

            status = stack.getStatus();
            if (status != null) {
                check = (status.equals("CREATE_COMPLETE") | status.equals("CREATE_FAILED"));
            }
        }
        return stack;

    }
    
    public void listResources(String stackID) {

        List<? extends Resource> l = heat.resources().list(stackID);
        Iterator<? extends Resource> it = l.iterator();
        while (it.hasNext()) {
            Resource r = it.next();
            System.out.println("uuid: " + r.getPhysicalResourceId());
            System.out.println("link: " + r.getLinks());
            System.out.println("nome nel manifest: " + r.getLocalReourceId());
            System.out.println("reason: " + r.getReason());
            System.out.println("requiredby: " + r.getRequiredBy());
            System.out.println("res name: " + r.getResourceName());
            System.out.println("status: " + r.getResourceStatus());
            System.out.println("time: " + r.getTime());
            System.out.println("type: " + r.getType());
        }
    }

    public List<? extends Resource> getResource(String stackID) {
        List<? extends Resource> l = heat.resources().list(stackID);
        return l;
    }

    public Resource getResource(String stackID, String stackName, String resourceName) {
        Resource l = heat.resources().show(stackName, stackID, resourceName);
        return l;
    }

   // TODO: validate template
    public String stackToJSON(Stack stack) {

        JSONObject obj = new JSONObject();

        try {
            obj.put("id", stack.getId());
            obj.put("name", stack.getName());
            obj.put("status", stack.getStatus());
            obj.put("description", stack.getDescription());
            obj.put("templateDescription", stack.getTemplateDescription());
            obj.put("timeoutMins", stack.getTimeoutMins());
            obj.put("outputs", this.outputToArray(stack.getOutputs()));
            obj.put("parameters", stack.getParameters());
            obj.put("creationTime", stack.getCreationTime());
            obj.put("links", this.linksToArray(stack.getLinks()));
            obj.put("updatedTime", stack.getUpdatedTime());

        } catch (Exception e) {

            e.printStackTrace();

        }
        return obj.toString();

    }

    private JSONArray linksToArray(List s) throws JSONException {

        JSONArray links = new JSONArray();
        JSONObject link;
        Iterator<Link> i;
        Link successivo;
        i = s.iterator();

        while (i.hasNext()) {
            link = new JSONObject();
            successivo = i.next();
            link.put("rel", successivo.getRel());
            link.put("href", successivo.getHref());
            links.put(link);
        }
        return links;
    }

    private JSONArray outputToArray(List<Map<String, Object>> list) {

        JSONArray outputs = new JSONArray();
        Iterator<Map<String, Object>> it = list.iterator();
        JSONObject output;
        // Map <String,Object> output;

        while (it.hasNext()) {
            output = new JSONObject(it.next());
            outputs.put(output);
        }
        return outputs;
    }

    public String resourceToJson(Resource risorsa) {

        JSONObject obj = new JSONObject();

        try {

            obj.put("links", this.linksToArray(risorsa.getLinks()));
            obj.put("localReourceId", risorsa.getLocalReourceId());
            obj.put("physicalResourceId", risorsa.getPhysicalResourceId());
            obj.put("reason", risorsa.getReason());
            obj.put("requiredBy", risorsa.getRequiredBy());
            obj.put("resourcename", risorsa.getResourceName());
            obj.put("resourceStatus", risorsa.getResourceStatus());
            obj.put("lastUpdate", risorsa.getTime());
            obj.put("type", risorsa.getType());

        } catch (Exception e) {

            e.printStackTrace();

        }
        return obj.toString();
    }

}
