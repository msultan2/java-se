/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package promo.util;

//import interpromo.VoucherEnum;

import java.util.ArrayList;

/**
 *
 * @author anassar
 */
public class PromoParameters {

   
    public static final String PROMO_ADJSUTED_VALUE_TYPE ;
    public static final String PROMO_VALUE_ABSOLUTE = "absolute";
    public static final String PROMO_VALUE_PERCENTAGE = "percentage";
    public static final double PROMO_ADJSUTED_VALUE;
    public static final int TARGET_ACCNT_ID;
    public static final int NEW_EXPIRY;

    //public static final double PROMO_DEDUCTED_VALUE;
    //public static final double sourceAccnt;
    public static final String dedicatedAccounts = "DedicatedAccounts.txt";
    public static final String chargingModels = "resources/chargingModels.xml";
    public static final int MAIN_ACCNT_ID = 0;
    public static final int VF_EASY_MAIN_ACCNT_ID = 1;

    //static final  ArrayList Easy_SvcCls;
    //static final  Integer Easy_SMS_DA_ID = 1;
    //static final  Integer Def_SMS_DA_ID = 4;
    public static final Integer defSvcClass = new Integer(-1);

    public static final SvcClassAction svcClassAction;
    public static final String svcClasses = "SvcClasses.txt";
    //public static final String SvcClassAction_INCLUDE = "include";
    //public static final String SvcClassAction_EXCLUDE = "exclude";
    public static enum SvcClassAction { INCLUDE, EXCLUDE };

    public static final String EasySvcClasses = "EasySvcClasses.txt";
    public static final String BBqSvcClasses = "BBqSvcClasses.txt";

    //public static final ArrayList Easy_SvcCls;

    public static final Integer pkg1Option = 50;
    public static final Integer pkg2Option = 100;
    public static final Integer refPKG = 900;
    //to get VF-Easy pkgIDs, simply add 1 for step 1 pkg
    //and add 2 for step 2 pkg if needed

    //public static final Integer invalidPkg = -1;
    public static final Integer INVALID_CHOICE = -1;

    public static final VoucherGroupAction voucherGroupAction;
    public static final String voucherGroups = "VoucherGroups.txt";
    //public static final String VoucherGroupAction_INCLUDE = "include";
    //public static final String VoucherGroupAction_EXCLUDE = "exclude";
    public static enum VoucherGroupAction { INCLUDE, EXCLUDE };
   
   //Start***********************Added By Sara Khaled for International Promo ****************************//
    public static final int DA_ID_default; 
   /* public static final int DA_ID_SCx; 
    
    public static final int promoAmount_C1; 
    public static final int promoAmount_A1;
    public static final int promoAmount_B1;
    public static final int promoAmount_Y1;
    public static final int promoAmount_F1;
    */
    public static final int MAX_PROMO_COUNT;
    public static final String ADJ_CODE;
    public static final String ADJ_TYPE;
    //End***********************Added By Sara Khaled for International Promo ****************************//
    
    public static  final  String PROMO_DB;// = "AIWA2";
    public static  final  String SMPP_DB;// = "SMS";
    public static  final  String Base_pROMO2_DB = "Base_pROMO2";

    public static final String LOG_BASE_DIR;
    public static final String iDELIM;
    public static final String oDELIM;
    
    public static final String COMMENT_MARK;
    public static final  String SMS_APP_ID;    // = "sms_app_id";
    public static final  String SMS_SENDER;    // = "sms_sender";
    public static final  String SMS_TEXT;

    //SMS Texts
    public static final  String SMS_SUCCESS_AR;
    //public static final  String SMS_SUCCESS_100;
    public static final  String SMS_FAILURE_AR;
    public static final  String SMS_ALREADY_SUBSCRIBED_AR;
    public static final  String SMS_NOT_ENOUGH_CREDIT_AR;
    //public static final  String SMS_REFUND;
    public static final  String SMS_NOT_ELIGIBLE_AR;
    public static final  String SMS_INVALID_CONTENT_AR;

    public static final  String SMS_SUCCESS_EN;
    //public static final  String SMS_SUCCESS_100;
    public static final  String SMS_FAILURE_EN;
    public static final  String SMS_ALREADY_SUBSCRIBED_EN;
    public static final  String SMS_NOT_ENOUGH_CREDIT_EN;
    //public static final  String SMS_REFUND_EN;
    public static final  String SMS_NOT_ELIGIBLE_EN;
    public static final  String SMS_INVALID_CONTENT_EN;

    public static String REFUND_VALUE_SMS;
    public static final int ARABIC_LANG_ID = 1;
    public static final int DEFAULT_LANG_ID = 0;

    public static final int DAYS_OF_MONTH = 30;

