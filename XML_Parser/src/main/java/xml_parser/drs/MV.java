package xml_parser.drs;

import java.util.ArrayList;

public class MV {
    private String moid=new String();
    private String Counter=new String();
    private ArrayList r =new ArrayList();
    private String sf;
    
    public MV() {
    }

    public void setMoid(String moid) {
        this.moid = moid;
    }

    public String getMoid() {
        return moid;
    }

    public void setR(ArrayList r) {
        this.r = r;
    }

    public ArrayList getR() {
        return r;
    }

    public void setSf(String sf) {
        this.sf = sf;
    }

    public String getSf() {
        return sf;
    }

    public void setCounter(String counter) {
        this.Counter = counter;
    }

    public String getCounter() {
        return Counter;
    }
}
