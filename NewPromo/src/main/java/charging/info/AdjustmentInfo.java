/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package charging.info;

/**
 *
 * @author anassar
 */
public class AdjustmentInfo {


    private String adjustmentCode;
    private String adjustmentType;


    public AdjustmentInfo(String adjustmentCode, String adjustmentType){
        this.adjustmentCode = adjustmentCode;
        this.adjustmentType = adjustmentType;
    }

    /**
     * @return the adjustmentCode
     */
    public String getAdjustmentCode() {
        return adjustmentCode;
    }

    /**
     * @param adjustmentCode the adjustmentCode to set
     */
    public void setAdjustmentCode(String adjustmentCode) {
        this.adjustmentCode = adjustmentCode;
    }

    /**
     * @return the adjustmentType
     */
    public String getAdjustmentType() {
        return adjustmentType;
    }

    /**
     * @param adjustmentType the adjustmentType to set
     */
    public void setAdjustmentType(String adjustmentType) {
        this.adjustmentType = adjustmentType;
    }

}
