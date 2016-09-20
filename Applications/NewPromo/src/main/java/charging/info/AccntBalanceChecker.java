/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package charging.info;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author anassar
 */

public class AccntBalanceChecker
        extends HashMap
        implements Map  {

 public AccntBalanceChecker(){
        //super(new HashMap<Integer, List<String>>());
        super(new HashMap<Integer, BeforeVsAfterInfo>());
    }

 public Double getAccntBalanceBefore(Integer accntId){
     if (super.get(accntId) != null)
        return ((BeforeVsAfterInfo)super.get(accntId)).getBalanceBefore();
     else return null;
 }

 public Double getAccntBalanceAfter(Integer accntId){
     if (super.get(accntId) != null)
        return ((BeforeVsAfterInfo)super.get(accntId)).getBalanceAfter();
     else return null;
 }

public void setAccntBalanceBefore(Integer accntID, Double bBefore){
    if(super.get(accntID) == null )
        super.put(accntID, new BeforeVsAfterInfo(accntID,bBefore));
    else
        ((BeforeVsAfterInfo)super.get(accntID)).setBalanceBefore(bBefore);
}

public void setAccntBalanceAfter(Integer accntID, Double bAfter ){
    BeforeVsAfterInfo xx;
    if(super.get(accntID) == null ){
        super.put(accntID, xx = new BeforeVsAfterInfo());
        xx.setAccntID(accntID);
        xx.setBalanceAfter(bAfter);
    }else
        ((BeforeVsAfterInfo)super.get(accntID)).setBalanceAfter(bAfter);
}

public boolean checkPromo(Integer accntID, Double promoValue){
    return (((BeforeVsAfterInfo)super.get(accntID)).getdelta() == promoValue) ? true : false ;
}



private class BeforeVsAfterInfo{

        private Double balanceBefore;
        private Double balanceAfter;
        private Integer accntID;
        
        public BeforeVsAfterInfo(){
            
        }

        public BeforeVsAfterInfo(Integer accntID,Double balanceBefore){
            this.accntID = accntID;
            this.balanceBefore = balanceBefore;

        }

        /**
         * @return the balanceBefore
         */
        public Double getBalanceBefore() {
            return balanceBefore;
        }

        /**
         * @param balanceBefore the balanceBefore to set
         */
        public void setBalanceBefore(Double balanceBefore) {
            this.balanceBefore = balanceBefore;
        }

        /**
         * @return the balanceAfter
         */
        public Double getBalanceAfter() {
            return balanceAfter;
        }

        /**
         * @param balanceAfter the balanceAfter to set
         */
        public void setBalanceAfter(Double balanceAfter) {
            this.balanceAfter = balanceAfter;
        }

        public double getdelta(){
            return balanceAfter - balanceBefore ;
        }

        /**
         * @return the accntID
         */
        public Integer getAccntID() {
            return accntID;
        }

        /**
         * @param accntID the accntID to set
         */
        public void setAccntID(Integer accntID) {
            this.accntID = accntID;
        }

    }
}