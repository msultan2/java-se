package sendmail;

import DBInterfaces.DBInterface;

import Parameters_Conf.Parameters;

import Variables.Attachment;
import Variables.BillingDetails;
import Variables.MailConverterDetails;
import Variables.MailDetails;
import Variables.Service_Details;

import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.QPDecoderStream;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.MailcapCommandMap;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.mail.util.SharedByteArrayInputStream;

import org.apache.commons.codec.binary.Base64;


public class CheckMail2 {
    public static MailDetails mailDetails = new MailDetails();
    public static DBInterface DB_Connection=new DBInterface("MYSQL","00:00");
    public static boolean  status;
    Integer GuardTime_Substract = 15;
    Integer GuardTime_Add = 30;
    public static long wait_ = 1000;
    enum Receipts{
        CNPVAS,MOHAMEDSULTAN,OTHERS,Mail2SMS,ITOSDAILYREPORT,DownloadAttachment
    }
    public CheckMail2() {
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);

        Format formatter;
        Properties mailProp = new Properties();
        mailProp.put("mail.smtp.host",mailDetails.getHost()); 
        mailProp.put("mail.smtp.port",mailDetails.getPort());
        mailProp.put("mail.smtp.starttls.enable","true");
        mailProp.put("mail.smtp.auth","true");
        mailProp.put("mail.smtp.socketFactory.port",mailDetails.getPort());
        //        mailProp.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        mailProp.put("mail.smtp.socketFactory.fallback","false");

