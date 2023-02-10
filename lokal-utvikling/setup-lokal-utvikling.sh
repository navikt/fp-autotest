#!/usr/bin/env bash

## FUNCTIONS
settPorterSomSkalErstattes () {
  applikasjon=$1
  case $applikasjon in
    oracle)
      replace_port_array=("1521")
      with_port_array=("1521")
      ;;
    postgres)
      replace_port_array=("5432")
      with_port_array=("5432")
      ;;
    vtp)
      replace_port_array=("8060" "8063" "8636" "9093" "9092" "8389")
      with_port_array=("8060" "8063" "8636" "9093" "9092" "8389")
      ;;
    authserver)
      replace_port_array=("8086")
      with_port_array=("8086")
      ;;
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
    fpsak)
      replace_port_array=("8080")
      with_port_array=("8080")
      ;;
    fpfrontend)
      replace_port_array=("9000")
      with_port_array=("9000")
      ;;
    fprisk)
      replace_port_array=("8080")
      with_port_array=("8075")
      ;;
    fpfordel)
      replace_port_array=("8080")
      with_port_array=("8090")
      ;;
    fpinfo)
      replace_port_array=("8080")
      with_port_array=("8040")
      ;;
    fpsoknad-mottak)
      replace_port_array=("9001")
      with_port_array=("9001")
      ;;
    fpabonnent)
      replace_port_array=("8080")
      with_port_array=("8065")
      ;;
    "")
      replace_port_array=()
      with_port_array=()
      ;;
    *)
      replace_port_array=()
      with_port_array=()
      echo "Angitt applikasjon '$applikasjon' er ikke med i verdikjeden; ignorerer denne variabelen."
  esac
}

## MAIN SCRIPT
host_adresse="host.docker.internal"
while [ -n "$1" ]; do # while loop starts
    case "$1" in
      -m|--mock)
        mock_applikasjon+=("$2")
        shift
        ;;
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
        echo "-m, --mock <applikasjon>                  Her kan du velge å mocke ut spesifikke applikasjoner istedenfor å bruke den"
        echo "                                          faktiske applikasjonen. En mock av applikasjonen i VTP blir dermed brukt."
        echo "                                          Applikasjoner som kan mockes ut er fptilbake, fpoppdrag og fpformidling."
        echo "                                          default: ingen applikasjoner mockes ut"
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
relativ_path=../../lokal-utvikling/$folder

if [ $# -eq 0 ]; then
  echo "Applikasjonnavn er ikke spesifisert. Lager dermed oppsett for å kjøre den minste verdikjeden i Docker Compose."
else
  echo "Applikasjonen(e) som må startes opp i IDE er: ${applikasjoner[*]}"
  echo "Containernavn for ${applikasjoner[*]} erstattes med ${host_adresse}"
fi

if [ -d "$folder" ]; then
  rm -r $folder
fi
mkdir $folder


# Copy all docker-compose file to separate docker-compose-lokal folder for local instance
cd ../resources/pipeline
settPorterSomSkalErstattes ${applikasjoner[0]}
for f in {.*,*}; do
    if [[ -f "$f" ]]; then
        cp "$f" "${relativ_path}/$f"
    elif [[ $f == oracle-init ]] || [[ $f == postgres-init ]] || [[ $f == tokenx ]] || [[ $f == authserver ]]; then
        cp -r "$f" "${relativ_path}"
    fi
done

# Redirect alle kall for angitte applikasjoner lokalt på maskinen din. F.eks. ved valg av fpabonnent erstattes
# http://fpabonnent:8080/fpabonnent med http://host.docker.internal:8065/fpabonnent
cd $relativ_path
for applikasjon in "${applikasjoner[@]}"; do
  settPorterSomSkalErstattes $applikasjon
  for f in {.*,*}; do
    if [ -f "$f" ] && [[ $f != .env ]]  && [[ $f != *.sh ]] && [[ $f != *.sql ]]; then # Only files && not .env %% not script files && not sql files
      for ((i=0;i<${#replace_port_array[@]};++i)); do
        sed -i.bak "s/${applikasjon}:${replace_port_array[i]}/${host_adresse}:${with_port_array[i]}/g" "$f"
        rm $f.bak
      done
    fi
  done
done

# Ved mocking av våre interne applikasjoner bruker vi en stub i vtp. Bare støtte for fpoppdrag, fptilbake, fpformidling eller fprisk
if [ ${#mock_applikasjon[@]} ]; then
    echo "Mocker ut følgende applikasjoner: ${mock_applikasjon[@]}"
    applikasjoner_som_kan_mockes_ut=(fpoppdrag fptilbake fpformidling fprisk)
    for applikasjon in "${mock_applikasjon[@]}"; do
        if [[ "${applikasjoner_som_kan_mockes_ut[@]}" =~ "${applikasjon}" ]]; then
            if [[ "$*" == *vtp* ]]; then
                sed -i.bak "s*${applikasjon}:8080*${host_adresse}:8060/rest/dummy*g" "compose.yml"
                sed -i.bak "s*${applikasjon}:8080*${host_adresse}:8060/rest/dummy*g" "common.env"
            else
                sed -i.bak "s*${applikasjon}:8080*vtp:8060/rest/dummy*g" "compose.yml"
                sed -i.bak "s*${applikasjon}:8080*vtp:8060/rest/dummy*g" "common.env"
            fi

            if [[ "${applikasjon}" =~ "fpoppdrag"  ]] && [[ "$*" == *fpfrontend* ]] ; then
                sed -i.bak "s*localhost:9000/fpoppdrag/api*vtp:8060/rest/dummy/fpoppdrag/api*g" "compose.yml"
                sed -i.bak "s*localhost:9000/fpoppdrag/api*vtp:8060/rest/dummy/fpoppdrag/api*g" "common.env"
            fi

            rm compose.yml.bak
            rm common.env.bak
        else
            echo "Mock av ${applikasjon} finnes ikke – beholder konfigurasjon som den er"
        fi
    done
fi


cd ../../resources/pipeline
cp "update-versions.sh" "${relativ_path}"
cp "update-versions.sh" "${relativ_path}/.."

sh "${relativ_path}"/update-versions.sh
