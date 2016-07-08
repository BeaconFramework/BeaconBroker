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

package demoHAIFA;

import JClouds_Adapter.OpenstackInfoContainer;
import MDBInt.DBMongo;
import MDBInt.MDBIException;
import MDBInt.Splitter;
import OSFFM_ELA.ElasticityManagerSimple;
import OSFFM_ORC.OrchestrationManager;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jclouds.openstack.neutron.v2.domain.Port;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Giuseppe Tricomi
 */
public class DemoHAIFA {
    
    static String defPath="./templateTOupload/templateYAML.yaml";
    
    /**
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean cont=false;
        HashMap<String,String> table_UUID_ten=new HashMap<String, String>();
        DemoHAIFA dh=new DemoHAIFA();
        Splitter s;
        String tenant="";
        String path="";
        String manifestName="";
        String stack="";
        String endpoint="";
        String user="";
        String psw="";
        String region="";
        DBMongo m=null;
        String templateUUID="";
        
        do{
            path=dh.consoleRequest("Insert absolute path of file where template are stored, or leave blank to use default value(./templateTOupload/templateYAML.yaml)",defPath);
            if(path.equals(""))
                path=defPath;
            m= new DBMongo();
            m.connectLocale();
            
            s=new Splitter(m);
            while(tenant.equals("")){
                tenant=dh.consoleRequest("Insert tenant name","");//beacon
            }
            Object result= s.loadFromFile(path, tenant);//da rivedere gestione assocazione
            if(result ==null){
                System.err.println("Somethings are went wrong with ");
                break;
            }
            else{
                System.out.println("Manifest is stored with UUID = "+result);
                templateUUID=(String)result;
                if((dh.consoleRequest("Insert another Manifest?(default:no)[yes or no]","no")).equals("yes")){
                    cont=true;
                    table_UUID_ten.put((String)result, tenant);
                    
                }
            }
        }
        while(cont);
        do
        {
            String uuid=dh.consoleRequest("Insert Manifest UUID that will be elaborated to deploy resources","");
            if(!uuid.equals("")){
                String tmpStr="";
                try {
                    tmpStr = s.ricomponiYamlManifest(uuid,tenant);
                } catch (MDBIException ex) {
                    Logger.getLogger(DemoHAIFA.class.getName()).log(Level.SEVERE, null, ex);
                }
                //System.out.println(tmp);
                Yaml yaml = new Yaml();
                LinkedHashMap<String, Object> list = (LinkedHashMap<String, Object>) yaml.load(tmpStr);
                JSONObject jo=new JSONObject(list);
//2.0 algoritmo di elaborazione manifest
                OrchestrationManager om=new OrchestrationManager();
                manifestName=path.substring(path.lastIndexOf("/")+1,path.lastIndexOf("."));
                om.manifestinstatiation(manifestName,jo,tenant);
//2.1 partendo dala geoshape richiamare funzione DBMongo.getDatacenters, per ogni oggetto dell'ArrayList devo recuperare le informazioni;
////idmEndpoint, cloudId; ci sono come altre info : nome, descrizione,punto geojson,insert timestamp
                HashMap<String,ArrayList<ArrayList<String>>> tmpMap=om.managementgeoPolygon(manifestName, m, tenant);
//2.2 richiamo funzione DBMongo.getFederatedCredential restituisce l'oggetto JSON in formato Stringa con 3 campi:
////federatedUser,federatedCloud,federatedPassword
                HashMap<String,ArrayList<ArrayList<OpenstackInfoContainer>>> tmpMapcred=om.managementRetrieveCredential(tmpMap,m,tenant,"userFederation","passwordFederation","RegionOne");//userFederation,passwordFederation
//2.3 chiama heat function per deploy of stack, e stampa a video equivalente della chiamata heat
////2.4 invocare funzione che lista da heat l'insieme delle risorse nello stack, per ogni risorsa recupera le informazioni sulla rete
////// e mette in un hashMap<String,string> (idRisorsa,idRete) e poi le trasferisce salvandole su Mongo DB
                dh.listSplittedTemplate(tenant);
                while(stack.equals("")){
                    stack=dh.consoleRequest("Insert stack name(name of submanifest) that we want instatiate","");
                }
                String home=System.getProperty("java.home");
                String template=dh.readFromFile("./subrepoTemplate/"+tenant+"/"+stack);
                String stackName=stack.substring(stack.lastIndexOf("_")+1,stack.lastIndexOf(".yaml")>=0?stack.lastIndexOf(".yaml"):stack.length());
                ArrayList arDC=(ArrayList<ArrayList<String>>)tmpMap.get(stackName);
                ArrayList arCr=(ArrayList<ArrayList<OpenstackInfoContainer>>)tmpMapcred.get(stackName);
                ArrayList<ArrayList<HashMap<String,ArrayList<Port>>>> arMapRes= new ArrayList<ArrayList<HashMap<String,ArrayList<Port>>>>();
                boolean skip=false,first=true;
                int arindex=0;
                while(!skip){
                    ArrayList tmpArDC=(ArrayList<String>)arDC.get(arindex);
                    ArrayList tmpArCr=(ArrayList<OpenstackInfoContainer>)arCr.get(arindex);
                    ArrayList<HashMap<String,ArrayList<Port>>> arRes= new ArrayList<HashMap<String,ArrayList<Port>>>();
                    for (Object tmpArCrob : tmpArCr) {
                        /*
                        endpoint = ((OpenstackInfoContainer) tmpArDC1).getEndpoint(); //new JSONObject((String)tmpArDC.get(index)).getString("idmEndpoint");
                        user = ((OpenstackInfoContainer) tmpArDC1).getUser(); //new JSONObject((String)tmpArCr.get(index)).getString("federatedUser");
                        psw = ((OpenstackInfoContainer) tmpArDC1).getPassword(); //new JSONObject((String)tmpArCr.get(index)).getString("federatedPassword");
                        */
                        //System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n"+template);
                        boolean resultIS=om.stackInstantiate(template,(OpenstackInfoContainer) tmpArCrob,m,stackName);//BEACON>>> in final version of OSFFM
                        //we will use variable result to understand if the stack is deployed inside the federated cloud
                        
