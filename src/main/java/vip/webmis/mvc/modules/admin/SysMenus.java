package vip.webmis.mvc.modules.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import vip.webmis.mvc.core.ControllerBase;
import vip.webmis.mvc.models.SysMenu;
import vip.webmis.mvc.service.TokenAdmin;
import vip.webmis.mvc.util.Util;

/* 系统菜单 */
@RestController
@Controller("AdminSysMenus")
@RequestMapping("/admin")
public class SysMenus extends ControllerBase {

  private static HashMap<String, List<HashMap<String, Object>>> menus = null;    // 全部菜单
  private static Map<String, Object> permAll = null;                                  // 用户权限

  /* 获取菜单-权限 */
  @RequestMapping(value="sys_menus/get_menus_perm", produces="application/json;charset=UTF-8")
  public Map<String, Object> GetMenusPerm(@RequestParam Map<String, String> params, @RequestBody Map<String, Object> json, HttpServletRequest request) {
    Map<String,Object> res;
    // 参数
    String token = (String) JsonName(json, "token");
    // 验证
    String msg = TokenAdmin.Verify(token, "");
    if(msg!="") {
      res = new HashMap<String,Object>();
      res.put("code", 4001);
      return GetJSON(res);
    }
    // 用户权限
    permAll = TokenAdmin.GetPerm(token);
    // 全部菜单
    _getMenus();
    // 返回
    List<HashMap<String, Object>> data = _getMenusPerm("0");
    res = new HashMap<String,Object>();
    res.put("code", 0);
    res.put("data", data);
    return GetJSON(res);
  }

  /* 递归菜单 */
  private List<HashMap<String, Object>> _getMenusPerm(String fid) {
    HashMap<String, Object> tmp;
    List<HashMap<String, Object>> menu;
    List<HashMap<String, Object>> data = new ArrayList<>();
    List<HashMap<String, Object>> M = menus.containsKey(fid)?menus.get(fid):data;
    for( HashMap<String, Object> val : M) {
      // 菜单权限
      String id = String.valueOf(val.get("id"));
      if(!permAll.containsKey(id)) continue;
      // 动作权限
      Integer perm = Integer.valueOf(permAll.get(id).toString());
      List<Map<String, Object>> action = new ArrayList<>();
      String actionStr = String.valueOf(val.get("action"));
      List<Map<String, Object>> actionArr = new ArrayList<>();
      if(!actionStr.equals("")) actionArr = Util.JsonDecodeArr(actionStr);
      for(Map<String, Object> v : actionArr){
        Integer permVal = Integer.valueOf(v.get("perm").toString());
        if((perm&permVal)>0) action.add(v);
      }
      // 数据
      Map<String, Object> value = new HashMap<String, Object>();
      value.put("url", val.get("url"));
      value.put("controller", val.get("controller"));
      value.put("action", action);
      Map<String, Object> langs = new HashMap<String, Object>();
      langs.put("en_US", val.get("en_US"));
      langs.put("zh_CN", val.get("zh_CN"));
      tmp = new HashMap<String, Object>();
      tmp.put("icon", val.get("ico"));
      tmp.put("label", val.get("title"));
      tmp.put("en", val.get("en"));
      tmp.put("value", value);
      tmp.put("langs", langs);
      menu = _getMenusPerm(id);
      if(menu.size()>0) tmp.put("children", menu);
      data.add(tmp);
    }
    return data;
  }

  /* 全部菜单 */
  private void _getMenus() {
    SysMenu m = new SysMenu();
    m.Columns(
      "id", "fid", "title", "en", "url", "ico", "controller", "sort", "status",
      "en_US", "zh_CN",
      "FROM_UNIXTIME(ctime, '%Y-%m-%d %H:%i:%s') as ctime", "FROM_UNIXTIME(utime, '%Y-%m-%d %H:%i:%s') as utime",
      "action", "remark"
    );
    m.Order("sort, id");
    List<HashMap<String, Object>> data = m.Find();
    // 数据
    String fid;
    ArrayList<HashMap<String, Object>> tmp;
    menus = new HashMap<String, List<HashMap<String, Object>>>();
    for(HashMap<String, Object> v : data) {
      fid = String.valueOf(v.get("fid"));
      if (menus.containsKey(fid)) {
        menus.get(fid).add(v);
      } else {
        tmp = new ArrayList<HashMap<String, Object>>();
        tmp.add(v);
        menus.put(fid, tmp);
      }
    }
  }
  
}
