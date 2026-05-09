# Contributing to Management Systems

感谢你对本项目的关注！以下是参与贡献的指南。

## 如何贡献

### 报告 Bug

1. 在 Issues 中搜索是否已有相同问题
2. 创建新 Issue，选择 **Bug Report** 模板
3. 提供完整的复现步骤、环境信息和错误日志

### 提交功能请求

1. 创建新 Issue，选择 **Feature Request** 模板
2. 说明使用场景和期望的行为

### 提交代码

1. Fork 本仓库并创建你的分支
2. 在对应子项目目录下进行修改
3. 确保代码能通过本地构建：
   - **Spring Boot 项目**：`mvn -B compile`
   - **Django 项目**：`python manage.py check`
4. 提交 Pull Request，使用 PR 模板填写变更说明

## 项目结构约定

每个子目录是一个独立项目，遵循各自技术栈的编码规范：

- `spring-boot/` — Java 17, Maven, Spring Boot 编码规范
- `django/` — Python 3.10, Django 编码规范

## 提交信息规范

建议使用以下前缀：

- `feat:` 新功能
- `fix:` Bug 修复
- `docs:` 文档更新
- `refactor:` 重构（不改变功能）
- `chore:` 构建/工具链变更

## 行为准则

请阅读 [CODE_OF_CONDUCT.md](./CODE_OF_CONDUCT.md)。
