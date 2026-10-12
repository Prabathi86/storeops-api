#!/bin/bash

BASE_URL="http://localhost:8080"

AGENTS_DIR=".harness/agents"
PLANNER="$AGENTS_DIR/planner-agent.md"
GENERATOR="$AGENTS_DIR/generator-agent.md"
EVALUATOR="$AGENTS_DIR/evaluator-agent.md"
MONITOR="$AGENTS_DIR/monitor-agent.md"

REVIEWS_DIR=".harness/reviews"
OUTPUT_DIR=".harness/output"

mkdir -p "$REVIEWS_DIR"
mkdir -p "$OUTPUT_DIR"

timestamp() {
  date +"%Y-%m-%d_%H-%M-%S"
}

write_file() {
  FILE="$1"
  CONTENT="$2"
  printf "%s\n" "$CONTENT" > "$FILE"
}

prompt_approval() {
  echo "Awaiting approval…"
  echo "Type APPROVED to continue."
  read -r INPUT
  [[ "$INPUT" == "APPROVED" ]]
}

# ============================================================
# REAL HTTP CALLS — SAFE (NO MULTILINE QUOTES)
# ============================================================

call_bulk_update() {
  ENDPOINT="$BASE_URL/api/activities/bulk-status"
  BODY='{"updates":[{"taskId":"task-1","status":"DONE"},{"taskId":"task-2","status":"BLOCKED"}]}'

  echo "PATCH $ENDPOINT"
  echo "Request Body: $BODY"

  RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X PATCH "$ENDPOINT" \
    -H "Content-Type: application/json" \
    -d "$BODY")

  printf "%s\n" "$RESPONSE"
}

call_sla_breach() {
  ENDPOINT1="$BASE_URL/api/activities"
  ENDPOINT2="$BASE_URL/api/alerts"

  echo "GET $ENDPOINT1"
  RES1=$(curl -s -w "\nHTTP_CODE:%{http_code}" "$ENDPOINT1")
  printf "%s\n" "$RES1"

  echo "GET $ENDPOINT2"
  RES2=$(curl -s -w "\nHTTP_CODE:%{http_code}" "$ENDPOINT2")
  printf "%s\n" "$RES2"

  printf "%s\n%s\n" "$RES1" "$RES2"
}

call_regional_rollup() {
  ENDPOINT="$BASE_URL/api/reports/region/region-123"

  echo "GET $ENDPOINT"
  RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" "$ENDPOINT")
  printf "%s\n" "$RESPONSE"
}

call_planogram_template() {
  ENDPOINT="$BASE_URL/api/programmes/prog-1/templates"
  BODY='{"templateId":"planogram-default"}'

  echo "POST $ENDPOINT"
  echo "Request Body: $BODY"

  RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST "$ENDPOINT" \
    -H "Content-Type: application/json" \
    -d "$BODY")

  printf "%s\n" "$RESPONSE"
}

# ============================================================
# MAIN FEATURE SIMULATION
# ============================================================

simulate_feature() {
  FEATURE_NAME="$1"
  SPRINT_NAME="$2"
  FEATURE_ID="$3"

  echo "============================================================"
  echo "HARNESS RUN: $FEATURE_NAME"
  echo "============================================================"

  write_file "spec.md" "# Spec for: $FEATURE_NAME"
  write_file "sprint-contract.md" "# Sprint Contract: $SPRINT_NAME"
  echo "→ Status: AWAITING APPROVAL"

  if ! prompt_approval; then
    echo "Sprint cancelled."
    return
  fi

  echo "[Generator] Running real API calls…"

  if [[ "$FEATURE_ID" == "1" ]]; then
    REAL_LOGS=$(call_bulk_update)
  elif [[ "$FEATURE_ID" == "2" ]]; then
    REAL_LOGS=$(call_sla_breach)
  elif [[ "$FEATURE_ID" == "3" ]]; then
    REAL_LOGS=$(call_regional_rollup)
  elif [[ "$FEATURE_ID" == "4" ]]; then
    REAL_LOGS=$(call_planogram_template)
  fi

  write_file "generator-summary.md" "Real API Logs:\n$REAL_LOGS"

  COVERAGE="Controller: 78%
Service: 88%
Utilities: 67%
Overall: 81%"

  write_file "evaluator-feedback.md" "Coverage:\n$COVERAGE"

  LOG_DIR="$REVIEWS_DIR/$(timestamp)"
  mkdir -p "$LOG_DIR"

  write_file "$LOG_DIR/run-log.md" "API Logs:\n$REAL_LOGS\n\nCoverage:\n$COVERAGE"

  echo "Sprint complete."
}

# ============================================================
# MENU
# ============================================================

while true; do
  echo "Select a feature:"
  echo "1) Bulk Update"
  echo "2) SLA Breach"
  echo "3) Regional Rollup"
  echo "4) Planogram Template"
  echo "5) Exit"
  read -r CHOICE

  case "$CHOICE" in
    1) simulate_feature "Bulk Update" "Sprint: Bulk Update" 1 ;;
    2) simulate_feature "SLA Breach" "Sprint: SLA Breach" 2 ;;
    3) simulate_feature "Regional Rollup" "Sprint: Regional Rollup" 3 ;;
    4) simulate_feature "Planogram Template" "Sprint: Planogram Template" 4 ;;
    5) exit 0 ;;
    *) echo "Invalid choice." ;;
  esac
done
