# TalentFlow HR：CI 与测试质量 30 条建议（已落地）

本文聚焦你要求的第一个项目（`talentflow-hr`），将 30 条建议与本次已完成改动一一对应，便于审阅和复盘。

| # | 建议 | 本次落地 |
|---|---|---|
| 1 | 为 PR 触发增加完整事件类型，避免漏跑 | `.github/workflows/talentflow-smoke.yml` 增加 `opened/synchronize/reopened/ready_for_review` |
| 2 | 增加并发组，防止旧任务占用资源 | 新增 `concurrency.group` |
| 3 | 允许新提交取消旧流水线，缩短反馈周期 | 新增 `cancel-in-progress: true` |
| 4 | 最小化权限，降低 CI 安全面 | 新增 `permissions: contents: read` |
| 5 | 后端任务设置超时，避免僵死 Job | `backend-tests.timeout-minutes: 25` |
| 6 | 前端任务设置超时，避免队列阻塞 | `frontend-tests.timeout-minutes: 20` |
| 7 | 后端统一时区，减少时间相关偶发失败 | `backend-tests.env.TZ=Asia/Shanghai` |
| 8 | 前端统一时区，保证行为一致 | `frontend-tests.env.TZ=Asia/Shanghai` |
| 9 | Maven 关闭冗余传输日志，提升可读性 | 测试命令增加 `-ntp` |
| 10 | Maven 显式传入 JVM 时区 | 测试命令增加 `-Duser.timezone=Asia/Shanghai` |
| 11 | 后端测试报告持久化，失败可追踪 | 新增 `Upload backend test reports` 步骤 |
| 12 | 明确 Surefire 报告归档路径 | 归档 `**/target/surefire-reports/*.xml/*.txt` |
| 13 | 前端安装依赖关闭审计与 fund 提示，降低噪音 | 使用 `npm ci --no-audit --no-fund` |
| 14 | 前端测试结果归档，便于 PR 审阅 | 新增 `Upload frontend test result`，归档 `tests/unit/results.json` |
| 15 | 固定 Node 版本，减少环境漂移 | 新增 `talentflow-ui/.nvmrc`（`16.18.1`） |
| 16 | 固定 npm registry，避免镜像差异 | 新增 `talentflow-ui/.npmrc` `registry=https://registry.npmjs.org/` |
| 17 | 启用 npm 严格 SSL，防止下载风险 | `.npmrc` 设置 `strict-ssl=true` |
| 18 | 关闭 fund 噪音，CI 日志更聚焦 | `.npmrc` 设置 `fund=false` |
| 19 | 关闭 audit 噪音，单测流水线更纯粹 | `.npmrc` 设置 `audit=false` |
| 20 | package 中声明 Node 版本约束 | `package.json` 新增 `engines.node=16.x` |
| 21 | package 中声明 npm 版本约束 | `package.json` 新增 `engines.npm=8.x` |
| 22 | 增加专用 CI 单测命令并产出 JSON | `package.json` 新增 `test:unit:ci` |
| 23 | 增加本地 watch 命令提升开发体验 | `package.json` 新增 `test:unit:watch` |
| 24 | 在 Jest 统一注册全局测试清理逻辑 | `jest.config.js` 新增 `setupFilesAfterEnv` |
| 25 | 每个测试后清理 `sessionStorage`，避免串扰 | `tests/unit/jest.setup.js` |
| 26 | 每个测试后清理 mock 状态，防止污染 | `tests/unit/jest.setup.js` |
| 27 | 登录/权限关联场景补负向断言 | `HrServiceTest#updateHrRoleShouldReturnFalseWhenInsertedCountNotMatch` |
| 28 | 密码修改补更新失败分支 | `HrServiceTest#updateHrPasswdShouldReturnFalseWhenUpdateCountNotOne` |
| 29 | 菜单权限在空菜单场景应可成功清理 | `MenuServiceTest#updateMenuRoleShouldReturnTrueWhenMidListIsEmpty` |
| 30 | 员工服务与认证集成补关键边界覆盖 | `EmployeeServiceTest` 新增 3 个用例；`AuthAndMenuIntegrationTest` 新增未登录/验证码错/密码错与二次菜单请求断言 |

## 说明

- 你要求的“后端业务单测、MySQL+Redis 集成测试（覆盖 `/doLogin` 与 `/system/config/menu`）、前端登录页与路由守卫单测、CI 跑测试”均已纳入本次落地。
- 除上表 30 条外，本轮还顺带对测试可维护性做了轻量重构（如 `loginRequest(...)` 复用），便于后续继续扩展用例。
