#!/usr/bin/env bash
# Dette scriptet setter opp avhengighetene slik at fpoppdrag og fptilbake kan kjøres utenfor Docker Compose i IDE.
# Denne konfigurasjonen setter ikke opp fprisk, men bruker mock av fprisk i VTP istedenfor.

ARGUMENT=${1}

if [[ $ARGUMENT == down ]]; then
  docker-compose -f docker-compose-lokal/docker-compose.yml down
else
  options="--mock fprisk"
  sh ./setup-lokal-utvikling.sh $options fpoppdrag fptilbake

  if [ -f .env ]; then
    echo "Bruker applikasjonsversjonene som er definert i eksisterende .env fil: $(pwd)/.env"
  else
    cp docker-compose-lokal/.env .env
  fi

  docker-compose -f docker-compose-lokal/docker-compose.yml pull
  docker-compose -f docker-compose-lokal/docker-compose.yml up --detach --scale fptilbake=0 --scale fpoppdrag=0
fi
