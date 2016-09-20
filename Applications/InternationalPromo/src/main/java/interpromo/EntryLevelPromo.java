

package interpromo;

import drs.GenericSubInfo;
import promo.util.PromoParameters;
import drs.Payment;
import oracle.util.PoolManager;
import oracle.util.OracleDao;
import java.io.*;
//import java.sql.CallableStatement;
import java.util.ArrayList;
//import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;
import ucip.UcipHandler;
//import java.util.logging.Logger;
import promo.util.PromoLogger;
import ucip.BasicUCIPResponse;
import charging.info.DedicatedAccountEnquiryInformation;
import ucip.UcipResponseCode;

public class EntryLevelPromo
{

    private OracleDao oraAIWA2;
    private OracleDao oraSMPP;
    private OracleDao oraBase_pROMO2;
    private UcipHandler ucipAdjuster; 
   //private File ifile;
   
    //private static String  logBaseDir;
    private static String baseDate;
    private ArrayList svcClses;
    private ArrayList easy_SvcCls;
    private ArrayList bbq_SvcCls;
    private ArrayList voucherGrps;
    //private ChargingModelsMap daMap;
    private Map<String,GenericSubInfo> subMap;
    // map = new HashMap<String,String>();

    
    private BufferedReader br;
    private PrintWriter promoAppLog;          //Promo Application Log -- OK
    
    private PrintWriter smsFailLog;           //Logging SMS Notification failures -- OK
    private PrintWriter refundLog;            //Log refunds SUCCESS,FAIL -- to be used only in refundSMS() -- OK
  
    private PrintWriter invalidOptInLOg;      //Log InvalidOptIn Messages -- OK

    private PrintWriter dbReadFailLog;        //Log DB failures when checking/getting subs -- OK
    private PrintWriter dbUpdateFailLog;      //Log DB failures when updating failed subs -- OK
    private PrintWriter criticalErrsLog;      //Log Critical Errors
    private PrintWriter easyErrLog;           //Log VF-Easy Errors -- to be used only in adjustEsay() -- OK
    
    //private PrintWriter promoSuccess;       //Logging UCIP Success
    //private PrintWriter refundFailLog;      //Log Refunds that failed
    //private boolean flag = true;           

    private static PromoLogger logger;
    private static PromoParameters ppms;
     
     static {

         logger = new PromoLogger(EntryLevelPromo.class);
         ppms = new PromoParameters();

     }

    public EntryLevelPromo(String baseDate)
    {
       EntryLevelPromo.baseDate = baseDate;
    }

    public boolean initPromoEngine(){

        logger.debug("Engine Initialized started..... :)");

       //Validate promo Parameters
       //Should be THE FIRST STEP
       if (! ppms.validateParemeters()){
           logger.error("Error While Initializing Promo Parameters...", null);
           //criticalErrsLog.println("Error While Initializing Promo Parameters.");
           System.out.println("Error While Initializing Promo Parameters.");
           return false;
       }

       if(!this.initSvcClasses()){ //)|| !this.initBBqSvcClasses()) {
           logger.error("Error While Initializing Service Calsses...", null);
           //criticalErrsLog.println("Error While Initializing Service Calsses.");
           System.out.println("Error While Initializing Service Calsses.");
           return false;
       }
/*
       if(!this.initEasySvcClasses()) {
           logger.error("Error While Initializing Easy Service Classes...", null);
           //criticalErrsLog.println("Error While Initializing Easy Service Classes.");
           System.out.println("Error While Initializing Easy Service Classes.");
           return false;
       }
*/           
        try
        {  
            //ucipSuccessLog = new PrintWriter(new BufferedWriter(new FileWriter(
            //        this.logBaseDir+"/UCIPSuccess-"+baseDate+".txt")) , true);
            //ucipFailLog = new PrintWriter(new BufferedWriter(new FileWriter(
              //      this.logBaseDir+"/UCIPFail-"+baseDate+".txt")) , true);
            promoAppLog = new PrintWriter(new BufferedWriter(new FileWriter(
                    PromoParameters.LOG_BASE_DIR+"/subscriberLog-"+baseDate+".txt")) , true);
            smsFailLog = new PrintWriter(new BufferedWriter(new FileWriter(
                    PromoParameters.LOG_BASE_DIR+"/smsFail-"+baseDate+".txt")) , true);
            refundLog  = new PrintWriter(new BufferedWriter(new FileWriter(
                    PromoParameters.LOG_BASE_DIR+"/refundLog-"+baseDate+".txt")) , true);
           //refundFailLog  = new PrintWriter(new BufferedWriter(new FileWriter(
             //       PromoParameters.LOG_BASE_DIR+"/refundFail-"+baseDate+".txt")) , true);
            dbReadFailLog = new PrintWriter(new BufferedWriter(new FileWriter(
                    PromoParameters.LOG_BASE_DIR+"/chkProcFail-"+baseDate+".txt")) , true);
            dbUpdateFailLog = new PrintWriter(new BufferedWriter(new FileWriter(
                    PromoParameters.LOG_BASE_DIR+"/dblogProcFail-"+baseDate+".txt")) , true);
            criticalErrsLog = new PrintWriter(new BufferedWriter(new FileWriter(
                    PromoParameters.LOG_BASE_DIR+"/Critical-"+baseDate+".txt")) , true);
            invalidOptInLOg = new PrintWriter(new BufferedWriter(new FileWriter(
                    PromoParameters.LOG_BASE_DIR+"/InvalidOptIns-"+baseDate+".txt")) , true);
            easyErrLog = new PrintWriter(new BufferedWriter(new FileWriter(
                    PromoParameters.LOG_BASE_DIR+"/EasyErrs-"+baseDate+".txt")) , true);

        }
        catch(Exception ex){  
            logger.error("Error in cerating Suc/Fail log files !!", ex);
            criticalErrsLog.println("Error in cerating Suc/Fail log files !!");
            return false;
       }

        //Initialise objects
       ucipAdjuster = new UcipHandler();
       if( !ucipAdjuster.initChargingModel()){
           logger.error("Error in initializing charging model...", null);
           criticalErrsLog.println("Error in initializing charging model.");
           return false;
       }
/*
       oraAIWA2 = new OracleDao(PromoParameters.PROMO_DB);
       if (! oraAIWA2.getConnection()) {
           logger.error("Error in Creating Connection to AIWA2...", null);
           criticalErrsLog.println("Error in Creating Connection to AIWA2.");
           return false;
       }
       
       oraSMPP = new OracleDao(PromoParameters.SMPP_DB);
       if (! oraSMPP.getConnection()){
           logger.error("Error in Creating Connection to SMPP...", null);
           criticalErrsLog.println("Error in Creating Connection to SMPP.");
           return false;
       }

       oraBase_pROMO2 = new OracleDao(PromoParameters.Base_pROMO2_DB);
       if (! oraBase_pROMO2.getConnection()){
           logger.error("Error in Creating Connection to Base_pROMO2_DB...", null);
           criticalErrsLog.println("Error in Creating Connection to Base_pROMO2_DB.");
           return false;
       }
*/       
       
       subMap = new HashMap<String,GenericSubInfo>();
       logger.debug("Engine Initialized successfully :)");
       return true;
    }

