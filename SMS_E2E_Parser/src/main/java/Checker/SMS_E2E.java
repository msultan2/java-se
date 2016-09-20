package Checker;

import DBInterfaces.RS_DB_Interface;

import Date_Functions.DateUtils;

import java.sql.SQLException;

import oracle.util.OracleDao;

import promo.util.PromoLogger;

public class SMS_E2E {

    private OracleDao db1;// = new OracleDao("AIWA2");
    private static PromoLogger logger  = new PromoLogger(SMS_E2E.class);
    public SMS_E2E() {
    }
public boolean checkAll(String dateYYYYMMDD,Integer time) throws SQLException{
    boolean status = false;
    RS_DB_Interface rs_DB_Interface = new RS_DB_Interface();
    status = rs_DB_Interface.isDbstate();
    logger.debug("status = "+status);
    

    boolean rs=false;
    
    try {
        rs=rs_DB_Interface.tp_statuskpi(dateYYYYMMDD,time);
    } catch (SQLException e) {
         System.out.println(e.getMessage());
        logger.debug("Exception = "+e.getMessage());
    }
    
    
    try {
        rs=rs_DB_Interface.router_ss7_link_kpi(dateYYYYMMDD,time);
    } catch (SQLException e) {
         System.out.println(e.getMessage());
        logger.debug("Exception = "+e.getMessage());
    }
    
    try {
        rs=rs_DB_Interface.router_m3ua_link_kpi(dateYYYYMMDD,time);
    } catch (SQLException e) {
         System.out.println(e.getMessage());
        logger.debug("Exception = "+e.getMessage());
    }
    
    try {
        rs=rs_DB_Interface.eci_diamtransstate_kpi(dateYYYYMMDD,time);
    } catch (SQLException e) {
         System.out.println(e.getMessage());
         logger.debug("Exception = "+e.getMessage());
    }
    
    try {
        rs=rs_DB_Interface.eci_diamtransadminstate_kpi(dateYYYYMMDD,time);
    } catch (SQLException e) {
         System.out.println(e.getMessage());
        logger.debug("Exception = "+e.getMessage());
    }
    
    try {
        rs=rs_DB_Interface.cm_polls(dateYYYYMMDD,time);
    } catch (SQLException e) {
         System.out.println(e.getMessage());
        logger.debug("Exception = "+e.getMessage());
    }
    
    try {
        rs=rs_DB_Interface.ams_amscnttotalstored(dateYYYYMMDD,time);
    } catch (SQLException e) {
         System.out.println(e.getMessage());
        logger.debug("Exception = "+e.getMessage());
    }
    
    try {
        rs=rs_DB_Interface.ams_queuesizeexceeded(dateYYYYMMDD,time);
    } catch (SQLException e) {
         System.out.println(e.getMessage());
        logger.debug("Exception = "+e.getMessage());
    }
    
    try {
        rs=rs_DB_Interface.router_mtplinkrxutilisation(dateYYYYMMDD,time);
    } catch (SQLException e) {
         System.out.println(e.getMessage());
        logger.debug("Exception = "+e.getMessage());
    }
    
    try {
        rs=rs_DB_Interface.router_mtplinktxutilisation(dateYYYYMMDD,time);
    } catch (SQLException e) {
         System.out.println(e.getMessage());
        logger.debug("Exception = "+e.getMessage());
    }

    try {
        rs=rs_DB_Interface.router_smscntmotimeoutcounter(dateYYYYMMDD,time);
    } catch (SQLException e) {
         System.out.println(e.getMessage());
        logger.debug("Exception = "+e.getMessage());
    }

    try {
        rs=rs_DB_Interface.router_smscntmttimeoutcounter(dateYYYYMMDD,time);
    } catch (SQLException e) {
         System.out.println(e.getMessage());
        logger.debug("Exception = "+e.getMessage());
    }
    try {
        rs=rs_DB_Interface.smsCntSriSmTimeoutCounter(dateYYYYMMDD,time);
    } catch (SQLException e) {
        System.out.println(e.getMessage());
        logger.debug("Exception = "+e.getMessage());
    }
    try {
        rs = rs_DB_Interface.System_Checker(dateYYYYMMDD,time);
    } catch (SQLException e) {
        System.out.println("Exception = "+e.getMessage());
         logger.debug("Exception = "+e.getMessage());
    }
        return rs;
    }

    public static void main(String[] args) {
        SMS_E2E sMS_E2E = new SMS_E2E();
        DateUtils dateUtils = new DateUtils();
        int numberOfDays = 1;
        int time = 15;
        String requiredDate = dateUtils.date("yyyyMMdd", numberOfDays);
        boolean rs = false;
        try {
            rs = sMS_E2E.checkAll(requiredDate, time);
        } catch (SQLException e) {
            // TODO
        }
    }
}
