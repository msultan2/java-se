/*
 * To change this template; choose Tools | Templates
 * and open the template in the editor.
 */

package drs;

/**
 *
 * @author anassar
 */
public class Payment {
    
   
	private String subscriberNumber          ;
	private String transactionID		 ;
	private String transactionAmount	 ;
	private String timeStamp		 ;
	private String originOperatorID          ;
	private String voucherGroup              ;
	private String serviceExpieryDate	 ;
	private Integer serviceClass             ;
        private String externalData1		 ;
        private String externalData2		 ;
	private String rechargeDivAmountPDedicated1	 ;
	private String rechargeDivAmountPDedicated2	 ;
	private String rechargeDivAmountPDedicated3	 ;
	private String rechargeDivAmountPDedicated4	 ;
	private String rechargeDivAmountPDedicated5	 ;
	private Double realTransactionAmount             ;
        private Double promoTransactionAmount            ;
        private String smsText                           ;
    
        public Payment(String [] payData){
            
            this.subscriberNumber = payData[0];
            this.transactionID = payData[1];
            this.transactionAmount = payData[2];
            this.timeStamp = payData[3];
            this.originOperatorID = payData[4];
            this.voucherGroup = payData[5];
            this.serviceExpieryDate = payData[6];
            
            try{
                this.serviceClass = Integer.parseInt(payData[7]);
            }catch (Exception Ex){
                this.serviceClass = null;
                Ex.printStackTrace();
            }
            
            this.externalData1 = payData[8];
            this.externalData2 = payData[9];
            this.rechargeDivAmountPDedicated1 = payData[10];
            this.rechargeDivAmountPDedicated2 = payData[11];
            this.rechargeDivAmountPDedicated3 = payData[12];
            this.rechargeDivAmountPDedicated4 = payData[13];
            this.rechargeDivAmountPDedicated5 = payData[14];
            try{
                this.realTransactionAmount = Double.valueOf(payData[15]);
            }catch (Exception Ex){
                this.realTransactionAmount = new Double (0.0);
                Ex.printStackTrace();
            }
            
        }

     public Payment(){
           this.subscriberNumber = "-1";
     }

    public String getSubscriberNumber() {
        return subscriberNumber;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getOriginOperatorID() {
        return originOperatorID;
    }

    public String getVoucherGroup() {
        return voucherGroup;
    }

    public String getServiceExpieryDate() {
        return serviceExpieryDate;
    }

    public Integer getServiceClass() {
        return serviceClass;
    }

    public String getExternalData1() {
        return externalData1;
    }

    public String getExternalData2() {
        return externalData2;
    }

    public String getRechargeDivAmountPDedicated1() {
        return rechargeDivAmountPDedicated1;
    }

    public String getRechargeDivAmountPDedicated2() {
        return rechargeDivAmountPDedicated2;
    }

    public String getRechargeDivAmountPDedicated3() {
        return rechargeDivAmountPDedicated3;
    }

    public String getRechargeDivAmountPDedicated4() {
        return rechargeDivAmountPDedicated4;
    }

    public String getRechargeDivAmountPDedicated5() {
        return rechargeDivAmountPDedicated5;
    }

    public Double getRealTransactionAmount() {
        return realTransactionAmount;
    }

    public Double getPromoTransactionAmount() {
        if (this.promoTransactionAmount != null)
            return promoTransactionAmount;
        else return 0.0;
    }

    public void setPromoTransactionAmount(Double promoTransactionAmount) {
            this.promoTransactionAmount = promoTransactionAmount;
    }

    public String getSmsText() {
        return smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }

}
