#!/usr/bin/env bash
# Dette scriptet setter opp avhengighetene til fpsak, slik at fpsak kan kjøres utenfor i IDE. Dette scriptet mocker
# ut fptilbake, fpoppdrag og fpformidling og spinner heller ikke opp fpabonnent. Ønskes du ikke dette må du gjøre det manuelt.

ARGUMENT=${1}

if [[ $ARGUMENT == down ]]; then
  docker-compose -f docker-compose-lokal/docker-compose.yml down
else
  sh ./setup-lokal-utvikling.sh --mock fptilbake --mock fpoppdrag --mock fprisk fpformidling

  if [ -f .env ]; then
    echo "Bruker applikasjonsversjonene som er definert i eksisterende .env fil: $(pwd)/.env"
  else
    cp docker-compose-lokal/.env .env
  fi

  docker-compose -f docker-compose-lokal/docker-compose.yml pull --include-deps oracle fpabakus fpsoknad-mottak
  docker-compose -f docker-compose-lokal/docker-compose.yml up --detach --scale fpfrontend=0 --scale fptilbake=0 --scale fpoppdrag=0 --scale fprisk=0
  docker-compose -f docker-compose-lokal/docker-compose.yml up --detach --scale fpformidling=0 fpsoknad-mottak
fi

