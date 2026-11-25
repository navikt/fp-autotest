#!/usr/bin/env bash
# Stopper Maven-prosessen som venter på søknader for en familie.
# Bruk: [OKT=<økt>] scripts/stopp-familie.sh <navn>
set -euo pipefail
NAVN=$1
ROOT=$(cd "$(dirname "$0")/.." && pwd)

if [[ ! "$NAVN" =~ ^[A-Za-z0-9._-]+$ ]]; then
  echo "Ugyldig familienavn: $NAVN" >&2
  exit 1
fi
if [[ "${OKT+x}" == x ]]; then
  if [[ ! "$OKT" =~ ^[A-Za-z0-9._-]+$ ]]; then
    echo "Ugyldig OKT: $OKT" >&2
    exit 1
  fi
  STATE="$ROOT/.state/okter/$OKT"
else
  STATE="$ROOT/.state"
fi

PID_FIL="$STATE/manuell/$NAVN/mvn.pid"
if [[ ! -f "$PID_FIL" ]]; then
  echo "Ingen Maven-prosess registrert for $NAVN."
  exit 0
fi

PID=$(cat "$PID_FIL")
if kill -0 "$PID" 2>/dev/null; then
  pkill -P "$PID" 2>/dev/null || true
  kill "$PID" 2>/dev/null || true
  echo "Stoppet Maven-prosessen for $NAVN (PID $PID)."
else
  echo "Maven-prosessen for $NAVN (PID $PID) kjører ikke."
fi
rm -f "$PID_FIL"