    public static String getBaseDate(){
        return EntryLevelPromo.baseDate;
    }

     private boolean initSvcClasses(){

      BufferedReader clsBr = null;

      File svcClassesFile = new File(PromoParameters.svcClasses);
      if (!svcClassesFile.exists()) {
            logger.error("Can not find Service Class file !! !!", null);
            return false;
      }
      try {
            clsBr = new BufferedReader(new FileReader(svcClassesFile));
     } catch (FileNotFoundException ex) {
            logger.error("Init Service Classes failed !!", ex);
            return false;
     }

     String line;
     svcClses = new ArrayList();

     try {
         line = clsBr.readLine();
         while (line != null){

                  svcClses.add(Integer.parseInt(line.trim()));
                  line = clsBr.readLine();
           }

     } catch (IOException ex) {
            logger.error("smthg wrong with loading svc classes list from file !!",ex);
            return false;
     }
     return true;
    }


    private boolean initEasySvcClasses(){

      BufferedReader clsBr = null;

      File easyFile = new File(PromoParameters.EasySvcClasses);
      if (!easyFile.exists()) {
          logger.error("Can not find Easy Service Class file !! !!", null);
          return false;
      }
      try {
            clsBr = new BufferedReader(new FileReader(easyFile));
     } catch (FileNotFoundException ex) {
            logger.error("Init Easy Service Classes failed !!", ex);
            return false;
     }

     String line;
     easy_SvcCls = new ArrayList();

     try {
         line = clsBr.readLine();
         while (line != null){

                  easy_SvcCls.add(Integer.parseInt(line.trim()));
                  line = clsBr.readLine();
           }

     } catch (IOException ex) {
            logger.error("smthg wrong with loading Easy svc classes list from file!!",ex);
            return false;
     }
     return true;
    }

     private boolean initBBqSvcClasses(){

      BufferedReader clsBr = null;

      File bbqFile = new File(PromoParameters.BBqSvcClasses);
      if (!bbqFile.exists()) {
          logger.error("Can not find BBQ Service Class file !! !!", null);
          return false;
      }
      try {
            clsBr = new BufferedReader(new FileReader(bbqFile));
     } catch (FileNotFoundException ex) {
            logger.error("Init BBQ Service Classes failed !!", ex);
            return false;
     }

     String line;
     bbq_SvcCls = new ArrayList();

     try {
         line = clsBr.readLine();
         while (line != null){

                  bbq_SvcCls.add(Integer.parseInt(line.trim()));
                  line = clsBr.readLine();
           }

     } catch (IOException ex) {
            logger.error("smthg wrong with loading BBQ svc classes list from file!!",ex);
            return false;
     }
     return true;
    }
   
    /*private Payment parseLine()
    {
        try {
            String line = br.readLine();
            if (line != null ) {
                if (line.trim().equals("")) return new Payment();
                else return new Payment(line.split(PromoParameters.iDELIM));
            } else {
                return null;
            }
        } catch (IOException ex) {
            //Logger.getLogger(FacePromo.class.getName()).log(Level.SEVERE, null, ex);
            logger.error("Failed to read line from file..",ex);
            return null;
        }
    }*/

    private boolean adjust(Payment payRecord){

       //This is to adjust Bonus SMSses

       Double adjValue;
       boolean status;
       
       //Fixed adjustment value, if variable, calculation goes here as well
       if(PromoParameters.PROMO_ADJSUTED_VALUE_TYPE.equalsIgnoreCase(PromoParameters.PROMO_VALUE_ABSOLUTE)){
            adjValue = new Double( PromoParameters.PROMO_ADJSUTED_VALUE  );
            logger.debug(payRecord.getSubscriberNumber()+" Normal absolute adjustment "+ adjValue.toString()
                     + " pt.");
       }else
       if(PromoParameters.PROMO_ADJSUTED_VALUE_TYPE.equalsIgnoreCase(PromoParameters.PROMO_VALUE_PERCENTAGE)){
            adjValue = new Double( 100 * PromoParameters.PROMO_ADJSUTED_VALUE * payRecord.getRealTransactionAmount());
            logger.debug(payRecord.getSubscriberNumber()+" Normal Percentage adjustment "+ adjValue.toString()
                    + " pt.");
       }
       else{
           //Should NOT Happen, checked in PromoParameters.validateParemeters()
                logger.error("UnExpexted Error, Unknown PROMO_VALUE_TYPE " +
                        PromoParameters.PROMO_ADJSUTED_VALUE_TYPE, null);
                return false;
       }


       payRecord.setPromoTransactionAmount(adjValue.doubleValue());

       //if (PromoParameters.Easy_SvcCls.contains(payRecord.getServiceClass())){
            
           Integer svsClass = payRecord.getServiceClass();
           String daID = "";
                   //((ArrayList)daMap.get(svsClass)).get(ChargingModelsMap.DA_INDEX).toString();

           Integer validity;
           try{
                validity = 0;
                        //Integer.valueOf((String)((ArrayList)daMap.get(svsClass)).get(ChargingModelsMap.VALIDITY_INDEX));
           }catch(IndexOutOfBoundsException  ex){
                validity = new Integer(-1);
           }


           logger.debug("UCIP Adjusting Svc-Class "+ svsClass.toString() + " "
                    + new Double(payRecord.getPromoTransactionAmount()).toString()+ " DA: "
                    +  daID + " to: "+ payRecord.getSubscriberNumber()
                    + " with No Expiry after " + validity.toString() );

           status = ucipAdjuster.adjustDedAccountFD1(
                    new Double(payRecord.getPromoTransactionAmount()).toString(),
                    daID,payRecord.getSubscriberNumber() ,"",
                   "",null, validity.intValue());
       /*}
       else {

            //Default Adjustment statmnets goes here
           logger.debug("UCIP Adjusting Default "+ new Double(payRecord.getPromoTransactionAmount()).toString()+ " DA: "
                    + PromoParameters.Def_SMS_DA_ID.toString() + " to: "+ payRecord.getSubscriberNumber()
                    + " " + PromoParameters.ADJ_CODE.toString()
                    + " " + PromoParameters.ADJ_TYPE.toString()
                    + " with 1 Month Validity.");

           status = ucipAdjuster.adjustDedAccountFD1(
                   new Double(payRecord.getPromoTransactionAmount()).toString(),
                    PromoParameters.Def_SMS_DA_ID.toString() ,payRecord.getSubscriberNumber() , PromoParameters.ADJ_CODE.toString(),
                    PromoParameters.ADJ_TYPE.toString(),true);
       }*/
        
        //Succesfull
        if(status == true){
            logger.debug(payRecord.getSubscriberNumber()+" UCIP succeded with staus true :)");
            payRecord.setSmsText(PromoParameters.SMS_SUCCESS_AR.replace("VAL", adjValue.toString()));
            oraSMPP.execStoredProc("SENDSMS", payRecord);
            logger.debug(payRecord.getSubscriberNumber()+" SMS Sent !");

            return true; //succssefull SP
        }

        //Failures
        logger.debug(payRecord.getSubscriberNumber() + " UCIP failed with staus fail :(");
        oraAIWA2.execStoredProc("D_insertAdjLog",payRecord);
        logger.debug(payRecord.getSubscriberNumber() + 
                " Stored Procedure called; UCIP failure succefully logged in DB");
        return false;
    }

