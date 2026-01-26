package vip.webmis.mvc.modules.admin;

import org.springframework.web.bind.annotation.RestController;

import vip.webmis.mvc.core.Base;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/* API */
@RestController
@Controller("AdminIndex")
@RequestMapping("/admin")
public class Index extends Base {
  
  /* 首页 */
  @RequestMapping(name="", produces="application/json;charset=UTF-8")
  public Map<String, Object> index() {
    // 返回
    HashMap<String,Object> res = new HashMap<String,Object>();
    res.put("code",0);
    res.put("msg","Java Admin");
    return res;
  }

}
