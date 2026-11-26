#!/usr/bin/env bash
# Felles hjelpere for skriptene i scripts/. All tilstand ligger i .state/okter/<økt>/.

gyldig_navn() {
  if [[ "${1:-}" =~ ^[A-Za-z0-9_-][A-Za-z0-9._-]*$ ]]; then
    return 0
  else
    return 1
  fi
}

krev_okt() {
  if [[ -z "${OKT:-}" ]]; then
    local liste
    liste=$(ls -1t "$ROOT/.state/okter" 2>/dev/null | paste -sd, - || true)
    if [[ -z "$liste" ]]; then
      liste='(ingen)'
    fi
    echo "Mangler OKT. Kjør med OKT=<økt>. Økter: $liste" >&2
    exit 1
  fi
  if ! gyldig_navn "$OKT"; then
    echo "Ugyldig OKT: $OKT" >&2
    exit 1
  fi
  STATE="$ROOT/.state/okter/$OKT"
}

prosess_lever() {
  local pid="$1" regex="$2" feil kommando
  if [[ ! "$pid" =~ ^[0-9]+$ ]]; then
    return 1
  fi
  if feil=$(kill -0 "$pid" 2>&1); then
    kommando=$(ps -p "$pid" -o command= 2>/dev/null || true)
    if [[ -z "$kommando" ]]; then
      return 0
    fi
    if printf '%s\n' "$kommando" | grep -Eq "$regex"; then
      return 0
    else
      return 1
    fi
  fi
  if printf '%s\n' "$feil" | grep -Eqi 'Operation not permitted|Permission denied'; then
    return 0
  fi
  return 1
}

MAVEN_MONSTER='plexus-classworlds|org\.codehaus\.plexus|maven|mvn'
RUN_MONSTER='run\.mjs'

okt_er_live() {
  local d="$1" pidfil pid
  while IFS= read -r pidfil; do
    [[ -n "$pidfil" ]] || continue
    pid=$(cat "$pidfil" 2>/dev/null || true)
    if prosess_lever "$pid" "$MAVEN_MONSTER"; then
      return 0
    fi
  done < <(find "$d" -type f -name mvn.pid -print 2>/dev/null)
  for pidfil in "$d"/kjorer-*.pid; do
    if [[ -f "$pidfil" ]]; then
      pid=$(cat "$pidfil" 2>/dev/null || true)
      if prosess_lever "$pid" "$RUN_MONSTER"; then
        return 0
      fi
    fi
  done
  return 1
}

rydd_gamle_okter() {
  local state_root="$1" gjeldende="$2" d navn pid antall maks i
  local -a okter
  maks=${MAKS_OKTER:-10}
  okter=()
  while IFS= read -r d; do
    if [[ -d "$d" ]]; then
      okter+=("$d")
    fi
  done < <(ls -1dt "$state_root"/okter/*/ 2>/dev/null || true)
  antall=${#okter[@]}
  i=$((antall - 1))
  while [[ "$antall" -gt "$maks" && "$i" -ge 0 ]]; do
    d=${okter[$i]}
    if [[ "${d%/}" != "${gjeldende%/}" ]] && ! okt_er_live "$d"; then
      navn=${d%/}
      navn=${navn##*/}
      rm -rf "$d"
      echo "Slettet gammel økt $navn" >&2
      antall=$((antall - 1))
    fi
    i=$((i - 1))
  done
  if [[ "$antall" -gt "$maks" ]]; then
    echo "MERK: $antall økter, maks er $maks. De øvrige er i bruk og ble ikke slettet." >&2
  fi
}
