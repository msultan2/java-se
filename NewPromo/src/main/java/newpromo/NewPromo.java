package newpromo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import oracle.util.OracleDao;

import oracle.util.PoolManager;

import ucip.UcipHandler;

public class NewPromo {

    private ArrayList allMSISDN = new ArrayList();
    
    private static final  String AIWADRD1_DB = "AIWA2";//PromoParameters.PROMO_DB;
    
    private OracleDao oraAIWA2;
    private UcipHandler ucipAdjuster;
    
    private static String baseDate;
    
    public NewPromo() {
    }
    public static String getBaseDate() {
        return NewPromo.baseDate;
    }
    public boolean initPromoEngine(){

        //logger.debug("Engine Initialization started..... :)");
        System.out.println("Engine Initialization started..... :)");

       //Validate promo Parameters
       //Should be THE FIRST STEP
       /*if (! ppms.validateParemeters()){
           logger.error("Error While Initializing Promo Parameters...", null);
           criticalErrsLog.println("Error While Initializing Promo Parameters.");
           System.out.println("Error While Initializing Promo Parameters.");
           return false;
       }*/
/*
        try
        {  
            br = new BufferedReader(new FileReader(this.ifile));
            promoAppLog = new PrintWriter(new BufferedWriter(new FileWriter(
                    PromoParameters.LOG_BASE_DIR+"/subscriberLog-"+baseDate+".txt")) , true);
            smsFailLog = new PrintWriter(new BufferedWriter(new FileWriter(
                    PromoParameters.LOG_BASE_DIR+"/smsFailLog-"+baseDate+".txt")) , true);
            dbReadLog = new PrintWriter(new BufferedWriter(new FileWriter(
                    PromoParameters.LOG_BASE_DIR+"/dbReadLog-"+baseDate+".txt")) , true);
            dbUpdateLog = new PrintWriter(new BufferedWriter(new FileWriter(
                    PromoParameters.LOG_BASE_DIR+"/dbUpdateLog-"+baseDate+".txt")) , true);
            criticalErrsLog = new PrintWriter(new BufferedWriter(new FileWriter(
                    PromoParameters.LOG_BASE_DIR+"/criticalErrsLog-"+baseDate+".txt")) , true);
        }
        catch(Exception ex){  
            logger.error("Error in creating Suc/Fail log files !!", ex);
            criticalErrsLog.println("Error in cerating Suc/Fail log files !!");
            return false;
       }
*/
        //Initialise objects
       ucipAdjuster = new UcipHandler();
       if( !ucipAdjuster.initChargingModel()){
//           logger.error("Error in initializing charging model...", null);
//           criticalErrsLog.println("Error in initializing charging model.");
            System.out.println("Error in initializing charging model...");
           return false;
       }

        //Initialise objects
       oraAIWA2 = new OracleDao(AIWADRD1_DB);
       if (! oraAIWA2.getConnection()) {
           //logger.error("Error in Creating Connection to AIWA2...", null);
           //criticalErrsLog.println("Error in Creating Connection to AIWA2.");
           System.out.println("Error in Creating Connection to AIWA2.");
           return false;
       }
      
        ArrayList MSISDNs = new ArrayList();
        MSISDNs = getAllMSISDN();
        if(MSISDNs == null || MSISDNs.size() == 0){
            //logger.error("No OptIns found in Database table...", null);
            //criticalErrsLog.println("No OptIns found in Database table...");
            System.out.println("No MSISDNs found in Database table...");
            return false;
        }else
            allMSISDN.addAll(MSISDNs);
       //logger.debug("Engine Initialized successfully :)");
       System.out.println("Engine Initialized successfully :)");
       return true;
    }

