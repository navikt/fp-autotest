#!/usr/bin/env bash

# Ønsker du – som er funksjonell eller ikke-teknisk person – å kjøre gjennom tester kan du kjøre dette scriptet for å
# få opp alt du trenger for å kjøre tester.

ARGUMENT=${1}

if [[ $ARGUMENT == down ]]; then
  docker-compose -f docker-compose-lokal/docker-compose.yml down
else
  sh ./lokal-utvikling.sh

  if [ -f .env ]; then
    echo "Bruker eksisterende .env fil: $(pwd)/.env"
  else
    sh ./update-versions.sh
  fi
  cp .env docker-compose-lokal/.env

  docker-compose -f docker-compose-lokal/docker-compose.yml up --quiet-pull --detach fpsak-frontend
fi
