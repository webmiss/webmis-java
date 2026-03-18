package vip.webmis.mvc.models;

import vip.webmis.mvc.core.Model;

/* 角色 */
public class SysRole extends Model {

  /* 构造函数 */
  public SysRole() {
    this.DBConn("default");
    this.Table("sys_role");
  }
  
}
