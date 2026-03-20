package vip.webmis.mvc.util;

import java.time.Instant;
import java.time.ZoneId;

/* 时间 */
public class Time {

  /* Time */
  public static Integer Time() {
    return (int) (System.currentTimeMillis()/1000);
  }

  /* Date: yyyy-MM-dd HH:mm:ss */
  public static String Date(String format) {
    return Date(format, 0, "Asia/Shanghai");
  }
  public static String Date(String format, Integer timestamp) {
    return Date(format, timestamp, "Asia/Shanghai");
  }
  public static String Date(String format, Integer timestamp, String timezone) {
    timestamp = timestamp>0?timestamp:Time();
    long time = (long) timestamp*1000;
    return java.time.LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of(timezone)).format(java.time.format.DateTimeFormatter.ofPattern(format));
  }
  
}
