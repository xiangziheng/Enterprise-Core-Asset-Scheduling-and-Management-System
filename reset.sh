#!/bin/bash

echo "======================================"
echo "🔄 重置企业核心资产调度管理系统"
echo "======================================"
echo ""

read -p "⚠️  这将删除所有数据库数据，确定继续？(y/N) " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ 操作已取消"
    exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SCHEMA_FILE="$SCRIPT_DIR/src/main/resources/schema.sql"

echo ""
echo "🗑️  删除容器和数据..."
docker-compose down -v

if [ $? -ne 0 ]; then
    echo "❌ 删除失败"
    exit 1
fi

echo "✅ 删除成功"
echo ""

echo "🚀 重新创建并启动容器..."
docker-compose up -d

if [ $? -ne 0 ]; then
    echo "❌ 启动失败"
    exit 1
fi

echo "✅ 启动成功"
echo ""

echo "⏳ 等待MySQL初始化（约30秒）..."
sleep 10

for i in {1..20}; do
    if docker-compose exec -T mysql mysqladmin ping -uroot -proot &> /dev/null; then
        echo "✅ MySQL已就绪"
        break
    fi
    echo "   初始化中... ($i/20)"
    sleep 2
done

echo ""
echo "📥 初始化数据库（UTF-8）..."
docker-compose exec -T mysql mysql -uroot -proot --default-character-set=utf8mb4 < "$SCHEMA_FILE"

if [ $? -ne 0 ]; then
    echo "❌ 初始化失败"
    exit 1
fi

echo "✅ 初始化完成"
echo ""

echo ""
echo "======================================"
echo "✅ 重置完成！"
echo "======================================"
echo ""
echo "数据库已重置为初始状态"
echo "可以运行 mvn spring-boot:run 启动应用"
echo ""
