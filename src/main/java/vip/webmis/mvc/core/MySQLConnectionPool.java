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

  private static final String name = "Pool";                // 名称
  private static final Object LOCK = new Object();          // 锁
  private static BlockingQueue<Connection> pool_default;    // 连接池: default
  private static BlockingQueue<Connection> pool_other;      // 连接池: other
  private static String db = "default";                     // 数据库
  private static int initSize;                              // 初始连接数
  private static int maxSize;                               // 最大连接数
  private static int maxWait;                               // 最大等待时间( 毫秒 )
  private static String url;                                // 数据库URL
  private static String user;                               // 用户名
  private static String password;                           // 密码

  /* 数据源 */
  static void initPool(String name) {
    // 配置
    db = name;
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
          Connection conn = createConnection(url, user, password);
          if(conn != null) {
            if("default".equals(name)) pool_default.offer(conn);
            else if("other".equals(name)) pool_other.offer(conn);
          }
        }
        Print("初始化连接池", name, initSize, maxSize);
      } catch (Exception e) {
        Print("[ "+name+" ]", e.getMessage());
      }
    }
  }

  /* 创建连接 */
  private static Connection createConnection(String url, String user, String password) {
    Connection conn = null;
    try {
      return DriverManager.getConnection(url, user, password);
    } catch (Exception e) {
      Print("[ " + name + " ] 创建连接", e.getMessage());
    }
    return conn;
  }

  /* 默认连接池 */
  static public BlockingQueue<Connection> getIdleConnections() {
    // 连接池
    BlockingQueue<Connection> idleConnections = null;
    if("default".equals(db)) idleConnections = pool_default;
    else if("other".equals(db)) idleConnections = pool_other;
    return idleConnections;
  }

  /* 获取连接 */
  static public Connection getConnection() {
    // 连接池
    BlockingQueue<Connection> idleConnections = getIdleConnections();
    if(idleConnections == null) return null;
    // 获取
    Connection conn = null;
    try{
      // 从队列取连接
      Print("1.获取连接", maxWait, GetIdleCount());
      conn = idleConnections.poll(maxWait, TimeUnit.MILLISECONDS);
      Print(conn, !conn.isClosed(), conn.isValid(2));
      if(conn!=null) {
        if(!conn.isClosed() && conn.isValid(2)) {
          return conn;
        } else {
          try { conn.close(); } catch (Exception ignored) {}
        }
      } 
      // 创建连接
      int totalUsed = maxSize - idleConnections.remainingCapacity();
      if (totalUsed < maxSize) {
        Connection newConn = createConnection(url, user, password);
        if (newConn != null) return newConn;
      }
      Print("[ " + name + " ] 连接池已满，获取连接超时");
    } catch (InterruptedException | SQLException e) {
      Print("[ " + name + " ] 获取连接", e.getMessage());
    }
    return null;
  }

  /* 归还连接 */
  static public boolean releaseConnection(Connection conn) {
    if (conn == null) return false;
    // 连接池
    BlockingQueue<Connection> idleConnections = getIdleConnections();
    if(idleConnections == null) return false;
    // 获取
    try {
      if(!conn.isClosed() && conn.isValid(2)) {
        boolean ok = idleConnections.offer(conn);
        Print("3.归还连接", ok, GetIdleCount(), conn);
        return ok;
      } else {
        try { conn.close(); } catch (Exception ignored) {}
      }
    } catch (SQLException e) {
      Print("[ " + name + " ] 归还连接:", e.getMessage());
    }
    return true;
  }

  /* 获取空闲连接数 */
  static public int GetIdleCount() {
    // 连接池
    BlockingQueue<Connection> idleConnections = getIdleConnections();
    if(idleConnections == null) return 0;
    // 空闲连接数
    return idleConnections.size();
  }

  /* 销毁连接池 */
  public void destroy() {
    try {
      // default: 关闭连接、清空连接池
      for(Connection conn : pool_default) if(!conn.isClosed()) conn.close();
      if(pool_default!=null) pool_default.clear();
      // other: 关闭连接、清空连接池
      for(Connection conn : pool_other) if(!conn.isClosed()) conn.close();
      if(pool_other!=null) pool_other.clear();
    } catch (SQLException e) {
      Print("[ " + name +  " ] 销毁连接池:", e.getMessage());
    }
  }
  
}
