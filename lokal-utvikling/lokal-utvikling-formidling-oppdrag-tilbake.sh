#!/usr/bin/env bash

ARGUMENT=${1}

# Dette scriptet setter opp avhengighetene til enten fpformidling, fpoppdrag eller fptilbake, slik disse kan kjøres utenfor i IDE.

if [[ $ARGUMENT == fpformidling ]] || [[ $ARGUMENT == fpoppdrag ]] || [[ $ARGUMENT == fptilbake ]]; then
  sh ./lokal-utvikling.sh $ARGUMENT

  if [ -f .env ]; then
    echo "Bruker eksisterende .env fil: $(pwd)/.env"
  else
    sh ./update-versions.sh
  fi
  cp .env docker-compose-lokal/.env

  docker-compose -f docker-compose-lokal/docker-compose.yml up --quiet-pull --detach fpsak-frontend
elif [[ $ARGUMENT == down ]]; then
  docker-compose -f docker-compose-lokal/docker-compose.yml down
else
  echo "Argumentene som støttes er: fpformidling, fpoppdrag eller fptilbake."
fi
