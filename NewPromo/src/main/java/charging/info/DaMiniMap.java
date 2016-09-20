/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package charging.info;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author anassar
 */
public class DaMiniMap
        extends HashMap
        implements Map  {

    //private static final PromoLogger logger = new PromoLogger(DaMiniMap.class);

    public DaMiniMap(){
        //super(new HashMap<Integer, List<String>>());
        super(new HashMap<Integer, DedicatedAccountInformation>());
    }

    public void put(Integer daID, String adjAmnt, Date expDate){
        super.put(daID, new DedicatedAccountInformation(daID,adjAmnt,expDate));
    }

    public void put(Integer daID, String adjAmnt){
        super.put(daID, new DedicatedAccountInformation(daID,adjAmnt));
    }
}
