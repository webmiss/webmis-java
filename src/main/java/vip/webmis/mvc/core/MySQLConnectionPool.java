package vip.webmis.mvc.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import vip.webmis.mvc.config.Db;

/* MySQL 连接池 */
public class MySQLConnectionPool extends Base {

  private static BlockingQueue<Connection> pool_default;    // 连接池: default
  private static BlockingQueue<Connection> pool_other;      // 连接池: other
  private static final String name = "MariaDB";             // 名称
  private static final Object LOCK = new Object();          // 锁
  private static String db = "default";                     // 数据库
  private static int initSize;                              // 初始连接数
  private static int maxSize;                               // 最大连接数
  private static int maxWait;                               // 最大等待时间( 毫秒 )
  private static String url;                                // 数据库URL
  private static String user;                               // 用户名
  private static String password;                           // 密码

  /* 数据源 */
  static void InitPool(String name) {
    db = name;
    // 配置
    Map<String, Object> config = new Db().Config(name);
    initSize = (Integer) config.get("poolInitSize");
    maxSize = (Integer) config.get("poolMaxSize");
    maxWait = (Integer) config.get("poolMaxWait");
    url = "jdbc:" + config.get("type") + "://" + config.get("host") + ":" + config.get("port") + "/" + config.get("database") + "?useUnicode=true&useOldAliasMetadataBehavior=true&characterEncoding=" + config.get("charset");
    user = (String)config.get("user");
    password = (String)config.get("password");
    // 初始化连接池
    if("default".equals(name) && pool_default != null) return;
    if("other".equals(name) && pool_other != null) return;
    synchronized (LOCK) {
      if("default".equals(name) && pool_default != null) return;
      if("other".equals(name) && pool_other != null) return;
      try{
        // 创建连接池
        if ("default".equals(name)) {
          pool_default = new LinkedBlockingQueue<>(maxSize);
        } else if("other".equals(name)) {
          pool_other = new LinkedBlockingQueue<>(maxSize);
        }
        // JDBC驱动
        Class.forName("org.mariadb.jdbc.Driver");
        // 初始化连接数
        for (int i = 0; i < initSize; i++) {
          Connection conn = CreateConnection();
          if(conn != null) {
            if("default".equals(name)) pool_default.offer(conn);
            else if("other".equals(name)) pool_other.offer(conn);
          }
        }
        Print("[ "+MySQLConnectionPool.name+" ] MariaDB Pool:", name, GetIdleCount());
      } catch (Exception e) {
        Print("[ "+MySQLConnectionPool.name+" ] MariaDB Pool:", e.getMessage());
      }
    }
  }

  /* 创建连接 */
  private static Connection CreateConnection() {
    Connection conn = null;
    try {
      return DriverManager.getConnection(url, user, password);
    } catch (Exception e) {
      Print("[ "+MySQLConnectionPool.name+" ] CreateConnection:", e.getMessage());
    }
    return conn;
  }

  /* 默认连接池 */
  static public BlockingQueue<Connection> GetIdleConnections() {
    // 连接池
    BlockingQueue<Connection> idleConnections = null;
    if("default".equals(db)) idleConnections = pool_default;
    else if("other".equals(db)) idleConnections = pool_other;
    return idleConnections;
  }

  /* 获取连接 */
  static public Connection GetConnection() {
    // 连接池
    BlockingQueue<Connection> idle = GetIdleConnections();
    if(idle == null) return null;
    // 连接
    Connection conn = null;
    try{
      // 从队列取连接
      conn = idle.poll(maxWait, TimeUnit.MILLISECONDS);
      if(conn!=null) {
        if(!conn.isClosed() && conn.isValid(2)) {
          return conn;
        } else {
          try { conn.close(); } catch (Exception ignored) {}
        }
      } 
      // 创建连接
      int totalUsed = maxSize - idle.remainingCapacity();
      if (totalUsed < maxSize) {
        Connection newConn = CreateConnection();
        return newConn;
      }
      Print("[ "+MySQLConnectionPool.name+" ] Connection pool is full, timeout while acquiring idle connection");
    } catch (InterruptedException | SQLException e) {
      Print("[ "+MySQLConnectionPool.name+" ] GetConnection:", e.getMessage());
    }
    return null;
  }

  /* 归还连接 */
  static public boolean ReleaseConnection(Connection conn) {
    if (conn == null) return false;
    // 连接池
    BlockingQueue<Connection> idle = GetIdleConnections();
    if(idle == null) return false;
    // 获取
    try {
      if(!conn.isClosed() && conn.isValid(2)) {
        return idle.offer(conn);
      } else {
        try { conn.close(); } catch (Exception ignored) {}
      }
    } catch (SQLException e) {
      Print("[ "+MySQLConnectionPool.name+" ] ReleaseConnection:", e.getMessage());
    }
    return true;
  }

  /* 获取空闲连接数 */
  static public int GetIdleCount() {
    // 连接池
    BlockingQueue<Connection> idle = GetIdleConnections();
    if(idle == null) return 0;
    // 空闲连接数
    return idle.size();
  }

  /* 销毁连接池 */
  static public void Destroy() {
    try {
      // 连接池: default
      for(Connection conn : pool_default) if(!conn.isClosed()) conn.close();
      if(pool_default!=null) pool_default.clear();
      // 连接池: other
      for(Connection conn : pool_other) if(!conn.isClosed()) conn.close();
      if(pool_other!=null) pool_other.clear();
    } catch (SQLException e) {
      Print("[ "+MySQLConnectionPool.name+" ] Destroy:", e.getMessage());
    } finally {
      pool_default = null;
      pool_other = null;
    }
  }
  
}
