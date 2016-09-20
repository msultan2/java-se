package ucip;

//import da.info.DaAdjInfo;
import charging.info.DedicatedAccountInformation;
//import drs.GenericSubInfo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.net.MalformedURLException;

import java.text.SimpleDateFormat;

//import java.util.Calendar;
//import java.util.Date;

//import java.util.GregorianCalendar;


import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import charging.info.ChargingModelsMap;
import charging.info.DaMiniMap;
import charging.info.DedicatedAccountUpdateInformation;

//import drs.PaymentCharge;

//import interpromo.BasePromo;
//import interpromo.EntryLevelPromo;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;

import newpromo.NewPromo;

import promo.util.PromoLogger;

import promo.util.PromoParameters;
import redstone.xmlrpc.XmlRpcClient;
import redstone.xmlrpc.XmlRpcFault;
import redstone.xmlrpc.XmlRpcStruct;

import promo.util.PropertyHandler;

public class UcipHandler {
    
    private static XmlRpcClient client = null;
    private static PromoLogger logger = new PromoLogger(UcipHandler.class);
    private static ChargingModelsMap chgMap = new ChargingModelsMap();
    private  static PrintWriter ucipLog;
    //private static ChargingLayoutParser chngModels = new ;
    //private static String adjCode = PromoParameters.ADJ_CODE;
    //private static String adjType = PromoParameters.ADJ_TYPE;



    public UcipHandler() {
        UcipHandler.getClient();
    }
    
    public static XmlRpcClient getClient() {
        //XmlRpcClient client = null;

        try {

            if(ucipLog == null){
                ucipLog = new PrintWriter(new BufferedWriter(new FileWriter(
                    PromoParameters.LOG_BASE_DIR+"/UCIPLog-"+NewPromo.getBaseDate()+".txt")) , true);
                //"D:\\myWork\\InternationalPromo\\InterPromo\\resources"+"UCIPLog-"+BasePromo.getBaseDate()+".txt")) , true);
            }

            if(client == null){
                 client =
                    new XmlRpcClient(PropertyHandler.getProperty("URL"), false);
                    //System.out.println(PropertyHandler.getProperty("URL"));
                    if(client == null){
                            logger.error("Can not Create client" , null);
                            ucipLog.println("Failed to create UCIP Client..");
                            //System.out.println("Can not Create client");
                    }
            }

            if (PropertyHandler.getProperty("version").trim().equalsIgnoreCase("FD6"))
                initFD6Request();
            else
                initFD1Request();

            client.setRequestProperty("Host", "VASP");
            client.setRequestProperty("Content-Type", "text/xml");
  
        } catch (Exception e) {
            logger.error("Error creating client: " , e);
            ucipLog.println("Failed to create UCIP Client..");
            //e.printStackTrace();
        }
        ucipLog.println("Successfully created UCIP Client..");
        return client;
    }

public boolean initChargingModel(){

        //if (! chgMap. initDedicatedAccounts(PromoParameters.dedicatedAccounts)){
     if (! chgMap. initChargingModels()){
            System.err.println("An error initializing Dedicated Accounts may have occured !");
            logger.error("An error initializing Dedicated Accounts may have occured !", null);
            return false;
        }
    return true;
}

public ChargingModelsMap getChargingModel(){
    return chgMap;
}


    private  static void initFD6Request(){

        client.setRequestProperty("Authorization","Basic U3lzQWRtOlN5c0FkbQ==");
        client.setRequestProperty("User-Agent", "UGw Server/3.1/1.0");
    }

    private static void initFD1Request(){
        client.setRequestProperty("User-Agent", "UGw Server/2.1.04/1.0");
        client.setRequestProperty("Authorization","Basic U3lzQWRtOlN5c0FkbQ==");
    }

