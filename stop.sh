#!/bin/bash

echo "======================================"
echo "⏹️  停止企业核心资产调度管理系统"
echo "======================================"
echo ""

# 停止Docker容器
echo "📦 停止MySQL容器..."
docker-compose stop

if [ $? -eq 0 ]; then
    echo "✅ MySQL容器已停止"
else
    echo "❌ 停止失败"
    exit 1
fi

echo ""
echo "======================================"
echo "✅ 停止完成"
echo "======================================"
echo ""
echo "提示："
echo "  - 重新启动: ./start.sh 或 docker-compose up -d"
echo "  - 删除容器: docker-compose down"
echo "  - 删除数据: docker-compose down -v"
echo ""

