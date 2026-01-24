package webmis.core;

/* 基础 */
public class Base {

  /* 输出到控制台 */
  static protected void Print(Object... content) {
    for(int i=0; i<content.length; i++){
      System.out.print(content[i]);
      System.out.print(" ");
    }
    System.out.print("\n");
  }
  
}
