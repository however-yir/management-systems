# Management Systems

一个覆盖企业信息化全场景的**管理系统合集**，包含 Java Spring Boot 和 Python Django 两个技术栈共 10 个子项目。每个子目录是独立可运行的完整管理系统。

> 本仓库是企业管理系统的学习与参考集合，各子项目之间相互独立，不依赖任何统一平台或框架。

## 目录结构

```
management-systems/
├── spring-boot/            # Java 管理系统（8 个项目）
│   ├── talentflow-hr/      # HR 人力资源管理系统
│   ├── DormLink/           # 宿舍管理系统
│   ├── EduFlow/            # 教务管理系统
│   ├── partyhub/           # 党建管理系统
│   ├── CodeTrainer/        # 编程培训考试系统
│   ├── StudentCore/        # Java Swing 学生管理系统
│   ├── aurora-mall/        # Spring Boot 电商（单体）
│   └── cloud-native-mall/  # Spring Cloud 电商（微服务）
└── django/                 # Python 管理系统（2 个项目）
    ├── StaffBase/          # 员工与工单管理系统
    └── ai_chatbot/         # Django 智能对话系统
```

---

## Java 管理系统（Spring Boot）

### [TalentFlow](./spring-boot/talentflow-hr/) — HR 人力资源管理系统

Spring Boot + Vue 全栈 HR 管理平台，覆盖组织架构管理、员工信息维护、工作流审批与数据报表。

**技术栈**：Spring Boot + Vue + MyBatis + JWT + MySQL

---

### [DormLink](./spring-boot/DormLink/) — 宿舍管理系统

Spring Boot + Vue3 宿舍管理平台，支持多角色权限控制、入住退宿流程、报修管理与调整审批。

**技术栈**：Spring Boot + Vue3 + Element Plus + MyBatis-Plus + MySQL

---

### [EduFlow](./spring-boot/EduFlow/) — 教务管理系统

Spring Boot 教务管理后端，包含课程编排、选课审批流程、JWT 认证与教学资源管理。

**技术栈**：Spring Boot + MyBatis + JWT + MySQL

---

### [PartyHub](./spring-boot/partyhub/) — 党建管理系统

Spring Boot 党建管理平台，涵盖党员信息管理、支部活动组织、积分考核与反馈统计。

**技术栈**：Spring Boot + MyBatis + MySQL + Layui

---

### [CodeTrainer](./spring-boot/CodeTrainer/) — 编程培训考试系统

Spring Boot 编程培训平台，包含题库管理、在线考试、成绩统计与社区论坛模块。

**技术栈**：Spring Boot + Vue + MyBatis-Plus + Shiro + MySQL

---

### [StudentCore](./spring-boot/StudentCore/) — Java Swing 学生管理系统

Java Swing 桌面学生信息管理系统，涵盖学生/课程/成绩的增删改查、参数化 SQL 与 BCrypt 密码加密。

**技术栈**：Java Swing + JDBC + MySQL

---

### [Aurora Mall](./spring-boot/aurora-mall/) — Spring Boot 电商系统（单体）

Spring Boot 单体电商应用，包含商品管理、购物车、订单、用户与轮播图等完整电商模块。

**技术栈**：Spring Boot + Thymeleaf + MyBatis + MySQL

---

### [Cloud Native Mall](./spring-boot/cloud-native-mall/) — Spring Cloud 电商系统（微服务）

Spring Cloud 微服务电商系统，包含网关、用户、商品、订单、支付 5 大服务，支持 Docker Compose 本地编排与 K8s 生产部署。

**技术栈**：Spring Cloud + Spring Gateway + JWT + Flyway + MySQL + K8s

---

## Python 管理系统（Django）

### [StaffBase](./django/StaffBase/) — 员工与工单管理系统

基于 Django + MySQL 的企业人员管理系统，覆盖组织部门、员工信息、任务与订单、可视化统计，以及 IoT 设备与数据展示模块。

**技术栈**：Python + Django 3.1.x + MySQL + Bootstrap + Pillow

---

### [AI Chatbot](./django/ai_chatbot/) — Django 智能对话系统

基于 Django + MySQL 的聊天机器人系统，包含用户管理、问答记录、后台运维，并提供可选的深度学习对话子模块（Seq2Seq/Attention）。

**技术栈**：Django 3.2.x + Python 3.x + MySQL 8.x + Layui + TensorFlow 1.x（可选）

---

## 快速启动

每个子目录都是独立可运行的完整项目：

```bash
cd spring-boot/talentflow-hr/   # 或其他子目录
# 按对应子目录内的 README 启动
```

## 技术共性

- **Java 后端**: Spring Boot, MyBatis / MyBatis-Plus, JWT 认证
- **Python 后端**: Django 3.x, MySQL
- **前端**: Vue 2/3, Element UI / Element Plus, Thymeleaf, Bootstrap, Layui
- **数据库**: MySQL
- **部署**: 支持 Docker / K8s / 传统部署

## Contributing

欢迎贡献！请阅读 [CONTRIBUTING.md](./CONTRIBUTING.md) 了解详情。

本项目采用 [MIT License](./LICENSE)。

---

> 原 `springboot-management-systems` 与 `django-management-systems` 已合并归档到本仓库。
