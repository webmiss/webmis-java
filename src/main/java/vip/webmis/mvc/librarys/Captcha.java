package vip.webmis.mvc.librarys;

import java.util.Random;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.RandomGenerator;

/* 验证码 */
public class Captcha {

  /* 字符集 */
  private static final String txtChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final Random RANDOM = new Random();

  /* 获取字符 */
  public static String GetCode(int num) {
    StringBuilder sb = new StringBuilder(num);
    for (int i = 0; i < num; i++) {
      int index = RANDOM.nextInt(txtChars.length());
      sb.append(txtChars.charAt(index));
    }
    return sb.toString();
  }

  /* 获取数字 */
  public static String GetNum(int num) {
    StringBuilder sb = new StringBuilder(num);
    for (int i = 0; i < num; i++) {
      int index = RANDOM.nextInt(10);
      sb.append(index);
    }
    return sb.toString();
  }

  /* 图形验证码 */
  public static Object[] Vcode() {
    return Vcode(4);
  }
  public static Object[] Vcode(Integer num) {
    ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(140, 40, num, 2);
    CodeGenerator generator = new RandomGenerator(txtChars, num);
    captcha.setGenerator(generator);
    captcha.createCode();
    String code = captcha.getCode();
    byte[] img = captcha.getImageBytes();
    return new Object[]{code, img};
  }
  
}
