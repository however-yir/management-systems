#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

echo "[canary] apply canary manifests"
kubectl apply -f k8s/canary

echo "[canary] done"
