/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package promo.util;

import DBInterfaces.DBInterface;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;


public class Parameter_Setter {

    public String ServiceName;         
    public String DB_From_IP;
    public String DB_From_Port;
    public String DB_From_DB_Name;
    public String DB_From_UserName;
    public String DB_From_Password;
    public String DB_From_DB_Type;
    public String Quary_From;
    
    public String DB_To_IP;
    public String DB_To_Port;
    public String DB_To_DB_Name;
    public String DB_To_UserName;
    public String DB_To_Password;
    public String DB_To_DB_Type;
    public String Quary_To;

    public String Log_DB_Type;
    public String Log_DB_IP;
    public String Log_DB_DataBase;
    public String Log_DB_TableName;
    public String Log_DB_UserName;
    public String Log_DB_Password; 
    public String Log_DB_Port;  
    
    public String TableName;
    public String DateColumnName;
    public String NumberOfMaps;
    
    public ArrayList InsertMap=new ArrayList();
    public ArrayList ExportMap=new ArrayList();
    public ArrayList UnixShellScript=new ArrayList();
    
    public String WithDate_Insert;
    public String Command2BExecutedB4Insert;
    public String Command2BExecutedAfterInsert;
    public String WithStatementsLog;
    public String ExportToFile;
    public String ExportToFile_Delimiter;
    
    public String Replace_Ignore;
    public String Dont_LoadDataInFile;
    public String Remove_File;
    public String ManualExport;
    public String AutoImport;
    public String ForceImport;

    public   String LOG_BASE_DIR;
    public   String iDELIM;
    public   String oDELIM;
    
    public   String COMMENT_MARK;
    
