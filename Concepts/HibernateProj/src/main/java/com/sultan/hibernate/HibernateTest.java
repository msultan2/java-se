package com.sultan.hibernate;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.sultan.hibernate.dto.UserDetails;

public class HibernateTest {

	public static void main(String[] args) {
		UserDetails user = new UserDetails();
		
		user.setUserID(1);
		user.setUserName("Sultan");
		user.setAddress("Sultan's Address");
		user.setJoinedDate(new Date());
		user.setDescription("Sultan Description");
		
		//Session Factory has to create once per project as it uses lots of resources.
		SessionFactory sessionFactory =new Configuration().configure().buildSessionFactory();
		
		//Saving object into Database.
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(user);
		session.getTransaction().commit();
		session.close();
		
		//Retreiving object from DB.
		user=null;
				
		session = sessionFactory.openSession();
		session.beginTransaction();
		//session.get(Model Object, the PK value);
		user=(UserDetails) session.get(UserDetails.class, 1);
		System.out.println("User Name is "+user.getUserName());
	}

}
