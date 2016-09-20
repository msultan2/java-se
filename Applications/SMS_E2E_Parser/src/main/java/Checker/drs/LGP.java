package Checker.drs;

public class LGP {
    boolean df=false;
    boolean vmstat=false;
    boolean tp_status=false;
    public LGP() {
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

    public void setVmstat(boolean vmstat) {
        this.vmstat = vmstat;
    }

    public boolean isVmstat() {
        return vmstat;
    }
}
