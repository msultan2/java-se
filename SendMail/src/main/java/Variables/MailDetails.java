package Variables;

import java.util.ArrayList;


public class MailDetails {
    String user_auth;
    String user_password;
    String host;
    String port;
    String Pop3_Link;
    String AccountMail;
    ArrayList PendingMails=new ArrayList();
    
    public void setUser_auth(String user_auth) {
        this.user_auth = user_auth;
    }

    public String getUser_auth() {
        return user_auth;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPort() {
        return port;
    }

    public void setPop3_Link(String pop3_Link) {
        this.Pop3_Link = pop3_Link;
    }

    public String getPop3_Link() {
        return Pop3_Link;
    }

    public void setAccountMail(String accountMail) {
        this.AccountMail = accountMail;
    }

    public String getAccountMail() {
        return AccountMail;
    }

    public void setPendingMails(ArrayList pendingMails) {
        this.PendingMails = pendingMails;
    }

    public ArrayList getPendingMails() {
        return PendingMails;
    }
}
