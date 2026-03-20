package vip.webmis.mvc.service;

import java.util.HashMap;
import java.util.Map;

import vip.webmis.mvc.config.Env;
import vip.webmis.mvc.util.Time;

/* 数据类 */
public class Data {

  // 分区时间
  public static Map<String, Integer> Partition() {
    Map<String, Integer> map = new HashMap<>();
    map.put("p2601", 1769875200);
    map.put("p2602", 1772294400);
    map.put("p2603", 1774972800);
    map.put("p2604", 1777564800);
    map.put("p2605", 1780243200);
    map.put("p2606", 1782835200);
    map.put("p2607", 1785513600);
    map.put("p2608", 1788192000);
    map.put("p2609", 1790784000);
    map.put("p2610", 1793462400);
    map.put("p2611", 1796054400);
    map.put("p2612", 1798732800);
    map.put("plast", 1798732800);
    return map;
  }

  /* 图片地址 */
  public static String Img(String img) {
    return Img(img, true);
  }
  public static String Img(String img, Boolean isTmp) {
    if(img.equals("")) return "";
    return isTmp?Env.img_url+img:Env.img_url+img+"?"+Time.Time();
  }
  /* 图片地址-商品 */
  public static String ImgGoods(String sku_id) {
    return ImgGoods(sku_id, true);
  }
  public static String ImgGoods(String sku_id, Boolean isTmp) {
    return Img("img/sku/"+sku_id+".jpg", isTmp);
  }
  
}
