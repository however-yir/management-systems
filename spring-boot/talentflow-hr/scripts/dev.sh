#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENV_FILE="${ENV_FILE:-$ROOT_DIR/.env}"

print_usage() {
  cat <<'EOF'
Usage: ./scripts/dev.sh <command>

Commands:
  doctor        Check required local commands
  infra-up      Start MySQL / Redis / RabbitMQ via docker compose
  infra-down    Stop local infra
  db-init       Import talentflow_hr.sql into MySQL
  backend-web   Run talentflow-web service
  backend-mail  Run talentflow-mailserver service
  frontend      Run talentflow-ui (npm ci + npm run serve)
  all           doctor + infra-up + print next-step commands
EOF
}

ensure_env_file() {
  if [[ ! -f "$ENV_FILE" ]]; then
    echo "[INFO] $ENV_FILE not found, creating from .env.example"
    cp "$ROOT_DIR/.env.example" "$ENV_FILE"
    echo "[WARN] Please edit $ENV_FILE before connecting to real services."
  fi
}

load_env() {
  ensure_env_file
  set -a
  # shellcheck disable=SC1090
  source "$ENV_FILE"
  set +a
}

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "[ERROR] Missing required command: $1"
    exit 1
  fi
}

doctor() {
  require_cmd docker
  require_cmd mvn
  require_cmd npm
  require_cmd mysql
  echo "[OK] Required commands are available."
}

infra_up() {
  load_env
  docker compose --env-file "$ENV_FILE" up -d
}

infra_down() {
  load_env
  docker compose --env-file "$ENV_FILE" down
}

db_init() {
  load_env
  mysql -h 127.0.0.1 -uroot -p"${TF_DB_ROOT_PASSWORD}" talentflow_hr < "$ROOT_DIR/talentflow_hr.sql"
  echo "[OK] Imported talentflow_hr.sql"
}

backend_web() {
  load_env
  cd "$ROOT_DIR/talentflow-platform"
  mvn -ntp -pl talentflow-server/talentflow-web -am spring-boot:run
}

backend_mail() {
  load_env
  cd "$ROOT_DIR/talentflow-platform"
  mvn -ntp -pl talentflow-mailserver spring-boot:run
}

frontend() {
  cd "$ROOT_DIR/talentflow-ui"
  npm ci
  npm run serve
}

all() {
  doctor
  infra_up
  cat <<'EOF'

Next steps:
  1) ./scripts/dev.sh db-init
  2) ./scripts/dev.sh backend-web
  3) ./scripts/dev.sh backend-mail
  4) ./scripts/dev.sh frontend
EOF
}

case "${1:-}" in
  doctor) doctor ;;
  infra-up) infra_up ;;
  infra-down) infra_down ;;
  db-init) db_init ;;
  backend-web) backend_web ;;
  backend-mail) backend_mail ;;
  frontend) frontend ;;
  all) all ;;
  *) print_usage; exit 1 ;;
esac