    public static enum OptInChannels { USSD, SMS, IVR };
    //public s

    //ERROR Codes
    public static final int INVALID_OPTiN_CONTENT ;
    public static final int GET_BALANCE_FAILURE ;
    public static final int PROMO_ADJ_FAILURE ;
    public static final int PROMO_UCIP100_FAILURE ;
    public static final int PROMO_NOT_ENOUGH_BALANCE;
    public static final int SVC_CALSS_ELIGIBILITY;
    public static final int REFUND_FAILED ;
    //public static final int SMS_NOTIFICATION_FAILED ;
    public static final int OTHER_FAILURE ;
    
    public static final int SUCCESS ;
    public static final int ALREADY_SUBSCRIBED;
    public static final int ALREADY_SUBSCRIBED_MAX_TIMES;
    public static final int NOT_OPTED_IN;
    public static final int UNKNOWN;


   

    private static PromoLogger logger = new PromoLogger(PromoParameters.class);
    
     static {
         
       //Either fixed value promo or relative percentage
         PropertyHandler.initXmlProperties();
         //Fixed values or percentage value
         PROMO_ADJSUTED_VALUE = new Double(PropertyHandler.getProperty("promo.value.adjusted")).doubleValue();
         //Absolute value or Percentage
         PROMO_ADJSUTED_VALUE_TYPE = PropertyHandler.getProperty("promo.valtype");
         TARGET_ACCNT_ID = Integer.parseInt(PropertyHandler.getProperty("promo.value.targetAccnt"));
         NEW_EXPIRY = new Integer(PropertyHandler.getProperty("promo.value.newExpiry"));
         //PROMO_DEDUCTED_VALUE = new Double(PropertyHandler.getProperty("promo.value.deducted")).doubleValue();

         PROMO_DB = PropertyHandler.getProperty("AIWA2");
         SMPP_DB = PropertyHandler.getProperty("SMPP");
         
         DA_ID_default = new Integer(PropertyHandler.getProperty("promo.DedicatedAccountID.Default"));
         
         MAX_PROMO_COUNT = new Integer(PropertyHandler.getProperty("MAX_PROMO_COUNT"));
         
         //promo variable percentage amounts
        /* promoAmount_C1 = new Integer(PropertyHandler.getProperty("promo.amount.C1"));
         promoAmount_A1 = new Integer(PropertyHandler.getProperty("promo.amount.A1"));
         promoAmount_B1 = new Integer(PropertyHandler.getProperty("promo.amount.B1"));
         promoAmount_Y1 = new Integer(PropertyHandler.getProperty("promo.amount.Y1"));
         promoAmount_F1 = new Integer(PropertyHandler.getProperty("promo.amount.F1"));
         */
         SMS_TEXT = PropertyHandler.getProperty("sms.txt");
         SMS_SENDER = PropertyHandler.getProperty("sms.sender");
         SMS_APP_ID = PropertyHandler.getProperty("sms.app_id");
         REFUND_VALUE_SMS = PropertyHandler.getProperty("sms.refundValue");

         iDELIM = PropertyHandler.getProperty("log.iDELIM");
         oDELIM = PropertyHandler.getProperty("log.oDELIM");
         LOG_BASE_DIR  =  PropertyHandler.getProperty("log.path");
         COMMENT_MARK = "#";

         ADJ_CODE = PropertyHandler.getProperty("ucip.adj_code");
         ADJ_TYPE = PropertyHandler.getProperty("ucip.adj_type");

         svcClassAction = SvcClassAction.valueOf(PropertyHandler.getProperty("svcClasses.action").toUpperCase());
         voucherGroupAction = VoucherGroupAction.valueOf(PropertyHandler.getProperty("voucherGroups.action").toUpperCase());

        //ERROR Codes
        INVALID_OPTiN_CONTENT = Integer.parseInt(PropertyHandler.getProperty("error.INVALID_OPTiN_CONTENT"));
        PROMO_ADJ_FAILURE = Integer.parseInt(PropertyHandler.getProperty("error.PROMO_ADJ_FAILURE"));
        PROMO_UCIP100_FAILURE = Integer.parseInt(PropertyHandler.getProperty("error.PROMO_UCIP100_FAILURE"));
        GET_BALANCE_FAILURE = Integer.parseInt(PropertyHandler.getProperty("error.GET_BALANCE_FAILURE"));
        PROMO_NOT_ENOUGH_BALANCE = Integer.parseInt(PropertyHandler.getProperty("error.NOT_ENOUGH_BALANCE"));
        SVC_CALSS_ELIGIBILITY = Integer.parseInt(PropertyHandler.getProperty("error.SVC_CALSS_ELIGIBILITY"));
        REFUND_FAILED = Integer.parseInt(PropertyHandler.getProperty("error.REFUND_FAILED"));
        //SMS_NOTIFICATION_FAILED = Integer.parseInt(PropertyHandler.getProperty("error.SMS_NOTIFICATION_Failed"));
        OTHER_FAILURE = Integer.parseInt(PropertyHandler.getProperty("error.OTHER_FAILURE"));
        SUCCESS = Integer.parseInt(PropertyHandler.getProperty("success"));
        ALREADY_SUBSCRIBED = Integer.parseInt(PropertyHandler.getProperty("already_subscribed"));
        ALREADY_SUBSCRIBED_MAX_TIMES = Integer.parseInt(PropertyHandler.getProperty("already_subscribed_max_times"));
        NOT_OPTED_IN = Integer.parseInt(PropertyHandler.getProperty("not_opted_in"));
        UNKNOWN = Integer.parseInt(PropertyHandler.getProperty("unknown"));
       
        //SMSes
        PropertyHandler.initSMSTemplates();
        SMS_SUCCESS_AR = PropertyHandler.getSMS("success.sms.ar");
        //SMS_SUCCESS_100 = PropertyHandler.getSMS("success100.sms");
        //SMS_REFUND  = PropertyHandler.getSMS("refund.sms");
        SMS_FAILURE_AR = PropertyHandler.getSMS("failure.sms.ar");
        SMS_ALREADY_SUBSCRIBED_AR = PropertyHandler.getSMS("subscribed.sms.ar");
        SMS_NOT_ENOUGH_CREDIT_AR = PropertyHandler.getSMS("no_credit.sms.ar");
        SMS_NOT_ELIGIBLE_AR = PropertyHandler.getSMS("not_eligible.sms.ar");
        SMS_INVALID_CONTENT_AR = PropertyHandler.getSMS("invalid.sms.ar");

        SMS_SUCCESS_EN = PropertyHandler.getSMS("success.sms.en");
        //SMS_SUCCESS_100 = PropertyHandler.getSMS("success100.sms");
        //SMS_REFUND  = PropertyHandler.getSMS("refund.sms");
        SMS_FAILURE_EN = PropertyHandler.getSMS("failure.sms.en");
        SMS_ALREADY_SUBSCRIBED_EN = PropertyHandler.getSMS("subscribed.sms.en");
        SMS_NOT_ENOUGH_CREDIT_EN = PropertyHandler.getSMS("no_credit.sms.en");
        SMS_NOT_ELIGIBLE_EN = PropertyHandler.getSMS("not_eligible.sms.en");
        SMS_INVALID_CONTENT_EN = PropertyHandler.getSMS("invalid.sms.en");

        //Easy_SvcCls = new ArrayList();

        //Easy_SvcCls.add(new Integer(820));
        //Easy_SvcCls.add(new Integer(821));

     }