    public static void ucipCall(String serviceOfferingID) {
        int responseCode = -1;


        //String command = "";
        BufferedWriter sWriter = null;
        BufferedWriter fWriter = null;
        BufferedReader inMsisdn = null;
        SubscriberSegmentationRequest request = null;

        try {
            //XmlRpcClient client = getClient();
            sWriter = new BufferedWriter(new FileWriter("success.txt", true));
            fWriter = new BufferedWriter(new FileWriter("fail.txt", true));
            inMsisdn = new BufferedReader(new FileReader("msisdnList.txt"));
            String msisdn = "";

            while ((msisdn = inMsisdn.readLine()) != null) {

                try {
                    Date now = new Date();
                    SimpleDateFormat sdf = 
                        new SimpleDateFormat("ddMMkkmmssSSS");
                    String transactionId = sdf.format(now);
                    request = new SubscriberSegmentationRequest();
                    request.setOriginHostName("IVR");
                    request.setOriginNodeType("EXT");
                    request.setOriginTimeStamp(new Date());
                    request.setOriginTransactionID(transactionId);
                    request.setSubscriberNumber(msisdn.trim());
                    ServiceOffering serviceOffering = new ServiceOffering();
                    serviceOffering.setServiceOfferingActiveFlag(new Boolean(true));
                    serviceOffering.setServiceOfferingID(new Integer(serviceOfferingID.trim()));
                    ServiceOffering[] serviceOfferings = 
                        new ServiceOffering[1];
                    serviceOfferings[0] = serviceOffering;
                    request.setServiceOfferings(serviceOfferings);
                    Object param = request;
                    Object returnValue = 
                        client.invoke("UpdateSubscriberSegmentation", 
                                      new Object[] { param });
                    responseCode = 
                            ((Integer)((XmlRpcStruct)returnValue).get("responseCode")).intValue();
                    logger.debug("RC: " + responseCode);
                    if (responseCode == 0) {
                        sWriter.write(msisdn);
                        sWriter.newLine();
                    } else {
                        fWriter.write(msisdn);
                        fWriter.newLine();
                    }
                    logger.debug("msisdn: " + msisdn + " responseCode: " + 
                                 responseCode);
                } catch (XmlRpcFault fault) {
                    fWriter.write(msisdn);
                    fWriter.newLine();
                    logger.error("MSISDN:" + msisdn, fault);
                } catch (Exception e) {
                    fWriter.write(msisdn);
                    fWriter.newLine();
                    logger.error("MSISDN:" + msisdn, e);
                }

            }
        } catch (FileNotFoundException e) {
            logger.error("FileNotFound", e);
        } catch (IOException e) {
            logger.error("IOException", e);
        } finally {
            try {


                inMsisdn.close();
                fWriter.close();
                sWriter.close();

            } catch (IOException e) {
                logger.error("error", e);
            }

        }


    }

    public static void ucipCallBatch() {

        String serviceOfferingInput = 
            PropertyHandler.getProperty("service.offering.input");
        if (serviceOfferingInput == null) {
            logger.debug("service.offering.input property not found");
            return;
        }
        int responseCode = -1;


        String command = "";
        BufferedWriter sWriter = null;
        BufferedWriter fWriter = null;
        BufferedReader inMsisdn = null;
        SubscriberSegmentationRequest request = null;
        String[] serviceOfferingArray = null;
        ServiceOffering serviceOffering = null;
        try {
            //XmlRpcClient client = getClient();
            sWriter = new BufferedWriter(new FileWriter("success.txt", true));
            fWriter = new BufferedWriter(new FileWriter("fail.txt", true));
            inMsisdn = new BufferedReader(new FileReader("msisdnList.txt"));
            String msisdn = "";

            if (serviceOfferingInput == null) {
                logger.debug("Invalid service offering property");
                return;
            }
            serviceOfferingArray = serviceOfferingInput.split(",");

            if ((serviceOfferingArray.length % 2) != 0) {
                logger.debug("Invalid service offering property");
                return;
            }
            ServiceOffering[] serviceOfferings = 
                new ServiceOffering[serviceOfferingArray.length / 2];

            for (int i = 0; i < serviceOfferingArray.length; i++) {


                if ((i % 2) == 0) {
                    serviceOffering = new ServiceOffering();
                    serviceOffering.setServiceOfferingID(new Integer(serviceOfferingArray[i]));
                }

                else {

                    if (serviceOfferingArray[i].equals("1")) {


                        serviceOffering.setServiceOfferingActiveFlag(new Boolean(true));
                    } else if (serviceOfferingArray[i].equals("0")) {


                        serviceOffering.setServiceOfferingActiveFlag(new Boolean(false));
                    } else {


                        logger.debug("Invalid service offering property");
                        return;
                    }
                }

                if ((i % 2) != 0)

                    serviceOfferings[(i - 1) / 2] = serviceOffering;
            }


            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMkkmmssSSS");
            String transactionId = sdf.format(now);
            request = new SubscriberSegmentationRequest();
            request.setOriginHostName("IVR");
            request.setOriginNodeType("EXT");
            request.setOriginTimeStamp(new Date());
            request.setOriginTransactionID(transactionId);
            request.setServiceOfferings(serviceOfferings);
            while ((msisdn = inMsisdn.readLine()) != null) {

                try {
                    request.setSubscriberNumber(msisdn.trim());

                    Object param = request;
                    Object returnValue = 
                        client.invoke("UpdateSubscriberSegmentation", 
                                      new Object[] { param });
                    responseCode = 
                            ((Integer)((XmlRpcStruct)returnValue).get("responseCode")).intValue();
                    logger.debug("RC: " + responseCode);
                    if (responseCode == 0) {
                        sWriter.write(msisdn);
                        sWriter.newLine();
                    } else {
                        fWriter.write(msisdn);
                        fWriter.newLine();
                    }
                    logger.debug("msisdn: " + msisdn + " responseCode: " + 
                                 responseCode);
                } catch (XmlRpcFault fault) {
                    fWriter.write(msisdn);
                    fWriter.newLine();
                    logger.error("MSISDN:" + msisdn, fault);
                } catch (Exception e) {
                    fWriter.write(msisdn);
                    fWriter.newLine();
                    logger.error("MSISDN:" + msisdn, e);
                }

            }
        } catch (FileNotFoundException e) {
            logger.error("FileNotFound", e);
        } catch (IOException e) {
            logger.error("IOException", e);
        } finally {
            try {


                inMsisdn.close();
                fWriter.close();
                sWriter.close();

            } catch (IOException e) {
                logger.error("error", e);
            }

        }


    }

