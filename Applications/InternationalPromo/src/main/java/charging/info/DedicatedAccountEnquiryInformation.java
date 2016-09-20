package charging.info;

import java.util.Date;

public class DedicatedAccountEnquiryInformation {  
    
    private Integer dedicatedAccountID;
    private String dedicatedAccountValue1;
    private String dedicatedAccountValue2;
    private Date expiryDate;


    public DedicatedAccountEnquiryInformation() {  
    }

    public DedicatedAccountEnquiryInformation(Integer dedicatedAccountID,String dedicatedAccountValue1,
            String dedicatedAccountValue2,Date expiryDate ) {
        this.dedicatedAccountID = dedicatedAccountID;
        this.dedicatedAccountValue1 = dedicatedAccountValue1;
        this.dedicatedAccountValue2 = dedicatedAccountValue2;
        this.expiryDate = expiryDate;
    }

    /**
     * @return the dedicatedAccountID
     */
    public Integer getDedicatedAccountID() {
        return dedicatedAccountID;
    }

    /**
     * @return the dedicatedAccountValue1
     */
    public String getDedicatedAccountValue1() {
        return dedicatedAccountValue1;
    }

    /**
     * @return the dedicatedAccountValue2
     */
    public String getDedicatedAccountValue2() {
        return dedicatedAccountValue2;
    }

    /**
     * @return the expiryDate
     */
    public Date getExpiryDate() {
        return expiryDate;
    }


    
}
