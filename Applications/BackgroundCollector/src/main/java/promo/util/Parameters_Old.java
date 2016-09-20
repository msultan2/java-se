/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package promo.util;

import java.util.ArrayList;


//import interpromo.VoucherEnum;


/**
 *
 * @author anassar
 */
public class Parameters_Old {

    public static  final  String ServiceName;
   
    public static  final  String DB_From_IP;
    public static  final  String DB_From_Port;
    public static  final  String DB_From_DB_Name;
    public static  final  String DB_From_UserName;
    public static  final  String DB_From_Password;
    public static  final  String DB_From_DB_Type;
    public static  final  String Quary_From;
        
    public static  final  String DB_To_IP;
    public static  final  String DB_To_Port;
    public static  final  String DB_To_DB_Name;
    public static  final  String DB_To_UserName;
    public static  final  String DB_To_Password;
    public static  final  String DB_To_DB_Type;
    public static  final  String Quary_To;

    
    public static  final  String Log_DB_Type;
    public static  final  String Log_DB_IP;
    public static  final  String Log_DB_DataBase;
    public static  final  String Log_DB_TableName;
    public static  final  String Log_DB_UserName;
    public static  final  String Log_DB_Password; 
    public static  final  String Log_DB_Port;  
//    public static  final  String DB_To_IP2;
//    public static  final  String DB_To_Port2;
//    public static  final  String DB_To_DB_Name2;
//    public static  final  String DB_To_UserName2;
//    public static  final  String DB_To_Password2;
//    public static  final  String DB_To_DB_Type2;
    
    public static  final  String TableName;
    public static  final  String DateColumnName;
    public static  final  String NumberOfMaps;
    public static  final  ArrayList InsertMap=new ArrayList();
    //public static  final  ArrayList SetMap=new ArrayList();
    public static  final  ArrayList ExportMap=new ArrayList();
    //public static  final  ArrayList WhereMap=new ArrayList();
    public static  final  String WithDate_Insert;
    //public static  final  String WithDate_Where;
    public static  final  String Command2BExecutedB4Insert;
    public static  final  String WithStatementsLog;
    public static  final  String ExportToFile;
    public static  final  String ExportToFile_Delimiter;
    
    public static  final  String Replace_Ignore;
    public static  final  String Dont_LoadDataInFile;
    public static  final  String Remove_File;

//    public static  final  String Map2;
//    public static  final  String Map3;
//    public static  final  String Map4;
//    public static  final  String Map5;
//    public static  final  String Map6;
//    public static  final  String Map7;
//    public static  final  String Map8;
//    public static  final  String Map9;
//    public static  final  String Map10;


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

          ServiceName=PropertyHandler.getProperty("ServiceName");

          DB_From_IP=PropertyHandler.getProperty("DB_From_IP");
          DB_From_Port=PropertyHandler.getProperty("DB_From_Port");
          DB_From_DB_Name=PropertyHandler.getProperty("DB_From_DB_Name");
          DB_From_UserName=PropertyHandler.getProperty("DB_From_UserName");
          DB_From_Password=PropertyHandler.getProperty("DB_From_Password");
          DB_From_DB_Type=PropertyHandler.getProperty("DB_From_DB_Type");
          Quary_From=PropertyHandler.getProperty("Quary_From");

          DB_To_IP=PropertyHandler.getProperty("DB_To_IP");
          DB_To_Port=PropertyHandler.getProperty("DB_To_Port");
          DB_To_DB_Name=PropertyHandler.getProperty("DB_To_DB_Name");
          DB_To_UserName=PropertyHandler.getProperty("DB_To_UserName");
          DB_To_Password=PropertyHandler.getProperty("DB_To_Password");
          DB_To_DB_Type=PropertyHandler.getProperty("DB_To_DB_Type");
          Quary_To=PropertyHandler.getProperty("Quary_To");
          
//         DB_To_IP2=PropertyHandler.getProperty("DB_To_IP2");
//         DB_To_Port2=PropertyHandler.getProperty("DB_To_Port2");
//         DB_To_DB_Name2=PropertyHandler.getProperty("DB_To_DB_Name2");
//         DB_To_UserName2=PropertyHandler.getProperty("DB_To_UserName2");
//         DB_To_Password2=PropertyHandler.getProperty("DB_To_Password2");
//         DB_To_DB_Type2=PropertyHandler.getProperty("DB_To_DB_Type2");
            
         TableName=PropertyHandler.getProperty("TableName");
         DateColumnName=PropertyHandler.getProperty("DateColumnName");
         NumberOfMaps=PropertyHandler.getProperty("NumberOfMaps");
         Remove_File=PropertyHandler.getProperty("Remove_File");
         
         //WithDate_Where=PropertyHandler.getProperty("WithDate_Where");
         WithDate_Insert=PropertyHandler.getProperty("WithDate_Insert");
         Command2BExecutedB4Insert=PropertyHandler.getProperty("Command2BExecutedB4Insert");
         WithStatementsLog=PropertyHandler.getProperty("WithStatementsLog");
         ExportToFile=PropertyHandler.getProperty("ExportToFile");
         ExportToFile_Delimiter=PropertyHandler.getProperty("ExportToFile_Delimiter");
         
         int j=1;
         String newMap=PropertyHandler.getProperty("InsertMap"+String.valueOf(j));
         
         while (newMap!=null && newMap!="" && newMap.length()>0){
             InsertMap.add(newMap);
             j++;
            newMap=PropertyHandler.getProperty("InsertMap"+String.valueOf(j));
            };
            
         j=1;
         
         Log_DB_IP=PropertyHandler.getProperty("Log_DB_IP");
         Log_DB_DataBase=PropertyHandler.getProperty("Log_DB_DataBase");
         Log_DB_TableName=PropertyHandler.getProperty("Log_DB_TableName");
         Log_DB_Type=PropertyHandler.getProperty("Log_DB_Type");
         Log_DB_Port=PropertyHandler.getProperty("Log_DB_Port");
         Log_DB_UserName=PropertyHandler.getProperty("Log_DB_UserName");
         Log_DB_Password=PropertyHandler.getProperty("Log_DB_Password");
         
         Replace_Ignore=PropertyHandler.getProperty("Replace_Ignore");
         Dont_LoadDataInFile=PropertyHandler.getProperty("Dont_LoadDataInFile");
         
         j=1;
         newMap=PropertyHandler.getProperty("ExportMap"+String.valueOf(j));
         
         while (newMap!=null && newMap!="" && newMap.length()>0){
             ExportMap.add(newMap);
             j++;
            newMap=PropertyHandler.getProperty("ExportMap"+String.valueOf(j));
            }
        
         iDELIM = PropertyHandler.getProperty("log.iDELIM");
         oDELIM = PropertyHandler.getProperty("log.oDELIM");
         LOG_BASE_DIR  =  PropertyHandler.getProperty("log.path");
         COMMENT_MARK = "#";

     }
    public Parameters_Old(){

    }

}