    public boolean adjustDedAccount(String dedAdjustmentAmount, 
                                    String dedicatedAccountId, String msisdn, 
                                    String adjCode, String adjType) {

        //FD6 command
        UcipHandler.initFD6Request();

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMkkmmssSSS");
        //XmlRpcClient client = getClient();
        String transactionId = sdf.format(now);
        UpdateBalDateRequest request = new UpdateBalDateRequest();
        request.setOriginHostName("IVR");
        request.setOriginNodeType("EXT");
        request.setOriginTimeStamp(new Date());
        request.setOriginTransactionID(transactionId);
        request.setSubscriberNumber(msisdn);
        request.setTransactionCurrency("EGP");

        DedicatedAccountUpdateInformation info =
            new DedicatedAccountUpdateInformation();
        info.setAdjustmentAmountRelative(dedAdjustmentAmount);
        info.setDedicatedAccountID(new Integer(dedicatedAccountId));
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH,30);
        info.setExpiryDate(calendar.getTime());
        DedicatedAccountUpdateInformation[] infoArr = 
            new DedicatedAccountUpdateInformation[2];
        infoArr[0] = info;
        info = 
            new DedicatedAccountUpdateInformation();
        info.setAdjustmentAmountRelative("700");
        info.setDedicatedAccountID(new Integer("1"));
       // GregorianCalendar calendar = new GregorianCalendar();
        //calendar.add(Calendar.DAY_OF_MONTH,30);
        //info.setExpiryDate(calendar.getTime());
        infoArr[1] = info;

        request.setDedicatedAccountUpdateInformation(infoArr);
        request.setExternalData1(adjCode);
        request.setExternalData2(adjType);
        Object param = request;
        Object returnValue = null;
        Integer responseCode = -1;
        try {
            returnValue = 
                    client.invoke("UpdateBalanceAndDate", new Object[] { param });
        } catch (XmlRpcFault e) {
            //e.printStackTrace();
            logger.error("XmlRpcFault: " ,e);
            ucipLog.println(msisdn + " UCIP invoke Failed with XmlRpcFault..");
        }
        if (returnValue != null) {
            responseCode = 
                    ((Integer)((XmlRpcStruct)returnValue).get("responseCode")).intValue();
            logger.debug(msisdn + " RC: " + responseCode);
            if (responseCode == 0){
                ucipLog.println(msisdn + " UCIP invoke succeded "
                        + " Dediacted ID: " + dedicatedAccountId +  " Dedicated amount: " + dedAdjustmentAmount);
                return true;
            }
        }
        ucipLog.println(msisdn + " UCIP invoke Failed with NULL repsonse..");
        logger.error(msisdn + "NULL UCIP response " ,null);
        return false;
    }
/*
     public Integer adjustDedAccount(GenericSubInfo sub) {

        //FD6 command
        UcipHandler.initFD6Request();

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMkkmmssSSS");
        //XmlRpcClient client = getClient();
        String transactionId = sdf.format(now);

       UpdateBalDateRequest request = new UpdateBalDateRequest(
              (AdjustmentRequest)chgMap.get(sub.getChargingID()));
       // UpdateBalDateRequest request = new UpdateBalDateRequest();
        request.setOriginHostName("IVR");
        request.setOriginNodeType("EXT");
        request.setOriginTimeStamp(new Date());
        request.setOriginTransactionID(transactionId);
        request.setSubscriberNumber(sub.getSubscriberNumberShortFormat());
        request.setTransactionCurrency("EGP");

        /*DedicatedAccountUpdateInformation info =
            new DedicatedAccountUpdateInformation();
        //info.setAdjustmentAmountRelative(sub.get);
        //info.setDedicatedAccountID(new Integer(dedicatedAccountId));
        DedicatedAccountUpdateInformation[] infoArr =
            new DedicatedAccountUpdateInformation[1];
        infoArr[0] = info;
        request.setDedicatedAccountUpdateInformation(infoArr);*/

        /*System.out.println(request.getAdjustmentAmount());
        for(DedicatedAccountUpdateInformation dauf : request.getDedicatedAccountUpdateInformation()){
            System.out.println(dauf.getAdjustmentAmountRelative());
            System.out.println(dauf.getDedicatedAccountID());
            System.out.println(dauf.getExpiryDate());
        }*/
        
        //System.out.println(request.getDedicatedAccountUpdateInformation()[1].getAdjustmentAmountRelative());
        //System.out.println(request.getDedicatedAccountUpdateInformation()[1].getDedicatedAccountID());
        //System.out.println(request.getDedicatedAccountUpdateInformation()[1].getExpiryDate());

        //request.setExternalData1(adjCode);
        //request.setExternalData2(adjType);
