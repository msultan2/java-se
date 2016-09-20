/*
 * DBInfo.java
 *
 * Created on October 30, 2007, 12:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oracle.util;

/**
 *
 * @author anassar
 */
public class DBInfo {
    
    private String dbHostName;
    private String dbIP;
    private Integer port;
    private String uname;
    private String passwd;
    private String table1;
    
    /** Creates a new instance of DBInfo */
    public DBInfo(String dbHostName,String dbIP,Integer port,
            String uname,String passwd,String table1) {
        this.dbHostName = dbHostName;
        this.dbIP = dbIP;
        this.port = port;
        this.uname = uname;
        this.passwd = passwd;
        this.table1 = table1;
    }
    
    public String getDbhost() {
        return dbHostName;
    }

    public void setDbhost(String dbHostName) {
        this.dbHostName = dbHostName;
    }
    
     public String getDbIP() {
        return dbIP;
    }

    public void setDbIP(String dbIP) {
        this.dbIP = dbIP;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
    
     public String getConnectionString() {
        return this.dbIP+":"+this.port.toString()+":"+this.dbHostName;
    }

    public String getTable1() {
        return table1;
    }

    public void setTable1(String table1) {
        this.table1 = table1;
    }

   

    
    
   
    
    
    
    
}
