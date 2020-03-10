#!/usr/bin/env bash

TRIGGER=${1}

if [ -z ${1+x} ]; then
  echo trigger="navikt/fpsak-autotest" >>environment.properties
else
  echo trigger="$TRIGGER" >>environment.properties
fi

awk 'NR <= 5' ../pipeline/.env >> environment.properties
