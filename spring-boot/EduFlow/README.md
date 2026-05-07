# EduFlow

🔥 A teaching management system based on Spring Boot + Vue3 + MySQL.  
🚀 Built for course management, member administration, enrollment workflows, and approval processes.  
⭐ Supports multi-role login, member/course CRUD, course enrollment, application/examination flow, and score management.

> 面向教务场景的课程与成员管理系统，包含前后端完整工程：后端负责鉴权与业务流程，前端提供管理员/教师/学生三类角色工作台。

## 1. 项目定位

`EduFlow` 用于教学管理业务的完整支撑：

- 后端代码位于 `teaching-manager-bk`
- 前端代码位于 `teaching-manager-ui/Pc/teaching-manager-pc-ui`

## 2. 已实现能力（前后端）

后端能力：
- 登录与登录校验（JWT）
- 用户密码修改
- 教师/学生/院系信息管理
- 课程管理（新增、更新、删除、状态切换）
- 学生选课、退课、已选课程查询
- 课程申请与课程审核流程
- CORS 与登录拦截器

前端能力：
- 管理员端：教师管理、学生管理、课程管理、课程审核
- 教师端：我的课程、课程申请、申请记录
- 学生端：选课中心、已选课程、退课
- 公共能力：登录鉴权、角色路由守卫、密码修改、统一请求封装

## 3. 技术栈

- Java 17
- Spring Boot 3.1.5
- MyBatis 3.x
- MySQL 8.x
- JWT（`io.jsonwebtoken`）
- Lombok / Fastjson
- Vue3 + Vite
- Element Plus + Pinia + Vue Router

## 4. 项目结构

```text
EduFlow
├── teaching-manager-bk/                        # Spring Boot 后端
│   ├── src/main/java/group/teachingmanagerbk
│   │   ├── controller/                         # Login/Course/Member/... 控制器
│   │   ├── service/                            # 业务服务层
│   │   ├── mapper/                             # MyBatis Mapper
│   │   ├── interceptor/                        # 登录拦截器
│   │   └── utils/                              # JWT 与统一返回结构
│   └── src/main/resources/application.properties
└── teaching-manager-ui/Pc/teaching-manager-pc-ui  # Vue3 前端
    ├── src/views/                                 # 管理员/教师/学生页面
    ├── src/api/                                   # 接口封装
    ├── src/router/                                # 角色路由与守卫
    └── src/stores/                                # 登录态管理
```

## 5. 本地启动（后端 + 前端）

### 5.1 环境

- JDK 17
- Maven 3.8+
- Docker（用于启动 MySQL）

### 5.2 一键流程（推荐）

```bash
cp .env.example .env
# 修改 .env 中口令配置，不能保留 change_me_*
# 若 DB_USERNAME=root，则 DB_PASSWORD 必须与 DB_ROOT_PASSWORD 一致
# 可通过 SERVER_PORT 配置后端端口（默认 8081）

./scripts/dev.sh check-env
./scripts/dev.sh all
```

`all` 会执行：

1. 校验 `.env` 是否存在且不含占位口令；
2. 通过 `docker-compose.dev.yml` 启动 MySQL；
3. 启动后端 `teaching-manager-bk`。

### 5.3 分步命令

```bash
./scripts/dev.sh infra-up
./scripts/dev.sh backend
./scripts/dev.sh infra-down
```

### 5.4 数据源说明

- 后端从环境变量读取数据库配置，映射在 [application.properties](teaching-manager-bk/src/main/resources/application.properties)：
  - `DB_URL`
  - `DB_USERNAME`
  - `DB_PASSWORD`
- 后端端口通过 `SERVER_PORT` 控制，默认 `8081`。
- 默认数据库名为 `teaching_manager`。
- `db/init.sql` 已包含完整建表与演示数据，可直接用于本地联调。

### 5.5 启动前端

```bash
cd teaching-manager-ui/Pc/teaching-manager-pc-ui
cp .env.example .env
npm install
npm run dev
```

- 默认开发端口：`5174`
- 默认代理：`/api -> http://127.0.0.1:8081`
- 可通过前端 `.env` 的 `VITE_PROXY_TARGET` 调整代理后端地址

## 6. 鉴权机制说明

- `/login`、`/check/login` 默认放行
- 其他接口走登录拦截，需在 `Authorization` 头中携带 JWT
- JWT 密钥通过环境变量 `TM_JWT_SECRET` 注入（建议长度 ≥ 32）
- 支持可选的 `TM_JWT_PREVIOUS_SECRET` 用于密钥轮换过渡期验签

