/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package charging.info;

//import javax.xml.parsers.DocumentBuilder;
import promo.util.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ucip.AdjustmentRequest;

/**
 *
 * @author anassar
 */
public class ChargingLayoutParser {

    private Document dom;
    //private ArrayList<DedicatedAccountInformation> accntInfo;
    private File chngFile;
    //private ChargingModelsMap chgModels;
    public static final Integer DAYS_OF_MONTH = 30;
    
    public static final PromoLogger logger = new PromoLogger(ChargingLayoutParser.class);

    public ChargingLayoutParser(){
        //accntInfo = new ArrayList<DedicatedAccountInformation>();
        chngFile = new File(PromoParameters.chargingModels);
        //chgModels = new ChargingModelsMap();
    }

    public ChargingLayoutParser(String chgFile){
        //accntInfo = new ArrayList<DedicatedAccountInformation>();
        chngFile = new File(chgFile);
        //chgModels = new ChargingModelsMap();
    }

    public boolean initChargingModel(ChargingModelsMap chgModels){

        if(this.parseXmlFile())
            return this.parseDocument(chgModels);
        else return false;

    }

    private boolean parseXmlFile(){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(chngFile);

		}catch(Exception ex) {
			ex.printStackTrace();
            logger.error("Error reading file "+PromoParameters.chargingModels, ex);
            return false;
        }
        return true;
}

private boolean parseDocument(ChargingModelsMap chgModels){


		//get the root elememt
        Element docEle = dom.getDocumentElement();
        NodeList n1 ;
        Element accntInfos;
        NodeList n2 ;
        NodeList n3 ;

        DedicatedAccountInformation accntInf = null;
        AdjustmentRequest adjReq = null;
        int mdlID;
        ArrayList<DedicatedAccountInformation> accntInfo;

		//get a nodelist of <employee> elements
		n1 = docEle.getElementsByTagName("ChargingModel");

        //n1 = docEle.getChildNodes();
		if(n1 != null && n1.getLength() > 0) {
            for(int i = 0 ; i < n1.getLength();i++) {
                adjReq = new AdjustmentRequest();
                mdlID = Integer.parseInt(((Element)n1.item(i)).getAttribute("id"));
				//get the employee element
				//Element el = (Element)n1.item(i);
                //n2 = docEle.getElementsByTagName("accntInfo");
                accntInfos = (Element)n1.item(i);
                n2 = accntInfos.getElementsByTagName("accntInfo");
                accntInfo = new ArrayList<DedicatedAccountInformation>();
                for(int j = 0 ; j < n2.getLength(); j++){
                    //get the AccountInfo object
                	accntInf = getAccntInfo((Element)n2.item(j));
                    //add it to list
                    accntInfo.add(accntInf);
               }
               adjReq.initAllAccountsInformation(accntInfo);
               chgModels.put(mdlID, adjReq);

               n3 = accntInfos.getElementsByTagName("adjustmentinfo");
               adjReq.initAdjustmentInfo(getAdjustmentInfo((Element)n3.item(0)));

              /*
               System.out.println("-----------------------------ChgID: " + mdlID + "-----------------------");
               System.out.println("Main Adj " + adjReq.getAdjustmentAmount());
               System.out.println("Adj Code " + adjReq.getExternalData1());
               System.out.println("Adj Type " + adjReq.getExternalData2());
               for (DedicatedAccountInformation dajj : adjReq.getDedicatedAccountInformation()){
                    System.out.println(dajj.getDedicatedAccountID());
                    System.out.println(dajj.getAdjustmentAmount());
                    System.out.println(dajj.getNewExpiryDate());
                }
               System.out.println("---------------------ChgID: " + mdlID + " Ended-------------------");
               */

			}
		}



        /*for(DedicatedAccountInformation dif: accntInfo){
            System.out.println(dif.getAdjustmentAmount()+dif.getDedicatedAccountID()+dif.getNewExpiryDate());
        }

        for (Iterator iter = chgModels.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
           // System.out.println((Integer)entry.getKey());
           // System.out.println(((AdjustmentRequest)entry.getValue()).getDedicatedAccountsInformation()[1].getAdjustmentAmount());
           // System.out.println(((AdjustmentRequest)entry.getValue()).getDedicatedAccountsInformation()[1].getDedicatedAccountID());
           // System.out.println(((AdjustmentRequest)entry.getValue()).getDedicatedAccountsInformation()[1].getNewExpiryDate());
           // System.out.println(((AdjustmentRequest)entry.getValue()).getAdjustmentAmount());
            //String value = ((AdjustmentRequest)entry.getValue()).getDedicatedAccountsInformation();

         }*/

        if (!chgModels.containsDefault()) {
            logger.error("Default Charging Behaviour not found !", null);
            logger.error("Please indicate it using Charging ID -1 ", null);
            return false;
        }

        return true;
	}

private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

private int getIntValue(Element ele, String tagName) {
		//in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele,tagName));
	}


private DedicatedAccountInformation getAccntInfo(Element accntInfo) {

        //for each <> element get text or int values of

		String adjValue = getTextValue(accntInfo,"adjustment");
		int expiry = getIntValue(accntInfo,"expiry");
        int id = Integer.parseInt(accntInfo.getAttribute("id"));

        //Create a new daf with the value read from the xml nodes
        DedicatedAccountInformation daf = null;
        if(expiry != PromoParameters.INVALID_CHOICE){
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MONTH*expiry);
          
           daf = new  DedicatedAccountInformation(id,adjValue,calendar.getTime());
  
        }else{
            
            daf = new  DedicatedAccountInformation(id,adjValue);
            
        }
        //System.out.println(id +" "+adjValue);

		return daf;
	}

private AdjustmentInfo getAdjustmentInfo(Element adjInfo) {

        //for each <> element get text or int values of

		String adjCode = getTextValue(adjInfo,"code");
		String adjType = getTextValue(adjInfo,"type");
       
        //Create a new AdjustmentInfo with the value read from the xml nodes
        return new AdjustmentInfo(adjCode, adjType);
		 
	}



public static void main(String[] args){
		//create an instance
		//ChargingLayoutParser dpe = new ChargingLayoutParser();
        ChargingModelsMap chggmap = new ChargingModelsMap();
		//call run example
		//dpe.parseXmlFile();
        //dpe.parseDocument(chggmap);
        System.out.println(chggmap.initChargingModels());

        /*for (DedicatedAccountInformation dajj : ((AdjustmentRequest)chggmap.get(51)).getDedicatedAccountInformation()){
            
            System.out.println("DA id: " + dajj.getDedicatedAccountID());
            System.out.println("DA Amt: " + dajj.getAdjustmentAmount());
            System.out.println("DA date: " + dajj.getNewExpiryDate());
        }*/
        /*System.out.println(((AdjustmentRequest)chggmap.get("50")).getDedicatedAccountInformation()[0].getAdjustmentAmount());
        System.out.println(((AdjustmentRequest)chggmap.get("50")).getDedicatedAccountInformation()[0].getDedicatedAccountID());

        System.out.println(((AdjustmentRequest)chggmap.get("50")).getDedicatedAccountInformation()[1].getAdjustmentAmount());
        System.out.println(((AdjustmentRequest)chggmap.get("50")).getDedicatedAccountInformation()[1].getDedicatedAccountID());*/


        //for()
	}

}
