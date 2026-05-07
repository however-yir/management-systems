# TalentFlow HR（中文说明）

🔥 基于 Spring Boot + Vue2 的人力资源管理系统。  
🚀 覆盖员工档案、组织与权限、薪资账套、站内通知、在线聊天和异步邮件。  
⭐ 已完成从旧项目命名到 `TalentFlow` 体系的目录级与模块级迁移。

---

## 目录

- [1. 项目定位](#1-项目定位)
- [2. 功能概览](#2-功能概览)
- [3. 目录级彻底改名结果](#3-目录级彻底改名结果)
- [4. 项目结构](#4-项目结构)
- [5. 快速启动](#5-快速启动)
- [6. 常用命令](#6-常用命令)
- [7. 配置说明](#7-配置说明)
- [8. 后续规划](#8-后续规划)

---

## 1. 项目定位

TalentFlow HR 是在 `vhr` 基础上进行品牌与工程化重构后的版本，当前重点在于：

- 产品名称统一：`TalentFlow HR`
- Java 包名统一：`io.howeveryir.talentflow`
- Maven 坐标统一：`talentflow-*`
- 后端目录与模块目录统一：`talentflow-*`
- 前端目录统一：`talentflow-ui`
- 环境配置统一为 `TF_*` 变量驱动

---

## 2. 功能概览

- 员工档案管理
- 部门 / 职称 / 职位 / 角色管理
- 薪资账套与员工账套配置
- 基于角色的动态菜单权限
- HR 在线聊天与通知入口
- RabbitMQ + 邮件服务异步入职邮件发送

---

## 3. 目录级彻底改名结果

| 原目录 | 新目录 |
|---|---|
| `vhr/` | `talentflow-platform/` |
| `vhr/vhrserver/` | `talentflow-platform/talentflow-server/` |
| `vhr/mailserver/` | `talentflow-platform/talentflow-mailserver/` |
| `vhr/vhrserver/vhr-model/` | `talentflow-platform/talentflow-server/talentflow-model/` |
| `vhr/vhrserver/vhr-mapper/` | `talentflow-platform/talentflow-server/talentflow-mapper/` |
| `vhr/vhrserver/vhr-service/` | `talentflow-platform/talentflow-server/talentflow-service/` |
| `vhr/vhrserver/vhr-web/` | `talentflow-platform/talentflow-server/talentflow-web/` |
| `vuehr/` | `talentflow-ui/` |

---

## 4. 项目结构

```text
.
├── talentflow-platform/
│   ├── pom.xml
│   ├── talentflow-mailserver/
│   └── talentflow-server/
│       ├── pom.xml
│       ├── talentflow-model/
│       ├── talentflow-mapper/
│       ├── talentflow-service/
│       └── talentflow-web/
├── talentflow-ui/
├── talentflow_hr.sql
├── .env.example
└── docker-compose.yml
```

---

## 5. 快速启动

### 5.1 环境要求

- JDK 8+
- Maven 3.8+
- Node.js 16+
- MySQL 8.0+
- Redis
- RabbitMQ

### 5.2 启动依赖（可选）

```bash
cp .env.example .env
./scripts/dev.sh all
```

### 5.3 导入数据库

```bash
./scripts/dev.sh db-init
```

### 5.4 加载环境变量

```bash
set -a
source .env
set +a
```

### 5.5 构建后端

```bash
cd talentflow-platform
mvn -ntp clean package
```

### 5.6 运行后端

终端 1：

```bash
./scripts/dev.sh backend-web
```

终端 2：

```bash
./scripts/dev.sh backend-mail
```

### 5.7 运行前端

```bash
./scripts/dev.sh frontend
```

---

## 6. 常用命令

```bash
# 后端编译
cd talentflow-platform
mvn -ntp compile

# 后端测试
cd talentflow-platform
mvn -ntp test

# 前端开发
cd talentflow-ui
npm run serve

# 前端打包
cd talentflow-ui
npm run build
```

---

## 7. 配置说明

主要配置文件：

- `talentflow-platform/talentflow-server/talentflow-web/src/main/resources/application.yml`
- `talentflow-platform/talentflow-mailserver/src/main/resources/application.properties`

核心环境变量前缀：

- `TF_DB_*`
- `TF_REDIS_*`
- `TF_RABBITMQ_*`
- `TF_MAIL_*`
- `TF_SERVER_PORT`
- `TF_MAIL_SERVER_PORT`
- `TF_STORAGE_PUBLIC_BASE_URL`

---

## 8. 后续规划

1. Java 升级到 17，并完成 Spring Boot 版本演进。
2. 前端迁移到 Vue3 + Vite。
3. 为权限、员工资料、薪资、邮件重试链路补齐集成测试。
4. 清理或重建后端 `static/` 下的历史前端产物。
