package vip.webmis.mvc.core;

import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotFoundException implements ErrorController {

  @RequestMapping("/error")
  public Object error(HttpServletRequest request, HttpServletResponse response){
    response.setStatus(200);
    HashMap<String, Object> map = new HashMap<>();
    map.put("code", 404);
    map.put("msg", "Not Found");
    return map;
  }
  
}
