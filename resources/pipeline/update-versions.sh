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
echo OAUTH2_SERVER_IMAGE="docker.pkg.github.com/navikt/mock-oauth2-server/mock-oauth2-server:0.2.1" >> .env
echo ORACLE_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fpsak-autotest/oracle-flattened")" >> .env
echo POSTGRES_IMAGE="postgres:12" >> .env
echo VTP_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/vtp/vtp")" >> .env
echo FPABAKUS_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fp-abakus/fpabakus")" >> .env
echo FPSAK_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fp-sak/fpsak")" >> .env
echo FPFORDEL_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fpfordel/fpfordel")" >> .env
echo FPINFO_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fpinfo/fpinfo")" >> .env
echo FPSOKNAD_MOTTAK_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fpsoknad-mottak/fpsoknad-mottak")" >> .env
echo FPFRONTEND_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fp-frontend/fp-frontend")" >> .env
echo FPFORMIDLING_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fp-formidling/fpformidling")" >> .env
echo FPDOKGEN_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fp-dokgen/fp-dokgen")" >> .env
echo FPOPPDRAG_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fpoppdrag/fpoppdrag")" >> .env
echo FPTILBAKE_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fptilbake/fptilbake")" >> .env
echo FPRISK_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fp-risk/fprisk")" >> .env
echo FPABONNENT_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fpabonnent/fpabonnent")" >> .env

echo ".env fil opprettet - Klart for docker-compose up"