                        region=dh.consoleRequest("Insert region name:[Deafult: RegionOne]","RegionOne");//this element it will be analized in second 
                        ((OpenstackInfoContainer)tmpArCrob).setRegion(region);
                          HashMap<String,ArrayList<Port>> map_res_port=om.sendShutSignalStack4DeployAction(stackName,(OpenstackInfoContainer)tmpArCrob,first,m);
                        if(resultIS)
                            first=false;//if first stack creation is successfully completed, the other stacks instantiated are not the First
                        //and need different treatment.
                        arRes.add(map_res_port);
                    }
                    arMapRes.add(arRes);
                    arindex++;
                    if(arindex>=arDC.size())
                        skip=true;
                }
//2.5 invocare funzione che simula la sofferenza della risorsa X
////2.6 spegnimento VM tramite Nova con le informazioni contenute su Mongo relative al runtime del sistema
//////per questa operazione è necessario recuperare il cloudID delle altre clouds e utilizzare le credenziali memorizzate dentro tmpMapcred
////////2.7 richimare il metodo simulateSuffers dell'elasticityManagerSimple per far si che si avvii la procedura di gestione opportuna
                ElasticityManagerSimple ems=new ElasticityManagerSimple();
                String vmName="";
                while(vmName.equals(""))
                    vmName=dh.consoleRequest("Insert VM UUID that will be entered in problematic state","");
                ems.simulatesuffering(om,vmName,tenant,"userFederation","passwordFederation",m,0,region);
//2.8 la funzione sopra restituisce le informazioni sulle vm in modo da far si che possano essere legate dai FA               
            }
        }
        while(cont);
    }
    
   
    /**
     * It returns a String that contains the whole manifest.
     * @param fileName
     * @return 
     */
    private String readFromFile(String fileName){
        Path p=Paths.get(fileName);
        String manifest="";
        try (InputStream in = Files.newInputStream(p);
            BufferedReader reader =
              new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                manifest=manifest+"\n"+line;
            }
        } catch (IOException x) {
            System.err.println(x.getMessage());
            System.err.println(x);
        }
        return manifest;
    }
    
    /**
     * It returns String acquired or empty String
     * @param message
     * @return 
     */
    private String consoleRequest(String message,String def){
        String result="";
        System.out.println(message);
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                result= reader.readLine();
                if(result.equals(""))
                    return def;
            }
            catch(Exception e)
            {
                System.err.println("Acquisition error"+e.getMessage());
                return "";
            }
            return result;
    }
    
    private void listSplittedTemplate(String tenant){
        String home=System.getProperty("java.home");
        String fs=System.getProperty("file.separator");
        //File folder = new File(home+fs+"subrepoTemplate");
        File folder = new File("./subrepoTemplate/"+tenant);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
          if (listOfFiles[i].isFile()) {
            System.out.println("File " + listOfFiles[i].getName());
          } else if (listOfFiles[i].isDirectory()) {
            System.out.println("Directory " + listOfFiles[i].getName());
          }
        }
    }
      
    
      
}
