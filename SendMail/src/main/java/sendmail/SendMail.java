package sendmail;

import DBInterfaces.DBInterface;

import Parameters_Conf.Parameters;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import Variables.MailDetails;

//public class SendMail {
//    public SendMail() {
//    }
//
//    public static void main(String[] args) {
//        SendMail sendMail = new SendMail();
//    }
//}


public class SendMail {
    
    public static boolean  status;
    public static MailDetails mailDetails = new MailDetails();
    
    public static void main(String [] args) {

        try{
            if (args[0].length()==0 || args[1].length()==0 || args[2].length()==0 || args[3].length()==0){
                System.out.println("PLZ use java -jar SendMail TO,Subject,Body,From");
                                       
                }
            else{
                SendMail(args[0],args[1],args[2],args[3]);
                }
            }catch (ArrayIndexOutOfBoundsException E){
                System.out.println("PLZ use java -jar SendMail TO,Subject,Body,From");
//                SendMail("mohamed.sultan@vodafone.com","Subject","Body");
//                SendMail("XX","Subject","Body");
            }
    }

    public static void SendMail(String To,String Subject,String Mail,String from) {
//        String from = Parameters.MailFrom;
//        String to = Parameters.MailTo;
//        String Subject = Parameters.MailSubject;
//        String MessageText = Parameters.MailBody;
        
        get_Mail_Details();        
        
        Properties mailProp = new Properties();
//        System.out.println(mailDetails.getHost());
        mailProp.put("mail.smtp.host",mailDetails.getHost()); 
        mailProp.put("mail.smtp.port",mailDetails.getPort());
        mailProp.put("mail.smtp.starttls.enable","true");
        mailProp.put("mail.smtp.auth","true");
        mailProp.put("mail.smtp.socketFactory.port",mailDetails.getPort());
//        mailProp.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        mailProp.put("mail.smtp.socketFactory.fallback","false");

        mailProp.put("mail.debug", "false");

       
         try {
        Authenticator myAuth = new MyAuthenticator();
        Session session = Session.getDefaultInstance(mailProp,myAuth);
        
//        session.setDebug(true);

        MimeMessage mymsg = new MimeMessage(session);
//        mymsg.setText(Mail);
        mymsg.setSubject(Subject);
        mymsg.setFrom(new InternetAddress(from));
        mymsg.setContent(Mail,"text/html");
        
//        ArrayList<InternetAddress> x = new ArrayList<InternetAddress>();
//        
//        x.add(new InternetAddress("mohamed.sultan@vodafone.com"));
//             x.add(new InternetAddress("cnp.vas@vodafone.com"));
//        

//        int x=2;
//        InternetAddress[] mailList=new InternetAddress[x];
//             mylist[0]=new InternetAddress("mohamed.sultan@vodafone.com");
//             mylist[1]=new InternetAddress("cnp.vas@vodafone.com");
             InternetAddress[] mailList=spliter(To);
//        mymsg.setRecipient(Message.RecipientType.TO, new InternetAddress(To));
             mymsg.addRecipients(Message.RecipientType.TO, mailList);
        System.out.println("Sending Mail ... "+Message.RecipientType.TO);
        Transport.send(mymsg);
//        session.getDebugOut();

         } catch (Exception e){
            e.printStackTrace();
        }
    }
    private static InternetAddress[] spliter(String Receipients){
        ArrayList<String> items = new  ArrayList<String>(Arrays.asList(Receipients.split(";")));
        InternetAddress[] mylist=new InternetAddress[items.size()];
        Iterator it = items.iterator();
        
        
        int i=0;
        while (it.hasNext()){
            String Email=(String)it.next();
            try {
                mylist[i]=new InternetAddress(Email);
            } catch (AddressException e) {
                // TODO
            }
            i++;
        }
      return mylist;
    }
   private static class MyAuthenticator extends javax.mail.Authenticator{
      @Override
      public PasswordAuthentication getPasswordAuthentication()
       {
           return new PasswordAuthentication(mailDetails.getUser_auth(), mailDetails.getUser_password());
       }
   }
    private static void  get_Mail_Details() {
        DBInterface DB_Connection=new DBInterface("MYSQL","00:00");
        long wait_ = 1000;
        mailDetails.setHost(Parameters.MailHost);
        mailDetails.setAccountMail(Parameters.MailAccount);
        mailDetails.setPop3_Link(Parameters.MailPop3_Link);
        mailDetails.setPort(Parameters.MailPort);
        mailDetails.setUser_auth(Parameters.MailUser_auth); 

        if (Parameters.MailPassword == null || Parameters.MailPassword.compareTo("XXX")==0){
            try{
            status = 
                DB_Connection.connectToDB(Parameters.DB_IP, Parameters.DB_Port, 
                                          Parameters.DB_Name, 
                                          Parameters.DB_UserName, 
                                          Parameters.DB_Password, 
                                          Parameters.DB_Type, wait_);
                try {
                    ResultSet RS = DB_Connection.executeSQL_RS("SELECT CNPVAS_Password FROM Scheduler_Mail_Conf");
                    while (RS.next()){
                        mailDetails.setUser_password(RS.getString("CNPVAS_Password"));
                    }
                    RS.close();
                    } catch (SQLException e) {
                        System.out.println("Error inside getting mail details:"+e.getMessage());
                        }
//                try {
//                    ResultSet RS = DB_Connection.executeSQL_RS("SELECT * FROM PendingMails");
//                    int j=1;
//                    while (RS.next()){
//                     
//                        newMap=PropertyHandler.getProperty("ExportMap"+String.valueOf(j));
//                        
//                        while (newMap!=null && newMap!="" && newMap.length()>0){
//                            ExportMap.add(newMap);
//                            j++;
//                           newMap=PropertyHandler.getProperty("ExportMap"+String.valueOf(j));
//                           }
//                        mailDetails.setUser_password(RS.getString("CNPVAS_Password"));
//                    }
//                    RS.close();
//                    } catch (SQLException e) {
//                        System.out.println("Error inside getting mail details:"+e.getMessage());
//                        }
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        else{
            mailDetails.setUser_password(Parameters.MailPassword);
        }
        
    }
}