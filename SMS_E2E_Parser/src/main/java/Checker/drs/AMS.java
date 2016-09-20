package Checker.drs;

public class AMS {
    boolean df=false;
    boolean tp_status=false;
    boolean queueCntRejectedMaxQueueSizeExceeded=false;
    boolean amsCntTotalStored=false;
    public AMS() {
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

    public void setQueueCntRejectedMaxQueueSizeExceeded(boolean queueCntRejectedMaxQueueSizeExceeded) {
        this.queueCntRejectedMaxQueueSizeExceeded = queueCntRejectedMaxQueueSizeExceeded;
    }

    public boolean isQueueCntRejectedMaxQueueSizeExceeded() {
        return queueCntRejectedMaxQueueSizeExceeded;
    }

    public void setAmsCntTotalStored(boolean amsCntTotalStored) {
        this.amsCntTotalStored = amsCntTotalStored;
    }

    public boolean isAmsCntTotalStored() {
        return amsCntTotalStored;
    }
}
