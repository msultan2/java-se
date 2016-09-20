package Parameters_Conf;

import java.io.FileInputStream;

import java.util.Properties;


/*
 * This the class that load the properties file
 */
public class PropertyHandler {

    private static PromoLogger logger = new PromoLogger(PropertyHandler.class);
    public static enum PROPERTY_TYPE { NORMAL , XML};
    private static  PROPERTY_TYPE currentType = PROPERTY_TYPE.XML;

    private PropertyHandler() {
    }

    private static Properties rb = null;
    private static Properties smsRB = null;



    public static void setPropertyType(PROPERTY_TYPE ptt){
        PropertyHandler.currentType = ptt;

    }

    public static void initXmlProperties() {
        try

        {
            //File file = new File(DMS_PROPS);
            //logger.debug(file.getAbsolutePath());
            rb = new Properties();
            rb.loadFromXML(new FileInputStream("properties.xml"));
            //rb.load(new FileInputStream("D:/Work Material/MyJavaProjects/SMS-Jan/resources/promo.properties"));
            //dmsProperties.load(new FileInputStream(new File(DMS_PROPS)));    
            //dmsProperties.load(propertiesStream);    
        } catch (Exception e) {
             logger.error("XML Properties file not found !",e);
              e.printStackTrace();
        }
    }


    public static void initProperties() {
        try

        {
            //File file = new File(DMS_PROPS);
            //logger.debug(file.getAbsolutePath());
            rb = new Properties();
            //rb.loadFromXML(new FileInputStream("resources/promo.properties.xml"));
            rb.load(new FileInputStream("promo.properties"));
            //dmsProperties.load(new FileInputStream(new File(DMS_PROPS)));
            //dmsProperties.load(propertiesStream);
        } catch (Exception e) {
              logger.error("Properties file not found exception",e);
              e.printStackTrace();
        }
    }

    public static void initSMSTemplates() {
        try

        {

            smsRB = new Properties();
            smsRB.loadFromXML(new FileInputStream("SMS.Templates.xml"));

        } catch (Exception e) {
             logger.error("SMS Templates file not found !",e);
              e.printStackTrace();
        }
    }


    /*
   * This method takes the key and returns the value if it exists; otherwise, returns null
   */

    public static String getProperty(String key) {
        String result = null;
        if (rb == null) {
            logger.error("Unable to retrieve properties file" , null );
            return null;
        }
        try {
            result = rb.getProperty(key);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("Failed to get Property: "+key, e );

        }
        return result;
    }

     public static String getSMS(String key) {
        String result = null;
        if (smsRB == null) {
            logger.error("Unable to retrieve SMS properties file" , null );
            return null;
        }
        try {
            result = new String(smsRB.getProperty(key).getBytes(),"cp1252");
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("Failed to get SMS: "+key, e );
        }
        return result;
    }


    public static void main(String[] args) {
        String test = PropertyHandler.getProperty("input.file.path");
//        logger.debug("Test result is: " + test);
    }

}

