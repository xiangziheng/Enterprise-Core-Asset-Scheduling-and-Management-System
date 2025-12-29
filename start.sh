#!/bin/bash

echo "======================================"
echo "🚀 企业核心资产调度管理系统 - 启动脚本"
echo "======================================"
echo ""

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ 错误：未检测到Docker，请先安装Docker"
    exit 1
fi

# 检查docker-compose是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "❌ 错误：未检测到docker-compose，请先安装Docker Compose"
    exit 1
fi

echo "✅ Docker环境检测通过"
echo ""

# 启动MySQL容器
echo "📦 启动MySQL容器..."
docker-compose up -d

if [ $? -ne 0 ]; then
    echo "❌ MySQL容器启动失败"
    exit 1
fi

echo "✅ MySQL容器启动成功"
echo ""

# 等待MySQL启动
echo "⏳ 等待MySQL完全启动（约20秒）..."
sleep 5

for i in {1..15}; do
    if docker-compose exec -T mysql mysqladmin ping -uroot -proot &> /dev/null; then
        echo "✅ MySQL已就绪"
        break
    fi
    echo "   等待中... ($i/15)"
    sleep 2
done

echo ""
echo "======================================"
echo "✅ MySQL启动完成！"
echo "======================================"
echo ""
echo "连接信息："
echo "  Host: localhost"
echo "  Port: 3306"
echo "  Username: root"
echo "  Password: root"
echo "  Database: asset_management"
echo ""
echo "======================================"
echo "🎯 启动Spring Boot应用"
echo "======================================"
echo ""
echo "执行: mvn spring-boot:run"
echo ""
echo "提示：如需停止MySQL容器，请运行: docker-compose stop"
echo ""

# 启动Spring Boot应用
mvn spring-boot:run

