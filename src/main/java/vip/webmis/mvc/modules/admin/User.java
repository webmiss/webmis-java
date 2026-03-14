package vip.webmis.mvc.modules.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import vip.webmis.mvc.core.ControllerBase;
import vip.webmis.mvc.util.Time;

/* 用户 */
@RestController
@Controller("AdminUser")
@RequestMapping("/admin")
public class User extends ControllerBase {

  /* 登录 */
  @RequestMapping(value="user/login", produces="application/json;charset=UTF-8")
  public Map<String, Object> Login(@RequestParam Map<String, String> params, @RequestBody Map<String, Object> json, HttpServletRequest request) {
    Map<String,Object> res;
    // 参数
    String uname = (String) JsonName(json, "uname");
    String passwd = (String) JsonName(json, "passwd");
    String vcode = (String) JsonName(json, "vcode");
    String vcode_url = BaseUrl(request, "admin/user/vcode")+"/"+uname+"?"+Time.time();
    Print(uname, passwd, vcode, vcode_url);
    // 返回
    res = new HashMap<String,Object>();
    res.put("code", 0);
    res.put("data", "");
    return GetJSON(res, params.get("lang"));
  }
  
}
