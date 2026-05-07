# API 设计草案

## 1. 统一约定

- Base URL: `http://localhost:8080`
- Content-Type: `application/json`
- 认证头：`Authorization: Bearer <token>`
- 租户头（网关受保护接口）：`X-Tenant-Id: public`
- 返回结构：

```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": {},
  "timestamp": "2026-04-09T09:30:00Z"
}
```

## 2. 网关

### 2.1 健康探针

- `GET /api/gateway/status`
- 说明：网关状态检查（白名单）

## 3. 用户服务

### 3.1 登录

- `POST /api/users/login`
- 请求：

```json
{
  "username": "mall-admin",
  "password": "<bootstrap-admin-password>"
}
```

- 响应：

```json
{
  "success": true,
  "code": "OK",
  "message": "login success",
  "data": {
    "accessToken": "<jwt>",
    "tokenType": "Bearer",
    "expiresIn": "7200",
    "roles": "ROLE_ADMIN,ROLE_USER"
  },
  "timestamp": "2026-04-09T09:30:00Z"
}
```

### 3.2 用户详情

- `GET /api/users/{id}`
- 说明：查询用户信息

## 4. 商品服务

### 4.1 商品列表

- `GET /api/products?pageNo=1&pageSize=10`

### 4.2 商品详情

- `GET /api/products/{id}`

## 5. 订单服务

### 5.1 创建订单

- `POST /api/orders`

```json
{
  "userId": 1,
  "productId": 1001,
  "quantity": 1
}
```

### 5.2 查询订单

- `GET /api/orders/{orderNo}`

### 5.3 下单并支付

- `POST /api/orders/checkout`

```json
{
  "userId": 1,
  "productId": 1001,
  "quantity": 1,
  "amount": 399.00,
  "channel": "WECHAT_PAY"
}
```

## 6. 支付服务

### 6.1 支付确认

- `POST /api/payments/confirm`

```json
{
  "orderNo": "OD10001",
  "amount": 399.00,
  "channel": "WECHAT_PAY"
}
```

## 7. 错误码（建议）

- `UNAUTHORIZED`：鉴权失败
- `INVALID_CREDENTIALS`：用户名或密码错误
- `USER_NOT_FOUND`：用户不存在
- `PRODUCT_NOT_FOUND`：商品不存在
- `ORDER_NOT_FOUND`：订单不存在

## 8. OpenAPI

- 网关：`GET /v3/api-docs`
- 用户服务：`GET /v3/api-docs`
- 商品服务：`GET /v3/api-docs`
- 订单服务：`GET /v3/api-docs`
- 支付服务：`GET /v3/api-docs`
