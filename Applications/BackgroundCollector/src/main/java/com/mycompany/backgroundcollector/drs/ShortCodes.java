package drs;

public class ShortCodes {
    String service=new String();
        String Calls=new String();
        String UnSucceeded=new String();
        String Answered=new String();

    public ShortCodes() {
    
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getService() {
        return service;
    }

    public void setCalls(String calls) {
        this.Calls = calls;
    }

    public String getCalls() {
        return Calls;
    }

    public void setUnSucceeded(String unSucceeded) {
        this.UnSucceeded = unSucceeded;
    }

    public String getUnSucceeded() {
        return UnSucceeded;
    }

    public void setAnswered(String answered) {
        this.Answered = answered;
    }

    public String getAnswered() {
        return Answered;
    }
}
