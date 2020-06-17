#!/usr/bin/env bash
# Dette scriptet setter opp avhengighetene til fpsak, slik at fpsak kan kjøres utenfor i IDE.

ARGUMENT=${1}

if [[ $ARGUMENT == down ]]; then
  docker-compose -f docker-compose-lokal/docker-compose.yml down
else
  sh ./setup-lokal-utvikling.sh fpsak

  if [ -f .env ]; then
    echo "Bruker eksisterende .env fil: $(pwd)/.env"
  else
    sh ./update-versions.sh
  fi
  cp .env docker-compose-lokal/.env

  docker-compose -f docker-compose-lokal/docker-compose.yml pull --include-deps oracle fpabakus
  docker-compose -f docker-compose-lokal/docker-compose.yml pull fpsak-frontend
  docker-compose -f docker-compose-lokal/docker-compose.yml up --detach --scale fpsak=0 fpsak-frontend
fi

