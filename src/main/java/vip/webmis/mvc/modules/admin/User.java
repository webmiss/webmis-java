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
import vip.webmis.mvc.models.SysRole;
import vip.webmis.mvc.util.Hash;
import vip.webmis.mvc.util.Time;
import vip.webmis.mvc.util.Util;

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
    // String vcode_url = "http://localhost:9000/admin/user/vcode/"+uname+"?"+Time.Time();
    String vcode_url = BaseUrl(request, "admin/user/vcode")+"/"+uname+"?"+Time.Time();
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
    vcode = Util.Lower(vcode.trim());
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
      where = "(a.uname='"+uname+"' OR a.tel='"+uname+"' OR a.email='"+uname+"') AND a.password='"+Hash.Md5(passwd)+"'";
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
    // 查询
    vip.webmis.mvc.models.User m = new vip.webmis.mvc.models.User();
    m.Table("user a");
    m.LeftJoin("user_info AS b", "a.id=b.uid");
    m.LeftJoin("sys_perm AS c", "a.id=c.uid");
    m.Columns(
      "a.id", "a.status", "a.password", "a.tel", "a.email",
      "b.type", "b.nickname", "b.department", "b.position", "b.name", "b.gender", "FROM_UNIXTIME(b.birthday, '%Y-%m-%d') as birthday", "b.img", "b.signature",
      "c.role", "c.perm", "c.brand", "c.shop", "c.partner", "c.partner_in"
    );
    m.Where(where);
    Map<String, Object> data = m.FindFirst();
    if(data.isEmpty()) {
      // 强制验证码(24小时)
      Redis redis = new Redis();
      redis.Set(Env.admin_token_prefix+"_vcode_"+uname, Time.Time().toString());
      redis.Expire(Env.admin_token_prefix+"_vcode_"+uname, 24*3600);
      // 返回
      res = new HashMap<String,Object>();
      res.put("code", 4000);
      res.put("msg", GetLang("login_verify"));
      res.put("vcode_url", vcode_url);
      return GetJSON(res);
    } else {
      // 清除验证码
      Redis redis = new Redis();
      redis.Del(Env.admin_token_prefix+"_vcode_"+uname);
    }
    // 是否禁用
    if(data.get("status").equals("0")) {
      res = new HashMap<String,Object>();
      res.put("code", 4000);
      res.put("msg", GetLang("login_verify_status"));
      return GetJSON(res);
    }
    // 默认密码
    Boolean isPasswd = data.get("password").equals(Hash.Md5(Env.password));
    // 权限
    String perm = (String) data.get("perm");
    if(perm.equals("")) {
      if(data.get("role").equals("")) {
        res = new HashMap<String,Object>();
        res.put("code", 4000);
        res.put("msg", GetLang("login_verify_perm"));
        return GetJSON(res);
      }
      // 角色权限
      SysRole m1 = new SysRole();
      m1.Columns("perm");
      m1.Where("id="+data.get("role"));
      Map<String, Object> d1 = m1.FindFirst();
      if(!d1.isEmpty()) perm = (String) d1.get("perm");
    }
    Print("perm", perm, isPasswd);
    // 返回
    res = new HashMap<String,Object>();
    res.put("code", 0);
    res.put("data", "");
    return GetJSON(res);
  }
  
}
