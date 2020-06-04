#!/usr/bin/env bash
# Dette scriptet setter opp avhengighetene til fpsak, slik at fpsak kan kjøres utenfor i IDE.

ARGUMENT=${1}

if [[ $ARGUMENT == down ]]; then
  docker-compose -f docker-compose-lokal/docker-compose.yml down
else
  sh ./lokal-utvikling.sh fpsak

  if [ -f .env ]; then
    echo "Bruker eksisterende .env fil: $(pwd)/.env"
  else
    sh ./update-versions.sh
  fi
  cp .env docker-compose-lokal/.env

  docker-compose -f docker-compose-lokal/docker-compose.yml up --quiet-pull --detach --scale fpsak=0 fpsak-frontend
fi

