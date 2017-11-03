An extensive desription of the beacon service manifest is done in D3.2, D3.3, D4.2 and D4.3 hosted here: http://www.beacon-project.eu/downloads/

What is important to know when you start to interact with beacon service manifest **[bsman]** is the YAML and HOT (Heat Orchestrator Template)standards.

Basically a bsman is composed by 4 main elements:

* heat_template_version: 2014-10-16

* description: [optional]

* parameters: used to define set of parameters used inside the bsman creation (actually this is used to define default value for the parameters)

* resources: in bsman all are defined as resources, nested or atomically all teh definition are done here

* outputs: [for future usages]

**Resources:**

the bsman is a HOT template enhanched, the is able to contains all the resource types defined for "heat_template_version: 2014-10-16"  plus some specifics resources: 

* OS::Beacon::Georeferenced_deploy : it is used define an area uin which a specific group of resources could be deployed or applied

* OS::Beacon::ServiceGroupManagement : it is used define a group of resources that have to be deployed as atomic set

* ONE::Beacon::OneFlowTemplate : it is used define a group of resources that have to be deployed as atomic set on OpenNebula sites. This resource will be send to OneFlow component. 

* TOSCA::Beacon::vnf_sfc_manifests : this is a map used to identify all the resources involved inside the TOSCA manifest. The property "name" of this resource is used to retrieve the pair VNFD and VNFFG manifest.

* tosca.nodes.nfv.VDU.Tacker

* tosca.nodes.nfv.CP.Tacker

* tosca.nodes.nfv.VL

* tosca.nodes.nfv.FP.Tacker

* tosca.groups.nfv.VNFFG

* OS::Beacon::PoliciesAccManagement : for future usages

* OS::Beacon::fedSecManagement : for future usages

* OS::Beacon::fedNetManagement : for future usages

Examples of bsman are provided here: https://github.com/BeaconFramework/BeaconBroker/tree/master/templateTOupload
