#!/usr/bin/env bash
# Lager en familie i VTP med fp-autotest ManuellSoknad og lagrer data i en økt.
# Bruk: [OKT=<økt>] [ANTALL=<n>] [FODT_UKER=<uker>] scripts/familie.sh <navn> <fodsel|termin> [auto|vent|ingen] [mor|far|begge]
#   OKT er valgfri og settes til familienavnet. MAKS_OKTER begrenser øktene (standard 10).
#   auto/vent kjører i bakgrunnen (venter på søknad og sender inntektsmelding). Med vent: echo mor > .state/okter/<økt>/manuell/<navn>/inntektsmelding
#   MOR_INNTEKT/FAR_INNTEKT=arbeid+frilans+naering (standard arbeid, eller ingen) styrer inntekt i VTP.
#   FODT_UKER=<uker> (standard 2) styrer hvor gammelt barnet er ved fodsel.
#   ANTALL=<n> (standard 1) lager n familier i én Maven-kjøring. De heter <navn>-01, <navn>-02, ... og deler loggen
#   .state/okter/<økt>/familie-<navn>.log. Med vent: echo mor > .state/okter/<økt>/manuell/<navn>/<id>/inntektsmelding
#   Flere familier kan lages samtidig. Maven-oppstarten går én om gangen via låsen, og navnet må være nytt.
set -euo pipefail
source "$(dirname "$0")/felles.sh"
NAVN=$1; FH=$2; IM=${3:-ingen}; SOKER=${4:-begge}; ANTALL=${ANTALL:-1}
ROOT=$(cd "$(dirname "$0")/.." && pwd); AT=$(cd "$ROOT/../.." && pwd)
STATE_ROOT="$ROOT/.state"
OKT=${OKT:-$NAVN}
if ! gyldig_navn "$OKT"; then
  echo "Ugyldig OKT: $OKT" >&2
  exit 1
fi
STATE="$STATE_ROOT/okter/$OKT"
if ! gyldig_navn "$NAVN"; then
  echo "Ugyldig familienavn: $NAVN" >&2
  exit 1
fi
if [[ ! "$ANTALL" =~ ^[1-9][0-9]{0,2}$ ]]; then
  echo "Ugyldig ANTALL: $ANTALL (1–999)" >&2
  exit 1
fi
if [[ ! "${FODT_UKER:-2}" =~ ^[0-9]+$ ]]; then
  echo "Ugyldig FODT_UKER: $FODT_UKER (antall uker)" >&2
  exit 1
fi

