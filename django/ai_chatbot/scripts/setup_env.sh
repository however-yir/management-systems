#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${ROOT_DIR}"

PYTHON_BIN="${PYTHON_BIN:-python3}"
VENV_DIR="${VENV_DIR:-.venv}"
WITH_DETECTION=false
FORCE_DETECTION=false

print_help() {
  cat <<'EOF'
Usage: scripts/setup_env.sh [options]

Options:
  --with-detection   Install legacy TensorFlow 1.x detection dependencies.
  --force-detection  Force detection install even on unsupported Python versions.
  -h, --help         Show this help message.

Environment variables:
  PYTHON_BIN         Python executable to use (default: python3)
  VENV_DIR           Virtual environment path (default: .venv)
EOF
}

while (($#)); do
  case "$1" in
    --with-detection)
      WITH_DETECTION=true
      shift
      ;;
    --force-detection)
      WITH_DETECTION=true
      FORCE_DETECTION=true
      shift
      ;;
    -h|--help)
      print_help
      exit 0
      ;;
    *)
      echo "Unknown option: $1"
      print_help
      exit 1
      ;;
  esac
done

if ! command -v "${PYTHON_BIN}" >/dev/null 2>&1; then
  echo "Python executable not found: ${PYTHON_BIN}"
  exit 1
fi

echo "[1/5] Creating virtual environment at ${VENV_DIR}"
"${PYTHON_BIN}" -m venv "${VENV_DIR}"

# shellcheck disable=SC1090
source "${VENV_DIR}/bin/activate"

echo "[2/5] Upgrading pip/setuptools/wheel"
python -m pip install --upgrade pip setuptools wheel

echo "[3/5] Installing core requirements"
python -m pip install -r requirements/core.txt

if [[ "${WITH_DETECTION}" == "true" ]]; then
  DETECTION_COMPATIBLE="$(python - <<'PY'
import sys
v = sys.version_info[:2]
print("1" if (3, 6) <= v <= (3, 7) else "0")
PY
)"

  if [[ "${DETECTION_COMPATIBLE}" == "1" || "${FORCE_DETECTION}" == "true" ]]; then
    echo "[4/5] Installing legacy detection requirements"
    python -m pip install -r requirements/detection-legacy.txt
  else
    echo "[4/5] Skip legacy detection requirements: requires Python 3.6/3.7 (or use --force-detection)"
  fi
else
  echo "[4/5] Skip legacy detection requirements"
fi

if [[ ! -f .env && -f .env.example ]]; then
  cp .env.example .env
  echo "[5/5] .env created from .env.example"
else
  echo "[5/5] .env already exists (or .env.example missing), skipped"
fi

if python manage.py check >/dev/null 2>&1; then
  echo "Django check passed."
else
  echo "Django check did not pass in current environment; dependencies were installed."
fi

cat <<'EOF'

Setup complete.

Next steps:
  source .venv/bin/activate
  python manage.py runserver 8000
EOF