    private void assignPromoPackage(GenericSubInfo sub){

        logger.debug("Choosing Promo package for " + sub.getSubscriberNumber()
                + " with OptIn " + sub.getOptInText());

        if(easy_SvcCls.contains(sub.getServiceClass())){
            if(sub.getOptInText().equalsIgnoreCase(PromoParameters.pkg1Option.toString()))
                sub.setChargingID(PromoParameters.pkg1Option + 1);
             else if(sub.getOptInText().equalsIgnoreCase(PromoParameters.pkg2Option.toString()))
                sub.setChargingID(PromoParameters.pkg2Option + 1);

        }
        else if(bbq_SvcCls.contains(sub.getServiceClass())){
        //BBQ Service Classes
            if(sub.getOptInText().equalsIgnoreCase(PromoParameters.pkg1Option.toString()))
                sub.setChargingID(PromoParameters.pkg1Option * 10);
             else if(sub.getOptInText().equalsIgnoreCase(PromoParameters.pkg2Option.toString()))
                sub.setChargingID(PromoParameters.pkg2Option * 10);

        }
        else //Other Service Calsses
        {
             if(sub.getOptInText().equalsIgnoreCase(PromoParameters.pkg1Option.toString()))
                sub.setChargingID(PromoParameters.pkg1Option);
             else if(sub.getOptInText().equalsIgnoreCase(PromoParameters.pkg2Option.toString()))
                sub.setChargingID(PromoParameters.pkg2Option);
        }
        logger.debug(sub.getSubscriberNumber() + " has been assigned package "+ sub.getChargingID());
    }


    private void assignSMSREFPackage(GenericSubInfo sub){

        logger.debug("Choosing Refund package for " + sub.getSubscriberNumber());

        if(easy_SvcCls.contains(sub.getServiceClass())){
               sub.setChargingID(PromoParameters.refPKG + 1);
        }
        else //Other Service Calsses
        {
             sub.setChargingID(PromoParameters.refPKG);
        }

        logger.debug(sub.getSubscriberNumber() + " has been assigned package "+ sub.getChargingID());
    }
    

    private int adjust(GenericSubInfo sub){

       //This is to adjust
       int status;

       status = ucipAdjuster.adjustDedAccountFD1(sub);  //, new DaMiniMap());

        //Succesfull
        if(status == UcipResponseCode.SUCCESSFUL){
            logger.debug(sub.getSubscriberNumber()+" UCIP succeded with staus " + status);
            //ucipSuccessLog.println(sub.getSubscriberNumber()+" UCIP succeded with staus " + status);
            return 0; //succssefull SP
        }

        //Failures
        logger.debug(sub.getSubscriberNumber() + " UCIP returned with staus " + status);

        //Not Enough Balance; Should not happen as already checked with isEligible()
         if(status == UcipResponseCode.BELOWMINIMUMBALANCE){
             //this.noCredit(sub);
             //return true;
             logger.error("Elgible Subscriber with No Balance cetected....should not happen !!", null);
             return 1;
         }

        //Not On CS, assume POST; Should not happen as already checked with isEligible()
        if(status == UcipResponseCode.SUBSCRIBERNOTFOUND){
            //logger.debug(sub.getSubscriberNumber() + " Not Found on Charging System..");
            //sub.setError(false);
            //sub.setResult(PromoParameters.SVC_CALSS_ELIGIBILITY);
            logger.error("Elgible Subscriber Not on Prepaid sytem detected....should not happen !!", null);
            return 2;
        }

        //UCIP 100
        if(status == UcipResponseCode.OTHERERROR.intValue()){
            int res = this.checkPromo(sub);
            if(res == 0){
                sub.setError(false);
                sub.setResult(PromoParameters.SUCCESS);
                logger.debug(sub.getSubscriberNumber() + " balance check passed, status 100 succeded to get the promo :)");
                //ucipSuccessLog.println(sub.getSubscriberNumber()+" balance check passed, UCIP succeded with staus " + status);
                return 0;
            }else if (res == 1){
                logger.debug(sub.getSubscriberNumber() + " balance check failed, status 100 Failed to get the promo...");
                sub.setResult(PromoParameters.PROMO_UCIP100_FAILURE);
                //ucipFailLog.println(sub.getSubscriberNumber() + " balance check failed, status 100 Failed to get the promo...");
                //continue to below as faliure
            }else if (res == 2){
                logger.debug(sub.getSubscriberNumber() + " failed to do balance inquiry, state UNKNOWN:(");
                sub.setResult(PromoParameters.UNKNOWN);
                //ucipFailLog.println(sub.getSubscriberNumber()+"  failed to do balance inquiry, promo status UNKNOWN !");
                //continue to below as failure
            }       
        }
        else
        {//Other Failures
            //Adj Failed for sure
            sub.setResult(PromoParameters.PROMO_ADJ_FAILURE);
            logger.debug(sub.getSubscriberNumber() + " Promo Adjustment failed !");
            //ucipFailLog.println(sub.getSubscriberNumber() + " Promo Adjustment failed!");
        }

        //ALL Failures
         logger.debug(sub.getSubscriberNumber() + " UCIP fail confirmation with staus " + status);
         sub.setError(true);
         return 3;
 
    }

    private int checkPromo(GenericSubInfo sub) {
        //throw new UnsupportedOperationException("Not yet implemented");
        BasicUCIPResponse bresp =
                    ucipAdjuster.BalanceEnquiryTRequest(sub.getSubscriberNumberShortFormat());
        if(bresp != null){
            if (bresp.getResponseCode() == 0){
                //fill sub account info from inquiry just like above
                    //Store Main Balance
                    sub.setAccntAfter(new Integer(0),new Double(bresp.getAccountValue1()));
                    for (DedicatedAccountEnquiryInformation da : bresp.getDedicatedAccountInformation()){
                        //if(da.getDedicatedAccountID() == PromoParameters.TARGET_ACCNT_ID);
                            sub.setAccntAfter(da.getDedicatedAccountID(),new Double(da.getDedicatedAccountValue1()));
                    }
                    logger.debug(sub.getSubscriberNumber()+" AFTER PROMO SvcClass " + Integer.toString(sub.getServiceClass())
                            +" with Main balance "+ sub.getAccntBefore(new Integer(0)));

                    sub.setError(false);
                    sub.setResult(PromoParameters.SUCCESS);
                }else{
                    logger.debug("Failed to enquire about error 100 after Promo :(");
                    return 2;
                }
            
            boolean result = sub.checkPromo(ucipAdjuster.getChargingModel());
            if (result == true) return 0; //got promo OK
            else return 1; //Failed to get promo
        }
        return 2; //failed to enquire on promo
    }

