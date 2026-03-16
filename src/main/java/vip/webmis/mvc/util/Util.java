package vip.webmis.mvc.util;

/* 常用工具 */
public class Util {

  /* Trim */
  public static String Trim(String str) {
    return str.trim();
  }

  /* Ltrim */
  public static String Ltrim(String str) {
    return str.replaceFirst("^\\s*", "");
  }

  /* Rtrim */
  public static String Rtrim(String str) {
    return str.replaceFirst("\\s*$", "");
  }

  /* Lower */
  public static String Lower(String str) {
    return str.toLowerCase();
  }

  /* Upper */
  public static String Upper(String str) {
    return str.toUpperCase();
  }
  
}
