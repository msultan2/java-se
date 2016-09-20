package Sender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.*;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import Model.Person;

public class Server {
	
    private Queue myQueue;
    private ConnectionFactory myQueueFactory;
    private String message;
    Connection connection = null;
	Session session = null;
	MessageProducer messageProducer =null ;

	public Server(){
		CreateMessageProducer();
	}
	
	public static void main(String[] args) {

		System.out.println("Start");
		Server server=new Server();
		System.out.println("Servce Created");
		server.SendMessage();
		System.out.println("MSG sent");
	}
	
	 private void CreateMessageProducer()
     {
        try {
        	Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
			env.put(Context.PROVIDER_URL, "t3://localhost:7001");
			InitialContext  ic =  new InitialContext(env);	
			
			QueueConnectionFactory queueConnectionFactory =(QueueConnectionFactory) ic.lookup("jms/myQueueFactory2");
			QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();
			session = queueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue myQueue = (Queue) ic.lookup("jms/myQueue");
			messageProducer = session.createProducer(myQueue);
        }
        catch (Exception ex) {
        	System.out.println(ex.getMessage());
        } 
     }
    private void SendMessage(){
	try{      
    
//        CreateMessageProducer();
        TextMessage tm = session.createTextMessage();
        tm.setText("Sultan");
        messageProducer.send(tm);
        System.out.println("Text MSG is Sent");
        
//        Person person = new Person();
//        person.setId(3);
//        person.setName("Sultan");
//        ObjectMessage obm = session.createObjectMessage(person) ;
//        messageProducer.send(obm);
//        messageProducer.close();
//        System.out.println("Obj MSG is Sent");
        
		}catch(JMSException e)
			{}

    }
}
