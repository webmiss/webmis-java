package vip.webmis.mvc.core;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/* Web配置 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // 拦截器
    registry.addInterceptor(new HandlerInterceptor() {
      @Override
      public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // response.setContentType("application/json;charset=UTF-8");
        System.out.print("Json Interceptor: " + request.getRequestURI() + "\n");
        return true;
      }
    }).addPathPatterns("/api/**", "/admin/**");
  }
  
}
