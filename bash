#!/bin/bash

# 配置
s=$1
name='webmis-java'
version='3.0.0'
log='public/server.log'

# 运行
if [ "$s" == "serve" ]; then
  mvn spring-boot:run
# 安装
elif [ "$s" == "install" ]; then
  mvn clean && mvn compile
# 打包
elif [ "$s" == "build" ]; then
  mvn package -DskipTests && rm -fr "./$name$version.jar" && cp "target/$name.jar" "./$name$version.jar"
# 启动
elif [ "$s" == "start" ]; then
  nohup java -jar $name$version.jar > $log 2>&1 &
# 停止
elif [ "$s" == "stop" ]; then
  ps -aux | grep "java -jar $name$version.jar" | grep -v grep | awk {'print $2'} | xargs kill
else
  echo "----------------------------------------------------"
  echo "[use] ./bash <command>"
  echo "----------------------------------------------------"
  echo "  ./bash <command>"
  echo "<command>"
  echo "  serve         运行: mvn spring-boot:run"
  echo "  install       安装依赖包: pom.xml"
  echo "  build         打包: mvn package -DskipTests"
  echo "<Server>"
  echo "  start         启动"
  echo "  stop          停止"
  echo "----------------------------------------------------"
fi
