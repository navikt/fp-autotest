#!/usr/bin/env bash
# Dette scriptet setter opp avhengighetene til enten fpformidling, fpoppdrag eller fptilbake, slik disse kan kjøres utenfor i IDE.

ARGUMENT=${1}

if [[ $ARGUMENT == fpformidling ]] || [[ $ARGUMENT == fpoppdrag ]] || [[ $ARGUMENT == fptilbake ]]; then
  sh ./setup-lokal-utvikling.sh $ARGUMENT

  if [ -f .env ]; then
    echo "Bruker eksisterende .env fil: $(pwd)/.env"
  else
    sh ./update-versions.sh
  fi
  cp .env docker-compose-lokal/.env

  docker-compose -f docker-compose-lokal/docker-compose.yml pull --include-deps fpsak-frontend
  docker-compose -f docker-compose-lokal/docker-compose.yml up --detach fpsak-frontend

elif [[ $ARGUMENT == down ]]; then
  docker-compose -f docker-compose-lokal/docker-compose.yml down
else
  echo "Argumentene som støttes er: fpformidling, fpoppdrag eller fptilbake."
fi