    public ArrayList getAllMSISDN(){
        
        System.out.println("getting all MSISDNs........."); 
        //logger.debug("getting all optIns........."); 
        
        ArrayList MSISDNs = new ArrayList();
        try{
            String query = "SELECT MSISDN FROM Test_MSISDN";
            
            System.out.println("query: "+query); 
            //dbReadLog.println("query: "+query); 
            
            ResultSet rs = oraAIWA2.execStatmnt(query);
            while(rs.next()){
                String msisdn = rs.getString("MSISDN");  
                MSISDNs.add(msisdn);
            }
            //dbReadLog.println(rs.getFetchSize()/2+" records found.");
            
        }catch(SQLException e){
            //dbReadLog.println("Error While Reading MSISDNs...");
            //logger.error("Error While Reading MSISDN...", null);
            //criticalErrsLog.println("Error While Reading MSISDNs...");
            System.out.println("Error While Reading MSISDNs...");
        }
        return MSISDNs;
    }
    public void givePromo()
    {      
      /*
        if ( flag != true ){
         logger.error("Error occured during Inialization......quitting......", null);   
         return;   
        }
        
        PaymentCharge payRecord = this.parseLine();
            
        if (payRecord == null){
            logger.warning("No Records in File " +  ifile.getName());
            this.terminate();
            return;
        }
        
        do{
            logger.debug("Parsing new line......");
            logger.debug(payRecord.getSubscriberNumber() + oDELIM +
                payRecord.getRealTransactionAmount().toString() + oDELIM +
                payRecord.getServiceClassCurrent() + oDELIM +
                payRecord.getTimeStamp() + oDELIM + 
                new Date().toString());
            System.out.println(payRecord.getSubscriberNumber() + oDELIM +
                payRecord.getRealTransactionAmount().toString() + oDELIM +
                payRecord.getServiceClassCurrent() + oDELIM +
                payRecord.getTimeStamp() + oDELIM + 
                new Date().toString());
                
            System.out.println("parsing line.............");        
            System.out.println("SubscriberNumber: "+payRecord.getSubscriberNumber());
            System.out.println("RealTransactionAmount: "+payRecord.getRealTransactionAmount().toString());
            System.out.println("ServiceClass: "+payRecord.getServiceClassCurrent());
            System.out.println("TimeStamp: "+payRecord.getTimeStamp());
            System.out.println(new Date().toString());
                
             System.out.println("checking Eligiblility..........");
             String msisdn = payRecord.getSubscriberNumber();
             GenericSubInfo sub = new GenericSubInfo();
             sub.setSubscriberNumber(msisdn);

             //Check Eligibility
             switch(isEligible(msisdn)){
                case 3:
                    logger.debug(sub.getSubscriberNumber() + " Already enjoyed promo Max Times ,so Not eligible for promo !");
                    promoAppLog.println("FAIL: " + sub.getSubscriberNumber() + " Already enjoyed promo Max Times ,so Not eligible for promo !");
                    sub.setSmsReason(PromoParameters.ALREADY_SUBSCRIBED);
                    sub.setError(false);
                    sub.setResult(PromoParameters.ALREADY_SUBSCRIBED_MAX_TIMES);
                    //this.promoFailure(sub);

                     payRecord = this.parseLine();
                    continue;
                    
                case 2:
                    logger.debug(sub.getSubscriberNumber() + " Not opted-In ,so Not eligible for promo !");
                    promoAppLog.println("FAIL: " + sub.getSubscriberNumber() + " Not opted-In ,so Not eligible for promo !");
                    //this.noCredit(sub);
                    sub.setSmsReason(PromoParameters.NOT_OPTED_IN);
                    sub.setError(false);
                    sub.setResult(PromoParameters.NOT_OPTED_IN);
                    //this.promoFailure(sub);

                     payRecord = this.parseLine();
                    continue;
                    
                case 1:
                    logger.debug(sub.getSubscriberNumber() + " eligible for promo !");
                    DA_ID = PromoParameters.promo_DA_ID(payRecord.getServiceClassCurrent());
                    payRecord.setDedAccountBefore(getDedicatedAmountBefore(sub.getSubscriberNumberShortFormat() ,DA_ID));
                    if(adjust(payRecord) == 0){
                        promoAppLog.println("SUCCESS: " + sub.getSubscriberNumber() + " Got the promo !!");
                        sub.setSmsReason(PromoParameters.SUCCESS);
                        sub.setError(false);
                        sub.setResult(PromoParameters.SUCCESS);
                        sub.setOptInText(String.valueOf(payRecord.getPromoTransactionAmount()));
                        //internally updates sub Promo Counter  as well.
                        //internally sends SMSNotification as well
                        this.promoSuccess(sub);
                        
                        payRecord = this.parseLine();
                        continue;
                    }
                    
                    
            //Failed Adjustmensts
                promoAppLog.println("FAIL: " + sub.getSubscriberNumber() + " OTHER ERROR Occured, please check promo.log for details !");
                sub.setSmsReason(PromoParameters.OTHER_FAILURE);
                sub.setError(true);
                sub.setResult(PromoParameters.OTHER_FAILURE);
                //this.promoSuccess(sub);
             }
            
            payRecord = this.parseLine();
        }while (payRecord != null);
    */
        //logger.debug("Reached End of File..");
        System.out.println("Reached End of File..");
    }
    private void terminate(){
         try{
          
           //logger.debug("Closing handles....");
            System.out.println("Closing handles....");
             /*      
           if(br != null )
               br.close();
           
           if(promoAppLog != null ){
               promoAppLog.close();
           }
           if(smsFailLog != null ){
               smsFailLog.close();
           }
           if(dbReadLog != null ){
               dbReadLog.close();
           }
           if(dbUpdateLog != null ){
               dbUpdateLog.close();
           }
           if(criticalErrsLog != null ){
               criticalErrsLog.close();
           }
        */
          
          //For the next functions, exceptions are handled internally
          oraAIWA2.closeDao();

          //UCIP LOg cleanUp
          ucipAdjuster.closeHandles();
          
          if( PoolManager.releseAll()){
              //logger.debug("Pool Manager Succefully leased.");
              System.out.println("Pool Manager Succefully released.");
          }else{
              //logger.debug("Pool Manager already leased or was null.");
              System.out.println("Pool Manager already leased or was null.");
          }
       }catch (Exception Ex){
           //Ex.printStackTrace();
           //logger.error("Exception when closing Handles..", Ex);
           System.out.println("Exception when closing Handles..");
       }
    }
    
    public static void main(String[] args) {
        
        NewPromo newPromo = new NewPromo();
        
        if (!newPromo.initPromoEngine()){
          //logger.error("Error occured during Inialization......", null);
          System.out.println("Error occured during Inialization......");
          //logger.error("Aborting......", null);
          System.out.println("Aborting......");
          System.exit(0);
        }
        newPromo.givePromo();
        newPromo.terminate();
    }
}
