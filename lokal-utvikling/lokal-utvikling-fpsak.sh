#!/usr/bin/env bash
# Dette scriptet setter opp avhengighetene til fpsak, slik at fpsak kan kjøres utenfor i IDE. Dette scriptet mocker
# ut fptilbake, fpoppdrag og fpformidling og spinner heller ikke opp fpabonnent. Ønskes du ikke dette må du gjøre det manuelt.
operativsystem="windows"
software="docker-dekstop"
while [ -n "$1" ]; do # while loop starts
    case "$1" in
    -o|--operativsystem)
        if [[ "$2" = "mac-colima" || "$2" = "mac-dekstop" ]] ; then
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
    sh ./setup-lokal-utvikling-mac.sh --software "$software" --mock fptilbake --mock fpoppdrag --mock fpformidling --mock fprisk fpsak
else
    sh ./setup-lokal-utvikling.sh --mock fptilbake --mock fpoppdrag --mock fpformidling --mock fprisk fpsak
fi

if [ -f .env ]; then
    echo "Bruker applikasjonsversjonene som er definert i eksisterende .env fil: $(pwd)/.env"
else
    cp docker-compose-lokal/.env .env
fi

docker-compose -f docker-compose-lokal/compose.yml pull --include-deps oracle fpabakus
docker-compose -f docker-compose-lokal/compose.yml pull fpfrontend
docker-compose -f docker-compose-lokal/compose.yml up --detach --scale fpsak=0 fpfrontend
