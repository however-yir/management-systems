#!/usr/bin/env bash
set -euo pipefail

cat <<MSG
Monitoring entrypoints:
- Prometheus: http://localhost:9090
- Grafana:    http://localhost:3000
- Kibana:     http://localhost:5601
- Jaeger:     http://localhost:16686

Gateway/service health:
- http://localhost:8080/actuator/health
- http://localhost:8081/actuator/health
- http://localhost:8082/actuator/health
- http://localhost:8083/actuator/health
- http://localhost:8084/actuator/health
MSG
