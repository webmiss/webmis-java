package vip.webmis.mvc.modules.admin;

import org.springframework.web.bind.annotation.RestController;

import vip.webmis.mvc.core.ControllerBase;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

/* API */
@RestController
@Controller("AdminIndex")
@RequestMapping("/admin")
public class Index extends ControllerBase {
  
  /* 首页 */
  @RequestMapping(produces="application/json;charset=UTF-8")
  public Map<String, Object> index() {
    // 返回
    HashMap<String,Object> res = new HashMap<String,Object>();
    res.put("code",0);
    res.put("msg","Java Admin");
    return res;
  }

  /* 软件升级 */
  @RequestMapping(value="index/version", produces="application/json;charset=UTF-8")
  public Map<String, Object> version(@RequestParam Map<String, String> params, @RequestBody Map<String, Object> json) {
    Map<String,Object> res;
    // 参数
    String os = (String) JsonName(json, "os");
    String local = (String) JsonName(json, "version");
    // 验证
    os = os.toLowerCase();
    if(!os.equals("web")) {
      res = new HashMap<String,Object>();
      res.put("code", 4000);
      res.put("msg", "["+os+"]该操作系统不支持更新!");
      return GetJSON(res, params.get("lang"));
    }
    // 数据
    Integer size = 0;
    String version = "";
    String url = "";
    if(os.equals("web")) {
      version = "3.0.0";
      url = "https://admin.webmis.vip";
      size = 0;
    }
    // 返回
    Map<String,Object> data = new HashMap<String,Object>();
    data.put("os", os);
    data.put("version", version);
    data.put("local", local);
    data.put("size", size);
    data.put("url", url);
    res = new HashMap<String,Object>();
    res.put("code", 0);
    res.put("data", data);
    return GetJSON(res, params.get("lang"));
  }

  /* 法定假期 */
  @RequestMapping(value="index/holiday", produces="application/json;charset=UTF-8")
  public Map<String, Object> holiday(@RequestParam Map<String, String> params, @RequestBody Map<String, Object> json) {
    HashMap<String,Object> res;
    // 参数
    String date = (String) JsonName(json, "date");
    String url = "https://java.webmis.vip/upload/img/holiday/";
    // 假期
    Map<String,Object> tmp;
    Map<String,Object> holiday;
    holiday = new HashMap<String,Object>();
    tmp = new HashMap<String,Object>();
    tmp.put("holiday", true);
    tmp.put("name", "春节");
    tmp.put("img", url+"20260216(360x420).png");
    tmp.put("bg", url+"202602(360x50).png");
    holiday.put("2026-02-16", tmp);
    // 返回
    res = new HashMap<String,Object>();
    res.put("code", 0);
    res.put("data", holiday.containsKey(date)?holiday.get(date):"");
    return GetJSON(res, params.get("lang"));
  }

}