         try {
        Authenticator myAuth = new MyAuthenticator();
        Session session = Session.getDefaultInstance(mailProp, myAuth); 
        Store store = session.getStore("pop3"); 
//        store.connect("owa.vodafone.com.eg", "CNPvas", "Sultan^666"); 
        store.connect(mailDetails.getPop3_Link(), mailDetails.getAccountMail(), mailDetails.getUser_password()); 
//            store.connect();
//        System.out.println(store);
        Folder inbox = store.getFolder("INBOX"); 
//        Folder outputFolder = store.getFolder("INBOX\\Processed");
        inbox.open(Folder.READ_WRITE);
        
        try{
                if (!inbox.exists()) {
                  System.out.println("No INBOX...");
                  System.exit(0);
                  }
                  if(!inbox.isOpen()){
                  inbox.open(Folder.READ_WRITE);}
                
                Message messages[] = inbox.getMessages();                            
                if (status) {
//                Logger_Interface Logger = new Logger_Interface();
                for (int i = 0; i < messages.length; i++) {
                    String from = InternetAddress.toString(messages[i].getFrom());
                    String subject = messages[i].getSubject();
                    Date sent = (Date)messages[i].getSentDate();
                    int TimeShift=0;
                    
                    try{
                    status = 
                        DB_Connection.connectToDB(Parameters.DB_IP, Parameters.DB_Port, 
                                                  Parameters.DB_Name, 
                                                  Parameters.DB_UserName, 
                                                  Parameters.DB_Password, 
                                                  Parameters.DB_Type, wait_);
                    
                    ResultSet RS = DB_Connection.executeSQL_RS("SELECT TimeShift FROM Main_Conf.Scheduler_Mail_Conf");
                            while (RS.next()){
                                TimeShift=RS.getInt("TimeShift");
                            }
                            RS.close();
                            } catch (SQLException e) {
                                // TODO
                                }
                    
                    System.out.println("TimeShift="+TimeShift);
                    
                    Calendar cal = Calendar.getInstance(); // creates calendar
                        cal.setTime(sent); // sets calendar time/date
                        cal.add(Calendar.HOUR_OF_DAY, TimeShift); // adds one hour
                       sent= cal.getTime(); // returns new date object, one hour in the future
                       
                    
                    Date Received = (Date)messages[i].getReceivedDate();
                    System.out.println("------------ Message " + (i + 1) + " ---------------------------------------------");
                    try{
                        formatter = new SimpleDateFormat("HH:mm");
                        String sentFormattedTime = formatter.format(sent);
                        formatter = new SimpleDateFormat("yyyy-MM-dd");
                        String sentFormattedDate = formatter.format(sent);
                        
//                        InputStream stream = messages[i].getInputStream();
//                         while (stream.available() != 0) {
//                         System.out.print((char) stream.read());
//                         }
                        String Body=GetBody(messages[i]);
                        
                        boolean statusUpdated=false;
                        System.out.println("From:"+from);
                        System.out.println("subject:"+subject);
                        System.out.println("sent:"+sentFormattedDate+","+sentFormattedTime);
                        System.out.println("Main:"+Body.substring(1,100));
                        statusUpdated = 
                                MailHandler(subject,from,Body,sentFormattedDate,sentFormattedTime,messages[i]);
                            System.out.println("status is " + statusUpdated);
                            if (statusUpdated == true){
                                messages[i].setFlag(Flags.Flag.DELETED, true);
// moveMessage(messages[i], outputFolder);
                                System.out.println("Mail Will be deleted");
                            }
                            else
                                System.out.println("Mail Will not be deleted");                            
                    } catch (Exception e) { //Catch exception if any
                                      System.err.println("Error: " + e.getMessage());
//                        MailHandler(subject,from1,"","");
                    }
                }
 
            } 
        }catch (Exception e) {
                System.out.println("Error:" + e.getMessage());
            }
        inbox.close(true);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
    }
    public void moveMessage(Message m, Folder to) throws MessagingException
    {
        m.getFolder().copyMessages(new Message[] {m}, to);
        m.setFlag(Flags.Flag.DELETED, true);
        m.getFolder().expunge();
    }
    public static void main(String[] args) {
        
        mailDetails.setHost("10.230.95.91");
        mailDetails.setAccountMail("CNP.VAS@vodafone.com");
        mailDetails.setPop3_Link("owa.vodafone.com.eg");
        mailDetails.setPort("25");
        mailDetails.setUser_auth("CNPVAS");    
        try{
        status = 
            DB_Connection.connectToDB(Parameters.DB_IP, Parameters.DB_Port, 
                                      Parameters.DB_Name, 
                                      Parameters.DB_UserName, 
                                      Parameters.DB_Password, 
                                      Parameters.DB_Type, wait_);
            try {
                ResultSet RS = DB_Connection.executeSQL_RS("SELECT CNPVAS_Password FROM Main_Conf.Scheduler_Mail_Conf");
                while (RS.next()){
                    mailDetails.setUser_password(RS.getString("CNPVAS_Password"));
                }
                RS.close();
                } catch (SQLException e) {
                    // TODO
                    System.out.println("Error in getting CNPVAS password:"+e.getMessage());
                    System.exit(0);
                    }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        CheckMail2 checkMail = new CheckMail2();
//        checkMail.Send2Mail2SMS("2013-09-14");
    }
    private class MyAuthenticator extends javax.mail.Authenticator{
       @Override
       public PasswordAuthentication getPasswordAuthentication(){
            return new PasswordAuthentication(mailDetails.getUser_auth(),mailDetails.getUser_password());
        }
    }
    private String GetServiceName(String Subject) {
    Integer Dash_Loc=Subject.indexOf(" - ");
    String X;
    if (Dash_Loc!=0){
        Subject=Subject.substring(0,Dash_Loc);
        Subject = Subject.replaceAll("FW:","");
        Subject = Subject.replaceAll("RE:","");
        Subject = Subject.replaceAll("Re: ","");
        Subject = Subject.replaceAll("Fw: ","");
        Subject = Subject.replaceAll("Fwd: ","");
        Subject = Subject.trim();
    }   
    return Subject;
    }
    private Service_Details CNPVASHandler_Min2Add(String subject){
        String ServiceName=GetServiceName(subject);
        String Query="SELECT ifnull(Minutes_To_Add_In_Check,0) Minutes_To_Add_In_Check FROM Main_Conf.Services WHERE `Service Name`='" + ServiceName + "' and Report_Activated=1";
        //    System.out.println(Query);
        ResultSet RS;
        Service_Details service_Details = new Service_Details();
        Integer Minutes=0;
        service_Details.setMinutes2Add(Minutes);
            try {
    //            System.out.println( DB_Connection.connection.isClosed());
                RS = DB_Connection.executeSQL_RS(Query);        
                while (RS.next()){
                    Minutes = RS.getInt("Minutes_To_Add_In_Check");
                    service_Details.setMinutes2Add(Minutes);
                }
         } catch (SQLException e) {
             System.out.println("Error:"+e.getMessage());
         }
        service_Details.setServiceName(ServiceName);
        return service_Details;
    }
    private boolean CNPVAS_Valid_Service(Service_Details service_Details,String SentOnDate,String SentOnTime){
        Boolean ServiceAvailability=false;
        String Query = 
    
        "Select Count(*) Count from Queues.Schedular_Log WHERE Date='"+SentOnDate+"' and ServiceName='" + 
        service_Details.getServiceName() + "' and " + "'" + SentOnTime + 
        "' BETWEEN addtime(CONVERT(concat(`Hours`, ':', `min`), TIME), '-0:" + 
                    GuardTime_Substract + 
                    ":00') and addtime(CONVERT(concat(`Hours`, ':', `min`), TIME)," +
                    "addtime(CONVERT('00:"+GuardTime_Add+":00',TIME), '0:" + 
                        service_Details.getMinutes2Add() + ":00'))";
//        System.out.println(Query);
        try {
            ResultSet RS = DB_Connection.executeSQL_RS(Query);
            Integer Count=0;
            while (RS.next()){
                Count = RS.getInt("Count");
                if (Count>0){
                    ServiceAvailability=true;
                }
                RS.close();
            }
            
            } catch (SQLException e) {
                // TODO
                }
        return ServiceAvailability;
    }
    private boolean CNPVAS_UpdateService(Service_Details service_Details,String SentOnDate,String SentOnTime){
        Boolean Status=false;
        String Query = 
                "update Queues.Schedular_Log " + "SET `Mail Status`='Mail Received',`Mail Delivery Date`=(select date_format(now(),'%Y-%m-%d %H:%i')) " + 
            "WHERE Date='"+SentOnDate+"' and ServiceName='" + 
                service_Details.getServiceName() + "' and " + "'" + SentOnTime + 
                "' BETWEEN addtime(CONVERT(concat(`Hours`, ':', `min`), TIME), '-0:" + 
                            GuardTime_Substract + 
                            ":00') and addtime(CONVERT(concat(`Hours`, ':', `min`), TIME)," +
                            "addtime(CONVERT('00:"+GuardTime_Add+":00',TIME), '0:" + 
                                service_Details.getMinutes2Add() + ":00'))";
//        System.out.println(Query);
        try{
            DB_Connection.executeSQL(Query, false);
            Status=true;
            }catch (SQLException e) {
                System.out.println("Error: "+e.getMessage());
            }
        return Status;
        }
    private boolean ITOSDailyReport_Updater(String Mail,String MailDate){
        
        Integer BillCycleStatus_Loc=Mail.indexOf("Bill Cycle Status");
        Integer RevenueAssurance_Loc=Mail.indexOf("Revenue Assurance")+"Revenue Assurance".length();
//        System.out.println(BillCycleStatus_Loc+","+RevenueAssurance_Loc);
        String BillingPart=Mail.substring(BillCycleStatus_Loc,RevenueAssurance_Loc);
        
        //Removing Incident Part
        if (BillingPart.indexOf("Incident:")>0)  BillingPart=BillingPart.replaceAll(BillingPart.substring(BillingPart.indexOf("Incident:"),BillingPart.indexOf("Revenue Assurance")),"");
        
        //Cutting to start of Table
        
//        BillingPart=BillingPart.replaceAll("nbsp","XXX");
        BillingPart = BillingPart.trim().replaceAll("nbsp", "X");
        BillingPart = BillingPart.trim().replaceAll("X+", "X");
        BillingPart = BillingPart.trim().replaceAll("XComment", "Comment");
        BillingPart=BillingPart.replaceAll("3.XXXXXXXX","");
        BillingPart=BillingPart.replaceAll(" ","");
        System.out.println("All Billing Part:"+BillingPart);
        Integer Start,End;
        Start=BillingPart.indexOf("StatusBCTypeTargetSLADaysActualDaysComment")+"StatusBCTypeTargetSLADaysActualDaysComment".length();
        End=BillingPart.indexOf("RevenueAssurance");
//        System.out.println(Start+","+End);
        BillingPart=BillingPart.substring(Start,End);
//        BillingPart=BillingPart.replaceAll("nbsp","XXX");
//        System.out.println("sub billing part:"+BillingPart);
        
        BillingDetails billingDetails=new BillingDetails();
        
        while (BillingPart.length()>=10){
            System.out.println("Handling:"+BillingPart.substring(0,BillingPart.length()));
            billingDetails=BillingParser(BillingPart.substring(0,BillingPart.length()));
            BillingPart=BillingPart.substring(billingDetails.getPointer(),BillingPart.length());
            
    
            if (billingDetails.getActual().equalsIgnoreCase("XXX")) billingDetails.setActual("0");
    
            try {
                String Query="REPLACE INTO Billing.BC_Status(Insertion_Date,BC,Type,`Target SLA (Days)`,Actual,Comment) VALUES(" +
                                            "'"+MailDate+"',"+
                                            "'"+billingDetails.getBC()+"',"+
                                            "'"+billingDetails.getType()+"',"+
                                            "'"+billingDetails.getTarget()+"',"+
                                            "'"+billingDetails.getActual()+"',"+
                                            "'"+billingDetails.getComment()+"')";
    //                                        System.out.println(Query);
                DB_Connection.executeSQL(Query,false);
                } catch (SQLException e) {
                   System.out.println("ITOSDailyReport_Updater Error:"+e.getMessage());
                   return false;
                }
                
            System.out.println(billingDetails.getBC()+","+billingDetails.getType()+","+billingDetails.getTarget()+","+billingDetails.getActual()+","+billingDetails.getComment()+","+billingDetails.getPointer());
        }
        
        return true;
    }
    private static boolean isNumber(String str)  {
        try  
        {  
         Double d= Double.parseDouble(str);  
        }  
        catch(NumberFormatException nfe)  
        {  
          return false;  
        }  
        return true; 
    }
    public static Integer FirstNumberOccurance(String string) {  
        Integer Pointer=0;
        String str="";
        boolean state=false;
        while (Pointer<string.length()){
            str=string.substring(Pointer,Pointer+1);
            Pointer=Pointer+1;
            state=isNumber(str);
            if (state) return Pointer;
        }
        return -1;
    }
    private BillingDetails BillingParser(String BillingPart){
//        String BillingPart=BillingPart_All.substring(1,FirstNumberOccurance(BillingPart_All));
//        System.out.println("Sub Parsing of:"+BillingPart);
        BillingDetails billingDetails=new BillingDetails();
        
        BillingPart=BillingPart+"  ";
        BillingPart=BillingPart.toLowerCase();
        Integer Pointer=0;
        String BC=BillingPart.substring(0,2);
        String Type="";
        Pointer=2;
        String Consumer=BillingPart.substring(Pointer,"Consumer".length()+Pointer);
        String Corporate=BillingPart.substring(Pointer,"Corporate".length()+Pointer);
        
        if (Corporate.equalsIgnoreCase("corporate")) 
            {
            Type="Corporate";
            Pointer=Pointer+Corporate.length();
            }
        if (Consumer.equalsIgnoreCase("consumer")) 
            {
            Type="Consumer";
            Pointer=Pointer+Consumer.length();
            }
            
//        System.out.println(Corporate);
//        System.out.println(Consumer);
//        System.out.println(Type);
        
        String Target=BillingPart.substring(Pointer,Pointer+1);
        if (Target.equalsIgnoreCase("x")) Pointer=Pointer+2;
        Pointer=Pointer+1;
//        String RemaingPart=BillingPart.substring(Pointer,FirstNumberOccurance(BillingPart));
//System.out.println(Pointer+","+BillingPart.substring(Pointer,Pointer+6));
        
        if (BillingPart.substring(Pointer,Pointer+6).equals("xxxxxx"))  {
            System.out.println("Found Empty record");
            billingDetails.setBC(BC);
//            billingDetails.setComment("");
            billingDetails.setType(Type);
            billingDetails.setTarget(Target);
//            billingDetails.setActual("");
            billingDetails.setPointer(Pointer+6);
            
            System.out.println(BC+","+Type+","+Target+","+""+","+"");
            return billingDetails;
        }
        else{
            System.out.println("Normal Record");
        }
        
        Integer Completed_loc=BillingPart.indexOf("completed");
        Integer InProgress_Loc=BillingPart.indexOf("inprogress");
        
        Integer NextTab_Loc=Completed_loc-Pointer;
        
        if ((InProgress_Loc<Completed_loc && InProgress_Loc>0) || Completed_loc<0) NextTab_Loc=InProgress_Loc-Pointer;
        System.out.println(Completed_loc+","+InProgress_Loc+","+NextTab_Loc+","+Pointer);
        
        String Actual=BillingPart.substring(Pointer,NextTab_Loc+Pointer);
        Pointer=Pointer+NextTab_Loc;
        
        
        String Completed=BillingPart.substring(Pointer,"Completed".length()+Pointer);
        String InProgress=BillingPart.substring(Pointer,"InProgress".length()+Pointer);
        String Comment="";
        
//        System.out.println(Completed);
//        System.out.println(InProgress);
        
        if (Completed.equalsIgnoreCase("completed")) 
            {
            Comment="Completed";
            Pointer=Pointer+Completed.length();
            }
        if (InProgress.equalsIgnoreCase("inprogress")) 
            {
            Comment="In Progress";
            Pointer=Pointer+InProgress.length();
            }
        
        
        
        billingDetails.setBC(BC);
        billingDetails.setComment(Comment);
        billingDetails.setType(Type);
        billingDetails.setTarget(Target);
        billingDetails.setActual(Actual.replaceAll("x","0"));
        billingDetails.setPointer(Pointer);
        
        System.out.println(BC+","+Type+","+Target+","+Actual+","+Comment);
        return billingDetails;
        
    }
    private String StringBetween(String WholeString,String LeftDelimiter,String RightDelimiter){
        int startIndex = WholeString.indexOf(LeftDelimiter)+1;
        int endIndex = WholeString.indexOf(RightDelimiter);

//        String firstPortion = WholeString.substring(0,startIndex);
//        String lastPortion = WholeString.substring(endIndex+1);
        String finalString = WholeString.substring(startIndex,endIndex);
        
        return finalString;

//        System.out.println("finalString : "+finalString);
    }
    private MailConverterDetails MailConverter(String Mail,boolean ReplaceWithSpace,String SentOnDate,String SentOnTime){
        System.out.println("inside MailConverter function");
        MailConverterDetails mailConverterDetails=new MailConverterDetails();
        String OutsideMail="";
        try{
        status = 
            DB_Connection.connectToDB(Parameters.DB_IP, Parameters.DB_Port, 
                                      Parameters.DB_Name, 
                                      Parameters.DB_UserName, 
                                      Parameters.DB_Password, 
                                      Parameters.DB_Type, wait_);
            try {
                System.out.println("Checking Mail2SMS");

                    if (Mail.contains("<")){
                        OutsideMail=StringBetween(Mail,"<",">").trim();
                    }
                    else{
                        OutsideMail=Mail.trim();
                    }
//                    System.out.println(Test);
                    
//                    OutsideMail = Mail.trim();
                    OutsideMail=OutsideMail.replaceAll("\"","");
                    Integer Space_Loc=OutsideMail.indexOf(" ");
                    if (Space_Loc>0){
                        OutsideMail=OutsideMail.substring(0,Space_Loc);
                        OutsideMail = OutsideMail.trim();
                    } 
                mailConverterDetails.setUsedServiceName("XXX");
                
                    String newFormat = "yyyy-MM-dd";
                    SimpleDateFormat sdf1 = new SimpleDateFormat(newFormat);
                    int dayOfWeek=0;
                    try {
                        Calendar c = Calendar.getInstance();
                        c.setTime(sdf1.parse(SentOnDate));
                         dayOfWeek= c.get(Calendar.DAY_OF_WEEK);
                        
                    //            System.out.println(dayOfWeek);
                    } catch (ParseException e) {
                        // TODO
                        System.out.println("Error in mail2SMS:"+e.getMessage());
                    }
                    String Minute=SentOnTime.substring(3,5);
                    String Hour=SentOnTime.substring(0,2);
                    String Day=Integer.toString(dayOfWeek);
                    
                
//                    select * from Services where (concat(',',StartHour,',') like '%,14,%' or StartHour='*') and (concat(',',`DayOfWeek(Sun=1)`,',') like '%,7,%' or `DayOfWeek(Sun=1)`='*') and MailFrom='SharePointAdmin@vodafone.com'
                ResultSet RS = DB_Connection.executeSQL_RS("SELECT ifnull(`Service Name`,'XXX') ServiceName,SendBody,SendSubject FROM MailHandler.`Mail2SMS Services` WHERE MailFrom='"+OutsideMail+"' and (concat(',',StartHour,',') like '%,"+Hour+",%' or StartHour='*') and (concat(',',`DayOfWeek(Sun=1)`,',') like '%,"+Day+",%' or `DayOfWeek(Sun=1)`='*')");
                System.out.println("SELECT ifnull(`Service Name`,'XXX') ServiceName,SendBody,SendSubject FROM MailHandler.`Mail2SMS Services` WHERE MailFrom='"+OutsideMail+"' and (concat(',',StartHour,',') like '%,"+Hour+",%' or StartHour='*') and (concat(',',`DayOfWeek(Sun=1)`,',') like '%,"+Day+",%' or `DayOfWeek(Sun=1)`='*')");
                System.out.println("GOt Results");
                while (RS.next()){
                    mailConverterDetails.setUsedServiceName(RS.getString("ServiceName"));
                    mailConverterDetails.setConvertedName("Mail2SMS"); 
                    mailConverterDetails.setSendBody(RS.getInt("SendBody")); 
                    mailConverterDetails.setSendSubject(RS.getInt("SendSubject"));
//                    mailConverterDetails.setStartHour(RS.getString("StartHour"));
//                    mailConverterDetails.setStartMin(RS.getString("StartMin"));;
                    System.out.println("Found in Mail2SMS DB");
                }
                RS.close();
                if (mailConverterDetails.getUsedServiceName().compareToIgnoreCase("XXX")!=0){
                    System.out.println("Mail is fund in Mail2SMS");
                    return mailConverterDetails;
                }
                System.out.println("Checking in Mail Attachemnt");
                    RS = DB_Connection.executeSQL_RS("SELECT MailFrom FROM MailHandler.Download_Attachemnt WHERE MailFrom='"+OutsideMail+"'");
                    while (RS.next()){
                        mailConverterDetails.setIsMailAttachemnt(true);
                        System.out.println("Found in Mail Attachemnet DB");
                    }
                    RS.close();
                
                } catch (SQLException e) {
                    // TODO
                    System.out.println("Exception in Mail 2 SMS part:"+e.getMessage());
                    }
        }catch (Exception e) {
            System.out.println("Not Found in Mail2SMS");
            System.out.println(e.getMessage());
        } 
        
        
        
        
        String Name=Mail;
        System.out.println("Not Found in Mail2SMS");
        
        Name=Name.replaceAll("(?<=<)(.*?)(?=>)", "");
        Name=Name.replaceAll("<>", "");
        
        Name=Name.toUpperCase();
        Name= Name.replaceAll("@VODAFONE.COM","");
        Name= Name.replaceAll(", VODAFONE EGYPT","");
        Name=Name.replaceAll("\"","");
        Name= Name.replaceAll("@","");
//        Name= Name.replaceAll("-","");
        Name= Name.replaceAll("&","");
        Name=Name.trim();
//        if (Name.equalsIgnoreCase("CNP VAS"))         Name= Name.replaceAll(" ","");
// Name= Name.replaceAll(" ","");
        
        if (ReplaceWithSpace)
            Name= Name.replaceAll("\\."," ");
//        Name= Name.replaceAll(" "," ");
        else{
            Name= Name.replaceAll("\\.","");
            Name= Name.replaceAll(" ","");
            }
        mailConverterDetails.setConvertedName(Name);   
        mailConverterDetails.setConvertedMail(Mail);
        return mailConverterDetails;        
    }
    private Attachment DownloadAttachemntLoc(String From,String Subject){
        ResultSet RS;
        boolean IsFound=false;
        Attachment attachmentDetails=new Attachment();
        if (From.contains("<")){
            From=StringBetween(From,"<",">").trim();
        }
        else{
            From=From.trim();
        }
        String Location="";
        String HostName="";
        
        try {
            HostName=InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.out.println("UnknownHostException:"+e.getMessage());
        }
        
        try {
            RS =  DB_Connection.executeSQL_RS("SELECT `Download In` DownloadIn FROM MailHandler.Download_Attachemnt WHERE MailFrom='"+From+"' AND '"+Subject+"' like Subject AND HostName='"+HostName+"'");
        while (RS.next()){
            Location=RS.getString("DownloadIn");
            IsFound=true;
        }
        RS.close();
        } catch (SQLException e) {
            System.out.println("Download Attachemnt:"+e.getMessage());
        }
        attachmentDetails.setAttachmentPath(Location);
        attachmentDetails.setIsFound(IsFound);
        return attachmentDetails;
    }
    private boolean MailHandler(String subject,String From,String Body,String SentOnDate,String SentOnTime,Message mail) throws ClassNotFoundException {

        System.out.println("inside MailHandler function");
        String Body_Up=Body.toUpperCase();
        if (!Body_Up.equalsIgnoreCase("")) System.out.println("Start OF Body:"+Body_Up.substring(1,100));
        System.out.println("Body:"+Body_Up);

//        subject=subject.replaceAll("re: ","");
        subject=subject.toLowerCase();
        if (subject.contains("it operations daily report") || subject.contains("it daily report")) {
            System.out.println("Found IT Operations Report Mail");
            From="IT O&S Daily Report";
        }
        
        Boolean updateStatus=false;  
        Service_Details service_Details=new Service_Details();
        
//        String UserName=MailConverter(From,false);
        Receipts UserID=Receipts.valueOf("OTHERS");
        MailConverterDetails mailConverterDetails=new MailConverterDetails();
        try{
            mailConverterDetails=MailConverter(From,false,SentOnDate,SentOnTime);
            UserID= Receipts.valueOf(mailConverterDetails.getConvertedName());
            System.out.println("Handling Mail of:"+mailConverterDetails.getConvertedName());
        } catch (Exception e) {
             System.out.println("User ID is not found, Others is selected, Error desc:"+e.getMessage());
                System.out.println("Handling Mail of:"+mailConverterDetails.getConvertedMail()+"(Others)");
//             Receipts UserID=Receipts.valueOf("OTHERS");
        }
        
        if (mailConverterDetails.getIsMailAttachemnt() != null && mailConverterDetails.getIsMailAttachemnt()){
            UserID=Receipts.valueOf("DownloadAttachment");
            System.out.println("Switching to DownloadAttachment");
        }
        switch (UserID){
            case DownloadAttachment:
            String contentType="";
            try {
                contentType = mail.getContentType();
                if (contentType.contains("multipart")) {
                    DataSource source = new ByteArrayDataSource(mail.getInputStream(), "multipart/*");
                    Multipart multiPart = new MimeMultipart(source);
                    multiPart = (Multipart)mail.getContent();
                    Attachment attchmentDetails = DownloadAttachemntLoc(From, subject);
                    if (attchmentDetails.getIsFound()) {
                        System.out.println("Mail Attachment is found");
                        for (int i = 0; i < multiPart.getCount(); i++) {
                            MimeBodyPart part = (MimeBodyPart)multiPart.getBodyPart(i);
                            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                                System.out.println("Saving File:" + part.getFileName()+" To "+attchmentDetails.getAttachmentPath());
                                part.saveFile(attchmentDetails.getAttachmentPath() + part.getFileName());
                                updateStatus=true;
                            }
                        }
                    }
                    else{
                        System.out.println("Mail Attachment configuration is not found in MailHandler Database");
                    }
                }
                } catch (MessagingException e) {
                    System.out.println("multipart MessagingException"+e.getMessage());
                } catch (IOException e) {
                     System.out.println("multipart IOException:"+e.getMessage());
                }
                    break;
            case ITOSDAILYREPORT:
//                System.out.println(Body);
                updateStatus=ITOSDailyReport_Updater(Body,SentOnDate);
                break;
            case Mail2SMS:
                try {
//                    if (Send2Mail2SMS(mailConverterDetails,SentOnDate,SentOnTime)){
    //                    System.out.println("Query:"+"Call Mail2SMS.SEND_SMS('"+Body+"','"+mailConverterDetails.getUsedServiceName()+"')");
                        if (mailConverterDetails.getSendBody()==1 && mailConverterDetails.getSendSubject()==1){
                            System.out.println("Sending Body and Subject");
                            System.out.println("Call MailHandler.SEND_SMS('S: "+subject+"\n\n"+"B: "+Body+"','"+mailConverterDetails.getUsedServiceName()+"')");
//                            DB_Connection.executeSQL("Call MailHandler.SEND_SMS('S: "+subject+"\n\n"+"B: "+Body+"','"+mailConverterDetails.getUsedServiceName()+"')",false);
                            DB_Connection.executeSQL("Call MailHandler.SEND_SMS_NEW('S: "+subject+"\n\n"+"B: "+Body+"','"+mailConverterDetails.getUsedServiceName()+"','"+subject+"')",false);
                        }
                        else if (mailConverterDetails.getSendBody() == 1) {
                            System.out.println("Sending Body Only using new procedure");
//                            DB_Connection.executeSQL("Call MailHandler.SEND_SMS('"+Body+"','"+mailConverterDetails.getUsedServiceName()+"')",false);
 System.out.println("Query:"+"Call MailHandler.SEND_SMS_NEW('"+Body+"','"+mailConverterDetails.getUsedServiceName()+"','"+subject+"'),false)");
                            DB_Connection.executeSQL("Call MailHandler.SEND_SMS_NEW('"+Body+"','"+mailConverterDetails.getUsedServiceName()+"','"+subject+"')",false);
                        }
                            else if (mailConverterDetails.getSendSubject() == 1) {
                                System.out.println("Sending Subject Only");
//                                DB_Connection.executeSQL("Call MailHandler.SEND_SMS('"+subject+"','"+mailConverterDetails.getUsedServiceName()+"')",false);
                                DB_Connection.executeSQL("Call MailHandler.SEND_SMS_NEW('"+subject+"','"+mailConverterDetails.getUsedServiceName()+"','"+subject+"')",false);
                        }
                        updateStatus=true;
//                    }
//                    else{
//                        System.out.println("It is not the time to send SMS");
//                        updateStatus=true;
//                    }
                } catch (SQLException e) {
                     System.out.println("Error: "+e.getMessage());
                }
                break;
            case CNPVAS:
                System.out.println("CNP Mail checker");
                service_Details=CNPVASHandler_Min2Add(subject);
                System.out.println("Validating "+service_Details.ServiceName+" Date:"+SentOnDate+" Time:"+SentOnTime);
                Boolean validService= CNPVAS_Valid_Service(service_Details,SentOnDate,SentOnTime);
                if (validService){
                        updateStatus = 
                                CNPVAS_UpdateService(service_Details,SentOnDate,SentOnTime);
                        
                }
                break;
//Sultan Requests
//----------------------------------------------------------------------------------------------------
            case MOHAMEDSULTAN:
                if (Body_Up.startsWith("ACTIVATE")){
                    try{
                    service_Details=CNPVASHandler_Min2Add(subject);
                    System.out.println("Found ACTIVATE Mail");
                    System.out.println("Query:"+"UPDATE Mail_Conf.Services SET Report_Activated=1,CollectData=1 WHERE `Service Name`='"+service_Details.getServiceName()+"'");
                    DB_Connection.executeSQL("UPDATE Main_Conf.Services SET Report_Activated=1,CollectData=1 WHERE `Service Name`='"+service_Details.getServiceName()+"'",false);
                    ResultSet RS=DB_Connection.executeSQL_RS("SELECT Report_Activated FROM Mail_Conf.Services WHERE `Service Name`='"+service_Details.getServiceName()+"'");
                    while (RS.next()){
                        if (RS.getInt("Report_Activated")==1)
                            DB_Connection.executeSQL("Call Main_Conf.SEND_SMS('CNP VAS','Service Activation','Service Name: "+service_Details.getServiceName()+" has been activated','201001843434')",false);    
                    }
                    updateStatus=true;
                        }catch (SQLException e) {
                            System.out.println("Error: "+e.getMessage());
                        }
                }else{
                    System.out.println("Mail desn't include Activate");
                }
                if (Body_Up.startsWith("DEACTIVATE")){
                    try{
                    service_Details=CNPVASHandler_Min2Add(subject);
                    System.out.println("Found DEACTIVATE Mail");
                    System.out.println("Query:"+"UPDATE Main_Conf.Services SET Report_Activated=0,CollectData=0 WHERE `Service Name`='"+service_Details.getServiceName()+"'");
                    DB_Connection.executeSQL("UPDATE Main_Conf.Services SET Report_Activated=0, CollectData=0 WHERE `Service Name`='"+service_Details.getServiceName()+"'",false);
                    ResultSet RS=DB_Connection.executeSQL_RS("SELECT Report_Activated FROM Main_Conf.Services WHERE `Service Name`='"+service_Details.getServiceName()+"'");
                    while (RS.next()){
                        if (RS.getInt("Report_Activated")==0)
                            DB_Connection.executeSQL("Call SEND_SMS('CNP VAS','Service Activation','Service Name: "+service_Details.getServiceName()+" has been deactivated','201001843434')",false);    
                    }
                    updateStatus=true;
                        }catch (SQLException e) {
                            System.out.println("Error: "+e.getMessage());
                        }
                }else{
                    System.out.println("Mail desn't include deactivate");
                }
//---------------------------------------------------------------------------------------------------
            default:

                if (Body_Up.startsWith("STOP")|| Body_Up.substring(1,"Stop".length()+1).equalsIgnoreCase("stop")){
                    try{
                    System.out.println("Mail starts with Stop");
                    service_Details=CNPVASHandler_Min2Add(subject);
                    System.out.println("Found Stop Mail");
                    String ServiceNameFormatted=service_Details.getServiceName();
                    ServiceNameFormatted=ServiceNameFormatted.toLowerCase().replaceAll("re:","").replaceAll("fw:","").trim();
                    System.out.println("Query:"+"call MailHandler.Stop_Me('"+MailConverter(From,true,SentOnDate,SentOnTime).getConvertedName()+"','"+ServiceNameFormatted+"')");
                    DB_Connection.executeSQL("call MailHandler.Stop_Me('"+MailConverter(From,true,SentOnDate,SentOnTime).getConvertedName()+"','"+ServiceNameFormatted+"')",false);
                    updateStatus=true;
                        }catch (SQLException e) {
                            System.out.println("Error: "+e.getMessage());
                        }
                        
                }
                else{
                    System.out.println("Mail Doesn't start with Stop, It is :"+Body_Up.substring(1,10));
                }
                System.out.println("Checking if starts with daily");
                if (Body_Up.startsWith("DAILY") || Body_Up.substring(1,"Daily".length()+1).equalsIgnoreCase("daily")){
                    try{
                    service_Details=CNPVASHandler_Min2Add(subject);
                    System.out.println("Found DAILY Mail");
                    String ServiceNameFormatted=service_Details.getServiceName();
                    ServiceNameFormatted=ServiceNameFormatted.toLowerCase().replaceAll("re:","").replaceAll("fw:","").trim();
                    System.out.println("Query:"+"call MailHandler.Daily_Me('"+MailConverter(From,true,SentOnDate,SentOnTime).getConvertedName()+"','"+ServiceNameFormatted+"')");
                    DB_Connection.executeSQL("call MailHandler.Daily_Me('"+MailConverter(From,true,SentOnDate,SentOnTime).getConvertedName()+"','"+ServiceNameFormatted+"')",false);
                    updateStatus=true;
                        }catch (SQLException e) {
                            System.out.println("Error: "+e.getMessage());
                        }
                }
                else{
                    System.out.println("Mail doesn't start with daily, it is:"+Body_Up.substring(1,"Daily".length()+1));
                    
                }
                break;
        }
        return updateStatus;
    }
    private String GetBody(Message mail){  
    
        System.out.println("inside getBody function");
        try {
            System.out.println("Step 1,Mail Type:"+mail.getContentType());
        } catch (MessagingException e) {
            // TODO
        }
             //-------------------------BASE64DecoderStream----------------
        try {
                 String base64dec="";
                 System.out.println("Step 2,Checking BASE64DecoderStream");     
                 base64dec = SmartDecoder((BASE64DecoderStream)mail.getContent());
             //            System.out.println("base64="+base64dec);
                 return ProcessMailBody(base64dec);            
             } catch (Exception e) {
                  System.out.println("Exception inside Getbody BASE64:"+e.getMessage()+"\n");
             } 
             //-------------------------SharedByteArrayInputStream----------------
        try {
            String MailContent="";
            System.out.println("Step 3,Checking SharedByteArrayInputStream");
            MailContent = readInput((SharedByteArrayInputStream)mail.getContent());
//            System.out.println(mail.getContentType());
//            System.out.println(MailContent.substring(1,1000));
            if (mail.getContentType().startsWith("multipart/related;") && MailContent.substring(1,MailContent.indexOf("\n")).contains("emailandroidcom") ){
                System.out.println("Found as multipart and sent from Android"); 
                
                Integer EnterLoc1=MailContent.indexOf("\n")+1;
                Integer EnterLoc2=MailContent.indexOf("\n",EnterLoc1+1);
                Integer EnterLoc3=MailContent.indexOf("\n",EnterLoc1+EnterLoc2+1);
                Integer EnterLoc4=MailContent.indexOf("\n",EnterLoc1+EnterLoc2+EnterLoc3+4);
                
                Integer ContentTypeLoc=MailContent.substring(1,EnterLoc4).indexOf("Content-Transfer-Encoding: ");
                
//                System.out.println(ContentType1+","+ContentType2+","+ContentType3+","+ContentType4);
                
                String ContentType=MailContent.substring(ContentTypeLoc+"Content-Transfer-Encoding: ".length()+1,EnterLoc4);
                System.out.println("Content Type ="+ContentType);
                String Sultan="X"+ContentType+"X";
                System.out.println(Sultan);
//                System.out.println(MailContent);
                
                if (ContentType.equalsIgnoreCase("base64") || ContentType.startsWith("base64")){
                
                System.out.println("Found Base64 Content Type");
                Integer Start=MailContent.indexOf("Content-Transfer-Encoding: base64");
                Integer End =MailContent.substring(MailContent.indexOf("Content-Transfer-Encoding: base64")+"Content-Transfer-Encoding: base64".length(),MailContent.length()).indexOf("Content-Type:")+Start+"Content-Transfer-Encoding: base64".length();
                Integer LastEnter=MailContent.substring(Start,End-1).lastIndexOf("\n");
 
//                System.out.println(Start+","+End+","+LastEnter);
                //System.out.println(Base64Decoder("SGVsbG8gV29ybGQ="));
                //System.out.println(MailContent.substring(Start+"Content-Transfer-Encoding: base64".length(),LastEnter+Start));
//                System.out.println("Start ProcessMailBody");
//                System.out.println(MailContent.substring(Start+"Content-Transfer-Encoding: base64".length(),LastEnter+Start));
                MailContent=Base64Decoder(MailContent.substring(Start+"Content-Transfer-Encoding: base64".length(),LastEnter+Start));
//                System.out.println("End ProcessMailBody");
                }else{
                    System.out.println("didn't find Base 64 content type");
                }
            }
            System.out.println("Processing Mail");
//            System.out.println(MailContent);
            String ProcessedMail=ProcessMailBody(MailContent);
            System.out.println("Mail Processed");
            return ProcessedMail;
        } catch (Exception e) {
            System.out.println("Exception inside Getbody SharedByteArrayInputStream:"+e.getMessage()+"\n");
        } 
        //-------------------------QPDecoderSteam----------------
        try {
            String MailContent="";
            System.out.println("Step 4,Checking QPDecoderSteam");
            System.out.println((QPDecoderStream)mail.getContent());
            MailContent = SmartDecoder((QPDecoderStream)mail.getContent());
//                    System.out.println("inside get body MailContent:"+MailContent);
            return ProcessMailBody(MailContent);
        } catch (Exception e) {
            System.out.println("Exception inside Getbody SharedByteArrayInputStream:"+e.getMessage()+"\n");
        } 

        //-------------------------Multipart----------------
        try{
        String contentType = mail.getContentType();
        if (contentType.contains("multipart")) {
            DataSource source = new ByteArrayDataSource(mail.getInputStream(), "multipart/*");
            Multipart multiPart = new MimeMultipart(source);
            multiPart = (Multipart)mail.getContent();
//            Attachment attchmentDetails = DownloadAttachemntLoc(From, subject);
//            if (attchmentDetails.getIsFound()) {
//                System.out.println("Mail Attachment is found");

                for (int i = 0; i < multiPart.getCount(); i++) {
                    MimeBodyPart bodyPart = (MimeBodyPart)multiPart.getBodyPart(i);
                    
                    
                    String disposition = bodyPart.getDisposition();

                    if (disposition != null && (disposition.equalsIgnoreCase("ATTACHMENT"))) { 
                        System.out.println("Mail have some attachment");                                
                      }
                    else { 
                            String body = (String)bodyPart.getContent();  
                             return ProcessMailBody(body);
//                            String base64dec = SmartDecoder((BASE64DecoderStream)mail.getContent());
                      }
                    
//                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
//                        System.out.println("Saving File:" + part.getFileName()+" To "+attchmentDetails.getAttachmentPath());
//                        part.saveFile(attchmentDetails.getAttachmentPath() + part.getFileName());
//                    }
                }
//            }
//            else{
//                System.out.println("Mail Attachment configuration is not found in MailHandler Database");
//            }
        }
            } catch (Exception e) {
                 System.out.println("Exception inside Getbody String:"+e.getMessage()+"\n");
            } 
        
        //-------------------------String----------------
        try {
            String body="";
            System.out.println("Step 5,Checking toString");
            body = mail.getContent().toString();
//            System.out.println("inside get body toString:"+body);
            return ProcessMailBody(body);
        } catch (Exception e) {
             System.out.println("Exception inside Getbody String:"+e.getMessage()+"\n");
        }  
        //-------------------------multipart----------------

         return "XXX";
    }
    public static String ProcessMailBody_new(String MailContent){
        //------------------Processing Mail body-------------------------------
//        System.out.println("Inside Processmail:"+MailContent.substring(1,3000));
return MailContent;}
    public static String ProcessMailBody(String MailContent){
        try {                  
//            System.out.println("Mail Content :"+MailContent.substring(1,1000));
                    
                    MailContent=MailContent.replaceAll("/", "-");
                    MailContent=MailContent.replaceAll("[^a-zA-Z0-9 .:<>=-]+", "");
            Integer StartOfTag=MailContent.indexOf("<");
            MailContent=MailContent.substring(StartOfTag,MailContent.length());
            MailContent=MailContent.replaceAll("(?<=<)(.*?)(?=>)", "");
            MailContent=MailContent.replaceAll("<>", "");
            MailContent=MailContent.replaceAll("v behaviorurldefaultVMLo behaviorurldefaultVMLw behaviorurldefaultVMLshape behaviorurldefaultVML","");
            MailContent=MailContent.replaceAll(": behavior:urldefaultVMLo: behavior:urldefaultVMLw: behavior:urldefaultVML.shape behavior:urldefaultVML","");
            
//            System.out.println("Mail Content Becomes1:"+MailContent.substring(1,1000));
                    
//                    MailContent=MailContent.replaceAll("^/", "");
                    //MailContent=MailContent.replaceAll("/", "");
                    
                    /*
                    StartTags=-1;
                    EndTags=-1;
                    
                    StartTags=MailContent.indexOf("<");
                    
                    MailContent=MailContent.substring(StartTags,MailContent.length());
                    StartTags=MailContent.indexOf("<");
//            System.out.println("Inside Processmail:"+MailContent);
                    EndTags=MailContent.indexOf(">");
//                    System.out.println(StartTags+" "+EndTags);
                    while (StartTags>=0){
//                        System.out.println("inside while StartTags="+StartTags);
//                        System.out.println("Deleting:"+MailContent.substring(StartTags,EndTags+1));
//                        System.out.println("loop X"+MailContent);
                        MailContent=MailContent.replaceAll(MailContent.substring(StartTags,EndTags+1),"\n");
                        MailContent=MailContent.trim();
                        StartTags=-1;
                        EndTags=-1;
                        StartTags=MailContent.indexOf("<");
                        EndTags=MailContent.indexOf(">");
                    }
                    while (MailContent.indexOf("\n\n")!=-1){
                        MailContent=MailContent.replaceAll("\n\n","\n");
                    }
                     */  
        }catch (Exception e) { //Catch exception if any
                      System.err.println("Body Error: " + e.getMessage());
                }
                
                
//        System.out.println("Mail Content Becomes:"+MailContent);
        
//        Integer EnterLocation=MailContent.indexOf("\n");

//        MailContent=MailContent.replaceAll(".*clickgsContent-Type.*","");
//        MailContent=MailContent.replaceAll(".*behavior:urldefaultVML.*","");
//        MailContent=MailContent.replaceAll(".*Content-Type:.*","");
//        MailContent=MailContent.replaceAll(".*Content-Transfer-Encoding:.*","");
//        MailContent=MailContent.replaceAll(".*emailandroidcom.*","");
//        MailContent=MailContent.replaceAll(".*Content-Type:.*","");
        
        
//        MailContent=MailContent.replaceFirst("\n","");
        
//        System.out.println("XXX");
        
//        if (MailContent.substring(1,MailContent.indexOf("\n")).contains("urldefaultVML")){
//            System.out.println("Found urldefaultVML");
//            MailContent=MailContent.substring(MailContent.indexOf("\n")+1,MailContent.length());
//        }
        
        
//        System.out.println("Mail Content Becomes2:"+MailContent);

        return MailContent;
    } 
    static String readInput(SharedByteArrayInputStream newItem) throws UnsupportedEncodingException,FileNotFoundException,IOException {
        StringBuffer buffer = new StringBuffer();
        try {
//            FileInputStream fis = new FileInputStream("test.txt");
            InputStreamReader isr = new InputStreamReader(newItem, "UTF8");
            Reader in = new BufferedReader(isr);
            int ch;
            while ((ch = in.read()) > -1) {
                buffer.append((char)ch);
            }
            in.close();
            return buffer.toString();
        } 
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String Base64Decoder(String string){
    
        byte[] decoded = Base64.decodeBase64(string.getBytes());
        String DecodedString = new String(decoded);
//        System.out.println(string + " = " + decodedString);
        return DecodedString;
    }  
    public static String SmartDecoder(BASE64DecoderStream mail){ 
        String DecodedMail="";
           int ascii = -1;
            try {
                while( (ascii = mail.read() ) != -1 ){
                    DecodedMail=DecodedMail+ (char)ascii;
//              System.out.write(ch);
           }
            } catch (IOException e) {
                System.out.println("Base64Decoder2 Exception:" + 
                                   e.getMessage());
            }
        return DecodedMail;
    }
    public static String SmartDecoder(Multipart multipart){
//            Log.e("BodyPart", "MultiPartCount: "+multipart.getCount());

 String disposition="";
        try {
            for (int j = 0; j < multipart.getCount(); j++) {

             BodyPart bodyPart = multipart.getBodyPart(j);

            if (bodyPart.getDisposition()==null || bodyPart.getDisposition().equalsIgnoreCase("null")){
//                disposition = disposition;
System.out.println("do nothing");
            }
            else{
                disposition = disposition+bodyPart.getDisposition();
            }

             if (disposition != null && (disposition.equalsIgnoreCase("ATTACHMENT"))) {
                 System.out.println("Mail have some attachment");

                 DataHandler handler = bodyPart.getDataHandler();
                 System.out.println("file name : " + handler.getName());
               }
             else {
//                     disposition = "";  // the changed code

                    try {
                        disposition=disposition+bodyPart.getContent();
//                        System.out.println(bodyPart.getContent());
                    } catch (IOException e) {
                        // TODO
                    }
                }

            }
            
        } catch (MessagingException e) {
            // TODO
        }
             return disposition;
    }
    public static String SmartDecoder(QPDecoderStream mail){ 
        String DecodedMail="";
           int ascii = -1;
            try {
                while( (ascii = mail.read() ) != -1 ){
                    DecodedMail=DecodedMail+ (char)ascii;
    //              System.out.write(ch);
           }
            } catch (IOException e) {
                System.out.println("Base64Decoder2 Exception:" + 
                                   e.getMessage());
            }
        return DecodedMail;
    }
}