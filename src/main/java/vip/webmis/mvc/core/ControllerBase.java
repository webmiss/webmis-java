package vip.webmis.mvc.core;

import java.util.Map;

/* 控制器 */
public class ControllerBase extends Base {

  /* 返回JSON */
  public Map<String, Object> GetJSON(Map<String, Object> data) {
    return GetJSON(data, "en_US");
  }
  public Map<String, Object> GetJSON(Map<String, Object> data, String lang) {
    lang = lang.toLowerCase();
    // Print(lang);
    return data;
  }

  /* Get参数 */
  public String Get(Map<String, String> params, String name) {
    return params.get(name);
  }

  /* Post参数 */
  public String Post(Map<String, String> params, String name) {
    return params.get(name);
  }

  /* Json参数 */
  public Object JsonName(Map<String, Object> param, String name) {
    return param.get(name);
  }
  
}
