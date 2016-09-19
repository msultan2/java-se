package CCN_Parser;

import DBInterfaces.DBInterface;

import Date_Functions.DateUtils;

import Directory_Operations.DirectoryReader;

import Directory_Operations.FileType;

import LineParser.LineParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import java.io.IOException;

import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import xml_parser.CopyTextFile;
import xml_parser.XML_Parser_Class;
import xml_parser.drs.MV;

public class CCN_Parser {
    public CCN_Parser() {
    }
private Statements generateSQLStatement(XML_Parser_Class XML_Parser_Class_Obj,MV tempMV,String tableName){
    Statements statements = new Statements();
    String statement = new String();
    LineParser lineParser =new LineParser();
    ArrayList lineData = lineParser.parser_ArrayList(XML_Parser_Class_Obj.mi.getHeadder(),",");
    Iterator iterator = lineData.iterator();
    while(iterator.hasNext()){
        if (statement.length()!=0)
            statement=statement+","+iterator.next();
        else
            statement=(String)iterator.next();
    }
    
        String values = new String();
        Iterator lr = tempMV.getR().iterator();
        values = "";
        while (lr.hasNext()) {
            if (values.length() == 0)
                values = (String)lr.next();
            else
                values = values + "," +(String)lr.next();
        }
    statements.setInsertStatement("INSERT INTO " + tableName + " (" + 
                                  statement  + ") VALUES('" + 
                                  XML_Parser_Class_Obj.mi.getTimeDate().getDate() + 
                                  "','" + 
                                  XML_Parser_Class_Obj.mi.getTimeDate().getFromtime() + 
                                  "','" + 
                                  XML_Parser_Class_Obj.mi.getTimeDate().getTotime() + 
                                  "','" + tempMV.getMoid() + "','" + 
                                  tempMV.getCounter() + "'," + 
                                  values + ")");  

        iterator = lineData.iterator();                              
        iterator.next();
        iterator.next();
        iterator.next();
        iterator.next();
        iterator.next();
        
        ArrayList ValuesData = lineParser.parser_ArrayList(values,",");
        Iterator ValuesDataIterator = ValuesData.iterator();
        
        String set =new String();
        
        while (iterator.hasNext()) {
            if (set.length() == 0)
                set = (String)iterator.next()+"='"+(String)ValuesDataIterator.next()+"'";
            else
                set = set +","+ (String)iterator.next()+"='"+(String)ValuesDataIterator.next()+"'";
        }
    
        statements.setUpdateStatement("UPDATE "+tableName+" SET " +
                    set +
                    " WHERE "
                    + "Date_='" + 
                    XML_Parser_Class_Obj.mi.getTimeDate().getDate() + 
                    "' AND " + "FromTime='" + 
                    XML_Parser_Class_Obj.mi.getTimeDate().getFromtime() + 
                    "' AND " + "ToTime='" + 
                    XML_Parser_Class_Obj.mi.getTimeDate().getTotime() + 
                    "' AND " + "Proccessor='" + 
                    tempMV.getMoid() + "' AND " + 
                    "Value='" + 
                    tempMV.getCounter() + "'");
    return statements;
}
//private void getConfigurations(){
//    
//    File file =new File("CCN_Parser.Conf");
//    FileReader fileReader;
//        try {
//            fileReader = new FileReader(file);
//        } catch (FileNotFoundException e) {
//            // TODO
//        }
//        BufferedReader reader = new BufferedReader(fileReader);
//
//    //... Loop as long as there are input lines.
//    String line = null;
//
//        //... Close reader and writer.
//        try {
//            while ((line = reader.readLine()) != null) {
//    }
//        } catch (IOException e) {
//            // TODO
//        } // Close to unlock.
//        try {
//            reader.close();
//        } catch (IOException e) {
//            // TODO
//        }
//    }
//}
    public static void main(String[] args) {
    
        CCN_Parser CCN_Parser_Obj = new CCN_Parser();
        
        DirectoryReader dirsReader = new DirectoryReader();
        FileType dirsFiles = new FileType();
        String dirPath = new String();

//        String[] args_=new String[1];
//        args_[0]="D:\\Work\\Exchange\\PMF_Files\\CcnCounters";
//        args_[0]="D:\\Work\\Exchange\\PMF_Files\\PlatformMeasures";
//        args_[0]="D:\\Work\\Exchange\\PMF_Files\\SignallingMeasures";
//        args_[0]="D:\\Work\\Exchange\\PMF_Files\\oamProvisioningCounter";
        
//        dirPath = args_[0];
//        String tableName = new String();
//        tableName = "Platform_Measures";
//        tableName = "Ccn_Counters";
//        tableName = "signalling_measures";
//        tableName = "oam_Provisioning_Counter";
        
        try {
            if (args[0].length() == 0 || args[0] == "") {
                System.out.println("Please Enter the Target Directory");
                System.exit(0);
            }
        } catch (ArrayIndexOutOfBoundsException E) {
            System.out.println("Please Enter the Target Directory");
            System.exit(0);
        }
        dirPath = args[0];
        try {
            if (args[1].length() == 0 || args[1] == "") {
                System.out.println("Please Enter the Target DB Table");
                System.exit(0);
            }
        } catch (ArrayIndexOutOfBoundsException E) {
            System.out.println("Please Enter the Target DB Table");
            System.exit(0);
        }
        String tableName = args[1];

        dirsFiles = dirsReader.getDirsFiles(dirPath);

        Iterator li = dirsFiles.getFiles().iterator();
        
        File currentPath = new File(System.getProperty("user.dir"));
        String pathSeparator = currentPath.separator;
        //System.out.println(pathSeparator);
        dirsReader.dirExists(dirPath + pathSeparator+"Formatted");

//        File ccnStatsFile = new File(dirPath + pathSeparator+"Formatted"+pathSeparator + "Output.txt");
//        if (ccnStatsFile.exists()) ccnStatsFile.delete();

        while (li.hasNext()) {
            XML_Parser_Class XML_Parser_Class_Obj = 
                new XML_Parser_Class(dirPath + pathSeparator + (String)li.next(), "");
            //XML_Parser_Class_Obj.mi.get

            CopyTextFile CopyTextFileObj = new CopyTextFile();
//            if (!ccnStatsFile.exists()) {
//                CopyTextFileObj.fileInsertText(ccnStatsFile.getPath(), 
//                                               XML_Parser_Class_Obj.mi.getHeadder(), 
//                                               true);
//            }

            //Adding Data
             DateUtils dateUtils = new DateUtils();
             Iterator lMv = XML_Parser_Class_Obj.mi.getMv().iterator();
             
             while (lMv.hasNext()) {
                 //headder = headder + "," + (String)li.next();
                  MV tempMV = new MV();
                 tempMV = (MV)lMv.next();

                 DBInterface dbInterface = new DBInterface("SQL");
                 Boolean status;
//                 status=dbInterface.connectToDB("localhost","3306","CCN_stats","CCN","CCN","MySQL");
                status=dbInterface.connectToDB("10.230.97.31","1433","CCN_Stats","CCN_User","CCN","SQL");
                if (status){
                Statements sqlStatement = CCN_Parser_Obj.generateSQLStatement(XML_Parser_Class_Obj,tempMV,tableName);
                    try {
                        CopyTextFileObj.log("Trying to Insert Data" + 
                                                       tempMV.getMoid(), true);
                        //System.out.println("Trying To Data" +tempMV.getMoid());
                        dbInterface.executeSQL(sqlStatement.getInsertStatement());
//                        System.out.println("Inserting Data" +tempMV.getMoid());
                         CopyTextFileObj.log("Data Inserted" + 
                                                        tempMV.getMoid(), true);
                    } catch (SQLException e) {
                        if(e.getErrorCode()==1062){
                            try {
                                dbInterface.executeSQL(sqlStatement.getUpdateStatement());
//                                System.out.println("Updating Data" +tempMV.getMoid());
                                } catch (SQLException E) {
                                 System.out.println("DB Error:"+E.getMessage());
                                }
                        }
                        else
                            System.out.println(e.getMessage());
                    }
                }
             }
//                
//                CopyTextFileObj.fileInsertText(ccnStatsFile.getPath(), 
//                                               XML_Parser_Class_Obj.mi.getTimeDate().getDate() + 
//                                               "." + 
//                                               XML_Parser_Class_Obj.mi.getTimeDate().getFromtime() + 
//                                               "-" + 
//                                               XML_Parser_Class_Obj.mi.getTimeDate().getTotime() + 
//                                               "," + tempMV.getMoid() + "," + 
//                                               tempMV.getCounter() + "," + 
//                                               values, true);

        }
    System.out.println("Finished");
    }
}