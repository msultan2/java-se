package DBInterfaces;

import java.io.*;
import java.sql.*;
import java.util.*;

public class TextToDatabaseTable {
   private static final String DB = "SMS",
                               TABLE_NAME = "records",
                               HOST = "jdbc:mysql://localhost:3306/",
                               ACCOUNT = "SMS", 
                               PASSWORD = "SMS",
                               DRIVER = "com.mysql.jdbc.Driver",
                               FILENAME = "records.txt";

   public static void main (String[] args) {
      try {

         // connect to db
         Properties props = new Properties();
         props.setProperty("user", ACCOUNT);
         props.setProperty("password", PASSWORD);

         Class.forName(DRIVER).newInstance();
         Connection con = DriverManager.getConnection( 
            HOST + DB, props); 
         Statement stmt = con.createStatement(); 

         // open text file
         BufferedReader in = new BufferedReader( 
                                new FileReader(FILENAME));

         // read and parse a line
         String line = in.readLine();
         while(line != null) {

            StringTokenizer tk = new StringTokenizer(line);
            String first = tk.nextToken(),
                   last = tk.nextToken(),
                   email = tk.nextToken(),
                   phone = tk.nextToken();

            // execute SQL insert statement
            String query = "INSERT INTO " + TABLE_NAME;
            query += " VALUES(" + quote(first) + ", ";
            query += quote(last) + ", ";
            query += quote(email) + ", ";
            query += quote(phone) + ");";
            stmt.executeQuery(query);

            // prepare to process next line
            line = in.readLine();
         }
         in.close();
      }

      catch( Exception e) { 
         e.printStackTrace();
      } 
   }

   // protect data with quotes
   private static String quote(String include) {
      return("\"" + include + "\"");
   }
}

