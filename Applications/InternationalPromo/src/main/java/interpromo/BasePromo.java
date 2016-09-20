package interpromo;

import drs.GenericSubInfo;

import drs.PaymentCharge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import oracle.util.OracleDao;
import oracle.util.PoolManager;

import promo.util.PromoLogger;
import promo.util.PromoParameters;

import ucip.BasicUCIPResponse;
import ucip.UcipHandler;

import ucip.UcipResponseCode;


    /*
     * Created by Sara Khaled at 15-6-2009
     * for International basePromo from table Promo_Prepaid
     */
     
public class BasePromo {
    
    private static Integer DA_ID;
    
    public static final String iDELIM = PromoParameters.iDELIM;
    public static final String oDELIM = PromoParameters.oDELIM;
    
    private static final  String AIWADRD1_DB = PromoParameters.PROMO_DB;
    private static final  String SMPP_DB = PromoParameters.SMPP_DB;
        
    private OracleDao oraAIWA2;
    private OracleDao oraSMPP;
    private UcipHandler ucipAdjuster;
    
    private String logBaseDir;
    private static String baseDate;
   
    private Map<String,GenericSubInfo> optIns = new HashMap<String,GenericSubInfo>();  //All OptIns records from Promo_Prepaid table in DB    
    
    private File ifile;                  //Input file for All Payment Charges (PCRs)
    
    private BufferedReader br;
    
    private PrintWriter promoAppLog;          //Promo Application Log
    private PrintWriter smsFailLog;           //Logging SMS Notification failures  
    private PrintWriter dbReadLog;            //Log DB checking/getting payRecords
    private PrintWriter dbUpdateLog;          //Log DB when updating Promo-Prepaid counters
    private PrintWriter criticalErrsLog;      //Log Critical Errors
    
    private boolean flag = true;
    
    private static PromoLogger logger;
    private static PromoParameters ppms;
    
    static {
        logger = new PromoLogger(BasePromo.class);
        ppms = new PromoParameters();
    }
    
    public BasePromo(String fin, String logBaseDir, String baseDate)
    {      
        this.logBaseDir = logBaseDir;
        this.baseDate = baseDate;
        this.ifile = new File(fin);
        if(!ifile.exists())
        {
            System.err.println("Input File: '" + ifile.getName() + "' does not exist !");
            logger.error("Input File: '" + ifile.getName() + "' does not exist !", null);
            System.exit(0);
        }
    }
    public boolean initPromoEngine(){

        logger.debug("Engine Initialization started..... :)");

       //Validate promo Parameters
       //Should be THE FIRST STEP
       if (! ppms.validateParemeters()){
           logger.error("Error While Initializing Promo Parameters...", null);
           criticalErrsLog.println("Error While Initializing Promo Parameters.");
           System.out.println("Error While Initializing Promo Parameters.");
           return false;
       }

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

        //Initialise objects
       ucipAdjuster = new UcipHandler();
       if( !ucipAdjuster.initChargingModel()){
           logger.error("Error in initializing charging model...", null);
           criticalErrsLog.println("Error in initializing charging model.");
           return false;
       }

        //Initialise objects
       oraAIWA2 = new OracleDao(AIWADRD1_DB);
       if (! oraAIWA2.getConnection()) {
           logger.error("Error in Creating Connection to AIWA2...", null);
           criticalErrsLog.println("Error in Creating Connection to AIWA2.");
           return false;
       }
      
       oraSMPP = new OracleDao(SMPP_DB);
       if (! oraSMPP.getConnection()){
           logger.error("Error in Creating Connection to SMPP...", null);
           criticalErrsLog.println("Error in Creating Connection to SMPP.");
           return false;
       }
/*
       oraBase_pROMO2 = new OracleDao(PromoParameters.Base_pROMO2_DB);
       if (! oraBase_pROMO2.getConnection()){
           logger.error("Error in Creating Connection to Base_pROMO2_DB...", null);
           criticalErrsLog.println("Error in Creating Connection to Base_pROMO2_DB.");
           return false;
       }
    */
        Map<String,GenericSubInfo> m = new HashMap<String,GenericSubInfo>();
        m = getAllOptIns();
        if(m == null || m.size() == 0){
            logger.error("No OptIns found in table Promo_Prepaid...", null);
            criticalErrsLog.println("No OptIns found in table Promo_Prepaid...");
            System.out.println("No OptIns found in table Promo_Prepaid...");
            return false;
        }else
            optIns.putAll(m); 
       logger.debug("Engine Initialized successfully :)");
       return true;
    }

