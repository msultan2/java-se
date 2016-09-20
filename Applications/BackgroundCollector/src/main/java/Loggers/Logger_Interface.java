package Loggers;

import Collectors.Stats_Collector;

import DBInterfaces.DBInterface;

import Date_Functions.DateUtils;

import java.sql.SQLException;


public class Logger_Interface {
    public Logger_Interface() {
    }

    public void LogX(String Desc,String Start_Time){}
            
 public void Log(String Desc,String Start_Time,String LogType){
         DateUtils dateUtils = new DateUtils();
         String date = dateUtils.now("yyyy-MM-dd");
         String End_Time = dateUtils.now("hh:mm:ss");
//         File directory = new File (".");
//         String ServicePath=Parameters.ServiceName;
         try {
//         ServicePath=directory.getCanonicalPath(); 
//         ServicePath=Parameters.ServiceName;
         }catch(Exception e) {
         System.out.println("Exception in Logger Interface:"+e.getMessage());
          }

         long wait_ = 1000;
         DBInterface Logger = new DBInterface(Stats_Collector.Parameters.Log_DB_Type,Start_Time);
         boolean Log_Status=
                 Logger.connectToDB(Stats_Collector.Parameters.Log_DB_IP,Stats_Collector.Parameters.Log_DB_Port , Stats_Collector.Parameters.Log_DB_DataBase, 
                                              Stats_Collector.Parameters.Log_DB_UserName, Stats_Collector.Parameters.Log_DB_Password, Stats_Collector.Parameters.Log_DB_Type,wait_);
 //System.out.println(Parameters.ServiceName);

         String Insert_Statement="INSERT INTO Java_Logs(Date,Start_Time,AppName,`Desc`,End_Time,Type,Day) VALUES('"+
                                                         date+"','"+Start_Time +"','"+Stats_Collector.Parameters.ServiceName+"','"+Desc.replaceAll("'","")+"','"+End_Time+"','"+LogType+"',day('"+date+"'))";
// System.out.println(Insert_Statement);
// System.out.println(Desc.replaceAll("'",""));
 //            ResultSet rs;
         try {
             Logger.executeSQL(Insert_Statement,true);
         } catch (SQLException e) {
              System.out.println("Logger Error: " + e.getErrorCode() + "   " + 
                                 e.getMessage());
 //                Logger.Log(e.getMessage(),StartTime);
         }
     }
  
}