# Id-ene følger ManuellSoknad: null-utfylt til minst to siffer (01..10, 001..150). Med ANTALL=1 finnes ingen id.
IDER=()
if [[ "$ANTALL" -gt 1 ]]; then
  BREDDE=${#ANTALL}; [[ "$BREDDE" -lt 2 ]] && BREDDE=2
  for i in $(seq 1 "$ANTALL"); do IDER+=("$(printf "%0${BREDDE}d" "$i")"); done
fi

MAPPE="$STATE/manuell/$NAVN"; LAS="$STATE_ROOT/okter/.mvn-lock"
mkdir -p "$STATE_ROOT/okter" "$STATE/manuell"; LOG="$STATE/familie-$NAVN.log"
avvis_gjenbruk() {
  echo "Familien $NAVN finnes fra før. Velg et nytt navn (gammel innlogging i .state/okter/$OKT/$NAVN-*.json ville ellers blitt brukt)." >&2
  exit 1
}
if [[ -e "$STATE/familie-$NAVN.txt" ]]; then
  avvis_gjenbruk
fi
for id in ${IDER[@]+"${IDER[@]}"}; do
  if [[ -e "$STATE/familie-$NAVN-$id.txt" ]]; then
    avvis_gjenbruk
  fi
done
for gammel in "$STATE/$NAVN-"*.json; do
  if [[ -f "$gammel" ]]; then
    avvis_gjenbruk
  fi
done
if ! mkdir "$MAPPE" 2>/dev/null; then
  avvis_gjenbruk
fi

# Finnes minst én familie.txt? Med ANTALL>1 ligger de i undermappene, og Maven kan fortsatt skrive der i bakgrunnen.
har_familie() {
  if [[ "$ANTALL" -eq 1 ]]; then
    if [[ -f "$MAPPE/familie.txt" ]]; then
      return 0
    else
      return 1
    fi
  else
    if compgen -G "$MAPPE/*/familie.txt" > /dev/null; then
      return 0
    else
      return 1
    fi
  fi
}

forklar_feil() {
  if grep -Eq 'Operation not permitted|EPERM|Permission denied' "$LOG" 2>/dev/null; then
    echo "ÅRSAK: Sandbox blokkerte tilkobling eller filtilgang (EPERM/Operation not permitted). Fiks utenfor cplt (f.eks. cplt config set sandbox.allow_localhost_any true)." >&2
  elif grep -Eq 'Connection refused|ConnectException' "$LOG" 2>/dev/null; then
    echo "ÅRSAK: Kunne ikke koble til lokal tjeneste (VTP/fpsak). Sjekk at lokal verdikjede kjører." >&2
  elif grep -Eq 'offline mode|Cannot access .* in offline mode|Could not resolve dependencies' "$LOG" 2>/dev/null; then
    echo "ÅRSAK: Maven mangler cachede avhengigheter i offline-modus. Prøv igjen med MVN_BUILD=1." >&2
  fi
}

LAS_EIET=0
rydd_opp() {
  if [[ "$LAS_EIET" -eq 1 && -f "$LAS/eier" ]] && [[ "$(cat "$LAS/eier" 2>/dev/null)" == "$NAVN" ]]; then
    rm -rf "$LAS"
  fi
  if ! har_familie; then
    rm -rf "$MAPPE"
  fi
}
trap rydd_opp EXIT

VENTET=0
while ! mkdir "$LAS" 2>/dev/null; do
  if [[ -d "$LAS" ]] && find "$LAS" -maxdepth 0 -mmin +5 -print -quit | grep -q .; then
    rm -rf "$LAS"
    continue
  fi
  if [[ "$VENTET" -eq 0 ]]; then
    HOLDER=$(cat "$LAS/eier" 2>/dev/null || echo ukjent)
    echo "VENTER PÅ LÅS (holdes av $HOLDER)"
    VENTET=1
  fi
  sleep 0.5
done
LAS_EIET=1
printf '%s\n' "$NAVN" > "$LAS/eier"
touch "$STATE"
rydd_gamle_okter "$STATE_ROOT" "$STATE"

ARGS=(-P utvikling -Dtest=ManuellSoknad -Dmanuell.familiehendelse=$FH -Dmanuell.mor=${MOR_INNTEKT:-arbeid} -Dmanuell.far=${FAR_INNTEKT:-arbeid} -Dmanuell.fodt.uker.siden=${FODT_UKER:-2} "-Dmanuell.mappe=$MAPPE" -Dmanuell.antall=$ANTALL)
cd "$AT"
KLASSE=target/test-classes/no/nav/foreldrepenger/autotest/fpsak/foreldrepenger/ManuellSoknad.class
KILDE=src/test/java/no/nav/foreldrepenger/autotest/fpsak/foreldrepenger/ManuellSoknad.java
if [[ "${MVN_BUILD:-0}" != 1 && -f "$KLASSE" && "$KLASSE" -nt pom.xml && "$KLASSE" -nt "$KILDE" ]]; then
  MVN=(mvn -o surefire:test)
else
  MVN=(mvn test)
fi
if [ "$IM" = ingen ]; then
  "${MVN[@]}" "${ARGS[@]}" > "$LOG" 2>&1 || true
else
  nohup "${MVN[@]}" "${ARGS[@]}" -Dmanuell.inntektsmelding=$IM -Dmanuell.soker=$SOKER </dev/null >"$LOG" 2>&1 &
  MVN_PID=$!
  printf '%s\n' "$MVN_PID" > "$MAPPE/mvn.pid"
  # ManuellSoknad skriver alle familie.txt før første «Familie opprettet, se». FamilieGenerator logger
  # «Familie opprettet med mor» før fila finnes, så den linjen teller ikke.
  for _ in $(seq 1 $(((90 + ANTALL) * 10))); do
    if grep -qE "Familie opprettet, se|BUILD FAILURE|BUILD SUCCESS" "$LOG" 2>/dev/null; then
      break
    fi
    if ! kill -0 "$MVN_PID" 2>/dev/null; then break; fi
    sleep 0.2
  done
fi

if [[ "$ANTALL" -eq 1 ]]; then
  if [[ ! -f "$MAPPE/familie.txt" ]]; then
    echo "Familien ble ikke laget. Se $LOG" >&2
    forklar_feil
    tail -20 "$LOG" >&2
    exit 1
  fi
  cp "$MAPPE/familie.txt" "$STATE/familie-$NAVN.txt"
  grep -vE '^(mor|far)=' "$STATE/familie-$NAVN.txt"
  echo "Økt: OKT=$OKT (bruk den i alle kommandoene for denne familien)"
  echo "Logg: $LOG (saksnummer: grep 'Inntektsmelding sendt' $LOG)"
  if [ "$IM" != ingen ]; then
    echo "Familiene er klare. Ikke vent på BUILD SUCCESS i loggen: Maven-prosessen venter på søknadene i bakgrunnen (PID $MVN_PID). Stopp den med: OKT=$OKT scripts/stopp-familie.sh $NAVN"
  fi
  exit 0
fi

MANGLER=()
for id in "${IDER[@]}"; do
  [[ -f "$MAPPE/$id/familie.txt" ]] || MANGLER+=("$NAVN-$id")
done
if [[ ${#MANGLER[@]} -gt 0 ]]; then
  echo "Familiene ble ikke laget: ${MANGLER[*]}. Se $LOG" >&2
  forklar_feil
  tail -20 "$LOG" >&2
  exit 1
fi
for id in "${IDER[@]}"; do
  cp "$MAPPE/$id/familie.txt" "$STATE/familie-$NAVN-$id.txt"
  grep -vE '^(mor|far)=' "$STATE/familie-$NAVN-$id.txt" | sed "s/^/$NAVN-$id: /"
done
echo "Logg: $LOG (saksnummer: grep 'i familie <id>, saksnummer' $LOG)"
echo "Økt: OKT=$OKT (bruk den i alle kommandoene for denne familien)"
if [ "$IM" != ingen ]; then
  echo "Familiene er klare. Ikke vent på BUILD SUCCESS i loggen: Maven-prosessen venter på søknadene i bakgrunnen (PID $MVN_PID). Stopp den med: OKT=$OKT scripts/stopp-familie.sh $NAVN"
fi
