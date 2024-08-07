#!/usr/bin/env bash
# Med dette scriptet kan du angi selv hvilke apper du ønsker å kjøre i IDE på kommandolinjen,
# feks: ./lokal-utvikling-ide.sh fpsak fpformidling

if [[ $1 == down ]]; then
    docker compose -f docker-compose-lokal/compose.yml down
    exit 0
fi

setup=""
scale=""
while (( "$#" )); do
    setup+=" ${1}"
    scale+=" --scale ${1}=0"
    shift
done

sh ./setup-lokal-utvikling.sh ${setup}

if [ -f .env ]; then
    echo "Bruker applikasjonsversjonene som er definert i eksisterende .env fil: $(pwd)/.env"
else
    cp docker-compose-lokal/.env .env
fi

docker compose -f docker-compose-lokal/compose.yml pull
docker compose -f docker-compose-lokal/compose.yml up --detach ${scale}

