package vip.webmis.mvc.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/* 常用工具 */
public class Util {

  /* Trim */
  public static String Trim(String str) {
    return str.trim();
  }
  public static String Trim(String str, String cutset) {
    if (str == null || str.isEmpty()) return "";
    return str.replaceAll("^[" + cutset + "]+|[" + cutset + "]+$", "");
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

  /* Explode */
  public static String[] Explode(String sep, String str) {
    return str.split(Pattern.quote(sep));
  }

  /* Implode */
  public static String Implode(String sep, String[] arr) {
    return String.join(sep, arr);
  }

  /* JsonEncode */
  public static String JsonEncode(Map<String, Object> data) {
    ObjectMapper objectMapper = new ObjectMapper();
    String json;
    try {
      json = objectMapper.writeValueAsString(data);
    } catch (JsonProcessingException e) {
      return null;
    }
    return json;
  }
  /* JsonEncodeArr */
  public static String JsonEncodeArr(List<Map<String, Object>> data) {
    ObjectMapper objectMapper = new ObjectMapper();
    String json;
    try {
      json = objectMapper.writeValueAsString(data);
    } catch (JsonProcessingException e) {
      return null;
    }
    return json;
  }

  /* JsonDecode */
  public static Map<String, Object> JsonDecode(String jsonStr) {
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> map;
    try {
      map = objectMapper.readValue(jsonStr, Map.class);
    } catch (JsonProcessingException e) {
      return null;
    }
    return map;
  }
  /* JsonDecodeArr */
  public static List<Map<String, Object>> JsonDecodeArr(String jsonStr) {
    ObjectMapper objectMapper = new ObjectMapper();
    List<Map<String, Object>> list;
    try {
      list = objectMapper.readValue(jsonStr, List.class);
    } catch (JsonProcessingException e) {
      return null;
    }
    return list;
  }

  /* ArrayMerge */
  public static HashMap<String, Object> ArrayMerge(HashMap<String, Object>... arrays) {
    HashMap<String, Object> res = new HashMap<String, Object>();
    for(HashMap<String, Object> arr:arrays){
      for(Entry<String, Object> entry:arr.entrySet()){
        res.put(entry.getKey(), entry.getValue());
      }
    }
    return res;
  }
  
}
