#!/usr/bin/env bash
# Sender fødselshendelse fra PDL (via VTP) for en familie laget med scripts/familie.sh.
# Bruk: OKT=<økt> scripts/fodselshendelse.sh <navn> <yyyy-mm-dd>
#   fpsak oppretter revurdering (RE_HENDELSE_FØDSEL) på saker for mor og far. Fødselsnummer skrives ikke ut.
set -euo pipefail
source "$(dirname "$0")/felles.sh"
NAVN=$1; DATO=$2
ROOT=$(cd "$(dirname "$0")/.." && pwd)
if ! gyldig_navn "$NAVN"; then
  echo "Ugyldig familienavn: $NAVN" >&2
  exit 1
fi
krev_okt
FIL="$STATE/familie-$NAVN.txt"
if [[ ! -f "$FIL" ]]; then
  echo "Fant ikke $FIL. Lag den med scripts/familie.sh $NAVN ..." >&2
  exit 1
fi
touch "$STATE"
MOR=$(grep '^mor=' "$FIL" | cut -d= -f2); FAR=$(grep -E '^(far|medfar)=' "$FIL" | head -1 | cut -d= -f2)
KODE=$(curl -s -o /dev/null -w '%{http_code}' -X POST http://localhost:8060/rest/api/pdl/leesah \
  -H 'Content-Type: application/json' -H 'Accept: application/json' \
  -d "{\"type\":\"fødselshendelse\",\"endringstype\":\"OPPRETTET\",\"fnrMor\":\"$MOR\",\"fnrFar\":\"$FAR\",\"foedselsdato\":\"$DATO\"}")
echo "Fødselshendelse $DATO for $NAVN: HTTP $KODE"
[[ "$KODE" =~ ^2 ]]
