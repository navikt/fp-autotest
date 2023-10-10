#!/usr/bin/env bash

cd $(dirname $0)

TRIGGER=${1}

if [ -z ${1+x} ]; then
  echo trigger="navikt/fp-autotest" >>environment.properties
else
  echo trigger="$TRIGGER" >>environment.properties
fi

cat ../pipeline/.env >> environment.properties
