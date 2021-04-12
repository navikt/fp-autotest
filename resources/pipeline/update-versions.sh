#!/usr/bin/env bash

cd $(dirname $0)

TRIGGER=${1-not an repo}
TRIGGER_VERSION=${2-latest}

imageVersion () {
  if echo "$1" | grep -iqF "${TRIGGER}/"; then
    echo "${1}:${TRIGGER_VERSION}"
  elif [[ "$1" == *"$TRIGGER" ]]; then
    echo "${1}:${TRIGGER_VERSION}"
  else
    echo "$1:latest"
  fi
}

echo AUDIT_NAIS_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/k9-verdikjede/audit-nais-mock")" > .env
echo ORACLE_IMAGE="ghcr.io/navikt/oracle-foreldrepenger:11" >> .env
echo POSTGRES_IMAGE="postgres:12" >> .env
echo VTP_IMAGE="$(imageVersion "ghcr.io/navikt/vtp")" >> .env
echo FPABAKUS_IMAGE="$(imageVersion "ghcr.io/navikt/fp-abakus")" >> .env
echo FPSAK_IMAGE="$(imageVersion "ghcr.io/navikt/fp-sak")" >> .env
echo FPTILBAKE_IMAGE="$(imageVersion "ghcr.io/navikt/fptilbake")" >> .env
echo FPSOKNAD_MOTTAK_IMAGE="$(imageVersion "ghcr.io/navikt/fpsoknad-mottak")" >> .env
echo FPFORDEL_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fpfordel/fpfordel")" >> .env
echo FPINFO_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fpinfo/fpinfo")" >> .env
echo FPFRONTEND_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fp-frontend/fp-frontend")" >> .env
echo FPFORMIDLING_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fp-formidling/fpformidling")" >> .env
echo FPDOKGEN_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fp-dokgen/fp-dokgen")" >> .env
echo FPOPPDRAG_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fpoppdrag/fpoppdrag")" >> .env
echo FPRISK_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fp-risk/fprisk")" >> .env
echo FPABONNENT_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fpabonnent/fpabonnent")" >> .env

echo ".env fil opprettet - Klart for docker-compose up"
