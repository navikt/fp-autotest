#!/usr/bin/env bash

# Dette scriptet setter opp avhengighetene til fpsak, slik at fpsak kan kjøres utenfor i IDE.
sh ./lokal-utvikling.sh fpsak

if [ -f .env ]; then
  echo "Bruker eksisterende .env fil: $(pwd)/.env"
else
  sh ./update-versions.sh
fi

docker-compose -f docker-compose-lokal/docker-compose.yml up --quiet-pull --detach --scale fpsak=0 fpsak-frontend
