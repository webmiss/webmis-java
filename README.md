# webmis-java
采用Java + Maven + SpringBoot开发的轻量级HMVC基础框架，目录结构清晰，支持CLI方式访问资料方便执行定时脚本。包括HMVC模块化管理、自动路由、CLI命令行、Socket通信、redis缓存、Token机制等功能，提供支付宝、微信、文件上传、图像处理、二维码等常用类。

## 安装
```bash
# 下载
$ git clone https://github.com/webmiss/webmis-java.git
$ cd webmis-java

# Linux、MacOS
./bash install

# Windows 11
.\cmd install
```

## 运行
```bash
# Linux、MacOS
./bash serve

# Windows 11
.\cmd serve
```

## 项目结构
```plaintext
webmis-java/
├── public                                 // 根目录
├── src/main/java/vip/webmis/mvc
│    ├── config                           // 配置文件
│    ├── core
│    │    ├── Base.java                  // 基础类
│    │    ├── GlobalCorsConfig.java      // 跨域配置
│    │    ├── Model.java                 // 基础模型
│    │    ├── MySQLConnectionPool.java   // MySQL 连接池
│    │    ├── NotFoundException.java     // 404 错误响应
│    │    └── Redis.java                 // 缓存数据库( 连接池 )
│    └── librarys                         // 第三方类
│    └── models                           // 模型
│    └── modules                          // 模块
│    │    ├── admin                      // 后台
│    │    ├── api                        // 应用
│    │    └── home                       // 网站
│    ├── task                             // 任务类
│    └── views                            // 视图文件
├── src/main/resources
│    ├── static                           // 静态资源
│    ├── templates                        // 视图模板
│    └── application.properties           // 项目配置文件
├── bash                                   // Linux/MacOS 启动脚本
├── cmd.bat                                // Windows 启动脚本
├── pom.xml                                // Maven 配置文件
└── run.py                                 // Web启动文件
```
