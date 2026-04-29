package vip.webmis.mvc.modules.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vip.webmis.mvc.config.Env;
import vip.webmis.mvc.core.ControllerBase;
import vip.webmis.mvc.librarys.FileEo;
import vip.webmis.mvc.librarys.Upload;
import vip.webmis.mvc.service.TokenAdmin;
import vip.webmis.mvc.util.Time;

/* 文件管理 */
@RestController
@Controller("AdminSysFile")
@RequestMapping("/admin")
public class SysFile extends ControllerBase {

  private static final String dirRoot = "upload/";

  /* 列表 */
  @RequestMapping(value="sys_file/list", produces="application/json;charset=UTF-8")
  public Map<String, Object> List(@RequestParam Map<String, String> params, @RequestBody Map<String, Object> json, HttpServletRequest request) {
    ControllerBase.lang = params.get("lang");
    Map<String, Object> res;
    // 参数
    String token = String.valueOf(JsonName(json, "token"));
    String path = String.valueOf(JsonName(json, "path"));
    // 验证
    String msg = TokenAdmin.Verify(token, request.getRequestURI());
    if(msg!="") {
      res = new HashMap<String, Object>();
      res.put("code", 4001);
      return GetJSON(res);
    }
    if(path.isEmpty()) {
      res = new HashMap<String, Object>();
      res.put("code", 4000);
      return GetJSON(res);
    }
    // 数据
    FileEo.Root = Env.root_dir + dirRoot;
    HashMap<String, Object> list = FileEo.List(path);
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("url", BaseUrl(request, dirRoot));
    data.put("list", list);
    // 返回
    res = new HashMap<String, Object>();
    res.put("code",0);
    res.put("time", Time.Date("Y/m/d H:i:s"));
    res.put("data", data);
    return GetJSON(res);
  }

  /* 新建文件夹 */
  @RequestMapping(value="sys_file/mkdir", produces="application/json;charset=UTF-8")
  public Map<String, Object> Mkdir(@RequestParam Map<String, String> params, @RequestBody Map<String, Object> json, HttpServletRequest request) {
    ControllerBase.lang = params.get("lang");
    Map<String, Object> res;
    // 参数
    String token = String.valueOf(JsonName(json, "token"));
    String path = String.valueOf(JsonName(json, "path"));
    String name = String.valueOf(JsonName(json, "name"));
    // 验证
    String msg = TokenAdmin.Verify(token, request.getRequestURI());
    if(msg!="") {
      res = new HashMap<String, Object>();
      res.put("code", 4001);
      return GetJSON(res);
    }
    if(path.isEmpty() || name.isEmpty()) {
      res = new HashMap<String, Object>();
      res.put("code", 4000);
      return GetJSON(res);
    }
    // 数据
    FileEo.Root = Env.root_dir + dirRoot;
    if(!FileEo.Mkdir(path+name)) {
      res = new HashMap<String, Object>();
      res.put("code", 5000);
      return GetJSON(res);
    }
    // 返回
    res = new HashMap<String, Object>();
    res.put("code",0);
    return GetJSON(res);
  }

  /* 重命名 */
  @RequestMapping(value="sys_file/rename", produces="application/json;charset=UTF-8")
  public Map<String, Object> Rename(@RequestParam Map<String, String> params, @RequestBody Map<String, Object> json, HttpServletRequest request) {
    ControllerBase.lang = params.get("lang");
    Map<String, Object> res;
    // 参数
    String token = String.valueOf(JsonName(json, "token"));
    String path = String.valueOf(JsonName(json, "path"));
    String name = String.valueOf(JsonName(json, "name"));
    String rename = String.valueOf(JsonName(json, "rename"));
    // 验证
    String msg = TokenAdmin.Verify(token, request.getRequestURI());
    if(msg!="") {
      res = new HashMap<String, Object>();
      res.put("code", 4001);
      return GetJSON(res);
    }
    if(path.isEmpty() || name.isEmpty() || rename.isEmpty()) {
      res = new HashMap<String, Object>();
      res.put("code", 4000);
      return GetJSON(res);
    }
    // 数据
    FileEo.Root = Env.root_dir + dirRoot;
    if(!FileEo.Rename(path+rename, path+name)) {
      res = new HashMap<String, Object>();
      res.put("code", 5000);
      return GetJSON(res);
    }
    // 返回
    res = new HashMap<String, Object>();
    res.put("code",0);
    return GetJSON(res);
  }

