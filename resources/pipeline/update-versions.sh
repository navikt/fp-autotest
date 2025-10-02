#!/usr/bin/env bash

cd $(dirname $0)

TRIGGER=${1-not an repo}
TRIGGER_VERSION=${2-latest}

imageVersion () {
  if [[ "$TRIGGER" == *"navikt/"* && "$1" == *$TRIGGER* ]]; then
    echo "$1:${TRIGGER_VERSION}"
  elif [[ "$1" == *"$TRIGGER"* ]]; then
    echo "$TRIGGER:${TRIGGER_VERSION}"
  else
    echo "$1:latest"
  fi
}

echo POSTGRES_IMAGE="postgres:18" > .env
echo ORACLE_IMAGE="gvenzl/oracle-free:23-slim-faststart" >> .env
echo AUDIT_NAIS_IMAGE="$(imageVersion "ghcr.io/navikt/fp-autotest/audit-nais-mock")" >> .env
echo VTP_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/vtp")" >> .env
echo FPABAKUS_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fp-abakus")" >> .env
echo FPKALKULUS_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fp-kalkulus")" >> .env
echo FPSAK_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fp-sak")" >> .env
echo FPFRONTEND_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fp-frontend/fp-frontend")" >> .env
echo AVDELINGSLEDER_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fp-frontend/fp-avdelingsleder")" >> .env
echo JOURNALFORING_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fp-frontend/fp-journalforing")" >> .env
echo FPFORMIDLING_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fp-formidling")" >> .env
echo FPDOKGEN_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fp-dokgen")" >> .env
echo FPOPPDRAG_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fpoppdrag")" >> .env
echo FPTILBAKE_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fptilbake")" >> .env
echo FPRISK_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fp-risk")" >> .env
echo FPABONNENT_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fpabonnent")" >> .env
echo FPFORDEL_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fpfordel")" >> .env
echo FPLOS_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fplos")" >> .env
echo FPSOKNAD_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fp-soknad")" >> .env
echo FORELDREPENGESOKNAD_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/foreldrepengesoknad/foreldrepengesoknad")" >> .env
echo FORELDREPENGEOVERSIKT_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/foreldrepengesoknad/foreldrepengeoversikt")" >> .env
echo SVANGERSKAPSPENGESOKNAD_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/foreldrepengesoknad/svangerskapspengesoknad")" >> .env
echo ENGANGSSTONAD_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/foreldrepengesoknad/engangsstonad")" >> .env
echo FPOVERSIKT_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fp-oversikt")" >> .env
echo FPSWAGGER_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fp-swagger")" >> .env
echo FPTILGANG_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fp-tilgang")" >> .env
echo FPINNTEKTSMELDING_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fp-inntektsmelding")" >> .env
echo FPINNTEKTSMELDINGFRONTEND_IMAGE="$(imageVersion "europe-north1-docker.pkg.dev/nais-management-233d/teamforeldrepenger/navikt/fp-inntektsmelding-frontend")" >> .env

echo ".env fil opprettet - Klart for docker compose up"
