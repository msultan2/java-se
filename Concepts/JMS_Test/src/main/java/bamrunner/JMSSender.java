package bamrunner;
import java.util.Hashtable;
import javax.naming.*;
import javax.jms.*;

public class JMSSender {
   private static InitialContext ctx = null;
   private static QueueConnectionFactory qcf = null;
   private static QueueConnection qc = null;
   private static QueueSession qsess = null;
   private static Queue q = null;
   private static QueueSender qsndr = null;
   private static TextMessage message = null;
   // NOTE: The next two lines set the name of the Queue Connection Factory
   //       and the Queue that we want to use.
   private static final String QCF_NAME = "jms/myQueueFactory2";
   private static final String QUEUE_NAME = "jms/myQueue";
   public JMSSender() {
       super();
   }
   public static void sendMessage(String messageText) {
       // create InitialContext
       Hashtable properties = new Hashtable();
       properties.put(Context.INITIAL_CONTEXT_FACTORY,
                      "weblogic.jndi.WLInitialContextFactory");
       // NOTE: The port number of the server is provided in the next line,
       //       followed by the userid and password on the next two lines.
       properties.put(Context.PROVIDER_URL, "t3://localhost:7001");
       try {
           ctx = new InitialContext(properties);
       } catch (NamingException ne) {
           ne.printStackTrace(System.err);
           System.exit(0);
       }
       System.out.println("Got InitialContext " + ctx.toString());
       // create QueueConnectionFactory
       try {
           qcf = (QueueConnectionFactory)ctx.lookup(QCF_NAME);
       }
       catch (NamingException ne) {
           ne.printStackTrace(System.err);
           System.exit(0);
       }
       System.out.println("Got QueueConnectionFactory " + qcf.toString());
       // create QueueConnection
       try {
           qc = qcf.createQueueConnection();
       }
       catch (JMSException jmse) {
           jmse.printStackTrace(System.err);
           System.exit(0);
       }
       System.out.println("Got QueueConnection " + qc.toString());
       // create QueueSession
       try {
           qsess = qc.createQueueSession(false, 0);
       }
       catch (JMSException jmse) {
           jmse.printStackTrace(System.err);
           System.exit(0);
       }
       System.out.println("Got QueueSession " + qsess.toString());
       // lookup Queue
       try {
           q = (Queue) ctx.lookup(QUEUE_NAME);
       }
       catch (NamingException ne) {
           ne.printStackTrace(System.err);
           System.exit(0);
       }
       System.out.println("Got Queue " + q.toString());
       // create QueueSender
       try {
           qsndr = qsess.createSender(q);
       }
       catch (JMSException jmse) {
           jmse.printStackTrace(System.err);
           System.exit(0);
       }
       System.out.println("Got QueueSender " + qsndr.toString());
       // create TextMessage
       try {
           message = qsess.createTextMessage();
       }
       catch (JMSException jmse) {
           jmse.printStackTrace(System.err);
           System.exit(0);
       }
       System.out.println("Got TextMessage " + message.toString());
       // set message text in TextMessage
       try {
           message.setText(messageText);
       }
       catch (JMSException jmse) {
           jmse.printStackTrace(System.err);
           System.exit(0);
       }
       System.out.println("Set text in TextMessage " + message.toString());
       // send message
       try {
           qsndr.send(message);
       }
       catch (JMSException jmse) {
           jmse.printStackTrace(System.err);
           System.exit(0);
       }
       System.out.println("Sent message ");
       // clean up
       try {
           message = null;
           qsndr.close();
           qsndr = null;
           q = null;
           qsess.close();
           qsess = null;
           qc.close();
           qc = null;
           qcf = null;
           ctx = null;
       }
       catch (JMSException jmse) {
           jmse.printStackTrace(System.err);
       }
       System.out.println("Cleaned up and done.");
   }
   public static void main(String args[]) {
       sendMessage("test");
   }
}