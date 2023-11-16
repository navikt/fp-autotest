# Lokalt oppsett

**Standard NAV-oppsett** og **Oppsett for foreldrepenger** trenger du bare gjøre en gang.

## Standard NAV-oppsett
1. Installer [Docker Desktop](https://www.docker.com/products/docker-desktop), [JDK16](https://adoptopenjdk.net/?variant=openjdk16&jvmVariant=hotspot), Git og [Maven](https://maven.apache.org/download.cgi).
2. Docker Desktop har maksimum 2 GiB minne som standard. Gå inn i innstillinger og endre dette til minst 8 GiB (gitt at du har minst 16GB minne på maskinen).
3. Sett opp SSH-nøkkel for Git. Se [stegvis forklaring](github-ssh-key.md).
4. Sett opp "Personal access tokens" for din Github-bruker som skal brukes for Maven og Docker. Se [stegvis forklaring](github-personal-access-tokens.md).
5. Sett opp Mavens "settings.xml" for tilgang mot NAVs repositories. Se [eksempelfil og stegvis forklaring](maven-settings.md).
6. Docker imagene som brukes er publisert på GAR. Du må derform autentiser deg mot GAR:
   1. Du må ha gcloud installert
   2. Logg inn gcloud auth login
   3. Legg inn repo i credentials helper `gcloud auth configure-docker europe-north1-docker.pkg.dev`
   4. Hent tokenet og logge inn i docker:
    ```
   gcloud auth print-access-token \
   | docker login \
   -u oauth2accesstoken \
   --password-stdin https://europe-north1-docker.pkg.dev
    ```

## Oppsett for foreldrepenger
1. Gå til katalogen der du vil ha kodeprosjektene dine og kjør `git clone git@github.com:navikt/fp-autotest.git`
2. Kjør `cd fp-autotest`
3. Kjør `resources/keystore/make-dummy-keystore.sh`
4. Legg til følgende i hosts-filen (på Mac/Linux: "/etc/hosts", på Windows: "C:\Windows\System32\drivers\etc\hosts"):
```
127.0.0.1  fptilbake fpoppdrag fpformidling fprisk fpabonennt fpfrontend fpsak fpabakus vtp oracle postgres
```

## Kjøring av fp-autotest via Docker Compose
Docker Compose brukes til å sette opp verdikjeden for testing lokalt, i pipeline (GA og Jenkins), og utvikling lokalt.

* Ønsker du å bruke Docker Compose for lokal utvikling se i avsnittet "Docker Compose for lokal utvikling". Dette passer for
deg som ønsker å kjøre en eller flere av applikasjonene utenfor i en IDE for å kunne debugge. Ønsker du å se på eksempler
på hvordan dette kan gjøres se: [lokal utvikling eksempler](lokal-utvikling-eksempler.md).

* Ønsker du å bruk docker-compose til å kjøre gjennom tester se i avsnittet "Docker Compose for utvikling av tester". 
Dette passer for deg som vil utvikler ny tester eller teste funksjonelt NB. dette kjører opp hele verdikjeden og passer
ikke for de som har en PC som er dårlig speccet (dvs. du trenger 12GB RAM eller mer på datamaskinen).

### Docker Compose for lokal utvikling
I en del situasjoner ønsker man ikke å kjøre opp hele verdikjeden, men bare de applikasjonene som er nødvendige. 
For å bruke docker-compose for lokal utvikling er det laget flere scripts – som ligger i mappen "_lokal-utvikling/_" – 
som skal gjøre dette lettere. Scriptene som finnes der, og kan brukes til lokal utvikling, er: 

1)  `lokal-utvikling-fpsak.sh`: Brukes for lokal utvikling av FPSAK.
2)  `lokal-utvikling-fpfrontend.sh`: Brukes for lokal utvikling av FPFRONTEND.
3)  `lokal-utvikling-fpformidling-fprisk-fpabonnent.sh`: Brukes for lokal utvikling av enten FPFORMIDLING, FPRISK eller
FPABONNENT. Dette scritet kjøres med en av følgende argumentene fpformidling, fprisk eller fpabonnent.
4)  `lokal-utvikling-fpoppdrag-og-fptilbake.sh`: Brukes for lokal utvikling av fpoppdrag og fptilbake samtidig. 
5)  `lokal-utvikling-ide.sh`: Gir deg mulighet til å skrive inn hvilke applikasjoner du vil utvikle lokalt i IDE som argumenter.
6)  `setup-lokal-utvikling.sh`: Brukes for lokal utvikling hvis de over ikke skulle dekke ditt behov.

