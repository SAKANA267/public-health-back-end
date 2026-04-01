#!/bin/bash
# ========================================
# Public Health API 启动脚本 (Linux/Mac)
# ========================================

echo "========================================"
echo "Public Health API 启动中..."
echo "========================================"

# ========================================
# 数据库配置 - 请根据实际情况修改
# ========================================
export DB_URL="jdbc:mysql://localhost:3306/public_health?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true"
export DB_USERNAME="root"
export DB_PASSWORD="43994399aawwddQ"

# ========================================
# JWT配置 - 生产环境请务必修改
# ========================================
export JWT_SECRET="public-health-api-secret-key-change-in-production-environment-min-32-chars"
export JWT_ISSUER="public-health-api"

# ========================================
# JVM参数配置
# ========================================
JVM_OPTS="-Xms512m -Xmx1024m"

# ========================================
# 检查jar文件是否存在
# ========================================
JAR_FILE="target/public-health-api-0.0.1-SNAPSHOT.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "错误: jar文件不存在，请先运行 ./mvnw clean package -DskipTests"
    exit 1
fi

# ========================================
# 启动应用
# ========================================
echo "正在启动应用..."
echo "数据库: $DB_URL"
echo ""

java $JVM_OPTS -jar $JAR_FILE
