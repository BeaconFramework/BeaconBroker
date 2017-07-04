/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OSFFM_ORC.testsORC;

import OSFFM_ORC.ManifestManager;
import OSFFM_ORC.OrchestrationManager;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author giuseppe
 */
public class sfcAnalisys {//UNFINISHED, NOT WORKING

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
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
"  out_B_private_ip:\n" +
"    description: IP address of B server in private network\n" +
"    value:\n" +
"      get_attr:\n" +
"        - B\n" +
"        - first_address\n" +
" \n" +
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
"    default: cirros-0.3.4-x86_64-uec\n" +
"  image-B:\n" +
"    type: string\n" +
"    description: description\n" +
"    default: cirros-0.3.4-x86_64-uec\n" +
"  cirros:\n" +
"    type: string\n" +
"    description: description\n" +
"    default: cirros-0.3.4-x86_64-uec            \n" +
"  key_name:\n" +
"    default: heat_key\n" +
"    description: Name of keypair to assign to servers\n" +
"    type: string\n" +
"  private_network:\n" +
"    type: string\n" +
"    label: Private network name or ID\n" +
"    description: Network to attach instance to.\n" +
"    default: private\n" +
"  public_network:\n" +
"    type: string\n" +
"    label: Public network name or ID\n" +
"    description: Network to attach instance to.\n" +
"    default: external\n" +
"\n" +
"resources:\n" +
"  geoshape_1:\n" +
"    type: OS::Beacon::Georeferenced_deploy\n" +
"    properties:\n" +
"      label: Shape label\n" +
"      description: descripition\n" +
"      shapes: [{\"type\":\"Feature\",\"id\":\"ITA\",\"properties\":{\"name\":\"Italy\"},\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[15.520376,38.231155],[15.160243,37.444046],[15.309898,37.134219],[15.099988,36.619987],[14.335229,36.996631],[13.826733,37.104531],[12.431004,37.61295],[12.570944,38.126381],[13.741156,38.034966],[14.761249,38.143874],[15.520376,38.231155]]],[[[9.210012,41.209991],[9.809975,40.500009],[9.669519,39.177376],[9.214818,39.240473],[8.806936,38.906618],[8.428302,39.171847],[8.388253,40.378311],[8.159998,40.950007],[8.709991,40.899984],[9.210012,41.209991]]],[[[12.376485,46.767559],[13.806475,46.509306],[13.69811,46.016778],[13.93763,45.591016],[13.141606,45.736692],[12.328581,45.381778],[12.383875,44.885374],[12.261453,44.600482],[12.589237,44.091366],[13.526906,43.587727],[14.029821,42.761008],[15.14257,41.95514],[15.926191,41.961315],[16.169897,41.740295],[15.889346,41.541082],[16.785002,41.179606],[17.519169,40.877143],[18.376687,40.355625],[18.480247,40.168866],[18.293385,39.810774],[17.73838,40.277671],[16.869596,40.442235],[16.448743,39.795401],[17.17149,39.4247],[17.052841,38.902871],[16.635088,38.843572],[16.100961,37.985899],[15.684087,37.908849],[15.687963,38.214593],[15.891981,38.750942],[16.109332,38.964547],[15.718814,39.544072],[15.413613,40.048357],[14.998496,40.172949],[14.703268,40.60455],[14.060672,40.786348],[13.627985,41.188287],[12.888082,41.25309],[12.106683,41.704535],[11.191906,42.355425],[10.511948,42.931463],[10.200029,43.920007],[9.702488,44.036279],[8.888946,44.366336],[8.428561,44.231228],[7.850767,43.767148],[7.435185,43.693845],[7.549596,44.127901],[7.007562,44.254767],[6.749955,45.028518],[7.096652,45.333099],[6.802355,45.70858],[6.843593,45.991147],[7.273851,45.776948],[7.755992,45.82449],[8.31663,46.163642],[8.489952,46.005151],[8.966306,46.036932],[9.182882,46.440215],[9.922837,46.314899],[10.363378,46.483571],[10.442701,46.893546],[11.048556,46.751359],[11.164828,46.941579],[12.153088,47.115393],[12.376485,46.767559]]]]}}]\n" +
"\n" +
"  geoshape_2:\n" +
"    type: OS::Beacon::Georeferenced_deploy\n" +
"    properties:\n" +
"      label: Shape label\n" +
"      description: descripition\n" +
"      shapes: [{\"type\":\"Feature\",\"id\":\"BEL\",\"properties\":{\"name\":\"Belgium\"},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[3.314971,51.345781],[4.047071,51.267259],[4.973991,51.475024],[5.606976,51.037298],[6.156658,50.803721],[6.043073,50.128052],[5.782417,50.090328],[5.674052,49.529484],[4.799222,49.985373],[4.286023,49.907497],[3.588184,50.378992],[3.123252,50.780363],[2.658422,50.796848],[2.513573,51.148506],[3.314971,51.345781]]]}},{\"type\":\"Feature\",\"id\":\"ITA\",\"properties\":{\"name\":\"Italy\"},\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[15.520376,38.231155],[15.160243,37.444046],[15.309898,37.134219],[15.099988,36.619987],[14.335229,36.996631],[13.826733,37.104531],[12.431004,37.61295],[12.570944,38.126381],[13.741156,38.034966],[14.761249,38.143874],[15.520376,38.231155]]],[[[9.210012,41.209991],[9.809975,40.500009],[9.669519,39.177376],[9.214818,39.240473],[8.806936,38.906618],[8.428302,39.171847],[8.388253,40.378311],[8.159998,40.950007],[8.709991,40.899984],[9.210012,41.209991]]],[[[12.376485,46.767559],[13.806475,46.509306],[13.69811,46.016778],[13.93763,45.591016],[13.141606,45.736692],[12.328581,45.381778],[12.383875,44.885374],[12.261453,44.600482],[12.589237,44.091366],[13.526906,43.587727],[14.029821,42.761008],[15.14257,41.95514],[15.926191,41.961315],[16.169897,41.740295],[15.889346,41.541082],[16.785002,41.179606],[17.519169,40.877143],[18.376687,40.355625],[18.480247,40.168866],[18.293385,39.810774],[17.73838,40.277671],[16.869596,40.442235],[16.448743,39.795401],[17.17149,39.4247],[17.052841,38.902871],[16.635088,38.843572],[16.100961,37.985899],[15.684087,37.908849],[15.687963,38.214593],[15.891981,38.750942],[16.109332,38.964547],[15.718814,39.544072],[15.413613,40.048357],[14.998496,40.172949],[14.703268,40.60455],[14.060672,40.786348],[13.627985,41.188287],[12.888082,41.25309],[12.106683,41.704535],[11.191906,42.355425],[10.511948,42.931463],[10.200029,43.920007],[9.702488,44.036279],[8.888946,44.366336],[8.428561,44.231228],[7.850767,43.767148],[7.435185,43.693845],[7.549596,44.127901],[7.007562,44.254767],[6.749955,45.028518],[7.096652,45.333099],[6.802355,45.70858],[6.843593,45.991147],[7.273851,45.776948],[7.755992,45.82449],[8.31663,46.163642],[8.489952,46.005151],[8.966306,46.036932],[9.182882,46.440215],[9.922837,46.314899],[10.363378,46.483571],[10.442701,46.893546],[11.048556,46.751359],[11.164828,46.941579],[12.153088,47.115393],[12.376485,46.767559]]]]}}]\n" +
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
"      metadata: {'cloudwave-migrate': 'true'}\n" +
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
"  federation:\n" +
"    type: OS::Beacon::ServiceGroupManagement\n" +
"    properties:\n" +
"      name: GroupName\n" +
"      geo_deploy: { get_resource: geoshape_2}\n" +
"      resource:\n" +
"        groups:  {get_resource: [ B, A] }    \n" +
"\n" +
"  federation2:\n" +
"    type: OS::Beacon::ServiceGroupManagement\n" +
"    properties:\n" +
"      name: GroupName\n" +
"      geo_deploy: { get_resource: geoshape_1}\n" +
"      resource:\n" +
"        groups:  {get_resource: B}\n" +
"               \n" +
"  fedNetworklink:\n" +
"    type: OS::Beacon::fedNetManagement\n" +
"    properties:\n" +
"      monitored_Group: stateless_VM\n" +
"      connected_VM: \n" +
"        - res_1_name\n" +
"        - res_2_name\n" +
"\n" +
"  fedSecGroup:\n" +
"    type: OS::Beacon::fedSecManagement\n" +
"    properties: \n" +
"      monitored_resource:\n" +
"        - federation\n" +
"        - res_1_name\n" +
"      fw_rules: [{ get_resource: server_security_group }]\n" +
"      policies: [{ get_resource: xacml_security_group }]\n" +
"\n" +
"  xacml_security_group:\n" +
"    type:  OS::Beacon::PoliciesAccManagement\n" +
"    properties:\n" +
"      rules:\n" +
"        - value \n" +
"\n" +
"  vnfsfcglobalResource:\n" +
"    type: TOSCA::Beacon::vnf_sfc_manifests\n" +
"    properties:\n" +
"      vnfd_version: version_____\n" +
"      vnfd_description: description\n" +
"      vnfd_templatename: template_name\n" +
"      vnffg_version: version_____\n" +
"      vnffg_description: description\n" +
"      vnffg_toplogy_template_description: description\n" +
"      resources: \n" +
"        innerres: {get_resource : [ VDU1,CP1,VL1,Forwarding_path1,VNFFG1]}\n" +
"  \n" +
"  VDU1:\n" +
"    type: tosca.nodes.nfv.VDU.Tacker\n" +
"    capabilities:\n" +
"      nfv_compute:\n" +
"        properties:\n" +
"          num_cpus: 1\n" +
"          mem_size: 512 MB\n" +
"          disk_size: 1 GB\n" +
"    properties:\n" +
"      image: cirros-0.3.5-x86_64-disk\n" +
"      availability_zone: nov\n" +
"      mgmt_driver: noop\n" +
"      config: |\n" +
"        param0: key1\n" +
"        param1: key2\n" +
"  \n" +
"  CP1:\n" +
"    type: tosca.nodes.nfv.CP.Tacker\n" +
"    properties:\n" +
"      management: true\n" +
"      order: 0\n" +
"      anti_spoofing_protection: false\n" +
"    requirements:\n" +
"      - virtualLink:\n" +
"          node: VL1\n" +
"      - virtualBinding:\n" +
"          node: VDU1\n" +
"          \n" +
"  VL1:\n" +
"    type: tosca.nodes.nfv.VL\n" +
"    properties:\n" +
"      network_name: net_mgmt\n" +
"      vendor: Tacker\n" +
"      \n" +
"  Forwarding_path1:\n" +
"    type: tosca.nodes.nfv.FP.Tacker\n" +
"    description: creates path (CP12->CP22)\n" +
"    properties:\n" +
"      id: 51\n" +
"      policy:\n" +
"        type: ACL\n" +
"        criteria:\n" +
"          - network_src_port_id: 640dfd77-c92b-45a3-b8fc-22712de480e1\n" +
"          - destination_port_range: 80-1024\n" +
"          - ip_proto: 6\n" +
"          - ip_dst_prefix: 192.168.1.2/24\n" +
"      path:\n" +
"        - forwarder: VNFD1\n" +
"          capability: CP12\n" +
"        - forwarder: VNFD2\n" +
"          capability: CP22\n" +
"\n" +
"  VNFFG1:\n" +
"    type: tosca.groups.nfv.VNFFG\n" +
"    description: HTTP to Corporate Net\n" +
"    properties:\n" +
"      vendor: tacker\n" +
"      version: 1.0\n" +
"      number_of_endpoints: 5\n" +
"      dependent_virtual_link: [VL12,VL22]\n" +
"      connection_point: [CP12,CP22]\n" +
"      constituent_vnfs: [VNFD1,VNFD2]\n" +
"    members: [Forwarding_path1]\n" ;
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
        //t.table_resourceset.put("OS::Beacon::Georeferenced_deploy",new LinkedHashMap<String,org.json.JSONObject>());
        //t.table_resourceset.put("OS::Beacon::ServiceGroupManagement",new LinkedHashMap<String,org.json.JSONObject>());
        
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
        
        System.out.println(om.getTostcaTempList(document));
        
    }
    
}
