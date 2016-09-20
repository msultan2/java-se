package ucip;

import java.net.MalformedURLException;

import java.text.SimpleDateFormat;

import java.util.Date;

import redstone.xmlrpc.XmlRpcClient;
import redstone.xmlrpc.XmlRpcFault;
import redstone.xmlrpc.XmlRpcStruct;

import promo.util.PropertyHandler;

public class UcipInterface {
    public UcipInterface() {
    }

    public static XmlRpcClient getClient() {
        XmlRpcClient client = null;
        try {
            client = 
                    new XmlRpcClient(PropertyHandler.getProperty("Host0"), false);
            client.setRequestProperty("User-Agent", "UGw Server/3.1/1.0");
            client.setRequestProperty("Host", "VASP");
            client.setRequestProperty("Content-Type", "text/xml");
            client.setRequestProperty("Authorization", 
                                      "Basic U3lzQWRtOlN5c0FkbQ==");
        } catch (MalformedURLException e) {
            return null;
        }
        return client;
    }

    public static int updateServiceOffering(String msisdn, 
                                            String serviceOfferingInput) {
        SubscriberSegmentationRequest request = null;
        String[] serviceOfferingArray = null;
        XmlRpcClient client = getClient();
        int responseCode = -1;
        ServiceOffering serviceOffering = null;
        try {
            if (client != null) {
                Date now = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("ddMMkkmmssSSS");
                String transactionId = sdf.format(now);
                request = new SubscriberSegmentationRequest();
                request.setOriginHostName("IVR");
                request.setOriginNodeType("EXT");
                request.setOriginTimeStamp(new Date());
                request.setOriginTransactionID(transactionId);
                request.setSubscriberNumber(msisdn.trim());
                if (serviceOfferingInput == null)
                    return -1;
                serviceOfferingArray = serviceOfferingInput.split(",");

                if ((serviceOfferingArray.length % 2) != 0)
                    return -1;
                ServiceOffering[] serviceOfferings = 
                    new ServiceOffering[serviceOfferingArray.length / 2];
                
                for (int i = 0; i < serviceOfferingArray.length; i++) {



                    if ((i % 2) == 0){
                        serviceOffering = new ServiceOffering();
                        serviceOffering.setServiceOfferingID(new Integer(serviceOfferingArray[i]));
                    }
                        
                    else {

                        if (serviceOfferingArray[i].equals("1")) {


                            serviceOffering.setServiceOfferingActiveFlag(new Boolean(true));
                        } else if (serviceOfferingArray[i].equals("0")) {


                            serviceOffering.setServiceOfferingActiveFlag(new Boolean(false));
                        } else {


                            return -1;
                        }
                    }

                    if ((i % 2) != 0)

                        serviceOfferings[(i-1) / 2] = serviceOffering;
                }



                request.setServiceOfferings(serviceOfferings);
                Object param = request;
                Object returnValue = null;

                try {
                    returnValue = 
                            client.invoke("UpdateSubscriberSegmentation", new Object[] { param });
                } catch (XmlRpcFault e) {
                    e.printStackTrace();
                }
                if (returnValue != null) {
                    responseCode = 
                            ((Integer)((XmlRpcStruct)returnValue).get("responseCode")).intValue();
                }
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseCode;

    }

    public static void main(String[] args) {
        UcipInterface call = new UcipInterface();
        call.updateServiceOffering(args[0], args[1]);
    }


}
