#!/bin/bash
software="docker-dekstop"
while [ -n "$1" ]; do # while loop starts
    case "$1" in
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


if [[ ${1} == down ]]; then
    docker context use colima
    docker-compose -f ../resources/pipeline/compose.yml down
    colima stop

    cd docker-compose-lokal || exit
    if [[ $software = "colima" ]] ; then
        docker context use colima-arm
        docker-compose down
        colima --profile arm stop
    else
        docker context use desktop-linux
        docker-compose down
    fi

else
    echo "Setting up oracle in colima"
    # Setting up oracle i x86_64 instans
    colima start --memory 3 --arch x86_64
    docker context use colima
    docker-compose -f ../resources/pipeline/compose.yml up -d oracle

    echo "Konfigurer docker-compose-lokal/ for å kjøre følgende applikasjoner i IDEA $*"
    # Setting up resten
    if [[ $software = "colima" ]] ; then
        colima start --profile arm --cpu 4 --memory 12 --arch aarch64
        docker context use colima-arm
        sh ./setup-lokal-utvikling.sh --system colima "$@" oracle
    else
        docker context use desktop-linux
        sh ./setup-lokal-utvikling.sh "$@" oracle
    fi

    cd docker-compose-lokal || exit
    # fjerner alle oracle objekter, herunder også oracle objekter under depends_on
    yq -i 'del(.. | select(has("oracle")).oracle)' compose.yml

    # fjerner oracle keys fra depends_on
    yq -i 'del(.services.*.depends_on[] | select(. == "oracle"))' compose.yml

fi
