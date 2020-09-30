#!/usr/bin/env bash
# Dette scriptet setter opp avhengighetene til fpsak, slik at fpsak kan kjøres utenfor i IDE.

ARGUMENT=${1}

if [[ $ARGUMENT == down ]]; then
  docker-compose -f docker-compose-lokal/docker-compose.yml down
else
  sh ./setup-lokal-utvikling.sh --mock fptilbake --mock fpoppdrag --mock fpformidling --mock fprisk fpsak

  if [ -f .env ]; then
    echo "Bruker applikasjonsversjonene som er definert i eksisterende .env fil: $(pwd)/.env"
  else
    cp docker-compose-lokal/.env .env
  fi

  docker-compose -f docker-compose-lokal/docker-compose.yml pull --include-deps oracle fpabakus
  docker-compose -f docker-compose-lokal/docker-compose.yml pull fpfrontend
  docker-compose -f docker-compose-lokal/docker-compose.yml up --detach --scale fpsak=0 fpfrontend
fi

