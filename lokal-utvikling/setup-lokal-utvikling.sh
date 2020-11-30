#!/usr/bin/env bash

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

while [ -n "$1" ]; do # while loop starts

    case "$1" in
      -m|--mock)
        mock_applikasjon+=("$2")
        shift
        ;;
      --help)
        echo "usage: ./setup-lokal-utvikling.sh [options] [APPLIKASJON_UTENFOR_DOCKER_COMPOSE ...]"
        echo ""
        echo "Options:"
        echo "-m,--mock <applikasjon>     Her kan du velge å mocke ut spesifikke applikasjoner istedenfor å bruke den"
        echo "                            faktiske applikasjonen. En mock av applikasjonen i VTP blir dermed brukt."
        echo "                            Applikasjoner som kan mockes ut er fptilbake, fpoppdrag og fpformidling."
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
fi

if [ -d "$folder" ]; then
  rm -r $folder
fi
mkdir $folder


cd ../resources/pipeline
settPorterSomSkalErstattes ${applikasjoner[0]}
for f in {.*,*}; do
  if [[ $f != .env ]]  && [[ $f != *.sh ]] && [[ $f != fpsak-docker-compose.yml ]] && [[ $f != autotest.list ]]; then
    if [[ -f "$f" ]]; then
      cp "$f" "${relativ_path}/$f"
    elif [[ $f == frontend ]] || [[ $f == oracle-init ]] || [[ $f == postgres-init ]]; then
      cp -r "$f" "${relativ_path}"
    fi
  fi
done


cd $relativ_path
for applikasjon in "${applikasjoner[@]}"; do
  settPorterSomSkalErstattes $applikasjon
  for f in {.*,*}; do
    if [ -f "$f" ] && [[ $f != .env ]]  && [[ $f != *.sh ]] && [[ $f != fpsak-docker-compose.yml ]] && [[ $f != autotest.list ]]; then
      for ((i=0;i<${#replace_port_array[@]};++i)); do
        sed -i.bak "s/${applikasjon}:${replace_port_array[i]}/host.docker.internal:${with_port_array[i]}/g" "$f"
        rm $f.bak
      done
    fi
  done
done

applikasjoner_som_kan_mockes_ut=(fpoppdrag fptilbake fpformidling fprisk)
echo "Mocker ut følgende applikasjoner: ${mock_applikasjon[@]}"
for applikasjon in "${mock_applikasjon[@]}"; do
  if [[ "${applikasjoner_som_kan_mockes_ut[@]}" =~ "${applikasjon}" ]]; then
    if [[ "$*" == *vtp* ]]; then
      sed -i.bak "s*${applikasjon}:8080*host.docker.internal:8060/rest/dummy*g" "docker-compose.yml"
    else
      sed -i.bak "s*${applikasjon}:8080*vtp:8060/rest/dummy*g" "docker-compose.yml"
    fi
    if [[ "${applikasjon}" =~ "fpoppdrag"  ]] && [[ "$*" == *fpfrontend* ]] ; then
      sed -i.bak "s*localhost:9000/fpoppdrag/api*vtp:8060/rest/dummy/fpoppdrag/api*g" "docker-compose.yml"
    fi
    rm docker-compose.yml.bak
  else
    echo "Mock av ${applikasjon} finnes ikke – beholder konfigurasjon som den er."
  fi
done

cd ../../resources/pipeline
cp "update-versions.sh" "${relativ_path}"
cp "update-versions.sh" "${relativ_path}/.."

sh "${relativ_path}"/update-versions.sh