/*        Object param = request;
        Object returnValue = null;
        Integer responseCode = -1;
        try {
            returnValue =
                    client.invoke("UpdateBalanceAndDate", new Object[] { param });
        } catch (XmlRpcFault e) {
            //e.printStackTrace();
            logger.error("XmlRpcFault: " ,e);
            ucipLog.println(sub.getSubscriberNumber() + " UCIP invoke Failed with XmlRpcFault..");
        }
        if (returnValue != null) {
            responseCode =
                    ((Integer)((XmlRpcStruct)returnValue).get("responseCode")).intValue();
            logger.debug(sub.getSubscriberNumber() + " RC: " + responseCode);
           return responseCode;
            // if (responseCode == 0)
             //   return true;
        }
        ucipLog.println(sub.getSubscriberNumber() + " UCIP invoke Failed with NULL repsonse..");
        logger.error(sub.getSubscriberNumber() + "NULL UCIP response " ,null);
        return UcipResponseCode.NULLRESPONSE;
    }
*/


    public boolean adjustDedAccountFD1(String dedAdjustmentAmount, 
                                    String dedicatedAccountId, String msisdn, 
                                    String adjCode, String adjType, String mainAdjustmentAmount,
                                    int validMonths) {
        //FD1 Command
        UcipHandler.initFD1Request();

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMkkmmssSSS");
        //XmlRpcClient client = getClient();
        String transactionId = sdf.format(now);
        AdjustmentRequest request = new AdjustmentRequest();
        request.setOriginHostName("IVR");
        request.setOriginNodeType("EXT");
        request.setOriginTimeStamp(new Date());
        request.setOriginTransactionID(transactionId);
        request.setSubscriberNumber(msisdn);
        request.setTransactionCurrency("EGP");
        request.setExternalData1(adjCode);
        request.setExternalData2(adjType);

        if ( mainAdjustmentAmount != null && !mainAdjustmentAmount.equalsIgnoreCase("0"))
                    request.setAdjustmentAmount(mainAdjustmentAmount);

        if(!dedicatedAccountId.equalsIgnoreCase("-1")){
           //request.setDedicatedAccountsInformation(setDAInfo
             //      (dedicatedAccountId,dedAdjustmentAmount,validMonths));

            DedicatedAccountInformation info =
            new DedicatedAccountInformation();
        info.setAdjustmentAmount(dedAdjustmentAmount);
        info.setDedicatedAccountID(new Integer(dedicatedAccountId));
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH,30);
        info.setNewExpiryDate(calendar.getTime());
        DedicatedAccountInformation[] infoArr =
            new DedicatedAccountInformation[1];
        infoArr[0] = info;
        request.setDedicatedAccountInformation(infoArr);
        }

        Object param = request;
        Object returnValue = null;
        //System.out.println(param);
        Integer responseCode = -1;
        try {
            returnValue = 
                    client.invoke("AdjustmentTRequest", new Object[] { param });
        } catch (XmlRpcFault e) {
            //e.printStackTrace();
            logger.error("XmlRpcFault: " ,e);
            ucipLog.println(msisdn + " UCIP invoke Failed with XmlRpcFault..");
        }
        if (returnValue != null) {
            responseCode = 
                    ((Integer)((XmlRpcStruct)returnValue).get("responseCode")).intValue();
            logger.debug(msisdn + " RC: " + responseCode);

            if (responseCode == 0){
                ucipLog.println(msisdn + " UCIP invoke succeded "+ "Main amount: " + mainAdjustmentAmount
                        + " Dediacted ID: " + dedicatedAccountId +  " Dedicated amount: " + dedAdjustmentAmount);
                return true;
            }
            ucipLog.println(msisdn + " UCIP invoke Failed with code "+ responseCode );
        }
        ucipLog.println(msisdn + " UCIP invoke Failed with NULL repsonse..");
        logger.error(msisdn + "NULL UCIP response " ,null);
        return false;

    }

    private DedicatedAccountInformation[] setDAInfo(String dedicatedAccountId,
            String dedAdjustmentAmount,int validMonths){

        DedicatedAccountInformation info =
                new DedicatedAccountInformation();
            info.setAdjustmentAmount(dedAdjustmentAmount);
            info.setDedicatedAccountID(new Integer(dedicatedAccountId));
            if( validMonths != -1){
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.add(GregorianCalendar.DAY_OF_MONTH,validMonths*30);
                info.setNewExpiryDate(calendar.getTime());
             }

        DedicatedAccountInformation[] infoArr =
            new DedicatedAccountInformation[1];
        infoArr[0] = info;
        //request.setDedicatedAccountsInformation(infoArr);
        return infoArr;


        //System.out.println(request.getDedicatedAccountsInformation()[0].getAdjustmentAmount());
        //System.out.println(request.getDedicatedAccountsInformation()[0].getDedicatedAccountID());
        //System.out.println(request.getDedicatedAccountsInformation()[0].getNewExpiryDate());

    }

    public boolean adjustDedAccountNew(String dedAdjustmentAmount,
                                    String dedicatedAccountId, String msisdn,
                                    String adjCode, String adjType) {

        //FD1 command
        UcipHandler.initFD1Request();

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMkkmmssSSS");
        //XmlRpcClient client = getClient();
        String transactionId = sdf.format(now);
        AdjustmentRequest request = new AdjustmentRequest();
        request.setOriginHostName("IVR");
        request.setOriginNodeType("EXT");
        request.setOriginTimeStamp(new Date());
        request.setOriginTransactionID(transactionId);
        request.setSubscriberNumber(msisdn);
        request.setTransactionCurrency("EGP");

        /*DedicatedAccountUpdateInformation info =
            new DedicatedAccountUpdateInformation();
        info.setAdjustmentAmountRelative(dedAdjustmentAmount);
        info.setDedicatedAccountID(new Integer(dedicatedAccountId));
        DedicatedAccountUpdateInformation[] infoArr =
            new DedicatedAccountUpdateInformation[1];
        infoArr[0] = info;
        request.setDedicatedAccountsInformation(info);*/
        
        request.setExternalData1(adjCode);
        request.setExternalData2(adjType);
        Object param = request;
        Object returnValue = null;
        Integer responseCode = -1;
        try {
            returnValue =
                    client.invoke("AdjustmentTRequest", new Object[] { param });
        } catch (XmlRpcFault e) {
            //e.printStackTrace();
            logger.error("XmlRpcFault: " ,e);
            ucipLog.println(msisdn + " UCIP invoke Failed with XmlRpcFault..");
        }
        if (returnValue != null) {
            responseCode =
                    ((Integer)((XmlRpcStruct)returnValue).get("responseCode")).intValue();
            logger.debug(msisdn + " RC: " + responseCode);
            if (responseCode == 0){
                ucipLog.println(msisdn + " UCIP invoke succeded "
                        + "Dediacted ID: " + dedicatedAccountId +  " Dedicated amount: " + dedAdjustmentAmount);
                return true;
            }
            ucipLog.println(msisdn + " UCIP invoke Failed with code "+ responseCode );
        }
        ucipLog.println(msisdn + " UCIP invoke Failed with NULL repsonse..");
        logger.error(msisdn + "NULL UCIP response " ,null);
        return false;
    }

 public Integer adjustDedAccountFD1(String msisdn, AdjustmentRequest adj,
         String adjCode, String adjType){

        //FD1 Command
        UcipHandler.initFD1Request();

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMkkmmssSSS");
        //XmlRpcClient client = getClient();
        String transactionId = sdf.format(now);

        //AdjustmentRequest request = new AdjustmentRequest();
        adj.setOriginHostName("IVR");
        adj.setOriginNodeType("EXT");
        adj.setTransactionCurrency("EGP");

        adj.setOriginTimeStamp(new Date());
        adj.setOriginTransactionID(transactionId);
        adj.setSubscriberNumber(msisdn);
        
        adj.setExternalData1(adjCode);
        adj.setExternalData2(adjType);

        //adj.getDedicatedAccountsInformation()[0].setAdjustmentAmount("100");
        //adj.getDedicatedAccountsInformation()[0].setDedicatedAccountID(2);
        //adj.getDedicatedAccountsInformation()[0].getNewExpiryDate();
        
        //System.out.println(adj.getDedicatedAccountsInformation()[0].getAdjustmentAmount());
        //System.out.println(adj.getDedicatedAccountsInformation()[0].getDedicatedAccountID());
        //System.out.println(adj.getDedicatedAccountsInformation()[0].getNewExpiryDate());


        Object param = adj;
        Object returnValue = null;
        Integer responseCode = -1;
        try {
            returnValue =
                    client.invoke("AdjustmentTRequest", new Object[] { param });
        } catch (XmlRpcFault e) {
            //e.printStackTrace();
            logger.error("XmlRpcFault: " ,e);
            ucipLog.println(msisdn + " UCIP invoke Failed with XmlRpcFault..");
        }
        if (returnValue != null) {
            responseCode =
                    ((Integer)((XmlRpcStruct)returnValue).get("responseCode")).intValue();
            logger.debug(msisdn + " RC: " + responseCode);
            ucipLog.println(msisdn + " UCIP adjustment success with code: " + responseCode);
            return responseCode;
            //if (responseCode == 0)
              //  return true;
        }
        ucipLog.println(msisdn + " UCIP invoke Failed with NULL repsonse..");
        logger.error(msisdn + "NULL UCIP response " ,null);
        return UcipResponseCode.NULLRESPONSE;

 }
