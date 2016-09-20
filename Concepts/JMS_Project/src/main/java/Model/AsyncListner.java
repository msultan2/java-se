package Model;

import javax.jms.JMSException;
import javax.jms.Message;
//import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class AsyncListner implements MessageListener {

	@Override
	public void onMessage(Message msg) {
		TextMessage tm = (TextMessage) msg;
		try {
			System.out.println("MSG received from Listner:"+ tm.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
