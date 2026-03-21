package vip.webmis.mvc.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import vip.webmis.mvc.config.Env;
import vip.webmis.mvc.core.Base;
import vip.webmis.mvc.core.Redis;
import vip.webmis.mvc.librarys.Safety;
import vip.webmis.mvc.models.SysMenu;
import vip.webmis.mvc.util.Hash;
import vip.webmis.mvc.util.Time;
import vip.webmis.mvc.util.Util;

/* Token Admin */
public class TokenAdmin extends Base {

  /* 验证 */
  public static String Verify(String token, String urlPerm) {
    // Token
    if(token.equals("")) return "Token不能为空!";
    Map<String, Object> tData = Safety.Decode(token);
    if(tData==null) return "Token验证失败!";
    // 是否过期
    String uid = (String) tData.get("uid");
    String key = Env.admin_token_prefix+"_token_"+uid;
    Redis redis = new Redis();
    Long time = redis.Ttl(key);
    if(time==null || time<1) return "请重新登录!";
    // 单点登录
    String access_token = redis.Get(key);
    if(Env.admin_token_sso && Hash.Md5(token)!=access_token) return "强制退出!";
    // 是否续期
    if(Env.admin_token_auto) {
      redis.Expire(key, Env.admin_token_time);
      redis.Expire(Env.admin_token_prefix+"_perm_"+uid, Env.admin_token_time);
    }
    // URL权限
    if(urlPerm.equals("")) return "";
    String[] arr = Util.Explode("/", urlPerm);
    String action = Util.Explode("?", arr[arr.length-1])[0];
    arr = Arrays.copyOf(arr, arr.length-1);
    String controller = Util.Implode("/", arr);
    // 查询菜单
    SysMenu m = new SysMenu();
    m.Columns("id", "action");
    m.Where("controller=?", controller);
    Map<String, Object> data = m.FindFirst();
    if(data.isEmpty()) return "菜单验证无效!";
    // 验证菜单
    String id = (String) data.get("id");
    Map<String, Object> perm = GetPerm(token);
    if(!perm.containsKey(id)) return "无权访问菜单!";
    // 验证动作
    Integer permVal = 0;
    Integer actionVal = (Integer) perm.get(id);
    Map<String, Object> permArr = Util.JsonDecode(data.get("action").toString());
    for(Map.Entry<String, Object> entry: permArr.entrySet()) {
      if(entry.getValue().equals(action)) {
        permVal = (Integer) entry.getValue();
        break;
      }
    }
    if((actionVal&permVal)==0) return "无权访问动作!";
    return "";
  }

  /* 权限-保存 */
  public static boolean SavePerm(String uid, String perm) {
    String key = Env.admin_token_prefix+"_perm_"+uid;
    Redis redis = new Redis();
    redis.Set(key, perm);
    redis.Expire(key, Env.admin_token_time);
    return true;
  }

  /* 权限-获取 */
  public static Map<String, Object> GetPerm(String token) {
    Map<String, Object> arr = new HashMap<>();
    // Token
    if(token.equals("")) return arr;
    Map<String, Object> tData = Safety.Decode(token);
    if(tData==null) return arr;
    // 权限
    String uid = (String) tData.get("uid");
    Redis redis = new Redis();
    String permStr = redis.Get(Env.admin_token_prefix+"_perm_"+uid);
    if(permStr==null) return arr;
    // 拆分
    String[] tmp;
    String[] perm = Util.Explode(" ", permStr);
    for(String p: perm) {
      tmp = Util.Explode(":", p);
      arr.put(tmp[0], tmp[1]);
    }
    return arr;
  }

  /* 生成 */
  public static String Create(Map<String, Object> data) {
    // 登录时间
    data.put("l_time", Time.Date("yyyy-MM-dd HH:mm:ss"));
    String token = Safety.Encode(data);
    // 缓存Token
    String key = Env.admin_token_prefix+"_token_"+data.get("uid");
    Redis redis = new Redis();
    redis.Set(key, Hash.Md5(token));
    redis.Expire(key, Env.admin_token_time);
    return token;
  }

  /* 解析 */
  public static Map<String, Object> Token(String token) {
    Map<String, Object> data = Safety.Decode(token);
    if(data==null) return null;
    // 过期时间
    Redis redis = new Redis();
    data.put("time", redis.Ttl(Env.admin_token_prefix+"_token_"+data.get("uid")));
    return data;
  }
  
}
