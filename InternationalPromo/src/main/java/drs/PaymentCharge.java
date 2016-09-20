package drs;
/*
 * Created By Sara Khaled
 *  16-6-2009
 *  PCR Structure
 */
public class PaymentCharge {
    
    private String subscriberNumber;
    private String voucherSerialNumber;
    private String transactionAmount;
    private Double realTransactionAmount;
    private Double PromoTransactionAmount;
    private Double dedAccountBefore;
    private String timeStamp;
    private String originOperatorID;
    private String voucherGroup;
    private String serviceFeeDateAfter;
    private Integer serviceClassCurrent;
    private String rechargeDivAmountPDedicated1;
    private String rechargeDivAmountPDedicated2;
    private String rechargeDivAmountPDedicated3;
    private String rechargeDivAmountPDedicated4;
    private String rechargeDivAmountPDedicated5;
    private String rechargeDivAmountPDedicated6;
    private String smsText;
    
    public PaymentCharge(String [] payData){
            
            this.subscriberNumber = payData[0];
            this.voucherSerialNumber = payData[1];
            this.transactionAmount = payData[2];
            this.timeStamp = payData[3];
            this.originOperatorID = payData[4];
            this.voucherGroup = payData[5];
            this.serviceFeeDateAfter = payData[6];
            
            try{
                this.serviceClassCurrent = Integer.parseInt(payData[7]);
            }catch (Exception Ex){
                this.serviceClassCurrent = null;
                Ex.printStackTrace();
            }/*
            try{
                this.rechargeDivAmountPDedicated1 = payData[8];
            }catch (Exception Ex){
                this.rechargeDivAmountPDedicated1 = null;
                Ex.printStackTrace();
            }
            try{
                this.rechargeDivAmountPDedicated2 = payData[9];
            }catch (Exception Ex){
                this.rechargeDivAmountPDedicated2 = null;
                Ex.printStackTrace();
            }
            try{
                this.rechargeDivAmountPDedicated3 = payData[10];
            }catch (Exception Ex){
                this.rechargeDivAmountPDedicated3 = null;
                Ex.printStackTrace();
            }
            try{
                this.rechargeDivAmountPDedicated4 = payData[11];
            }catch (Exception Ex){
                this.rechargeDivAmountPDedicated4 = null;
                Ex.printStackTrace();
            }
            try{
                this.rechargeDivAmountPDedicated5 = payData[12];
            }catch (Exception Ex){
                this.rechargeDivAmountPDedicated5 = null;
                Ex.printStackTrace();
            }
            */
            try{
                this.realTransactionAmount = Double.valueOf(payData[2]);
            }catch (Exception Ex){
                this.realTransactionAmount = new Double (0.0);
                Ex.printStackTrace();
            }
            
        }

    public void setSubscriberNumber(String subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
    }

    public String getSubscriberNumber() {
        return subscriberNumber;
    }
    public String getSubscriberNumberShortFormat() {
        return subscriberNumber.replaceFirst("^20","");
    }
    public void setVoucherSerialNumber(String voucherSerialNumber) {
        this.voucherSerialNumber = voucherSerialNumber;
    }

    public String getVoucherSerialNumber() {
        return voucherSerialNumber;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setOriginOperatorID(String originOperatorID) {
        this.originOperatorID = originOperatorID;
    }

    public String getOriginOperatorID() {
        return originOperatorID;
    }

    public void setVoucherGroup(String voucherGroup) {
        this.voucherGroup = voucherGroup;
    }

    public String getVoucherGroup() {
        return voucherGroup;
    }

    public void setServiceFeeDateAfter(String serviceFeeDateAfter) {
        this.serviceFeeDateAfter = serviceFeeDateAfter;
    }

    public String getServiceFeeDateAfter() {
        return serviceFeeDateAfter;
    }

    public void setServiceClassCurrent(Integer serviceClassCurrent) {
        this.serviceClassCurrent = serviceClassCurrent;
    }

    public Integer getServiceClassCurrent() {
        return serviceClassCurrent;
    }

    public void setRechargeDivAmountPDedicated1(String rechargeDivAmountPDedicated1) {
        this.rechargeDivAmountPDedicated1 = rechargeDivAmountPDedicated1;
    }

    public String getRechargeDivAmountPDedicated1() {
        return rechargeDivAmountPDedicated1;
    }

    public void setRechargeDivAmountPDedicated2(String rechargeDivAmountPDedicated2) {
        this.rechargeDivAmountPDedicated2 = rechargeDivAmountPDedicated2;
    }

    public String getRechargeDivAmountPDedicated2() {
        return rechargeDivAmountPDedicated2;
    }

    public void setRechargeDivAmountPDedicated3(String rechargeDivAmountPDedicated3) {
        this.rechargeDivAmountPDedicated3 = rechargeDivAmountPDedicated3;
    }

    public String getRechargeDivAmountPDedicated3() {
        return rechargeDivAmountPDedicated3;
    }

    public void setRechargeDivAmountPDedicated4(String rechargeDivAmountPDedicated4) {
        this.rechargeDivAmountPDedicated4 = rechargeDivAmountPDedicated4;
    }

    public String getRechargeDivAmountPDedicated4() {
        return rechargeDivAmountPDedicated4;
    }

    public void setRechargeDivAmountPDedicated5(String rechargeDivAmountPDedicated5) {
        this.rechargeDivAmountPDedicated5 = rechargeDivAmountPDedicated5;
    }

    public String getRechargeDivAmountPDedicated5() {
        return rechargeDivAmountPDedicated5;
    }

    public void setRealTransactionAmount(Double realTransactionAmount) {
        this.realTransactionAmount = realTransactionAmount;
    }

    public Double getRealTransactionAmount() {
        return realTransactionAmount;
    }

    public void setRechargeDivAmountPDedicated6(String rechargeDivAmountPDedicated6) {
        this.rechargeDivAmountPDedicated6 = rechargeDivAmountPDedicated6;
    }

    public String getRechargeDivAmountPDedicated6() {
        return rechargeDivAmountPDedicated6;
    }

    public void setPromoTransactionAmount(Double promoTransactionAmount) {
        this.PromoTransactionAmount = promoTransactionAmount;
    }

    public Double getPromoTransactionAmount() {
        return PromoTransactionAmount;
    }
    public String getSmsText() {
        return smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }

    public void setDedAccountBefore(Double dedAccountBefore) {
        this.dedAccountBefore = dedAccountBefore;
    }

    public Double getDedAccountBefore() {
        return dedAccountBefore;
    }
}
