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
import vip.webmis.mvc.service.Data;
import vip.webmis.mvc.service.TokenAdmin;
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
    TokenAdmin.SavePerm(String.valueOf(data.get("id")), perm);
    // 登录时间
    Integer ltime = Time.Time();
    Map<String,Object> d = new HashMap<String,Object>();
    d.put("ltime", ltime);
    m = new vip.webmis.mvc.models.User();
    m.Set(d);
    m.Where("id=?", data.get("id"));
    m.Update();
    // Token
    Map<String,Object> tData = new HashMap<String,Object>();
    tData.put("uid", data.get("id").toString());
    tData.put("uname", uname);
    tData.put("name", data.get("name"));
    tData.put("type", data.get("type"));
    tData.put("isPasswd", isPasswd);
    tData.put("brand", data.get("brand"));
    tData.put("shop", data.get("shop"));
    tData.put("partner", data.get("partner"));
    tData.put("partner_in", data.get("partner_in"));
    String token = TokenAdmin.Create(tData);
    // 用户信息
    Map<String,Object> uinfo = new HashMap<String,Object>();
    uinfo.put("uid", data.get("id").toString());
    uinfo.put("uname", uname);
    uinfo.put("tel", data.get("tel"));
    uinfo.put("email", data.get("email"));
    uinfo.put("ltime", Time.Date("yyyy-MM-dd HH:mm:ss", ltime));
    uinfo.put("type", data.get("type"));
    uinfo.put("nickname", data.get("nickname"));
    uinfo.put("department", data.get("department"));
    uinfo.put("position", data.get("position"));
    uinfo.put("name", data.get("name"));
    uinfo.put("gender", data.get("gender"));
    uinfo.put("birthday", data.get("birthday"));
    uinfo.put("img", Data.Img(data.get("img").toString()));
    uinfo.put("signature", data.get("signature"));
    // 返回
    data = new HashMap<String,Object>();
    data.put("token", token);
    data.put("uinfo", uinfo);
    data.put("isPasswd", isPasswd);
    res = new HashMap<String,Object>();
    res.put("code", 0);
    res.put("data", data);
    return GetJSON(res);
  }

  /* Token验证 */
  @RequestMapping(value="user/token", produces="application/json;charset=UTF-8")
  public Map<String, Object> Token(@RequestParam Map<String, String> params, @RequestBody Map<String, Object> json, HttpServletRequest request) {
    ControllerBase.lang = params.get("lang");
    Map<String,Object> res;
    // 参数
    String token = (String) JsonName(json, "token");
    Boolean is_uinfo = (Boolean) JsonName(json, "uinfo");
    // 验证
    String msg = TokenAdmin.Verify(token, "");
    if(msg!="") {
      res = new HashMap<String,Object>();
      res.put("code", 4001);
      return GetJSON(res);
    }
    Map<String,Object> tData = TokenAdmin.Token(token);
    // 用户信息
    Map<String,Object> uinfo = new HashMap<String,Object>();
    if(is_uinfo) {
      vip.webmis.mvc.models.User m = new vip.webmis.mvc.models.User();
      m.Table("user as a");
      m.LeftJoin("user_info as b", "a.id=b.uid");
      m.Columns(
        "FROM_UNIXTIME(a.ltime) as ltime", "a.tel", "a.email",
        "b.type", "b.nickname", "b.department", "b.position", "b.name", "b.gender", "b.img", "b.signature", "FROM_UNIXTIME(b.birthday, '%Y-%m-%d') as birthday"
      );
      m.Where("a.id=?", tData.get("uid"));
      uinfo = m.FindFirst();
      uinfo.put("uid", String.valueOf(tData.get("uid")));
      uinfo.put("uname", tData.get("uname"));
      uinfo.put("img", Data.Img(String.valueOf(uinfo.get("img"))));
    }
    // 返回
    Map<String,Object> data = new HashMap<String,Object>();
    data.put("token_time", tData.get("time"));
    data.put("uinfo", uinfo);
    data.put("isPasswd", tData.get("time"));
    res = new HashMap<String,Object>();
    res.put("code", 0);
    res.put("data", data);
    return GetJSON(res);
  }
  
}
