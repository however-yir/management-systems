#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

errors=0

echo "[check] route -> template"
for scope in admin mall; do
  controller_dir="src/main/java/io/howeveryir/auroramall/controller/${scope}"
  if [[ ! -d "$controller_dir" ]]; then
    continue
  fi

  while IFS= read -r view; do
    [[ -z "$view" ]] && continue
    template_path="src/main/resources/templates/${scope}/${view}.html"
    if [[ ! -f "$template_path" ]]; then
      echo "[missing-template] ${scope}/${view} => ${template_path}"
      errors=1
    fi
  done < <(rg -No "return \"${scope}/[^\"]+\"" "$controller_dir"/*.java \
    | sed -E "s#.*return \\\"${scope}/##; s/\\\"$//" \
    | sort -u)
done

# Extract local static refs from templates
# 1) Thymeleaf style: th:src="@{/...}" / th:href="@{/...}"
# 2) Plain style: src="/..." / href="/..." (only admin|mall paths)

echo "[check] template -> static assets"

while IFS= read -r rel_path; do
  [[ -z "$rel_path" ]] && continue
  # Skip dynamic thymeleaf refs such as @{'/mall/styles/'+${path}+'.css'}
  if [[ "$rel_path" == *'${'* ]]; then
    continue
  fi
  static_path="src/main/resources/static/${rel_path}"
  if [[ ! -f "$static_path" ]]; then
    echo "[missing-static] /${rel_path} => ${static_path}"
    errors=1
  fi
done < <(
  {
    rg -No 'th:(src|href)="@\{/[^"}]+\.[^"}]+\}"' src/main/resources/templates \
      | sed -E 's/.*@\{\/(.+)\}.*/\1/'
    rg -No '(src|href)="\/[^"]+\.[^"]+"' src/main/resources/templates \
      | sed -E 's/.*="\/(.+)".*/\1/'
  } | sort -u
)

if [[ "$errors" -ne 0 ]]; then
  echo "[fail] route/template/static consistency check failed"
  exit 1
fi

echo "[pass] route/template/static consistency check passed"
