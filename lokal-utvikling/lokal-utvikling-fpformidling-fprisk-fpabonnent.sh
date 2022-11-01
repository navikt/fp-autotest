#!/usr/bin/env bash
# Dette scriptet setter opp avhengighetene til enten fpformidling, fprisk eller fpabonnent. Bare ett argument kan velges.
# Avhengig av hvilken applikasjonen du velger så vil dette scriptet mocke ut fpoppdrag, fptilbake, fpformidling og/elelr fprisk.
# F.eks. velges fpformidling så mockes fpoppdrag, fptilbake og fprisk ut.

ARGUMENT=${1}

SUPPORTED_APPLIACTIONS=(fpformidling fprisk fpabonnent)
applikasjoner_som_kan_mockes=(fpoppdrag fptilbake fpformidling fprisk)
if [[ "${SUPPORTED_APPLIACTIONS[@]}" =~ "$ARGUMENT" ]]; then

  for app in "${applikasjoner_som_kan_mockes[@]}"; do
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
  docker-compose -f docker-compose-lokal/docker-compose.yml up --detach --wait fpfrontend

elif [[ $ARGUMENT == down ]]; then
  docker-compose -f docker-compose-lokal/docker-compose.yml down
else
  echo "Argument som støttes er: fpformidling, fprisk eller fpabonnent"
fi
