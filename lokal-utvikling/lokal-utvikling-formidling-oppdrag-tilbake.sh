#!/usr/bin/env bash

APPLIKASJON=${1}

# Dette scriptet setter opp avhengighetene til enten fpformidling, fpoppdrag eller fptilbake, slik disse kan kjøres utenfor i IDE.

if [[ $APPLIKASJON == fpformidling ]] || [[ $APPLIKASJON == fpoppdrag ]] || [[ $APPLIKASJON == fptilbake ]]; then
  sh ./lokal-utvikling.sh $APPLIKASJON

  if [ -f .env ]; then
    echo "Bruker eksisterende .env fil: $(pwd)/.env"
  else
    sh ./update-versions.sh
  fi

  docker-compose -f docker-compose-lokal/docker-compose.yml up --quiet-pull --detach fpsak-frontend
else
  echo "Argumentene som støttes er: fpformidling, fpoppdrag eller fptilbake."
fi



