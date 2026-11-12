#!/bin/sh
# Kjører ett steg i testsak-verktøyet. Kompilerer prosjektet først hvis kildekoden er endret.
# Bruk: verktoy/testsak.sh <steg> ['{"param":"verdi"}']   (verktoy/testsak.sh hjelp lister stegene)
set -eu

verktoy=$(cd "$(dirname "$0")" && pwd)
rot=$(dirname "$verktoy")
klasser="$rot/target/classes"
cp_fil="$rot/target/verktoy-classpath.txt"

utdatert() {
    [ ! -f "$cp_fil" ] && return 0
    [ -n "$(find "$rot/src/main" "$rot/pom.xml" -newer "$cp_fil" -type f 2>/dev/null | head -n 1)" ]
}

# cplt setter JAVA_TOOL_OPTIONS, og JVM-en melder det da på stderr. Send verdiene som vanlige argumenter i stedet.
jvm_args="${JAVA_TOOL_OPTIONS:-}"
unset JAVA_TOOL_OPTIONS
set -f

if utdatert; then
    echo "Kompilerer fp-autotest …" >&2
    mvn_args="-q"
    # I cplt-sandkassa er ~/.m2/settings.xml ikke lesbar; bygg da offline mot den lokale Maven-cachen.
    if [ -e "$HOME/.m2/settings.xml" ] && [ ! -r "$HOME/.m2/settings.xml" ]; then
        tom="${TMPDIR:-/tmp}/autotest-verktoy-settings.xml"
        printf '<settings/>\n' > "$tom"
        mvn_args="$mvn_args -o -s $tom"
    fi
    MAVEN_OPTS="${MAVEN_OPTS:-} $jvm_args" mvn $mvn_args -f "$rot/pom.xml" compile dependency:build-classpath \
        -Dmdep.outputFile="$cp_fil" -Dmdep.includeScope=runtime >&2
    # build-classpath lar fila stå urørt når innholdet er likt, så tidsstempelet må oppdateres her.
    touch "$cp_fil"
fi

# shellcheck disable=SC2086
exec java $jvm_args -Dlogback.configurationFile="$verktoy/logback.xml" \
    -cp "$klasser:$(cat "$cp_fil")" no.nav.foreldrepenger.autotest.verktoy.Cli "$@"
