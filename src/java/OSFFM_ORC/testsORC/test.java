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

package OSFFM_ORC.testsORC;

import OSFFM_ORC.GeoManager;
import OSFFM_ORC.ManifestManager;
import OSFFM_ORC.OrchestrationManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Giuseppe Tricomi
 */
public class test {

    /**
     * @param args the command line arguments
     */
    GeoManager geo_man= new GeoManager();
    JSONObject resource=new JSONObject();
    LinkedHashMap<String,LinkedHashMap> table_resourceset= new LinkedHashMap<String,LinkedHashMap>();
    
    public static void main(String[] args) {
        // TODO code application logic here
        test t =new test();
        Yaml yaml = new Yaml();
        String document="heat_template_version: 2014-10-16\n" +
"\n" +
"description: \n" +
"\n" +
"outputs:\n" +
"  out_A_private_ip:\n" +
"    description:  IP address server in private network\n" +
"    value:\n" +
"      get_attr:\n" +
"        - A\n" +
"        - first_address\n" +
"  out_A_public_ip:\n" +
"    description: Floating IP address of A server in public network\n" +
"    value:\n" +
"      get_attr:\n" +
"        - A_floating_ip\n" +
"        - floating_ip_address\n" +
"  out_B_private_ip:\n" +
"    description: IP address of B server in private network\n" +
"    value:\n" +
"      get_attr:\n" +
"        - B\n" +
"        - first_address\n" +
"  out_B_public_ip:\n" +
"    description: Floating IP address of B server in public network\n" +
"    value:\n" +
"      get_attr:\n" +
"        - B_floating_ip\n" +
"        - floating_ip_address\n" +
"\n" +
"parameters:\n" +
"  flavor:\n" +
"    constraints:\n" +
"      -\n" +
"        allowed_values:\n" +
"          - m1.tinyCM\n" +
"          - m1.smallCM\n" +
"          - m1.mediumCM\n" +
"        description: must be a valid CloudWave Server flavor.\n" +
"    default: m1.smallCM\n" +
"    description: Flavor to use for servers\n" +
"    type: string\n" +
"  image-A:\n" +
"    type: string\n" +
"    description: description\n" +
"    default: cm-A-latest\n" +
"  image-B:\n" +
"    type: string\n" +
"    description: description\n" +
"    default: 56c01296-8fac-4c74-89ab-dbc36dbdf909\n" +
"  cirros:\n" +
"    type: string\n" +
"    description: description\n" +
"    default: cm-B-latest\n" +                
"  key_name:\n" +
"    default: UME_Admin_P\n" +
"    description: Name of keypair to assign to servers\n" +
"    type: string\n" +
"  private_network:\n" +
"    type: string\n" +
"    label: Private network name or ID\n" +
"    description: Network to attach instance to.\n" +
"    default: net0\n" +
"  public_network:\n" +
"    type: string\n" +
"    label: Public network name or ID\n" +
"    description: Network to attach instance to.\n" +
"    default: external\n" +
"  \n" +
"resources:\n" +
"  geoshape_1:\n" +
"    type: OS::Beacon::Georeferenced_deploy\n" +
"    properties:\n" +
"      label: Shape label\n" +
"      description: descripition\n" +
"      shapes: ["
                + "{\"type\":\"Feature\",\"id\":\"AGO\",\"properties\":{\"name\":\"Angola\"},\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[71.014198,40.244366],[70.648019,39.935754],[69.55961,40.103211],[69.464887,39.526683],[70.549162,39.604198],[71.784694,39.279463],[73.675379,39.431237],[73.928852,38.505815],[74.257514,38.606507],[74.864816,38.378846],[74.829986,37.990007],[74.980002,37.41999],[73.948696,37.421566],[73.260056,37.495257],[72.63689,37.047558],[72.193041,36.948288],[71.844638,36.738171],[71.448693,37.065645],[71.541918,37.905774],[71.239404,37.953265],[71.348131,38.258905],[70.806821,38.486282],[70.376304,38.138396],[70.270574,37.735165],[70.116578,37.588223],[69.518785,37.608997],[69.196273,37.151144],[68.859446,37.344336],[68.135562,37.023115],[67.83,37.144994],[68.392033,38.157025],[68.176025,38.901553],[67.44222,39.140144],[67.701429,39.580478],[68.536416,39.533453],[69.011633,40.086158],[69.329495,40.727824],[70.666622,40.960213],[70.45816,40.496495],[70.601407,40.218527],[71.014198,40.244366]]]]}}"
                + "]\n" +
"\n" +
"  association_ip_A:\n" +
"    type: OS::Neutron::FloatingIPAssociation\n" +
"    properties:\n" +
"      floatingip_id: {get_resource: A_floating_ip}\n" +
"      port_id: {get_attr: [A, addresses, {get_param: private_network}, 0, port]}\n" +
"\n" +
"  A_floating_ip:\n" +
"    type: OS::Neutron::FloatingIP\n" +
"    properties:\n" +
"      floating_network: { get_param: public_network }\n" +
"\n" +
"  A:\n" +
"    type: OS::Nova::Server\n" +
"    properties:\n" +
"      name: VM-A\n" +
"      metadata: {'cloudwave-migrate': 'true'} # specify that this resource should be migrated\n" +
"      key_name: { get_param: key_name }\n" +
"      image: { get_param: image-A }\n" +
"      flavor: { get_param: flavor }\n" +
"      networks: [{\"fixed_ip\": 10.0.0.61, \"network\": { get_param: private_network } }]\n" +
"      security_groups:  [{ get_resource: server_security_group }]\n" +
"      user_data: |\n" +
"          #!/bin/bash\n" +
"          echo root:vagrant | chpasswd\n" +
"          sudo apt-get update\n" +
"\n" +
"  association_ip_B:\n" +
"    type: OS::Neutron::FloatingIPAssociation\n" +
"    properties:\n" +
"      floatingip_id: {get_resource: B_floating_ip}\n" +
"      port_id: {get_attr: [B, addresses, {get_param: private_network}, 0, port]}\n" +
"\n" +
"  B_floating_ip:\n" +
"    type: OS::Neutron::FloatingIP\n" +
"    properties:\n" +
"      floating_network: { get_param: public_network }\n" +
"\n" +
"  B:\n" +
"    type: OS::Nova::Server\n" +
"    properties:\n" +
"      name: test\n" +
"      key_name: {get_param: key_name }\n" +
"      image: {get_param: cirros }\n" +
"      flavor: m1.tiny\n" +
//"      networks:[ {network: net1}] \n" +
//"      security_groups:  [{ get_resource: server_security_group }]\n" +
//"      user_data: |\n" +
//"          #!/bin/bash\n" +
//"          echo root:vagrant | chpasswd\n" +
//"          sudo apt-get update           \n" +
"\n" +
"  server_security_group:\n" +
"    properties:\n" +
"      description: Standard firewall rules\n" +
"      name: SEC_GR_Name\n" +
"      rules:\n" +
"      - direction: egress\n" +
"        ethertype: IPv6\n" +
"      - direction: ingress\n" +
"        ethertype: IPv6\n" +
"        remote_mode: remote_group_id\n" +
"      - direction: ingress\n" +
"        ethertype: IPv4\n" +
"        port_range_max: 22\n" +
"        port_range_min: 22\n" +
"        protocol: tcp\n" +
"        remote_ip_prefix: 0.0.0.0/0\n" +
"      - direction: egress\n" +
"        ethertype: IPv4\n" +
"      - direction: ingress\n" +
"        ethertype: IPv4\n" +
"        remote_mode: remote_group_id\n" +
"      - direction: ingress\n" +
"        ethertype: IPv4\n" +
"        port_range_max: 5060\n" +
"        port_range_min: 5060\n" +
"        protocol: tcp\n" +
"        remote_ip_prefix: 0.0.0.0/0\n" +
"      - protocol: icmp\n" +
"    type: OS::Neutron::SecurityGroup\n" +
"\n" +
"  stateless_VM:\n" +
"    type: OS::Heat::AutoScalingGroup\n" +
"    properties:\n" +
"      resource:\n" +
//"        res_1_NAME: {get_resource: B}\n" +
"        res_2_NAME:\n" +
"          type: OS::Nova::Server\n" +
"          properties:\n" +
"            key_name: { get_param: key_name }\n" +
//"            image: { get_param: image-B }\n" +
"            flavor: { get_param: flavor }\n" +
"            networks: [{network: {get_param: private_network} }]\n" +
"        res_3_NAME:\n" +
"          type: OS::Nova::Server\n" +
"          properties:\n" +
"            key_name: { get_param: key_name }\n" +
"            image: { get_param: image-A }\n" +
"            flavor: { get_param: flavor }\n" +
"            networks: [{network: {get_param: private_network} }]\n" +
"      min_size: 1\n" +
"      desired_capacity: 3\n" +
"      max_size: 10\n" +
"\n" +
"  scale_up_policy:\n" +
"    type: OS::Beacon::ScalingPolicy\n" +
"    properties:\n" +
"      adjustment_type: change_in_capacity\n" +
"      auto_scaling_group_id: {get_resource: stateless_VM}\n" +
"      cooldown: 60\n" +
"      scaling_adjustment: 1\n" +
"\n" +
"  scale_down_policy:\n" +
"    type: OS::Beacon::ScalingPolicy\n" +
"    properties:\n" +
"      adjustment_type: change_in_capacity\n" +
"      auto_scaling_group_id: {get_resource: stateless_VM}\n" +
"      cooldown: 60\n" +
"      scaling_adjustment: '-1'\n" +
"\n" +
"  federation:\n" +
"    type: OS::Beacon::ServiceGroupManagement\n" +
"    properties:\n" +
"      name: GroupName\n" +
"      geo_deploy: { get_resource: geoshape_1}\n" +
"      resource:\n" +
"        groups:  {get_resource: stateless_VM}    \n" +
"\n" +
"  federation2:\n" +
"    type: OS::Beacon::ServiceGroupManagement\n" +
"    properties:\n" +
"      name: GroupName\n" +
"      geo_deploy: { get_resource: geoshape_1}\n" +
"      resource:\n" +
"        groups:  {get_resource: B}\n"+
//"        groups2: {get_resource: B_floating_ip}\n" +                
"  \n" +
"  fedNetworklink:\n" +
"    type: OS::Beacon::fedNetManagement\n" +
"    properties:\n" +
"      monitored_Group: stateless_VM            # name of monitored group \n" +
"      connected_VM: \n" +
"        - res_1_name\n" +
"        - res_2_name\n" +
"\n" +
"  fedSecGroup:\n" +
"    type: OS::Beacon::fedSecManagement\n" +
"    properties: \n" +
"      monitored_resource: \n" +
"        - federation\n" +
"        - res_1_name\n" +
"      fw_rules: [{ get_resource: server_security_group }]\n" +
"      policies: [{ get_resource: xacml_security_group }]\n" +
"\n" +
"  xacml_security_group:\n" +
"    type:  OS::Beacon::PoliciesAccManagement\n" +
"    properties:\n" +
"      rules:\n" +
"        - value #we need information about this field";
        //document viene sostuito da ricomponiYamlManifest della classe splitter
        LinkedHashMap<String, Object> list = (LinkedHashMap<String, Object>) yaml.load(document);
        t.resource=new JSONObject(list);
        try{
        //System.out.println(new org.json.simple.parser.JSONParser().parse((String)t.resource.toString()));
        }
        catch(Exception e){}
        JSONObject a=new JSONObject();
        System.out.println(t.resource.toString());
        HashMap<String,Object> al=new HashMap<String,Object>();
        t.table_resourceset.put("OS::Beacon::Georeferenced_deploy",new LinkedHashMap<String,org.json.JSONObject>());
        t.table_resourceset.put("OS::Beacon::ServiceGroupManagement",new LinkedHashMap<String,org.json.JSONObject>());
        try {
            //geo_man.consume_georeference("document", list);
            ManifestManager mm=new ManifestManager("document",t.resource);
        } catch (JSONException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }
        OrchestrationManager om=new OrchestrationManager();
        try{
      //  t.extractResourcefromManifest(t.resource, t.table_resourceset);
          //mm.analizeGlobalManifest();
            om.addManifestToWorkf("test",t.resource );
   //         om.test("test", "yam-out");
     /*   try{
        Object ob=g.retrievegeoref("document", 0);
            System.out.println(ob);*/
        }catch(Exception e){System.out.println("eccezione"+e.getMessage());}
       // al=t.elaborateGeoRef(t.geo_man, t.table_resourceset);
        
        System.out.println("");
    }
    