/*
 public Integer adjustDedAccountFD1(GenericSubInfo sub){

       logger.debug("UCIP Adjusting " + sub.getSubscriberNumber() + " Svc-Class "+ sub.getServiceClass());
       logger.debug("With adjCode " + ((AdjustmentRequest)chgMap.get(sub.getChargingID())).getExternalData1()
                    + " & adjType " + ((AdjustmentRequest)chgMap.get(sub.getChargingID())).getExternalData2());

        //FD1 Command
        UcipHandler.initFD1Request();

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMkkmmssSSS");
        //XmlRpcClient client = getClient();
        String transactionId = sdf.format(now);

        //AdjustmentRequest adj = (AdjustmentRequest)daMap.get(sub.getServiceClass());
        AdjustmentRequest adj = (AdjustmentRequest)chgMap.get(sub.getChargingID());
        adj.setOriginHostName("IVR");
        adj.setOriginNodeType("EXT");
        adj.setTransactionCurrency("EGP");

        adj.setOriginTimeStamp(new Date());
        adj.setOriginTransactionID(transactionId);
        adj.setSubscriberNumber(sub.getSubscriberNumberShortFormat());

        //adj.setExternalData1(adjCode);
        //adj.setExternalData2(adjType);

        /*for (DedicatedAccountInformation dajj : adj.getDedicatedAccountInformation()){
            System.out.println(dajj.getAdjustmentAmount());
            System.out.println(dajj.getDedicatedAccountID());
            System.out.println(dajj.getNewExpiryDate());
        }*/
