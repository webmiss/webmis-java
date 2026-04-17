package vip.webmis.mvc.config;

/* 公共配置 */
public class Env {

  public static final String title = "WebMIS 3.0";                      // 项目名称
  public static final String copy = "webmis.vip © 2026";                // 版权
  public static final String key = "e4b99adec618e653400966be536c45f8";  // 加密密钥
  public static final String password = "123456";                       // 默认密码
  // 资源
  public static final String root_dir = "public/";                      // 根目录
  public static final String img_url = "https://java.webmis.vip/";
  // Token
  public static final String admin_token_prefix = "webmisAdmin";        // 前缀-Admin
  public static final Integer admin_token_time = 2*3600;                // 有效时长(2小时)
  public static final Boolean admin_token_auto = true;                  // 自动续期
  public static final Boolean admin_token_sso = false;                  // 单点登录
  public static final String api_token_prefix = "webmisApi";            // 前缀-Api
  public static final Integer api_token_time = 7*24*3600;               // 有效时长(7天)
  public static final Boolean api_token_auto = true;                    // 自动续期
  public static final Boolean api_token_sso = true;                     // 单点登录
  
}
