#!/usr/bin/env bash
# Dette scriptet setter opp hele verdikjeden i Docker utenom fpfrontend slik at du kan kjøre opp fpfrontend lokalt.
operativsystem="windows"
software="docker-dekstop"
while [ -n "$1" ]; do # while loop starts
    case "$1" in
    -o|--operativsystem)
        if [[ "$2" = "mac" || "$2" = "windows" ]] ; then
            operativsystem="$2"
        else
            echo "Ukjent operativsystem. Defaulter til Windows."
        fi
        shift
        ;;
    -s|--software)
        if [[ "$2" = "colima" || "$2" = "docker-dekstop" ]] ; then
            software="$2"
        else
            echo "Ukjent operativsystem. Defaulter til Docker-Dekstop."
        fi
        shift
        ;;
    --help)
        echo "usage: ./setup-lokal-utvikling.sh [options]"
        echo ""
        echo "Options:"
        echo "-o|--operativsystem       <windows|mac>               Default: windows"
        echo "-s|--software             <colima|docker-dekstop>     Velg mellom colima og docker-dekstop (colima kjører opp oracle for mac uansett)"
        echo "                                                      default: docker-dekstop"
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
    if [[ $operativsystem = "mac" ]] ; then
        sh ./setup-lokal-utvikling-mac.sh --software "$software" down
    else
        docker-compose -f docker-compose-lokal/compose.yml down
    fi
    exit 0
fi


if [[ $operativsystem = "mac" ]] ; then
    sh ./setup-lokal-utvikling-mac.sh --software "$software" fpfrontend
else
    sh ./setup-lokal-utvikling.sh fpfrontend
fi

if [ -f .env ]; then
    echo "Bruker applikasjonsversjonene som er definert i eksisterende .env fil: $(pwd)/.env"
else
    cp docker-compose-lokal/.env .env
fi

docker-compose -f docker-compose-lokal/compose.yml pull
docker-compose -f docker-compose-lokal/compose.yml up --detach --scale fpfrontend=0
