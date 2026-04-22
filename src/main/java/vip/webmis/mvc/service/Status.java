package vip.webmis.mvc.service;

import java.util.HashMap;
import java.util.Map;

/* 状态 */
public class Status {

  /* 公共 */
  static public Map<String, Object> Public(String name) {
    Map<String, Object> data = new HashMap<>();
    switch(name) {
      case "role_name":
        data.put("0", "用户");
        data.put("1", "开发");
        break;
      case "status_name":
        data.put("0", "禁用");
        data.put("1", "正常");
        break;
    }
    return data;
  }
  
}
