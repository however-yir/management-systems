# Engineering Quality Plan

This document defines the quality baseline and delivery policy for `however-yir/ai_chatbot`.

## 1. Scope

- Repository: `however-yir/ai_chatbot`
- Primary language: `Python`
- Framework/runtime: `Django 3.2 + MySQL`
- Baseline release tag: `baseline-2026-04-10`

## 2. Quality Gates

- Security scans: `gitleaks` in `.github/workflows/baseline-ci.yml`
- Dependency hygiene: pinned requirements split into `requirements/core.txt` and `requirements/detection-legacy.txt`
- Static checks: `ruff check .` when Python dependencies are present
- Build checks: `python -m compileall -q .`
- Runtime validation: `python manage.py check` in `scripts/setup_env.sh`
- Delivery hygiene: `.github/pull_request_template.md` requires summary, linked issue, test evidence, risk, and rollback notes

## 3. Test Strategy

- Configuration layer: verify `.env.example`, database settings, and dependency installation paths stay runnable
- Framework layer: use `python manage.py check` as the minimum Django health gate for each environment setup
- Regression layer: prioritize manual smoke checks for login, registration, chat flow, record management, and admin access
- Legacy AI layer: treat `index/detection` as opt-in legacy code and validate it only in explicitly compatible Python environments

## 4. Pull Request Definition of Done

- Baseline CI passes
- README or docs are updated when behavior, setup, or operations change
- Database or environment-variable changes include upgrade notes
- Risk and rollback notes are filled in for user-facing or admin-facing changes

## 5. Next Deepening Steps

- Add pytest smoke tests for auth, chat, and record-management flows
- Add CI-backed Django integration tests with fixture data
- Add migration and seed-data validation for MySQL bootstrap steps
- Add a dedicated compatibility lane for the legacy detection module
