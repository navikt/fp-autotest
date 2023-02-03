#!/bin/bash
system="mac-dekstop"
while [ -n "$1" ]; do
    case "$1" in
    -s|--system)
        if [[ "$2" = "mac-colima" || "$2" = "mac-dekstop" ]] ; then
            system="$2"
        else
            echo "Ukjent system. Defaulter til mac-dekstop."
        fi
        shift
        ;;
    --help)
        echo "usage: ./setup-lokal-utvikling.sh [options]"
        echo ""
        echo "Options:"
        echo "-s, --system <mac-colima|mac-dekstop>     Velg mellom mac bare med colima eller med både colima og dekstop)"
        echo "                                          default: mac-dekstop"
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
    if [[ $system = "mac-colima" ]] ; then
        docker context use colima-arm
        docker-compose down
        colima --profile arm stop
    else
        docker context use desktop-linux
        docker-compose down
    fi

else
    # Setting up oracle i x86_64 instans
    colima start --memory 3 --arch x86_64
    docker context use colima
    docker-compose -f ../resources/pipeline/compose.yml up -d oracle


    # Setting up resten
    if [[ $system = "mac-colima" ]] ; then
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
