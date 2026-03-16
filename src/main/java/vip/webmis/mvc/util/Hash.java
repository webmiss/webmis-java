package vip.webmis.mvc.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* 哈希 */
public class Hash {

  /* MD5 */
  public static String md5(String str) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] bytes = md.digest(str.getBytes()); // 加密
      StringBuilder sb = new StringBuilder();
      for (byte b : bytes) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
  
}
