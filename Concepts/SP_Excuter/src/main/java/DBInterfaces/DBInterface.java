package DBInterfaces;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Properties;

//import promo.util.Parameter_Setter;


public class DBInterface {
    public Connection connection;
    private String serverIP_, port_, dbName_, userName_, password_, dbType_;
    public String StartTime;
    private Long wait_;

    private void displaySQLErrors(SQLException e, Long wait) {
        if (e.getSQLState().compareToIgnoreCase("08S01") == 0 && 
            e.getErrorCode() == 0) {
            System.out.println("System Is waiting for " + 
                               String.valueOf(wait) + " Seconds");
//            Logger.Log("System Is waiting for "+String.valueOf(wait) + " Seconds", StartTime,"Error");
            try {
                Thread.sleep(wait);
                connectToDB(serverIP_, port_, dbName_, userName_, password_, 
                            dbType_, wait_);
            } catch (InterruptedException f) {
                System.out.println("Sleep Exception:" + f.getMessage());
                //            Logger_Interface Logger = new Logger_Interface();
                //            DateUtils dateUtils = new DateUtils();
                //            String Start_Time = dateUtils.now("hh:mm");
//                Logger.Log(e.getMessage(), StartTime,"Error");
            }
        } else {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState:     " + e.getSQLState());
            System.out.println("VendorError:  " + e.getErrorCode());
//            Logger.Log(e.getMessage(), StartTime,"Error");
        }
    }

    public DBInterface(String dbType, String StartTime_IN) {
        StartTime = StartTime_IN;
        try {
            if (dbType.compareToIgnoreCase("MySQL") == 0) {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } else if (dbType.compareToIgnoreCase("SQL") == 0 || 
                       dbType.compareToIgnoreCase("SQLServer") == 0 ||
                       dbType.compareToIgnoreCase("SQLSecure") == 0) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            } else if (dbType.compareToIgnoreCase("Oracle") == 0) {
                Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            } else if (dbType.compareToIgnoreCase("Sybase") == 0) {
                //            Class.forName("com.sybase.jdbc3.jdbc.SybDriver").newInstance();
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            } else if (dbType.compareToIgnoreCase("ENIQ") == 0) {
                //            Class.forName("com.sybase.jdbc3.jdbc.SybDriver").newInstance();
                Class.forName("com.sybase.jdbc3.jdbc.SybDriver").newInstance();
            }
        } catch (Exception e) {
            System.err.println("Unable to find and load driver" + 
                               e.getMessage());
            //        Logger_Interface Logger = new Logger_Interface();
            //        DateUtils dateUtils = new DateUtils();
            //        String Start_Time = dateUtils.now("hh:mm");
//            Logger.Log(e.getMessage(), StartTime,"Error");
            System.exit(1);
        }
    }
    public boolean connectToDB(String serverIP, String port, String dbName, 
                               String userName, String password, String dbType, 
                               Long wait) {
        serverIP_ = serverIP;
        port_ = port;
        dbName_ = dbName;
        userName_ = userName;
        password_ = password;
        dbType_ = dbType;
        wait_ = wait;

        boolean status = false;
        try {
            if (connection == null || connection.isClosed()) {
                try {
                    Properties props = new Properties();
                    props.setProperty("user", userName);
                    props.setProperty("password", password);
                    if (dbType.compareToIgnoreCase("MySQL") == 0) {
                        connection = 
                                DriverManager.getConnection("jdbc:mysql://" + 
                                                            serverIP + ":" + 
                                                            port + "/" + 
                                                            dbName, props);
                        status = true;
                    } else if (dbType.compareToIgnoreCase("SQL") == 0) {
                        connection = 
                                DriverManager.getConnection("jdbc:sqlserver://" + 
                                                            serverIP + ":" + 
                                                            port + 
                                                            ";DatabaseName=" + 
                                                            dbName + ";user=" + 
                                                            userName + 
                                                            ";password=" + 
                                                            password + ";");
                        status = true;
                    } else if (dbType.compareToIgnoreCase("SQLSECURE") == 0) {
                        connection = 
                                DriverManager.getConnection("jdbc:sqlserver://" + 
                                                            serverIP + ":" + 
                                                            port + 
                                                            ";DatabaseName=" + 
                                                            dbName + ";integratedSecurity=true;authenticationScheme=JavaKerberos;");
                        status = true;
                    } 
                    else if (dbType.compareToIgnoreCase("Oracle") == 0) {
                        connection = 
                                DriverManager.getConnection("jdbc:oracle:thin:@" + 
                                                            serverIP + ":" + 
                                                            port + ":" + 
                                                            dbName, userName, 
                                                            password);
                        status = true;
                    } else if (dbType.compareToIgnoreCase("Sybase") == 0) {
                        //                        connection = 
                        //                                DriverManager.getConnection("jdbc:sybase:Tds:" + 
                        //                                                            serverIP + ":" + 
                        //                                                            port + 
                        //                                                            "/" + 
                        //                                                            dbName ,userName,password);
                        connection = 
                                DriverManager.getConnection("jdbc:jtds:sybase://" + 
                                                            serverIP + ":" + 
                                                            port + "/" + 
                                                            dbName, userName, 
                                                            password);
                        status = true;
                    } else if (dbType.compareToIgnoreCase("ENIQ") == 0) {
                        connection = 
                                DriverManager.getConnection("jdbc:sybase:Tds:" + 
                                                            serverIP + ":" + 
                                                            port + "/" + 
                                                            dbName, userName, 
                                                            password);
                        status = true;
                    }
                } catch (SQLException e) {
                    displaySQLErrors(e, wait);
                    //                    Logger_Interface Logger = new Logger_Interface();
                    //                    DateUtils dateUtils = new DateUtils();
                    //                    String Start_Time = dateUtils.now("hh:mm");
//                    Logger.Log(e.getMessage(), StartTime,"Error");
                }
            }
        } catch (SQLException e) {
            displaySQLErrors(e, wait);
//            Logger.Log(e.getMessage(), StartTime,"Error");
        }
        return status;
    }

    public void executeSQL(String queryStatement, 
                           boolean closeConnection) throws SQLException {
        boolean status = false;
        Statement statement = connection.createStatement();
        //ResultSet rs = statement.executeQuery(queryStatement);
        statement.executeUpdate(queryStatement);
        //rs.close();
        statement.close();
        if (closeConnection)
            connection.close();
    }

    public ResultSet executeSQL_RS(String queryStatement) throws SQLException {
        boolean status = false;
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(queryStatement);
        return rs;
    }

    public void closeConnection() {

        boolean isClosed = false;

        try {
            isClosed = connection.isClosed();
        } catch (SQLException e) {
//            Logger.Log(e.getMessage(), StartTime,"Error");
        }
        if (!isClosed) {
            try {
                connection.close();
            } catch (SQLException e) {
//                Logger.Log(e.getMessage(), StartTime,"Error");
            }
        }
    }

    public static void main(String[] args) {
        DBInterface hello = new DBInterface("SQL", "20:20");

        //    hello.connectToDB("localhost","3306","SMS","SMS","SMS","MySQL");
        //    hello.connectToDB("10.230.97.31","1433","CCN_Stats","CCN_User","CCN","SQL",5000);
        //        try {
        //            ResultSet rs =  hello.executeSQL("Select * from ccn_counters");
        //        } catch (SQLException e) {
        //            // TODO
        //        }
    }
}
