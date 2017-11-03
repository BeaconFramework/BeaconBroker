# BeaconBroker
High level Orchestrator mainly oriented to Openstack Clouds, developed inside BEACON H2020 Project
The architecture of the BB is described in the public deliverable hosted here: http://www.beacon-project.eu/downloads/
in particular the reader need to have a look in deliverable:

 i)        *D2.3* for general architectural concept
 
 ii)       *D3.2, D3.3, D4.2* and *D4.3* for compenent details and interaction details
 
The activity done by the Beacon Broker **[BB]** is strictly related with the data contained inside the MongoDB databases.

Data, is grouped in several databases, one for each federation tenant (called borrower), and a common database called "ManagementDB". 
Each borrower's database has a series of required collection of document needed for the BB works; these collections contains the  information related to clouds involved in federation. Below are listed the collection that need to be constructedby federation administrator when a new borrower is included in the federation:

**a) Inside borrower database:**

* Countries: each document represents a multipoligon able to identify a nation. More in general in this collection are stored the multipolygon usefull for the borrower deployment;

* credentials: it contains a series document in which are represented the user (we are talking about user of the federation) credential in a target federated cloud;

* datacenters: it contains information about cloud in which borrower has an agreement/contract and that are used in the federated deployment;

* faInfo: It contains the information related to BNA component for each federated cloud;

**b) Inside ManagementDB some document have to be added in the listed below collection**

* Federation_Credential: it contains information about borrower's name, token and password

* fedtenanttoBor: it contains information about borrower's name, uuid of its tenant in afederated cloud and the endpoint of that cloud.

An important collection present inside "ManagementDB" database is "SystemInfos",  this collection contains the pointer for the module involved in the federation like BNM.

Examples of document present in the collection managed by administrator are available here: https://github.com/BeaconFramework/BeaconBroker/tree/master/Collection_Template

Like the BNMD and the BB_ELA also the BB is a Web Application, to instantiate it, the administrator have to create a directory in the path: "/home/beacon/beaconConf/" with the configuration file listed below according with the template provided in the template folder:

* configuration_bigDataPlugin.xml

The BB exposes several functionality via REST WS that are listed below:

* http://[BB_BASE_PATH]/os2os/northBr/manifest/{borrower}/templates/ : it is a POST WS, consumes and produces a JSON object and it is used to store on borrower's databese in MongoDB the beacon service manifest provided as input.

Input parameters are:

> * username: name of the user/borrower that has requested the operation

> * templateName: it is an UUID used to store the manifest

> * templateRef: it is the UUID of the previous version of the manifest, if the manifest is totally new this field is null

> * templates: this is the string representation of the YAML beacon service manifest

* http://[BB_BASE_PATH]/fednet/northBr/network/netsegment: it is a POST WS, consumes and produces a JSON object and it is used to create a network segment on a federated cloud in the borrower's project in target cloud.

Input parameters are:

> * dbName: name of the borrower that has requested the operation

> * fedUser: it is a nested JSONObject that contains BB info:

>> * token: token used by BB to recognize borrower/user

>> * endpoint: endpoint of the target federated cloud

> * netSeg: BB name of the network segment

> * CloudName: name of the federated cloud used by BB, it corresponds with "cloudId" field of the "datacenters" collection o mongoDB

> * hashMapParam: it is a String representation of an HashMap that contains te following elements:

>> * dhcpEnable: set enabled the dhcp for new network segments

>> * shared: true if the network segment is shared with other tenant, normally this parameter is false

>> * adminStateUp: true if this network segment will be used only by project admin

>> * external: true if the network segment is external network, normally this parameter is false

* http://[BB_BASE_PATH]/os2os/{borrower}/templates/ : it is a GET WS, produces a JSON object that contains all the beacon service manifest stored inside borrower's databases. The object returned has the following structure:

