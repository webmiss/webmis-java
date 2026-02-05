package vip.webmis.mvc.config;

import java.util.HashMap;
import java.util.Map;

/* 缓存数据库 */
public class Redis {

  /* 配置 */
  public Map<String, Object> Config() {
    return Config("default");
  }
  public Map<String, Object> Config(String name) {
    Map<String, Object> data = new HashMap<>();
    switch(name) {
      case "default":
        data.put("host", "127.0.0.1");                              // 主机
        data.put("port", 6379);                                     // 端口
        data.put("password", "e4b99adec618e653400966be536c45f8");   // 密码
        data.put("db", 0);                                          // 硬盘
        data.put("maxTotal", 30);                                   // 最大连接数
        data.put("minIdle", 10);                                    // 最小空闲连接数
        data.put("maxIdle", 15);                                    // 最大空闲连接数
        data.put("maxWait", 3000);                                  // 等待时间( 毫秒 )
        break;
      case "other":
        data.put("host", "127.0.0.1");                              // 主机
        data.put("port", 6379);                                     // 端口
        data.put("password", "e4b99adec618e653400966be536c45f8");   // 密码
        data.put("db", 0);                                          // 硬盘
        data.put("maxTotal", 30);                                   // 最大连接数
        data.put("minIdle", 10);                                    // 最小空闲连接数
        data.put("maxIdle", 15);                                    // 最大空闲连接数
        data.put("maxWait", 3000);                                  // 等待时间( 毫秒 )
        break;
    }
    return data;
  }
  
}