    public Map<String,GenericSubInfo> getAllOptIns(){
        
        System.out.println("getting all optIns........."); 
        logger.debug("getting all optIns........."); 
        try{
            String query = "SELECT MSISDN,status,promoCounter FROM promo_prepaid WHERE status='I'";
            
            System.out.println("query: "+query); 
            dbReadLog.println("query: "+query); 
            
            ResultSet rs = oraAIWA2.execStatmnt(query);
            while(rs.next()){
                String msisdn = rs.getString("MSISDN");  
                String status = rs.getString("status"); 
                int promoCounter = Integer.parseInt(rs.getString("promoCounter"));
                
                GenericSubInfo sub = new  GenericSubInfo();
                sub.setSubscriberNumber(msisdn);
                sub.setOptInStatus(status);
                sub.setPromoCounter(promoCounter);
                
                System.out.println("(msisdn,status,promoCounter): ("+msisdn+","+status+","+promoCounter+")");     
                dbReadLog.println("(msisdn,status,promoCounter): ("+msisdn+","+status+","+promoCounter+")"); 
                optIns.put(msisdn,sub);
            }
            dbReadLog.println(rs.getFetchSize()/2+" records found.");
            
        }catch(SQLException e){
            dbReadLog.println("Error While Reading OptIns...");
            logger.error("Error While Reading OptIns...", null);
            criticalErrsLog.println("Error While Reading OptIns...");
            System.out.println("Error While Reading OptIns...");
        }
        return optIns;
    }
    private int updatePromoCounter(String msisdn,int promoCounter){
        
        System.out.println("updating the opted-In PromoCounter of MSISDN: "+msisdn);
            
        int newPromoCounter = promoCounter + 1;       
        logger.debug("current promoCounter: "+promoCounter); 
        logger.debug("new PromoCounter: "+newPromoCounter); 
        
       
        String updateQuery = "UPDATE promo_prepaid" +
                        " SET promoCounter="+ newPromoCounter+
                        " WHERE msisdn='"+msisdn+"'";
        
        dbUpdateLog.println("updateQuery:" + updateQuery);
        System.out.println("updateQuery:" + updateQuery);
   
        int rs = oraAIWA2.execUpdate(updateQuery);
    
        if(rs != 1)
            dbUpdateLog.println("Error while updating  promoCounter to:"+newPromoCounter+" for msisdn: "+ msisdn);
        dbUpdateLog.println("query result: "+rs); 
        System.out.println("query result: "+rs); 
        return rs;                               
    }
    private PaymentCharge parseLine()
    {
        try {
            String line = br.readLine();
            if (line != null) {
                return new PaymentCharge(line.split(iDELIM));
            } else {
                return null;
            }
        } catch (IOException ex) {
            //Logger.getLogger(FacePromo.class.getName()).log(Level.SEVERE, null, ex);
            logger.error("Failed to read line from file..",ex);
            return null;
        }
    }
    private int isEligible(String MSISDN){
        
        int elligiblity;
        if(!optIns.containsKey(MSISDN)){
            logger.debug("MSISDN: "+MSISDN+" is not optted-In");
            System.out.println("MSISDN: "+MSISDN+" is not optted-In");
            elligiblity = 2;
        }
        else if(optIns.get(MSISDN).getPromoCounter() >= PromoParameters.MAX_PROMO_COUNT ){
            logger.debug("MSISDN: "+MSISDN+" has previously used the Promo "+PromoParameters.MAX_PROMO_COUNT+" times.");
            System.out.println("MSISDN: "+MSISDN+" has previously used the Promo "+"3"+" times.");
            elligiblity = 3;
        }
        //If passed all the above, then eligible :)
        else
            elligiblity = 1;
        logger.debug("MSISDN: "+MSISDN+" isEligible: "+elligiblity);    
        System.out.println("MSISDN: "+MSISDN+" isEligible: "+elligiblity);            
        return elligiblity;
    }
    private int adjust(PaymentCharge payRecord){
                             
       Double transactionAmount = payRecord.getRealTransactionAmount();
       Double adjValue =  transactionAmount * getPromoFactor(payRecord.getVoucherGroup());
       System.out.println("transactionAmount: "+transactionAmount);
       System.out.println("adjValue: "+adjValue);
       payRecord.setPromoTransactionAmount(adjValue.doubleValue());
       
        //DA_ID = PromoParameters.promo_DA_ID(payRecord.getServiceClassCurrent());
        
        //Adjust statements goes here
       logger.debug("UCIP Adjusting "+ new Double(100 * payRecord.getPromoTransactionAmount()).toString()+ " DA"
                + DA_ID.toString() + " to: "+ payRecord.getSubscriberNumber()
                + " Adj.Code: " + PromoParameters.ADJ_CODE.toString()
                + " Adj.Type: " + PromoParameters.ADJ_TYPE.toString() ); 
       
       int status = ucipAdjuster.adjustDedAccountFD1_(
               new Double(100 * payRecord.getPromoTransactionAmount()).toString(),
                DA_ID.toString() ,payRecord.getSubscriberNumber() , PromoParameters.ADJ_CODE.toString(), 
                PromoParameters.ADJ_TYPE.toString());
        
      //Succesfull
      if(status == UcipResponseCode.SUCCESSFUL){
          logger.debug(payRecord.getSubscriberNumber()+" UCIP succeded with status " + status);
          return 0; //succssefull SP
      }

      //Failures
      logger.debug(payRecord.getSubscriberNumber() + " UCIP returned with status: " + status);


      //Not On CS, assume POST; Should not happen
      if(status == UcipResponseCode.SUBSCRIBERNOTFOUND){
          logger.debug(payRecord.getSubscriberNumber() + " Not Found on Charging System..");
          //sub.setError(false);
          //sub.setResult(PromoParameters.SVC_CALSS_ELIGIBILITY);
          logger.error("Elgible Subscriber Not on Prepaid sytem detected....should not happen !!", null);
          return 2;
      }

      //UCIP 100
      if(status == UcipResponseCode.OTHERERROR.intValue()){
          int res = this.checkPromo(payRecord.getSubscriberNumber(),DA_ID,payRecord.getDedAccountBefore());
          if(res == 0){
                    //sub.setError(false);
                    //sub.setResult(PromoParameters.SUCCESS);
              logger.debug(payRecord.getSubscriberNumber() + " balance check passed, status 100 succeded to get the promo :)");
              return 0;
          }else if (res == 1){
              logger.debug(payRecord.getSubscriberNumber() + " balance check failed, status 100 Failed to get the promo...");
                    //sub.setResult(PromoParameters.PROMO_UCIP100_FAILURE);
              //continue to below as faliure
          }else if (res == 2){
              logger.debug(payRecord.getSubscriberNumber() + " failed to do balance inquiry, state UNKNOWN :(");
                    //sub.setResult(PromoParameters.UNKNOWN);
              //continue to below as failure
              return 0;
          }
          
      }
      else
      {//Other Failures
          //Adj Failed for sure
                //sub.setResult(PromoParameters.PROMO_ADJ_FAILURE);
          logger.debug(payRecord.getSubscriberNumber() + " Promo Adjustment failed !");
      }

      //ALL Failures
       logger.debug(payRecord.getSubscriberNumber() + " UCIP fail confirmation with status " + status);
            //sub.setError(true);
       return 3;
      
    }
    private int checkPromo(String msisdn, int DaID, double Da_amountBefore) {
    
        BasicUCIPResponse bresp = ucipAdjuster.BalanceEnquiryTRequest(msisdn);
        if(bresp != null){
            if (bresp.getResponseCode() == 0){
                //get msisdn account info from inquiry for Promo Dedicated Account
                    double Da_amountAfter  = new Double (bresp.getDedicatedAccountInformation()[DaID-1].getDedicatedAccountValue1() );
                    if(Da_amountBefore == Da_amountAfter){
                    logger.debug(msisdn+" with balance after in DA"+DaID+"= "+ Da_amountBefore);

                    //sub.setError(false);
                    //sub.setResult(PromoParameters.SUCCESS);
                    logger.debug("enquire about error 100 succeeded :)");
                    return 1; // Didn't get promo
                }else{
                    logger.debug("enquire about error 100 succeeded :)");
                    return 0; // Got Promo
                }
            }
        }
        logger.debug("Failed to enquire about error 100 after Promo :(");
        return 2; //failed to enquire on promo
    }
    /*
    private int getDedicatedAccountID(PaymentCharge payRecord){
        
        int dedicatedAccountID;
        int serviceClass = payRecord.getServiceClassCurrent();
        
        switch(serviceClass){
            case 8 :  dedicatedAccountID = PromoParameters.DA_ID_SCx;
                      break;
            default : dedicatedAccountID = PromoParameters.DA_ID_default;
        }
        return dedicatedAccountID;
    }*/
    private double getPromoFactor(Double transactionAmount){
        
        int promoAmount;
        double promoFactor;
        int transactionAmountInt = transactionAmount.intValue();
        System.out.print("Card Amount: ");
        
        switch(transactionAmountInt){
            case 200 : System.out.println("200");
                         promoAmount = 50;
                         break;
            case 100 : System.out.println("100");   
                         promoAmount = 40;
                         break;
            case 50 : System.out.println("50");
                        promoAmount = 30;
                        break;
            case 25 : System.out.println("25");  
                        promoAmount = 20;
                        break;
            case 10 : System.out.println("10");  
                        promoAmount = 10;
                        break;
            default: System.out.println("0");  
                        promoAmount = 0;
                        break;
        }
        promoFactor = (double)promoAmount / 100;
        System.out.println("promoAmount: " + promoAmount+" %");
        System.out.println("promoFactor: " + promoFactor);
        return promoFactor;
    }
    private double getPromoFactor(String voucherGroup){
        
        double promoAmount;
        double promoFactor;
        System.out.print("Card Amount: ");
        VoucherEnum voucher = VoucherEnum.forToken(voucherGroup);
        if(voucher == null){
            logger.debug("Unknown Voucher Group: "+voucherGroup+" !!");
            promoAmount = 0;
            return 0;
        }
        else
            promoAmount = PromoParameters.promoFactor(voucher);
        /*
        switch(voucher){
            case C1 : System.out.println("200");
                         promoAmount = PromoParameters.promoAmount_C1;
                         break;
            case A1 : System.out.println("100");   
                         promoAmount = PromoParameters.promoAmount_A1;
                         break;
            case B1 : System.out.println("50");
                        promoAmount = PromoParameters.promoAmount_B1;
                        break;
            case Y1 : System.out.println("25");  
                        promoAmount = PromoParameters.promoAmount_Y1;
                        break;
            case F1 : System.out.println("10");  
                        promoAmount = PromoParameters.promoAmount_F1;
                        break;
            default: System.out.println("0");  
                        promoAmount = 0;
                        break;
        }
        */
        promoFactor = promoAmount / 100.0;
        logger.debug("promoAmount: " + promoAmount+" %");
        System.out.println("promoAmount: " + promoAmount+" %");
        System.out.println("promoFactor: " + promoFactor);
        return promoFactor;
    }
    public void givePromo()
    {      
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
    
        logger.debug("Reached End of File..");
        System.out.println("Reached End of File..");
    }
    private double getDedicatedAmountBefore(String msisdn, int Da_ID){
         
         //Get Balance in Dedicated account Da_ID
            logger.debug("Inquiring Subscriber Information Initial Balance.....");
            //msisdn = msisdn.replaceFirst("^20","");

            double dedAccntBefore = -1.0;
            BasicUCIPResponse bresp = ucipAdjuster.BalanceEnquiryTRequest(msisdn);
            if(bresp != null){
                if (bresp.getResponseCode().equals(UcipResponseCode.SUCCESSFUL)){                  
                    dedAccntBefore = new Double((bresp.getDedicatedAccountInformation()[Da_ID-1]).getDedicatedAccountValue1());
                 
                    logger.debug(msisdn+" with balance before in DA"+Da_ID+"= "+ dedAccntBefore);
                }
                else if (bresp.getResponseCode().equals(UcipResponseCode.SUBSCRIBERNOTFOUND)){
                    //System.out.println("NOT FOUND Code = " + bresp.getResponseCode());
                    logger.debug( msisdn +" Not Found on Charging System, code: "+bresp.getResponseCode());
                }
                else
                {
                    logger.error(msisdn + " failed Initial Balance Enquiry, with UCIP code " +bresp.getResponseCode(),null);
                }
            }else{
                logger.error(msisdn + " failed to get Promo due to Initial NULL Balance Enquiry !",null);
            }
        
        return dedAccntBefore;
    }
    private void terminate(){
         try{
          
           logger.debug("Closing handles....");
           
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
        
          
          //For the next functions, exceptions are handled internally
          oraAIWA2.closeDao();
//          oraSMPP.closeDao();

          //UCIP LOg cleanUp
          ucipAdjuster.closeHandles();
          
          if( PoolManager.releseAll()){
              logger.debug("Pool Manager Succefully leased.");
          }else
              logger.debug("Pool Manager already leased or was null.");
           
       }catch (Exception Ex){
           //Ex.printStackTrace();
           logger.error("Exception when closing Handles..", Ex);
       }
    }
    private boolean promoSuccess(GenericSubInfo sub){

        String msisdn = sub.getSubscriberNumber();
        logger.debug("Updating Promo Counter... ");
        updatePromoCounter(msisdn,optIns.get(msisdn).getPromoCounter());
        
        if(this.smsNotify(sub)){
            logger.debug(sub.getSubscriberNumber()+" SMS Sent !");
            return true;
        }else{
            logger.debug(sub.getSubscriberNumber()+" SMS NOT Sent !");
            return false;
        }
    }

