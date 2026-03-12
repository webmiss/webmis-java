package vip.webmis.mvc.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/* 全局跨域配置 */
@Configuration
public class GlobalCorsConfig {

  @Bean
    public CorsFilter corsFilter() {
      CorsConfiguration config = new CorsConfiguration();
      // 允许跨域请求
      // config.addAllowedOrigin("https://www.fe.com");
      config.addAllowedOriginPattern("*");      // 域名
      config.setAllowCredentials(true);      // cookie
      config.addAllowedMethod("*");                    // 方法
      config.addAllowedHeader("*");
      config.setMaxAge(2592000L);                      // OPTIONS(缓存30天)
      // 映射路径
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/api/**", config);
      source.registerCorsConfiguration("/admin/**", config);
      return new CorsFilter(source);
    }
  
}
