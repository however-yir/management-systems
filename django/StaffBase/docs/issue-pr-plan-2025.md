# 2025 Issue and PR Plan

## Project Complexity
- medium

## Milestone Strategy
- M1(1-3月): 初始化与首批核心能力
- M2(4-7月): 核心扩展
- M3(8-10月): 修复与测试
- M4(11-12月): 文档与部署

## Issue Backlog
| # | Title | Labels | Milestone | Planned |
|---|---|---|---|---|
| #1 | 初始化员工域服务与目录规范 | type:feature,priority:P2,area:init | M1(1-3月) | 2025-01 |
| #2 | 建立角色与组织基础数据种子 | type:feature,priority:P2,area:init | M1(1-3月) | 2025-01 |
| #3 | 实现员工档案与组织树查询 | type:feature,priority:P1,area:core | M1(1-3月) | 2025-03 |
| #4 | 实现 RBAC 与薪资接口保护 | type:feature,priority:P2,area:core | M2(4-7月) | 2025-05 |
| #5 | 实现考勤导入与查询能力 | type:feature,priority:P2,area:core | M2(4-7月) | 2025-06 |
| #6 | 修复非 HR 角色薪资字段泄漏 | type:bug,priority:P1,area:bugfix | M3(8-10月) | 2025-08 |
| #7 | 补齐权限矩阵与集成测试 | type:test,priority:P2,area:test | M3(8-10月) | 2025-09 |
| #8 | 完善管理手册与部署回滚说明 | type:docs,priority:P3,area:deploy | M4(11-12月) | 2025-11 |

## PR Cadence
- PR1 Foundation Setup: Closes #1, #2
- PR2 Core Capability A: Closes #3
- PR3 Core Capability B: Closes #4, #5
- PR4 Bugfix & Test: Closes #6, #7
- PR5 Docs & Deploy: Closes #8

## Standard PR Template Fields
- Summary
- Linked Issue
- Test Evidence
- Risk
- Rollback