    private  boolean smsNotify(GenericSubInfo sub){
        //Has lower piriority than other errors
        //Can NOT mask other errors in database, only stored localy

        String smsText;
        String reason;
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
        else if(sub.getSmsReason() == PromoParameters.SUCCESS){     //SUCCESS
            if(sub.getLanguageID() == PromoParameters.ARABIC_LANG_ID)
                smsText = PromoParameters.SMS_SUCCESS_AR ;
            else
                smsText = PromoParameters.SMS_SUCCESS_EN ;
            smsText = smsText.replaceAll("VAL", sub.getOptInText());
            reason = "PROMO_SUCCESS";
        }                                                           //ALREADY_SUBSCRIBED_MAX_TIMES
        else if(sub.getSmsReason() == PromoParameters.ALREADY_SUBSCRIBED_MAX_TIMES){
            if(sub.getLanguageID() == PromoParameters.ARABIC_LANG_ID)
                smsText = PromoParameters.SMS_ALREADY_SUBSCRIBED_AR ;
            else
                smsText = PromoParameters.SMS_ALREADY_SUBSCRIBED_EN ;
            smsText = smsText.replaceAll("MXX",String.valueOf(PromoParameters.MAX_PROMO_COUNT)); 
            reason = "ALREADY_SUBSCRIBED";
        }
         else if(sub.getSmsReason() == PromoParameters.INVALID_OPTiN_CONTENT){
            if(sub.getLanguageID() == PromoParameters.ARABIC_LANG_ID)
                smsText = PromoParameters.SMS_INVALID_CONTENT_AR ;
            else
                smsText = PromoParameters.SMS_INVALID_CONTENT_EN ;
            reason = "INVALID CONTENT";
        }

        else //else general failure
        {
            smsText = PromoParameters.SMS_FAILURE_AR;
            reason = "GENERAL FAILURE";
        }

        try{
            logger.debug("Sending SMS Notification for "+ sub.getSubscriberNumber() + " " + reason );
            logger.debug("SMS text: "+smsText);
            oraSMPP.execProc_SENDSMS(sub.getSubscriberNumberShortFormat(),smsText);
        } catch (Exception ex) {
            logger.error("SMS Notification for "+ sub.getSubscriberNumber() +" failed....", ex);
            smsFailLog.println(sub.getSubscriberNumber()+ " " + reason + "Notification failed..");
            return false;
        }
        return true;
    }
    
    public static void main(String[] args) {
        
        
        //delete from here
         Date now = new Date();
         SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        
        String[] arg = new String[3];
        arg[0] = "resources/infile.txt";
        arg[1] = "D:\\myWork\\InternationalPromo\\InterPromo\\resources\\log";
        arg[2] = sdf.format(now);
        // delete to here
        
        if(arg.length != 3)
        {
            System.err.println("Missing Parameter.");
            System.out.println("Usage: java -jar FacePromo.jar xx yy zz ");
            System.out.println("where 'xx' is the Input PaymentCharge (PCR) File.");
            System.out.println("where 'yy' is the Log Directory.");
            System.out.println("where 'zz' is the Date.");
            System.exit(1);
        }
        BasePromo basePromo = new BasePromo(arg[0],arg[1],arg[2]);       
        if (!basePromo.initPromoEngine()){
          logger.error("Error occured during Inialization......", null);
          logger.error("Aborting......", null);
          System.exit(0);
        }
        basePromo.givePromo();
        basePromo.terminate();
    }

    public static String getBaseDate() {
        return BasePromo.baseDate;
    }
}
