package vip.webmis.mvc.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* MySQL 连接池 */
public class MySQLConnectionPool extends Base {

  private final String name = "Pool";           // 名称
  private String url;                           // 连接URL
  private String user;                          // 用户名
  private String password;                      // 密码

  private int initSize;                          // 初始连接数
  private int maxSize;                           // 最大连接数
  private BlockingQueue<Connection> idleConnections;    // 空闲连接队列

  /* 构造函数 */
  public MySQLConnectionPool(Map<String, Object> config, int initSize, int maxSize) {
    // 配置
    this.url = "jdbc:" + config.get("type") + "://" + config.get("host") + ":" + config.get("port") + "/" + config.get("database") + "?useUnicode=true&useOldAliasMetadataBehavior=true&characterEncoding=" + config.get("charset");
    this.user = (String)config.get("user");
    this.password = (String)config.get("password");
    // 初始化
    this.initSize = initSize;
    this.maxSize = maxSize;
    if(initSize > maxSize) {
      this.maxSize = initSize;
    }
    // 初始化阻塞队列
    this.idleConnections = new LinkedBlockingQueue<>(this.maxSize);
    // JDBC驱动
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      Print("[ " + this.name + " ] 加载MySQL驱动失败", e);
    }
    // 初始化连接数
    for (int i = 0; i < this.initSize; i++) {
      Connection conn = createConnection();
      if(conn != null) idleConnections.offer(conn);
    }
  }

  /* 创建连接 */
  private Connection createConnection() {
    Connection conn = null;
    try {
      return DriverManager.getConnection(this.url, this.user, this.password);
    } catch (Exception e) {
      Print("[ " + this.name + " ] 创建连接", e.getMessage());
    }
    return conn;
  }

  /* 获取连接 */
  public Connection getConnection(long timeout) {
    Connection conn = null;
    try {
      // 从队列取连接
      conn = idleConnections.poll(timeout, TimeUnit.MILLISECONDS);
      Print("1.获取连接", timeout, conn, GetIdleCount());
      if (conn == null) {
        if (idleConnections.size() + (maxSize - idleConnections.remainingCapacity()) < maxSize) {
          return createConnection();
        } else {
          Print("[ " + this.name + " ] 连接池已满，获取连接超时");
        }
      }
    } catch (InterruptedException e) {
      Print("[ " + this.name + " ] 获取连接", e.getMessage());
    }
    return conn;
  }

  /* 归还连接 */
  public boolean releaseConnection(Connection conn) {
    if (conn == null) return false;
    try {
      if (!conn.isClosed() && conn.isValid(3)) {
        Print("2.归还连接", conn, GetIdleCount());
        return idleConnections.offer(conn);
      }
    } catch (SQLException e) {
      Print("[ " + this.name + " ] 归还连接", e.getMessage());
    }
    return false;
  }

  /* 获取空闲连接数 */
  public int GetIdleCount() {
    return idleConnections.size();
  }

  /* 销毁连接池 */
  public void destroy() {
    for (Connection conn : idleConnections) {
      try {
        if (!conn.isClosed()) conn.close();
      } catch (SQLException e) {
        Print("[ " + this.name +  " ]", e.getMessage());
      }
    }
    idleConnections.clear();
  }
  
}
