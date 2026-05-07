# 部署说明

## 1. 本地运行

### 1.1 启动基础设施

```bash
cd docker
docker compose up -d
```

### 1.2 构建项目

```bash
cp .env.example .env
./scripts/build-all.sh
```

### 1.3 启动服务（示例）

```bash
mvn -pl mall-user spring-boot:run
mvn -pl mall-product spring-boot:run
mvn -pl mall-order spring-boot:run
mvn -pl mall-payment spring-boot:run
mvn -pl mall-gateway spring-boot:run
```

### 1.4 冒烟检查

```bash
./scripts/smoke-test.sh
```

## 2. Kubernetes 部署

先创建 JWT Secret（根据模板改值）：

```bash
cp k8s/deployment/mall-jwt-secret.example.yaml k8s/deployment/mall-jwt-secret.yaml
kubectl apply -f k8s/deployment/mall-jwt-secret.yaml
```

再部署服务：

```bash
kubectl apply -f k8s/deployment
kubectl apply -f k8s/service
kubectl apply -f k8s/ingress
```

灰度发布（Gateway canary）：

```bash
./scripts/deploy-canary.sh
```

## 3. 观测入口

- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`
- Kibana: `http://localhost:5601`
- Jaeger: `http://localhost:16686`

## 4. CI/CD 发布

- 持续集成：`.github/workflows/maven-ci.yml`
- 发布流水线：`.github/workflows/release.yml`
  - 多服务镜像构建并推送到 GHCR
  - Trivy 镜像漏洞扫描（High/Critical 阻断）
  - Canary 清单 `kubectl apply --dry-run=client` 校验
  - Tag 发布时可选自动部署 canary（需配置 `KUBE_CONFIG_DATA`）

## 5. 生产建议

- JWT 秘钥使用 K8s Secret，不要明文写入配置文件
- 订单与支付链路引入 Seata，避免跨服务写入不一致
- 网关按接口重要性配置限流和熔断阈值
- 所有服务开启 tracing，打通日志和指标上下文
