#!/usr/bin/env bash

ARGUMENT=${1}


if [[ $ARGUMENT == down ]]; then
    docker-compose -f docker-compose-lokal/docker-compose.yml down
else
    sh ./setup-lokal-utvikling.sh --mock fptilbake --mock fpoppdrag --mock fpformidling --mock fprisk

    if [ -f .env ]; then
        echo "Bruker applikasjonsversjonene som er definert i eksisterende .env fil: $(pwd)/.env"
    else
        cp docker-compose-lokal/.env .env
    fi
    docker-compose -f docker-compose-lokal/docker-compose.yml up --detach fpfrontend foreldrepengesoknad foreldrepengeoversikt
fi