/*
        Object param = adj;
        Object returnValue = null;
        Integer responseCode = -1;
        try {
            returnValue =
                    client.invoke("AdjustmentTRequest", new Object[] { param });
        } catch (XmlRpcFault e) {
            //e.printStackTrace();
            logger.error("XmlRpcFault: " ,e);
            ucipLog.println(sub.getSubscriberNumber() + " UCIP invoke Failed with XmlRpcFault..");
        }
        if (returnValue != null) {
            responseCode =
                    ((Integer)((XmlRpcStruct)returnValue).get("responseCode")).intValue();
            logger.debug(sub.getSubscriberNumber() + " RC: " + responseCode);
            ucipLog.println(sub.getSubscriberNumber() + " UCIP adjustment success with code: " + responseCode);
            return responseCode;
            //if (responseCode == 0)
              //  return true;
        }
        ucipLog.println(sub.getSubscriberNumber() + " UCIP invoke Failed with NULL repsonse..");
        logger.error(sub.getSubscriberNumber() + "NULL UCIP response " ,null);
        return UcipResponseCode.NULLRESPONSE;

 }
 */
    public Integer adjustDedAccountFD1_(String dedAdjustmentAmount,
                                       String dedicatedAccountId, String msisdn,
                                       String adjCode, String adjType) {
        
            logger.debug("adjustDedAccountFD1_ for : "+msisdn );
           Date now = new Date();
           SimpleDateFormat sdf = new SimpleDateFormat("ddMMkkmmssSSS");
           //XmlRpcClient client = getClient();
           String transactionId = sdf.format(now);
           AdjustmentRequest request = new AdjustmentRequest();
           request.setOriginHostName("IVR");
           request.setOriginNodeType("EXT");
           request.setOriginTimeStamp(new Date());
           request.setOriginTransactionID(transactionId);
           request.setSubscriberNumber(msisdn);
           request.setTransactionCurrency("EGP");
           DedicatedAccountInformation info =
               new DedicatedAccountInformation();
           info.setAdjustmentAmount(dedAdjustmentAmount);
           info.setDedicatedAccountID(new Integer(dedicatedAccountId));
           GregorianCalendar calendar = new GregorianCalendar();
           calendar.add(Calendar.DAY_OF_MONTH,30);
           info.setNewExpiryDate(calendar.getTime());
           DedicatedAccountInformation[] infoArr =
               new DedicatedAccountInformation[1];
           infoArr[0] = info;
           request.setDedicatedAccountInformation(infoArr);
           request.setExternalData1(adjCode);
           request.setExternalData2(adjType);
           
           Object param = request;
           Object returnValue = null;
           Integer responseCode = -1;
           try {
               returnValue =
                       client.invoke("AdjustmentTRequest", new Object[] { param });
           } catch (XmlRpcFault e) {
               //e.printStackTrace();
               logger.error("XmlRpcFault: " ,e);
               ucipLog.println(msisdn + " UCIP invoke Failed with XmlRpcFault..");
           }
           if (returnValue != null) {
               responseCode =
                       ((Integer)((XmlRpcStruct)returnValue).get("responseCode")).intValue();
               logger.debug(msisdn + " RC: " + responseCode);
               ucipLog.println(msisdn + " UCIP invoke success with code: " + responseCode);
               return responseCode;
               //if (responseCode == 0)
                 //  return true;
           }
           ucipLog.println(msisdn + " UCIP invoke Failed with NULL repsonse..");
           logger.error(msisdn + " NULL UCIP response " ,null);
           return UcipResponseCode.NULLRESPONSE;

    }

 public boolean adjustDedAccountFD1(String dedAdjustmentAmount,
                                    String dedicatedAccountId, String msisdn,
                                    String adjCode, String adjType) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMkkmmssSSS");
        //XmlRpcClient client = getClient();
        String transactionId = sdf.format(now);
        AdjustmentRequest request = new AdjustmentRequest();
        request.setOriginHostName("IVR");
        request.setOriginNodeType("EXT");
        request.setOriginTimeStamp(new Date());
        request.setOriginTransactionID(transactionId);
        request.setSubscriberNumber(msisdn);
        request.setTransactionCurrency("EGP");
        DedicatedAccountInformation info =
            new DedicatedAccountInformation();
        info.setAdjustmentAmount(dedAdjustmentAmount);
        info.setDedicatedAccountID(new Integer(dedicatedAccountId));
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH,30);
        info.setNewExpiryDate(calendar.getTime());
        DedicatedAccountInformation[] infoArr =
            new DedicatedAccountInformation[1];
        infoArr[0] = info;
        request.setDedicatedAccountInformation(infoArr);
        request.setExternalData1(adjCode);
        request.setExternalData2(adjType);
        Object param = request;
        Object returnValue = null;
        Integer responseCode = -1;
        try {
            returnValue =
                    client.invoke("AdjustmentTRequest", new Object[] { param });
        } catch (XmlRpcFault e) {
            //e.printStackTrace();
            logger.error("XmlRpcFault: " ,e);
            ucipLog.println(msisdn + " UCIP invoke Failed with XmlRpcFault..");
        }
        if (returnValue != null) {
            responseCode =
                    ((Integer)((XmlRpcStruct)returnValue).get("responseCode")).intValue();
            logger.debug(msisdn + " RC: " + responseCode);
            System.out.println(msisdn + " RC: " + responseCode);
            if (responseCode == 0 || responseCode == 100){
                ucipLog.println(msisdn + " UCIP invoke succeded "
                        + "Dediacted ID: " + dedicatedAccountId +  " Dedicated amount: " + dedAdjustmentAmount);
                System.out.println(msisdn + " UCIP invoke succeded "
                        + "Dediacted ID: " + dedicatedAccountId +  " Dedicated amount: " + dedAdjustmentAmount);
                return true;
            }
            ucipLog.println(msisdn + " UCIP invoke Failed (for Dediacted ID: " + dedicatedAccountId +  " Dedicated amount: " + dedAdjustmentAmount+") with response Code: "+responseCode);
            logger.error(msisdn + "UCIP response Code: "+responseCode ,null);
            return false;
        }
        ucipLog.println(msisdn + " UCIP invoke Failed (for Dediacted ID: \" + dedicatedAccountId +  \" Dedicated amount: \" + dedAdjustmentAmount+\") with NULL repsonse..");
        logger.error(msisdn + "NULL UCIP response " ,null);
        return false;

    }
