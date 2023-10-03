#!/usr/bin/env bash
# Dette scriptet setter opp avhengighetene til fpsak, slik at fpsak kan kjøres utenfor i IDE. Dette scriptet mocker
# ut fptilbake, fpoppdrag og fpformidling og spinner heller ikke opp fpabonnent. Ønskes du ikke dette må du gjøre det manuelt.

ARGUMENT=${1}

if [[ $ARGUMENT == down ]]; then
    docker-compose -f docker-compose-lokal/compose.yml down
    exit 0
fi

sh ./setup-lokal-utvikling.sh --mock fptilbake --mock fpoppdrag --mock fpformidling --mock fprisk fpsak

if [ -f .env ]; then
    echo "Bruker applikasjonsversjonene som er definert i eksisterende .env fil: $(pwd)/.env"
else
    cp docker-compose-lokal/.env .env
fi

docker-compose -f docker-compose-lokal/compose.yml pull --include-deps oracle fpabakus
docker-compose -f docker-compose-lokal/compose.yml pull fpfrontend
docker-compose -f docker-compose-lokal/compose.yml up --detach --scale fpsak=0 fpfrontend
