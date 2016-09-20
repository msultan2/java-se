package oracle.util;


//import drs.GenericSubInfo;
//import drs.GenericSubInfo;
//import drs.Payment;

//import drs.PaymentCharge;

//import interpromo.FacePromo;

import java.util.logging.Level;
import java.util.logging.Logger;
import promo.util.PromoParameters;
import java.sql.*;

import java.io.*;


import java.util.Map;
import oracle.jdbc.OracleTypes;
import promo.util.PromoLogger;

 
public class OracleDao {

    private String dbname;
    //private PrintWriter dbLog;
    //private DBInfo db;
    
    private Connection conn = null;
    private Statement qStmt = null; //query statement
    private Statement updtStmt = null; //update statement
    private PreparedStatement pstmt = null;

    private CallableStatement insertAdjLogCS = null;
    private CallableStatement recharge_StatusCS = null;
    private CallableStatement test_cursCS = null;
    private CallableStatement optIns_smsCS = null;
    private CallableStatement updateSubCS = null;
    private CallableStatement smsCS = null;
   // private CallableStatement cs = null;
    private ResultSet rset = null;
    //private String table1;
    //private static DslLogger logger = new DslLogger(OracleDao.class);
    //private boolean usePool = true;

    public static final String procAdjustmedLog = "D_insertAdjLog";
    public static final String procRchrgStatus = "Recharge_Status";
    public static final String procTestCurs = "TEST_CURS";
    public static final String procMSG_Cursor = "MSG_Cursor";
    public static final String procUPDATE_SUB = "update_sub";
    public static final String procSendSMS = "SENDSMS";

    private PrintWriter dbReadFailLog = null;     //Log DB failures when checking/getting subs
    private PrintWriter dbUpdateFailLog = null;     //Log DB failures when updating failed subs
    private PrintWriter dbSMSFailLog = null;     //Log DB failures when sending calling SMPP Stored Proc


    private static PromoLogger logger = new PromoLogger(OracleDao.class);
    
    public OracleDao(String dbname) {
        this.dbname = dbname;
        //this.dbLog = dbLog;
    }

    public boolean getConnection() {     
        try {
            conn = getPooledConnection(dbname);
        }
        catch (Exception e) {
             //e.printStackTrace();
             logger.error("Obtaining connection from pool FAILED !", e);
             System.out.println("Obtaining connection from pool FAILED !");
             return false;
        }
        if (conn == null) {
            logger.error("No connection available from pool !", null);
            System.out.println("No connection available from pool !");
            return false;
        }
        return true;
    }
    
    private Connection getPooledConnection(String databaseName) {
        
        try {
            if (PoolManager.getInstance() != null)
                conn = PoolManager.getInstance().getConnection(databaseName);
                System.out.println("Pool Manager Connection: databaseName: "+databaseName);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("Pool Manager failed to get Connection: ", e);
            System.out.println("Pool Manager failed to get Connection: ");
            return null;
        }
        return conn;

    }


    public PreparedStatement prepareStatement(String pstmtString) {
        try {

      //      ((OracleConnection)conn).setDefaultExecuteBatch(DslConstants.ChargingDB.BATCH_SIZE);
            //pstmtString = pstmtString.replaceAll("<table1>",db.getTable1());
            pstmt = conn.prepareStatement(pstmtString);
        } catch (Exception ex) {
            //ex.printStackTrace();
            logger.error("Preparing Statement FAILED !", ex);
            System.out.println("Preparing Statement FAILED !");
            return null;
        }
        return pstmt;
    }

    public void closeStmt() {
        try {
            qStmt.close();
        } catch (Exception ex) {
            //ex.printStackTrace();
            logger.error("Close Statement FAILED !", ex);
        }
    }

    public ResultSet execStatmnt(String stmtString) {
        try {
            //stmt = conn.createStatement();//ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            // stmtString = stmtString.replaceAll("<table1>",db.getTable1());
            qStmt = conn.createStatement();
            rset = qStmt.executeQuery(stmtString);
            //stmt.close();
        } catch (Exception ex) {
            //ex.printStackTrace();
            logger.error("Select Statement FAILED !", ex);
            return null;
        }
        return rset;
    }

    public int execUpdate(String stmtString) {
        //stmtString = stmtString.replaceAll("<table1>",db.getTable1());

        try {
            updtStmt = conn.createStatement();
            int i = updtStmt.executeUpdate(stmtString);
            updtStmt.close();
            return i;

        } catch (Exception ex) {
            //ex.printStackTrace();
            logger.error("Update Statement FAILED !", ex);
            return -1;
        }
    }

    public void closeConnections() {
        try {
            if (conn == null){
                logger.debug("Trying to Close a null Connection..did not !");
                return;
            }
            if ( ! conn.isClosed() ){
                 logger.debug("Closing Connection... !");
                 conn.close();
            }        
       } catch (Exception ex) {
            //ex.printStackTrace();
            logger.error("Closing Connection failed... !",ex);
        }
    }

    //---------------------------Clean Up-------------------------------------
    public void closeSatements(){
        
        try{
            if( insertAdjLogCS != null ){
                insertAdjLogCS.close();
            }
        }catch (Exception ex){
            logger.error("Error when closing insert adjLogs Stms ",ex);
        }
        
       try{ 
            if( recharge_StatusCS != null ){
                recharge_StatusCS.close();
            }
        }catch (Exception ex){
            logger.error("Error when closing insert recharge_status Stms ",ex);
        }
        
        try{
            if( smsCS != null){
                smsCS.close();
            }   
        }catch (Exception ex){
            logger.error("Error when closing insert SMS Stms ",ex);
        }

        try{
            if( test_cursCS != null){
                test_cursCS.close();
            }
        }catch (Exception ex){
            logger.error("Error when closing test Cursors Stms ",ex);
        }

        try{
            if( optIns_smsCS != null){
                optIns_smsCS.close();
            }
        }catch (Exception ex){
            logger.error("Error when closing Opt In Stms ",ex);
        }

        try{
            if( updateSubCS != null){
                updateSubCS.close();
            }
        }catch (Exception ex){
            logger.error("Error when closing updateSub Stms ",ex);
        }

       
    }

    public void closeDao(){

        this.closeSatements();
        this.closeConnections();

         if(dbReadFailLog != null ){
                dbReadFailLog.close();
            }

        if(dbUpdateFailLog != null ){
                dbUpdateFailLog.close();
        }

        if(dbSMSFailLog != null ){
                dbSMSFailLog.close();
        }
    }

    /**
	 * Method main
	 *
	 *
	 * @param args
	 *
	 */

    public static String getContents(File aFile) throws IOException {
        //...checks on aFile are elided
        StringBuilder contents = new StringBuilder();

        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(aFile));
            try {
                String line = null; //not declared within while loop
        /*
                 * readLine is a bit quirky :
                 * it returns the content of a line MINUS the newline.
                 * it returns null only for the END of the stream.
                 * it returns an empty String if two newlines appear in a row.
                 */
                line = input.readLine();
                //while ((line = input.readLine()) != null) {
                {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return contents.toString();
    }
}