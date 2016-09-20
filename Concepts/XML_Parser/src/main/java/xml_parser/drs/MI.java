package xml_parser.drs;

import java.util.ArrayList;

import xml_parser.TimeDate;


public class MI {
    private String mts = new String();
    private String gp = new String();
    private String headder = new String();
    private TimeDate timeDate = new TimeDate();
    private ArrayList<String> mt = new ArrayList<String>();
    private ArrayList<MV> mv = new ArrayList<MV>();

    public MI() {
    }

    public void setMts(String mts) {
        this.mts = mts;
    }

    public String getMts() {
        return mts;
    }

    public void setGp(String gp) {
        this.gp = gp;
    }

    public String getGp() {
        return gp;
    }

    public void setMt(ArrayList<String> mt) {
        this.mt = mt;
    }

    public ArrayList<String> getMt() {
        return mt;
    }

    public void setMv(ArrayList<MV> mv) {
        this.mv = mv;
    }

    public ArrayList<MV> getMv() {
        return mv;
    }

    public void setTimeDate(TimeDate timeDate) {
        this.timeDate = timeDate;
    }

    public TimeDate getTimeDate() {
        return timeDate;
    }

    public void setHeadder(String headder) {
        this.headder = headder;
    }

    public String getHeadder() {
        return headder;
    }
}
