package sp_executer;

import DBInterfaces.DBInterface;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class SP_Excuter {
    public Connection connection;

    private String driver = "oracle.jdbc.driver.OracleDriver";

    private String url = "jdbc:oracle:thin:@";

    private Connection conn = null;

    public SP_Excuter(String host, String db, String Port,String user, String password)
        throws ClassNotFoundException, SQLException {

        // construct the url
        url = url + host + ":" + Port + ":" + db;

        // load the Oracle driver and establish a connection
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
        }
        catch (ClassNotFoundException ex) {
            System.out.println("Failed to find driver class: " + driver);
            throw ex;
        }
        catch (SQLException ex) {
            System.out.println("Failed to establish a connection to: " + url);
            throw ex;
        }
    }

    public void cleanup() throws SQLException {

        if (conn != null)
            conn.close();
    }

    public void SP_Caller(String SP_Name,String Parameters,String ParameterTypes){
//    System.out.println("Hello");
        try {
        
            String[] Params;
            String[] Types;
            
            Params = Parameters.split("\\|");
            Types = ParameterTypes.split(",");
            
            String Questions = "";
            for(int i =0; i < Params.length ; i++){
                Questions = Questions + "?,";
            }
            Questions="("+Questions.substring(0,Questions.length()-1)+")";
            
            CallableStatement proc = 
                conn.prepareCall("{ call " + SP_Name + Questions+" }"); //SendSMS
//                System.out.println("Prepairing:"+"{ call " + SP_Name + Questions+" }");
              
              
                
              for(int i =0; i < Params.length ; i++){
              if(i == 3)
              {
                String temp=Params[i].replaceAll("NEWLINE","\n");
                proc.setString(i+1,temp);
                continue;
              }
                if (Types[i].equalsIgnoreCase("String".trim()))
                  proc.setString(i+1,Params[i]);
                if (Types[i].equalsIgnoreCase("Integer".trim()))
                  proc.setInt(i+1,Integer.valueOf(Params[i]));
              }
              
              /*
               proc.setString(1,"OTHER");
            proc.setString(2,"VAS Quality");
            proc.setString(3,"201007021881");
            proc.setString(4,"Test\nTest");
            proc.setString(5,"0");
            proc.setString(6,"2");
            proc.setString(7,"0");
            */
//              System.out.println("executing Proc:"+proc.toString());
              
            proc.execute();
        }
        catch (SQLException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private static SP_Service_Parameters ServiceParmas(String ServiceName){
        
        SP_Service_Parameters service_Parameters=new SP_Service_Parameters();
        long wait_ = 1000;
        DBInterface dbInterface_Service_Details = new DBInterface("MYSQL","00:00");
        boolean status = dbInterface_Service_Details.connectToDB("172.23.201.51",
                                                                 "3307", 
                                                                 "Queues", 
                                                                 "Reader", 
                                                                 "Reader", 
                                                                 "MYSQL",
                                                                 wait_);
        ResultSet rs;        
        try {
        rs=dbInterface_Service_Details.executeSQL_RS("Select count(*) Counts from Java_SP_Queue WHERE Status='Pending'");
        String Counts="";
        while (rs.next()){
         Counts=rs.getString("Counts");
         if (Counts.equalsIgnoreCase("0")){
             System.out.println("No Pending Stored Procedure to be executed!");
             System.exit(0);
         }
        }
        rs.close();
        rs=dbInterface_Service_Details.executeSQL_RS("Select * from Java_SP_Queue WHERE Status='Pending'");
        while (rs.next()){
            service_Parameters.setDB_Name(rs.getString("DB_Name"));
            service_Parameters.setHostIP(rs.getString("Host_IP"));
            service_Parameters.setHostPort(rs.getString("Host_Port"));
//            service_Parameters.setParameters(rs.getString("Parameters"));
            service_Parameters.setParamterTypes(rs.getString("Paramter_Types"));
            service_Parameters.setPassword(rs.getString("Password"));
            service_Parameters.setUserName(rs.getString("User_Name"));
            service_Parameters.setSP_Name(rs.getString("SP_Name"));
        }
        rs.close();
        } catch (SQLException e) {
             System.out.println("SQLException:"+e.getMessage());
             System.exit(1);
         }
         return service_Parameters;
    }
    public static void main(String[] args) throws Exception {
            try {
             if (args[0].length()==0 || args[1].length()==0 ){
                 System.out.println("PLZ use java -jar SP_Executer.jar ServiceName Parameters");
                 }
             else{
//                SP_Service_Parameters service_Parameters=ServiceParmas(args[0]);
//                System.out.println("Connecting to DB:"+args[0]);
                SP_Excuter jdbc = new SP_Excuter(args[0], args[1], args[2],args[3],args[4]);
//                System.out.println("Connection Established");
                jdbc.SP_Caller(args[5],args[6],args[7]);
                                                   
                 jdbc.cleanup();
                 }
             }catch (ArrayIndexOutOfBoundsException E){
                 System.out.println("PLZ use java -jar SP_Executer.jar ServiceName Parameters");
             }

            catch (ClassNotFoundException ex) {
                System.out.println(" failed");
            }
            catch (SQLException ex) {
                System.out.println(" failed: " + ex.getMessage());
            }
    //        }
    }
    }
