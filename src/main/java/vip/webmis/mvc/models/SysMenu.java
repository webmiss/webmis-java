package vip.webmis.mvc.models;

import vip.webmis.mvc.core.Model;

/* 系统菜单 */
public class SysMenu extends Model {

  /* 构造函数 */
  public SysMenu() {
    this.DBConfig("default");
    this.Table("sys_menus");
  }
  
}
