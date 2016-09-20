 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */

 package Parameters_Conf;


public class Parameters {

     public static  final  String ServiceName;
    
     public static  final  String DB_IP;
     public static  final  String DB_Port;
     public static  final  String DB_Name;
     public static  final  String DB_UserName;
     public static  final  String DB_Password;
     public static  final  String DB_Type;

     public static final String DB_TableName;
     public static final String DB_DBName;
     
     public static final String MailHost;
     public static final String MailAccount;
     public static final String MailPort;
     public static final String MailPop3_Link;
     public static final String MailUser_auth;
     public static final String MailPassword;
     
     public static final String iDELIM;
     public static final String oDELIM;
     public static final String MailBody;
     public static final String LOG_BASE_DIR;
     
     
     public static final String Log_DB_IP;
     public static final String Log_DB_Type;
     public static final String Log_DB_Port;
     public static final String Log_DB_UserName;
     public static final String Log_DB_Password;
     public static final String Log_DB_DataBase;
     public static final String Log_DB_TableName;
     
     public static final String COMMENT_MARK;
 //    public static enum OptInChannels { USSD, SMS, IVR };

     private static PromoLogger logger = new PromoLogger(Parameters.class);
     
      static {
          
        //Either fixed value promo or relative percentage
          PropertyHandler.initXmlProperties();
          //Fixed values or percentage value

           ServiceName=PropertyHandler.getProperty("ServiceName");

           DB_IP=PropertyHandler.getProperty("DB_IP");
           DB_Port=PropertyHandler.getProperty("DB_Port");
           DB_Name=PropertyHandler.getProperty("DB_Name");
           DB_UserName=PropertyHandler.getProperty("DB_UserName");
           DB_Password=PropertyHandler.getProperty("DB_Password");
           DB_Type=PropertyHandler.getProperty("DB_Type");
           
         DB_TableName=PropertyHandler.getProperty("DB_TableName");
         DB_DBName=PropertyHandler.getProperty("DB_DBName");
         MailHost=PropertyHandler.getProperty("MailHost");
         MailAccount=PropertyHandler.getProperty("MailAccount");
         MailPort=PropertyHandler.getProperty("MailPort");
         MailPop3_Link=PropertyHandler.getProperty("MailPop3_Link");
         MailUser_auth=PropertyHandler.getProperty("MailUser_auth");
         MailBody=PropertyHandler.getProperty("MailBody");
          MailPassword=PropertyHandler.getProperty("MailPassword");
         
          Log_DB_IP=PropertyHandler.getProperty("Log_DB_IP");
          Log_DB_Type=PropertyHandler.getProperty("Log_DB_Type");
          Log_DB_Port=PropertyHandler.getProperty("Log_DB_Port");
          Log_DB_UserName=PropertyHandler.getProperty("Log_DB_UserName");
          Log_DB_Password=PropertyHandler.getProperty("Log_DB_Password");
          Log_DB_DataBase=PropertyHandler.getProperty("Log_DB_DataBase");
          Log_DB_TableName=PropertyHandler.getProperty("Log_DB_TableName");
          
          iDELIM=PropertyHandler.getProperty("iDELIM");
          oDELIM=PropertyHandler.getProperty("oDELIM");
          
          LOG_BASE_DIR  =  PropertyHandler.getProperty("log.path");
          COMMENT_MARK = "#";


      }

     public static int promo_DA_ID(int sc){ 
         
         String serviceClass = PropertyHandler.getProperty("promo.DedicatedAccountID.SC_"+String.valueOf(sc));
         if(serviceClass == null)
             serviceClass = PropertyHandler.getProperty("promo.DedicatedAccountID.Default");
           return new Integer(serviceClass);
     }
     public Parameters(){

     }

 }
