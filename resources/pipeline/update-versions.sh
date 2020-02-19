#!/usr/bin/env bash

TRIGGER=${1-not an repo}
TRIGGER_VERSION=${2-latest}

imageVersion () {
  if echo "$1" | grep -iqF "${TRIGGER}/"; then
    echo "${1}:${TRIGGER_VERSION}"
  else
    echo "${1}:latest"
  fi
}

echo ABAKUS_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fp-abakus/fpabakus")" > .env
echo VTP_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/vtp/vtp")" >> .env
echo FORMIDLING_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fp-formidling/fpformidling")" >> .env
echo FPSAK_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/fp-sak/fpsak-test")" >> .env
echo ORACLE_IMAGE="$(imageVersion "docker.pkg.github.com/navikt/vtp/foreldrepenger-oracle")" >> .env
echo POSTGRES_IMAGE=postgres:12 >> .env

echo ".env fil opprettet - Klart for docker-compose up"