    private boolean promoSuccess(GenericSubInfo sub){

        if(this.smsNotify(sub))
            logger.debug(sub.getSubscriberNumber()+" SMS Sent !");
        else
            logger.debug(sub.getSubscriberNumber()+" SMS NOT Sent !");
        
        logger.debug("Logging Promo Success in Database...");
        
        if(this.updateSubStatus(sub)){
            logger.debug("Logging Promo Success in Database for "+ sub.getSubscriberNumber() +" Succeeded....");
            return true;
        }    
        else{ 
            logger.debug("Logging Promo Success in Database for "+ sub.getSubscriberNumber() +" Failed....");
            return false;
        }
        

        /*try{
          //Call Stored Procedure to store Success
          logger.debug("Logging Promo Success in Database...");
          oraBase_pROMO2.execProc_UPDATE_SUB(sub);

       }catch (Exception ex) {
          logger.error("Databse logging Update "+ sub.getSubscriberNumber() +" failed....", ex);
          dbUpdateFailLog.println("Call To Stored Proc " + OracleDao.procUPDATE_SUB
                        + "Failed..");
          return false;
       }*/

       //return true;
    }
    
    private boolean promoFailure(GenericSubInfo sub){

        if(this.refundSMSFees(sub))
            logger.debug(sub.getSubscriberNumber()+" SMS Refund Succeded !");
        else
            logger.debug(sub.getSubscriberNumber()+" SMS Refund Failed !");

        if(this.smsNotify(sub))
            logger.debug(sub.getSubscriberNumber()+" SMS Sent !");
        else
            logger.debug(sub.getSubscriberNumber()+" SMS NOT Sent !");

        logger.debug("Logging Promo failure in Database...");

        if(this.updateSubStatus(sub)){
            logger.debug("Logging Promo Failure in Database for "+ sub.getSubscriberNumber() +" Succeeded....");
            return true;
        }
        else{
            logger.debug("Logging Promo Failure in Database for "+ sub.getSubscriberNumber() +" Failed....");
            return false;
        }
    }

    private boolean noCredit(GenericSubInfo sub){

            logger.debug(sub.getSubscriberNumber() + " status "+ UcipResponseCode.BELOWMINIMUMBALANCE
                    +" Failed to get the promo...");
            logger.debug(sub.getSubscriberNumber() + " Does not have enough balance....");
            sub.setResult(PromoParameters.PROMO_NOT_ENOUGH_BALANCE);
            sub.setError(false);

            //Send SMS Notification that Not enough balance and sub should try agian
            sub.setSmsReason(PromoParameters.PROMO_NOT_ENOUGH_BALANCE);
            this.smsNotify(sub);
        return true;
    }

    /*public void givePromo()
    {      
        if ( flag != true ){
         logger.error("Error occured during Inialization......quitting......", null);   
         return;   
        }
        
        Payment payRecord = this.parseLine();
        
        if (payRecord == null){
            logger.warning("No Records in File " +  ifile.getName());
            return;
        }

        
        //while (payRecord != null){
        do{
            payRecord = this.parseLine();
            if (payRecord.getSubscriberNumber().equals("-1")) {
                payRecord = this.parseLine();
                logger.debug("Empty Line, Skipping rest of loop..");
                continue;
            }
        
            //Here we should check based on the property file
            //what promo type (voice/sms/data) and
            //what adjustment vale type (fixed/percentage)
            //and then call the appropriate function for each case
            //fixed/percentage checkup is done in the function itself

            //e.g adjustBonusSMS(payRecord)
            boolean result = adjust(payRecord);
            
            if (result == true){
                //Success
                logger.debug(payRecord.getSubscriberNumber()+" logging Adjustment Succeded..");
                 promoSuccessLog.println(payRecord.getSubscriberNumber() + PromoParameters.oDELIM +
                    payRecord.getRealTransactionAmount().toString() + PromoParameters.oDELIM +
                    payRecord.getServiceClass() + PromoParameters.oDELIM +
                    payRecord.getExternalData1() + PromoParameters.oDELIM +
                    payRecord.getExternalData2() + PromoParameters.oDELIM +
                    payRecord.getTimeStamp() + PromoParameters.oDELIM +
                    payRecord.getPromoTransactionAmount().toString() + PromoParameters.oDELIM +
                    new Date().toString());
            }
            else   {     
                //Failure
                logger.debug(payRecord.getSubscriberNumber()+" logging Adjustment Failed..");
                promoFailLog.println(payRecord.getSubscriberNumber() + PromoParameters.oDELIM +
                    payRecord.getRealTransactionAmount().toString() + PromoParameters.oDELIM +
                    payRecord.getServiceClass() + PromoParameters.oDELIM +
                    payRecord.getExternalData1() + PromoParameters.oDELIM +
                    payRecord.getExternalData2() + PromoParameters.oDELIM +
                    payRecord.getTimeStamp() + PromoParameters.oDELIM +
                    payRecord.getPromoTransactionAmount().toString() + PromoParameters.oDELIM +
                    new Date().toString());
            }
            payRecord = this.parseLine();
        }while (payRecord != null);
        
        logger.debug("Reached End of File..");
    }*/

    private boolean getSubs(){

        try {
            //oraBase_pROMO2.execProc_TestCURS(subMap,chkStatusFail);
            oraBase_pROMO2.execProc_MSG_Cursor(subMap);
        } catch (Exception ex) {
            logger.error("Fetching Subscribers failed....", ex);
            dbReadFailLog.println("Call To Stored Proc " + OracleDao.procTestCurs
                    + "Failed..");
            criticalErrsLog.println("Call To Stored Proc " + OracleDao.procTestCurs
                    + "Failed..");
            return false;
        }
        logger.debug("Fetching Subscribers Succeeded......");
        return true;
    }

    private boolean updateSubStatus(GenericSubInfo sub){

        if(sub.getResult() == PromoParameters.ALREADY_SUBSCRIBED){
            logger.debug(sub.getSubscriberNumber() + " Alrady subscribed, will NOT update databse...");
            return true;
        }

        logger.debug(sub.getSubscriberNumber() + " Trying to store promo status in database...");
        try{
             oraBase_pROMO2.execProc_UPDATE_SUB(sub);
        }catch (Exception ex) {
             logger.error("Databse logging Update "+ sub.getSubscriberNumber() +" failed....", ex);
             dbUpdateFailLog.println("Call To Stored Proc " + OracleDao.procUPDATE_SUB
                    + "Failed..");
             return false;
        }
        logger.debug(sub.getSubscriberNumber() + " promo status updated in database...");
        return true;
    }

     /*public boolean givePromo(){

         if(!this.initSubs())
             return false;
         


         return true;


    }*/