## 7. 关键接口（示例）

- 登录：`POST /login`
- 成员管理：`/teachers`、`/students`、`/departments`
- 课程管理：`/insert/course`、`/update/course`、`/delete/course`
- 选课流程：`/student/select/course`、`/exit/course`
- 课程申请：`/apply/add/course`、`/all/application`
- 审核流程：`/wait/examination`、`/course/examination`

## 8. 当前仓库状态

- 后端与前端均可运行，支持完整教务流程演示
- 数据库初始化脚本已内置演示数据，适合本地快速联调

## 9. 开发建议

- 使用环境变量管理数据库与 JWT 配置
- 增加 Flyway/Liquibase 管理数据库迁移
- 增加统一异常处理与接口文档（OpenAPI）

## 10. 设计与实现思路

`EduFlow` 的实现遵循“先公共能力、后业务流程、再前后端闭环”的推进顺序：

1. 先完成登录、JWT 鉴权和拦截器，明确接口访问边界；
2. 再完成教师、学生、院系、课程等基础数据模块；
3. 完成选课、退课、课程申请、审批与成绩录入等流程型接口；
4. 补齐 Vue3 前端工作台，形成管理员/教师/学生角色闭环。

这种分阶段方式有两个直接好处：

- 公共能力先稳定，后续业务扩展时改动范围更可控；
- 前后端职责清晰，联调时能快速定位问题归属并迭代。

## 11. 关键难点与优化方向

### 11.1 关键难点

项目的难点主要不在单个 CRUD 接口，而在于多个流程之间的一致性：

- 登录态、接口权限和业务角色要保持统一；
- 课程管理、选课和审核流程之间要有清晰边界；
- 后端接口与前端页面要保持字段契约一致，减少联调摩擦。

### 11.2 当前处理方式

为此，当前实现采用了几项明确的约束：

- 使用 JWT 和拦截器统一处理登录态，而不是把鉴权逻辑分散到每个接口中；
- 按 `controller -> service -> mapper` 分层拆分课程、成员、申请、审核等逻辑，降低模块耦合；
- 将课程相关操作按“新增 / 修改 / 删除 / 申请 / 审核 / 选课”进行职责划分，避免流程膨胀后难以维护；
- 提前处理跨域与基础拦截逻辑，减少后续联调阶段的环境类问题。

### 11.3 后续优化方向

如果继续演进，优先级较高的方向包括：

- 增加数据库初始化脚本与迁移工具，降低部署和接手成本；
- 增加更细粒度的角色权限控制，而不是只依赖“已登录”判断；
- 补统一异常处理、接口文档和测试用例，提升工程完整度；
- 增强前端 E2E 测试与页面级错误边界处理能力。

## 12.1 贡献建议

欢迎通过 Issue / PR 提交：

- SQL 初始化脚本与演示数据
- 前端页面体验与联调说明优化
- 鉴权与权限粒度优化
- 自动化测试与 CI 配置

## 12.2 许可说明

本仓库采用 MIT License，详见 [LICENSE](LICENSE)。

## 13. 简历改造清单

- 改造追踪见 [docs/resume-upgrade-checklist.md](docs/resume-upgrade-checklist.md)
- 2D 架构升级说明见 [docs/architecture-upgrade-guide.md](docs/architecture-upgrade-guide.md)
- 开发环境模板见 [.env.example](.env.example)
- 本地数据库快速启动见 [docker-compose.dev.yml](docker-compose.dev.yml)
- RBAC 架构演进 SQL 草案见 [db/rbac-upgrade-schema.sql](db/rbac-upgrade-schema.sql)
- CI 配置见 [.github/workflows/ci.yml](.github/workflows/ci.yml)

本轮已落地：

- 选课状态限制（仅可选/待选）
- 选课时间冲突校验
- 两条核心规则的单元测试
- Vue3 前端工作台（管理员/教师/学生）
- 课程审核与成绩录入前端页面
- 数据库初始化脚本与演示数据
- 全局异常处理（ControllerAdvice）与业务异常体系
- RBAC 权限注解（@RequirePermission）与切面校验
- SpringDoc OpenAPI 文档接入（`/swagger-ui.html`）
- Flyway 迁移脚本与迁移配置
- 选课并发策略（悲观锁/乐观锁）可配置切换
