package vip.webmis.mvc.core;

import java.lang.reflect.Field;
import java.util.Map;

/* 控制器 */
public class ControllerBase extends Base {

  /* 获取语言 */
  public String GetLang(String lang, String action, Object... args) {
    lang = !lang.equals("")?lang.toLowerCase():"en_us";
    String className = "vip.webmis.mvc.config.langs."+lang;
    // 反射
    Class<?> clazz = null;
    try {
      clazz = Class.forName(className);
    } catch (ClassNotFoundException e) {
      try {
        clazz = Class.forName("vip.webmis.mvc.config.langs.en_us");
      } catch (ClassNotFoundException e1) {
      }
    }
    // 调用属性
    try {
      Field field = clazz.getField(action);
      try {
        // 获取值
        String msg = (String) field.get(null);
        if(args.length>0) msg = String.format(msg, args);
        // 返回结果 (成功
        return msg;
      } catch (IllegalArgumentException | IllegalAccessException e) {
        return "";
      }
    } catch (NoSuchFieldException e) {
      return "";
    }
  }

  /* 返回JSON */
  public Map<String, Object> GetJSON(Map<String, Object> data) {
    return GetJSON(data, "en_US");
  }
  public Map<String, Object> GetJSON(Map<String, Object> data, String lang) {
    // 语言
    if(data.get("code")!=null && data.get("msg")==null) {
      lang = lang.equals("")?"en_US":lang;
      String msg = GetLang(lang, "code_"+data.get("code"));
      data.put("msg", msg);
    }
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
