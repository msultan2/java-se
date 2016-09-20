package Checker.drs;

public class CM {
    boolean poll=false;
    boolean vmstat=false;
    public CM() {
    }

    public void setPoll(boolean poll) {
        this.poll = poll;
    }

    public boolean isPoll() {
        return poll;
    }

    public void setVmstat(boolean vmstat) {
        this.vmstat = vmstat;
    }

    public boolean isVmstat() {
        return vmstat;
    }
}
