package charging.info;

import java.util.Date;

public class DedicatedAccountUpdateInformation {

    private Integer dedicatedAccountID;
    private String adjustmentAmountRelative;
    private String dedicatedAccountValueNew;
    private String adjustmentDateRelative;
    private Date expiryDate;

    public DedicatedAccountUpdateInformation() {
    }

    public DedicatedAccountUpdateInformation(Integer dedicatedAccountID, String adjustmentAmountRelative
            ,Date expiryDate) {

        this.dedicatedAccountID = dedicatedAccountID;
        this.adjustmentAmountRelative = adjustmentAmountRelative;
        if(expiryDate != null){
              this.expiryDate = expiryDate;
        }

    }

    public DedicatedAccountUpdateInformation(Integer dedicatedAccountID, String adjustmentAmountRelative) {

        this.dedicatedAccountID = dedicatedAccountID;
        this.adjustmentAmountRelative = adjustmentAmountRelative;

    }


    public void setDedicatedAccountID(Integer dedicatedAccountID) {
        this.dedicatedAccountID = dedicatedAccountID;
    }

    public Integer getDedicatedAccountID() {
        return dedicatedAccountID;
    }

    public void setAdjustmentAmountRelative(String adjustmentAmountRelative) {
        this.adjustmentAmountRelative = adjustmentAmountRelative;
    }

    public String getAdjustmentAmountRelative() {
        return adjustmentAmountRelative;
    }

    public void setDedicatedAccountValueNew(String dedicatedAccountValueNew) {
        this.dedicatedAccountValueNew = dedicatedAccountValueNew;
    }

    public String getDedicatedAccountValueNew() {
        return dedicatedAccountValueNew;
    }

    public void setAdjustmentDateRelative(String adjustmentDateRelative) {
        this.adjustmentDateRelative = adjustmentDateRelative;
    }

    public String getAdjustmentDateRelative() {
        return adjustmentDateRelative;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }
}
