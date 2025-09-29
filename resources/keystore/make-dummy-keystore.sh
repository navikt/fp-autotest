#!/usr/bin/env bash

SERVERKEYSTORE=serverkeystore.p12
CERT_PEM=cert.pem
KEY_PEM=key.pem
KEYSTORE_FILE=keystore.jks
KEYSTORE_PASS=vtpvtp
TRUSTSTORE_FILE=truststore.jks
TRUSTSTORE_PASS=vtpvtp
CONFIG_FILE=csr.txt

cd $(dirname "$0")

mkdir -p ~/.modig
cp ${CONFIG_FILE} ~/.modig
cd ~/.modig

openssl req -x509 -newkey rsa:2048 -keyout ${KEY_PEM} -out ${CERT_PEM} -days 900 -nodes -config ${CONFIG_FILE}

# local-host SSL
rm -f ${KEYSTORE_FILE}
if test -f ${TRUSTSTORE_FILE};
then
  echo "Fjerner gammel truststore og erstatter med ny"
  rm ${TRUSTSTORE_FILE}
fi
openssl pkcs12 -export -name localhost-ssl -in ${CERT_PEM} -inkey ${KEY_PEM} -out ${SERVERKEYSTORE} -password pass:${KEYSTORE_PASS}
keytool -importkeystore -destkeystore ${KEYSTORE_FILE} -srckeystore ${SERVERKEYSTORE} -srcstoretype pkcs12 -alias localhost-ssl -storepass ${KEYSTORE_PASS} -keypass ${KEYSTORE_PASS} -srcstorepass ${KEYSTORE_PASS}
rm -f ${SERVERKEYSTORE}

# app-key (jwt uststeder bl.a. i mocken, vi bruker samme noekkel per naa):
openssl pkcs12 -export -name app-key -in ${CERT_PEM} -inkey ${KEY_PEM} -out ${SERVERKEYSTORE} -password pass:${KEYSTORE_PASS}
keytool -importkeystore -destkeystore ${KEYSTORE_FILE} -srckeystore ${SERVERKEYSTORE} -srcstoretype pkcs12 -alias app-key -storepass ${KEYSTORE_PASS} -keypass ${KEYSTORE_PASS} -srcstorepass ${KEYSTORE_PASS}

# clean up
rm -f ${SERVERKEYSTORE}
rm -f ${KEY_PEM}

# truststore for SSL:
if test -f ${TRUSTSTORE_FILE};
then
  echo "Fjerner gammel truststore og erstatter med ny"
  rm ${TRUSTSTORE_FILE}
fi
keytool -import -trustcacerts -alias localhost-ssl -file ${CERT_PEM} -keystore ${TRUSTSTORE_FILE} -storepass ${TRUSTSTORE_PASS} -noprompt

# clean up
rm ${CERT_PEM}

echo "${KEYSTORE_PASS}" > keystore_creds
echo "${KEYSTORE_PASS}" > key_creds
echo "${TRUSTSTORE_PASS}" > truststore_creds