Etter at du har kjørt enten script 1, 2, 3, 4 eller 5 er det mulig å kjøre ned applikasjonene i Docker Compose med å kalle 
scriptet igjen med argumentet "_down_" – på lignende måte som en gjør i docker-compose.


Skulle script 1-5 mot formodning ikke dekke ditt behov, så kan du bruke det sjette scriptet 
`setup-lokal-utvikling.sh` til å sette opp hva enn du måtte ønske. Dette scriptet brukes til å sette opp miljøvariablene
slik at de peker ut på applikasjonene som du kjører utenfor Docker Compose. Når du kjører dette scriptet spesifiserer
du hvilke applikasjoner du ønsker å kjøre utenfor docker-compose (og valgfritt, om du ønsker å kjøre opp mer av verdikjeden innenfor Docker Compose):

    ./setup-lokal-utvikling.sh [APPLIKASJON_UTENFOR_DOCKER_COMPOSE ...]

Etter at du har kjørt scriptet vil det lages en mappen: *lokal-utvikling/docker-compose-lokal*; gå inn i denne mappen.
Denne mappen inneholder riktig konfigurasjonen for oppsettet i Docker Compose. Som standard så hentes den siste versjon 
av Docker imagene til samtlige applikasjoner. Ønsker du mot formodning en annen versjon kan du gjøre dette ved å kjøre 
følgende kommando:

    ./update-versions.sh <APPLIKASJONSNAVN> <VERSION>

Deretter henter du ned Docker imagene og kjører opp de spesifiserte tjenestene ved å bruke en av alternativene under.

Alternativ 1:

    docker-compose pull [SERVICE ...]
    docker-compose up --detach  [--scale APPLIKASJON_UTENFOR_DOCKER_COMPOSE=0] [SERVICE...]
    
Alternativ 2:

    docker-compose up --quiet-pull --detach [--scale APPLIKASJON_UTENFOR_DOCKER_COMPOSE=0] [SERVICE...]

* _--quiet-pull_: Henter ned Docker images som er spesifisert i _[SERVICE...]_ med versjon fra _.env_-filen.    
* _--detach_: Referer til "detatched mode" og kjører da alt opp i bakgrunnen slik at det ikke okkuperer terminalen din.
* _[--scale APPLIKASJON_UTENFOR_DOCKER_COMPOSE=0]_: Her spesifiserer du at du ikke ønsker å kjøre opp en eller flere tjenester.
* _[SERVICE...]_: Det siste ordet/ordene referer til hvilke tjenste(r) som skal kjøres (med sine respektive avhengigeter).

Forskjellen mellom de to alternativene over er at i den første får du muligheten til å hoppe over _"pull"_ steget hvis du
ikke ønsker å hente ned "en ny siste versjon" (hvis det har vært endringer på master nylig). Den tar da utgangspunkt i et 
image som du har lastet ned tidligere (f.eks. for noen timer eller dager siden). For det andre alternativet henter du siste 
versjon for hver gang du skriver inn kommandoet. 

**NB: Her er det viktig å kjøre opp tjenestene i riktig rekkefølge. Gyldige applikasjonsnavn er: oracle, postgres, vtp, 
fpabakus, fpsak, fpfrontend, fpformidling, fpoppdrag og fptilbake.**

 Mer informasjon og eksempler på hvordan dette gjøres kan du finne her: [lokal utvikling eksempler](lokal-utvikling-eksempler.md).


### Docker Compose for utvikling av tester
I prosjektet finnes det en docker-compose fil som befinner seg under `resources/pipeline/compose.yml`. Denne blir
brukt til å kjøre opp verdikjeden i Github Action. For å kjøre opp HELE verdikjeden kan du gå til katalogen hvor 
*compose.yml* filen befinner seg (_resources/pipeline_) og kjøre følgende:

1. Sette hvilke versjoner som skal kjøres opp: `./update-versions.sh`
    1. Kommandoet over brukes den siste versjon av alle applikasjonene (dvs. "latest"). Ønsker du en spesisfikk versjon 
    for en spesifikk applikasjon kan du kjøre kommandoen med følgende argumenter:
        `./update-versions.sh <APPLIKASJONSNAVN> <VERSION>` 
2. Hente ned oppdaterte Docker-images:`docker-compose pull`
3. Starte alle Docker-containerene: `docker-compose up -d`
