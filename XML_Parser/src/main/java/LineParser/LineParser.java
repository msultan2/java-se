package LineParser;

import java.util.ArrayList;
import java.util.Hashtable;

public class LineParser {
    public LineParser() {
    }
public Hashtable parser_Hashtable(String line,String delimiter){
    Hashtable test = new Hashtable();
    int loc;
    int step=0;
    line=line+delimiter;
    while(line.contains(delimiter)){
        loc=line.indexOf(delimiter);
        test.put(step,line.substring(0,loc));
        line=line.substring(loc+1,line.length());
        step++;
    }
    return test;
    //test.put()
}
 public ArrayList parser_ArrayList(String line,String delimiter){
     ArrayList test = new ArrayList();
     int loc;
     line=line+delimiter;
     while(line.contains(delimiter)){
         loc=line.indexOf(delimiter);
         test.add(line.substring(0,loc));
         line=line.substring(loc+1,line.length());
     }
     return test;
 }
    public static void main(String[] args) {
        LineParser lineParser = new LineParser();
        lineParser.parser_Hashtable("Mohamed,Sultan,1,2,3,4,5",",");
    }
}
