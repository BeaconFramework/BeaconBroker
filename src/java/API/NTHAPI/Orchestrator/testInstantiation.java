/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API.NTHAPI.Orchestrator;



import API.NTHAPI.SitesResource;
import JClouds_Adapter.OpenstackInfoContainer;
import MDBInt.DBMongo;
import MDBInt.Splitter;
import MDBInt.MDBIException;
import OSFFM_ELA.ElasticityManagerSimple;
import OSFFM_ORC.OrchestrationManager;
import OSFFM_ORC.Utils.ElasticitysuppContainer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Level;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;
import org.jclouds.openstack.neutron.v2.domain.Port;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.yaml.snakeyaml.Yaml;
import utils.FileFunction;

/**
 *
 * @author Giuseppe Tricomi
 */


public class testInstantiation {

    
   static final Logger LOGGER = Logger.getLogger(IstantiateManifest.class);
    Splitter spli;
    DBMongo m;
     /**
   
    /**
     * This function Istantiate all Stack described inside global Manifest.
     * @param tenant
     * @param jsonInput
     * @return 
     * @author gtricomi
     */
    
    public static void main(String[] args) {
        //BEACON>>> INSIDE THIS FUNCTION WE NEED TO ADD SOME AUTHENTICATION STUFF, FOR THE MOMENT IS A 
        //SIMPLE UNAUTHENTICATING OPERATION
        String templatename="";//templatename="cc228189-0f2f-4aa4-8336-88db88e477d2";
        //String home=System.getProperty("java.home");      //Removed, unused
        //String fs=System.getProperty("file.separator");   //Removed, unused
        //String prepath=home+fs+"subrepoTemplate";
        String tenant="yamlTest2";
        String templateRef="null";
        String userFederation="",passwordFederation="";
        String prepath="./subrepoTemplate/"+tenant;
         userFederation="userFederation";
         passwordFederation="passwordFederation";
        // prepath="./subrepoTemplate/yamltest2";
        String templateUUID="";
        boolean cont=false;
        DBMongo m= new DBMongo();
        m.connectLocale("10.9.0.42");
        HashMap<String,String> table_UUID_ten=new HashMap<String, String>();
        testInstantiation dh=new testInstantiation();
         Splitter spli=new Splitter(m);
          OrchestrationManager om=new OrchestrationManager();
        do{
           /* path=dh.consoleRequest("Insert absolute path of file where template are stored, or leave blank to use default value(./templateTOupload/templateYAML.yaml)",defPath);
            if(path.equals(""))
                path=defPath;
            
            
            
            while(tenant.equals("")){
                tenant=dh.consoleRequest("Insert tenant name","");//beacon
            }*/
          
            Object result= spli.loadFromFile("./templateTOupload/ONEFLtemplateYAML2.yaml","yamlTest2");//path, tenant);//da rivedere gestione assocazione
            if(result ==null){
                System.err.println("Somethings are went wrong with ");
                break;
            }
            else{
                System.out.println("Manifest is stored with UUID = "+result);
                templateUUID=(String)result;
                templatename=templateUUID;
               try {
                   System.out.println((m.retrieveONEFlowTemplate("yamlTest2", templateUUID, "prova")));
               } catch (MDBIException ex) {
                   java.util.logging.Logger.getLogger(testInstantiation.class.getName()).log(Level.SEVERE, null, ex);
               }
                if((dh.consoleRequest("Insert another Manifest?(default:no)[yes or no]","no")).equals("yes")){
                    cont=true;
                    table_UUID_ten.put((String)result, tenant);
                    
                }
            }
            
        }
        while(cont);
        
        String tmpStr="";
        try{
            tmpStr = spli.ricomponiYamlManifest(templatename, tenant);
        }
        catch(MDBIException e ){
            System.err.println(e.getMessage());
            
        }
        Yaml yaml = new Yaml();
        LinkedHashMap<String, Object> list = (LinkedHashMap<String, Object>) yaml.load(tmpStr);
        org.json.JSONObject jo=new org.json.JSONObject(list);
       
       
       // ElasticityManagerSimple ela=new ElasticityManagerSimple(this.m);
        File f=new File(prepath);
        if (!f.exists()) {
            if(!f.mkdirs()){
                System.err.println("It's impossible create TMP file for manifest istantiation; OPERATION ABORTED.");
                
            }
        }
        //BEACON>>> There is another om.manifestistantiation created for dashboard but it isn't complete, for the moment
        String manifestName=templatename;
        //System.out.println("The Manifest "+manifestName+" analysis process started....");
        om.manifestinstatiation(manifestName,jo,tenant);
        spli.loadFromYAMLString(tmpStr,tenant,userFederation,manifestName,templateRef);
        HashMap<String,ArrayList<ArrayList<String>>> tmpMap=om.managementgeoPolygon(manifestName, m, tenant);
        //retrieve from MongoDb federation password for federation user
        if(tmpMap==null)
        {
            System.out.println("No one Datacenter is found for manifest instantiation.\n");
            
        }
        String tmp=m.getFederationCredential(tenant, userFederation,"federationUser");
        //System.out.println(tmp);
        try {
            org.json.JSONObject tj=new org.json.JSONObject(tmp);
        } catch (JSONException ex) {
            System.err.println("Error occurred in manifest istantiation; OPERATION ABORTED."+ex.getMessage());
            //ex.printStackTrace();
            
        }
        
        HashMap<String, ArrayList<ArrayList<OpenstackInfoContainer>>> tmpMapcred = om.managementRetrieveCredential(tmpMap, m, tenant, userFederation, passwordFederation, "RegionOne");
        //////////////////////////////////////////////////////////////////////////////////SERGROUP MANAGEMENT
        Set<String> setStack = om.getSGList(manifestName);
        //BEACON>>> this step it will be substitude by a function that analize the manifest and retireve the ServiceManagementGroups 
            //stored inside global manifest
        for (String stack : setStack) {
            
            FileFunction ff = new FileFunction();
            String template = ff.readFromFile(prepath + "/" + tenant + manifestName + "_" + stack);
            
            ElasticitysuppContainer tmpsupp=om.deployManifest(template, stack, tmpMapcred, tmpMap, m,manifestName);
            ArrayList<ArrayList<HashMap<String, ArrayList<Port>>>> arMapRes= tmpsupp.getInfo();
            //BEACON:>>> It is needed decide what do with the info returned from om.deployManifest inside strucure arMapRes
            ////INSERIRE LA PARTE CHE GESTISCA L'ISTANZIAZIONE DEL SUNLIGHT POLICY THREAD
         /*  try{
                if(om.getELaContainer(manifestName, stack)!=null){
                    String i=om.getELaContainer(manifestName, stack).getMinimumgap();
                    ela=ela.startMonitoringThreads(this.m,tenant, stack, tmpMap, userFederation,passwordFederation,i,tmpsupp.getFirstCloudId() );
                }
            }
            catch(Exception e){
                
                System.err.println("Error occurred in elasticity Thread launching; OPERATION ABORTED."+e.getMessage());
            }*/
        }
        /////////////////////////////////////////////////////////////ONEFLOWTEMPLATE MANAGEMENT
        //INSERIRE QUI LA PARTE CHE GESTISCE I TEMPLATE PER ONEFLOW
        om.istantiateONE_Templates(manifestName, tmpMapcred, tmpMap, m, tenant);
        
        
        ////////////////////////////////////////////////////////////NETWORK LINK MANAGEMENT
        om.prepareNetTables4completeSharing(tenant, manifestName, tmpMapcred, m);//27/02 verificare questa parte del flusso deve interagire diversamente con il FEDSDN
        String[]entries = f.list();
        for(String s: entries){
            File currentFile = new File(f.getPath(),s);
            currentFile.delete();
        }
        f.delete();//at the end
        
    }
    
    private JSONObject createErrorAnswer(String errrorMesg){
        JSONObject reply=new JSONObject();
        reply.put("returncode", 1);
        reply.put("errormesg", errrorMesg);
        reply.put("templateuuid", null); 
        return reply;
    }
    
    private void take_time_instant(FileWriter fw, String begin) throws Exception
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
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