> {"templates": [{"id": "uuid","templateName":"name","version": "number","user": "name","templateRef": "uuidStartingTemplate","date": "timestamp"}, {"id": "uuid","templateName":"name","version": "number","user": "name","templateRef": "uuidStartingTemplate", "date": "timestamp"} ],“returncode”: 0,“errormesg”:”None”}

* http://[BB_BASE_PATH]/os2os/{borrower}/templates/{uuidTemplate} : it is a GET WS, produces a JSON object that contains the beacon service manifest identified by {uuidTemplate} stored inside borrower's databases. The object returned has the following structure:

> {"templates": "yamlTemplateString" , “returncode”: 0, “errormesg”:”None”, "templateName":"name", "version": "number", "user": "name", "templateRef": "uuidStartingTemplate", "date": "timestamp" }

* http://[BB_BASE_PATH]/os2os/{tenant}/templates/{uuidTemplate}/runTime :  it is a GET WS, produces a JSON object that contains information about the deployed application in the target clouds, this is the information stored inside the borrower's database. The object returned has the following structure:

> { “Shapes” : [{ “id” : “idDatacenter2”, “EndPoint” : ”IP/port”, “State” : “up/down”, “GeoShape”: {GEOJSON Multipolygon}, “resources” : [{“id”:“idRes1”, “nameRes”: “name”},{“id”:“idRes2”, “nameRes”: “name”}]},{ “id” : “idDatacenter1”,“EndPoint” : ”IP/port”,“State” : “up/down”,“GeoShape”: {GEOJSON Multipolygon},“resources” : [{“id”:“idRes1”, “nameRes”: “name”},{“id”:“idRes2”, “nameRes”: “name”}]}]  Links: [ { “src” : “idDatacenter1”, “dst” : “idDatacenter2” }, { “src” : “idDatacenter1”, “dst” : “idDatacenter2”}], “returncode”: 0, “errormesg”:”None”}

* http://[BB_BASE_PATH]/os2os/orchestrator/{borrower}/startTemplates/ : it is a POST WS, consumes and produces a JSON object and it is used to start the deployment of beacon service manifest stored on borrower's databese in MongoDB.

Input parameters are:

> * federationTenant: name of the borrower that has requested the operation

> * passwordFederation: password of the borrower in federation context

> * templateId: uuid of the template that will be deployed

* http://[BB_BASE_PATH]/os2os/orchestrator/activatetwin :it is a POST WS, consumes and produces a JSON object and it is used to activate a twinVM without shutoff the original one. 

Input parameters are:

> * tenant: name of the borrower that has requested the operation

> * userFederation: username that invokes this operation

> * pswFederation: password of the user/borrower in federation context 

> * vmTwin: uuid that identify the VM that will be activated

* http://[BB_BASE_PATH]/TOSCAmanifest/{borrower}/{manifestName}/{templateRef}/{resName} : it is a GET WS, produces a JSON object that contains both VNFD and VNFFG related to the TOSCA Manifest stored inside *borrower*'s database as element contained in the beacon service manifest named *manifestName* and identify by the resource name *resName*. Path parameter *templateRef* represents the "father manifest" of this beacon service manifest and could be *null*. 
information about the deployed application in the target clouds, this is the information stored inside the borrower's database.

* http://[BB_BASE_PATH]/os2os/orchestrator/migrateVMs : it is a POST WS, consumes a JSON object and it is used to activate a twinVM after having shutted-off the original one. Normally it is used by BB_ELA when an elasticity action have to be completed.

Input parameters are:

> * tenant: name of the borrower that has requested the operation

> * userFederation: username that invokes this operation

> * pswFederation: password of the user/borrower in federation context 

> * vmTwin: uuid that identify the VM that will be activated

> * vm: uuid that identify the VM that will be shutted-off

Refers to https://github.com/BeaconFramework/BeaconBroker/blob/master/BeaconServiceManifest-Fundamentals to understand how create a beacon service manifest.
