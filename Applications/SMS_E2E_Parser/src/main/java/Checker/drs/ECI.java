package Checker.drs;

public class ECI {
    boolean df=false;
    boolean vmstat=false;
    boolean tp_status=false;
    boolean pbcChainsRet=false;
    boolean diamTransAdminState=false;
    boolean diamTransState=false;
    public ECI() {
    }

    public void setDf(boolean df) {
        this.df = df;
    }

    public boolean isDf() {
        return df;
    }

    public void setTp_status(boolean tp_status) {
        this.tp_status = tp_status;
    }

    public boolean isTp_status() {
        return tp_status;
    }

    public void setPbcChainsRet(boolean pbcChainsRet) {
        this.pbcChainsRet = pbcChainsRet;
    }

    public boolean isPbcChainsRet() {
        return pbcChainsRet;
    }

    public void setDiamTransAdminState(boolean diamTransAdminState) {
        this.diamTransAdminState = diamTransAdminState;
    }

    public boolean isDiamTransAdminState() {
        return diamTransAdminState;
    }

    public void setDiamTransState(boolean diamTransState) {
        this.diamTransState = diamTransState;
    }

    public boolean isDiamTransState() {
        return diamTransState;
    }

    public void setVmstat(boolean vmstat) {
        this.vmstat = vmstat;
    }

    public boolean isVmstat() {
        return vmstat;
    }
}
