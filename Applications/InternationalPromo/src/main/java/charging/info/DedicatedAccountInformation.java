package charging.info;

import java.util.Date;

public class DedicatedAccountInformation {  
    
    private Integer dedicatedAccountID;
    private String adjustmentAmount;
    private Date newExpiryDate;

    public DedicatedAccountInformation() {
    }

    public DedicatedAccountInformation(Integer dedicatedAccountID, String adjustmentAmount
            ,Date newExpiryDate) {

        this.dedicatedAccountID = dedicatedAccountID;
        this.adjustmentAmount = adjustmentAmount;
        this.newExpiryDate = newExpiryDate;

    }

    public DedicatedAccountInformation(Integer dedicatedAccountID, String adjustmentAmount) {

        this.dedicatedAccountID = dedicatedAccountID;
        this.adjustmentAmount = adjustmentAmount;

    }

    public void setDedicatedAccountID(Integer dedicatedAccountID) {
        this.dedicatedAccountID = dedicatedAccountID;
    }

    public Integer getDedicatedAccountID() {
        return dedicatedAccountID;
    }

    public void setAdjustmentAmount(String adjustmentAmount) {
        this.adjustmentAmount = adjustmentAmount;
    }

    public String getAdjustmentAmount() {
        return adjustmentAmount;
    }


    public void setNewExpiryDate(Date newExpiryDate) {
        this.newExpiryDate = newExpiryDate;
    }

    public Date getNewExpiryDate() {
        return newExpiryDate;
    }
}
