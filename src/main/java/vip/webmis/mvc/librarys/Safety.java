package vip.webmis.mvc.librarys;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.Claim;

import vip.webmis.mvc.config.Env;

/* 验证类 */
public class Safety {

  /* 正则-公共 */
  static public Boolean IsRight(String name, String value) {
    switch (name) {
      case "uname":
        return Test("^[a-zA-Z][a-zA-Z0-9\\_\\@\\-\\*\\&]{3,15}$", value);
      case "passwd":
        return Test("^[a-zA-Z0-9|_|@|-|*|&]{6,16}$", value);
      case "tel":
        return Test("^1\\d{10}$", value);
      case "email":
        return Test("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", value);
      case "idcard":
        return Test("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$", value);
      default:
        return false;
    }
  }

  /* 正则-验证 */
  static public Boolean Test(String reg, String value) {
    Pattern pattern = Pattern.compile(reg);
    Matcher matcher = pattern.matcher(value);
    return matcher.matches();
  }

  /* Base64-加密 */
  static public String Encode(Map<String,Object> param) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(Env.key);
      return JWT.create().withClaim("data", param).sign(algorithm);
    } catch (JWTCreationException exception){
      return null;
    }
  }

  /* Base64-解密 */
  static public HashMap<String,Object> Decode(String token) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(Env.key);
      Claim data = JWT.require(algorithm).build().verify(token).getClaim("data");
      return (HashMap<String,Object>) data.asMap();
    } catch (Exception exception){
      return null;
    }
  }
  
}
