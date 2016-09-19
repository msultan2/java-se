/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package promo.util;

//import interpromo.VoucherEnum;
/**
 *
 * @author anassar
 */
public class Parameters {

    public static final String DBType;
    public static final String serverIP;
    public static final String port;
    public static final String dbName;
    public static final String userName;
    public static final String password;

    public static final String LOG_BASE_DIR;
    public static final String iDELIM;
    public static final String oDELIM;

    public static final String COMMENT_MARK;
//    public static enum OptInChannels { USSD, SMS, IVR };

    private static PromoLogger logger = new PromoLogger(Parameters.class);

    static {

        //Either fixed value promo or relative percentage
        PropertyHandler.initXmlProperties();
        //Fixed values or percentage value

        DBType = PropertyHandler.getProperty("DBType");
        serverIP = PropertyHandler.getProperty("serverIP");
        port = PropertyHandler.getProperty("port");
        dbName = PropertyHandler.getProperty("dbName");
        userName = PropertyHandler.getProperty("userName");
        password = PropertyHandler.getProperty("password");

        iDELIM = PropertyHandler.getProperty("log.iDELIM");
        oDELIM = PropertyHandler.getProperty("log.oDELIM");
        LOG_BASE_DIR = PropertyHandler.getProperty("log.path");
        COMMENT_MARK = "#";

    }

    public static int promo_DA_ID(int sc) {

        String serviceClass = PropertyHandler.getProperty("promo.DedicatedAccountID.SC_" + String.valueOf(sc));
        if (serviceClass == null) {
            serviceClass = PropertyHandler.getProperty("promo.DedicatedAccountID.Default");
        }
        return new Integer(serviceClass);
    }

    /*    public boolean validateParemeters(){

         if  (! (Parameters.svcClassAction.equals(Parameters.SvcClassAction.INCLUDE) || Parameters.svcClassAction.equals(Parameters.SvcClassAction.EXCLUDE) )){

            System.err.println("Unknow Service Class Action : '" + Parameters.svcClassAction + "' !");
            logger.error("Unknow Service Class Action : '" + Parameters.svcClassAction + "' !", null);
            return false;
        }

         if  (! (Parameters.voucherGroupAction.equals(Parameters.VoucherGroupAction.INCLUDE) || Parameters.voucherGroupAction.equals(Parameters.VoucherGroupAction.EXCLUDE) )){

            System.err.println("Unknow Voucher Group Action : '" + Parameters.voucherGroupAction + "' !");
            logger.error("Unknow Voucher Group Action : '" + Parameters.voucherGroupAction + "' !", null);
            return false;
        }

         if  (! (Parameters.PROMO_ADJSUTED_VALUE_TYPE.equalsIgnoreCase(PROMO_VALUE_PERCENTAGE) || Parameters.PROMO_ADJSUTED_VALUE_TYPE.equalsIgnoreCase(PROMO_VALUE_ABSOLUTE) )){

            System.err.println("Unknow Promo Value Type : '" + Parameters.PROMO_ADJSUTED_VALUE_TYPE + "' !");
            logger.error("Unknow Promo Value Type :  '" + Parameters.PROMO_ADJSUTED_VALUE_TYPE + "' !", null);
            return false;
         }

         return true;
    }

     */
    public Parameters() {

    }

}