    private String leafcheck(String key,Object val){
        switch (key) {
            case "get_resource":
                return "C "+val;
            case "get_param":
                return"D "+val;
            case "get_attr":
                //prendere solo il primo elemento dell array
                //String resName=(String)((org.json.JSONArray)((HashMap)tmp).get("get_attr")).get(0);//da restituire o inserire in lista
                //System.out.println(resName);
                return"E "+val;
            default:
                 return null;
        }
    }
    
    public void resourceRecursiveDeepInspection(JSONObject properties)throws JSONException{
        Iterator it=properties.keys();
        while(it.hasNext()){
            String key=(String)it.next();
            Object tmp=properties.get(key);
            if(tmp instanceof String){
                //richiamo la funzione ricorsivamente verificando tutti gli elementi dell'array
                String res=this.leafcheck(key, tmp);
                if (res!=null)
                    System.out.println(res);
            }
            else if(tmp instanceof JSONObject){
                //richiamo la funzione ricorsiva
                Iterator it2=((JSONObject)tmp).keys();
                while(it2.hasNext()){
                    String key2=(String)it2.next();
                    Object tmp2=((JSONObject)tmp).get(key2);
                    if(tmp2 instanceof JSONObject){
                        this.resourceRecursiveDeepInspection((JSONObject) tmp2);
                    }
                    else if(tmp2 instanceof JSONArray){
                        String res=this.leafcheck(key2, ((JSONArray)tmp2).get(0));
                        if (res!=null)
                            System.out.println(res);
                    }
                    else if(tmp2 instanceof String){
                        //richiamo la funzione ricorsivamente verificando tutti gli elementi dell'array
                        String res=this.leafcheck(key2, tmp2);
                        if (res!=null)
                            System.out.println(res);
                    }
                }
            }
            else{
                if(((HashMap)tmp).containsKey("get_resource")){
                    System.out.println("C");
                }else if(((HashMap)tmp).containsKey("get_param")){
                    System.out.println("D");
                }else if(((HashMap)tmp).containsKey("get_attr")){
                    //prendere solo il primo elemento dell array
                    System.out.println("E");
                    String resName=(String)((org.json.JSONArray)((HashMap)tmp).get("get_attr")).get(0);//da restituire o inserire in lista
                    System.out.println(resName);
                }
                    
                //analizzo l'elemento(di certo bisogna verificare nella key della mappa) alla ricerca di get_param, get_resource,get_attr e passandoli all'opportuna funzione di management 
            }
        }
    }
    
