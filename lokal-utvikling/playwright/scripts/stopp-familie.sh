#!/usr/bin/env bash
# Stopper Maven-prosessen som venter på søknader for en familie i en økt.
# Bruk: OKT=<økt> scripts/stopp-familie.sh <navn>
set -euo pipefail
source "$(dirname "$0")/felles.sh"
NAVN=$1
ROOT=$(cd "$(dirname "$0")/.." && pwd)

if ! gyldig_navn "$NAVN"; then
  echo "Ugyldig familienavn: $NAVN" >&2
  exit 1
fi
krev_okt

PID_FIL="$STATE/manuell/$NAVN/mvn.pid"
if [[ ! -f "$PID_FIL" ]]; then
  echo "Ingen Maven-prosess registrert for $NAVN."
  exit 0
fi

PID=$(cat "$PID_FIL")
if prosess_lever "$PID" "$MAVEN_MONSTER"; then
  pkill -P "$PID" 2>/dev/null || true
  kill "$PID" 2>/dev/null || true
  echo "Stoppet Maven-prosessen for $NAVN (PID $PID)."
else
  echo "Maven-prosessen for $NAVN (PID $PID) kjører ikke."
fi
rm -f "$PID_FIL"