    public boolean giveEntryLevelPromo(){

        if (!this.getSubs()){
            promoAppLog.println("FAIL: Error getting subscribers....");
            return false;
        }

        /*Iterator myVeryOwnIterator = subMap.keySet().iterator();
        while(myVeryOwnIterator.hasNext()) {
                    //System.out.println(myVeryOwnIterator.next());
        }*/

        //Loop thru subs and do stuff :)
        if(subMap.isEmpty()){
            logger.debug("No Subscribers to process...");
            return false;
        }

        for(Map.Entry entry : subMap.entrySet()) {
			
            GenericSubInfo sub = (GenericSubInfo)entry.getValue();
            logger.debug(sub.getSubscriberNumber() + " From OptIn Channel " + sub.getOptInChannel().toString());

            //Get Balance and Service Class
            if(!this.getSubscricerInfo(sub)){
                promoAppLog.println("FAIL: " + sub.getSubscriberNumber() + " failed to get Subscriber Information....");;
                logger.debug(sub.getSubscriberNumber() + " failed to get Subscriber Information....");
                //Try to refund and send SMS
                logger.debug(sub.getSubscriberNumber() + " trying to refund....");
                sub.setSmsReason(PromoParameters.OTHER_FAILURE);
                sub.setError(true);
                sub.setResult(PromoParameters.GET_BALANCE_FAILURE);
                this.promoFailure(sub);

                //this.refundSMSFees(sub);
                //this.smsNotify(sub);
                //Update Promo status
                //this.updateSubStatus(sub);
                continue;
            }

            //Validate OptIn Text
            int pkgType = validateOptInContent(sub.getOptInText());
            if (pkgType == PromoParameters.INVALID_CHOICE){
                invalidOptInLOg.println(sub.getSubscriberNumber() + " Has Invalid OptIn Msg " + sub.getOptInText());
                promoAppLog.println("FAIL: " + sub.getSubscriberNumber() + " Has Invalid OptIn Msg " + sub.getOptInText());
                logger.debug(sub.getSubscriberNumber() + " Has Invalid OptIn Msg "+ sub.getOptInText() + " trying to refund....");
                sub.setSmsReason(PromoParameters.INVALID_OPTiN_CONTENT);
                sub.setError(false);
                sub.setResult(PromoParameters.INVALID_OPTiN_CONTENT);
                this.promoFailure(sub);

                //this.refundSMSFees(sub);
                //this.smsNotify(sub);
                //Update Promo status
                //this.updateSubStatus(sub);
                continue;
            }

            //Valid OptIn Text


            /*Get Balance and Service Class
            String msisdn = sub.getSubscriberNumberShortFormat();
            logger.debug("Checking Initial Balance.....");
            BasicUCIPResponse bresp =
                    ucipAdjuster.BalanceEnquiryTRequest(msisdn);
            if(bresp != null){            
                if (bresp.getResponseCode().equals(UcipResponseCode.SUCCESSFUL)){
                    //System.out.println("SUCCESS Code = " + bresp.getResponseCode());
                    //Store Service Class
                    sub.setServiceClass(new Integer(bresp.getServiceClassCurrent()));
                    //Store Main Balance
                    sub.setAccntBefore(new Integer(0),new Double(bresp.getAccountValue1()));
                    //System.out.println("Acc1 "+ bresp.getAccountValue1());
                    //System.out.println("Acc2 "+ bresp.getAccountValue2());
                    //System.out.println(bresp.get);
                    for (DedicatedAccountEnquiryInformation da : bresp.getDedicatedAccountInformation()){
                        //if(da.getDedicatedAccountID() == PromoParameters.TARGET_ACCNT_ID);
                            sub.setAccntBefore(da.getDedicatedAccountID(),
                                    new Double(da.getDedicatedAccountValue1()));
                    }
                    logger.debug(msisdn+" SvcClass " + Integer.toString(sub.getServiceClass())
                            +" with Main balance "+ sub.getAccntBefore(new Integer(0)));
                    sub.setError(false);
                    sub.setResult(PromoParameters.SUCCESS);
                }
                else if (bresp.getResponseCode().equals(UcipResponseCode.SUBSCRIBERNOTFOUND)){
                    //System.out.println("NOT FOUND Code = " + bresp.getResponseCode());
                    //Do Nothing, will be detected by isEligible()
                    logger.debug( msisdn +" Not Found on Charging System, code: "+
                            bresp.getResponseCode());
                    sub.setServiceClass(-1);
                }
                else
                {
                    logger.error("Balance Enquiry for "+ msisdn +" Failed with code "
                            + bresp.getResponseCode(),null);
                    promoFailLog.println(sub.getSubscriberNumber() + " Failed to get Promo due to failed Balance Enquiry !");
                    sub.setError(true);
                    sub.setResult(PromoParameters.GET_BALANCE_FAILURE);
                   
                    //Try to refund and send SMS
                    logger.debug(sub.getSubscriberNumber() + " trying to refund....");
                    this.refundSMSFees(sub);
                    sub.setSmsReason(PromoParameters.OTHER_FAILURE);
                    this.smsNotify(sub);

                    //Update Promo status
                    this.updateSubStatus(sub);

                    continue;
               }
            }else{
                logger.error("NULL Balance Enquiry for "+msisdn,null);
                promoFailLog.println(sub.getSubscriberNumber() + " Failed to get Promo due to NULL Balance Enquiry !");
                this.refundSMSFees(sub);
                sub.setError(true);
                sub.setResult(PromoParameters.GET_BALANCE_FAILURE);

                //Try to refund and send SMS
                logger.debug(sub.getSubscriberNumber() + " trying to refund....");
                this.refundSMSFees(sub);
                sub.setSmsReason(PromoParameters.OTHER_FAILURE);
                this.smsNotify(sub);
                //Update Promo status
                this.updateSubStatus(sub);

                continue;
            }*/



            //Assign PAckage
            this.assignPromoPackage(sub);
            
            //Check Eligibility
            switch(this.isEligible(sub)){
                case 1:
                    logger.debug(sub.getSubscriberNumber() + " SvcClass "+ sub.getServiceClass()+ " Not eligible for promro !");
                    promoAppLog.println("FAIL: " + sub.getSubscriberNumber() + " SvcClass "+ sub.getServiceClass()+ " Not eligible for promro !");
                    sub.setSmsReason(PromoParameters.SVC_CALSS_ELIGIBILITY);
                    sub.setError(false);
                    sub.setResult(PromoParameters.SVC_CALSS_ELIGIBILITY);
                    this.promoFailure(sub);

                    //Try to refund + SMS
                    //this.refundSMSFees(sub);
                    //this.smsNotify(sub);
                    //Update Promo status
                    //this.updateSubStatus(sub);
                    continue;
                    //break;
                case 2:
                    logger.debug(sub.getSubscriberNumber() + " Not enough balance ,so Not eligible for promro !");
                    promoAppLog.println("FAIL: " + sub.getSubscriberNumber() + " Not enough balance ,so Not eligible for promro !");
                    //this.noCredit(sub);
                    sub.setSmsReason(PromoParameters.PROMO_NOT_ENOUGH_BALANCE);
                    sub.setError(false);
                    sub.setResult(PromoParameters.PROMO_NOT_ENOUGH_BALANCE);
                    this.promoFailure(sub);

                    //Try to refund + SMS
                    //this.refundSMSFees(sub);
                    //this.smsNotify(sub);
                    //Update Promo status
                    //this.updateSubStatus(sub);
                    continue;
                    //break;
                case 3:
                    logger.debug(sub.getSubscriberNumber() + " Already subscribed ,so Not eligible for promro !");
                    promoAppLog.println("FAIL: " + sub.getSubscriberNumber() + " Already subscribed ,so Not eligible for promro !");
                    sub.setSmsReason(PromoParameters.ALREADY_SUBSCRIBED);
                    sub.setError(false);
                    sub.setResult(PromoParameters.ALREADY_SUBSCRIBED);
                    this.promoFailure(sub);

                    //Try to refund + SMS
                    //this.refundSMSFees(sub);
                    //this.smsNotify(sub);
                    //No NeedUpdate Promo status
                    //this.updateSubStatus(sub);
                    continue;
                    //break;
               case 100:
                    logger.debug(sub.getSubscriberNumber() + " Unknown eligibility error !");
                    promoAppLog.println("FAIL: " + sub.getSubscriberNumber() + "Unknown eligibility error !");
                    sub.setSmsReason(PromoParameters.OTHER_FAILURE);
                    sub.setError(true);
                    sub.setResult(PromoParameters.OTHER_FAILURE);
                    this.promoFailure(sub);

                    //Try to refund + SMS
                    //this.refundSMSFees(sub);
                    //this.smsNotify(sub);
                    //Update Promo status
                    //this.updateSubStatus(sub);
                    continue;
                    //break;
                case 0:
                    boolean gotPromo = false;
                    logger.debug(sub.getSubscriberNumber() + " eligible for promro !");
                    //Check Subscriber Service Class
                     //Try To adjust the subscribers
                    if(easy_SvcCls.contains(sub.getServiceClass())){
                        if(this.adjustEasy(sub) == 0)
                            gotPromo = true;
                    }else{
                        if(this.adjust(sub) == 0)
                            gotPromo = true;
                    }
                    if(gotPromo == true){
                        promoAppLog.println("SUCCESS: " + sub.getSubscriberNumber() + " Got the promo !!");
                        sub.setSmsReason(PromoParameters.SUCCESS);
                        sub.setError(false);
                        sub.setResult(PromoParameters.SUCCESS);
                        this.promoSuccess(sub);
                        //internally updates substatus as well.
                        //internally sends SMSNotification as well
                        //return true;
                        continue;
                    }
                    //Failed Adjustmensts
                        promoAppLog.println("FAIL: " + sub.getSubscriberNumber() + "OTHER ERROR Occured, please check promo.log for details !");
                        sub.setSmsReason(PromoParameters.OTHER_FAILURE);
                        sub.setError(true);
                        sub.setResult(PromoParameters.OTHER_FAILURE);
                        this.promoSuccess(sub);

                        //Try to refund + SMS
                        //this.refundSMSFees(sub);
                        //adjust() function already handles the specific error reason
                        //sub.setSmsReason(sub.getSmsReason());
                        //this.smsNotify(sub);
                        //Update Promo status
                        //this.updateSubStatus(sub);
                    }
                    //return true;
                    continue;
            }
		//}
        //All went fine
        return true;
    }

