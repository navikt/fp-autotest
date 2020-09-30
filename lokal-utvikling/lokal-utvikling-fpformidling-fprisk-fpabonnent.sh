#!/usr/bin/env bash
# Dette scriptet setter opp avhengighetene til enten fpformidling, fprisk eller fpabonnent. Bare ett argument kan velges.
# Avhengig av applikasjonene du velger så vil dette scriptet mocke ut fpoppdrag, fptilbake, fpformidling og/elelr fprisk.
# F.eks. velges fpformidling så mockes fpoppdrag, fptilbake og fprisk ut.

ARGUMENT=${1}

applikasjoner_som_støttes_av_script=(fpformidling fprisk fpabonnent)
applikasjoner_som_kan_mmockes=(fpoppdrag fptilbake fpformidling fprisk)
if [[ "${applikasjoner_som_støttes_av_script[@]}" =~ "$ARGUMENT" ]]; then

  for app in "${applikasjoner_som_kan_mmockes[@]}"; do
    if [[ $app != $ARGUMENT ]]; then
      options+=" --mock $app"
    fi
  done
  sh ./setup-lokal-utvikling.sh $options $ARGUMENT

  if [ -f .env ]; then
    echo "Bruker applikasjonsversjonene som er definert i eksisterende .env fil: $(pwd)/.env"
  else
    cp docker-compose-lokal/.env .env
  fi

  docker-compose -f docker-compose-lokal/docker-compose.yml pull --include-deps fpfrontend
  docker-compose -f docker-compose-lokal/docker-compose.yml up --detach fpfrontend

elif [[ $ARGUMENT == down ]]; then
  docker-compose -f docker-compose-lokal/docker-compose.yml down
else
  echo "Argumentene som støttes er: fpformidling, fprisk eller fpabonnent"
fi
