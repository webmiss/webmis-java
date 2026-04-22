package vip.webmis.mvc.modules.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vip.webmis.mvc.core.ControllerBase;

/* 消息 */
@RestController
@Controller("AdminMsg")
@RequestMapping("/admin")
public class Msg extends ControllerBase {

  /* 列表 */
  @RequestMapping(value="msg/list", produces="application/json;charset=UTF-8")
  public Map<String, Object> List(@RequestParam Map<String, String> params) {
    // 数据
    Map<String, Object> data = new HashMap<String, Object>();
    int num = 0;
    List<HashMap<String, Object>> list = new ArrayList<>();
    data.put("num", num);
    data.put("list", list);
    // 返回
    Map<String, Object> res = new HashMap<String, Object>();
    res.put("code",0);
    res.put("data", data);
    return GetJSON(res);
  }
  
}
