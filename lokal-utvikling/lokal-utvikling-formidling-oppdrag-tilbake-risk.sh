#!/usr/bin/env bash
# Dette scriptet setter opp avhengighetene til enten fpformidling, fpoppdrag, fptilbake eller fprisk, slik at disse kan
# kjøres utenfor i IDE.

ARGUMENT=${1}

if [[ $ARGUMENT == fpformidling ]] || [[ $ARGUMENT == fpoppdrag ]] || [[ $ARGUMENT == fptilbake ]] || [[ $ARGUMENT == fprisk ]]; then
  sh ./setup-lokal-utvikling.sh $ARGUMENT

  if [ -f .env ]; then
    echo "Bruker eksisterende .env fil: $(pwd)/.env"
  else
    sh ./update-versions.sh
  fi
  cp .env docker-compose-lokal/.env

  docker-compose -f docker-compose-lokal/docker-compose.yml pull --include-deps fpfrontend
  docker-compose -f docker-compose-lokal/docker-compose.yml up --detach fpfrontend

elif [[ $ARGUMENT == down ]]; then
  docker-compose -f docker-compose-lokal/docker-compose.yml down
else
  echo "Argumentene som støttes er: fpformidling, fpoppdrag, fptilbake eller fprisk."
fi
