package ucip;

//import charging.info.DedicatedAccountInformation;
import charging.info.AdjustmentInfo;
import charging.info.DedicatedAccountInformation;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import promo.util.PromoParameters;

public class AdjustmentRequest extends BasicUCIPRequest{

    
    //Assumption that MAIN account has ID Zero

    public AdjustmentRequest() {
    }

    private String transactionCurrency;
    private DedicatedAccountInformation[] dedicatedAccountInformation;


    public void setTransactionCurrency(String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
    }

    public String getTransactionCurrency() {
        return transactionCurrency;
    }

    public void setDedicatedAccountInformation(DedicatedAccountInformation[] dedicatedAccountsInformation) {
        this.dedicatedAccountInformation = dedicatedAccountsInformation;
    }

     public DedicatedAccountInformation[] getDedicatedAccountInformation() {
        return dedicatedAccountInformation;
    }

    /*public void initAllAccountsInformation(DedicatedAccountInformation[] dedicatedAccountsInformation) {
        for(int i = 0; i < dedicatedAccountsInformation.length ; i++)
            if (dedicatedAccountsInformation[i].getDedicatedAccountID() == MAIN_ACCNT_ID )
                super.setAdjustmentAmount(dedicatedAccountsInformation[i].getAdjustmentAmount());

        this.dedicatedAccountsInformation = dedicatedAccountsInformation;
    }*/

    public void setDedicatedAccountInformation(ArrayList <DedicatedAccountInformation> dedicatedAccountsInformation) {
        this.dedicatedAccountInformation =
                (DedicatedAccountInformation[]) dedicatedAccountsInformation.toArray(
                new DedicatedAccountInformation[dedicatedAccountsInformation.size()]);
    }

    

    public void initAllAccountsInformation(ArrayList <DedicatedAccountInformation> dedicatedAccountsInformation) {
       for (Iterator it = dedicatedAccountsInformation.iterator(); it.hasNext();) {
         DedicatedAccountInformation dd = (DedicatedAccountInformation)it.next();
         if(dd.getDedicatedAccountID() == PromoParameters.MAIN_ACCNT_ID){
             super.setAdjustmentAmount(dd.getAdjustmentAmount());
             it.remove();
         }
                    
       }

        //for(DedicatedAccountInformation dd : dedicatedAccountsInformation){
            //if(dd.getDedicatedAccountID() == PromoParameters.MAIN_ACCNT_ID)
              //   super.setAdjustmentAmount(dd.getAdjustmentAmount());
                 //dedicatedAccountsInformation.remove(dd);
        //}
        this.dedicatedAccountInformation =
                (DedicatedAccountInformation[]) dedicatedAccountsInformation.toArray(
                new DedicatedAccountInformation[dedicatedAccountsInformation.size()]);
    }

    public void initAccntInfo(int id, String val, int newExp){
        if(id == PromoParameters.MAIN_ACCNT_ID){
            super.setAdjustmentAmount(val);
            return;
        }
        for(DedicatedAccountInformation da : dedicatedAccountInformation){
            if (da.getDedicatedAccountID() == id){
                da.setAdjustmentAmount(val);
                if(newExp != PromoParameters.INVALID_CHOICE){
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.add(Calendar.DAY_OF_MONTH, PromoParameters.DAYS_OF_MONTH*newExp);
                    da.setNewExpiryDate(calendar.getTime());
                }
            }
        }
    }
    
    public void initAdjustmentInfo(AdjustmentInfo adjInf){
        super.setExternalData1(adjInf.getAdjustmentCode());
        super.setExternalData2(adjInf.getAdjustmentType());
    }
}

