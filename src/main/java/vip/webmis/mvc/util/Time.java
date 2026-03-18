package vip.webmis.mvc.util;

/* 时间 */
public class Time {

  /* Time */
  public static Integer Time() {
    return (int) (System.currentTimeMillis()/1000);
  }

  /* Date: yyyy-MM-dd HH:mm:ss */
  public static String Date(String format) {
    return Date(format, 0);
  }
  public static String Date(String format, Integer timestamp) {
    timestamp = timestamp>0?timestamp:Time();
    timestamp = timestamp*1000;
    return java.time.LocalDateTime.ofEpochSecond(timestamp, 0, java.time.ZoneOffset.UTC).format(java.time.format.DateTimeFormatter.ofPattern(format));
  }
  
}
