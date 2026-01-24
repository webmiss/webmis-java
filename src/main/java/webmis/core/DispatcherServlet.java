package webmis.core;

// import com.mvc.annotation.Controller;
// import com.mvc.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import webmis.annotation.Controller;
import webmis.annotation.RequestMapping;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/*")
public class DispatcherServlet extends HttpServlet {

  private Map<String, ControllerMethod> urlMapping = new HashMap<>();

  /* 构建URL-方法映射关系 */
  @Override
  public void init() throws ServletException {
    super.init();
    // registerController(com.mvc.controller.UserController.class);
  }

  /* 注册控制器 */
  private void registerController(Class<?> controllerClass) {
    // 是否标记注解
    if (!controllerClass.isAnnotationPresent(Controller.class)) { return; }
    // 控制器前缀
    Controller controllerAnno = controllerClass.getAnnotation(Controller.class);
    String prefix = controllerAnno.value().trim();
    prefix = prefix.startsWith("/") ? prefix : "/" + prefix;
    // 遍历方法，解析@RequestMapping
    Method[] methods = controllerClass.getDeclaredMethods();
    for (Method method : methods) {
      if (!method.isAnnotationPresent(RequestMapping.class)) { continue; }
      // 获取方法后缀并格式化
      RequestMapping reqAnno = method.getAnnotation(RequestMapping.class);
      String suffix = reqAnno.value().trim();
      suffix = suffix.startsWith("/") ? suffix : "/" + suffix;
      // 拼接完整URL，存入映射表
      String fullUrl = prefix + suffix;
      urlMapping.put(fullUrl, new ControllerMethod(controllerClass, method));
    }
  }

  /* GET/POST请求 */
  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // 编码
    resp.setContentType("text/html;charset=UTF-8");
    PrintWriter out = resp.getWriter();
    try {
      // 请求URL
      String requestUri = req.getRequestURI().trim();
      String contextPath = req.getContextPath().trim();
      if (requestUri.startsWith(contextPath)) {
        requestUri = requestUri.substring(contextPath.length());
      }
      String test = "Test";
      Base.Print(test, requestUri);
      // 匹配URL
      ControllerMethod cm = urlMapping.get(requestUri);
      if (cm == null) {
        out.write("404 NOT FOUND → 请求URL：" + requestUri);
        return;
      }
      // 反射控制器方法
      Class<?> cClass = cm.getControllerClass();
      Object controller = cClass.getDeclaredConstructor().newInstance();
      Method targetMethod = cm.getMethod();
      targetMethod.invoke(controller);
      // 响应结果
      out.write("请求成功 ✔ → URL：" + requestUri + "<br/>");
      out.write("执行方法：" + cClass.getSimpleName() + "." + targetMethod.getName() + "()");
    } catch (Exception e) {
      out.write("500 SERVER ERROR → 原因：" + e.getMessage());
      e.printStackTrace();
    } finally {
      out.close();
    }
  }

  /* 封装控制器 */
  private static class ControllerMethod {
    private final Class<?> controllerClass;
    private final Method method;

    public ControllerMethod(Class<?> controllerClass, Method method) {
      this.controllerClass = controllerClass;
      this.method = method;
    }

    public Class<?> getControllerClass() {
      return controllerClass;
    }

    public Method getMethod() {
      return method;
    }

  }
  
}
