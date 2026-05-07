#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
TENANT_ID="${TENANT_ID:-public}"
SMOKE_USERNAME="${SMOKE_USERNAME:-mall-admin}"
SMOKE_PASSWORD="${SMOKE_PASSWORD:-}"

if [[ -z "$SMOKE_PASSWORD" ]]; then
  echo "[smoke] missing SMOKE_PASSWORD"
  exit 1
fi

echo "[smoke] gateway health"
curl -fsS "$BASE_URL/api/gateway/status" > /dev/null

echo "[smoke] login"
TOKEN=$(curl -fsS -X POST "$BASE_URL/api/users/login" \
  -H 'Content-Type: application/json' \
  -d "{\"username\":\"$SMOKE_USERNAME\",\"password\":\"$SMOKE_PASSWORD\"}" \
  | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p')

if [[ -z "$TOKEN" ]]; then
  echo "[smoke] failed to fetch token"
  exit 1
fi

echo "[smoke] query products"
curl -fsS "$BASE_URL/api/products?pageNo=1&pageSize=2" \
  -H "Authorization: Bearer $TOKEN" \
  -H "X-Tenant-Id: $TENANT_ID" > /dev/null

echo "[smoke] create order"
ORDER_NO=$(curl -fsS -X POST "$BASE_URL/api/orders" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -H "X-Tenant-Id: $TENANT_ID" \
  -d '{"userId":1,"productId":1001,"quantity":1}' \
  | sed -n 's/.*"orderNo":"\([^"]*\)".*/\1/p')

if [[ -z "$ORDER_NO" ]]; then
  echo "[smoke] failed to create order"
  exit 1
fi

echo "[smoke] pay order"
curl -fsS -X POST "$BASE_URL/api/payments/confirm" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -H "X-Tenant-Id: $TENANT_ID" \
  -d "{\"orderNo\":\"$ORDER_NO\",\"amount\":399.00,\"channel\":\"WECHAT_PAY\"}" > /dev/null

echo "[smoke] success"
