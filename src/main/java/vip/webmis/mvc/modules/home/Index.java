package vip.webmis.mvc.modules.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import vip.webmis.mvc.config.Env;
import vip.webmis.mvc.core.Base;

import org.springframework.ui.Model;

/* 网站 */
@Controller("Index")
@RequestMapping("/")
public class Index extends Base {

  /* 首页 */
  @RequestMapping("")
  String index(Model model) {
    model.addAttribute("title", Env.title);
    model.addAttribute("copy", Env.copy);  
    return "home/index";
  }
  
}
