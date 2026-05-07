# 项目完善建议（30 条）与实施跟踪

## 已实施（本轮）

1. [x] 新增统一错误码 `ErrorCode`，避免业务层散落字符串错误码。
2. [x] 新增 `GlobalExceptionHandler`，统一处理参数校验、业务异常和系统异常。
3. [x] 新增 `BusinessException` 统一业务异常模型。
4. [x] 登录逻辑改为 BCrypt 校验，移除明文密码直接比对。
5. [x] 用户角色体系完善（`ROLE_ADMIN` / `ROLE_USER`），登录返回角色信息。
6. [x] 登录响应新增 `expiresIn`，前端可感知 token 时效。
7. [x] JWT 解析增加 issuer 校验，防止跨系统 token 误用。
8. [x] JWT secret 增加最小长度校验（至少 32 字节）。
9. [x] JWT secret 支持显式 `base64-secret` 配置，去除猜测式判断。
10. [x] 网关透传 `X-User-Roles`，为下游细粒度鉴权预留。
11. [x] 网关白名单匹配增强，空配置时行为更安全。
12. [x] 商品列表改为真正分页，避免全量返回。
13. [x] API 参数校验增强（`@Min/@Max/@Positive/@Pattern`）。
14. [x] 服务配置改为环境变量优先，便于 dev/staging/prod 分层。
15. [x] 各服务启用 readiness/liveness health probes。
16. [x] 增加日志级别输出 traceId 约定格式。
17. [x] 新增 GitHub Actions `maven-ci` 工作流。
18. [x] 新增 `scripts/smoke-test.sh` 一键联调冒烟脚本。
19. [x] 新增 `JwtTokenService` 单元测试。
20. [x] 新增商品分页逻辑单元测试。

## 已实施（第二阶段）

21. [x] Gateway 路由已切换 `lb://service-name`，并接入 Eureka 服务发现（保留 Nacos 扩展位）。
22. [x] 订单链路接入 Seata AT（`@GlobalTransactional` + `seata` 配置）。
23. [x] 引入 OpenTelemetry 依赖并对接 Jaeger OTLP（`docker-compose` 新增 Jaeger）。
24. [x] 接入 Spring Cloud Config Client（`spring.config.import` + 环境变量治理）。
25. [x] 订单与支付服务完成 JPA 持久化 + Flyway 初始化脚本。
26. [x] 商品服务新增 Redis 防护（空值缓存、互斥锁、过期抖动）。
27. [x] 网关增加租户头校验、IP/租户黑白名单与租户维度限流键。
28. [x] 增加 OpenAPI 暴露与关键接口契约测试（user/order）。
29. [x] 增加 Testcontainers 集成测试（MySQL + Redis + Elasticsearch）。
30. [x] 增加 release 流水线（构建、Trivy 扫描、canary 清单校验与部署步骤）。
