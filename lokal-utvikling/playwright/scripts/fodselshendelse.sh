#!/usr/bin/env bash
# Sender fødselshendelse fra PDL (via VTP) for en familie laget med scripts/familie.sh.
# Bruk: scripts/fodselshendelse.sh <navn> <yyyy-mm-dd>
#   fpsak oppretter revurdering (RE_HENDELSE_FØDSEL) på saker for mor og far. Fødselsnummer skrives ikke ut.
set -euo pipefail
NAVN=$1; DATO=$2
ROOT=$(cd "$(dirname "$0")/.." && pwd)
if [[ "${OKT+x}" == x ]]; then
  if [[ ! "$OKT" =~ ^[A-Za-z0-9._-]+$ ]]; then
    echo "Ugyldig OKT: $OKT" >&2
    exit 1
  fi
  FIL="$ROOT/.state/okter/$OKT/familie-$NAVN.txt"
else
  FIL="$ROOT/.state/familie-$NAVN.txt"
fi
[ -f "$FIL" ] || { echo "Fant ikke $FIL. Lag den med scripts/familie.sh $NAVN ..."; exit 1; }
MOR=$(grep '^mor=' "$FIL" | cut -d= -f2); FAR=$(grep -E '^(far|medfar)=' "$FIL" | head -1 | cut -d= -f2)
KODE=$(curl -s -o /dev/null -w '%{http_code}' -X POST http://localhost:8060/rest/api/pdl/leesah \
  -H 'Content-Type: application/json' -H 'Accept: application/json' \
  -d "{\"type\":\"fødselshendelse\",\"endringstype\":\"OPPRETTET\",\"fnrMor\":\"$MOR\",\"fnrFar\":\"$FAR\",\"foedselsdato\":\"$DATO\"}")
echo "Fødselshendelse $DATO for $NAVN: HTTP $KODE"
[[ "$KODE" =~ ^2 ]]
