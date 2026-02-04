package vip.webmis.mvc.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vip.webmis.mvc.config.Db;

/* 模型 */
public class Model extends Base {
  public static MySQLConnectionPool pool;           // 连接池
  public Connection conn;                           // 连接
  private final String name = "Model";              // 名称
  private String table = "";                        // 数据表
  private String columns = "*";                     // 字段
  private String where = "";                        // 条件
  private String group = "";                        // 分组
  private String having = "";                       // 筛选
  private String order = "";                        // 排序
  private String limit = "";                        // 限制
  private List<Object> args = new ArrayList<>();    // 参数
  private String sql = "";                          // SQL语句
  private String keys = "";                         // 添加-键
  private String values = "";                       // 添加-值
  private String data = "";                         // 更新-数据
  private Integer id = 0;                           // 自增ID
  private Integer nums = 0;                         // 影响行数

  /* 获取连接 */
  public Connection DBConn() {
    return this.DBConn("default");
  } 
  public Connection DBConn(String name) {
    // 配置
    Map<String, Object> config = new Db().Config(name);
    int InitSize = (Integer)config.get("poolInitSize");
    int MaxSize = (Integer)config.get("poolMaxSize");
    int MaxWait = (Integer)config.get("poolMaxWait");
    // 连接池
    if(Model.pool == null) {
      Model.pool = new MySQLConnectionPool(config, InitSize, MaxSize);
    }
    // 创建连接
    if (this.conn == null) {
      try {
        this.conn = Model.pool.getConnection(MaxWait);
      } catch (Exception e) {
        Print("[ "+this.name+" ] Conn:", e.getMessage());
      }
    }
    return this.conn;
  }

  /* 执行SQL */
  public PreparedStatement Exec(Connection conn, String sql, List<Object> args) {
    return this.Exec(conn, sql, args, true);
  }
  public PreparedStatement Exec(Connection conn, String sql, List<Object> args, boolean isQuery) {
    try {
      PreparedStatement ps = isQuery?conn.prepareStatement(sql):conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
      for(int i=0;i<args.size();i++) {
        ps.setObject(i+1, args.get(i));
      }
      return ps;
    } catch (Exception e) {
      Print("[ "+this.name+" ] Exec:", e.getMessage());
      return null;
    }
  }

  /* 关闭 */
  public void Close() {
    try {
      if (Model.pool != null) {
        // 归还连接池
        Model.pool.releaseConnection(this.conn);
        this.conn = null;
      }
    } catch (Exception e) {
      Print("[ "+this.name+" ] Close:", e.getMessage());
    }
  }

  /* 获取-SQL */
  public String GetSQL() {
    return this.sql;
  }

  /* 获取-自增ID */
  public Integer GetID() {
    return this.id;
  }

