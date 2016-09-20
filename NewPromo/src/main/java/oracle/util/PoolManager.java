package oracle.util;

import java.io.File;
import promo.util.PromoLogger;
import snaq.db.ConnectionPoolManager;

public abstract class PoolManager {
    
   private static  ConnectionPoolManager cpm = null;
   private static  PromoLogger dblog = new PromoLogger(PoolManager.class);
   private static  File poolProperty  = new File("resources\\dbpool.properties");
       
   static{
        
       try{
            if ( !poolProperty.exists() ){
                  dblog.error("Failed to open pool Property File "+
                          poolProperty.getName() ,null);
                System.out.println("Failed to open pool Property File "+poolProperty.getName());
            }
            cpm = ConnectionPoolManager.getInstance(poolProperty);
        }catch (Exception e){
                dblog.error("Failed to open connection pool",e);
            System.out.println("Failed to open connection pool");
                e.printStackTrace();
        }
    }
    
    public static ConnectionPoolManager getInstance () {
        if (cpm != null && !cpm.isReleased()) {
            return cpm;
        }
        else {
            try {
                if ( !poolProperty.exists() ){
                    dblog.error("Failed to open pool Property File "+
                          poolProperty.getName() ,null);
                    System.out.println("Failed to open pool Property File "+poolProperty.getName());
                }
                cpm = ConnectionPoolManager.getInstance(poolProperty);
            }
            catch (Exception e) {
                dblog.error("Failed to open connection pool",e);
                System.out.println("Failed to open connection pool");
                e.printStackTrace();
            }
        }
        return cpm;
    }
    
    public static boolean releseAll(){
         if (cpm != null && !cpm.isReleased()) {
            cpm.release();
            return true;
        }
        return false;
    }
}
