package ucip;

/*
 * File name:BaseUCIPRequest.java
 * Copyright:Copyright 2004-2006 Huawei Tech. Co. Ltd. All Rights Reserved
 * Description:
 * Modify by:
 * Modify time: YYYY-MM-DD
 * Tracking ID:
 * Modify content:
 */

import charging.info.DedicatedAccountEnquiryInformation;
import charging.info.DedicatedAccountInformation;
import java.util.ArrayList;
import java.util.Date;
import redstone.xmlrpc.XmlRpcArray;
import redstone.xmlrpc.XmlRpcStruct;

/**
 * <p>Title:  </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright 1998-2007,  All rights reserved</p>
 * <p>Company: Huawei Technologies Co., Ltd. </p>
 * @author q43637
 * @version V1.0 2007-7-25
 * @since
 */
public class BasicUCIPResponse extends XmlRpcStruct
{

    /*private int	responseCode;
    private String	originTransactionID;
    private int	serviceClassCurrent;
    private String	currency1;
    private String	accountValue1;
    private String	currency2;
    private String	accountValue2;
    private DedicatedAccountInformation_OLD	dedicatedAccountInformation;
    private Date	supervisionExpiryDate;
    private Date	serviceFeeExpiryDate;
    private Date	creditClearanceDate;
    private Date	serviceRemovalDate;
    private int	languageIDCurrent;
    private boolean	temporaryBlockedFlag;
    */
    
    //charginResultInformation;
    //should be added, of type struct <chargingResultInformation>

    private XmlRpcStruct xmlrpcstruct;

    /**
     * @return the responseCode
     */
    public BasicUCIPResponse(XmlRpcStruct xmlrpcstruct){
        this.xmlrpcstruct = xmlrpcstruct;
    }

    public Integer getResponseCode() {
        return (Integer)xmlrpcstruct.get("responseCode");
    }

    /**
     * @return the originTransactionID
     */
    public String getOriginTransactionID() {
        return (String)xmlrpcstruct.get("originTransactionID");
    }

    /**
     * @return the serviceClassCurrent
     */
    public Integer getServiceClassCurrent() {
        return (Integer)xmlrpcstruct.get("serviceClassCurrent");
    }

    /**
     * @return the currency1
     */
    public String getCurrency1() {
        return (String)xmlrpcstruct.get("currency1");
    }

    /**
     * @return the accountValue1
     */
    public String getAccountValue1() {
        return (String)xmlrpcstruct.get("accountValue1");
    }

    /**
     * @return the currency2
     */
    public String getCurrency2() {
         return (String)xmlrpcstruct.get("currency2");
    }

    /**
     * @return the accountValue2
     */
    public String getAccountValue2() {
        return (String)xmlrpcstruct.get("accountValue2");
    }

    /**
     * @return the dedicatedAccountInformation
     */
    //public DedicatedAccountEnquiryInformation[] getDedicatedAccountInformation() {
    public DedicatedAccountEnquiryInformation[] getDedicatedAccountInformation() {
       XmlRpcArray arr = (XmlRpcArray) xmlrpcstruct.get("dedicatedAccountInformation");
       //System.out.println(arr.size());
       DedicatedAccountEnquiryInformation[] daArr =  new DedicatedAccountEnquiryInformation[arr.size()];

       for(int i = 0 ; i < arr.size(); i++){
           XmlRpcStruct xstruct = arr.getStruct(i);
           daArr [i] = new DedicatedAccountEnquiryInformation(xstruct.getInteger("dedicatedAccountID"),xstruct.getString("dedicatedAccountValue1"),
                   xstruct.getString("dedicatedAccountValue2"),xstruct.getDate("expiryDate"));
       }
         //daArr [i] = new DedicatedAccountEnquiryInformation(.getInteger(i));
       return daArr;
    }

    /**
     * @return the supervisionExpiryDate
     */
    public Date getSupervisionExpiryDate() {
        return (Date)xmlrpcstruct.get("supervisionExpiryDate");
    }

    /**
     * @return the serviceFeeExpiryDate
     */
    public Date getServiceFeeExpiryDate() {
        return (Date)xmlrpcstruct.get("serviceFeeExpiryDate");
    }

    /**
     * @return the creditClearanceDate
     */
    public Date getCreditClearanceDate() {
        return (Date)xmlrpcstruct.get("creditClearanceDate");
    }

    /**
     * @return the serviceRemovalDate
     */
    public Date getServiceRemovalDate() {
        return (Date)xmlrpcstruct.get("serviceRemovalDate");
    }

    /**
     * @return the languageIDCurrent
     */
    public Integer getLanguageIDCurrent() {
        return (Integer)xmlrpcstruct.get("languageIDCurrent");
    }

    /**
     * @return the temporaryBlockedFlag
     */
    public boolean isTemporaryBlockedFlag() {
        return ((Boolean)xmlrpcstruct.get("temporaryBlockedFlag")).booleanValue();
    }
   
   
}

