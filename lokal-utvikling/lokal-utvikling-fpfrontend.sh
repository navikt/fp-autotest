#!/usr/bin/env bash

# Ønsker du – som er funksjonell eller ikke-teknisk person – å kjøre gjennom tester kan du kjøre dette scriptet for å
# få opp alt du trenger for å kjøre tester.

ARGUMENT=${1}

if [[ $ARGUMENT == down ]]; then
  docker-compose -f docker-compose-lokal/docker-compose.yml down
else
  sh ./setup-lokal-utvikling.sh --mock fptilbake --mock fpoppdrag --mock fpformidling --mock fprisk fpfrontend

  if [ -f .env ]; then
    echo "Bruker applikasjonsversjonene som er definert i eksisterende .env fil: $(pwd)/.env"
  else
    cp docker-compose-lokal/.env .env
  fi

  docker-compose -f docker-compose-lokal/docker-compose.yml pull --include-deps fpsak
  docker-compose -f docker-compose-lokal/docker-compose.yml up --detach fpsak
fi
