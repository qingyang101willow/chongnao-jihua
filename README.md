# 虫巢计划 - GitHub Actions 配置示例

本项目演示如何在 GitHub Actions 中配置 Fabric Minecraft 模组构建流程。

## 快速开始

1. Fork 本仓库
2. 复制 `.github/workflows/build.yml` 到你的项目
3. 修改 `gradle.properties` 中的版本号
4. Push 代码触发自动构建

## 文件说明

- `.github/workflows/build.yml` - GitHub Actions 工作流配置
- `gradle/wrapper/` - Gradle Wrapper 文件
- `build.gradle` - Gradle 构建脚本
- `gradle.properties` - 项目属性配置

## 构建产物

构建成功后，JAR 文件可在 Actions Artifacts 中下载。
