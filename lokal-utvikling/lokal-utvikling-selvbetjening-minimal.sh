#!/usr/bin/env bash

system="windows"
while [ -n "$1" ]; do # while loop starts
    case "$1" in
    -s|--system)
        if [[ "$2" = "mac-colima" || "$2" = "mac-dekstop" ]] ; then
            system="$2"
        else
            echo "Ukjent system. Defaulter til Windows."
        fi
        shift
        ;;
    --help)
        echo "usage: ./setup-lokal-utvikling.sh [options]"
        echo ""
        echo "Options:"
        echo "-s, --system <windows|mac-colima|mac-dekstop>     Velg mellom windows og mac (bare colima eller colima og dekstop)"
        echo "                                                  default: windows"
        echo
        exit 0
        ;;
    *)
        break
        ;;
    esac
    shift
done

ARGUMENT=${1}

if [[ $ARGUMENT == down ]]; then
    if [[ $system = "mac-dekstop" || $system = "mac-colima" ]] ; then
        sh ./setup-lokal-utvikling-mac.sh --system "$system" down
    else
        docker-compose -f docker-compose-lokal/compose.yml down
    fi
    exit 0
fi


if [[ $system = "mac-dekstop" || $system = "mac-colima" ]] ; then
    sh ./setup-lokal-utvikling-mac.sh --system "$system" --mock fptilbake --mock fpoppdrag --mock fpformidling --mock fprisk
else
    sh ./setup-lokal-utvikling.sh --mock fptilbake --mock fpoppdrag --mock fpformidling --mock fprisk
fi


if [ -f .env ]; then
    echo "Bruker applikasjonsversjonene som er definert i eksisterende .env fil: $(pwd)/.env"
else
    cp docker-compose-lokal/.env .env
fi
docker-compose -f docker-compose-lokal/compose.yml up --detach fpfrontend foreldrepengesoknad foreldrepengeoversikt svangerskapspengesoknad

