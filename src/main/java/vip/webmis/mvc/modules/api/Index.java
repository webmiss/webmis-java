package vip.webmis.mvc.modules.api;

import org.springframework.web.bind.annotation.RestController;

import vip.webmis.mvc.core.Base;
import vip.webmis.mvc.core.Redis;
import vip.webmis.mvc.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/* API */
@RestController
@Controller("ApiIndex")
@RequestMapping("/api")
public class Index extends Base {
  
  /* 首页 */
  @RequestMapping(name="", produces="application/json;charset=UTF-8")
  public Map<String, Object> index() {
    // 查询
    User m = new User();
    m.Columns("id", "uname");
    List<HashMap<String, Object>> data = m.Find();
    // Redis
    Redis r = new Redis();
    r.Set("test", "Java Redis");
    Print("Data", data, r.Get("test"));
    r.Close();
    // 返回
    HashMap<String,Object> res = new HashMap<String,Object>();
    res.put("code",0);
    res.put("msg","Java Api");
    return res;
  }

}
