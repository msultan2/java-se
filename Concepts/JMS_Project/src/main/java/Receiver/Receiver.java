package Receiver;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import Model.AsyncListner;
import Sender.Server;

public class Receiver {
	 
	private Queue myQueue;
	private ConnectionFactory myQueueFactory;
	private String message;
	Connection connection = null;
	Session session = null;
	MessageConsumer messageConsumer =null ;
	
	public Receiver(){
		CreateMessageProducer();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Start");
		Receiver receiver=new Receiver();
		System.out.println("Servce Created");
		receiver.ReceiveMessage();
		System.out.println("MSG Received");
	}
	private String ReceiveMessage(){
		String msgReceived="";
		try {
			//incase of Asysn Listener
			AsyncListner asyncListner=new AsyncListner();
			messageConsumer.setMessageListener(asyncListner);
			System.out.println("Waiting for message");
			
			//incase of Synsc message
//			TextMessage msg = (TextMessage) messageConsumer.receive();
//			msgReceived=msg.getText();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return msgReceived;
	}
	private void CreateMessageProducer(){
        try {
        	Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
//			env.put(Context.PROVIDER_URL, "t3://OPLZEZR:7001");
			env.put(Context.PROVIDER_URL, "t3://localhost:7001");
			InitialContext  ic =  new InitialContext(env);	
			
			QueueConnectionFactory queueConnectionFactory =(QueueConnectionFactory) ic.lookup("jms/myQueueFactory2");
			QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();
			session = queueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			queueConnection.start();
			Queue myQueue = (Queue) ic.lookup("jms/myQueue");
			messageConsumer = session.createConsumer(myQueue);
			

			
        }
        catch (Exception ex) {
        	System.out.println(ex.getMessage());
        } 
     }
}
