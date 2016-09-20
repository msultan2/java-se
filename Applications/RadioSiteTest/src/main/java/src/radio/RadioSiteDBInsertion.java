package radio;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import control.SiteData;

public class RadioSiteDBInsertion {

	public void addSite(String siteID,String siteName,
							String area,
							String type,
							String date){
						
		SiteData siteData=new SiteData();
		
		siteData.setArea(area);
		siteData.setDate(date);
		siteData.setSiteID(siteID);
		siteData.setSiteName(siteName);
		siteData.setType(type);
		
		SessionFactory sessionFactory =new Configuration().configure().buildSessionFactory();
		
		//Saving object into Database.
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(siteData);
		session.getTransaction().commit();
	}
//	public RadioSiteDBInsertion() {
//		// TODO Auto-generated constructor stub
//			
//	}
	public static void main(String[] args) {
		RadioSiteDBInsertion radioSiteDBInsertion=new RadioSiteDBInsertion();
		
		String siteID=args[0];
		String siteName=args[1];
		String area=args[2];
		String type=args[3];
		String date=args[4];
		
		radioSiteDBInsertion.addSite(siteID, siteName, area, type, date);
//		radioSiteDBInsertion.addSite("22", "Sultan", "Area", "2G", "201");
	}

}
