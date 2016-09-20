package ucip;

import charging.info.DedicatedAccountInformation;
import charging.info.DedicatedAccountUpdateInformation;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import promo.util.PromoParameters;

public class UpdateBalDateRequest extends BasicUCIPRequest{

    public UpdateBalDateRequest() {
    }

    public UpdateBalDateRequest(AdjustmentRequest adj) {
        if(adj.getAdjustmentAmount() != null)
            super.setAdjustmentAmount(adj.getAdjustmentAmount());
        DedicatedAccountInformation[] daf_arr = adj.getDedicatedAccountInformation();
        if ( daf_arr != null){
            DedicatedAccountUpdateInformation[] dauf_arr = new DedicatedAccountUpdateInformation[daf_arr.length];
            int i =0;
            for(DedicatedAccountInformation daf : daf_arr ){
              dauf_arr[i] = new DedicatedAccountUpdateInformation(daf.getDedicatedAccountID(),daf.getAdjustmentAmount()
                      ,daf.getNewExpiryDate());
              i++;
              }
            this.setDedicatedAccountUpdateInformation(dauf_arr);
         }
    }

    private String transactionCurrency;
    private DedicatedAccountUpdateInformation[] dedicatedAccountUpdateInformation;

    public void setTransactionCurrency(String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
    }

    public String getTransactionCurrency() {
        return transactionCurrency;
    }

     public void setDedicatedAccountUpdateInformation(ArrayList <DedicatedAccountUpdateInformation> dedicatedAccountsUpdateInformation) {
          this.dedicatedAccountUpdateInformation =
                (DedicatedAccountUpdateInformation[]) dedicatedAccountsUpdateInformation.toArray(
                new DedicatedAccountUpdateInformation[dedicatedAccountsUpdateInformation.size()]);
     }

    public void setDedicatedAccountUpdateInformation(DedicatedAccountUpdateInformation[] dedicatedAccountUpdateInformation) {
        this.dedicatedAccountUpdateInformation = dedicatedAccountUpdateInformation;
    }

    public DedicatedAccountUpdateInformation[] getDedicatedAccountUpdateInformation() {
        return dedicatedAccountUpdateInformation;
    }

    public void initAllAccountsUpdateInformation(ArrayList <DedicatedAccountUpdateInformation> dedicatedAccountsUpdateInformation) {
        for (Iterator it = dedicatedAccountsUpdateInformation.iterator(); it.hasNext();) {
         DedicatedAccountUpdateInformation dd = (DedicatedAccountUpdateInformation)it.next();
         if(dd.getDedicatedAccountID() == PromoParameters.MAIN_ACCNT_ID){
             super.setAdjustmentAmount(dd.getAdjustmentAmountRelative());
             it.remove();
         }
       }
       this.dedicatedAccountUpdateInformation =
                (DedicatedAccountUpdateInformation[]) dedicatedAccountsUpdateInformation.toArray(
                new DedicatedAccountUpdateInformation[dedicatedAccountsUpdateInformation.size()]);
    }

     public void initAccntUpdateInfo(int id, String val, int newExp){
          if(id == PromoParameters.MAIN_ACCNT_ID){
            super.setAdjustmentAmount(val);
            return;
        }
        for(DedicatedAccountUpdateInformation da : dedicatedAccountUpdateInformation){
            if (da.getDedicatedAccountID() == id){
                da.setAdjustmentAmountRelative(val);
                if(newExp != PromoParameters.INVALID_CHOICE){
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.add(Calendar.DAY_OF_MONTH, PromoParameters.DAYS_OF_MONTH*newExp);
                    da.setExpiryDate(calendar.getTime());
                }
            }
        }
     }

}
