package vip.webmis.mvc.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/* 时间 */
public class Time {

  /* Time */
  public static Integer Time() {
    return (int) (System.currentTimeMillis()/1000);
  }

  /* Date: Y-m-d H:i:s */
  public static String Date(String format) {
    return Date(format, 0);
  }
  public static String Date(String format, Integer timestamp) {
    timestamp = timestamp>0?timestamp:Time();
    long time = (long) timestamp*1000;
    java.util.Date date = new java.util.Date(time);
    // 格式
    Map<String, String> replacer = new HashMap<>();
    replacer.put("Y", "yyyy");
    replacer.put("y", "yy");
    replacer.put("m", "MM");
    replacer.put("n", "M");
    replacer.put("d", "dd");
    replacer.put("j", "d");
    replacer.put("H", "HH");
    replacer.put("h", "hh");
    replacer.put("i", "mm");
    replacer.put("s", "ss");
    replacer.put("a", "a");
    StringBuilder sb = new StringBuilder();
    for (char c : format.toCharArray()) {
      String key = String.valueOf(c);
      sb.append(replacer.getOrDefault(key, key));
    }
    String javaFormat = sb.toString();
    SimpleDateFormat sdf = new SimpleDateFormat(javaFormat);
    return sdf.format(date);
  }

  /* StrToTime */
  public static Integer StrToTime(String datetime) {
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      LocalDateTime dt = LocalDateTime.parse(datetime, formatter);
      return (int) dt.atZone(ZoneId.systemDefault()).toEpochSecond();
    } catch (Exception e) {
      try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dt = LocalDateTime.parse(datetime + " 00:00:00", formatter);
        return (int) dt.atZone(ZoneId.systemDefault()).toEpochSecond();
      } catch (Exception ex) {
        return (int) System.currentTimeMillis()/1000;
      }
    }
  }
  
}
