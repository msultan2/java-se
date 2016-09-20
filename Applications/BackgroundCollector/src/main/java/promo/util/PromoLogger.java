package promo.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class PromoLogger {

    Class c;
    private Logger logger = null;

    static{
        PropertyConfigurator.configure("log4j.properties");
    }

    public PromoLogger(Class c) {
        this.c = c;
        logger = LogManager.getLogger(c);
    }
    
    public PromoLogger(String name) {
        logger = LogManager.getLogger(name);
    }
    
    
    public void debug(Object obj) {
        logger.debug(obj);
    }
    public void info(Object obj) {
        logger.info(obj);
    }
    
    public void warning(Object obj) {
        logger.warn(obj);
    }
    
    public void error(Object obj, Exception e) {
        logger.error(obj,e);
        if ( e != null )
            e.printStackTrace();
    }
    
    public static void main(String[] args) {
        
    }
    


}

