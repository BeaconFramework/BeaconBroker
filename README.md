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

> * fedUser: it is a nested JSONObject usefull for 

> * templateRef: it is the UUID of the previous version of the manifest, if the manifest is totally new this field is null

> * templates: this is the string representation of the YAML beacon service manifest.

* http://[BB_BASE_PATH]/

* http://[BB_BASE_PATH]/

* http://[BB_BASE_PATH]/

* http://[BB_BASE_PATH]/

* http://[BB_BASE_PATH]/

* http://[BB_BASE_PATH]/

....
TBC
