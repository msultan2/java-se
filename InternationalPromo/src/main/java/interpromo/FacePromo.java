// FrontEnd Plus GUI for JAD
// DeCompiled : FAFMaker.class

package interpromo;

import drs.GenericSubInfo;
import drs.Payment;

import java.io.*;
//import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
//import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;

import oracle.util.OracleDao;

import oracle.util.PoolManager;

import org.apache.log4j.Logger;

import promo.util.PropertyHandler;

import ucip.UcipHandler;
//import java.util.logging.Logger;
import ucip.log.UcipLogger;
//import ucip.util.PropertyHandler;

public class FacePromo
{

    private static final  double PROMO_FACTOR = 0.25;
    private static final  double MAX_ADJ_LIMIT = 200.0;
    private static final  Integer DA_ID = 2;
    private static final String ADJ_CODE = "CRE";
    private static final String ADJ_TYPE = "25-REC-PRO";
    
    private final String svcClasses = "Elig_SvcClasses.txt";
    public static final String iDELIM = ",";
    public static final String oDELIM = ",";
    
    static final  String AIWADRD1_DB = "AIWA2";
    static final  String SMPP_DB = "SMS";
    
    public static final  String SMS_APP_ID = "RCHRG";
    public static final  String SMS_SENDER = "vodafone";
    static final  String SMS_FILE = "sms.txt";
    static final  String SMS_TEXT;;
    
    private OracleDao oraAIWA2;
    private OracleDao oraSMPP;
    private UcipHandler ucipAdjuster; 
    private File ifile;
    //private File succcessLog;
    //private File failureLog;
    private String logBaseDir;
    private String baseDate;
    private ArrayList svcClses;
    
    private BufferedReader br;
    private PrintWriter pwFail;
    private PrintWriter pwSuccess;
    private PrintWriter smsFail;
    private PrintWriter chkStatusFail;
    private PrintWriter adjDBLogFail;
    private boolean flag = true;
    
    //private Payment payment;
    
     private static UcipLogger logger;
     
     static {
         SMS_TEXT = PropertyHandler.getProperty(SMS_FILE);   
         logger = new UcipLogger(FacePromo.class);
     }

    public FacePromo(String fin, String logBaseDir, String baseDate)
    {
        //i = 0;
        this.logBaseDir = logBaseDir;
        this.baseDate = baseDate;
        this.ifile = new File(fin);
        if(!ifile.exists())
        {
            System.err.println("Input File: '" + ifile.getName() + "' does not exist !");
            logger.error("Input File: '" + ifile.getName() + "' does not exist !", null);
            System.exit(0);
        }
        
        if (! this.initSvcClasses()){
            System.err.println("Input File: '" + ifile.getName() + "' does not exist "+
                    "Or an error initializing Service Classes may have occured !"
                    );
            logger.error("Input File: '" + ifile.getName() + "' does not exist "+
                    "Or an error initializing Service Classes may have occured !", null);
            System.exit(0);
        }
        
        //this.succcessLog = new File (successLog);
        //this.failureLog = new File (failureLog);
        
  
        try
        {
            br = new BufferedReader(new FileReader(this.ifile));
            pwSuccess = new PrintWriter(new BufferedWriter(new FileWriter(
                    this.logBaseDir+"/UCIPSuccess-"+baseDate+".txt")) , true);
            pwFail = new PrintWriter(new BufferedWriter(new FileWriter(
                    this.logBaseDir+"/UCIPFail-"+baseDate+".txt")) , true);
            smsFail = new PrintWriter(new BufferedWriter(new FileWriter(
                    this.logBaseDir+"/smsFail-"+baseDate+".txt")) , true);
            chkStatusFail = new PrintWriter(new BufferedWriter(new FileWriter(
                    this.logBaseDir+"/chkProcFail-"+baseDate+".txt")) , true);
            adjDBLogFail = new PrintWriter(new BufferedWriter(new FileWriter(
                    this.logBaseDir+"/dblogProcFail-"+baseDate+".txt")) , true);
                    
        }
        catch(Exception ex){  
            //exception.printStackTrace(); 
            logger.error("Error in cerating Suc/Fail log files !!", ex);
            flag = false;
       }
        
       //Initialise objects
       oraAIWA2 = new OracleDao(AIWADRD1_DB);
       if (! oraAIWA2.getConnection()) {
           logger.error("Error in Creating Connection to AIWA2...", null);
           flag = false;
       }
       
//       oraSMPP = new OracleDao(SMPP_DB);
//       if (! oraSMPP.getConnection()){
//           logger.error("Error in Creating Connection to SMPP...", null);
//           flag = false;
//       }
       
       ucipAdjuster = new UcipHandler();
    }
    