     public Parameter_Setter(String ServiceName_DB,String ManualExportFlag){

//           System.out.println(ManualExportFlag);
        ManualExport=ManualExportFlag;   
        if (ManualExportFlag.compareToIgnoreCase("true")==0){
            ManualExport="True";
            AutoImport="False";
            
        }
        else{
        
         long wait_ = 1000;
         DBInterface dbInterface_Service_Details = new DBInterface(Service_Parameters.DB_Service_Details_DB_Type,"00:00");
         boolean status = dbInterface_Service_Details.connectToDB(Service_Parameters.DB_Service_Details_IP, 
                                                                    Service_Parameters.DB_Service_Details_Port, 
                                                                    Service_Parameters.DB_Service_Details_DB_Name, 
                                                                    Service_Parameters.DB_Service_Details_UserName, 
                                                                    Service_Parameters.DB_Service_Details_Password, 
                                                                    Service_Parameters.DB_Service_Details_DB_Type,
                                                                    wait_);
//        System.out.println(status);
         ResultSet rs;
//         String quaryFrom="Select * from "
        String DB_Service_Details_Query=Service_Parameters.DB_Service_Details_Query.replaceAll("%ServiceName_DB%",ServiceName_DB);
        
        try {
        
        rs=dbInterface_Service_Details.executeSQL_RS(Service_Parameters.DB_Service_Details_Query_Check.replaceAll("%ServiceName_DB%",ServiceName_DB));
        String Counts="";
        while (rs.next()){
            Counts=rs.getString("Count");
//            System.out.println(Counts);
            if (Counts.equalsIgnoreCase("0")){
                System.out.println("Sorry there is no service with the mentioned service name ("+ServiceName_DB+")!");
                System.exit(0);
            }
        }
        rs.close();
        
        rs=dbInterface_Service_Details.executeSQL_RS(DB_Service_Details_Query);
        
        
        while (rs.next()) {
            DB_From_IP=rs.getString("DB_From_IP");
            
            ServiceName=rs.getString("ServicePath");
            DB_From_Port=rs.getString("DB_From_Port");
            DB_From_DB_Name=rs.getString("DB_From_DB_Name");
            DB_From_UserName=rs.getString("DB_From_UserName");
            DB_From_Password=rs.getString("DB_From_Password");
            DB_From_DB_Type=rs.getString("DB_From_DB_Type");
            Quary_From=rs.getString("Quary_From");
            
            DB_To_IP=rs.getString("DB_To_IP");
            DB_To_Port=rs.getString("DB_To_Port");
            DB_To_DB_Name=rs.getString("DB_To_DB_Name");
            DB_To_UserName=rs.getString("DB_To_UserName");
            DB_To_Password=rs.getString("DB_To_Password");
            DB_To_DB_Type=rs.getString("DB_To_DB_Type");
            
                    
            TableName=rs.getString("TableName");
            DateColumnName=rs.getString("DateColumnName");
            //         NumberOfMaps=rs.getString("NumberOfMaps");
            Remove_File=rs.getString("Remove_File");
            AutoImport=rs.getString("AutoImport");

            WithDate_Insert=rs.getString("WithDate_Insert");
            Command2BExecutedB4Insert=rs.getString("Command2BExecutedB4Insert");
            Command2BExecutedAfterInsert=rs.getString("Command2BExecutedAfterInsert");
            WithStatementsLog=rs.getString("WithStatementsLog");
            ExportToFile=rs.getString("ExportToFile");
            ExportToFile_Delimiter=rs.getString("ExportToFile_Delimiter");
            ForceImport=rs.getString("ForceImport");
            
            int j=1;
            String newMap=rs.getString("InsertMap"+String.valueOf(j));

            while (j<=69){
                if (newMap!=null && !newMap.equalsIgnoreCase("null") && !newMap.equalsIgnoreCase("xxx"))  {
                    InsertMap.add(newMap);
                }
                j++;
                newMap=rs.getString("InsertMap"+String.valueOf(j));
        //            System.out.println(newMap);
                };

            Log_DB_IP=rs.getString("Log_DB_IP");
            Log_DB_DataBase=rs.getString("Log_DB_DataBase");
            Log_DB_TableName=rs.getString("Log_DB_TableName");
            Log_DB_Type=rs.getString("Log_DB_Type");
            Log_DB_Port=rs.getString("Log_DB_Port");
            Log_DB_UserName=rs.getString("Log_DB_UserName");
            Log_DB_Password=rs.getString("Log_DB_Password");
            
            Replace_Ignore=rs.getString("Replace_Ignore");
            Dont_LoadDataInFile=rs.getString("Dont_LoadDataInFile");
//            System.out.println(Dont_LoadDataInFile);
            j=1;
            newMap=rs.getString("ExportMap"+String.valueOf(j));
            while (j<=69){
                if (newMap!=null && !newMap.equalsIgnoreCase("null") && !newMap.equalsIgnoreCase("xxx"))  {
                    ExportMap.add(newMap);
                }
                j++;
                newMap=rs.getString("ExportMap"+String.valueOf(j));
            }
            
            j=1;
            newMap=rs.getString("UnixShellScript"+String.valueOf(j));
            while (j<=9){
               if (newMap!=null && !newMap.equalsIgnoreCase("null") && !newMap.equalsIgnoreCase("xxx"))  {
                   UnixShellScript.add(newMap);
               }
               j++;
               newMap=rs.getString("UnixShellScript"+String.valueOf(j));
               }
        }
             
         } catch (SQLException e) {
             // TODO
             System.out.println("Error:"+e.getMessage());
         }
     }
     }