    public void extractResourcefromManifest(JSONObject res,LinkedHashMap table_resourceset)throws JSONException{
        String str = null;
        Iterator it_res=res.keys();
        while(it_res.hasNext()){
            org.json.JSONObject tmp=null;
            String key=(String)it_res.next();
            tmp=new org.json.JSONObject(res.toString());
            tmp=tmp.getJSONObject(key);
            str=tmp.getString("type");
            switch(str){
                case "OS::Beacon::ServiceGroupManagement":{
                    this.resourceRecursiveDeepInspection(tmp.getJSONObject("properties"));
                    ((LinkedHashMap)table_resourceset.get("OS::Beacon::ServiceGroupManagement")).put(key, tmp);
                    break;
                }
                case "OS::Beacon::fedNetManagement":{
                    ((LinkedHashMap)table_resourceset.get("OS::Beacon::fedNetManagement")).put(key, tmp);
                    break;
                }
                case "OS::Beacon::PoliciesAccManagement":{
                    ((LinkedHashMap)table_resourceset.get("OS::Beacon::PoliciesAccManagement")).put(key, tmp);
                    break;
                }
                case "OS::Beacon::fedSecManagement":{
                    ((LinkedHashMap)table_resourceset.get("OS::Beacon::fedSecManagement")).put(key, tmp);
                    break;
                }
                case "OS::Beacon::ScalingPolicy":{
                   ((LinkedHashMap)table_resourceset.get("OS::Beacon::ScalingPolicy")).put(key, tmp);
                    break;
                }
                case "OS::Beacon::Georeferenced_deploy":{
                    ((LinkedHashMap)table_resourceset.get("OS::Beacon::Georeferenced_deploy")).put(key, tmp);
                    break;
                }
                default :{
                    break;
                }
            }
        } 
    }
    
    public HashMap<String,Object> elaborateGeoRef(GeoManager geo_man,LinkedHashMap table_resourceset){
        boolean completed=false;
        String error="";
        LinkedHashMap<String,Object> result=new LinkedHashMap<String,Object>();
        result.put("error", error);
        String name="";
        org.json.JSONObject tmp;
        LinkedHashMap        element;
        Iterator itkey=((LinkedHashMap)table_resourceset.get("OS::Beacon::Georeferenced_deploy")).keySet().iterator();
        while(itkey.hasNext()){
            String key=(String)itkey.next();
            geo_man.consume_georeference(name, (org.json.JSONObject)((LinkedHashMap)table_resourceset.get("OS::Beacon::Georeferenced_deploy")).get(key));
        }
        result.put("completed", completed);
        return result;
    }
}
