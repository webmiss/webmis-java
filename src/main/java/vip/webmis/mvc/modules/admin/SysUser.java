package vip.webmis.mvc.modules.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import vip.webmis.mvc.core.ControllerBase;
import vip.webmis.mvc.service.TokenAdmin;
import vip.webmis.mvc.util.Time;
import vip.webmis.mvc.util.Util;

/* 系统用户 */
@RestController
@Controller("AdminSysUser")
@RequestMapping("/admin")
public class SysUser extends ControllerBase {

  /* 统计 */
  @RequestMapping(value="sys_user/total", produces="application/json;charset=UTF-8")
  public Map<String, Object> Total(@RequestParam Map<String, String> params, @RequestBody Map<String, Object> json, HttpServletRequest request) {
    ControllerBase.lang = params.get("lang");
    Map<String,Object> res;
    // 参数
    String token = String.valueOf(JsonName(json, "token"));
    HashMap<String,Object> data = (HashMap<String,Object>) JsonName(json, "data");
    // 验证
    String msg = TokenAdmin.Verify(token, "");
    if(msg!="") {
      res = new HashMap<String,Object>();
      res.put("code", 4001);
      return GetJSON(res);
    }
    if(data==null || data.size()==0) {
      res = new HashMap<String,Object>();
      res.put("code", 4000);
      return GetJSON(res);
    }
    // 条件
    String where = getWhere(data);
    // 数据
    Map<String,Object> list = new HashMap<String,Object>();
    // 返回
    res = new HashMap<String,Object>();
    res.put("code",0);
    res.put("time", Time.Date("Y/m/d H:i:s"));
    res.put("data", list);
    return GetJSON(res);
  }

  /* 列表 */
  @RequestMapping(value="sys_user/list", produces="application/json;charset=UTF-8")
  public Map<String, Object> List(@RequestParam Map<String, String> params, @RequestBody Map<String, Object> json, HttpServletRequest request) {
    ControllerBase.lang = params.get("lang");
    Map<String,Object> res;
    // 参数
    String token = String.valueOf(JsonName(json, "token"));
    HashMap<String,Object> data = (HashMap<String,Object>) JsonName(json, "data");
    Integer page = (Integer) JsonName(json, "page");
    Integer limit = (Integer) JsonName(json, "limit");
    String order = String.valueOf(JsonName(json, "order"));
    // 验证
    String msg = TokenAdmin.Verify(token, request.getRequestURI());
    if(msg!="") {
      res = new HashMap<String,Object>();
      res.put("code", 4001);
      return GetJSON(res);
    }
    if(data==null || data.size()==0 || page==null || limit==null) {
      res = new HashMap<String,Object>();
      res.put("code", 4000);
      return GetJSON(res);
    }
    // 条件
    String where = getWhere(data);
    Print(where);
    // 数据
    Map<String,Object> list = new HashMap<String,Object>();
    // 返回
    res = new HashMap<String,Object>();
    res.put("code",0);
    res.put("time", Time.Date("Y/m/d H:i:s"));
    res.put("data", list);
    return GetJSON(res);
  }

  /* 搜索条件 */
  private String getWhere(HashMap<String,Object> d) {
    ArrayList<String> where = new ArrayList<String>();
    // 时间
    String stime = d.containsKey("stune")?String.valueOf(d.get("stime")):Time.Date("Y-m-d");
    Integer start = Time.StrToTime(stime+" 00:00:00");
    where.add("stime>="+String.valueOf(start));
    return Util.Implode(" AND ", where.toArray(new String[0]));
  }
  
}
