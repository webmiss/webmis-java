package vip.webmis.mvc.modules.api;

import org.springframework.web.bind.annotation.RestController;

import vip.webmis.mvc.core.Base;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

/* API */
@RestController
@Controller("ApiIndex")
@RequestMapping("/api")
public class Index extends Base {
  
  /* 首页 */
  @RequestMapping("")
  public Map<String, Object> index(@RequestParam Map<String, Object> params, @RequestBody Map<String, Object> json) {
    // 返回
    Map<String,Object> res = new HashMap<String,Object>();
    res.put("code",0);
    res.put("msg","Java Api");
    res.put("data", params);
    res.put("json", json);
    return res;
  }

}
