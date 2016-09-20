package sendmail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;


public class ReceiveMailImap {

  public ReceiveMailImap() {}

 

  //

  // inspired by :

  // http://www.mikedesjardins.net/content/2008/03/using-javamail-to-read-and-extract/

  //

 

  public static void doit() throws MessagingException, IOException {
      String user_auth = "CNPVAS";
      String user_password = "Sultan^666";
      String host="10.230.95.91";
      String port = "25";
    Folder folder = null;

    Store store = null;

      String from = "cnp.vas@vodafone.com";
      String to = "mohamed.sultan@vodafone.com";   
//      String Subject = "Hello Word";
//      String MessageText = "Exchange Test mail";

      Properties mailProp = new Properties();
//      mailProp.put("mail.smtp.host",host); 
//      mailProp.put("mail.smtp.port",port);
      /*
       * Itt jonnek a TLS beallitasok
       */
//      mailProp.put("mail.smtp.starttls.enable","true");
//      mailProp.put("mail.smtp.auth","true");
//      mailProp.put("mail.smtp.socketFactory.port",port);
//              mailProp.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
//      mailProp.put("mail.smtp.socketFactory.fallback","false");

      // Optionalisan debug
      mailProp.put("mail.debug", "true");

      
       try {
       /*
        * Az Autentikalast ez a fuggveny vegzi el. Tulajdonkeppen a  beepitet
        * fuggvenyt a JavaMail lib-bol irjuk felul, hogy nekunk dolgozzon :D
        * Majd a vegen az Instancot ezzel toltjuk fel.
        */
        Properties props = new Properties(); 
        
        mailProp.setProperty("mail.imap.port", "25"); 
        mailProp.setProperty("mail.imap.starttls.enable", "true") ;
        props.setProperty("mail.imap.socketFactory.fallback", "false");
        mailProp.put("mail.imap.socketFactory.port",port);
        mailProp.put("mail.imap.auth","true");
      Session session = Session.getDefaultInstance(mailProp,null);
      try{
        store = session.getStore("imap");
              store.connect(host,user_auth, user_password);
      }catch(Exception e){
          int X=1;
          System.out.println("Error:"+e.getMessage());
      }
      folder = store.getFolder("Inbox");

      folder.open(Folder.READ_WRITE);

      Message messages[] = folder.getMessages();

      System.out.println("No of Messages : " + folder.getMessageCount());

      System.out.println("No of Unread Messages : " + folder.getUnreadMessageCount());

      for (int i=0; i < messages.length; ++i) {

        System.out.println("MESSAGE #" + (i + 1) + ":");

        Message msg = messages[i];

        /*

          if we don''t want to fetch messages already processed

          if (!msg.isSet(Flags.Flag.SEEN)) {

             String from = "unknown";

             ...

          }

        */

//        String from = "unknown";

        if (msg.getReplyTo().length >= 1) {

          from = msg.getReplyTo()[0].toString();

        }

        else if (msg.getFrom().length >= 1) {

          from = msg.getFrom()[0].toString();

        }

        String subject = msg.getSubject();

        System.out.println("Saving ... " + subject +" " + from);

        // you may want to replace the spaces with "_"

        // the TEMP directory is used to store the files

        String filename = "c:/temp/" +  subject;

        saveParts(msg.getContent(), filename);

        msg.setFlag(Flags.Flag.SEEN,true);

        // to delete the message

        // msg.setFlag(Flags.Flag.DELETED, true);

      }

    }

    finally {

      if (folder != null) { folder.close(true); }

      if (store != null) { store.close(); }

    }

  }

 

  public static void saveParts(Object content, String filename)

  throws IOException, MessagingException

  {

    OutputStream out = null;

    InputStream in = null;

    try {

      if (content instanceof Multipart) {

        Multipart multi = ((Multipart)content);

        int parts = multi.getCount();

        for (int j=0; j < parts; ++j) {

          MimeBodyPart part = (MimeBodyPart)multi.getBodyPart(j);

          if (part.getContent() instanceof Multipart) {

            // part-within-a-part, do some recursion...

            saveParts(part.getContent(), filename);

          }

          else {

            String extension = "";

            if (part.isMimeType("text/html")) {

              extension = "html";

            }

            else {

              if (part.isMimeType("text/plain")) {

                extension = "txt";

              }

              else {

                //  Try to get the name of the attachment

                extension = part.getDataHandler().getName();

              }

              filename = filename + "." + extension;

              System.out.println("... " + filename);

              out = new FileOutputStream(new File(filename));

              in = part.getInputStream();

              int k;

              while ((k = in.read()) != -1) {

                out.write(k);

              }

            }

          }

        }

      }

    }

    finally {

      if (in != null) { in.close(); }

      if (out != null) { out.flush(); out.close(); }

    }

  }

 

  public static void main(String args[]) throws Exception {

    ReceiveMailImap.doit();

  }

}

