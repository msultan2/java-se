/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package charging.info;


import charging.info.DedicatedAccountInformation;
import promo.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import ucip.AdjustmentRequest;

/**
 *
 * @author anassar
 */
public class ChargingModelsMap
        extends HashMap
        implements Map  {


    //public static final Integer DA_INDEX = 0;
    public static final Integer MIN_FIELDS = 4;
    //public static final Integer VALIDITY_INDEX = 1;
    private static final PromoLogger logger = new PromoLogger(ChargingModelsMap.class);

    //This is the Key of the default SvcClass behaviour
    private final Integer DEFAULT_ENRTY = -1;
    private  ChargingLayoutParser dpe;

    public ChargingModelsMap(){
        //super(new HashMap<Integer, List<String>>());
        super(new HashMap<Integer, AdjustmentRequest>());
    }


    @Override
    public Object get(Object key){
        Object obj = super.get(key);
        return (obj == null ? super.get(DEFAULT_ENRTY) : obj) ;
    }

    /*public Integer getValidityMonths(Object key, int daID){
        //Last element of the array is validity
        ArrayList tmparr = (ArrayList)super.get(key);
        tmparr.l
        return Integer.valueOf((String)tmparr.get(tmparr.size()));
    }*/

    /*public Integer getDA(Object key){
         return Integer.valueOf((String)((ArrayList)super.get(key)).get(DA_INDEX));
    }*/

    public boolean containsDefault(){
        return super.get(DEFAULT_ENRTY) == null ? false : true;
    }

    public  boolean initChargingModels(String chngFile){
        dpe = new ChargingLayoutParser(chngFile);
        if(dpe.initChargingModel(this))
            return true;
        return false;
    }

     public  boolean initChargingModels(){
        dpe = new ChargingLayoutParser();
        if(dpe.initChargingModel(this))
            return true;
        return false;
    }

   /* public boolean initDedicatedAccounts(String daFile){

      BufferedReader clsBr = null;

      File dedicatedAccntsFile = new File(daFile);

      if (!dedicatedAccntsFile.exists()) {
            return false;
      }

      try {
            clsBr = new BufferedReader(new FileReader(dedicatedAccntsFile));
     } catch (FileNotFoundException ex) {
            logger.error("Init Dedicated Accounts used failed !!", ex);
            //ex.printStackTrace();
            return false;
     }

     String line = null;
     String lineContents[];
     Integer svcClassIndex = 0; //first element of the line is SvcClass
     AdjustmentRequest adj;

     do{
           try {
                    line = clsBr.readLine();
            } catch (IOException ex) {
                    Logger.getLogger(ChargingModelsMap.class.getName()).log(Level.SEVERE, null, ex);
            }

           if ((line == null || line.startsWith(PromoParameters.COMMENT_MARK) || (line.trim().equals("")))) continue;
                lineContents = line.trim().split(PromoParameters.iDELIM);
                if (lineContents.length < ChargingModelsMap.MIN_FIELDS){
                       logger.error("Incomplete Record in configuration file: "+
                               line, null);
                       return false;
                }
                super.put(new Integer(lineContents[svcClassIndex]),adj = new AdjustmentRequest());

            try{

               ArrayList<DedicatedAccountInformation> tmparr = new ArrayList<DedicatedAccountInformation>();

               for (int i=1; i<lineContents.length ; i++){
                    //Skip service class element
                    if(lineContents[i].equalsIgnoreCase("M")){
                        adj.setAdjustmentAmount(lineContents[++i]);
                        i++; //Skip MAIN validity months
                    }
                    else{
                       int validMonths =  Integer.parseInt(lineContents[i+2]);
                       if(validMonths != -1){
                           GregorianCalendar calendar = new GregorianCalendar();
                           calendar.add(Calendar.DAY_OF_MONTH,30*validMonths);
                           
                           tmparr.add(
                                new DedicatedAccountInformation(Integer.parseInt(lineContents[i]),lineContents[++i]
                                ,calendar.getTime()));
                       }else
                           tmparr.add(
                                new DedicatedAccountInformation(Integer.parseInt(lineContents[i]),lineContents[++i]));
       
                       i++; //count for validity months
                    }
               }
              adj.setDedicatedAccountsInformation(tmparr);

            }catch(Exception ex){
               logger.error("Incomplete Record in configuration file: "+
                               line, ex);
               return false;
           }

   }while (line != null);

   if (!this.containsDefault()) {
            logger.error("Default Adjustment Behaviour not found !", null);
            logger.error("Please indicate it using dummy SvcClass -1 ", null);
            return false;
   }

   return true;
}*/



     public static void main(String args[]){

         ChargingModelsMap dd = new ChargingModelsMap();//<String ,List <String>>();
         //dd.initDedicatedAccounts(PromoParameters.dedicatedAccounts);

         /*List<String> l;
         dd.put(new Integer(1), l = new ArrayList<String>() );
         l.add("123");
         l.add("456");

         dd.put(new Integer(-1), l = new ArrayList<String>() );
         l.add("-1");
         l.add("-1");*/

         /*System.out.println(dd.values().toString());
         System.out.println(dd.get(new Integer(1)));
         System.out.println(dd.get(new Integer(2)));*/

         
     }

}
