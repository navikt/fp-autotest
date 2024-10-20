#!/usr/bin/env bash

## FUNCTIONS
settPorterSomSkalErstattes () {
  applikasjon=$1
  case $applikasjon in
    fpabakus)
      replace_port_array=("8080")
      with_port_array=("8015")
      ;;
    fpformidling)
      replace_port_array=("8080")
      with_port_array=("8010")
      ;;
    fpoppdrag)
      replace_port_array=("8080")
      with_port_array=("8070")
      ;;
    fptilbake)
      replace_port_array=("8080")
      with_port_array=("8030")
      ;;
    fprisk)
      replace_port_array=("8080")
      with_port_array=("8075")
      ;;
    fpfordel)
      replace_port_array=("8080")
      with_port_array=("8090")
      ;;
    fpabonnent)
      replace_port_array=("8080")
      with_port_array=("8065")
      ;;
    fpoversikt)
      replace_port_array=("8080")
      with_port_array=("8889")
      ;;
    fptilgang)
      replace_port_array=("8080")
      with_port_array=("8050")
      ;;
    fpinntektsmelding)
          replace_port_array=("8080")
          with_port_array=("8040")
          ;;
    *)
      replace_port_array=("")
      with_port_array=("")
  esac
}

## MAIN SCRIPT
host_adresse="host.docker.internal"
while [ -n "$1" ]; do # while loop starts
    case "$1" in
        -s|--system)
            if [[ "$2" =~ "colima"  ]] ; then
                host_adresse="192.168.5.2"
            fi
            shift
            ;;
        --help)
            echo "usage: ./setup-lokal-utvikling.sh [options] [APPLIKASJON_UTENFOR_DOCKER_COMPOSE ...]"
            echo ""
            echo "Options:"
            echo "-s, --system <docker_desktop|colima>      Velg enten docker_desktop eller colima. Brukes bare av colima brukere"
            echo "                                          default: docker_desktop"
            exit 0
            ;;
        *)
            break
            ;;
    esac
    shift
done


applikasjoner=( "$@" )
folder=docker-compose-lokal
relativ_path=../../resources/pipeline

if [ $# -eq 0 ]; then
    echo "Alle applikasjonene er satt opp til å kjøre i docker compose"
else
    echo "Applikasjonen(e) som må startes opp i IDE er: ${applikasjoner[*]}"
    echo "Containernavn for ${applikasjoner[*]} erstattes med ${host_adresse}"
fi

if [ -d "$folder" ]; then
    rm -r $folder
fi
mkdir $folder;
cd $folder || exit
cp -a "${relativ_path}/." "." # Copy all docker-compose file to separate docker-compose-lokal folder for local instance
sh update-versions.sh
cp -f ".env" "../."

# Redirect alle kall for angitte applikasjoner lokalt på maskinen din. F.eks. ved valg av fpabonnent erstattes
# http://fpabonnent:8080/fpabonnent med http://host.docker.internal:8065/fpabonnent
for applikasjon in "${applikasjoner[@]}"; do
    settPorterSomSkalErstattes $applikasjon
    for ((i=0;i<${#replace_port_array[@]};++i)); do
        for f in {.*,*}; do
            if [ -f "$f" ] && [[ $f != *.sh ]] && [[ $f != *.sql ]]; then # Only files %% not script files && not sql files
                sed -i.bak "s/\:\/\/${applikasjon}:${replace_port_array[i]}/\:\/\/${host_adresse}:${with_port_array[i]}/g" "$f"
                rm $f.bak
            fi
        done
    done
done
