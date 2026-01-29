package vip.webmis.mvc.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vip.webmis.mvc.config.Db;

/* 模型 */
public class Model extends Base {
  public Connection conn;                           // 连接
  private String name = "Model";                      // 名称
  private String table = "";                          // 数据表
  private String columns = "*";                       // 字段
  private String where = "";                          // 条件
  private String group = "";                          // 分组
  private String having = "";                         // 筛选
  private String order = "";                          // 排序
  private String limit = "";                          // 限制
  private List<Object> args = new ArrayList<>();      // 参数
  private String sql="";                            // SQL语句

  /* 获取连接 */
  public Boolean DBConn() {
    return this.DBConn("default");
  } 
  public Boolean DBConn(String name) {
    Map<String, Object> config = new Db().Config(name);
    if (this.conn == null) {
      try {
        String URL = "jdbc:" + config.get("type") + "://" + config.get("host") + ":" + config.get("port") + "/" + config.get("database") + "?useUnicode=true&characterEncoding=" + config.get("charset");
        String USERNAME = (String)config.get("user");
        String PASSWORD = (String)config.get("password");
        this.conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
      } catch (Exception e) {
        Print("[ "+this.name+" ] Conn:", e.getMessage());
      } finally {
        try {
          if (this.conn != null && !this.conn.isClosed()) this.conn.close();
          Print("[ "+this.name+" ] Reconnect DB");
        } catch (Exception e) {
          Print("[ "+this.name+" ] Conn:", e.getMessage());
        }
      }
    }
    return this.conn != null ? true : false;
  }

  /* 表 */
  public void Table(String table) {
    this.table = table;
  }

  /* 分区 */
  public void Partition(String... partition) {
    this.table += " PARTITION(" + String.join(",", partition) + ")";
  }

  /* 关联-INNER */
  public void Join(String table, String on) {
    this.table += " INNER JOIN " + table + " ON " + on;
  }

  /* 关联-LEFT */
  public void LeftJoin(String table, String on) {
    this.table += " LEFT JOIN " + table + " ON " + on;
  }

  /* 关联-RIGHT */
  public void RightJoin(String table, String on) {
    this.table += " RIGHT JOIN " + table + " ON " + on;
  }

  /* 关联-FULL */
  public void FullJoin(String table, String on) {
    this.table += " FULL JOIN " + table + " ON " + on;
  }

  /* 字段 */
  public void Columns(String... columns) {
    this.columns = String.join(",", columns);
  }

  /* 条件 */
  public void Where(String where, Object... args) {
    this.where = " WHERE " + where;
    for(Object arg : args) {
      this.args.add(arg);
    }
  }

  /* 分组 */
  public void Group(String... group) {
    this.group = " GROUP BY " + String.join(",", group);
  }

  /* 筛选 */
  public void Having(String having) {
    this.having = " HAVING " + having;
  }

  /* 排序 */
  public void Order(String... order) {
    this.order = " ORDER BY " + String.join(",", order);
  }

  /* 限制 */
  public void Limit(String start, String limit) {
    this.limit = " LIMIT " + start + "," + limit;
  }

  /* 分页 */
  public void Page(int page, int limit) {
    this.limit = " LIMIT " + (page - 1) * limit + "," + limit;
  }

  /* 查询-SQL */
  public Object[] SelectSQL() {
    //  验证
    if(this.table.equals("")) {
      Print("[ "+this.name+" ]", "Select: 表不能为空!");
      return null;
    }
    if(this.columns.equals("")) {
      Print("[ "+this.name+" ]", "Select: 字段不能为空!");
      return null;
    }
    // SQL
    this.sql = "SELECT " + this.columns + " FROM " + this.table;
    this.table = "";
    this.columns = "*";
    if(!this.where.equals("")) {
      this.sql += this.where;
      this.where = "";
    }
    if(!this.group.equals("")) {
      this.sql += this.group;
      this.group = "";
    }
    if(!this.having.equals("")) {
      this.sql += this.having;
      this.having = "";
    }
    if(!this.order.equals("")) {
      this.sql += this.order;
      this.order = "";
    }
    if(!this.limit.equals("")) {
      this.sql += this.limit;
      this.limit = "";
    }
    // 参数
    List<Object> args = this.args;
    this.args = new ArrayList<>();
    // 结果
    return new Object[]{this.sql, args};
  }

  /* 查询-多条 */
  public void Find(Object[] param) {
    Object[] res = param!=null?param:SelectSQL();
  }

}