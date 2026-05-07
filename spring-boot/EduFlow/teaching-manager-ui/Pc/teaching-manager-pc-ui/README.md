# EduFlow Frontend (PC)

前端技术栈：`Vue3 + Vite + Element Plus + Pinia + Vue Router`

## 1. 功能覆盖

- 登录鉴权（管理员/教师/学生）
- 管理员端：教师管理、学生管理、课程管理、课程审核
- 教师端：我的课程、课程申请、申请记录
- 学生端：选课中心、已选课程
- 个人设置：密码修改

## 2. 本地启动

```bash
cp .env.example .env
npm install
npm run dev
```

默认开发端口：`5174`

`vite` 已内置代理：`/api -> http://127.0.0.1:8080`

## 3. 构建

```bash
npm run build
npm run preview
```

## 4. 接口约定

- 后端统一返回格式：`{ code, message, data }`
- `code = 1` 视为成功
- JWT 自动写入 `Authorization` 请求头

## 5. 联调建议

先在仓库根目录启动后端：

```bash
./scripts/dev.sh all
```

然后在本目录启动前端：

```bash
npm run dev
```
