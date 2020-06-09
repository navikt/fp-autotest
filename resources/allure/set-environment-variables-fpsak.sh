#!/usr/bin/env bash

cd $(dirname $0)

TRIGGER=${1}

if [ -z ${1+x} ]; then
  echo trigger="navikt/fpsak-autotest" >>environment.properties
else
  echo trigger="$TRIGGER" >>environment.properties
fi

grep -e '^disclaimer\|^trigger\|^AUDIT_NAIS_IMAGE\|^ORACLE_IMAGE\|^POSTGRES_IMAGE\|^VTP_IMAGE\|^FPABAKUS_IMAGE\|^FPSAK_IMAGE' ../../lokal-utvikling/.env >> environment.properties