    private boolean initSvcClasses(){
        
      BufferedReader clsBr = null;

      File svcClassesFile = new File(svcClasses);
      if (!svcClassesFile.exists()) {
            return false;
      }
      try {
            clsBr = new BufferedReader(new FileReader(svcClassesFile));
     } catch (FileNotFoundException ex) {
            logger.error("Init Service Classes failed !!", ex);
            //ex.printStackTrace();
            return false;
     }
            
     String line;
     svcClses = new ArrayList();
     
     try {
           //for (line = clsBr.readLine(); line != null; line = clsBr.readLine();) {
         line = clsBr.readLine();
         while (line != null){
             
                  svcClses.add(Integer.parseInt(line));
                  line = clsBr.readLine();
           }

     } catch (IOException ex) {
            logger.error("smthg wrong with loading svc classes list !!",ex);
            //ex.printStackTrace();
            return false;
     }
     return true;
    }
        
    
    private Payment parseLine()
    {
        try {
            String line = br.readLine();
            if (line != null) {
                return new Payment(line.split(iDELIM));
            } else {
                return null;
            }
        } catch (IOException ex) {
            //Logger.getLogger(FacePromo.class.getName()).log(Level.SEVERE, null, ex);
            logger.error("Failed to read line from file..",ex);
            return null;
        }
    }
    
