# BeaconBroker
High level Orchestrator mainly oriented to Openstack Clouds, developed inside BEACON H2020 Project
The architecture of the BB is described in the public deliverable hosted here: http://www.beacon-project.eu/downloads/
in particular the reader need to have a look in deliverable:

 i)        D2.3 for general architectural concept
 
 ii)       D3.2, D3.3, D4.2 and D4.3 for compenent details and interaction details
 
The activity done by the BB is strictly related with the data contained inside the MongoDB databases.

Data, is grouped in several databases, one for each federation tenant (called borrower), and on common for all borrower called "ManagementDB". 
Each borrower's database has a series of required collection of document needed for the BB works; these collections contains the  information related to clouds involved in federation. Below are listed the collection that need to be constructedby federation administrator when a new borrower is included in the federation:

**a) Inside borrower database:**

* Countries: each document represents a multipoligon able to identify a nation. More in general in this collection are stored the multipolygon usefull for the borrower deployment;

* credentials: it contains a series document in which are represented the user (we are talking about user of the federation) credential in a target federated cloud;

* datacenters: it contains information about cloud in which borrower has an agreement/contract and that are used in the federated deployment;

* faInfo: It contains the information related to BNA component for each federated cloud;

**b) Inside ManagementDB some document have to be added in the listed below collection**

* Federation_Credential: it contains information about borrower's name, token and password

* fedtenanttoBor: it contains information about borrower's name, uuid of its tenant in afederated cloud and the endpoint of that cloud.


....
TBC
