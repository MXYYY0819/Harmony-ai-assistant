#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080}"
LOGIN=$(curl --fail-with-body -sS "$BASE_URL/auth/login" -H 'Content-Type: application/json' -d '{"username":"demo","password":"demo123"}')
TOKEN=$(printf '%s' "$LOGIN" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')
if [[ -z "$TOKEN" ]]; then printf 'Login failed: %s\n' "$LOGIN" >&2; exit 1; fi
CONV=$(curl --fail-with-body -sS "$BASE_URL/conversation" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"title":"Mock SSE demo","modelCode":"mock"}')
ID=$(printf '%s' "$CONV" | sed -n 's/.*"data":\([0-9]*\).*/\1/p')
if [[ -z "$ID" ]]; then printf 'Create failed: %s\n' "$CONV" >&2; exit 1; fi
printf 'conversationId=%s\n' "$ID"
curl --fail-with-body -sS -N "$BASE_URL/chat/completions" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d "{\"conversationId\":$ID,\"modelCode\":\"mock\",\"content\":\"Hello\"}"
printf '\nSaved messages:\n'
curl --fail-with-body -sS "$BASE_URL/conversation/$ID/messages?page=1&size=20" -H "Authorization: Bearer $TOKEN"
printf '\n'