    private boolean getSubscricerInfo(GenericSubInfo sub){
         //Get Balance and Service Class
            logger.debug("Inquiring Subscriber Information Initial Balance.....");
            String msisdn = sub.getSubscriberNumberShortFormat();

            BasicUCIPResponse bresp =
                    ucipAdjuster.BalanceEnquiryTRequest(msisdn);
            if(bresp != null){
                if (bresp.getResponseCode().equals(UcipResponseCode.SUCCESSFUL)){
                    //System.out.println("SUCCESS Code = " + bresp.getResponseCode());
                    //Store Service Class
                    sub.setServiceClass(new Integer(bresp.getServiceClassCurrent()));
                    //Store Main Balance
                    sub.setAccntBefore(new Integer(PromoParameters.MAIN_ACCNT_ID),new Double(bresp.getAccountValue1()));
                    //System.out.println("Lang ID "+ bresp.getLanguageIDCurrent());
                    if(bresp.getLanguageIDCurrent() == null){
                        sub.setLanguageID(new Integer(PromoParameters.DEFAULT_LANG_ID));
                        logger.debug("Failed to get Langugae ID,using default: " + PromoParameters.ARABIC_LANG_ID);
                    }else
                        sub.setLanguageID(new Integer(bresp.getLanguageIDCurrent()));
                    //System.out.println("Acc1 "+ bresp.getAccountValue1());
                    //System.out.println("Acc2 "+ bresp.getAccountValue2());
                    //System.out.println(bresp.get);
                    for (DedicatedAccountEnquiryInformation da : bresp.getDedicatedAccountInformation()){
                        //if(da.getDedicatedAccountID() == PromoParameters.TARGET_ACCNT_ID);
                            sub.setAccntBefore(da.getDedicatedAccountID(),
                                    new Double(da.getDedicatedAccountValue1()));
                    }
                    logger.debug(msisdn+" SvcClass " + Integer.toString(sub.getServiceClass())
                            +" with Main balance "+ sub.getAccntBefore(new Integer(0)));
                    sub.setError(false);
                    sub.setResult(PromoParameters.SUCCESS);
                    return true;
                }
                else if (bresp.getResponseCode().equals(UcipResponseCode.SUBSCRIBERNOTFOUND)){
                    //System.out.println("NOT FOUND Code = " + bresp.getResponseCode());
                    //Do Nothing, will be detected by isEligible()
                    logger.debug( msisdn +" Not Found on Charging System, code: "+
                            bresp.getResponseCode());
                    sub.setServiceClass(-1);
                    return true;
                }
                else
                {
                    logger.error(sub.getSubscriberNumber() + " failed Initial Balance Enquiry, with UCIP code " +
                           bresp.getResponseCode(),null);
                    //ucipFailLog.println(sub.getSubscriberNumber() + " failed Initial Balance Enquiry, code " +
                    //        bresp.getResponseCode() );
                    //sub.setError(true);
                    //sub.setResult(PromoParameters.GET_BALANCE_FAILURE);
                    return false;

                    /*NOT EXECUTED
                    //Try to refund and send SMS
                    logger.debug(sub.getSubscriberNumber() + " trying to refund....");
                    this.refundSMSFees(sub);
                    sub.setSmsReason(PromoParameters.OTHER_FAILURE);
                    this.smsNotify(sub);

                    //Update Promo status
                    this.updateSubStatus(sub);
                    */

               }
            }else{
                logger.error(sub.getSubscriberNumber() + " failed to get Promo due to Initial NULL Balance Enquiry !",null);
                //ucipFailLog.println(sub.getSubscriberNumber() + " failed to get Promo due to Initial NULL Balance Enquiry !");
                //this.refundSMSFees(sub);
                //sub.setError(true);
                //sub.setResult(PromoParameters.GET_BALANCE_FAILURE);
                return false;

                /*NOT EXECUTED

                //Try to refund and send SMS
                logger.debug(sub.getSubscriberNumber() + " trying to refund....");
                this.refundSMSFees(sub);
                sub.setSmsReason(PromoParameters.OTHER_FAILURE);
                this.smsNotify(sub);
                //Update Promo status
                this.updateSubStatus(sub);
                 */

            }


    }


    private int validateOptInContent(String optIn) {
        if (optIn == null)
            return PromoParameters.INVALID_CHOICE;
        if (optIn.equalsIgnoreCase(PromoParameters.pkg1Option.toString()))
            return PromoParameters.pkg1Option;
        if (optIn.equalsIgnoreCase(PromoParameters.pkg2Option.toString()))
            return PromoParameters.pkg2Option;
        return PromoParameters.INVALID_CHOICE;

    }

