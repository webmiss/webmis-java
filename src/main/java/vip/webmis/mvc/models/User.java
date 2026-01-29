package vip.webmis.mvc.models;

import vip.webmis.mvc.core.Model;

/* 用户表 */
public class User extends Model {

  /* 构造函数 */
  public User() {
    this.DBConn();
    this.Table("user");
  }

}
