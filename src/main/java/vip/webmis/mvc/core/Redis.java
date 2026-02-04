package vip.webmis.mvc.core;

import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* 缓存数据库 */
public class Redis  extends Base {

  public static GenericObjectPool<StatefulRedisConnection<String, String>> pool;  // 连接池
  public StatefulRedisConnection<String, String> conn;                            // 连接
  private final String name = "Pool";                                             // 名称

  /* 构造函数 */
  public Redis() {
    this("default");
  }
  public Redis(String name) {
    // 创建1次
    if (Redis.pool == null) {
      // 配置
      vip.webmis.mvc.config.Redis redis = new vip.webmis.mvc.config.Redis();
      Map<String, Object> cfg = redis.Config(name);
      try {
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
        Redis.pool = ConnectionPoolSupport.createGenericObjectPool(() -> redisClient.connect(), poolConfig);
      } catch (Exception e) {
        Print("[ "+this.name+" ]", e.getMessage());
      }
    }
    // 连接
    if (this.conn == null) this.conn = RedisConn();
  }

  /* 获取连接 */
  public StatefulRedisConnection<String, String> RedisConn() {
    if (this.conn == null) {
      try {
        this.conn = Redis.pool.borrowObject();
      } catch (Exception e) {
        Print("[ "+this.name+" ] conn:", e.getMessage());
      }
    }
    return this.conn;
  }

  /* 关闭连接 */
  public void Close() {
    if (this.conn != null) {
      try {
        Redis.pool.returnObject(this.conn);
      } catch (Exception e) {
        Print("[ "+this.name+" ] close:", e.getMessage());
      }
    }
  }

  /* 销毁连接池 */
  public void Destroy() {
    if (Redis.pool != null) Redis.pool.close();
  }

  /* 添加 */
  public boolean Set(String key, String value) {
    if (this.conn == null) return false;
    this.conn.sync().set(key, value);
    return true;
  }

  /* 自增 */
  public long Incr(String key) {
    if (this.conn == null) return 0;
    return this.conn.sync().incr(key);
  }

  /* 自减 */
  public long Decr(String key) {
    if (this.conn == null) return 0;
    return this.conn.sync().decr(key);
  }

  /* 获取 */
  public String Get(String key) {
    if (this.conn == null) return "";
    return this.conn.sync().get(key);
  }

  /* 删除 */
  public Long Del(String key) {
    if (this.conn == null) return null;
    return this.conn.sync().del(key);
  }

  /* 是否存在 */
  public Long Exist(String key) {
    if (this.conn == null) return null;
    return this.conn.sync().exists(key);
  }

  /* 设置过期时间(秒) */
  public boolean Expire(String key, int seconds) {
    if (this.conn == null) return false;
    return this.conn.sync().expire(key, Duration.ofSeconds(seconds));
  }

  /* 获取过期时间(秒) */
  public Long Ttl(String key) {
    if (this.conn == null) return null;
    return this.conn.sync().ttl(key);
  }

  /* 获取长度 */
  public Long Len(String key) {
    if (this.conn == null) return null;
    return this.conn.sync().strlen(key);
  }

  /* 哈希(Hash)-添加 */
  public boolean HSet(String key, String field, String value) {
    if (this.conn == null) return false;
    return this.conn.sync().hset(key, field, value);
  }

  /* 哈希(Hash)-删除 */
  public Long HDel(String key, String field) {
    if (this.conn == null) return null;
    return this.conn.sync().hdel(key, field);
  }

  /* 哈希(Hash)-获取 */
  public String HGet(String key, String field) {
    if (this.conn == null) return "";
    return this.conn.sync().hget(key, field);
  }

  /* 哈希(Hash)-获取全部 */
  public Map<String, String> HGetAll(String key) {
    if (this.conn == null) return new HashMap<>();
    return this.conn.sync().hgetall(key);
  }

  /* 哈希(Hash)-获取全部字段 */
  public List<String> HKeys(String key) {
    if (this.conn == null) return new ArrayList<>();
    return this.conn.sync().hkeys(key);
  }

  /* 哈希(Hash)-获取全部值 */
  public List<String> HVals(String key) {
    if (this.conn == null) return new ArrayList<>();
    return this.conn.sync().hvals(key);
  }

  /* 哈希(Hash)-是否存在 */
  public Boolean HExist(String key, String field) {
    if (this.conn == null) return false;
    return this.conn.sync().hexists(key, field);
  }

  /* 哈希(Hash)-获取长度 */
  public Long HLen(String key) {
    if (this.conn == null) return null;
    return this.conn.sync().hlen(key);
  }

  /* 列表(List)-添加 */
  public Long LPush(String key, String value) {
    if (this.conn == null) return null;
    return this.conn.sync().lpush(key, value);
  }
  public Long RPush(String key, String value) {
    if (this.conn == null) return null;
    return this.conn.sync().rpush(key, value);
  }

  /* 列表(List)-获取 */
  public List<String> LRange(String key) {
    if (this.conn == null) return null;
    return this.conn.sync().lrange(key, 0, -1);
  }
  public String LPop(String key) {
    if (this.conn == null) return null;
    return this.conn.sync().lpop(key);
  }
  public String RPop(String key) {
    if (this.conn == null) return null;
    return this.conn.sync().rpop(key);
  }
  public KeyValue<String, String> BLPop(String key) {
    if (this.conn == null) return null;
    return this.conn.sync().blpop(0, key);
  }
  public KeyValue<String, String> BRPop(String key) {
    if (this.conn == null) return null;
    return this.conn.sync().brpop(0, key);
  }
  
}
