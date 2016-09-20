package Date_Functions;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Locale;


public class DateUtils {

    public static String date(String dateFormat,int numberOfDays) {
      Calendar cal = Calendar.getInstance();
      SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
      cal.add(Calendar.DATE, -1 * numberOfDays);

      return sdf.format(cal.getTime());

    }
//    public static String DateFormatter(String DateIN, String OutputFormat) {
//
//        try {
//                    SimpleDateFormat format =
//                                new SimpleDateFormat("OutputFormat",Locale.US);
//                    Date parsed;
//                    parsed = (Date)format.parse(DateIN);
//                    return parsed.toString();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//    
  public static String now(String dateFormat) {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat,Locale.US);
//    cal.add(Calendar.HOUR, 2);
    return sdf.format(cal.getTime());

  }

  public static void  main(String arg[]) {
     System.out.println("Now : " + DateUtils.now("yyyy-MM-dd HH:mm:ss"));
     System.out.println(DateUtils.now("dd MMMMM yyyy"));
     System.out.println(DateUtils.now("yyyyMMdd"));
     System.out.println(DateUtils.now("dd.MM.yy"));
     System.out.println(DateUtils.now("MM/dd/yy"));
     System.out.println(DateUtils.now("yyyy.MM.dd G 'at' hh:mm:ss z"));
     System.out.println(DateUtils.now("EEE, MMM d, ''yy"));
     System.out.println(DateUtils.now("h:mm a"));
     System.out.println(DateUtils.now("H:mm:ss:SSS"));
     System.out.println(DateUtils.now("K:mm a,z"));
     System.out.println(DateUtils.now("yyyy.MMMMM.dd GGG hh:mm aaa"));
  }



}

