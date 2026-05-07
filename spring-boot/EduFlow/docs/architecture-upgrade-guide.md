# 2D 架构与工程化升级指南

## 2D.2 核心改造清单

| 优先级 | 改造项 | 本仓库落地情况 |
| --- | --- | --- |
| P0 | SQL 初始化脚本 | `db/init.sql` 已补齐完整建表与测试数据 |
| P0 | 统一异常处理 | 新增 `GlobalExceptionHandler` 与业务异常体系 |
| P1 | RBAC 权限细化 | 新增 `@RequirePermission` + `PermissionAspect` |
| P1 | 接口文档 | 已接入 SpringDoc OpenAPI（`/swagger-ui.html`） |
| P1 | 选课冲突处理 | 支持悲观锁/乐观锁两种策略对比 |
| P2 | Flyway 迁移 | 已接入 Flyway + `V1__baseline_schema.sql` |

## 2D.3 已落地能力说明

### SQL 初始化

- 根目录脚本：`db/init.sql`
- Flyway 脚本：`teaching-manager-bk/src/main/resources/db/migration/V1__baseline_schema.sql`

### 统一异常处理

- `BusinessException`：通用业务异常，包含业务码
- `EnrollmentConflictException`：选课冲突专用异常（重复选课/人数已满/并发冲突）
- `GlobalExceptionHandler`：统一处理业务异常、参数校验异常与系统异常

### RBAC 权限模型

- 注解：`@RequirePermission`
- 切面：`PermissionAspect`
- 角色常量：`ROLE_ADMIN` / `ROLE_TEACHER` / `ROLE_STUDENT` / `ROLE_ALL`

### OpenAPI 文档

- Swagger UI：`/swagger-ui.html`
- OpenAPI JSON：`/v3/api-docs`

### 选课并发控制

通过配置切换锁策略：

```properties
eduflow.enrollment-lock-mode=PESSIMISTIC
# or OPTIMISTIC
```

- `PESSIMISTIC`：`SELECT ... FOR UPDATE` 锁课程行后执行选课
- `OPTIMISTIC`：通过 `version` 字段执行 CAS 更新，冲突则回滚

### Flyway 迁移

- 开启配置：`spring.flyway.enabled=true`
- 基线策略：`spring.flyway.baseline-on-migrate=true`
- 迁移目录：`classpath:db/migration`
