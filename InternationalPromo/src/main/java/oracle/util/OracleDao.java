package oracle.util;


//import drs.GenericSubInfo;
import drs.GenericSubInfo;
import drs.Payment;

import drs.PaymentCharge;

import interpromo.FacePromo;

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

    //------------------------Stored Procedures-------------------------------

    public String execProc_InsertAdjLog(Payment paySub)
    throws Exception {

        if(insertAdjLogCS == null){
                insertAdjLogCS = conn.prepareCall("{call " + OracleDao.procAdjustmedLog
                            + "(?,?,?,?,?,?,?)}");
        }

        //msisdn,SERVICE_CLASS,CLIENT_ID,ADJ_AMOUNT,CARD_AMOUNT,ACTIVATION_CODE,sqlStatus
        insertAdjLogCS.setString(1, paySub.getSubscriberNumber() );
        insertAdjLogCS.setString(2, paySub.getServiceClass().toString());
        insertAdjLogCS.setString(3, "Payments");
        insertAdjLogCS.setString(4, paySub.getPromoTransactionAmount().toString());
        insertAdjLogCS.setString(5, paySub.getRealTransactionAmount().toString());
        insertAdjLogCS.setString(6, "00000000000000");
        insertAdjLogCS.registerOutParameter(7, Types.VARCHAR);

        // Execute the stored procedure and retrieve the OUT value
        insertAdjLogCS.execute();

        String status = insertAdjLogCS.getString(7);     // OUT parameter
       logger.debug("In Stored proc D_insertAdjLog with status " + status +" !");
       return status;
    }

    public String execProc_RechargeStatus(Payment paySub)
    throws Exception {

        logger.debug("In Stored proc Recharge_Status......!");

        if(recharge_StatusCS == null){
               recharge_StatusCS =  conn.prepareCall("{call " + OracleDao.procRchrgStatus
                            + "(?,?,?)}");
        }

        //msisdn,status,sqlstatus
        recharge_StatusCS.setString(1, paySub.getSubscriberNumber() );
        recharge_StatusCS.registerOutParameter(2, Types.VARCHAR);
        recharge_StatusCS.registerOutParameter(3, Types.VARCHAR);

        // Execute the stored procedure and retrieve the OUT value
        recharge_StatusCS.execute();
        String status = recharge_StatusCS.getString(2);     // OUT parameter
        //recharge_StatusCS.close();
        logger.debug("Stored proc Recharge_Status finished with status " + status +" !");
        return status;
    }

    public String execProc_SENDSMS(Payment paySub)
    throws Exception {

        logger.debug("In Stored proc SENDSMS.....!");
        if(smsCS == null){
              smsCS =  conn.prepareCall("{call "+ OracleDao.procSendSMS
                      + "(?,?,?,?,?,?,?,?)}");
        }

        smsCS.setString(1, PromoParameters.SMS_APP_ID);
        smsCS.setString(2, PromoParameters.SMS_SENDER);
        smsCS.setString(3, "20"+paySub.getSubscriberNumber());
        smsCS.setString(4, paySub.getSmsText());
        smsCS.setInt(5, 0);
        smsCS.setInt(6, 2);
        smsCS.setInt(7, 1);
        smsCS.setInt(8, 0);

        smsCS.execute();
        
        return "Notified";
    }

    public String execProc_SENDSMS(String msisdn,String smsText)
    throws Exception {

         logger.debug("In Stored proc SENDSMS.....!");
        if(smsCS == null){
              smsCS =  conn.prepareCall("{call "+ OracleDao.procSendSMS
                      + "(?,?,?,?,?,?,?)}");
        }

        smsCS.setString(1, PromoParameters.SMS_APP_ID);
        smsCS.setString(2, PromoParameters.SMS_SENDER);
        smsCS.setString(3, "20"+ msisdn);
        smsCS.setString(4, smsText);
        smsCS.setInt(5, 0);
        smsCS.setInt(6, 2);
        smsCS.setInt(7, 1);
        //smsCS.setInt(8, 1);

        smsCS.execute();

        return "Notified";
     }

    public void execProc_TestCURS(Map<String,GenericSubInfo> subMap)
    throws Exception {

        logger.debug("In Stored proc " + OracleDao.procTestCurs +  "!");

        if(test_cursCS == null){
             test_cursCS =  conn.prepareCall("{call " + OracleDao.procTestCurs
                     + "(?)}");
        }

        //CURSOR Output
        test_cursCS.registerOutParameter(1, OracleTypes.CURSOR);

        // Execute the stored procedure and retrieve the OUT value
        test_cursCS.execute();
        ResultSet rs = (ResultSet) test_cursCS.getObject(1);     // OUT parameter

        while (rs.next())
            subMap.put(rs.getString(1), new GenericSubInfo(rs.getString(1),rs.getString(2),
                    rs.getString(4),rs.getInt(5)));

        {
            //String name = rs.getString(1);
            //String status = rs.getString(2);
            //int age = rs.getInt(2);
            //System.out.println(name+" "+status);

        }
        rs.close();
    }

    public void execProc_MSG_Cursor(Map<String,GenericSubInfo> subMap)
    throws Exception {

        logger.debug("In Stored proc " + OracleDao.procMSG_Cursor +  "!");

        if(optIns_smsCS == null){
             optIns_smsCS =  conn.prepareCall("{call " + OracleDao.procMSG_Cursor
                     + "(?)}");
        }

        //CURSOR Output
        optIns_smsCS.registerOutParameter(1, OracleTypes.CURSOR);

        // Execute the stored procedure and retrieve the OUT value
        optIns_smsCS.execute();
        ResultSet rs = (ResultSet) optIns_smsCS.getObject(1);     // OUT parameter

        while (rs.next())
            subMap.put(rs.getString(1),new GenericSubInfo(rs.getString(1),rs.getString(2),
                    rs.getString(4),rs.getInt(5)));

        {
            //String name = rs.getString(1);
            //String status = rs.getString(2);
            //int age = rs.getInt(2);
            //System.out.println(name+" "+status);

        }
        rs.close();
    }

    public void execProc_UPDATE_SUB(GenericSubInfo sub)
    throws Exception {

        if(updateSubCS == null){
             updateSubCS =  conn.prepareCall("{call " + OracleDao.procUPDATE_SUB
                     + "(?,?,?,?)}");
        }

        //msisdn,status,error code
        updateSubCS.setString(1, sub.getSubscriberNumber());
        if(sub.isError()){
            updateSubCS.setString(2,"FAILURE");
            updateSubCS.setString(3, Integer.toString(sub.getResult()));
        }else {
            updateSubCS.setString(2,"SUCCESS");
            updateSubCS.setString(3,Integer.toString(sub.getResult()));
        }
        updateSubCS.registerOutParameter(4, Types.VARCHAR);

        // Execute the stored procedure and retrieve the OUT value
        updateSubCS.execute();
        
        logger.debug("Update Procedure Return Status "+ updateSubCS.getString(4));

        logger.debug("Stored proc Recharge_Status finished !");

     }

    //------------------------------------------------------------------------
    public String execStoredProc(String procName, Payment paySub){
        try {

            //Adjustment and Subscriber info procedures
            if (procName.equals("D_insertAdjLog"))
            {
                if(insertAdjLogCS == null){
                    insertAdjLogCS = conn.prepareCall("{call " + procName + "(?,?,?,?,?,?,?)}");  
                }
                //msisdn,SERVICE_CLASS,CLIENT_ID,ADJ_AMOUNT,CARD_AMOUNT,ACTIVATION_CODE,sqlStatus
              
                insertAdjLogCS.setString(1, paySub.getSubscriberNumber() );
                insertAdjLogCS.setString(2, paySub.getServiceClass().toString());
                insertAdjLogCS.setString(3, "Payments");
                insertAdjLogCS.setString(4, paySub.getPromoTransactionAmount().toString());
                insertAdjLogCS.setString(5, paySub.getRealTransactionAmount().toString());
                insertAdjLogCS.setString(6, "00000000000000");
                insertAdjLogCS.registerOutParameter(7, Types.VARCHAR);

                // Execute the stored procedure and retrieve the OUT value
                insertAdjLogCS.execute();
                
                String status = insertAdjLogCS.getString(7);     // OUT parameter
               logger.debug("In Stored proc D_insertAdjLog with status " + status +" !");
                return status;
            }
            else if (procName.equals("Recharge_Status")){
                
                if(recharge_StatusCS == null){
                    recharge_StatusCS =  conn.prepareCall("{call " + procName + "(?,?,?)}");  
                }
                
                //msisdn,status,sqlstatus
                recharge_StatusCS.setString(1, paySub.getSubscriberNumber() );
                recharge_StatusCS.registerOutParameter(2, Types.VARCHAR);
                recharge_StatusCS.registerOutParameter(3, Types.VARCHAR);
                
                // Execute the stored procedure and retrieve the OUT value
                recharge_StatusCS.execute();
                String status = recharge_StatusCS.getString(2);     // OUT parameter
                //recharge_StatusCS.close();
                logger.debug("In Stored proc Recharge_Status with status " + status +" !");
                return status;
            }
            //SMS Procedures
            else if (procName.equals("SENDSMS")){
                
                if(smsCS == null){
                    smsCS =  conn.prepareCall("{call "+ procName + "(?,?,?,?,?,?,?,?)}");  
                }
                smsCS.setString(1, PromoParameters.SMS_APP_ID);
                smsCS.setString(2, PromoParameters.SMS_SENDER);
                smsCS.setString(3, "20"+paySub.getSubscriberNumber());
                smsCS.setString(4, paySub.getSmsText());
                smsCS.setInt(5, 0);
                smsCS.setInt(6, 2);
                smsCS.setInt(7, 1);
                smsCS.setInt(8, 0);  
                
                smsCS.execute();
                logger.debug("In Stored proc SENDSMS  !");
                
                return "Notified";
            }

            /*dbLog.println(paySub.getSubscriberNumber() + PromoParameters.oDELIM +
                    "Unknow Proc "+ procName + PromoParameters.oDELIM +
                    paySub.getRealTransactionAmount().toString() + PromoParameters.oDELIM +
                    paySub.getServiceClass() + PromoParameters.oDELIM +
                    paySub.getExternalData1() + PromoParameters.oDELIM +
                    paySub.getExternalData2() + PromoParameters.oDELIM +
                    paySub.getTimeStamp() + PromoParameters.oDELIM +
                    paySub.getPromoTransactionAmount().toString() + PromoParameters.oDELIM +
                    new java.util.Date().toString());*/
            return null;

        } catch (Exception ex) {
             logger.error(paySub.getSubscriberNumber()+
                    " Stored proc"+ procName +" FAILED !", ex);
            
            /*dbLog.println(paySub.getSubscriberNumber() + PromoParameters.oDELIM +
                    "Failed in "+ procName + PromoParameters.oDELIM +
                    paySub.getRealTransactionAmount().toString() + PromoParameters.oDELIM +
                    paySub.getServiceClass() + PromoParameters.oDELIM +
                    paySub.getExternalData1() + PromoParameters.oDELIM +
                    paySub.getExternalData2() + PromoParameters.oDELIM +
                    paySub.getTimeStamp() + PromoParameters.oDELIM +
                    paySub.getPromoTransactionAmount().toString() + PromoParameters.oDELIM +
                    new java.util.Date().toString());*/
            return null;
        }   
    }
    public String execStoredProc(String procName, PaymentCharge paySub){
        try {

            //Adjustment and Subscriber info procedures
            if (procName.equals("D_insertAdjLog"))
            {
                if(insertAdjLogCS == null){
                    insertAdjLogCS = conn.prepareCall("{call " + procName + "(?,?,?,?,?,?,?)}");  
                }
                //msisdn,SERVICE_CLASS,CLIENT_ID,ADJ_AMOUNT,CARD_AMOUNT,ACTIVATION_CODE,sqlStatus
              
                insertAdjLogCS.setString(1, paySub.getSubscriberNumber() );
                insertAdjLogCS.setString(2, paySub.getServiceClassCurrent().toString());
                insertAdjLogCS.setString(3, "Payments");
                insertAdjLogCS.setString(4, paySub.getPromoTransactionAmount().toString());
                insertAdjLogCS.setString(5, paySub.getRealTransactionAmount().toString());
                insertAdjLogCS.setString(6, "00000000000000");
                insertAdjLogCS.registerOutParameter(7, Types.VARCHAR);

                // Execute the stored procedure and retrieve the OUT value
                insertAdjLogCS.execute();
                
                String status = insertAdjLogCS.getString(7);     // OUT parameter
                logger.debug("In Stored proc D_insertAdjLog with status " + status +" !");
                return status;
            }
            else if (procName.equals("Recharge_Status")){
                
                if(recharge_StatusCS == null){
                    recharge_StatusCS =  conn.prepareCall("{call " + procName + "(?,?,?)}");  
                }
                
                //msisdn,status,sqlstatus
                recharge_StatusCS.setString(1, paySub.getSubscriberNumber() );
                recharge_StatusCS.registerOutParameter(2, Types.VARCHAR);
                recharge_StatusCS.registerOutParameter(3, Types.VARCHAR);
                
                // Execute the stored procedure and retrieve the OUT value
                recharge_StatusCS.execute();
                String status = recharge_StatusCS.getString(2);     // OUT parameter
                //recharge_StatusCS.close();
                logger.debug("In Stored proc Recharge_Status with status " + status +" !");
                return status;
            }
            //SMS Procedures
            else if (procName.equals("SENDSMS")){
                
                if(smsCS == null){
                    smsCS =  conn.prepareCall("{call "+ procName + "(?,?,?,?,?,?,?,?)}");  
                }
                smsCS.setString(1, PromoParameters.SMS_APP_ID);
                smsCS.setString(2, PromoParameters.SMS_SENDER);
                smsCS.setString(3, "20"+paySub.getSubscriberNumber());
                smsCS.setString(4, paySub.getSmsText());
                smsCS.setInt(5, 0);
                smsCS.setInt(6, 2);
                smsCS.setInt(7, 1);
                smsCS.setInt(8, 0);  
                
                smsCS.execute();
                logger.debug("In Stored proc SENDSMS  !");
                
                return "Notified";
            }

            /*dbLog.println(paySub.getSubscriberNumber() + PromoParameters.oDELIM +
                    "Unknow Proc "+ procName + PromoParameters.oDELIM +
                    paySub.getRealTransactionAmount().toString() + PromoParameters.oDELIM +
                    paySub.getServiceClass() + PromoParameters.oDELIM +
                    paySub.getExternalData1() + PromoParameters.oDELIM +
                    paySub.getExternalData2() + PromoParameters.oDELIM +
                    paySub.getTimeStamp() + PromoParameters.oDELIM +
                    paySub.getPromoTransactionAmount().toString() + PromoParameters.oDELIM +
                    new java.util.Date().toString());*/
            return null;

        } catch (Exception ex) {
             logger.error(paySub.getSubscriberNumber()+
                    " Stored proc"+ procName +" FAILED !", ex);
            
            /*dbLog.println(paySub.getSubscriberNumber() + PromoParameters.oDELIM +
                    "Failed in "+ procName + PromoParameters.oDELIM +
                    paySub.getRealTransactionAmount().toString() + PromoParameters.oDELIM +
                    paySub.getServiceClass() + PromoParameters.oDELIM +
                    paySub.getExternalData1() + PromoParameters.oDELIM +
                    paySub.getExternalData2() + PromoParameters.oDELIM +
                    paySub.getTimeStamp() + PromoParameters.oDELIM +
                    paySub.getPromoTransactionAmount().toString() + PromoParameters.oDELIM +
                    new java.util.Date().toString());*/
            return null;
        }   
    }
    public String execStoredProc(String procName, Payment paySub, PrintWriter pwfail){
            try {
                
                if (procName.equals("D_insertAdjLog"))
                {
                    if(insertAdjLogCS == null){
                        insertAdjLogCS = conn.prepareCall("{call " + procName + "(?,?,?,?,?,?,?)}");  
                    }
                    //cs = conn.prepareCall("{call " + procName + "(?,?,?,?,?,?,?)}");
                    //msisdn,SERVICE_CLASS,CLIENT_ID,ADJ_AMOUNT,CARD_AMOUNT,ACTIVATION_CODE,sqlStatus
                  
                    insertAdjLogCS.setString(1, paySub.getSubscriberNumber() );
                    insertAdjLogCS.setString(2, paySub.getServiceClass().toString());
                    insertAdjLogCS.setString(3, "Payments");
                    insertAdjLogCS.setString(4, paySub.getPromoTransactionAmount().toString());
                    insertAdjLogCS.setString(5, paySub.getRealTransactionAmount().toString());
                    insertAdjLogCS.setString(6, "00000000000000");
                    insertAdjLogCS.registerOutParameter(7, Types.VARCHAR);

                    // Execute the stored procedure and retrieve the OUT value
                    insertAdjLogCS.execute();
                    
                    String status = insertAdjLogCS.getString(7);     // OUT parameter
                    //insertAdjLogCS.close();
                    logger.debug("In Stored proc D_insertAdjLog with status " + status +" !");
                    return status;
                }
                else if (procName.equals("Recharge_Status")){
                    
                    if(recharge_StatusCS == null){
                        recharge_StatusCS =  conn.prepareCall("{call " + procName + "(?,?,?)}");  
                    }
                    
                    //cs = conn.prepareCall("{call " + procName + "(?,?,?)}");
                    //msisdn,status,sqlstatus
                    recharge_StatusCS.setString(1, paySub.getSubscriberNumber() );
                    recharge_StatusCS.registerOutParameter(2, Types.VARCHAR);
                    recharge_StatusCS.registerOutParameter(3, Types.VARCHAR);
                    
                    // Execute the stored procedure and retrieve the OUT value
                    recharge_StatusCS.execute();
                    String status = recharge_StatusCS.getString(2);     // OUT parameter
                    //recharge_StatusCS.close();
                    logger.debug("In Stored proc Recharge_Status with status " + status +" !");
                    return status;
                }
                else if (procName.equals("SENDSMS")){
                    
                    //cs = conn.prepareCall("{call "+ procName + "(?,?,?,?,?,?,?,?)}");
                    if(smsCS == null){
                        smsCS =  conn.prepareCall("{call "+ procName + "(?,?,?,?,?,?,?,?)}");  
                    }
                     
                    smsCS.setString(1, FacePromo.SMS_APP_ID);
                    smsCS.setString(2, FacePromo.SMS_SENDER);
                    smsCS.setString(3, "20"+paySub.getSubscriberNumber());
                    smsCS.setString(4, paySub.getSmsText());
                    smsCS.setInt(5, 0);
                    smsCS.setInt(6, 2);
                    smsCS.setInt(7, 1);
                    smsCS.setInt(8, 0);  
                    
                    smsCS.execute();
                    logger.debug("In Stored proc SENDSMS  !");
                    //smsCS.close();
                    return "Notified";
                }
                pwfail.println(paySub.getSubscriberNumber() + FacePromo.oDELIM +
                        "Unknow Proc "+ procName + FacePromo.oDELIM +
                        paySub.getRealTransactionAmount().toString() + FacePromo.oDELIM +
                        paySub.getServiceClass() + FacePromo.oDELIM +
                        paySub.getExternalData1() + FacePromo.oDELIM + 
                        paySub.getExternalData2() + FacePromo.oDELIM +
                        paySub.getTimeStamp() + FacePromo.oDELIM + 
                        paySub.getPromoTransactionAmount().toString() + FacePromo.oDELIM +
                        new java.util.Date().toString());
                return null;

            } catch (Exception ex) {
                //Logger.getLogger(OracleDao.class.getName()).log(Level.SEVERE, null, ex);
                //ex.printStackTrace();
                logger.error(paySub.getSubscriberNumber()+
                        " Stored proc"+ procName +" FAILED !", ex);
                
                pwfail.println(paySub.getSubscriberNumber() + FacePromo.oDELIM +
                        "Failed in "+ procName + FacePromo.oDELIM +
                        paySub.getRealTransactionAmount().toString() + FacePromo.oDELIM +
                        paySub.getServiceClass() + FacePromo.oDELIM +
                        paySub.getExternalData1() + FacePromo.oDELIM + 
                        paySub.getExternalData2() + FacePromo.oDELIM +
                        paySub.getTimeStamp() + FacePromo.oDELIM + 
                        paySub.getPromoTransactionAmount().toString() + FacePromo.oDELIM +
                        new java.util.Date().toString());
                return null;
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

    public static void main(String[] args) {
        
       PromoParameters pps = new PromoParameters();
       pps.validateParemeters();
       String sms2 = PromoParameters.SMS_SUCCESS_AR;
       String sms = null;
       String sms3 = null;
      // char[] arbic = '';
        try {
            System.out.println("System encoding " + System.getProperty("file.encoding"));
            //sms = new String(sms2.getBytes(),"cp1252");
            sms3 = new BufferedReader(new InputStreamReader(new FileInputStream("resources/test.txt"), "cp1256")).readLine().trim();
            //sms = new String((getContents(new File("resources/arabicMessage.txt"))).getBytes(),"cp1252");
            //JOptionPane.showConfirmDialog(null, sms);
            //sms = "\u00ca\u00e3 \u00c7\u00d6\u00c7\u00dd\u00e5 \u00c7\u00e1\u00d1\u00d5\u00ed\u00cf \u00c7\u00e1\u00d0\u00ec \u00ca\u00e3 \u00ce\u00d5\u00e3\u00e5 \u00c8\u00c7\u00e1\u00ce\u00d8\u00c3 \u00da\u00e4\u00cf\u00c7\u00e1\u00c7\u00d3\u00ca\u00da\u00e1\u00c7\u00e3 \u00da\u00e4 \u00c7\u00e1\u00d1\u00d5\u00ed\u00cf \u00ed\u00e6\u00e3 \u00c7\u00e1\u00cc\u00e3\u00da\u00e5";
            sms = new String(sms3.getBytes(),"cp1252");
            //JOptionPane.showMessageDialog(null, sms);
            //sms = new BufferedReader(new FileReader("SMS.txt")).readLine().trim();
        } catch (Exception ex) {
            Logger.getLogger(OracleDao.class.getName()).log(Level.SEVERE, null, ex);
        }
       System.out.println(sms);
       
       OracleDao oraSMPP = new OracleDao(PromoParameters.SMPP_DB);
       if (! oraSMPP.getConnection()){
           logger.error("Error in Creating Connection to SMPP...", null);
           return;
       }

        PrintWriter pwLog = null;
        BufferedReader clsBr = null;
        String line = null;
        //Integer value =0;

        try{
                pwLog = new PrintWriter(new BufferedWriter(new FileWriter(
                            "SMSLog.txt")) , true);
                clsBr = new BufferedReader(new FileReader("UCIPLog.txt"));

                line = clsBr.readLine().trim();

                while(line != null){
                     line = line.trim();
                     String[] arr = line.split(" ");
                     logger.debug(arr[0]+ " sending SMS ");
                     try{
                        oraSMPP.execProc_SENDSMS(arr[0],sms);
                      } catch (Exception ex) {
                        logger.error("SMS Notification for " + arr[0] + " failed....", ex);
                     }
                     //logger.debug(arr[1]+ "Adjusted "+ value.toString() + " with code: " + res);
                     pwLog.println(arr[0]+ " SMSed ");
                     line = clsBr.readLine();
                }

        }catch(Exception ex){
            ex.printStackTrace();
            logger.error("Error :(", ex);
        }

        oraSMPP.closeDao();

       if( PoolManager.releseAll()){
               logger.debug("Pool Manager Succefully leased.");
       }else
               logger.debug("Pool Manager already leased or was null.");
       }

}