    private  boolean smsNotify(GenericSubInfo sub){
        //Has lower piriority than other errors
        //Can NOT mask other errors in database, only stored localy

        String smsText;
        String reason;
       // System.out.println(sub.getSmsReason());
        if(sub.getSmsReason() == PromoParameters.SVC_CALSS_ELIGIBILITY ){
           if(sub.getLanguageID() == PromoParameters.ARABIC_LANG_ID)
              smsText = PromoParameters.SMS_NOT_ELIGIBLE_AR ;
           else
              smsText = PromoParameters.SMS_NOT_ELIGIBLE_EN ;
            reason = "SVC_CALSS_ELIGIBILITY";
        }
        else if(sub.getSmsReason() == PromoParameters.PROMO_NOT_ENOUGH_BALANCE){
             if(sub.getLanguageID() == PromoParameters.ARABIC_LANG_ID)
                smsText = PromoParameters.SMS_NOT_ENOUGH_CREDIT_AR ;
             else
                smsText = PromoParameters.SMS_NOT_ENOUGH_CREDIT_EN ;
            reason = "PROMO_NOT_ENOUGH_BALANCE";
        }
        else if(sub.getSmsReason() == PromoParameters.SUCCESS){
            if(sub.getLanguageID() == PromoParameters.ARABIC_LANG_ID)
                smsText = PromoParameters.SMS_SUCCESS_AR ;
            else
                smsText = PromoParameters.SMS_SUCCESS_EN ;
            smsText = smsText.replaceAll("VAL", sub.getOptInText());
            reason = "PROMO_SUCCESS";
        }
        else if(sub.getSmsReason() == PromoParameters.ALREADY_SUBSCRIBED){
            if(sub.getLanguageID() == PromoParameters.ARABIC_LANG_ID)
                smsText = PromoParameters.SMS_ALREADY_SUBSCRIBED_AR ;
            else
                smsText = PromoParameters.SMS_ALREADY_SUBSCRIBED_EN ;
            reason = "ALREADY_SUBSCRIBED";
        }
         else if(sub.getSmsReason() == PromoParameters.INVALID_OPTiN_CONTENT){
            if(sub.getLanguageID() == PromoParameters.ARABIC_LANG_ID)
                smsText = PromoParameters.SMS_INVALID_CONTENT_AR ;
            else
                smsText = PromoParameters.SMS_INVALID_CONTENT_EN ;
            reason = "INVALID CONTENT";
        }

        else //else genral failure
        {
            smsText = PromoParameters.SMS_FAILURE_AR;
            reason = "GENERAL FAILURE";
        }

        try{
            logger.debug("Sending SMS Notification for "+ sub.getSubscriberNumber() + " " + reason );
            oraSMPP.execProc_SENDSMS(sub.getSubscriberNumberShortFormat(),smsText);
        } catch (Exception ex) {
            logger.error("SMS Notification for "+ sub.getSubscriberNumber() +" failed....", ex);
            smsFailLog.println(sub.getSubscriberNumber()+ " " + reason + "Notification failed..");
            return false;
        }
        return true;
    }

    private boolean refundSMSFees(GenericSubInfo sub){

        //Refunds are only eligible for SMS OptInChannel
        //Takes piriority over (can mask) other errors in database

        if(sub.getOptInChannel().equals(PromoParameters.OptInChannels.SMS)){

             logger.debug(sub.getSubscriberNumber() + " Trying to refund SMS....");
             this.assignSMSREFPackage(sub);
             
             //boolean result = ucipAdjuster.adjustDedAccountFD1("-1",
               //     "-1" ,sub.getSubscriberNumberShortFormat() , PromoParameters.ADJ_CODE.toString(),
                 //   PromoParameters.ADJ_TYPE.toString(),PromoParameters.REFUND_VALUE_SMS,-1);

             int result = ucipAdjuster.adjustDedAccountFD1(sub);

             if (result != UcipResponseCode.SUCCESSFUL.intValue()){
                 refundLog.println("FAIL: " + sub.getSubscriberNumber() + " SMSFee Refund Failed");
                 logger.error("SMSFee Refund Failed for " + sub.getSubscriberNumber(),null);
                 //OverRide ANY OTHER ERROR with REFUND_ERRROR
                 sub.setError(true);
                 sub.setResult(PromoParameters.REFUND_FAILED);

                 /*try{
                     //Call Stored Procedure to store Refund error
                     logger.debug("Logging Refund Failure in Database...");
                     oraBase_pROMO2.execProc_UPDATE_SUB(sub);

                 }catch (Exception ex) {
                        logger.error("Databse logging Update "+ sub.getSubscriberNumber() +" failed....", ex);
                        dbUpdateFailLog.println("Call To Stored Proc " + OracleDao.procUPDATE_SUB
                                + "Failed..");
                        return false;
                 }
                 return false;*/
             }
             else{
                //Refund Succeeded

                //.println("SMSFee Refund Succeded for " + sub.getSubscriberNumber());
                logger.debug("SMSFee Refund Succeded for " + sub.getSubscriberNumber());
                refundLog.println("SUCCESS: " + sub.getSubscriberNumber() + " Refunded value " + PromoParameters.REFUND_VALUE_SMS );
                //sub.setResult(PromoParameters.);
                return true;
             }
         }
        logger.debug(sub.getSubscriberNumber() + " Not Eligible for Refund !");
        return false;
        /*
         else {
                //Other OptIns only SMS notification to try again
                try{
                    oraSMPP.execProc_SENDSMS(sub.getSubscriberNumber(),PromoParameters.SMS_FAILURE);
                } catch (Exception ex) {
                    logger.error("SMS Notification for "+ sub.getSubscriberNumber() +" failed....", ex);
                    smsFailLog.println(sub.getSubscriberNumber()+" Please Try again Notification Only failed..");
                    sub.setError(true);
                    sub.setResult(PromoParameters.SMS_NOTIFICATION_FAILED);
                    return false;
                }

         }

         sub.setError(false);
         sub.setResult(PromoParameters.SUCCESS);
         return true;*/
     }

