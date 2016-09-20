package Checker.drs;

public class Routers {
    boolean df=false;
    boolean vmstat=false;
    boolean tp_status=false;
    boolean ss7_link=false;
    boolean m3ua_link=false;
    boolean mtpLinkRxUtilisation=false;
    boolean mtpLinkTxUtilisation=false;
    boolean smsCntMoTimeoutCounter=false;
    boolean smsCntMtTimeoutCounter=false;
    boolean smsCntSriSmTimeoutCounter=false;
    public Routers() {
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

    public void setSs7_link(boolean ss7_link) {
        this.ss7_link = ss7_link;
    }

    public boolean isSs7_link() {
        return ss7_link;
    }

    public void setM3ua_link(boolean m3ua_link) {
        this.m3ua_link = m3ua_link;
    }

    public boolean isM3ua_link() {
        return m3ua_link;
    }

    public void setMtpLinkRxUtilisation(boolean mtpLinkRxUtilisation) {
        this.mtpLinkRxUtilisation = mtpLinkRxUtilisation;
    }

    public boolean isMtpLinkRxUtilisation() {
        return mtpLinkRxUtilisation;
    }

    public void setMtpLinkTxUtilisation(boolean mtpLinkTxUtilisation) {
        this.mtpLinkTxUtilisation = mtpLinkTxUtilisation;
    }

    public boolean isMtpLinkTxUtilisation() {
        return mtpLinkTxUtilisation;
    }

    public void setSmsCntMoTimeoutCounter(boolean smsCntMoTimeoutCounter) {
        this.smsCntMoTimeoutCounter = smsCntMoTimeoutCounter;
    }

    public boolean isSmsCntMoTimeoutCounter() {
        return smsCntMoTimeoutCounter;
    }

    public void setSmsCntMtTimeoutCounter(boolean smsCntMtTimeoutCounter) {
        this.smsCntMtTimeoutCounter = smsCntMtTimeoutCounter;
    }

    public boolean isSmsCntMtTimeoutCounter() {
        return smsCntMtTimeoutCounter;
    }

    public void setSmsCntSriSmTimeoutCounter(boolean smsCntSriSmTimeoutCounter) {
        this.smsCntSriSmTimeoutCounter = smsCntSriSmTimeoutCounter;
    }

    public boolean isSmsCntSriSmTimeoutCounter() {
        return smsCntSriSmTimeoutCounter;
    }
}
