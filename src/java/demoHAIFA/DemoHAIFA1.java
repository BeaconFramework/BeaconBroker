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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jclouds.openstack.neutron.v2.domain.Port;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;
import utils.FileFunction;

/**
 *
 * @author Giuseppe Tricomi
 */
public class DemoHAIFA1 {
    
    static String defPath="./templateTOupload/templateYAML.yaml";
    
    /**
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean cont=false;
        HashMap<String,String> table_UUID_ten=new HashMap<String, String>();
        DemoHAIFA1 dh=new DemoHAIFA1();
        Splitter spli;
        String tenant="";
        String path="";
        String manifestName="";
        //String stack="";
        String endpoint="";
        String user="";
        String psw="";
        String region="";
        DBMongo m=null;
        String templateUUID="";
        
        String userFederation="userFederation";
        String passwordFederation="passwordFederation";
        String prepath="./subrepoTemplate/demo";
        do{
            path=dh.consoleRequest("Insert absolute path of file where template are stored, or leave blank to use default value(./templateTOupload/templateYAML.yaml)",defPath);
            if(path.equals(""))
                path=defPath;
            m= new DBMongo();
            m.connectLocale("10.9.240.1");
            
            spli=new Splitter(m);
            while(tenant.equals("")){
                tenant=dh.consoleRequest("Insert tenant name","");//beacon
            }
            Object result= spli.loadFromFile(path, tenant);//da rivedere gestione assocazione
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
        String templatename=templateUUID;
        String tmpStr="";
        try{
            tmpStr = spli.ricomponiYamlManifest(templatename, tenant);
        }
        catch(MDBIException e ){
            System.out.println(e.getMessage());
            //reply.put("returncode", 1); 
            //reply.put("errormesg", "OPERATION ABORTED "+e.getMessage());
           
            //return reply.toJSONString();
        }
        Yaml yaml = new Yaml();
        LinkedHashMap<String, Object> list = (LinkedHashMap<String, Object>) yaml.load(tmpStr);
        org.json.JSONObject jo=new org.json.JSONObject(list);
        
        /*try{
            take_time_instant( "Starting Manifest analysis: ");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }*/
        OrchestrationManager om=new OrchestrationManager();
        ElasticityManagerSimple ela=new ElasticityManagerSimple(m);
        File f=new File(prepath);
        if (!f.exists()) {
            if(!f.mkdirs()){
                System.out.println("It's impossible create TMP file for manifest istantiation; OPERATION ABORTED.");
                //reply.put("returncode", 1); 
                //reply.put("errormesg", "It's impossible create TMP file for manifest istantiation; OPERATION ABORTED.");
                //return reply.toJSONString();
            }
        }
        //BEACON>>> There is another om.manifestistantiation created for dashboard but it isn't complete, for the moment
        manifestName=templatename;
        System.out.println("The Manifest "+manifestName+" analysis process started....");
        om.manifestinstatiation(manifestName,jo,tenant);
        spli.loadFromYAMLString(tmpStr,tenant,userFederation,manifestName,"null");
        try{
            take_time_instant( "Ending Manifest analysis: ");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        HashMap<String,ArrayList<ArrayList<String>>> tmpMap=om.managementgeoPolygon(manifestName, m, tenant);
        //retrieve from MongoDb federation password for federation user
        if(tmpMap==null)
        {
            System.out.println("No one Datacenter is found for manifest instantiation.\n");
            //reply=this.createErrorAnswer("No one Datacenter is found for manifest instantiation");
            //return reply.toJSONString();
        }
        String tmp=m.getFederationCredential(tenant, userFederation,"federationUser");
        //System.out.println(tmp);
        try {
            org.json.JSONObject tj=new org.json.JSONObject(tmp);
        } catch (JSONException ex) {
            System.out.println("Error occurred in manifest istantiation; OPERATION ABORTED."+ex.getMessage());
            ex.printStackTrace();
            //reply=createErrorAnswer("Error occurred in manifest istantiation; OPERATION ABORTED."+ex.getMessage());
            //return reply.toJSONString();
        }
        HashMap<String, ArrayList<ArrayList<OpenstackInfoContainer>>> tmpMapcred = om.managementRetrieveCredential(tmpMap, m, tenant, userFederation, passwordFederation, "RegionOne");
        //////////////////////////////////////////////////////////////////////////////////
        Set<String> setStack = om.getSGList(manifestName);
        //BEACON>>> this step it will be substitude by a function that analize the manifest and retireve the ServiceManagementGroups 
            //stored inside global manifest
        try{
            take_time_instant("Starting Manifest deploy: ");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        for (String stack : setStack) {
            
            ///String stack = "federation2";
            FileFunction ff = new FileFunction();
            String template = ff.readFromFile(prepath + "/" + tenant + manifestName + "_" + stack);

            OSFFM_ORC.Utils.ElasticitysuppContainer tmpsupp= om.deployManifest(template, stack, tmpMapcred, tmpMap, m,manifestName);
            ArrayList<ArrayList<HashMap<String, ArrayList<Port>>>> arMapRes= tmpsupp.getInfo();
            //BEACON:>>> It is needed decide what do with the info returned from om.deployManifest inside strucure arMapRes
            ////INSERIRE LA PARTE CHE GESTISCA L'ISTANZIAZIONE DEL SUNLIGHT POLICY THREAD
            
            try{
                if(om.getELaContainer(manifestName, stack)!=null){
                    String i=om.getELaContainer(manifestName, stack).getMinimumgap();
                    ela=ela.startMonitoringThreads(m,tenant, stack, tmpMap, userFederation,passwordFederation,i,tmpsupp.getFirstCloudId(),manifestName );
                }
            }
            catch(Exception e){
                System.err.println(e.getMessage());
            }
        }
        /*try{
            take_time_instant( "Ending Manifest deploy: ");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        try{
            take_time_instant( "Starting Network linking: ");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }*/
        String tmptest="";
        while(tmptest.equals("")){
                tmptest=dh.consoleRequest("Press any key to continue:","");//beacon
            }
        om.prepareNetTables4completeSharing(tenant, manifestName, tmpMapcred, m);
       /* try{
            take_time_instant( "Ending Network linking: ");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }*/
        String[]entries = f.list();
        for(String s: entries){
            File currentFile = new File(f.getPath(),s);
            currentFile.delete();
        }
        f.delete();//at the end
        //reply.put("returncode", 0);
        //reply.put("errormesg", "");
        //return reply.toJSONString();
    }
    
    private static org.json.simple.JSONObject createErrorAnswer(String errrorMesg){
        org.json.simple.JSONObject reply=new org.json.simple.JSONObject();
        reply.put("returncode", 1);
        reply.put("errormesg", errrorMesg);
        reply.put("templateuuid", null); 
        return reply;
    }
    
    private static void take_time_instant(String begin) throws Exception
    {
        String fileTimelog=".timelog.txt";
        FileWriter fw=null;
        try{
            File timelog=new File(fileTimelog);
            if (!timelog.exists()) {
                timelog.createNewFile();
            }
            fw = new FileWriter(timelog.getAbsoluteFile(),true);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(begin+sdf.format(cal.getTime()));
        bw.close();
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