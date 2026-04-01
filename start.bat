@echo off
REM ========================================
REM Public Health API 启动脚本
REM ========================================

echo ========================================
echo Public Health API 启动中...
echo ========================================

REM ========================================
REM 数据库配置 - 请根据实际情况修改
REM ========================================
set DB_URL=jdbc:mysql://localhost:3306/public_health?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
set DB_USERNAME=root
set DB_PASSWORD=43994399aawwddQ

REM ========================================
REM JWT配置 - 生产环境请务必修改
REM ========================================
set JWT_SECRET=public-health-api-secret-key-change-in-production-environment-min-32-chars
set JWT_ISSUER=public-health-api

REM ========================================
REM JVM参数配置
REM ========================================
set JVM_OPTS=-Xms512m -Xmx1024m

REM ========================================
REM 检查jar文件是否存在
REM ========================================
if not exist "target\public-health-api-0.0.1-SNAPSHOT.jar" (
    echo 错误: jar文件不存在，请先运行 mvn clean package -DskipTests
    pause
    exit /b 1
)

REM ========================================
REM 启动应用
REM ========================================
echo 正在启动应用...
echo 数据库: %DB_URL%
echo.

java %JVM_OPTS% -jar target\public-health-api-0.0.1-SNAPSHOT.jar

REM ========================================
REM 应用异常退出
REM ========================================
echo.
echo 应用已停止
pause
