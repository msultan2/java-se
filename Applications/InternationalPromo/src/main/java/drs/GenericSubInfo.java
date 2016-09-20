/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drs;

import charging.info.AccntBalanceChecker;
import charging.info.ChargingModelsMap;
import charging.info.DedicatedAccountInformation;
import promo.util.PromoParameters;
import promo.util.PromoParameters.OptInChannels;
import ucip.AdjustmentRequest;

/**
 *
 * @author anassar
 */


public class GenericSubInfo {

    private String subscriberNumber          ;
    private String timeStamp                 ;
    private Integer serviceClass             ;
    private Double promoTransactionAmount    ;
    private String optInText                 ;
    private String optInStatus               ;  //new by Sara Khaled
    private int promoCounter                 ;  //new by Sara Khaled
    private OptInChannels optInChannel       ;
    private String smsText                   ;
    private Integer smsReason                ;
    //private String successSMSText          ;
    private AccntBalanceChecker bChecker     ;
    private Integer chargingID               ;
    private Integer previousState            ;
    private Integer languageID = PromoParameters.DEFAULT_LANG_ID ;

    private Integer result                   ;
    private boolean error                    ;

    public GenericSubInfo(){
        bChecker =  new AccntBalanceChecker();
    }

    public GenericSubInfo(String subscriberNumber, String optInText, String optInChannel,Integer previousState){
        bChecker =  new AccntBalanceChecker();
        this.subscriberNumber = subscriberNumber;
        this.optInText = optInText;
        this.optInChannel = OptInChannels.valueOf(optInChannel.toUpperCase().trim());
        if(previousState == null)
            this.previousState = -1;
        else
        this.previousState = previousState;
    }

    /**
     * @return the subscriberNumber
     */
    public String getSubscriberNumber() {
        return subscriberNumber;
    }

    public String getSubscriberNumberShortFormat() {
        return subscriberNumber.replaceFirst("^20","");
    }

    /**
     * @param subscriberNumber the subscriberNumber to set
     */
    public void setSubscriberNumber(String subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
        //this.subscriberNumber = subscriberNumber.replaceFirst("^20","");
    }

    /**
     * @return the timeStamp
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @return the serviceClass
     */
    public int getServiceClass() {
        return serviceClass.intValue();
    }

    /**
     * @param serviceClass the serviceClass to set
     */
    public void setServiceClass(int serviceClass) {
        this.serviceClass = serviceClass;
    }

    /**
     * @return the promoTransactionAmount
     */
    public Double getPromoTransactionAmount() {
        return promoTransactionAmount;
    }

    /**
     * @param promoTransactionAmount the promoTransactionAmount to set
     */
    public void setPromoTransactionAmount(Double promoTransactionAmount) {
        this.promoTransactionAmount = promoTransactionAmount;
    }

    /**
     * @return the optInText
     */
    public String getOptInText() {
        return optInText;
    }

    /**
     * @param optInText the optInText to set
     */
    public void setOptInText(String optInText) {
        this.optInText = optInText;
    }

    /**
     * @return the refundSMSText
     */
    public String getSmsText() {
        return smsText;
    }

    /**
     * @param refundSMSText the refundSMSText to set
     */
    public void setSmsText(String refundSMSText) {
        this.smsText = refundSMSText;
    }

    /**
     * @return the successSMSText
     */
    public int getSmsReason() {
        return smsReason.intValue();
    }

    public void setSmsReason(int smsReason) {
        this.smsReason = smsReason;
    }

   
    public Double getAccntBefore(int accntID) {
        return bChecker.getAccntBalanceBefore(accntID);
    }

   
    public void setAccntBefore(Integer accntID, Double balance) {
        bChecker.setAccntBalanceBefore(accntID, balance);
    }

    public Double getAccntAfter(Integer accntID) {
        return bChecker.getAccntBalanceAfter(accntID);
    }


    public void setAccntAfter(Integer accntID, Double balance) {
        bChecker.setAccntBalanceAfter(accntID, balance);
    }

    /**
     * @return the result
     */
    public int getResult() {
        return result.intValue();
    }

    /**
     * @param result the result to set
     */
    public void setResult(int result) {
        this.result = result;
    }

    /**
     * @return the error
     */
    public boolean isError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(boolean error) {
        this.error = error;
    }

    /**
     * @return the chargingID
     */
    public int getChargingID() {
        return chargingID.intValue();
    }

    /**
     * @param chargingID the chargingID to set
     */
    public void setChargingID(int chargingID) {
        this.chargingID = chargingID;
    }

     /**
     * @return the optInChannel
     */
    public OptInChannels getOptInChannel() {
        return optInChannel;
    }

    /**
     * @param optInChannel the optInChannel to set
     */
    public void setOptInChannel(OptInChannels optInChannel) {
        this.optInChannel = optInChannel;
    }

      /**
     * @return the previousState
     */
    public int getPreviousState() {
        return previousState.intValue();
    }

    /**
     * @param previousState the previousState to set
     */
    public void setPreviousState(Integer previousState) {
        this.previousState = previousState;
    }

     /**
     * @return the languageID
     */
    public Integer getLanguageID() {
        return languageID;
    }

    /**
     * @param languageID the languageID to set
     */
    public void setLanguageID(Integer languageID) {
        this.languageID = languageID.intValue();
    }



    public boolean checkPromo(ChargingModelsMap chgMap){
        boolean flag = true;
        String mainAdj = ((AdjustmentRequest)chgMap.get(chargingID)).getAdjustmentAmount();
        if ( mainAdj != null )
           flag = bChecker.checkPromo(0,new Double(mainAdj));
        for(DedicatedAccountInformation dainf : ((AdjustmentRequest)chgMap.get(chargingID)).getDedicatedAccountInformation()){
            flag = bChecker.checkPromo(dainf.getDedicatedAccountID(),new Double(dainf.getAdjustmentAmount()));
        }

        return flag;
    }

    //public String


    public void setOptInStatus(String optInStatus) {
        this.optInStatus = optInStatus;
    }

    public String getOptInStatus() {
        return optInStatus;
    }

    public void setPromoCounter(int promoCounter) {
        this.promoCounter = promoCounter;
    }

    public int getPromoCounter() {
        return promoCounter;
    }
}
