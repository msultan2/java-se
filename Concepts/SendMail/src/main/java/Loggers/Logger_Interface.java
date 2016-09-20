package Loggers;

import DBInterfaces.DBInterface;

import Date_Functions.DateUtils;

import Parameters_Conf.Parameters;

import java.io.File;

import java.sql.SQLException;


public class Logger_Interface {
    public Logger_Interface() {
    }

    public void Log(String Desc,String Start_Time){
            DateUtils dateUtils = new DateUtils();
            String date = dateUtils.now("yyyy-MM-dd");
            String End_Time = dateUtils.now("hh:mm:ss");
            File directory = new File (".");
            String ServicePath="";
            try {
            ServicePath=directory.getCanonicalPath(); 
            }catch(Exception e) {
            System.out.println("Exception is ="+e.getMessage());
            ServicePath=Parameters.ServiceName;
             }

            long wait_ = 1000;
            DBInterface Logger = new DBInterface(Parameters.Log_DB_Type,Start_Time);
            boolean Log_Status=
                    Logger.connectToDB(Parameters.Log_DB_IP,Parameters.Log_DB_Port , Parameters.Log_DB_DataBase, 
                                                 Parameters.Log_DB_UserName, Parameters.Log_DB_Password, Parameters.Log_DB_Type,wait_);
System.out.println(Parameters.ServiceName);

            String Insert_Statement="INSERT INTO Java_Logs(Date,Start_Time,AppName,`Desc`,End_Time) VALUES('"+
                                                            date+"','"+Start_Time +"','"+ServicePath+"','"+Desc.replaceAll("'","")+"','"+End_Time+"')";
System.out.println(Insert_Statement);
System.out.println(Desc.replaceAll("'",""));
//            ResultSet rs;
            try {
                Logger.executeSQL(Insert_Statement,true);
            } catch (SQLException e) {
                // TODO
                 System.out.println("Logger Error: " + e.getErrorCode() + "   " + 
                                    e.getMessage());
//                Logger.Log(e.getMessage(),StartTime);
            }
        }
}