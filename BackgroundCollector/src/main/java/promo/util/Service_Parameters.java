/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package promo.util;


//import interpromo.VoucherEnum;


/**
 *
 * @author anassar
 */
public class Service_Parameters {

    public static final String DB_Service_Details_Query_Check;
    public static  final  String DB_Service_Details_IP;
    public static  final  String DB_Service_Details_Port;
    public static  final  String DB_Service_Details_DB_Name;
    public static  final  String DB_Service_Details_UserName;
    public static  final  String DB_Service_Details_Password;
    public static  final  String DB_Service_Details_DB_Type;
    public static  final  String DB_Service_Details_Query;
    
    public static  final  String DB_AutoImport_IP;
    public static  final  String DB_AutoImport_Port;
    public static  final  String DB_AutoImport_DB_Name;
    public static  final  String DB_AutoImport_UserName;
    public static  final  String DB_AutoImport_Password;
    public static  final  String DB_AutoImport_DB_Type;

    public static final String LOG_BASE_DIR;
    public static final String iDELIM;
    public static final String oDELIM;
    
    public static final String COMMENT_MARK;
//    public static enum OptInChannels { USSD, SMS, IVR };

//    private static PromoLogger logger = new PromoLogger(Parameters.class);
    
     static {
         
       //Either fixed value promo or relative percentage
         PropertyHandler.initXmlProperties();
         //Fixed values or percentage value

          DB_Service_Details_IP=PropertyHandler.getProperty("DB_Service_Details_IP");
          DB_Service_Details_Port=PropertyHandler.getProperty("DB_Service_Details_Port");
          DB_Service_Details_DB_Name=PropertyHandler.getProperty("DB_Service_Details_DB_Name");
          DB_Service_Details_UserName=PropertyHandler.getProperty("DB_Service_Details_UserName");
          DB_Service_Details_Password=PropertyHandler.getProperty("DB_Service_Details_Password");
          DB_Service_Details_DB_Type=PropertyHandler.getProperty("DB_Service_Details_DB_Type");
          DB_Service_Details_Query=PropertyHandler.getProperty("DB_Service_Details_Query");
         DB_Service_Details_Query_Check=PropertyHandler.getProperty("DB_Service_Details_Query_Check");
         
         DB_AutoImport_IP=PropertyHandler.getProperty("DB_AutoImport_IP");
         DB_AutoImport_Port=PropertyHandler.getProperty("DB_AutoImport_Port");
         DB_AutoImport_DB_Name=PropertyHandler.getProperty("DB_AutoImport_DB_Name");
         DB_AutoImport_UserName=PropertyHandler.getProperty("DB_AutoImport_UserName");
         DB_AutoImport_Password=PropertyHandler.getProperty("DB_AutoImport_Password");
         DB_AutoImport_DB_Type=PropertyHandler.getProperty("DB_AutoImport_DB_Type");
         
        
         iDELIM = PropertyHandler.getProperty("log.iDELIM");
         oDELIM = PropertyHandler.getProperty("log.oDELIM");
         LOG_BASE_DIR  =  PropertyHandler.getProperty("log.path");
         COMMENT_MARK = "#";

     }
    public Service_Parameters(){

    }

}
