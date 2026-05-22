# Management Systems

企业管理系统案例集，覆盖 Java Spring Boot / Spring Cloud、Java Swing 和 Python Django 三条技术线。仓库内 10 个子项目彼此独立，定位从“项目堆叠”收束为可筛选、可运行、可验证的企业管理系统案例。

## 子项目总览

| 子项目 | 业务场景 | 技术栈 | 运行方式 | 状态 | 推荐指数 |
| --- | --- | --- | --- | --- | --- |
| [talentflow-hr](./spring-boot/talentflow-hr/) | HR 人力资源、组织架构、审批与报表 | Spring Boot + Vue + MyBatis + MySQL | `cd spring-boot/talentflow-hr && ./scripts/dev.sh all` | 主推案例 | 5/5 |
| [cloud-native-mall](./spring-boot/cloud-native-mall/) | 云原生电商、网关、用户、商品、订单、支付 | Spring Cloud + Gateway + JWT + Docker/K8s | `cd spring-boot/cloud-native-mall && ./scripts/build-all.sh` | 主推案例 | 5/5 |
| [aurora-mall](./spring-boot/aurora-mall/) | 单体电商、商城前台、后台管理 | Spring Boot + Thymeleaf + MyBatis + MySQL | `cd spring-boot/aurora-mall && ./scripts/dev.sh all-local` | 主推案例 | 5/5 |
| [DormLink](./spring-boot/DormLink/) | 宿舍入住、调宿、报修、访客与公告 | Spring Boot + Vue3 + MyBatis-Plus + MySQL | `cd spring-boot/DormLink && ./scripts/dev.sh backend` | sample | 4/5 |
| [EduFlow](./spring-boot/EduFlow/) | 教务课程、选课、审批、成绩管理 | Spring Boot + Vue3 + MyBatis + JWT + MySQL | `cd spring-boot/EduFlow && ./scripts/dev.sh all` | sample | 4/5 |
| [partyhub](./spring-boot/partyhub/) | 党建事务、评星定级、统计分析 | Spring Boot + Spring Security + MyBatis-Plus + Redis | `cd spring-boot/partyhub && mvn spring-boot:run` | sample | 4/5 |
| [StaffBase](./django/StaffBase/) | 员工、部门、任务、订单与 IoT 看板 | Django + MySQL + Bootstrap + Pillow | `cd django/StaffBase/djangoProject && python manage.py runserver 0.0.0.0:8000` | sample | 3/5 |
| [CodeTrainer](./spring-boot/CodeTrainer/) | 编程训练、题库、考试与论坛 | Spring Boot 2.2 + MyBatis-Plus + Shiro + Vue2 | `cd spring-boot/CodeTrainer/springbootx1786 && mvn spring-boot:run` | archived learning project | 2/5 |
| [StudentCore](./spring-boot/StudentCore/) | 桌面端学生、课程、成绩管理 | Java Swing + JDBC + MySQL | `cd spring-boot/StudentCore/完整源码/demo6 && ./run.sh` | archived learning project | 2/5 |
| [ai_chatbot](./django/ai_chatbot/) | Web 聊天、用户、问答记录与后台运维 | Django + MySQL + Layui + TensorFlow 1.x 可选 | `cd django/ai_chatbot && python manage.py runserver 8000` | archived learning project | 2/5 |

> 主推案例用于作品集和持续工程化迭代；`sample` 适合作为业务场景参考；`archived learning project` 保留学习价值，运行前建议先看子项目 README 的环境说明。

## 目录结构

```text
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

## 快速启动

以下命令默认从仓库根目录执行。Maven、StudentCore 和 Django 项目采用不同验证方式，路径与 [.github/workflows/ci.yml](./.github/workflows/ci.yml) 保持一致。

### Maven / Spring Boot 项目

| 子项目 | CI 编译命令 | 本地启动入口 |
| --- | --- | --- |
| talentflow-hr | `mvn -B compile -f spring-boot/talentflow-hr/talentflow-platform/pom.xml` | `cd spring-boot/talentflow-hr && cp .env.example .env && ./scripts/dev.sh backend-web` |
| DormLink | `mvn -B compile -f spring-boot/DormLink/Dormitory_business/pom.xml` | `cd spring-boot/DormLink && cp .env.example .env && ./scripts/dev.sh backend` |
| EduFlow | `mvn -B compile -f spring-boot/EduFlow/teaching-manager-bk/pom.xml` | `cd spring-boot/EduFlow && cp .env.example .env && ./scripts/dev.sh all` |
| partyhub | `mvn -B compile -f spring-boot/partyhub/pom.xml` | `cd spring-boot/partyhub && cp .env.example .env && mvn spring-boot:run` |
| CodeTrainer | `mvn -B compile -f spring-boot/CodeTrainer/springbootx1786/pom.xml` | `cd spring-boot/CodeTrainer/springbootx1786 && mvn spring-boot:run` |
| aurora-mall | `mvn -B compile -f spring-boot/aurora-mall/pom.xml` | `cd spring-boot/aurora-mall && cp .env.example .env && ./scripts/dev.sh all-local` |
| cloud-native-mall | `mvn -B compile -f spring-boot/cloud-native-mall/pom.xml` | `cd spring-boot/cloud-native-mall && cp .env.example .env && ./scripts/build-all.sh` |

### StudentCore（非 Maven）

| 子项目 | CI 编译命令 | 本地启动入口 |
| --- | --- | --- |
| StudentCore | `cd spring-boot/StudentCore/完整源码/demo6 && CP=$(find lib -name "*.jar" \| tr '\n' ':') && find src -name "*.java" > sources.txt && javac -cp "$CP" @sources.txt` | `cd spring-boot/StudentCore/完整源码/demo6 && ./run.sh` |

### Django 项目

CI 中 Django 检查会执行 `manage.py check`，并将依赖、数据库或环境变量缺失导致的非零结果降级为 warning。完整本地检查请按子项目 README 安装 requirements，并配置 `.env` 和 MySQL。

| 子项目 | CI 检查路径 | 本地启动入口 |
| --- | --- | --- |
| StaffBase | `cd django/StaffBase/djangoProject && python manage.py check` | `cd django/StaffBase/djangoProject && python manage.py runserver 0.0.0.0:8000` |
| ai_chatbot | `cd django/ai_chatbot && python manage.py check` | `cd django/ai_chatbot && python manage.py runserver 8000` |

## 技术共性

- Java 后端：Spring Boot, Spring Cloud, MyBatis / MyBatis-Plus, JWT 认证
- Python 后端：Django 3.x, MySQL
- 前端：Vue 2/3, Element UI / Element Plus, Thymeleaf, Bootstrap, Layui
- 数据库：MySQL
- 部署：Docker Compose / K8s / 传统本地部署

## Contributing

欢迎贡献！请阅读 [CONTRIBUTING.md](./CONTRIBUTING.md) 了解详情。

本项目采用 [MIT License](./LICENSE)。

> 原 `springboot-management-systems` 与 `django-management-systems` 已合并归档到本仓库。
