#!/usr/bin/env bash
# Dette scriptet setter opp hele verdikjeden i Docker utenom fpfrontend slik at du kan kjøre opp fpfrontend lokalt.

ARGUMENT=${1}

if [[ $ARGUMENT == down ]]; then
    docker-compose -f docker-compose-lokal/compose.yml down
    exit 0
fi

sh ./setup-lokal-utvikling.sh fpfrontend

if [ -f .env ]; then
    echo "Bruker applikasjonsversjonene som er definert i eksisterende .env fil: $(pwd)/.env"
else
    cp docker-compose-lokal/.env .env
fi

docker-compose -f docker-compose-lokal/compose.yml pull
docker-compose -f docker-compose-lokal/compose.yml up --detach --scale fpfrontend=0
