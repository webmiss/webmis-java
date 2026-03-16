package vip.webmis.mvc.modules.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import vip.webmis.mvc.config.Env;
import vip.webmis.mvc.core.ControllerBase;
import vip.webmis.mvc.core.Redis;
import vip.webmis.mvc.librarys.Safety;
import vip.webmis.mvc.util.Hash;
import vip.webmis.mvc.util.Time;

/* 用户 */
@RestController
@Controller("AdminUser")
@RequestMapping("/admin")
public class User extends ControllerBase {

  /* 登录 */
  @RequestMapping(value="user/login", produces="application/json;charset=UTF-8")
  public Map<String, Object> Login(@RequestParam Map<String, String> params, @RequestBody Map<String, Object> json, HttpServletRequest request) {
    ControllerBase.lang = params.get("lang");
    Map<String,Object> res;
    // 参数
    String uname = (String) JsonName(json, "uname");
    String passwd = (String) JsonName(json, "passwd");
    String vcode = (String) JsonName(json, "vcode");
    String vcode_url = BaseUrl(request, "admin/user/vcode")+"/"+uname+"?"+Time.time();
    // 验证
    if(!Safety.IsRight("uname", uname) &&!Safety.IsRight("tel", uname) &&!Safety.IsRight("email", uname)) {
      res = new HashMap<String,Object>();
      res.put("code", 4000);
      res.put("msg", GetLang("login_uname"));
      return GetJSON(res);
    }
    if(!passwd.equals("") && !vcode.equals("")) {
      res = new HashMap<String,Object>();
      res.put("code", 4000);
      res.put("msg", GetLang("login_verify"));
      return GetJSON(res);
    }
    // 登录方式
    String where = "";
    vcode = vcode.trim().toLowerCase();
    if(!passwd.equals("")) {
      // 密码长度
      if(!Safety.IsRight("passwd", passwd)) {
        res = new HashMap<String,Object>();
        res.put("code", 4000);
        res.put("msg", GetLang("login_passwd"));
        return GetJSON(res);
      }
      // 验证码
      Redis redis = new Redis();
      String code = redis.Get(Env.admin_token_prefix+"_vcode_"+uname);
      if(!code.equals("")) {
        if(code.length()!=4) {
          res = new HashMap<String,Object>();
          res.put("code", 4001);
          res.put("msg", GetLang("login_vcode"));
          res.put("vcode_url", vcode_url);
          return GetJSON(res);
        } else if(code != vcode) {
          res = new HashMap<String,Object>();
          res.put("code", 4002);
          res.put("msg", GetLang("login_verify_vcode"));
          res.put("vcode_url", vcode_url);
          return GetJSON(res);
        }
      }
      where = "(a.uname='"+uname+"' OR a.tel='"+uname+"' OR a.email='"+uname+"') AND a.password='"+Hash.md5(passwd)+"'";
      Print("redis", code, where);
    } else {
      // 验证码
      Redis redis = new Redis();
      String code = redis.Get(Env.admin_token_prefix+"_vcode_"+uname);
      if(code.equals("") || code!=vcode) {
        res = new HashMap<String,Object>();
        res.put("code", 4000);
        res.put("msg", GetLang("login_verify_vcode"));
        return GetJSON(res);
      }
      // 条件
      where = "a.tel='"+uname+"'";
    }
    Print(uname, passwd, vcode, vcode_url, where);
    // 返回
    res = new HashMap<String,Object>();
    res.put("code", 0);
    res.put("data", "");
    return GetJSON(res);
  }
  
}
