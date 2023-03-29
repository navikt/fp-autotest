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

echo POSTGRES_IMAGE="postgres:12" > .env
echo ORACLE_IMAGE="ghcr.io/navikt/oracle-foreldrepenger:18-migrert" >> .env
echo AUDIT_NAIS_IMAGE="$(imageVersion "ghcr.io/navikt/fp-autotest/audit-nais-mock")" >> .env
echo VTP_IMAGE="$(imageVersion "ghcr.io/navikt/vtp")" >> .env
echo FPABAKUS_IMAGE="$(imageVersion "ghcr.io/navikt/fp-abakus")" >> .env
echo FPSAK_IMAGE="$(imageVersion "ghcr.io/navikt/fp-sak")" >> .env
echo FPFRONTEND_IMAGE="$(imageVersion "ghcr.io/navikt/fp-frontend")" >> .env
echo FPFORMIDLING_IMAGE="$(imageVersion "ghcr.io/navikt/fp-formidling")" >> .env
echo FPDOKGEN_IMAGE="$(imageVersion "ghcr.io/navikt/fp-dokgen")" >> .env
echo FPOPPDRAG_IMAGE="$(imageVersion "ghcr.io/navikt/fpoppdrag")" >> .env
echo FPTILBAKE_IMAGE="$(imageVersion "ghcr.io/navikt/fptilbake")" >> .env
echo FPRISK_IMAGE="$(imageVersion "ghcr.io/navikt/fp-risk")" >> .env
echo FPABONNENT_IMAGE="$(imageVersion "ghcr.io/navikt/fpabonnent")" >> .env
echo FPFORDEL_IMAGE="$(imageVersion "ghcr.io/navikt/fpfordel")" >> .env
echo FPINFO_IMAGE="$(imageVersion "ghcr.io/navikt/fpinfo")" >> .env
echo FPLOS_IMAGE="$(imageVersion "ghcr.io/navikt/fplos")" >> .env
echo FPSOKNAD_MOTTAK_IMAGE="$(imageVersion "ghcr.io/navikt/fpsoknad-mottak")" >> .env
echo FPINFO_HISTORIKK_IMAGE="$(imageVersion "ghcr.io/navikt/fpinfo-historikk")" >> .env
echo FORELDREPENGESOKNADAPI_IMAGE="$(imageVersion "ghcr.io/navikt/foreldrepengesoknad-api")" >> .env
echo FORELDREPENGESOKNAD_IMAGE="$(imageVersion "ghcr.io/navikt/foreldrepengesoknad")" >> .env
echo FORELDREPENGEOVERSIKT_IMAGE="$(imageVersion "ghcr.io/navikt/foreldrepengeoversikt")" >> .env
echo SVANGERSKAPSPENGESOKNAD_IMAGE="$(imageVersion "ghcr.io/navikt/svangerskapspengesoknad")" >> .env
echo FPOVERSIKT_IMAGE="$(imageVersion "ghcr.io/navikt/fp-oversikt")" >> .env

echo ".env fil opprettet - Klart for docker-compose up"