    private boolean adjust(Payment payRecord){
                
      /*
       Double adjValue;
       if (payRecord.getRealTransactionAmount() > MAX_ADJ_LIMIT){
         adjValue = new Double( PROMO_FACTOR * MAX_ADJ_LIMIT);
         logger.debug(payRecord.getSubscriberNumber()+" capped adjustment "+adjValue.toString());
       }
       else
         adjValue = new Double( PROMO_FACTOR * payRecord.getRealTransactionAmount());
         logger.debug(payRecord.getSubscriberNumber()+" Normal adjustment "+ adjValue.toString());
        
       payRecord.setPromoTransactionAmount(adjValue.doubleValue());
        
        //Adjust statmnets goes here
       logger.debug("UCIP Adjusting "+ new Double(100 * payRecord.getPromoTransactionAmount()).toString()+ "DA: "
                + DA_ID.toString() + "to: "+ payRecord.getSubscriberNumber()
                + " " + ADJ_CODE.toString()
                + " " + ADJ_TYPE.toString() ); 
       
       boolean status = ucipAdjuster.adjustDedAccountFD1(
               new Double(100 * payRecord.getPromoTransactionAmount()).toString(),
                DA_ID.toString() ,payRecord.getSubscriberNumber() , ADJ_CODE.toString(), 
                ADJ_TYPE.toString());
        
 */       
        //Succesfull
//        //UCIP Failure here
         logger.debug(payRecord.getSubscriberNumber() + " UCIP failed with staus fail :(");
        //And Call external Stored Procedure here  to log failures
        if (! oraAIWA2.getConnection()){
             logger.debug(payRecord.getSubscriberNumber() + 
                     " D_insertAdjLog Conn to AIWA2 failed !, UCIP failure not logged in DB");
             adjDBLogFail.println(payRecord.getSubscriberNumber() + oDELIM +
                    "Failed to get conn 4 D_insertAdjLog"+ oDELIM +
                    payRecord.getRealTransactionAmount().toString() + oDELIM +
                    payRecord.getServiceClass() + FacePromo.oDELIM +
                    payRecord.getExternalData1() + FacePromo.oDELIM + 
                    payRecord.getExternalData2() + FacePromo.oDELIM +
                    payRecord.getTimeStamp() + FacePromo.oDELIM + 
                    payRecord.getPromoTransactionAmount().toString() + FacePromo.oDELIM +
                    new java.util.Date().toString());
               
            return false;
        }
          
//        oraAIWA2.execStoredProc("D_insertAdjLog",payRecord,adjDBLogFail);
        //oraAIWA2.closeConnections();
        logger.debug(payRecord.getSubscriberNumber() + 
                " Stored Procedure called; UCIP failure succefully logged in DB");
        //return false; //Failures
        
       return true;
    }
    private boolean isEligible(String MSISDN, Map optIns){
        
        boolean isEligible = false;
        if(!optIns.containsKey(MSISDN)){
            System.out.println("MSISDN: "+MSISDN+" is not optted-In");
            isEligible = false;
        }
        else if(((GenericSubInfo)optIns.get(MSISDN)).getPromoCounter() >= 3 ){
            System.out.println("MSISDN: "+MSISDN+" has previously used the Promo "+"3"+" times.");
            isEligible = false;
        }
        else
            isEligible = true;
        
        System.out.println("MSISDN: "+MSISDN+" isEligible: "+isEligible);            
        return isEligible;
    }
    private boolean isEligible(Payment payRecord){

     
     
     /*   double realValue = payRecord.getRealTransactionAmount(); 
        int paySvcClass = payRecord.getServiceClass().intValue();
        
        //Service Calss eligibality
        if(! svcClses.contains(paySvcClass)){
            logger.debug(payRecord.getSubscriberNumber() + 
                    " eligibality failed due to service class " +
                    payRecord.getServiceClass());
            return false;
        }
        
        //Exclude Zero payments and less than 5 L.E
        if ( realValue == 0.0 || realValue < 5.0 ){
             logger.debug(payRecord.getSubscriberNumber() +
                     " eligibality failed due to adj value " +
                     payRecord.getRealTransactionAmount());
             return false; //not eligible
         }
       
        //call stored procedure which both checks eligibility
        //and marks as "already taken the promo"
        /*if (! oraAIWA2.getConnection()){
            logger.error(payRecord.getSubscriberNumber()+ 
                    " Recharge_Status Conn to AIWA2 failed !, eligibality failed !",null);
            
            chkStatusFail.println(payRecord.getSubscriberNumber() + oDELIM +
                    "Failed to get conn 4 Recharge_Status"+ oDELIM +
                    payRecord.getRealTransactionAmount().toString() + oDELIM +
                    payRecord.getServiceClass() + FacePromo.oDELIM +
                    payRecord.getExternalData1() + FacePromo.oDELIM + 
                    payRecord.getExternalData2() + FacePromo.oDELIM +
                    payRecord.getTimeStamp() + FacePromo.oDELIM + 
                    payRecord.getPromoTransactionAmount().toString() + FacePromo.oDELIM +
                    new java.util.Date().toString());
            return false;
        }*/
          
        //String status = oraAIWA2.execStoredProc("D_insertAdjLog",payRecord);
   /*     String status = oraAIWA2.execStoredProc("Recharge_Status",payRecord,chkStatusFail);
        //oraAIWA2.closeConnections();
        
        if (status == null){
            logger.error(payRecord.getSubscriberNumber() + " Call to Stored Proc Failed ", null);
            return false;
            //logging handled in call to Store Proc
        }
        
        if (status.equalsIgnoreCase("0")){
            //code zero symbolizes eligability for promo
            logger.debug(payRecord.getSubscriberNumber() +
                    " Srtored proc succeded; Sub eligible for promo !");
            return true;
        }
        else
        logger.debug(payRecord.getSubscriberNumber() + 
                " Srtored proc succeded; Sub NOT eligible for promo !");
        return false;
        */
        return true;
    }
    
  
    public void givePromo()
    {      
        if ( flag != true ){
         logger.error("Error occured during Inialization......quitting......", null);   
         return;   
        }
        
        Payment payRecord = this.parseLine();
        
        if (payRecord == null){
            logger.warning("No Records in File " +  ifile.getName());
            //this.terminate();
            return;
        }
        
        //while (payRecord != null){
        do{
            //payRecord = this.parseLine();
             System.out.println("checking Eligiblility..........");
            if ( ! isEligible(payRecord)) {
                payRecord = this.parseLine();
                logger.debug("Skipping rest of loop..");
                System.out.println("Skipping rest of loop..");
                continue;
            }
            System.out.println("applying adjustments.............");     
            boolean result = adjust(payRecord);
            if (result == true){
                //Success
                logger.debug(payRecord.getSubscriberNumber()+" logging Adjustment Succeded..");
                 pwSuccess.println(payRecord.getSubscriberNumber() + oDELIM +
                    payRecord.getRealTransactionAmount().toString() + oDELIM +
                    payRecord.getServiceClass() + oDELIM +
                    payRecord.getExternalData1() + FacePromo.oDELIM + 
                    payRecord.getExternalData2() + FacePromo.oDELIM +
                    payRecord.getTimeStamp() + oDELIM + 
                    payRecord.getPromoTransactionAmount().toString() + oDELIM +
                    new Date().toString());
                
                System.out.println(payRecord.getSubscriberNumber() + oDELIM +
                    payRecord.getRealTransactionAmount().toString() + oDELIM +
                    payRecord.getServiceClass() + oDELIM +
                    payRecord.getExternalData1() + FacePromo.oDELIM + 
                    payRecord.getExternalData2() + FacePromo.oDELIM +
                    payRecord.getTimeStamp() + oDELIM + 
                    payRecord.getPromoTransactionAmount().toString() + oDELIM +
                    new Date().toString());
                System.out.println("parsing line.............");        
                System.out.println("SubscriberNumber: "+payRecord.getSubscriberNumber());
                    System.out.println("RealTransactionAmount: "+payRecord.getRealTransactionAmount().toString());
                    System.out.println("ServiceClass: "+payRecord.getServiceClass());
                    System.out.println("ExternalData1: "+payRecord.getExternalData1());
                    System.out.println("ExternalData1: "+payRecord.getExternalData2());
                    System.out.println("TimeStamp: "+payRecord.getTimeStamp());
                    System.out.println("TransactionAmount: "+ payRecord.getPromoTransactionAmount().toString());
                    System.out.println(new Date().toString());
                    
            }
            else   {     
                //Failure
                logger.debug(payRecord.getSubscriberNumber()+" logging Adjustment Failed..");
                pwFail.println(payRecord.getSubscriberNumber() + oDELIM +
                    payRecord.getRealTransactionAmount().toString() + oDELIM +
                    payRecord.getServiceClass() + oDELIM +
                    payRecord.getExternalData1() + FacePromo.oDELIM + 
                    payRecord.getExternalData2() + FacePromo.oDELIM +
                    payRecord.getTimeStamp() + oDELIM + 
                    payRecord.getPromoTransactionAmount().toString() + oDELIM +
                    new Date().toString());
            }
            payRecord = this.parseLine();
        }while (payRecord != null);
        
        logger.debug("Reached End of File..");
    }
    
