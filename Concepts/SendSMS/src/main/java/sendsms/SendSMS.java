package sendsms;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class SendSMS {
    public Connection connection;

    private String driver = "oracle.jdbc.driver.OracleDriver";

    private String url = "jdbc:oracle:thin:@";

    private String port = "1521";

    private Connection conn = null;

    public SendSMS(String host, String db, String user, String password)
        throws ClassNotFoundException, SQLException {

        // construct the url
        url = url + host + ":" + port + ":" + db;

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


     public void callSP(String From, String To, String msgText){
        try
        {
            String APPID = "SD_KLMNA_I";
            String OrigMSISDN = From;
            String DstMSISDN = To;
            String Msgtxt = msgText;
            int MstType = 0;
            int OrigType = 2;
            int LangID=0;

            CallableStatement proc =
                conn.prepareCall("{ call smpp6.SendSMS(?, ?,?,?,?,?,?) }");
            proc.setString(1, APPID);
            proc.setString(2, OrigMSISDN);
            proc.setString(3, DstMSISDN);
            proc.setString(4, Msgtxt);
            proc.setInt(5, MstType);
            proc.setInt(6, OrigType);
            proc.setInt(7, LangID);
            proc.execute();
        }
        catch (SQLException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void main(String[] args) throws Exception {

//        if (args.length != 5) {
//            SendSMS.usage();
//            System.exit(1);
//        }
//        else {
            try {
                // assign the args to sensible variables for clarity
//                String host = args[0];
//                String db = args[1];
//                String user = args[2];
//                String password = args[3];
//                float price = Float.valueOf(args[4]).floatValue();
 SendSMS jdbc = new SendSMS("172.28.37.216", "SMPPRD1", "VAS_NOT", "VAS_NOT_456");
jdbc.callSP("0109239385","01012815946","allah ");
 jdbc.cleanup();


            /* if (args[0].length()==0 || args[1].length()==0 || args[2].length()==0 ){
                 System.out.println("PLZ use java -jar SendSMS.jar From To Message_Text");
                     
                 }
             else{
                 SendSMS jdbc = new SendSMS("172.28.37.216", "SMPPRD1", "VAS_NOT", "VAS_NOT_456");
                 jdbc.callSP(args[0],args[1],args[2]);
                 jdbc.cleanup();
                 }*/
             }catch (ArrayIndexOutOfBoundsException E){
                 System.out.println("PLZ use java -jar SendSMS.jar From To Message_Text");
             //                SendMail("XX","Subject","Body");
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