/*
 public Integer adjustDedAccountFD1(GenericSubInfo sub, DaMiniMap miniDAs){

        //FD1 Command
        UcipHandler.initFD1Request();

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMkkmmssSSS");
        //XmlRpcClient client = getClient();
        String transactionId = sdf.format(now);

        //AdjustmentRequest adj = (AdjustmentRequest)daMap.get(sub.getSubscriberNumber());
        AdjustmentRequest adj = new AdjustmentRequest();
        adj.setOriginHostName("IVR");
        adj.setOriginNodeType("EXT");
        adj.setTransactionCurrency("EGP");

        adj.setOriginTimeStamp(new Date());
        adj.setOriginTransactionID(transactionId);
        adj.setSubscriberNumber(sub.getSubscriberNumberShortFormat());

        //adj.setExternalData1(adjCode);
        //adj.setExternalData2(adjType);

        int i = 0;
        DedicatedAccountInformation[] infoArr =
            new DedicatedAccountInformation[miniDAs.size()];

        for (Iterator iter = miniDAs.entrySet().iterator(); iter.hasNext();i++) {
            Map.Entry entry = (Entry) iter.next();
            if ((Integer)entry.getKey() == 0){ //Main balance adjustment
                adj.setAdjustmentAmount(((DedicatedAccountInformation)miniDAs.get("0")).getAdjustmentAmount());
            }else{ //DA Structure
                infoArr[i] = new DedicatedAccountInformation();
                infoArr[i].setDedicatedAccountID((Integer)entry.getKey());
                infoArr[i].setAdjustmentAmount(((DedicatedAccountInformation)entry.getValue()).getAdjustmentAmount());
                if( miniDAs.get(((DedicatedAccountInformation)entry.getValue()).getNewExpiryDate()) != null)
                    infoArr[i].setNewExpiryDate(((DedicatedAccountInformation)entry.getValue()).getNewExpiryDate());
                  }
        }
       adj.setDedicatedAccountInformation(infoArr);

        //adj.getDedicatedAccountsInformation()[0].setDedicatedAccountID(2);
        //adj.getDedicatedAccountsInformation()[0].getNewExpiryDate();

        //System.out.println(adj.getDedicatedAccountsInformation()[0].getAdjustmentAmount());
        //System.out.println(adj.getDedicatedAccountsInformation()[0].getDedicatedAccountID());
        //System.out.println(adj.getDedicatedAccountsInformation()[0].getNewExpiryDate());


        Object param = adj;
        Object returnValue = null;
        Integer responseCode = -1;
        try {
            returnValue =
                    client.invoke("AdjustmentTRequest", new Object[] { param });
        } catch (XmlRpcFault e) {
            //e.printStackTrace();
            logger.error("XmlRpcFault: " ,e);
            ucipLog.println(sub.getSubscriberNumber() + " UCIP invoke Failed with XmlRpcFault..");
        }
        if (returnValue != null) {
            responseCode =
                    ((Integer)((XmlRpcStruct)returnValue).get("responseCode")).intValue();
            logger.debug(sub.getSubscriberNumber() +" RC: " + responseCode);
            ucipLog.println(sub.getSubscriberNumber() + " UCIP adjustment success with code: " + responseCode);
            return responseCode;
            //if (responseCode == 0)
              //  return true;
        }
        ucipLog.println(sub.getSubscriberNumber() + " UCIP invoke Failed with NULL repsonse..");
        logger.error(sub.getSubscriberNumber() + "NULL UCIP response " ,null);
        return UcipResponseCode.NULLRESPONSE;

 }


*/
public BasicUCIPResponse BalanceEnquiryTRequest(String msisdn) {

         //FD1 Command
        logger.debug("BalanceEnquiryTRequest for : "+msisdn );
        UcipHandler.initFD1Request();

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMkkmmssSSS");
        //XmlRpcClient client = getClient();
        String transactionId = sdf.format(now);
        BasicUCIPRequest request = new BasicUCIPRequest();
        request.setOriginHostName("IVR");
        request.setOriginNodeType("EXT");
        request.setOriginTimeStamp(new Date());
        request.setOriginTransactionID(transactionId);
        request.setSubscriberNumber(msisdn);
        Object param = request;
        Object returnValue = null;
        Integer responseCode = -1;
        //String accountValue = "";
        //Integer serviceClass = null;
        try {
            returnValue =
                    client.invoke("BalanceEnquiryTRequest", new Object[] { param });
        } catch (XmlRpcFault e) {
            //e.printStackTrace();
            logger.error("XmlRpcFault: " ,e);
            ucipLog.println(msisdn + " UCIP invoke Failed with XmlRpcFault..");
        }
        if (returnValue != null) {
            responseCode =
                    ((Integer)((XmlRpcStruct)returnValue).get("responseCode")).intValue();
            logger.debug(msisdn + " RC: " + responseCode);
            ucipLog.println(msisdn + " UCIP invoke BalanceEnquiryT success with code: " + responseCode);

            //if (responseCode == 0)
            //{
                //accountValue = (String)((XmlRpcStruct)returnValue).get("accountValue1");
                //serviceClass = (Integer)((XmlRpcStruct)returnValue).get("serviceClassCurrent");
                return new BasicUCIPResponse((XmlRpcStruct)returnValue);
            //}
    }
    ucipLog.println(msisdn + " UCIP invoke Failed with NULL repsonse..");
    logger.error(msisdn + "NULL UCIP response " ,null);
    return null;
    }

    public BasicUCIPResponse getBalance(String msisdn) {
        //FD6 command
        UcipHandler.initFD6Request();

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMkkmmssSSS");
        //XmlRpcClient client = getClient();
        String transactionId = sdf.format(now);
        BasicUCIPRequest request = new BasicUCIPRequest();
        request.setOriginHostName("IVR");
        request.setOriginNodeType("EXT");
        request.setOriginTimeStamp(new Date());
        request.setOriginTransactionID(transactionId);
        request.setSubscriberNumber(msisdn);
        //request.set
        Object param = request;
        Object returnValue = null;
        Integer responseCode = -1;
        //String accountValue = "";
        //Integer serviceClass = null;
        try {
            returnValue =
                    client.invoke("GetBalanceAndDate", new Object[] { param });
        } catch (XmlRpcFault e) {
            //e.printStackTrace();
            logger.error("XmlRpcFault: " ,e);
            ucipLog.println(msisdn + " UCIP invoke Failed with XmlRpcFault..");
        }
        if (returnValue != null) {
            responseCode =
                    ((Integer)((XmlRpcStruct)returnValue).get("responseCode")).intValue();
            logger.debug(msisdn + " RC: " + responseCode);
            ucipLog.println(msisdn + " UCIP invoke GetBalanceAndDate success with code: " + responseCode);
            //if (responseCode == 0)
            //{
                //accountValue = (String)((XmlRpcStruct)returnValue).get("accountValue1");
                //serviceClass = (Integer)((XmlRpcStruct)returnValue).get("serviceClassCurrent");
               
                return new BasicUCIPResponse((XmlRpcStruct)returnValue);
            //}
    }
    ucipLog.println(msisdn + " UCIP invoke Failed with NULL repsonse..");
    logger.error(msisdn + "NULL UCIP response " ,null);
    return null;
    }

    public void closeHandles(){
        
        ucipLog.println("Successfully Terminated UCIP Client..");

        if(ucipLog != null)
            ucipLog.close();

    }




    
}
