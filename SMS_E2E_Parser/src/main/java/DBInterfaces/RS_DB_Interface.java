package DBInterfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import promo.util.Parameters;
import promo.util.PromoLogger;


public class RS_DB_Interface{
    
    DBInterface dbInterface = new DBInterface(Parameters.DBType);
    private enum statements {
        smsCntSriSmTimeoutCounter,
        System_Checker,
        router_smscntmttimeoutcounter,
        router_smscntmotimeoutcounter,
        router_mtplinktxutilisation,
        router_mtplinkrxutilisation,
        ams_queuesizeexceeded,
        ams_amscnttotalstored,
        cm_polls,
        eci_diamtransadminstate_kpi,
        eci_diamtransstate_kpi,
        router_m3ua_link_kpi,
        router_ss7_link_kpi,
        tp_statuskpi
        ;
    }
    private static PromoLogger logger  = new PromoLogger(RS_DB_Interface.class);
    private boolean dbstate=false;
    
    public RS_DB_Interface() {
        dbstate=dbInterface.connectToDB(Parameters.serverIP, Parameters.port, 
                                Parameters.dbName, Parameters.userName, 
                                Parameters.password, Parameters.DBType);
    }
    
    public boolean System_Checker(String dateYYYYMMDD,Integer time) throws SQLException {
        boolean status = true;
        Statement statement;
        statement = dbInterface.connection.createStatement();
        ResultSet rs;
        rs = statement.executeQuery(statementBuilder("System_Checker",dateYYYYMMDD,time));
            while (rs.next()) {
                System.out.println("Capacity Exceeded on "+ rs.getString("Date") +
                                    " SMS Node: " + rs.getString("SMS_Node_Name")+
                                    " At: "+ rs.getString("Time")+ " "+ 
                                    rs.getString("Times") + " Times" );
                status=false;
                            
            }
            rs.close();
            statement.close();
        
        return status;
    }
    public boolean eci_diamtransadminstate_kpi(String dateYYYYMMDD,Integer time) throws SQLException {
        boolean status = true;
        Statement statement;
        statement = dbInterface.connection.createStatement();
        ResultSet rs;
        rs = statement.executeQuery(statementBuilder("eci_diamtransadminstate_kpi",dateYYYYMMDD,time));
            while (rs.next()) {
                System.out.println("eci_diamtransadminstate Exceeded on "+ rs.getString("Date") +
                                    " SMS Node: " + rs.getString("SMS_Node_Name")+
                                    " At: "+ rs.getString("Time"));
                status=false;
                            
            }
            rs.close();
            statement.close();
        
        return status;
    }
    public boolean eci_diamtransstate_kpi(String dateYYYYMMDD,Integer time) throws SQLException {
        boolean status = true;
        Statement statement;
        statement = dbInterface.connection.createStatement();
        ResultSet rs;
        rs = statement.executeQuery(statementBuilder("eci_diamtransstate_kpi",dateYYYYMMDD,time));
            while (rs.next()) {
                System.out.println("eci_diamtransstate_kpi Exceeded on "+ rs.getString("Date") +
                                    " SMS Node: " + rs.getString("SMS_Node_Name")+
                                    " At: "+ rs.getString("Time"));
                status=false;
                            
            }
            rs.close();
            statement.close();
        
        return status;
    }
    public boolean router_m3ua_link_kpi(String dateYYYYMMDD,Integer time) throws SQLException {
        boolean status = true;
        Statement statement;
        statement = dbInterface.connection.createStatement();
        ResultSet rs;
        rs = statement.executeQuery(statementBuilder("router_m3ua_link_kpi",dateYYYYMMDD,time));
            while (rs.next()) {
                System.out.println("router_m3ua_link_kpi Exceeded on "+ rs.getString("Date") +
                                    " SMS Node: " + rs.getString("SMS_Node_Name")+
                                    " At: "+ rs.getString("Time"));
                status=false;
                            
            }
            rs.close();
            statement.close();
        
        return status;
    }
    public boolean router_ss7_link_kpi(String dateYYYYMMDD,Integer time) throws SQLException {
        boolean status = true;
        Statement statement;
        statement = dbInterface.connection.createStatement();
        ResultSet rs;
        rs = statement.executeQuery(statementBuilder("router_ss7_link_kpi",dateYYYYMMDD,time));
            while (rs.next()) {
                System.out.println("router_ss7_link_kpi Exceeded on "+ rs.getString("Date") +
                                    " SMS Node: " + rs.getString("SMS_Node_Name")+
                                    " At: "+ rs.getString("Time"));
                status=false;
                            
            }
            rs.close();
            statement.close();
        
        return status;
    }
    public boolean tp_statuskpi(String dateYYYYMMDD,Integer time) throws SQLException {
        boolean status = true;
        Statement statement;
        statement = dbInterface.connection.createStatement();
        ResultSet rs;
        rs = statement.executeQuery(statementBuilder("router_ss7_link_kpi",dateYYYYMMDD,time));
            while (rs.next()) {
            if (!(rs.getString("SMS_Node_Name").compareToIgnoreCase("CM01") == 0 && 
                  rs.getString("PROCESS").compareToIgnoreCase("textpass") == 0)) {
                System.out.println("router_ss7_link_kpi Exceeded on " + 
                                   rs.getString("Date") + " SMS Node: " + 
                                   rs.getString("SMS_Node_Name") + " At: " + 
                                   rs.getString("Time"));
                status=false;
                }
                            
            }
            rs.close();
            statement.close();
        
        return status;
    }
    private String statementBuilder(String statementType,String dateYYYYMMDD,Integer time){
        String queryStr= new String();
        switch(statements.valueOf(statementType)){
            case smsCntSriSmTimeoutCounter : 
                queryStr= "SELECT TOC.NAME,toc1.value-TOC.Value Diff,TOC.Date,TOC.Time+1 Time,TOC.SMS_Node_name " + 
                         "FROM router_smscntsrismtimeoutcounter TOC , (SELECT NAME,Value,Date,Time-1 time,SMS_Node_name FROM router_smscntsrismtimeoutcounter)TOC1 " + 
                         "WHERE TOC.date = '" + dateYYYYMMDD + "' and TOC.time+1=" + time.toString() +" and TOC.Time=toc1.time and TOC.SMS_Node_name=toc1.sms_node_name";
                break;
            case System_Checker : 
                queryStr= "SELECT SMS_node_name,date,time,count(SMS_node_name) Times FROM system_KPI where date = '" + 
                                    dateYYYYMMDD + "' and time=" + time.toString() + 
                                    " group by SMS_node_name,date,time";
                break;
            case router_smscntmttimeoutcounter:
                queryStr= "SELECT TOC.NAME,toc1.value-TOC.Value Diff,TOC.Date,TOC.Time+1 Time,TOC.SMS_Node_name " + 
                         "FROM router_smscntmttimeoutcounter TOC , (SELECT NAME,Value,Date,Time-1 time,SMS_Node_name FROM router_smscntmttimeoutcounter)TOC1 " + 
                         "WHERE TOC.date = '" + dateYYYYMMDD + "' and TOC.time+1=" + time.toString() +" and TOC.Time=toc1.time and TOC.SMS_Node_name=toc1.sms_node_name";
                break;
            case router_smscntmotimeoutcounter:
                queryStr= "SELECT TOC.NAME,toc1.value-TOC.Value Diff,TOC.Date,TOC.Time+1 Time,TOC.SMS_Node_name " + 
                         "FROM router_smscntmotimeoutcounter TOC , (SELECT NAME,Value,Date,Time-1 time,SMS_Node_name FROM router_smscntmotimeoutcounter)TOC1 " + 
                         "WHERE TOC.date = '" + dateYYYYMMDD + "' and TOC.time+1=" + time.toString() +" and TOC.Time=toc1.time and TOC.SMS_Node_name=toc1.sms_node_name";
                break;
            case router_mtplinktxutilisation:
                queryStr= "SELECT TOC.NAME,toc1.value-TOC.Value Diff,TOC.Date,TOC.Time+1 Time,TOC.SMS_Node_name " + 
                         "FROM router_mtplinktxutilisation TOC , (SELECT NAME,Value,Date,Time-1 time,SMS_Node_name FROM router_mtplinktxutilisation)TOC1 " + 
                         "WHERE TOC.date = '" + dateYYYYMMDD + "' and TOC.time+1=" + time.toString() +" and TOC.Time=toc1.time and TOC.SMS_Node_name=toc1.sms_node_name and TOC.NAME=toc1.name";
                break;
            case router_mtplinkrxutilisation:
                queryStr= "SELECT TOC.NAME,toc1.value-TOC.Value Diff,TOC.Date,TOC.Time+1 Time,TOC.SMS_Node_name " + 
                         "FROM router_mtplinkrxutilisation TOC , (SELECT NAME,Value,Date,Time-1 time,SMS_Node_name FROM router_mtplinkrxutilisation)TOC1 " + 
                         "WHERE TOC.date = '" + dateYYYYMMDD + "' and TOC.time+1=" + time.toString() +" and TOC.Time=toc1.time and TOC.SMS_Node_name=toc1.sms_node_name and TOC.NAME=toc1.name";
                break;
            case ams_queuesizeexceeded:
                queryStr= "SELECT TOC.NAME,toc1.value-TOC.Value Diff,TOC.Date,TOC.Time+1 Time,TOC.SMS_Node_name " + 
                         "FROM ams_queuesizeexceeded TOC , (SELECT NAME,Value,Date,Time-1 time,SMS_Node_name FROM ams_queuesizeexceeded)TOC1 " + 
                         "WHERE TOC.date = '" + dateYYYYMMDD + "' and TOC.time+1=" + time.toString() +" and TOC.Time=toc1.time and TOC.SMS_Node_name=toc1.sms_node_name and TOC.NAME=toc1.name";
                break;
            case ams_amscnttotalstored:
                queryStr= "SELECT TOC.NAME,toc1.value-TOC.Value Diff,TOC.Date,TOC.Time+1 Time,TOC.SMS_Node_name " + 
                         "FROM ams_amscnttotalstored TOC , (SELECT NAME,Value,Date,Time-1 time,SMS_Node_name FROM ams_amscnttotalstored)TOC1 " + 
                         "WHERE TOC.date = '" + dateYYYYMMDD + "' and TOC.time+1=" + time.toString() +" and TOC.Time=toc1.time and TOC.SMS_Node_name=toc1.sms_node_name and TOC.NAME=toc1.name";
                break;
            case cm_polls:
                queryStr= "select SMS_node_Name,app_path,date,time,count(Continous) Count from cm_polls where date='" + dateYYYYMMDD + "' and time = " + time.toString() +" group by Continous";
                break;
            case tp_statuskpi:
                queryStr= "SELECT * from tp_statuskpi WHERE date = '" + dateYYYYMMDD + "' and time=" + time.toString();
                break;
            case eci_diamtransadminstate_kpi:
                queryStr= "SELECT * from eci_diamtransadminstate_kpi WHERE date = '" + dateYYYYMMDD + "' and time=" + time.toString();
                break;
            case eci_diamtransstate_kpi:
                queryStr= "SELECT * from eci_diamtransstate_kpi WHERE date = '" + dateYYYYMMDD + "' and time=" + time.toString();
                break;
            case router_m3ua_link_kpi:
                queryStr= "SELECT * from router_m3ua_link_kpi WHERE date = '" + dateYYYYMMDD + "' and time=" + time.toString();
                break;
            case router_ss7_link_kpi:
                queryStr= "SELECT * from router_ss7_link_kpi WHERE date = '" + dateYYYYMMDD + "' and time=" + time.toString();
                break;
        }
        return queryStr;
    }
    public boolean smsCntSriSmTimeoutCounter (String dateYYYYMMDD,Integer time) throws SQLException{
        boolean status = true;
        Statement statement;
        statement = dbInterface.connection.createStatement();
        ResultSet rs =   statement.executeQuery(statementBuilder("smsCntSriSmTimeoutCounter",dateYYYYMMDD,time));
             
            while (rs.next()) {
                if (Float.valueOf(rs.getString("Diff"))>400){
                    System.out.println("smscntsrismtimeoutcounter Is Increased by:" +rs.getString("Diff") +" on "+ rs.getString("Date") +
                                        " SMS Node: " + rs.getString("SMS_Node_Name")+
                                        " At: "+ rs.getString("Time"));
                    status=false;
                }                            
            }
            rs.close();
            statement.close();
        
        return status;
    }
    public boolean router_smscntmttimeoutcounter(String dateYYYYMMDD,Integer time) throws SQLException{
        boolean status = true;
        Statement statement;
        statement = dbInterface.connection.createStatement();
        ResultSet rs =   statement.executeQuery(statementBuilder("router_smscntmttimeoutcounter",dateYYYYMMDD,time));
             
            while (rs.next()) {
                if (Float.valueOf(rs.getString("Diff"))>400){
                    System.out.println("router_smscntmttimeoutcounter Is Increased by: " +rs.getString("Diff") +" on "+ rs.getString("Date") +
                                        " SMS Node: " + rs.getString("SMS_Node_Name")+
                                        " At: "+ rs.getString("Time"));
                    status=false;
                }                            
            }
            rs.close();
            statement.close();
        
        return status;
    }
    public boolean router_smscntmotimeoutcounter(String dateYYYYMMDD,Integer time) throws SQLException{
        boolean status = true;
        Statement statement;
        statement = dbInterface.connection.createStatement();
        ResultSet rs =   statement.executeQuery(statementBuilder("router_smscntmotimeoutcounter",dateYYYYMMDD,time));
             
            while (rs.next()) {
                if (Float.valueOf(rs.getString("Diff"))>100){
                    System.out.println("router_smscntmotimeoutcounter Is Increased by: " +rs.getString("Diff") +" on "+ rs.getString("Date") +
                                        " SMS Node: " + rs.getString("SMS_Node_Name")+
                                        " At: "+ rs.getString("Time"));
                    status=false;
                }                            
            }
            rs.close();
            statement.close();
        
        return status;
    }
    public boolean router_mtplinktxutilisation(String dateYYYYMMDD,Integer time) throws SQLException{
        boolean status = true;
        Statement statement;
        statement = dbInterface.connection.createStatement();
        ResultSet rs =   statement.executeQuery(statementBuilder("router_mtplinktxutilisation",dateYYYYMMDD,time));
             
            while (rs.next()) {
                if (Math.abs(Float.valueOf(rs.getString("Diff")))>100){
                    System.out.println("router_mtplinktxutilisation Is Increased by: " +rs.getString("Diff") +" on "+ rs.getString("Date") +
                                        " SMS Node: " + rs.getString("SMS_Node_Name")+
                                        " At: "+ rs.getString("Time"));
                    status=false;
                }                            
            }
            rs.close();
            statement.close();
        
        return status;
    }
    public boolean router_mtplinkrxutilisation(String dateYYYYMMDD,Integer time) throws SQLException{
        boolean status = true;
        Statement statement;
        statement = dbInterface.connection.createStatement();
        ResultSet rs =   statement.executeQuery(statementBuilder("router_mtplinkrxutilisation",dateYYYYMMDD,time));
             
            while (rs.next()) {
                if (Math.abs(Float.valueOf(rs.getString("Diff")))>100){
                    System.out.println("router_mtplinkrxutilisation Is Increased by: " +rs.getString("Diff") +" on "+ rs.getString("Date") +
                                        " SMS Node: " + rs.getString("SMS_Node_Name")+
                                        " At: "+ rs.getString("Time"));
                    status=false;
                }                            
            }
            rs.close();
            statement.close();
        
        return status;
    }
    public boolean ams_queuesizeexceeded(String dateYYYYMMDD,Integer time) throws SQLException{
        boolean status = true;
        Statement statement;
        statement = dbInterface.connection.createStatement();
        ResultSet rs =   statement.executeQuery(statementBuilder("ams_queuesizeexceeded",dateYYYYMMDD,time));
             
            while (rs.next()) {
                if (Math.abs(Float.valueOf(rs.getString("Diff")))>100){
                    System.out.println("ams_queuesizeexceeded Is Increased by: " +rs.getString("Diff") +" on "+ rs.getString("Date") +
                                        " SMS Node: " + rs.getString("SMS_Node_Name")+
                                        " At: "+ rs.getString("Time"));
                    status=false;
                }                            
            }
            rs.close();
            statement.close();
        
        return status;
    }
    public boolean ams_amscnttotalstored(String dateYYYYMMDD,Integer time) throws SQLException{
        boolean status = true;
        Statement statement;
        statement = dbInterface.connection.createStatement();
        ResultSet rs =   statement.executeQuery(statementBuilder("ams_amscnttotalstored",dateYYYYMMDD,time));
             
            while (rs.next()) {
                if (Math.abs(Float.valueOf(rs.getString("Diff")))>100){
                    System.out.println("ams_amscnttotalstored Is Increased by: " +rs.getString("Diff") +" on "+ rs.getString("Date") +
                                        " SMS Node: " + rs.getString("SMS_Node_Name")+
                                        " At: "+ rs.getString("Time"));
                    status=false;
                }                            
            }
            rs.close();
            statement.close();
        
        return status;
    }
    public boolean cm_polls(String dateYYYYMMDD,Integer time) throws SQLException{
        boolean status = true;
        Statement statement;
        statement = dbInterface.connection.createStatement();
        ResultSet rs =   statement.executeQuery(statementBuilder("cm_polls",dateYYYYMMDD,time));
             
            boolean count5=false;
            boolean count1=false;
            String SMS_Node=new String();
            while (rs.next()) {
                if (Float.valueOf(rs.getString("Count"))==5 && rs.getString("app_path").compareToIgnoreCase("/usr/TextPass/TS/bin/poll_mgr2")==0) count5=true;
                if (Float.valueOf(rs.getString("Count"))==1 && rs.getString("app_path").compareToIgnoreCase("poll")==0) count1=true;
                SMS_Node=rs.getString("SMS_Node_Name");
            }
            if  (count1!=true || count5!=true){
                System.out.println("cm_polls Count is Incorrect on "+ dateYYYYMMDD +
                                    " SMS Node: " + SMS_Node+
                                    " At: "+ String.valueOf(time));
                status=false;
            }
            rs.close();
            statement.close();
        
        return status;
    }
    public void setDbstate(boolean dbstate) {
        this.dbstate = dbstate;
    }

    public boolean isDbstate() {
        return dbstate;
    }
}
