package Collectors;

import DBInterfaces.DBInterface;

import Date_Functions.DateUtils;

import Loggers.Logger_Interface;

import Unix_Executer.Unix_Execute;

import drs.ColumnMaps;
import drs.Statements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import promo.util.Parameter_Setter;
import promo.util.Service_Parameters;

import xml_parser.CopyTextFile;


public class Stats_Collector {
    private DBInterface dbInterface_from;
    private DBInterface dbInterface_To;
    private DBInterface dbInterface_Importer;
    
//    private DBInterface dbInterface_Logger = new DBInterface(Parameters.getLog_DB_Type,);
//    public DateUtils dateUtils = new DateUtils();
    
    public String StartTime=new String();
    public Logger_Interface Logger = new Logger_Interface();
   public static Parameter_Setter Parameters; 
//    private DBInterface dbInterface_To2 = new DBInterface(Parameters.getDB_To_DB_Type);
    private Statements generateStatements(ArrayList InsertMap,String dateYYYYMMDD,ResultSet rs){
        //Creating Insert Statement
        String insertStatement=new String();
        String updateStatement=new String();
        
        Iterator it = InsertMap.iterator();
        String to="";
        String values = "";
        
        if (Parameters.getWithDate_Insert().compareToIgnoreCase("true")==0){
            to=Parameters.getDateColumnName();
            values = "'" + dateYYYYMMDD + "'";
        }
        
        String bothColumns="";
        ColumnMaps columnMaps;
        while (it.hasNext()){
            bothColumns=(String)it.next();
            columnMaps=splitColumns(bothColumns);
            try {
                if (to.length()==0 || to=="" || to.compareToIgnoreCase("")==0){
                    to=columnMaps.getColumTo();
                    values="'"+rs.getString(columnMaps.getColumnFrom())+"'";
                }
                else{
                     to=to+","+columnMaps.getColumTo();
                    values=values+",'"+rs.getString(columnMaps.getColumnFrom())+"'";
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
                Logger.Log(e.getMessage(),StartTime,"Error");
            }
//            i++;
        }
        
        if (Parameters.getReplace_Ignore().compareToIgnoreCase("ignore")==0)
            {    
            insertStatement = 
                "INSERT IGNORE INTO "+ Parameters.getTableName()+ "(" + to + ") " + 
                "VALUES ("  + values + ")";
            }
        else if (Parameters.getReplace_Ignore().compareToIgnoreCase("replace")==0)
            {
                insertStatement = 
                "REPLACE INTO "+ Parameters.getTableName()+ "(" + to + ") " + 
                "VALUES ("  + values + ")";
//                LoadDataInFile="LOAD DATA INFILE '/log/Shared/"+Parameters.getServiceName()+"_"+ dateYYYYMMDD+"' REPLACE INTO TABLE "+Parameters.getTableName()+" FIELDS TERMINATED BY '"+Parameters.getExportToFile()_Delimiter+"' ("+to+")";
//                System.out.println(LoadDataInFile);
            }
                else {
                        insertStatement = 
                        "INSERT INTO "+ Parameters.getTableName()+ "(" + to + ") " + 
                        "VALUES ("  + values + ")";
                    }            
        
        Statements statements=new Statements();
        statements.setInsertStatement(insertStatement);
        statements.setUpdateStatement(updateStatement);
//        statements.setLoadDataInFile(LoadDataInFile);
        
        return statements;
    }
    private ColumnMaps splitColumns(String both){
//        System.out.println(both);
        ColumnMaps columnMaps = new ColumnMaps();
        int loc=both.indexOf("|");
        
        columnMaps.setColumnFrom(both.substring(0,loc));
        columnMaps.setColumTo(both.substring(loc+1));
        return columnMaps;
    }
    private void addData(String insertStatement, String updateStatement){
        try {
            dbInterface_To.executeSQL(insertStatement,false);
        } catch (SQLException e) {
             try {
                if (e.getErrorCode()==1062 || e.getErrorCode()==2627){
                 dbInterface_To.executeSQL(updateStatement, 
                                           false);
                }
                else System.out.println("Error: " +e.getErrorCode() + "   " + 
                                        e.getMessage());
                 Logger.Log(e.getMessage(),StartTime,"Error");
             } catch (SQLException f) {
                     System.out.println("Error: " +f.getErrorCode() + "   " + 
                                        f.getMessage());
                Logger.Log(e.getMessage(),StartTime,"Error");
        
    }
    }
    }

    public Stats_Collector(String dateYYYYMMDD,String StartTime_IN,Parameter_Setter Parameters_) {
        Parameters=Parameters_;
        
        System.out.println(Parameters.getDB_From_DB_Type());
        dbInterface_from = new DBInterface(Parameters.getDB_From_DB_Type(),"00:00");
        
        if (Parameters.ExportToFile.compareToIgnoreCase("True")!=0){
            dbInterface_To = new DBInterface(Parameters.getDB_To_DB_Type(),"00:00");
        }
        StartTime=StartTime_IN;
        System.out.println("Stats Collector Version 8.3 (Manual Import, Auto Import, Forced Import, UTF8)");
        String Delimiter=new String();
        String Tos=new String();

        boolean status,status2 = false;
        long wait_ = 1000;
//        System.out.println("Connecting to "+Parameters.getDB_From_IP()+Parameters.getDB_From_Port()+ Parameters.getDB_From_DB_Name()+ 
//                                             Parameters.getDB_From_UserName()+ Parameters.getDB_From_Password()+ Parameters.getDB_From_DB_Type()+wait_);
        System.out.println("Connecting To From DB:"+Parameters.getDB_From_IP());
        status = 
                dbInterface_from.connectToDB(Parameters.getDB_From_IP(), Parameters.getDB_From_Port(), Parameters.getDB_From_DB_Name(), 
                                             Parameters.getDB_From_UserName(), Parameters.getDB_From_Password(), Parameters.getDB_From_DB_Type(),wait_);
              
//              System.out.println(status);
        if (!status) {
            System.out.println("Can't connect to from DB");
            Logger.Log("Can't connect to from DB",StartTime,"Error");
            System.exit(0);
        }
        
        System.out.println("Connected to FromDB:"+Parameters.getDB_From_IP());
        CopyTextFile CopyTextFileObj = new CopyTextFile();
        
        
//        CopyTextFileObj.log(Parameters.getServiceName()+".log",Parameters.getServiceName()+ ": Connection to From DB (" + Parameters.getDB_From_IP() + ")is " + status,true);
        Logger.Log("Connection to From DB (" + Parameters.getDB_From_IP() + ")is " + status,StartTime,"Info");
        
            if (Parameters.getExportToFile() !=null && Parameters.getExportToFile().compareToIgnoreCase("true")==0) 
                {status=true;}
            else{
                System.out.println("Connecting to ToDB:"+Parameters.getDB_To_IP());
                status = 
                        dbInterface_To.connectToDB(Parameters.getDB_To_IP(), Parameters.getDB_To_Port(), Parameters.getDB_To_DB_Name(), 
                                                   Parameters.getDB_To_UserName(), Parameters.getDB_To_Password(), 
                                                   Parameters.getDB_To_DB_Type(),wait_);
                }
        
        if (!status) {
            System.out.println("Can't connect to To DB");
            Logger.Log("Can't connect to ToDB",StartTime,"Error");
            System.exit(0);
        }
        else {
                System.out.println("Connected to ToDB:"+Parameters.getDB_To_IP());
            }
        
//        CopyTextFileObj.log(Parameters.getServiceName()+".log",Parameters.getServiceName()+ ": Connection to To DB (" + Parameters.getDB_To_IP() + ")is " + status,true);
        Logger.Log("Connection to To DB (" + Parameters.getDB_To_IP() + ")is " + status,StartTime,"Info");
        
        DateUtils dateUtils = new DateUtils();

        ResultSet rs;
        boolean HasResultSet=false;

        try {
//            CopyTextFileObj.log(Parameters.getServiceName()+".log",Parameters.getServiceName()+ ": "+dateUtils.now("yyyy-MM-dd HH:mm:ss") + " Getting "+Parameters.getServiceName()+" Stats",true);
            Logger.Log("Getting Stats",StartTime,"Info");
//            rs = 
//                 dbInterface_from.executeSQL_RS("select service,sum(NCalls) Calls,sum(nunsucc) UnSucceeded,sum(nansw) Answered " + 
//                                                "from IVR_Services " + 
//                                                "where CONVERT(VARCHAR(10), time, 120)='" +dateYYYYMMDD+ "' " + 
//                                                "group by service");
            String quaryFrom = new String();
            quaryFrom=Parameters.getQuary_From().replaceAll("dateYYYY-MM-DD", dateYYYYMMDD.replaceAll("/","-"));
//            if (Parameters.getQuary_From.contains("dateYYYY-MM-DD")) quaryFrom=Parameters.getQuary_From.replaceAll("dateYYYY-MM-DD", dateYYYYMMDD.replaceAll("/","-"));
            if (Parameters.getQuary_From().contains("dateYYYY/MM/DD")) quaryFrom=Parameters.getQuary_From().replaceAll("dateYYYY/MM/DD", dateYYYYMMDD.replaceAll("-","/"));

//            quaryFrom = Parameters.getQuary_From.replaceAll("dateYYYY/MM/DD",dateYYYYMMDD);
            
            
            if (Parameters.getWithStatementsLog().compareToIgnoreCase("true")==0) CopyTextFileObj.log(Parameters.getServiceName()+"_Statements.log",Parameters.getServiceName()+ ": "+quaryFrom,true);
            
//            System.out.println(quaryFrom);
            System.out.println("Executing Query:"+quaryFrom);
            rs=dbInterface_from.executeSQL_RS(quaryFrom);
            System.out.println("Query is Done");
//            rs.last(); 
//            int size = rs.getRow(); 
//            rs.beforeFirst();
            
            int step = 0;
//            CopyTextFileObj.log(Parameters.getServiceName()+".log",Parameters.getServiceName()+ ": "+ dateUtils.now("yyyy-MM-dd HH:mm:ss") + " Got "+Parameters.getServiceName()+" Stats",true);
//            CopyTextFileObj.log(Parameters.getServiceName()+".log",Parameters.getServiceName()+ ": "+ dateUtils.now("yyyy-MM-dd HH:mm:ss") + " Inserting/Updating Stats",true);
            Logger.Log("Got Stats",StartTime,"Info");
            Logger.Log("Inserting/Updating Stats",StartTime,"Info");
                       
//-----------this part is related to forcing to write to file---------------------
            ArrayList ExportMapFrom=new ArrayList();
//            ArrayList ExportMapTo=new ArrayList();
            Iterator maps = Parameters.getInsertMap().iterator();
            
            if (Parameters.getExportToFile_Delimiter()==null) {
                Delimiter="|";
            }
            else{
                Delimiter=Parameters.getExportToFile_Delimiter();
            }
            
            if ((Parameters.getDont_LoadDataInFile()==null || Parameters.getDont_LoadDataInFile().equalsIgnoreCase("false")) && (Parameters.getExportToFile()==null || Parameters.getExportToFile().equalsIgnoreCase("false"))){
                // Normal connection
                System.out.println("Normal Connection");
                if (Parameters.getWithDate_Insert().compareToIgnoreCase("true")==0) 
                    Tos=","+Parameters.getDateColumnName();
                
                String bothColumns="";
                System.out.println("Exporting to file for load data");
                while (maps.hasNext()){
                    bothColumns=(String)maps.next();
//                    System.out.println("bothColumns="+bothColumns);
                    ExportMapFrom.add(splitColumns(bothColumns).getColumnFrom());
                    Tos=Tos+","+splitColumns(bothColumns).getColumTo();
                }      
                 Tos=Tos.substring(1,Tos.length());
            }
            if(Parameters.getExportToFile()!=null && Parameters.getExportToFile().compareToIgnoreCase("true")==0 ){
            //Export to File
            System.out.println("Exporting to File");
            Iterator mapsEx = Parameters.getExportMap().iterator();
            while (mapsEx.hasNext()){
                String Map =(String)mapsEx.next();
                ExportMapFrom.add(Map);
//                System.out.println(Map);
                }
            }
            
            
//------------------------------------------------------------------------------
            while (rs.next()) {
//                ArrayList ExportMapFromTemp = null;
                HasResultSet=true;
                ArrayList<String> ExportMapFromTemp= new ArrayList<String>();
                ExportMapFromTemp.addAll(ExportMapFrom);
                
//                Collections.copy(ExportMapFromTemp,ExportMapFrom);
//                if ((Parameters.getExportToFile()!=null && Parameters.getExportToFile().compareToIgnoreCase("true")==0 ) || (Parameters.getDont_LoadDataInFile()()==null || Parameters.getDont_LoadDataInFile().compareTo("false")==0 ))
                    if (Parameters.getDont_LoadDataInFile()!=null && Parameters.getDont_LoadDataInFile().equalsIgnoreCase("true"))
                    {
                        Statements statements = generateStatements(Parameters.getInsertMap(),dateYYYYMMDD,rs);                        
                        if (Parameters.getWithStatementsLog().compareToIgnoreCase("true")==0) CopyTextFileObj.log(Parameters.getServiceName()+"_Statements.log",Parameters.getServiceName()+ ": "+statements.getInsertStatement(),true);
                        if (Parameters.getWithStatementsLog().compareToIgnoreCase("true")==0) CopyTextFileObj.log(Parameters.getServiceName()+"_Statements.log",Parameters.getServiceName()+ ": "+statements.getUpdateStatement(),true);
                        addData(statements.getInsertStatement(),statements.getUpdateStatement());
                    }
                else{
                        if (step==0){
                            ExportData(ExportMapFromTemp,dateYYYYMMDD,rs,Delimiter,false);
                        }
                        else{
                            ExportData(ExportMapFromTemp,dateYYYYMMDD,rs,Delimiter,true);
                        }
                    }
//                }
                step++;
            }
            
            try {
                rs.close();
                if (!Parameters.getCommand2BExecutedB4Insert().equalsIgnoreCase("") && !Parameters.getCommand2BExecutedB4Insert().equalsIgnoreCase("xxx"))
                    {
                    System.out.println("executing Command before insert:"+Parameters.getCommand2BExecutedB4Insert().replaceAll("dateYYYY-MM-DD", dateYYYYMMDD));
                    dbInterface_To.executeSQL(Parameters.getCommand2BExecutedB4Insert().replaceAll("dateYYYY-MM-DD", dateYYYYMMDD),false);
                    }
            } catch (SQLException e) {
                 System.err.println("Error: " + e.getMessage());
                Logger.Log(e.getMessage(),StartTime,"Error");
            }
//            CopyTextFileObj.log(Parameters.getServiceName()+".log",Parameters.getServiceName()+ ": "+ dateUtils.now("yyyy-MM-dd HH:mm:ss") + " Stats Updated/Inserted",true);
            Logger.Log("Stats Updated/Inserted",StartTime,"Info");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getErrorCode() + "   " + 
                               e.getMessage());
//                Logger_Interface Logger = new Logger_Interface();
                //                DateUtils dateUtils = new DateUtils();
//                String Start_Time = dateUtils.now("hh:mm");
                Logger.Log(e.getMessage(),StartTime,"Error");
            }
        System.out.println("Closing FromDB Connection1");

        try {
            if (!dbInterface_from.connection.isClosed()){
            dbInterface_from.closeConnection(); }
        } catch (SQLException e) {
            System.out.println("Exception:"+e.getMessage());
        }
        Unix_Execute UX_Eexe=new Unix_Execute();
        String CommandCP="cp "+Parameters.getServiceName()+"_"+dateYYYYMMDD+" /share/StatsCollector ";
        String CommandMV="rm "+Parameters.getServiceName()+"_"+dateYYYYMMDD;

        if (HasResultSet) {
            if (Parameters.getAutoImport().equalsIgnoreCase("true")) {
                dbInterface_Importer = 
                        new DBInterface(Service_Parameters.DB_AutoImport_DB_Type, 
                                        "00:00");
                boolean dbInterface_Importer_Status = 
                    dbInterface_Importer.connectToDB(Service_Parameters.DB_AutoImport_IP, 
                                                     Service_Parameters.DB_AutoImport_Port, 
                                                     Service_Parameters.DB_AutoImport_DB_Name, 
                                                     Service_Parameters.DB_AutoImport_UserName, 
                                                     Service_Parameters.DB_AutoImport_Password, 
                                                     Service_Parameters.DB_AutoImport_DB_Type, 
                                                     wait_);

                if (dbInterface_Importer_Status) {

                    File currentPath = 
                        new File(System.getProperty("user.dir"));
                    String pathSeparator = currentPath.separator;
                    String FilePath = 
                        currentPath + pathSeparator + Parameters.getServiceName() + 
                        "_" + dateYYYYMMDD;
                    String Statement ="";
                    Random rand = new Random();
                    int randomNum = rand.nextInt((65000 - 1) + 1) + 1;
                    try{
                        if (Parameters.getForceImport().equalsIgnoreCase("true")){
                            Statement = 
                                "REPLACE INTO Queues.`Pending Imports`(ID,ForcedID,FilePath,MachineIP,RemoveFile,TableName,`Columns`,FieldsTerminatedBy,DatabaseName,`Replace`,Service) VALUES('" + 
                                randomNum + "','" + randomNum + "','" +
                                FilePath + "','" + Parameters.getDB_To_IP() + "','" + 
                                Parameters.getRemove_File() + "','" + 
                                Parameters.getTableName() + "','" + Tos + "','" + 
                                Parameters.getExportToFile_Delimiter() + "','" + 
                                Parameters.getDB_To_DB_Name() + "','" + 
                                Parameters.Replace_Ignore + "','" + 
                                Parameters.ServiceName + "')";
                            dbInterface_Importer.executeSQL(Statement, false);
//                            Unix_Execute UX_Eexe=new Unix_Execute();
                            System.out.println("Force import ID/FID:"+randomNum);
                            CommandCP="Script_Exporter.sh /app_univ/AutoImporter/ QueryExecuter.sh "+randomNum+" " + randomNum;
                            try {
                                UX_Eexe.ShellExecute(CommandCP);
                            } catch (IOException e) {
                                System.out.println("IO error in Unix Command");
                            } catch (InterruptedException e) {
                                 System.out.println("Interrupted error in Unix Command");
                            }
                        }else{
                            Statement = 
                                "REPLACE INTO Queues.`Pending Imports`(FilePath,MachineIP,RemoveFile,TableName,`Columns`,FieldsTerminatedBy,DatabaseName,`Replace`,Service) VALUES('" + 
                                FilePath + "','" + Parameters.getDB_To_IP() + "','" + 
                                Parameters.getRemove_File() + "','" + 
                                Parameters.getTableName() + "','" + Tos + "','" + 
                                Parameters.getExportToFile_Delimiter() + "','" + 
                                Parameters.getDB_To_DB_Name() + "','" + 
                                Parameters.Replace_Ignore + "','" + 
                                Parameters.ServiceName + "')";
                                dbInterface_Importer.executeSQL(Statement, false);
                            }
                    }catch (SQLException e) {
                        System.out.println("Error in AutoImport DB");
                        System.out.println(Statement);
                        System.out.println(e.getMessage());
                        System.exit(0);
                    }
                    
                } else {
                    System.out.println("Can't connect to AutoImport DB");
                    System.exit(0);
                }
            } else {

                if ((Parameters.getExportToFile() == null || 
                     !Parameters.getExportToFile().equalsIgnoreCase("true")) && 
                    (Parameters.getDont_LoadDataInFile() == null || 
                     Parameters.getDont_LoadDataInFile().equalsIgnoreCase("false"))) {
                    //            Tos=Tos.replaceAll("`","\\`");
                    Tos = Tos.replaceAll(" ", "@");
                    String CommandMySQL = "";
                    if (Parameters.getReplace_Ignore().compareToIgnoreCase("ignore") == 
                        0) {
                        //                    CommandMySQL="/app_univ/StatsCollector/Load_Data_Executer.sh "+Parameters.getDB_To_IP()+" "+ Parameters.getDB_To_UserName()+" "+ Parameters.getDB_To_Password()+" "+ Parameters.getDB_To_DB_Name()+" "+ "LOAD@DATA@INFILE@'/share/StatsCollector/"+Parameters.getServiceName()+"_"+ dateYYYYMMDD+"'@IGNORE@INTO@TABLE@"+Parameters.getTableName()+"@CHARACTER@SET@utf8@FIELDS@TERMINATED@BY@'"+Delimiter+"'@("+Tos+")";
                        CommandMySQL = 
                                "/app_univ/StatsCollector/Load_Data_Executer.sh " + 
                                Parameters.getDB_To_IP() + " " + 
                                Parameters.getDB_To_UserName() + " " + 
                                Parameters.getDB_To_Password() + " " + 
                                Parameters.getDB_To_DB_Name() + " " + 
                                "LOAD@DATA@INFILE@'/share/StatsCollector/" + 
                                Parameters.getServiceName() + "_" + 
                                dateYYYYMMDD + "'@IGNORE@INTO@TABLE@" + 
                                Parameters.getTableName() + 
                                "@FIELDS@TERMINATED@BY@'" + Delimiter + "'@(" + 
                                Tos + ")";
                    } else {
                        CommandMySQL = 
                                "/app_univ/StatsCollector/Load_Data_Executer.sh " + 
                                Parameters.getDB_To_IP() + " " + 
                                Parameters.getDB_To_UserName() + " " + 
                                Parameters.getDB_To_Password() + " " + 
                                Parameters.getDB_To_DB_Name() + " " + 
                                "LOAD@DATA@INFILE@'/share/StatsCollector/" + 
                                Parameters.getServiceName() + "_" + 
                                dateYYYYMMDD + "'@REPLACE@INTO@TABLE@" + 
                                Parameters.getTableName() + 
                                "@FIELDS@TERMINATED@BY@'" + Delimiter + "'@(" + 
                                Tos + ")";
                        //                    CommandMySQL="/app_univ/StatsCollector/Load_Data_Executer.sh "+Parameters.getDB_To_IP()+" "+ Parameters.getDB_To_UserName()+" "+ Parameters.getDB_To_Password()+" "+ Parameters.getDB_To_DB_Name()+" "+ "LOAD@DATA@INFILE@'/share/StatsCollector/"+Parameters.getServiceName()+"_"+ dateYYYYMMDD+"'@REPLACE@INTO@TABLE@"+Parameters.getTableName()+"@CHARACTER@SET@utf8@FIELDS@TERMINATED@BY@'"+Delimiter+"'@("+Tos+")";
                    }

                    //CommandMySQL="/app_univ/StatsCollector/Load_Data_Executer.sh "+Parameters.getDB_To_IP()+" "+ Parameters.getDB_To_UserName()+" "+ Parameters.getDB_To_Password()+" "+ Parameters.getDB_To_DB_Name()+" "+ "LOAD@DATA@INFILE@'/share/StatsCollector/"+Parameters.getServiceName()+"_"+ dateYYYYMMDD+"'@REPLACE@INTO@TABLE@"+Parameters.getTableName()+"@FIELDS@TERMINATED@BY@'"+Delimiter+"'@("+Tos+")";
                    //            String CommandMySQL="Load_Data_Executer.sh mysql@-h"+Parameters.getDB_To_IP()+"@-u"+Parameters.getDB_To_UserName+"@-p"+Parameters.getDB_To_Password+"@-e\"LOAD@DATA@INFILE@'/log/Shared/"+Parameters.getServiceName()+"_"+ dateYYYYMMDD+"'@REPLACE@INTO@TABLE@"+Parameters.getTableName()+"@FIELDS@TERMINATED@BY@'"+Delimiter+"'@("+Tos+")\"@"+ Parameters.getDB_To_DB_Name;
                    String CommandRM = 
                        "rm /share/StatsCollector/" + Parameters.getServiceName() + 
                        "_" + dateYYYYMMDD;


                    try {
                        UX_Eexe.ShellExecute(CommandCP);
                        UX_Eexe.ShellExecute(CommandMV);
                        UX_Eexe.ShellExecute(CommandMySQL);
                        if (Parameters.getRemove_File() == null || 
                            Parameters.getRemove_File().equalsIgnoreCase("true")) {
                            UX_Eexe.ShellExecute(CommandRM);
                        } else {
                            System.out.println("File is not removed");
                        }
                    } catch (IOException e) {
                        System.out.println("IOException:" + e.getMessage());
                    } catch (InterruptedException e) {
                        System.out.println("InterruptedException:" + 
                                           e.getMessage());
                    }
                }

                // Unix Shell Script Execution
                //             UnixShellScript
                //             if ()
                String UnixShellScript = "";
                Iterator UnixShellScriptIT = 
                    Parameters.UnixShellScript.iterator();
                while (UnixShellScriptIT.hasNext()) {
                    UnixShellScript = 
                            UnixShellScriptIT.next().toString().replaceAll("dateYYYY-MM-DD", 
                                                                           dateYYYYMMDD.replaceAll("/", 
                                                                                                   "-"));
                    try {
                        UX_Eexe.ShellExecute(UnixShellScript);
                    } catch (IOException e) {
                        System.out.println("IOException:" + e.getMessage());
                    } catch (InterruptedException e) {
                        System.out.println("InterruptedException:" + 
                                           e.getMessage());
                    }
                }
            }
        } else {
            System.out.println("");
            System.out.println("DIDN'T FIND ANY RESULTSET! ");
//            System.out.println("--------------------------");
        }
        
//        System.out.println("Checking Connection");
        //Closing Connection if exists
        if (Parameters.getExportToFile() == null || 
            Parameters.getExportToFile().compareToIgnoreCase("false") == 0) {
            if(Parameters.getCommand2BExecutedAfterInsert().compareToIgnoreCase("xxx")!=0){
                try {
                    System.out.println("Exexuting command after insertion:"+Parameters.getCommand2BExecutedAfterInsert().replaceAll("dateYYYY-MM-DD", dateYYYYMMDD));
                    dbInterface_To.executeSQL(Parameters.getCommand2BExecutedAfterInsert().replaceAll("dateYYYY-MM-DD", dateYYYYMMDD),false);
                } catch (SQLException e) {
                    System.out.println("Error in command2BExecutedAfterInsert:"+e.getMessage());
                }
            }
            dbInterface_To.closeConnection();
        } else {
            System.out.println("No Need To close Connection");
        }
    }

