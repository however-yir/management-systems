#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENV_FILE="${ROOT_DIR}/.env"
ENV_EXAMPLE="${ROOT_DIR}/.env.example"
BACKEND_DIR="${ROOT_DIR}/teaching-manager-bk"

usage() {
  cat <<'USAGE'
Usage: ./scripts/dev.sh <command>

Commands:
  check-env    Validate local .env exists and no placeholder password remains
  infra-up     Start MySQL via docker compose
  infra-down   Stop MySQL via docker compose
  backend      Run Spring Boot backend (teaching-manager-bk)
  all          check-env + infra-up + backend
USAGE
}

ensure_env() {
  if [[ ! -f "${ENV_FILE}" ]]; then
    echo "Missing ${ENV_FILE}. Copy ${ENV_EXAMPLE} and fill your local values first."
    exit 1
  fi

  if grep -q 'change_me_' "${ENV_FILE}"; then
    echo "Detected placeholder secrets in ${ENV_FILE}. Please replace all change_me_* values."
    exit 1
  fi

  # If using root as datasource user, DB password must match MySQL root password.
  # This avoids a common local startup failure when DB_PASSWORD and DB_ROOT_PASSWORD differ.
  # shellcheck disable=SC1090
  source "${ENV_FILE}"
  if [[ "${DB_USERNAME:-}" == "root" && "${DB_PASSWORD:-}" != "${DB_ROOT_PASSWORD:-}" ]]; then
    echo "DB_USERNAME is root, but DB_PASSWORD != DB_ROOT_PASSWORD."
    echo "Please align them or switch DB_USERNAME to a dedicated app user."
    exit 1
  fi
}

ensure_port_free() {
  local port="${SERVER_PORT:-8081}"
  if lsof -nP -iTCP:"${port}" -sTCP:LISTEN >/dev/null 2>&1; then
    echo "Port ${port} is already in use. Update SERVER_PORT in ${ENV_FILE} and retry."
    exit 1
  fi
}

load_env() {
  set -a
  # shellcheck disable=SC1090
  source "${ENV_FILE}"
  set +a
}

compose() {
  docker compose --env-file "${ENV_FILE}" -f "${ROOT_DIR}/docker-compose.dev.yml" "$@"
}

cmd="${1:-}"
case "${cmd}" in
  check-env)
    ensure_env
    echo "Environment check passed."
    ;;
  infra-up)
    ensure_env
    compose up -d
    ;;
  infra-down)
    ensure_env
    compose down
    ;;
  backend)
    ensure_env
    load_env
    ensure_port_free
    cd "${BACKEND_DIR}"
    mvn spring-boot:run
    ;;
  all)
    ensure_env
    compose up -d
    load_env
    ensure_port_free
    cd "${BACKEND_DIR}"
    mvn spring-boot:run
    ;;
  *)
    usage
    exit 1
    ;;
esac
