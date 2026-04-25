package vip.webmis.mvc.core;

import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/* 缓存数据库 */
public class Redis  extends Base {

  private static GenericObjectPool<StatefulRedisConnection<String, String>> pool_default;  // 连接池: default;
  private static GenericObjectPool<StatefulRedisConnection<String, String>> pool_other;    // 连接池: other;
  private static final String name = "Redis";                                              // 名称
  private static final Object LOCK = new Object();                                         // 锁
  private static String db = "default";                                                    // 数据库

  /* 构造函数 */
  public Redis() {
    db = "default";
  }
  public Redis(String name) {
    db = name;
  }

  /* 数据源 */
  private static void initPool(String name) {
    if("default".equals(name) && pool_default != null && !pool_default.isClosed()) return;
    if("other".equals(name) && pool_other != null && !pool_other.isClosed()) return;
    synchronized (LOCK) {
      if("default".equals(name) && pool_default != null && !pool_default.isClosed()) return;
      if("other".equals(name) && pool_other != null && !pool_other.isClosed()) return;
       // 配置
      vip.webmis.mvc.config.Redis redis = new vip.webmis.mvc.config.Redis();
      Map<String, Object> cfg = redis.Config(name);
      try{
        // 创建连接
        RedisURI redisURI = RedisURI.builder()
        .withHost((String)cfg.get("host"))
        .withPort((Integer)cfg.get("port"))
        .withPassword(((String)cfg.get("password")).toCharArray())
        .withDatabase((Integer)cfg.get("db"))
        .withTimeout(Duration.ofMillis(5000))
        .build();
        // 初始化客户端
        RedisClient redisClient = RedisClient.create(redisURI);
        // 配置连接池
        GenericObjectPoolConfig<StatefulRedisConnection<String, String>> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMinIdle((Integer)cfg.get("minIdle"));
        poolConfig.setMaxIdle((Integer)cfg.get("maxIdle"));
        poolConfig.setMaxTotal((Integer)cfg.get("maxTotal"));
        poolConfig.setMaxWait(Duration.ofMillis((Integer)cfg.get("maxWait")));
        // 创建连接池
        if("default".equals(name)) Redis.pool_default = ConnectionPoolSupport.createGenericObjectPool(() -> redisClient.connect(), poolConfig);
        else if("other".equals(name)) Redis.pool_other = ConnectionPoolSupport.createGenericObjectPool(() -> redisClient.connect(), poolConfig);
      } catch (Exception e) {
        Print("[ "+Redis.name+" ]", e.getMessage());
      }
      Print("[ "+Redis.name+" ] Redis Pool:", name, cfg.get("maxIdle"));
    }
  }

  /* 获取连接 */
  public StatefulRedisConnection<String, String> RedisConn(String name) {
    db = name;
    StatefulRedisConnection<String, String> conn = null;
    try{
      // 初始化连接池
      initPool(name);
      // 获取连接
      if("default".equals(name)) conn = Redis.pool_default.borrowObject();
      else if("other".equals(name)) conn = Redis.pool_other.borrowObject();
    } catch(Exception e) {
      Print("[ "+Redis.name+" ] RedisConn:", e.getMessage());
    }
    return conn;
  }

  /* 关闭连接 */
  public void Close(StatefulRedisConnection<String, String> conn) {
    if (conn != null) {
      try {
        if("default".equals(db)) Redis.pool_default.returnObject(conn);
        else if("other".equals(db)) Redis.pool_other.returnObject(conn);
      } catch (Exception e) {
        Print("[ "+Redis.name+" ] close:", e.getMessage());
        conn = null;
      }
    }
  }

  /* 销毁连接池 */
  public void Destroy() {
    if (Redis.pool_default != null) Redis.pool_default.close();
    if (Redis.pool_other != null) Redis.pool_other.close();
  }

  /* 添加 */
  public boolean Set(String key, String value) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return false;
    // 执行
    try {
      conn.sync().set(key, value);
      return true;
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] Set:", e.getMessage());
      return false;
    } finally {
      Close(conn);
    }
  }

  /* 自增 */
  public long Incr(String key) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return 0;
    // 执行
    try{
      return conn.sync().incr(key);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] Incr:", e.getMessage());
      return 0;
    } finally {
      Close(conn);
    }
    
  }

  /* 自减 */
  public long Decr(String key) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return 0;
    // 执行
    try{
      return conn.sync().decr(key);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] Decr:", e.getMessage());
      return 0;
    } finally {
      Close(conn);
    }
  }

  /* 获取 */
  public String Get(String key) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      String res = conn.sync().get(key);
      return res == null?"":res;
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] Get:", e.getMessage());
      return "";
    } finally {
      Close(conn);
    }
  }

  /* 删除 */
  public Long Del(String key) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().del(key);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] Del:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }

  /* 是否存在 */
  public Long Exist(String key) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().exists(key);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] Exist:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }

  /* 设置过期时间(秒) */
  public boolean Expire(String key, int seconds) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return false;
    // 执行
    try{
      return conn.sync().expire(key, Duration.ofSeconds(seconds));
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] Expire:", e.getMessage());
      return false;
    } finally {
      Close(conn);
    }
  }

  /* 获取过期时间(秒) */
  public Long Ttl(String key) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().ttl(key);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] Ttl:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }

  /* 获取长度 */
  public Long Len(String key) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().strlen(key);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] Len:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }

  /* 哈希(Hash)-添加 */
  public boolean HSet(String key, String field, String value) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return false;
    // 执行
    try{
      return conn.sync().hset(key, field, value);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] HSet:", e.getMessage());
      return false;
    } finally {
      Close(conn);
    }
  }

  /* 哈希(Hash)-删除 */
  public Long HDel(String key, String field) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().hdel(key, field);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] HDel:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }

  /* 哈希(Hash)-获取 */
  public String HGet(String key, String field) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().hget(key, field);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] HGet:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }

  /* 哈希(Hash)-获取全部 */
  public Map<String, String> HGetAll(String key) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().hgetall(key);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] HGetAll:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }

  /* 哈希(Hash)-获取全部字段 */
  public List<String> HKeys(String key) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().hkeys(key);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] HKeys:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }

  /* 哈希(Hash)-获取全部值 */
  public List<String> HVals(String key) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().hvals(key);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] HVals:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }

  /* 哈希(Hash)-是否存在 */
  public Boolean HExist(String key, String field) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().hexists(key, field);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] HExist:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }

  /* 哈希(Hash)-获取长度 */
  public Long HLen(String key) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().hlen(key);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] HLen:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }

  /* 列表(List)-添加 */
  public Long LPush(String key, String value) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().lpush(key, value);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] LPush:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }
  public Long RPush(String key, String value) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().rpush(key, value);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] RPush:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }

  /* 列表(List)-获取 */
  public List<String> LRange(String key) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().lrange(key, 0, -1);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] LRange:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }
  public String LPop(String key) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().lpop(key);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] LPop:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }
  public String RPop(String key) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().rpop(key);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] RPop:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }
  public KeyValue<String, String> BLPop(String key) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().blpop(0, key);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] BLPop:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }
  public KeyValue<String, String> BRPop(String key) {
    // 连接
    StatefulRedisConnection<String, String> conn = RedisConn(db);
    if(conn==null) return null;
    // 执行
    try{
      return conn.sync().brpop(0, key);
    } catch (Exception e) {
      Print("[ "+Redis.name+" ] BRPop:", e.getMessage());
      return null;
    } finally {
      Close(conn);
    }
  }
  
}