    public void setServiceName(String serviceName) {
        this.ServiceName = serviceName;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public void setDB_From_IP(String dB_From_IP) {
        this.DB_From_IP = dB_From_IP;
    }

    public String getDB_From_IP() {
        return DB_From_IP;
    }

    public void setDB_From_Port(String dB_From_Port) {
        this.DB_From_Port = dB_From_Port;
    }

    public String getDB_From_Port() {
        return DB_From_Port;
    }

    public void setDB_From_DB_Name(String dB_From_DB_Name) {
        this.DB_From_DB_Name = dB_From_DB_Name;
    }

    public String getDB_From_DB_Name() {
        return DB_From_DB_Name;
    }

    public void setDB_From_UserName(String dB_From_UserName) {
        this.DB_From_UserName = dB_From_UserName;
    }

    public String getDB_From_UserName() {
        return DB_From_UserName;
    }

    public void setDB_From_Password(String dB_From_Password) {
        this.DB_From_Password = dB_From_Password;
    }

    public String getDB_From_Password() {
        return DB_From_Password;
    }

    public void setDB_From_DB_Type(String dB_From_DB_Type) {
        this.DB_From_DB_Type = dB_From_DB_Type;
    }

    public String getDB_From_DB_Type() {
        return DB_From_DB_Type;
    }

    public void setQuary_From(String quary_From) {
        this.Quary_From = quary_From;
    }

    public String getQuary_From() {
        return Quary_From;
    }

    public void setDB_To_IP(String dB_To_IP) {
        this.DB_To_IP = dB_To_IP;
    }

    public String getDB_To_IP() {
        return DB_To_IP;
    }

    public void setDB_To_Port(String dB_To_Port) {
        this.DB_To_Port = dB_To_Port;
    }

    public String getDB_To_Port() {
        return DB_To_Port;
    }

    public void setDB_To_DB_Name(String dB_To_DB_Name) {
        this.DB_To_DB_Name = dB_To_DB_Name;
    }

    public String getDB_To_DB_Name() {
        return DB_To_DB_Name;
    }

    public void setDB_To_UserName(String dB_To_UserName) {
        this.DB_To_UserName = dB_To_UserName;
    }

    public String getDB_To_UserName() {
        return DB_To_UserName;
    }

    public void setDB_To_Password(String dB_To_Password) {
        this.DB_To_Password = dB_To_Password;
    }

    public String getDB_To_Password() {
        return DB_To_Password;
    }

    public void setDB_To_DB_Type(String dB_To_DB_Type) {
        this.DB_To_DB_Type = dB_To_DB_Type;
    }

    public String getDB_To_DB_Type() {
        return DB_To_DB_Type;
    }

    public void setQuary_To(String quary_To) {
        this.Quary_To = quary_To;
    }

    public String getQuary_To() {
        return Quary_To;
    }

    public void setLog_DB_Type(String log_DB_Type) {
        this.Log_DB_Type = log_DB_Type;
    }

    public String getLog_DB_Type() {
        return Log_DB_Type;
    }

    public void setLog_DB_IP(String log_DB_IP) {
        this.Log_DB_IP = log_DB_IP;
    }

    public String getLog_DB_IP() {
        return Log_DB_IP;
    }

    public void setLog_DB_DataBase(String log_DB_DataBase) {
        this.Log_DB_DataBase = log_DB_DataBase;
    }

    public String getLog_DB_DataBase() {
        return Log_DB_DataBase;
    }

    public void setLog_DB_TableName(String log_DB_TableName) {
        this.Log_DB_TableName = log_DB_TableName;
    }

    public String getLog_DB_TableName() {
        return Log_DB_TableName;
    }

    public void setLog_DB_UserName(String log_DB_UserName) {
        this.Log_DB_UserName = log_DB_UserName;
    }

    public String getLog_DB_UserName() {
        return Log_DB_UserName;
    }

    public void setLog_DB_Password(String log_DB_Password) {
        this.Log_DB_Password = log_DB_Password;
    }

    public String getLog_DB_Password() {
        return Log_DB_Password;
    }

    public void setLog_DB_Port(String log_DB_Port) {
        this.Log_DB_Port = log_DB_Port;
    }

    public String getLog_DB_Port() {
        return Log_DB_Port;
    }

    public void setTableName(String tableName) {
        this.TableName = tableName;
    }

    public String getTableName() {
        return TableName;
    }

    public void setDateColumnName(String dateColumnName) {
        this.DateColumnName = dateColumnName;
    }

    public String getDateColumnName() {
        return DateColumnName;
    }

    public void setNumberOfMaps(String numberOfMaps) {
        this.NumberOfMaps = numberOfMaps;
    }

    public String getNumberOfMaps() {
        return NumberOfMaps;
    }

    public void setInsertMap(ArrayList insertMap) {
        this.InsertMap = insertMap;
    }

    public ArrayList getInsertMap() {
        return InsertMap;
    }

    public void setExportMap(ArrayList exportMap) {
        this.ExportMap = exportMap;
    }

    public ArrayList getExportMap() {
        return ExportMap;
    }

    public void setWithDate_Insert(String withDate_Insert) {
        this.WithDate_Insert = withDate_Insert;
    }

    public String getWithDate_Insert() {
        return WithDate_Insert;
    }

    public void setCommand2BExecutedB4Insert(String command2BExecutedB4Insert) {
        this.Command2BExecutedB4Insert = command2BExecutedB4Insert;
    }

    public String getCommand2BExecutedB4Insert() {
        return Command2BExecutedB4Insert;
    }

    public void setWithStatementsLog(String withStatementsLog) {
        this.WithStatementsLog = withStatementsLog;
    }

    public String getWithStatementsLog() {
        return WithStatementsLog;
    }

    public void setExportToFile(String exportToFile) {
        this.ExportToFile = exportToFile;
    }

    public String getExportToFile() {
        return ExportToFile;
    }

    public void setExportToFile_Delimiter(String exportToFile_Delimiter) {
        this.ExportToFile_Delimiter = exportToFile_Delimiter;
    }

    public String getExportToFile_Delimiter() {
        return ExportToFile_Delimiter;
    }

    public void setReplace_Ignore(String replace_Ignore) {
        this.Replace_Ignore = replace_Ignore;
    }

    public String getReplace_Ignore() {
        return Replace_Ignore;
    }

    public void setDont_LoadDataInFile(String dont_LoadDataInFile) {
        this.Dont_LoadDataInFile = dont_LoadDataInFile;
    }

    public String getDont_LoadDataInFile() {
        return Dont_LoadDataInFile;
    }

    public void setRemove_File(String remove_File) {
        this.Remove_File = remove_File;
    }

    public String getRemove_File() {
        return Remove_File;
    }

    public void setLOG_BASE_DIR(String lOG_BASE_DIR) {
        this.LOG_BASE_DIR = lOG_BASE_DIR;
    }

    public String getLOG_BASE_DIR() {
        return LOG_BASE_DIR;
    }

    public void setIDELIM(String iDELIM) {
        this.iDELIM = iDELIM;
    }

    public String getIDELIM() {
        return iDELIM;
    }

    public void setODELIM(String oDELIM) {
        this.oDELIM = oDELIM;
    }

    public String getODELIM() {
        return oDELIM;
    }

    public void setCOMMENT_MARK(String cOMMENT_MARK) {
        this.COMMENT_MARK = cOMMENT_MARK;
    }

    public String getCOMMENT_MARK() {
        return COMMENT_MARK;
    }

    public void setUnixShellScript(ArrayList unixShellScript) {
        this.UnixShellScript = unixShellScript;
    }

    public ArrayList getUnixShellScript() {
        return UnixShellScript;
    }

    public void setManualExport(String manualExport) {
        this.ManualExport = manualExport;
    }

    public String getManualExport() {
        return ManualExport;
    }

    public void setAutoImport(String autoImport) {
        this.AutoImport = autoImport;
    }

    public String getAutoImport() {
        return AutoImport;
    }

    public void setCommand2BExecutedAfterInsert(String command2BExecutedAfterInsert) {
        this.Command2BExecutedAfterInsert = command2BExecutedAfterInsert;
    }

    public String getCommand2BExecutedAfterInsert() {
        return Command2BExecutedAfterInsert;
    }

    public void setForceImport(String forceImport) {
        this.ForceImport = forceImport;
    }

    public String getForceImport() {
        return ForceImport;
    }
}
