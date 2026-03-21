package vip.webmis.mvc.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import vip.webmis.mvc.core.Model;
import vip.webmis.mvc.util.Util;

/* 系统菜单 */
public class ErpBasePartner extends Model {

  /* 构造函数 */
  public ErpBasePartner() {
    this.DBConn("default");
    this.Table("erp_base_partner");
  }

  /* 列表 */
  public static Map<String, Map<String, Object>> GetList(String[] where) {
    String[] columns = {"name", "status"};
    return GetList(where, columns, "status DESC, sort DESC, name ASC");
  }
  public static Map<String, Map<String, Object>> GetList(String[] where, String[] columns) {
    return GetList(where, columns, "status DESC, sort DESC, name ASC");
  }
  public static Map<String, Map<String, Object>> GetList(String[] where, String[] columns, String order_by) {
    // 字段
    List<String> list = new ArrayList<>(Arrays.asList(columns));
    list.add("wms_co_id");
    // 查询
    ErpBasePartner m = new ErpBasePartner();
    m.Columns(list.toArray(new String[0]));
    m.Where(Util.Implode(" AND ", where));
    m.Order(order_by);
    List<HashMap<String, Object>> all = m.Find();
    // 数据
    Map<String, Map<String, Object>> data = new HashMap<>();
    for(HashMap<String, Object> v : all) {
      data.put(String.valueOf(v.get("wms_co_id")), v);
    }
    return data;
  }
  
}
