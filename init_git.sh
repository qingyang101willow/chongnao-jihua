#!/bin/bash
set -e
cd /data/data/com.dshmobile.dsh_mobile/files/workspace/fabric_template
git init
git add .
git commit -m "Initial: Fabric 1.21.1 template with GitHub Actions CI"
git branch -M main
# TODO: 用户需提供 GitHub token 并设置 remote
echo ""
echo "=== 项目已初始化 ==="
echo "请执行以下命令推送到 GitHub:"
echo "  git remote add origin https://github.com/用户名/虫巢计划.git"
echo "  git push -u origin main"
echo ""
echo "或在 GitHub 网页创建仓库后执行上述命令。"
echo ""
echo "CI 配置已就绪：/.github/workflows/build.yml"
echo "推送后 GitHub Actions 将自动构建 JAR 包。"
