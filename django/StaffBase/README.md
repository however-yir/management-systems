# StaffBase - 员工与工单管理系统 | Staff and Work Order Management System

🔥 A Django staff management system based on Python, MySQL, and dashboard-style administration.  
🚀 Built for department, employee, task, order, chart, and IoT data management scenarios.  
⭐ Supports admin workflows, statistics views, device-related pages, and enterprise back-office operations.

> 基于 Django + MySQL 的企业人员管理系统，覆盖组织部门、员工信息、任务与订单、可视化统计，以及 IoT 设备与数据展示模块。

## 统一案例入口

> 以下命令默认从 `management-systems` 仓库根目录执行。

- 定位：Django 员工与工单管理 sample，覆盖组织、员工、任务、订单、图表和 IoT 看板。
- 技术栈：Django 3.1.x + MySQL + Bootstrap + Pillow。
- 启动命令：`cd django/StaffBase/djangoProject && python manage.py runserver 0.0.0.0:8000`
- 验证命令：`cd django/StaffBase && pip install -r djangoProject/requirements.txt && cd djangoProject && python manage.py check`
- 截图/接口入口：登录入口 `http://127.0.0.1:8000/login/`；核心路由包括 `/depart/*`、`/user/*`、`/task/*`、`/order/*`、`/chart/*`。

## 1. 项目定位

`StaffBase` 是一个偏“管理后台 + 数据看板”的综合示例系统，核心是企业人员信息管理，并扩展了订单、任务、图表与设备管理能力。

## 2. 已实现功能

- 登录认证（含图形验证码）
- 管理员、部门、员工、靓号管理
- 任务管理（AJAX）
- 订单管理（增删改查与详情）
- 图表统计（柱状图/饼图/折线图）
- IoT 数据展示（多类传感器数据）
- 设备管理与命令下发页面

## 3. 技术栈

- Python + Django 3.1.x
- MySQL
- Bootstrap + Django Template
- Pillow（验证码图片）
- 自定义中间件（登录鉴权）

## 4. 项目结构

```text
StaffBase
├── djangoProject/
│   ├── manage.py
│   ├── djangoProject/settings.py
│   └── app01/
│       ├── models.py
│       ├── views/                   # account/user/depart/order/iot/chart...
│       ├── middleware/auth.py
│       ├── templates/
│       └── utils/
├── 数据库脚本/bighousework.sql
└── README.md
```

## 5. 本地运行

### 5.1 环境准备

- Python 3.8+
- MySQL 8.x

### 5.2 安装依赖

使用仓库内依赖清单安装：

```bash
pip install -r djangoProject/requirements.txt
```

### 5.3 初始化数据库

1. 创建数据库：`bighousework`
2. 导入 [bighousework.sql](数据库脚本/bighousework.sql)
3. 复制环境变量模板：`cp djangoProject/.env.example djangoProject/.env`
4. 在 `djangoProject/.env` 中填写 `DJANGO_SECRET_KEY` 和 `DB_PASSWORD`

### 5.4 启动

```bash
cd djangoProject
python manage.py runserver 0.0.0.0:8000
```

访问：`http://127.0.0.1:8000/login/`

## 6. 默认账号

根据数据库脚本与加密逻辑（`md5(SECRET_KEY + password)`）：

- 管理员账号：`admin`
- 默认密码：`123456`

## 7. 关键路由示例

- 组织与员工：`/depart/*`、`/user/*`
- 管理员：`/admin/*`
- 任务与订单：`/task/*`、`/order/*`
- 图表：`/chart/*`
- IoT：`/iot/*`、`/equipment/*`、`/mqtt/view/`

## 8. 安全与改进建议

- 将数据库密码迁移到环境变量
- 对 IoT 命令下发接口增加权限与审计
- 增加接口层的参数校验与异常统一处理

## 9. 常见问题

- 登录失败：确认验证码输入正确且会话未超时
- 数据库连接失败：检查 MySQL 端口、账号和数据库名
- 静态资源异常：确认 Django 静态文件配置与模板路径

## 12.1 贡献建议

欢迎通过 Issue / PR 提交：

- 文档勘误与部署脚本完善
- IoT 模块协议与设备模拟器扩展
- 统计图接口性能优化
- 自动化测试补充

## 12.2 许可说明

本仓库采用 MIT License，详见 [LICENSE](LICENSE)。
