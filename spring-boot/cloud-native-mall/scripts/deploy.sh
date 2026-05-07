#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

echo "[deploy] start infrastructure"
docker compose -f docker/docker-compose.yml up -d

if [[ -f "k8s/deployment/mall-jwt-secret.yaml" ]]; then
  echo "[deploy] apply jwt secret"
  kubectl apply -f k8s/deployment/mall-jwt-secret.yaml
else
  echo "[deploy] warning: k8s/deployment/mall-jwt-secret.yaml not found, skip secret apply"
fi

echo "[deploy] apply kubernetes manifests"
kubectl apply -f k8s/deployment
kubectl apply -f k8s/service
kubectl apply -f k8s/ingress

echo "[deploy] finished"
