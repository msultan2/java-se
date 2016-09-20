package ucip.util;


import java.io.FileInputStream;

import java.util.*;

import ucip.log.UcipLogger;


/*
 * This the class that load the properties file
 */
public class PropertyHandler {
    private PropertyHandler() {
    }
    private static Properties rb = null;
//    private static UcipLogger logger = 
//        new UcipLogger(PropertyHandler.class);

    /*
   * This blocks intializes the properties
   */
    static {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
//            logger.error("Ucip properties file could not be obtained!",e);
        }
    }

    private static void init() {
        try

        {

            //File file = new File(DMS_PROPS);
            //logger.debug(file.getAbsolutePath());
            rb = new Properties();
            rb.load(new FileInputStream("resources/promo.properties.xml"));//without.xml
            //dmsProperties.load(new FileInputStream(new File(DMS_PROPS)));    
            //dmsProperties.load(propertiesStream);    
        } catch (Exception e) {
//            logger.error("Properties file not found exception",e);
              e.printStackTrace();
        }
    }

    /*
   * This method takes the key and returns the value if it exists; otherwise, returns null
   */

    public static String getProperty(String key) {
        String result = null;
        if (rb == null) {
            //      logger.debug("Unable to retrieve properties file");
            return null;
        }
        try {
            result = rb.getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
//            logger.info("Failed to get Property: "+key);

        }
        return result;
    }

    public static void main(String[] args) {
        String test = PropertyHandler.getProperty("input.file.path");
//        logger.debug("Test result is: " + test);
    }

}

