package DBInterfaces;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Properties;

public class DBInterface {
  public Connection connection;

  private void displaySQLErrors(SQLException e) {
    System.out.println("SQLException: " + e.getMessage());
    System.out.println("SQLState:     " + e.getSQLState());
    System.out.println("VendorError:  " + e.getErrorCode());
  }

  public DBInterface(String dbType) {
    try {
        if (dbType.compareToIgnoreCase("MySQL")==0){
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }
        else if (dbType.compareToIgnoreCase("SQL")==0 || dbType.compareToIgnoreCase("SQLServer")==0){
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        }
    } catch (Exception e) {
      System.err.println("Unable to find and load driver");
      System.exit(1);
    }
  }

  public boolean connectToDB(String serverIP, String port, String dbName, 
                            String userName, String password,String dbType) {
        boolean status=false;
        try {
            if (connection == null || connection.isClosed()) {
                try {
                    Properties props = new Properties();
                    props.setProperty("user", userName);
                    props.setProperty("password", password);
                    if (dbType.compareToIgnoreCase("MySQL")==0){
                    connection = 
                            DriverManager.getConnection("jdbc:mysql://" + serverIP + 
                                                        ":" + port + "/" + 
                                                        dbName, props);
                    status= true;
                    }
                    else if (dbType.compareToIgnoreCase("SQL")==0){
                        connection = 
                                DriverManager.getConnection("jdbc:sqlserver://" + 
                                                            serverIP + ":" + 
                                                            port + 
                                                            ";databaseName=" + 
                                                            dbName + 
                                                            ";user=" + 
                                                            userName + 
                                                            ";password=" + 
                                                            password + ";");
                        status= true;
                    }
                } catch (SQLException e) {
                    displaySQLErrors(e);
                }
            }
        } catch (SQLException e) {
            displaySQLErrors(e);
        }   
    return status;
    }

    public void executeSQL(String queryStatement ) throws SQLException{
      boolean status = false;
      Statement statement = connection.createStatement();
      //ResultSet rs = statement.executeQuery(queryStatement);
       statement.executeUpdate(queryStatement);
       
//      while (rs.next()) {
//        System.out.println(rs.getString("Date_"));
//        System.out.println(rs.getString("b") + " " + rs.getBoolean("MO_Success_SMS"));
//        System.out.println(rs.getString("c") + " " + rs.getBoolean("c"));
//        System.out.println(rs.getString("d") + " " + rs.getBoolean("d"));
//      }

      //rs.close();
      statement.close();
      connection.close();
  }
    public ResultSet executeSQL_RS(String queryStatement ) throws SQLException{
      boolean status = false;
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(queryStatement);
       
    //      while (rs.next()) {
    //        System.out.println(rs.getString("Date_"));
    //        System.out.println(rs.getString("b") + " " + rs.getBoolean("MO_Success_SMS"));
    //        System.out.println(rs.getString("c") + " " + rs.getBoolean("c"));
    //        System.out.println(rs.getString("d") + " " + rs.getBoolean("d"));
    //      }

      //rs.close();
//      statement.close();
//      connection.close();
    return rs;
    }
    public void closeConnection(){
        
        boolean isClosed=false;

        try {
            isClosed = connection.isClosed();
        } catch (SQLException e) {
            // TODO
        }
        if (!isClosed) {
            try {
                connection.close();
            } catch (SQLException e) {
                // TODO
            }
        }
    }
  public static void main(String[] args) {
    DBInterface hello = new DBInterface("SQL");
    
//    hello.connectToDB("localhost","3306","SMS","SMS","SMS","MySQL");
    hello.connectToDB("10.230.97.31","1433","CCN_Stats","CCN_User","CCN","SQL");
//        try {
//            ResultSet rs =  hello.executeSQL("Select * from ccn_counters");
//        } catch (SQLException e) {
//            // TODO
//        }
    }
}
