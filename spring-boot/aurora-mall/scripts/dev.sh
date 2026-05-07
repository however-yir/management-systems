#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENV_FILE="${ROOT_DIR}/.env"
ENV_EXAMPLE="${ROOT_DIR}/.env.example"

usage() {
  cat <<'USAGE'
Usage: ./scripts/dev.sh <command>

Commands:
  deps         Print required/optional local dependencies
  check-env    Validate .env exists and secrets are not placeholder values
  mysql-up     Start MySQL only via docker compose
  mysql-down   Stop MySQL service
  run-local    Run Spring Boot locally with environment from .env
  run-docker   Run full app stack in docker compose
  all-local    check-env + mysql-up + run-local
USAGE
}

ensure_env() {
  if [[ ! -f "${ENV_FILE}" ]]; then
    echo "Missing ${ENV_FILE}. Copy ${ENV_EXAMPLE} first."
    exit 1
  fi

  if grep -Eq 'change_me_|your_db_' "${ENV_FILE}"; then
    echo "Placeholder credentials detected in ${ENV_FILE}. Please update them before starting."
    exit 1
  fi
}

compose() {
  docker compose --env-file "${ENV_FILE}" -f "${ROOT_DIR}/docker-compose.yml" "$@"
}

cmd="${1:-}"
case "${cmd}" in
  deps)
    cat <<'DEPS'
Required:
- JDK 8+
- Maven 3.6+
- MySQL 8 (local or docker compose mysql service)

Optional (not required for current default startup):
- Redis
- Ollama
DEPS
    ;;
  check-env)
    ensure_env
    echo "Environment check passed."
    ;;
  mysql-up)
    ensure_env
    compose up -d mysql
    ;;
  mysql-down)
    ensure_env
    compose stop mysql
    ;;
  run-local)
    ensure_env
    set -a
    source "${ENV_FILE}"
    set +a
    cd "${ROOT_DIR}"
    mvn spring-boot:run
    ;;
  run-docker)
    ensure_env
    compose up --build
    ;;
  all-local)
    ensure_env
    compose up -d mysql
    set -a
    source "${ENV_FILE}"
    set +a
    cd "${ROOT_DIR}"
    mvn spring-boot:run
    ;;
  *)
    usage
    exit 1
    ;;
esac