  /* 获取-影响行数 */
  public Integer GetNums() {
    return this.nums;
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
  public List<HashMap<String,Object>> Find() {
    return this.Find("");
  }
  public List<HashMap<String,Object>> Find(String sql, Object... args) {
    List<Object> param = Arrays.asList(args);
    if(sql.equals("")) {
      Object[] res = this.SelectSQL();
      sql = (String)res[0];
      param = (List<Object>)res[1];
      if(sql.equals("")) return new ArrayList<>();
    }
    PreparedStatement ps = this.Exec(this.conn, sql, param);
    if(ps == null) return new ArrayList<>();
    List<HashMap<String,Object>> data = this.FindDataAll(this.conn, ps);
    return data;
  }

  /* 查询-单条 */
  public Map<String,Object> FindFirst() {
    return this.FindFirst("");
  }
  public Map<String,Object> FindFirst(String sql, Object... args) {
    List<Object> param = Arrays.asList(args);
    if(sql.equals("")) {
      this.Limit("0", "1");
      Object[] res = this.SelectSQL();
      sql = (String)res[0];
      param = (List<Object>)res[1];
      if(sql.equals("")) return new HashMap<>();
    }
    PreparedStatement ps = this.Exec(this.conn, sql, param);
    if(ps == null) return new HashMap<>();
    List<HashMap<String,Object>> data = this.FindDataAll(this.conn, ps);
    if(data.size() == 0) return new HashMap<>();
    return data.get(0);
  }

  /* 查询-结果 */
  public List<HashMap<String,Object>> FindDataAll(Connection conn, PreparedStatement ps) {
    List<HashMap<String,Object>> res = new ArrayList<>();
    try {
      ResultSet rs = ps.executeQuery();
      ResultSetMetaData data = rs.getMetaData();
      while(rs.next()) {
        HashMap<String,Object> row = new HashMap<>();
        for(int i=1;i<=data.getColumnCount();i++) {
          row.put(data.getColumnName(i), rs.getObject(i));
        }
        res.add(row);
      }
      rs.close();
      ps.close();
      this.Close();
    } catch (SQLException e) {
      Print("[ "+this.name+" ]", "Query: "+e.getMessage());
    }
    return res;
  }

  /* 添加-单条 */
  public void Values(Map<String, Object> data) {
    this.args = new ArrayList<>();
    String keys = "";
    String vals = "";
    for(Map.Entry<String, Object> entry : data.entrySet()) {
      keys += entry.getKey() + ",";
      vals += "?,";
      this.args.add(entry.getValue());
    }
    if(!keys.equals("")) {
      this.keys = keys.substring(0, keys.length()-1);
      this.values = vals.substring(0, vals.length()-1);
    }
  }

  /* 添加-多条 */
  public void ValuesAll(ArrayList<HashMap<String, Object>> data) {
    String keys = "";
    String vals = "";
    String tmp = "";
    for(Map.Entry<String, Object> entry : data.get(0).entrySet()) {
      keys += entry.getKey() + ",";
    }
    for(HashMap<String, Object> row : data) {
      tmp = "";
      for(Object val : row.values()) {
        vals += val + ",";
        tmp += "?,";
        this.args.add(val);
      }
      vals += "("+tmp.substring(0, tmp.length()-1)+"),";
    }
    if(!keys.equals("")) {
      this.keys = columns.substring(0, keys.length()-1);
      this.values = vals.substring(0, vals.length()-1);
    }
  }

  /* 添加-SQL */
  public Object[] InsertSQL() {
    //  验证
    if(this.table.equals("")) {
      Print("[ "+this.name+" ]", "Insert: 表不能为空!");
      return null;
    }
    if(this.keys.equals("") || this.values.equals("")) {
      Print("[ "+this.name+" ]", "Insert: 字段或值不能为空!");
      return null;
    }
    // SQL
    this.sql = "INSERT INTO " + this.table + " (" + this.keys + ") VALUES (" + this.values + ")";
    this.table = "";
    this.keys = "";
    this.values = "";
    // 参数
    List<Object> args = this.args;
    this.args = new ArrayList<>();
    // 结果
    return new Object[]{this.sql, args};
  }

  /* 添加-执行 */
  public Integer Insert() {
    return this.Insert("");
  }
  public Integer Insert(String sql, Object... args) {
    List<Object> param = Arrays.asList(args);
    if(sql.equals("")) {
      Object[] res = this.InsertSQL();
      if(res == null) return -1;
      sql = (String)res[0];
      param = (List<Object>)res[1];
    }
    try {
      PreparedStatement ps = this.Exec(this.conn, sql, param, false);
      this.nums = ps.executeUpdate();
      ResultSet rs = ps.getGeneratedKeys();
      this.id = rs.next()?rs.getInt(1):0;
      rs.close();
      ps.close();
      this.Close();
      return this.id;
    } catch (SQLException e) {
      Print("[ "+this.name+" ]", "Insert: "+e.getMessage());
      return -1;
    }
  }

  /* 更新-数据 */
  public void Set(Map<String, Object> data) {
    this.args = new ArrayList<>();
    String vals = "";
    for(Map.Entry<String, Object> entry : data.entrySet()) {
      vals += entry.getKey() + "=?,";
      this.args.add(entry.getValue());
    }
    if(!vals.equals("")) {
      this.data = vals.substring(0, vals.length()-1);
    }
  }

  /* 更新-SQL */
  public Object[] UpdateSQL() {
    //  验证
    if(this.table.equals("")) {
      Print("[ "+this.name+" ]", "Update: 表不能为空!");
      return null;
    }
    if(this.data.equals("")) {
      Print("[ "+this.name+" ]", "Update: 数据不能为空!");
      return null;
    }
    if(this.where.equals("")) {
      Print("[ "+this.name+" ]", "Update: 条件不能为空!");
      return null;
    }
    // SQL
    this.sql = "UPDATE " + this.table + " SET " + this.data + this.where;
    // 重置
    this.table = "";
    this.data = "";
    this.where = "";
    // 参数
    List<Object> args = this.args;
    this.args = new ArrayList<>();
    // 结果
    return new Object[]{this.sql, args};
  }

  /* 更新-执行 */
  public Boolean Update() {
    return this.Update("");
  }
  public Boolean Update(String sql, Object... args) {
    List<Object> param = Arrays.asList(args);
    if(sql.equals("")) {
      Object[] res = this.UpdateSQL();
      if(res == null) return false;
      sql = (String)res[0];
      param = (List<Object>)res[1];
    }
    try {
      PreparedStatement ps = this.Exec(this.conn, sql, param, false);
      this.nums = ps.executeUpdate();
      ps.close();
      this.Close();
      return true;
    } catch (SQLException e) {
      Print("[ "+this.name+" ]", "Update: "+e.getMessage());
      return false;
    }
  }

  /* 删除-SQL */
  public Object[] DeleteSQL() {
    //  验证
    if(this.table.equals("")) {
      Print("[ "+this.name+" ]", "Delete: 表不能为空!");
      return null;
    }
    if(this.where.equals("")) {
      Print("[ "+this.name+" ]", "Delete: 条件不能为空!");
      return null;
    }
    // SQL
    this.sql = "DELETE FROM " + this.table + this.where;
    // 重置
    this.table = "";
    this.where = "";
    // 结果
    List<Object> args = this.args;
    this.args = new ArrayList<>();
    return new Object[]{this.sql, args};
  }

  /* 删除-执行 */
  public Boolean Delete() {
    return this.Delete("");
  }
  public Boolean Delete(String sql, Object... args) {
    List<Object> param = Arrays.asList(args);
    if(sql.equals("")) {
      Object[] res = this.DeleteSQL();
      if(res == null) return false;
      sql = (String)res[0];
      param = (List<Object>)res[1];
    }
    try {
      PreparedStatement ps = this.Exec(this.conn, sql, param, false);
      this.nums = ps.executeUpdate();
      ps.close();
      this.Close();
      return true;
    } catch (SQLException e) {
      Print("[ "+this.name+" ]", "Delete: "+e.getMessage());
      return false;
    }
  }

}