    private void ExportData(ArrayList ExportMap,String dateYYYYMMDD,ResultSet rs,String delimiter,Boolean append){
        File currentPath = new File(System.getProperty("user.dir"));
                String pathSeparator = currentPath.separator;
//                DateUtils dateUtils = new DateUtils();
                
                try {
                
//                    OutputStreamWriter char_output = new OutputStreamWriter(
//                         new FileOutputStream("some_output.utf8"),
//                         Charset.forName("UTF-8").newEncoder() 
//                     );
//                     
                     
                    FileOutputStream fstream = new FileOutputStream(currentPath + pathSeparator + Parameters.getServiceName() + "_" + dateYYYYMMDD,append);  
                    PrintStream out = new PrintStream(fstream, true, "UTF-8");  
                        
//                                    out.println(sb);
                
                
//                    FileWriter fstream = 
//                        new FileWriter(currentPath + pathSeparator + Parameters.getServiceName() + "_" + dateYYYYMMDD,append);
//                    BufferedWriter out = new BufferedWriter(fstream);
//                    
                    String ExportedData = new String(); 
                    String Data= new String();
                    if ((Parameters.getExportToFile()==null || Parameters.getExportToFile().compareToIgnoreCase("false")==0 ) && (Parameters.getWithDate_Insert().compareToIgnoreCase("true")==0)){
                        //Normal Connection
                         ExportedData=dateYYYYMMDD+delimiter;
                    }
                    if (Parameters.ManualExport.compareToIgnoreCase("true")==0){
//                        System.out.println("Column names");
                          int col = rs.getMetaData().getColumnCount();
//                          System.out.println("Exporting "+ col+" Columns");
//                          System.out.println("Columns Name: ");
                          for (int i = 1; i <= col; i++){
                              String col_name = rs.getMetaData().getColumnName(i);
                              ExportMap.add(col_name);
//                              System.out.println("Adding:"+col_name);
//                              System.out.println("counter:"+i);
                          }
                    }

                    Iterator ExportMapIt = ExportMap.iterator();
                    while (ExportMapIt.hasNext()){
                        try {
                            Data=ExportMapIt.next().toString();
                            ExportedData=ExportedData+rs.getString(Data)+delimiter;
//                            System.out.println("ExportedData:"+ExportedData);
                            } catch (SQLException e) {
                                 System.err.println("Error: " + e.getMessage());
//                                Logger_Interface Logger = new Logger_Interface();
                                //                DateUtils dateUtils = new DateUtils();
//                                String Start_Time = dateUtils.now("hh:mm");
                                Logger.Log(e.getMessage(),StartTime,"Error");
                            }
                    }
//                    out.write((String)ExportedData.subSequence(0,ExportedData.length()-1));
                    out.println((String)ExportedData.subSequence(0,ExportedData.length()-1));
//                    out.newLine();

                    out.close();
                    ExportMap.clear();
                } catch (IOException e) { //Catch exception if any
                    System.err.println("Error: " + e.getMessage());
//                    Logger_Interface Logger = new Logger_Interface();
                    //                DateUtils dateUtils = new DateUtils();
//                    String Start_Time = dateUtils.now("hh:mm");
                    Logger.Log(e.getMessage(),StartTime,"Error");
                }
                    catch(Exception e) { 
                        System.err.println("Export Error: " + e.getMessage());
                        Logger.Log(e.getMessage(),StartTime,"Error");
                    }
    }
    private static void ManualSetter(Parameter_Setter Parameters_setter,String ServiceName,
                                                    String DB_From_IP,String DB_From_Port,String DB_From_DB_Name,
                                                    String DB_From_UserName,String DB_From_Password,
                                                    String DB_From_DB_Type,String Quary_From,String ExportToFile_Delimiter
                                           ){                                
System.out.println("1");                                           
        Parameters_setter.setServiceName(ServiceName);
        Parameters_setter.setManualExport("True");
        Parameters_setter.setDB_From_IP(DB_From_IP);
        Parameters_setter.setDB_From_Port(DB_From_Port);
        Parameters_setter.setDB_From_DB_Name(DB_From_DB_Name);
        Parameters_setter.setDB_From_UserName(DB_From_UserName);
        Parameters_setter.setDB_From_Password(DB_From_Password);
        Parameters_setter.setDB_From_DB_Type(DB_From_DB_Type);
        Parameters_setter.setQuary_From(Quary_From);
        Parameters_setter.setExportToFile_Delimiter(ExportToFile_Delimiter);
        
        Parameters_setter.setExportToFile("True");
        Parameters_setter.setDont_LoadDataInFile("False");
        Parameters_setter.setRemove_File("False");
        Parameters_setter.setWithStatementsLog("False");
        Parameters_setter.setCommand2BExecutedB4Insert("XXX");
        
        Parameters_setter.setLog_DB_IP("CNPVAS01");
        Parameters_setter.setLog_DB_DataBase("Loggers");
        Parameters_setter.setLog_DB_TableName("Java_Logs");
        Parameters_setter.setLog_DB_Type("MySQL");
        Parameters_setter.setLog_DB_Port("3306");
        Parameters_setter.setLog_DB_UserName("Writer");
        Parameters_setter.setLog_DB_Password("Writer");

        //                Parameters_setter.DB_To_IP;
        //                Parameters_setter.DB_To_Port;
        //                Parameters_setter.DB_To_DB_Name;
        //                Parameters_setter.DB_To_UserName;
        //                Parameters_setter.DB_To_Password;
        //                Parameters_setter.DB_To_DB_Type;
        //
        //                Parameters_setter.TableName;
        //                Parameters_setter.DateColumnName;
        //
        //                public ArrayList InsertMap=new ArrayList();
        //                public ArrayList ExportMap=new ArrayList();
        //                public ArrayList UnixShellScript=new ArrayList();
        //
        //                Parameters_setter.WithDate_Insert;
        //                Parameters_setter.Command2BExecutedB4Insert;

        //                Parameters_setter.Replace_Ignore;
        System.out.println("2");
//        return Parameters_setter;
    }
    public static void main(String[] args) {
        DateUtils dateUtils = new DateUtils();
        String date = dateUtils.date("yyyy-MM-dd", 0);
//        String date = "2013-10-16";
        String Start_Time = dateUtils.now("HH:mm:ss");
//        String Start_Time = "12:50:50";
        String ManualExport="";
        
    try{
         // args[0] is the service name and args[1] is the date
            if (args[0].length() == 0 || args[0] == ""){
                System.out.println("you must specify service name");
                System.out.println("In case of exporting data without accing internal DB, Make sure that ManualExportFlag is passed by true");
                System.out.println("Manual Export paramters:ServiceName,Date,ManualExportFlag,DB_From_IP,DB_From_Port,DB_From_DB_Name,DB_From_UserName,DB_From_Password,DB_From_DB_Type,Quary_From,ExportToFile_Delimiter");
                System.exit(0);
            }
          
            if (args.length<3){
                ManualExport="False";
            }
            else{
                ManualExport=args[2];
            }
            System.out.println("getting params for Service:"+args[0]);
            System.out.println(ManualExport);
            Parameter_Setter Parameters_setter=new Parameter_Setter(args[0],ManualExport);
//            System.out.println("gor params");
            if (args.length>=3 && args[2].compareToIgnoreCase("true")==0){
                System.out.println("Manual Export");
                System.out.println("XXX");  
                if (args.length<10){
                    System.out.println("Missing a Parameter");
                    System.out.println("you must specify:ServiceName,Date,ManualExportFlag,DB_From_IP,DB_From_Port,DB_From_DB_Name,DB_From_UserName,DB_From_Password,DB_From_DB_Type,Quary_From,ExportToFile_Delimiter");
                    System.exit(0);
                } else{
                    ManualSetter(Parameters_setter,args[0],args[3],args[4],args[5],
                                                args[6],args[7],args[8],args[9],args[10]);
                }
            }
            if (args.length<1 || args[1] == ""){
                System.out.println("Date is not entered, current date is selected");
                Stats_Collector shortCode_Collector = new Stats_Collector(date,Start_Time,Parameters_setter);   
            }
            else{
                System.out.println("Getting data for date:"+args[1]);
                Stats_Collector shortCode_Collector = new Stats_Collector(args[1],Start_Time,Parameters_setter);
            }
        }catch (ArrayIndexOutOfBoundsException E){
            if (E.getLocalizedMessage().equalsIgnoreCase("0")) {
                System.out.println("you must specify service name");
                System.out.println("In case of exporting data without accing internal DB.");
                System.out.println("Manual Export paramters:ServiceName,Date,ManualExportFlag,DB_From_IP,DB_From_Port,DB_From_DB_Name,DB_From_UserName,DB_From_Password,DB_From_DB_Type,Quary_From,ExportToFile_Delimiter");
                System.exit(0);
            }else if(E.getLocalizedMessage().equalsIgnoreCase("2")){
                System.out.println("Else");
                System.out.println(E.getMessage());
            }
            if (E.getMessage().equalsIgnoreCase("1")){
                Parameter_Setter Parameters_setter=new Parameter_Setter(args[0],ManualExport);
                Stats_Collector shortCode_Collector = new Stats_Collector(date,Start_Time,Parameters_setter);
            }
        }
    }
}