     private Integer isEligible(GenericSubInfo sub){
     logger.debug(sub.getSubscriberNumber() + " Checking Eligibility.....");
        //Eligibilty for Service Class + Balance
            //Result 1: SvcClass NOT Eligible
            //          OR Not found, assume postpaid
            //Result 2: NoCredit
            //Result 3: Already Subscribed
            //Result 100: UNKNOWN Err
            //Result 0: Eligible

        //Check if does not exist on CS
        if(sub.getServiceClass() == PromoParameters.INVALID_CHOICE.intValue()){
            logger.debug(sub.getSubscriberNumber() + " Eligibility failed, Not Found on Charging System..");
            return 1;
        }
        logger.debug(sub.getSubscriberNumber() + " found on CS....");

        //Check if already OptedIN
       logger.debug(sub.getSubscriberNumber() + " Previous Status " + sub.getPreviousState());
       if(sub.getPreviousState() == PromoParameters.SUCCESS){
           logger.debug(sub.getSubscriberNumber() + " Already subscribed in the promo !" );
           return 3;
        }
       

        //Service Calss eligibality
        Integer svcC = sub.getServiceClass();
        //If to Include Service Classes
        if (PromoParameters.svcClassAction.equals(PromoParameters.svcClassAction.INCLUDE)){
            if(! svcClses.contains(svcC)){
                logger.debug(sub.getSubscriberNumber() +
                    " eligibality failed due to service class " + svcC + "Not included..");
                
            return 1;
            }
        }else if (PromoParameters.svcClassAction.equals(PromoParameters.svcClassAction.EXCLUDE)){

        //If to Exclude Service Classes
        if( svcClses.contains(svcC)){
                logger.debug(sub.getSubscriberNumber() +
                    " eligibality failed due to service class " + svcC+ " Excluded..");
               
            return 1;
            }
        }
        else {
            //Should not Happen, checked in PromoParameters.validateParemeters()
            logger.error("UnExpexted Error, Unknown Service Class Action " +
                    PromoParameters.svcClassAction, null);
           
            return 100;
       }

       

       //Exclude Subscribers with Balance less than desired package

       //VF-Easy and Bill Manager, sheck for DA1
        logger.debug("Checking Balance Eligibility...");
        if(easy_SvcCls.contains(sub.getServiceClass())){
            if ( sub.getAccntBefore(new Integer(PromoParameters.VF_EASY_MAIN_ACCNT_ID)).intValue() < Integer.parseInt(sub.getOptInText()) * 100){
                //this.noCredit(sub);
                logger.debug(sub.getSubscriberNumber() + " Does Not have Enough Balance in DA1 !");
                return 2;
            }
        }else //Other Service Calsses check for MAIN Balance
            if ( sub.getAccntBefore(PromoParameters.MAIN_ACCNT_ID).intValue() < Integer.parseInt(sub.getOptInText()) * 100){
                //this.noCredit(sub);
                logger.debug(sub.getSubscriberNumber() + " Does Not have Enough Balance in Main !");
                return 2;
            }

        //If passed all the above, then eligible :)
        return 0;

     }

     private int adjustEasy(GenericSubInfo easySub){

         switch (this.adjust(easySub)){
             case 0:
                 logger.debug(easySub.getSubscriberNumber() + " VF-Easy 1st adjsutment succeeded...");
                 break;
             case 1:   //No Balance
                return 1;
             case 2:   //Not found
                return 2;
             case 3:
                 logger.error(easySub.getSubscriberNumber() + " 1st adjsustment may have failed....plz check",null);
                 easyErrLog.println(easySub.getSubscriberNumber() + " 1st adjsustment may have failed....plz check");
                 return 3;
         }

         //Here then 1st adjustment succeded
         //Move on to second adjustment
         easySub.setChargingID(easySub.getChargingID() + 1);
         switch (this.adjust(easySub)){
             case 0:
                 logger.debug(easySub.getSubscriberNumber() + " VF-Easy 2nd adjsutment succeeded...");
                 return 0;
             case 1:   //No Balance, should NOT HAPPEN
                logger.error(easySub.getSubscriberNumber() + " VF-Easy 2nd adjsutment No Balance, should NOT happen...",null);
                easyErrLog.println(easySub.getSubscriberNumber() + " VF-Easy 2nd adjsutment No Balance, should NOT happen...");
                return 1;
             case 2:   //Not found
                logger.error(easySub.getSubscriberNumber() + " VF-Easy 2nd adjsutment sub not found, should NOT happen...",null);
                easyErrLog.println(easySub.getSubscriberNumber() + " VF-Easy 2nd adjsutment sub not found, should NOT happen...");
                return 2;
             case 3:
                 logger.error(easySub.getSubscriberNumber() + " 2nd adjsustment may have failed....plz check",null);
                 easyErrLog.println(easySub.getSubscriberNumber() + " 2nd adjsustment may have failed....plz check");
                 return 3;
         }
         logger.error(easySub.getSubscriberNumber() + " adjustEasy() should never reach here !!",null);
         return 3;
     }
    
     private void terminate(){
          try{
           
            logger.debug("Closing handles....");
            
            if(br != null )
                br.close();
            
            if(promoAppLog != null ){
                promoAppLog.close();
            }

            /* if(promoFailLog != null ){
                promoFailLog.close();
            }

            if(ucipSuccessLog != null ){
                ucipSuccessLog.close();
            }

            if(ucipFailLog != null ){
                ucipFailLog.close();
            }*/

            if(smsFailLog != null ){
                smsFailLog.close();
            }
            if(refundLog != null ){
                refundLog.close();
            }
            /*if(refundFailLog != null ){
                refundFailLog.close();
            }*/

            if(dbReadFailLog != null ){
                dbReadFailLog.close();
            }

             if(dbUpdateFailLog != null ){
                dbUpdateFailLog.close();
            }

            if(criticalErrsLog != null ){
                criticalErrsLog.close();
            }

              if(invalidOptInLOg != null ){
                invalidOptInLOg.close();
            }

            if(easyErrLog != null ){
                easyErrLog.close();
            }

            
           
           //For the next functions, exceptions are handled internally
           oraAIWA2.closeDao();
           oraSMPP.closeDao();
           oraBase_pROMO2.closeDao();

           //UCIP LOg cleanUp
           ucipAdjuster.closeHandles();
           
           if( PoolManager.releseAll()){
               logger.debug("Pool Manager Succefully leased.");
           }else
               logger.debug("Pool Manager already leased or was null.");
            
        }catch (Exception Ex){
            //Ex.printStackTrace();
            logger.error("Exeption when closing Handles..", Ex);
        }
    }
     
     
    public static void main(String args[])
    {
        String arg0 = new String();
        arg0 = "dd1mm1yyyy1";
        if(arg0 == null)//if(args.length != 1)
        {
            System.err.println("Wrong Input Parameter Number .");
            System.out.println("Usage: java -jar EntryLevelPromo.jar zz ");
            System.out.println("where 'zz' is the base Date.");
            System.exit(1);
        }
        EntryLevelPromo entrypromo = new EntryLevelPromo(arg0);//args[0]);

        if (!entrypromo.initPromoEngine()){
          logger.error("Error occured during Inialization......", null);
          logger.error("Aborting......", null);
          System.exit(0);
        }

      //  entrypromo.giveEntryLevelPromo();

      //  entrypromo.terminate();

        /*Double dd = Double.valueOf("5.1");
        //Double ddd = new Double("100");
        Double bb = new Double (510);
        System.out.println(dd.toString());
        System.out.println(new BigDecimal("5.1").multiply(new BigDecimal("100")).toString());*/


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
    public static void entryPromoHandler(){
        String arg0 = new String();
        arg0 = "dd1mm1yyyy1";
        if(arg0 == null)//if(args.length != 1)
        {
            System.err.println("Wrong Input Parameter Number .");
            System.out.println("Usage: java -jar EntryLevelPromo.jar zz ");
            System.out.println("where 'zz' is the base Date.");
            System.exit(1);
        }
        EntryLevelPromo entrypromo = new EntryLevelPromo(arg0);//args[0]);

        if (!entrypromo.initPromoEngine()){
          logger.error("Error occured during Inialization......", null);
          logger.error("Aborting......", null);
          System.exit(0);
        }

        //  entrypromo.giveEntryLevelPromo();

        //  entrypromo.terminate();

        /*Double dd = Double.valueOf("5.1");
        //Double ddd = new Double("100");
        Double bb = new Double (510);
        System.out.println(dd.toString());
        System.out.println(new BigDecimal("5.1").multiply(new BigDecimal("100")).toString());*/


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