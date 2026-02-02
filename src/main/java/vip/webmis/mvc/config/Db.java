package vip.webmis.mvc.config;

import java.util.HashMap;
import java.util.Map;

/* 数据库 */
public class Db {

  /* 配置 */
  public Map<String, Object> Config() {
    return Config("default");
  }
  public Map<String, Object> Config(String name) {
    Map<String, Object> data = new HashMap<>();
    switch(name) {
      case "default":
        data.put("type", "mysql");                                  // 类型
        data.put("host", "127.0.0.1");                              // 主机
        data.put("port", "3306");                                   // 端口
        data.put("user", "root");                                   // 用户名
        data.put("password", "123456");                             // 密码
        data.put("database", "webmis");                             // 数据库
        data.put("charset", "utf8");                                // 编码
        data.put("poolInitSize", 100);                              // 连接池初始数量
        data.put("poolMaxSize", 150);                               // 连接池最大数量
        data.put("poolMaxWait", 3000);                              // 获取连接等待时间( 毫秒 )
        break;
      case "other":
        data.put("type", "mysql");                                  // 类型
        data.put("host", "127.0.0.1");                              // 主机
        data.put("port", "3306");                                   // 端口
        data.put("user", "root");                                   // 用户名
        data.put("password", "e4b99adec618e653400966be536c45f8");   // 密码
        data.put("database", "webmis");                             // 数据库
        data.put("charset", "utf8");                                // 编码
        data.put("poolInitSize", 100);                              // 连接池初始数量
        data.put("poolMaxSize", 150);                               // 连接池最大数量
        data.put("poolMaxWait", 3000);                              // 获取连接等待时间( 毫秒 )
        break;
    }
    return data;
  }
  
}
