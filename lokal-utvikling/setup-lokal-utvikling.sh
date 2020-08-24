#!/usr/bin/env bash

hentSedArguement () {
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
      -i|--inkluder)
        med_applikasjon+=("$2")
        shift
        ;;
      --help)
        echo "usage: ./setup-lokal-utvikling.sh [options] [APPLIKASJON_UTENFOR_DOCKER_COMPOSE ...]"
        echo ""
        echo "Options:"
        echo "-i,--inkluder <arg>     Her kan du spesifisere applikasjoner, som vanligvis ikke spinnes opp,"
        echo "                        til å kjøre i Docker Compose. Eksempler på dette er fptilbake, fpoppdrag"
        echo "                        og fpformidling hvor mock i vtp brukes som standard."
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
hentSedArguement ${applikasjoner[0]}
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
  hentSedArguement $applikasjon
  for f in {.*,*}; do
    if [ -f "$f" ] && [[ $f != .env ]]  && [[ $f != *.sh ]] && [[ $f != fpsak-docker-compose.yml ]] && [[ $f != autotest.list ]]; then
      for ((i=0;i<${#replace_port_array[@]};++i)); do
        sed -i.bak "s/${applikasjon}:${replace_port_array[i]}/host.docker.internal:${with_port_array[i]}/g" "$f"
        rm $f.bak
      done
    fi
  done
done


if [[ "$*" != *fpoppdrag* ]] && [[ ! "${med_applikasjon[*]}" =~ "fpoppdrag" ]]; then
  echo "Argumentet inneholder IKKE fpoppdrag; bruker mock i vtp istedenfor den faktiske applikasjonen."
  if [[ "$*" == *vtp* ]]; then
    sed -i.bak "s*fpoppdrag:8080*host.docker.internal:8060/rest/dummy*g" "docker-compose.yml"
  else
    sed -i.bak "s*fpoppdrag:8080*vtp:8060/rest/dummy*g" "docker-compose.yml"
  fi
  if [[ "$*" == *fpfrontend* ]]; then
    sed -i.bak "s*localhost:9000/fpoppdrag/api*vtp:8060/rest/dummy/fpoppdrag/api*g" "docker-compose.yml"
  fi
  rm docker-compose.yml.bak
else
  echo "Argumentet inneholder fpoppdrag; setter opp for kjøring med fpoppdrag."
fi

if [[ "$*" != *fptilbake* ]] && [[ ! "${med_applikasjon[*]}" =~ "fptilbake" ]]; then
  echo "Argumentet inneholder IKKE fptilbake; bruker mock i vtp istedenfor den faktiske applikasjonen."
  if [[ "$*" == *vtp* ]]; then
    sed -i.bak "s*fptilbake:8080*host.docker.internal:8060/rest/dummy/boolean/false*g" "docker-compose.yml"
  else
    sed -i.bak "s*fptilbake:8080*vtp:8060/rest/dummy/boolean/false*g" "docker-compose.yml"
  fi
  rm docker-compose.yml.bak
else
  echo "Argumentet inneholder fptilbake; setter opp for kjøring med fptilbake."
fi

if [[ "$*" != *fpformidling* ]] && [[ ! "${med_applikasjon[*]}" =~ "fpformidling" ]]; then
  echo "Argumentet inneholder IKKE fpformidling; bruker mock i vtp istedenfor den faktiske applikasjonen."
  if [[ "$*" == *vtp* ]]; then
    sed -i.bak "s*fpformidling:8080*host.docker.internal:8060/rest/dummy*g" "docker-compose.yml"
  else
    sed -i.bak "s*fpformidling:8080*vtp:8060/rest/dummy*g" "docker-compose.yml"
  fi
  rm docker-compose.yml.bak
else
  echo "Argumentet inneholder fpformidling; setter opp for kjøring med fpformidling."
fi


cd ../../resources/pipeline
cp "update-versions.sh" "${relativ_path}"
cp "update-versions.sh" "${relativ_path}/.."

sh "${relativ_path}"/update-versions.sh
