#!/usr/bin/env bash

cd $(dirname $0)

TRIGGER=${1-not an repo}
TRIGGER_VERSION=${2-latest}

imageVersion () {
  if echo "$1" | grep -iqF "${TRIGGER}/"; then
    echo "${1}:${TRIGGER_VERSION}"
  else
    echo "${1}:latest"
  fi
}

echo AUDIT_NAIS_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/k9-verdikjede/audit-nais-mock")" > .env
echo ORACLE_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fpsak-autotest/oracle-flattened")" >> .env
echo POSTGRES_IMAGE="postgres:12" >> .env
echo VTP_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/vtp/vtp")" >> .env
echo FPABAKUS_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fp-abakus/fpabakus")" >> .env
echo FPSAK_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fp-sak/fpsak-test")" >> .env
echo FPSAK_FRONTEND_IMAGE="$(imageVersion "docker.io/navikt/fpsak-frontend")" >> .env
echo FORMIDLING_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fp-formidling/fpformidling")" >> .env
echo FPOPPDRAG_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fpoppdrag/fpoppdrag")" >> .env
echo FPTILBAKE_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fptilbake/fptilbake-test")" >> .env

echo ".env fil opprettet - Klart for docker-compose up"
