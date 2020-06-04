#!/usr/bin/env bash

applikasjoner=( "$@" )
folder=docker-compose-lokal
relativ_path=../../lokal-utvikling/$folder

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
    fpsak-frontend)
      replace_port_array=("9000")
      with_port_array=("9000")
      ;;
    "")
      replace_port_array=()
      with_port_array=()
      ;;
    *)
      replace_port_array=()
      with_port_array=()
      echo "Angitt applikasjon er ikke med i verdikjeden. Gjør derfor ingen endringer for denne applikasjonen."
  esac
}

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


if [[ "$*" != *fpoppdrag* ]]; then
  echo "Argumentet inneholder ikke fpoppdrag. Bruker derfor mock i vtp istedenfor den faktiske applikasjonen."
  if [[ "$*" == *vtp* ]]; then
    sed -i.bak "s*fpoppdrag:8080*host.docker.internal:8060/rest/dummy*g" "docker-compose.yml"
  else
    sed -i.bak "s*fpoppdrag:8080*vtp:8060/rest/dummy*g" "docker-compose.yml"
  fi
  rm docker-compose.yml.bak
fi

if [[ "$*" != *fptilbake* ]]; then
  echo "Argumentet inneholder ikke fptilbake. Bruker derfor mock i vtp istedenfor den faktiske applikasjonen."
  if [[ "$*" == *vtp* ]]; then
    sed -i.bak "s*fptilbake:8080*host.docker.internal:8060/rest/dummy/boolean/false*g" "docker-compose.yml"
  else
    sed -i.bak "s*fptilbake:8080*vtp:8060/rest/dummy/boolean/false*g" "docker-compose.yml"
  fi
  rm docker-compose.yml.bak
fi


if [[ "$*" != *fpformidling* ]]; then
  echo "Argumentet inneholder ikke fpformidling. Bruker derfor mock i vtp istedenfor den faktiske applikasjonen."
  if [[ "$*" == *vtp* ]]; then
    sed -i.bak "s*fpformidling:8080*host.docker.internal:8060/rest/dummy*g" "docker-compose.yml"
  else
    sed -i.bak "s*fpformidling:8080*vtp:8060/rest/dummy*g" "docker-compose.yml"
  fi
  rm docker-compose.yml.bak
fi


cd ../../resources/pipeline
cp "update-versions.sh" "${relativ_path}"
cp "update-versions.sh" "${relativ_path}/.."