  /* 删除 */
  @RequestMapping(value="sys_file/remove", produces="application/json;charset=UTF-8")
  public Map<String, Object> Remove(@RequestParam Map<String, String> params, @RequestBody Map<String, Object> json, HttpServletRequest request) {
    ControllerBase.lang = params.get("lang");
    Map<String, Object> res;
    // 参数
    String token = String.valueOf(JsonName(json, "token"));
    String path = String.valueOf(JsonName(json, "path"));
    ArrayList<String> data = (ArrayList<String>) JsonName(json, "data");
    // 验证
    String msg = TokenAdmin.Verify(token, request.getRequestURI());
    if(msg!="") {
      res = new HashMap<String, Object>();
      res.put("code", 4001);
      return GetJSON(res);
    }
    if(path.isEmpty() || data.isEmpty()) {
      res = new HashMap<String, Object>();
      res.put("code", 4000);
      return GetJSON(res);
    }
    // 数据
    FileEo.Root = Env.root_dir + dirRoot;
    for(String v : data) {
      FileEo.RemoveAll(path+v);
    }
    // 返回
    res = new HashMap<String, Object>();
    res.put("code",0);
    return GetJSON(res);
  }

  /* 上传 */
  @RequestMapping(value="sys_file/upload", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
  public Map<String, Object> Upload(@RequestParam Map<String, String> params, HttpServletRequest request, @RequestParam("file") MultipartFile file) {
    ControllerBase.lang = params.get("lang");
    Map<String, Object> res;
    // // 参数
    String token = String.valueOf(params.get("token"));
    String path = String.valueOf(params.get("path"));
    // 验证
    String msg = TokenAdmin.Verify(token, request.getRequestURI());
    if(msg!="") {
      res = new HashMap<String, Object>();
      res.put("code", 4001);
      return GetJSON(res);
    }
    if(path.isEmpty()) {
      res = new HashMap<String, Object>();
      res.put("code", 4000);
      return GetJSON(res);
    }
    // 数据
    HashMap<String, Object> param = new HashMap<String, Object>();
    param.put("path", dirRoot+path);
    param.put("bind", null);
    String img = Upload.File(file, param);
    if(img.isEmpty()) {
      res = new HashMap<String, Object>();
      res.put("code", 5000);
      res.put("msg", "上传失败!");
      return GetJSON(res);
    }
    // 返回
    res = new HashMap<String, Object>();
    res.put("code",0);
    return GetJSON(res);
  }

  /* 下载 */
  @RequestMapping(value="sys_file/down", produces="application/json;charset=UTF-8")
  public byte[] Down(@RequestBody Map<String, Object> json, HttpServletRequest request, HttpServletResponse response) {
    // 参数
    String token = String.valueOf(JsonName(json, "token"));
    String path = String.valueOf(JsonName(json, "path"));
    String filename = String.valueOf(JsonName(json, "filename"));
    // 验证
    String msg = TokenAdmin.Verify(token, request.getRequestURI());
    if(msg!="") return null;
    if(path.isEmpty() || filename.isEmpty()) return null;
    // 数据
    FileEo.Root = Env.root_dir + dirRoot;
    byte[] data = FileEo.Bytes(path+filename);
    // 返回
    return GetFile(response, data, new HashMap<String, String>(){{
      put("Content-Type", "application/octet-stream");
    }});
  }
}