    public static int promo_DA_ID(int sc){ 
        
        String serviceClass = PropertyHandler.getProperty("promo.DedicatedAccountID.SC_"+String.valueOf(sc));
        if(serviceClass == null)
            serviceClass = PropertyHandler.getProperty("promo.DedicatedAccountID.Default");
          return new Integer(serviceClass);
    }
    public boolean validateParemeters(){

         if  (! ( PromoParameters.svcClassAction.equals(SvcClassAction.INCLUDE) ||
               PromoParameters.svcClassAction.equals(SvcClassAction.EXCLUDE) )){

            System.err.println("Unknow Service Class Action : '" + PromoParameters.svcClassAction + "' !");
            logger.error("Unknow Service Class Action : '" + PromoParameters.svcClassAction + "' !", null);
            return false;
        }

         if  (! ( PromoParameters.voucherGroupAction.equals(VoucherGroupAction.INCLUDE) ||
               PromoParameters.voucherGroupAction.equals(VoucherGroupAction.EXCLUDE) )){

            System.err.println("Unknow Voucher Group Action : '" + PromoParameters.voucherGroupAction + "' !");
            logger.error("Unknow Voucher Group Action : '" + PromoParameters.voucherGroupAction + "' !", null);
            return false;
        }

         if  (! ( PromoParameters.PROMO_ADJSUTED_VALUE_TYPE.equalsIgnoreCase(PROMO_VALUE_PERCENTAGE) ||
               PromoParameters.PROMO_ADJSUTED_VALUE_TYPE.equalsIgnoreCase(PROMO_VALUE_ABSOLUTE) )){

            System.err.println("Unknow Promo Value Type : '" + PromoParameters.PROMO_ADJSUTED_VALUE_TYPE + "' !");
            logger.error("Unknow Promo Value Type :  '" + PromoParameters.PROMO_ADJSUTED_VALUE_TYPE + "' !", null);
            return false;
         }

         return true;
    }


    public PromoParameters(){

    }

}
