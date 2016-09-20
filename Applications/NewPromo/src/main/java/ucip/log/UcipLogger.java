package ucip.log;

import org.apache.log4j.*;

public class UcipLogger {

    Class c;
    private Logger logger = null;

    static{
        PropertyConfigurator.configure("resources/log4j.properties");
    }

    public UcipLogger(Class c) {
        this.c = c;
        logger = LogManager.getLogger(c);
    }
    
    public UcipLogger(String name) {
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