     private void terminate(){
        
        try{
           
            logger.debug("Closing handles....");
            
            if(br != null )
                br.close();
            if(pwSuccess != null ){
                //pwSuccess.flush();
                pwSuccess.close();
            }
            if(pwFail != null ){
                //pwFail.flush();
                pwFail .close();
            }
           
           //For the next 4 functions, exceptions are handled internally
           oraAIWA2.closeSatements();
           oraAIWA2.closeConnections();
           
           oraSMPP.closeSatements();
           oraSMPP.closeConnections();
            
           if( PoolManager.releseAll()){
               logger.debug("Pool Manager Succefully leased.");
           }else
               logger.debug("Pool Manager already leased or was null.");
            
        }catch (Exception Ex){
            //Ex.printStackTrace();
            logger.error("Exeption when closing Handles..", Ex);
        }
    }
    public static void facePromoHandler(){
        
        String arg0 = new String();
        String arg1 = new String();
        String arg2 = new String();
        arg0 = "resources/infile.txt";
        arg1 = "D:\\myWork\\InternationalPromo\\InterPromo\\resources";
        arg2 = "ddmmyyyy";
        if(arg0 == null)//if(args.length != 1)
        {
            System.err.println("Missing Parameter.");
            System.out.println("Usage: java -jar FacePromo.jar xx yy zz ");
            System.out.println("where 'xx' is the Input Payment File.");
            System.out.println("where 'yy' is the Success Log File.");
            System.out.println("where 'zz' is the Failure File.");
            System.exit(1);
        }
        //FacePromo facepromo = new FacePromo(args[0],args[1],args[2]);
        FacePromo facepromo = new FacePromo(arg0,arg1,arg2);
        System.out.println("Parameters completed");
        
        facepromo.givePromo();
        facepromo.terminate();
        
        
        /*ArrayList arr = new ArrayList();
        arr.add(533);
        arr.add(561);
        arr.add(5);
        if (arr.contains(15))
            System.out.println("yohoooooooo");
        */
        //FacePromo.makeList();
        // boolean flag = fafmaker.upload("10.230.91.190", "moads", "moads");
        //if(!flag)
          //  System.err.println("Ftp process failed !!");
    
    } 
     
    public static void main(String args[])
    {
        String arg0 = new String();
        String arg1 = new String();
        String arg2 = new String();
        arg0 = "resources/infile.txt";
        arg1 = "D:\\myWork\\InternationalPromo\\InterPromo\\resources";
        arg2 = "ddmmyyyy";
        if(arg0 == null)//if(args.length != 1)
        {
            System.err.println("Missing Parameter.");
            System.out.println("Usage: java -jar FacePromo.jar xx yy zz ");
            System.out.println("where 'xx' is the Input Payment File.");
            System.out.println("where 'yy' is the Success Log File.");
            System.out.println("where 'zz' is the Failure File.");
            System.exit(1);
        }
        //FacePromo facepromo = new FacePromo(args[0],args[1],args[2]);
        FacePromo facepromo = new FacePromo(arg0,arg1,arg2);
        System.out.println("Parameters completed");
        
        facepromo.givePromo();
        facepromo.terminate();
        
        
        /*ArrayList arr = new ArrayList();
        arr.add(533);
        arr.add(561);
        arr.add(5);
        if (arr.contains(15))
            System.out.println("yohoooooooo");
        */
        //FacePromo.makeList();
       // boolean flag = fafmaker.upload("10.230.91.190", "moads", "moads");
        //if(!flag)
          //  System.err.println("Ftp process failed !!");
    }
    
    
}