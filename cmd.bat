@echo off
CHCP 65001 >nul 2>&1

REM 配置
set s=%1%
set name=webmis-java
set version=3.0.0
set java_dir=D:\soft\java
set java_version=25.0.2
set java_url=https://download.java.net/java/GA/jdk%java_version%/b1e0dfa218384cb9959bdcb897162d4e/10/GPL/openjdk-%java_version%_windows-x64_bin.zip
set maven_dir=D:\soft\java\maven
set maven_version=3.9.12
set maven_url=https://archive.apache.org/dist/maven/maven-3/%maven_version%/binaries/apache-maven-%maven_version%-bin.zip

@REM Java环境
java -version >nul 2>&1
if %errorLevel% neq 0 (
  @REM 是否存在目录
  if not exist "%maven_dir%\" (
    @REM Java目录
    md "%java_dir%" >nul 2>&1
    if exist "%java_dir%\" (
      echo [✓] 创建目录: %java_dir%
    ) else (
      echo [✗] 创建目录: %java_dir%
    )
    @REM Maven目录
    md "%maven_dir%" >nul 2>&1
    if exist "%maven_dir%\" (
      echo [✓] 创建目录: %maven_dir%
    ) else (
      echo [✗] 创建目录: %maven_dir%
    )
  )
  @REM 下载JDK
  if not exist "%java_dir%\bin\java.exe" (
    echo [✓] 下载文件：%java_url%
    curl -L "%java_url%" -o java.zip
    @REM 解压文件
    echo [✓] 正在解压: java.zip
    powershell -Command "Expand-Archive -Path 'java.zip' -DestinationPath '%java_dir%' -Force"
    xcopy "%java_dir%\jdk-%java_version%\*" "%java_dir%\" /e /y >nul
    rmdir /s /q "%java_dir%\jdk-%java_version%" >nul
    echo [✓] 解压文件: java.zip 到 %java_dir%
    @REM 删除文件
    del java.zip >nul 2>&1
  )
  @REM 下载Maven
  if not exist "%maven_dir%\bin\mvn.exe" (
    echo [✓] 下载文件：%maven_url%
    curl -L "%maven_url%" -o maven.zip
    @REM 解压文件
    echo [✓] 正在解压: maven.zip
    powershell -Command "Expand-Archive -Path 'maven.zip' -DestinationPath '%maven_dir%' -Force"
    xcopy "%maven_dir%\apache-maven-%maven_version%\*" "%maven_dir%\" /e /y >nul
    rmdir /s /q "%maven_dir%\apache-maven-%maven_version%" >nul
    echo [✓] 解压文件: maven.zip 到 %maven_dir%
    @REM 删除文件
    del maven.zip >nul 2>&1
  )
  @REM 配置环境变量
  echo [✓] 安装成功：请手动添加环境变量
  echo JAVA_HOME %java_dir%
  echo PATH %java_dir%\bin
  echo MAVEN_HOME %maven_dir%
  echo PATH %maven_dir%\bin
  pause
  @REM 验证
  java -version >nul 2>&1
  if %errorLevel% neq 0 (
    @REM 临时环境变量
    set JAVA_HOME=%java_dir%
    set MAVEN_HOME=%maven_dir%
    set PATH=%PATH%;%java_dir%\bin;%maven_dir%\bin
    @REM 查看版本
    java -version
    mvn -v
  )
)

REM 运行
if "%s%"=="serve" (
  mvn spring-boot:run
REM 安装
) else if "%s%"=="install" (
  mvn clean && mvn compile
  echo [✓] 运行: .\cmd serve
REM 打包
) else if "%s%"=="build" (
  mvn package -DskipTests && del .\%name%%version%.jar && copy target\%name%.jar .\%name%%version%.jar
) else (
  echo ----------------------------------------------------
  echo [use] .\cmd ^<command^>
  echo ----------------------------------------------------
  echo ^<command^>
  echo   serve         运行: mvn tomcat7:run
  echo   install       安装依赖包: pom.xml
  echo   build         打包: mvn package -DskipTests
  echo ----------------------------------------------------
)