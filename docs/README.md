# Lokalt oppsett
## Oppsett for foreldrepenger
1. Gå til katalogen der du vil ha kodeprosjektene dine og kjør `git clone git@github.com:navikt/fpsak-autotest.git`
2. Kjør `cd fpsak-autotest`
3. Kjør `resources/keystore/make-dummy-keystore.sh`
4. Legg til følgende i hosts-filen (på Mac/Linux: "/etc/hosts", på Windows: "C:\Windows\System32\drivers\etc\hosts"):
```
127.0.0.1  fptilbake fpoppdrag fpformidling fpsak-frontend fpsak fpabakus vtp oracle postgres
```

NB. stegene over trenger du bare å gjøre en gang!

## Docker Compose
Docker Compose brukes til å sette opp verdikjeden for testing lokalt, i pipeline (GA og Jenkins), og utvikling lokalt.

* Ønsker du å bruke Docker Compose for lokal utvikling se i avsnittet "Docker Compose for lokal utvikling". Dette passer for
deg som ønsker å kjøre en eller flere av applikasjonene utenfor i en IDE for å kunne debugge. Ønsker du å se på eksempler
på hvordan dette kan gjøres se: [lokal utvikling eksempler](lokal-utvikling-eksempler.md).

* Ønsker du å bruk docker-compose til å kjøre gjennom tester se i avsnittet "Docker Compose for utvikling av tester". Dette passer for deg som vil utvikler ny tester. 
NB. dette kjører opp hele verdikjeden og passer ikke for de som har en PC som er dårlig speccet
(dvs. 8GB RAM eller mindre).

* Ønsker du – som er funksjonell eller ikke-teknisk person – å kjøre gjennom tester kan du følge oppskriften her: 
[oppskrift for funksjonelle](funksjonell-testing-eksempel.md).


### Docker Compose for lokal utvikling
I en del situasjoner ønskes det ikke at hele verdikjeden kjøres opp, men bare det applikasjonene som er nødvendig. 
For å bruke docker-compose for lokal utvikling er det laget et script (*lokal-utvikling.sh* som ligger i mappen 
"_lokal-utvikling/_") som skal gjøre dette lettere. Når du kjører scriptet spesifiserer du hvilke applikasjoner du ønsker å kjøre utenfor docker-compose:

    ./lokal-utvikling [APPLIKASJONER_UTENFOR_DOCKER_COMPOSE ...]

Etter at du har kjørt scriptet vil det lages en mappen: *lokal-utvikling/docker-compose-lokal*: Gå inn i denne mappen.
Denne mappen inneholder spesifikasjonene for å kjøre opp Docker Compose. By default så hentes det siste versjon av alle 
Docker imagene. Ønsker du mot formodning en annen versjon kan du gjøre dette ved følgende kommando:

    ./update-versions.sh <APPLIKASJONSNAVN> <VERSION>

Deretter henter du ned Docker imagene og kjører opp de spesifiserte tjenestene ved å bruke en av alternativene under.

Alternativ 1:

    docker-compose pull [SERVICE...]
    docker-compose up -d [--scale APPLIKASJON_UTENFOR_DOCKER_COMPOSE=0] [SERVICE...]
    
Alternativ 2:

    docker-compose up --quiet-pull --detach [--scale APPLIKASJON_UTENFOR_DOCKER_COMPOSE=0] [SERVICE...]

* _--quiet-pull_: Henter ned Docker images som er spesifisert i _[SERVICE...]_ med versjon fra _.env_-filen.    
* _--detach_: Referer til "detatched mode" og kjører da alt opp i bakgrunnen slik at det ikke okkuperer terminalen din.
* _[--scale APPLIKASJON_UTENFOR_DOCKER_COMPOSE=0]_: Her spesifiserer du at du ikke ønsker å kjøre opp en eller flere tjenester.
* _[SERVICE...]_: Det siste ordet/ordene referer til hvilke tjenste(r) som skal kjøres (med sine respektive avhengigeter).

Forskjellen mellom disse to alternativene over er at i den første får du muligheten til å hoppe over _"pull"_ steget hvis du
ikke ønsker å hente ned "en ny siste versjon" (hvis det har vært endringer på master nylig). Den tar da utgangspunkt i et 
image som du har lastet ned tidligere (f.eks. for noen timer eller dager siden). For det andre alternativet henter du siste 
versjon for hver gang du skriver inn kommandoet. 

**NB: Her er det viktig å kjøre opp tjenestene i riktig rekkefølge. Gyldige applikasjonsnavn er: oracle, postgres, vtp, 
fpabakus, fpsak, fpsak-frontend, fpformidling, fpoppdrag og fptilbake.**

Det finnes to scripts under "lokal-utvikling" som kan brukes for dette formålet. Disse scriptene kan brukes for å sette opp miljøet for FPSAK, 
FPFORMIDLING, FPTILBAKE eller FPOPPDRAG. Disse to scriptene heter:
*   `lokal-utvikling-fpsak.sh`: Brukes for lokal utvikling av fpsak.
*   `lokal-utvikling-formidling-oppdrag-tilbake.sh`: Brukes for lokal utvikling av enten fpformidling, fpoppdrag eller fptilbake.

Etter at du har brukt en av disse scriptene kan du alltids kjøre ned applikasjonene med å kalle scriptet igjen med argumentet "_down_"
– på lignende måte som du gjør med Docker Compose. Mer informasjon og eksempler på hvordan dette gjøres kan du finne her: [lokal utvikling eksempler](lokal-utvikling-eksempler.md).


### Docker Compose for utvikling av tester
I prosjektet finnes det to docker-compose filer (by default). Den ene blir brukt til å kjøre opp verdikjeden i Github Action,
mens den andre blir brukt til å kjøre opp verdikjeden i Jenkins. Begge disse befinner seg i mappen *"resources/pipeline/"*.
De to filene er:

* *docker-compose.yml*: Brukes av Github Action til å sette opp verdikjeden eller til å sette opp verdikjeden for utvikling av tester.
* *fpsak-docker-compose.yml*: Brukes bare av Jenkins og skal ikke brukes for lokal utvikling.


For å kjøre opp HELE verdikjeden kan du gå til katalogen hvor *docker-compose.yml* filen befinner seg (_resources/pipeline_) 
og kjøre følgende:

1. Sette hvilke versjoner som skal kjøres opp: `./update-versions.sh`
    1. Kommandoet over brukes det siste versjon av alle applikasjonene (dvs. "latest"). Ønsker du en spesisfikk versjon for en spesifikk applikasjon
    kan du kjøre kommandoen med følgende argumenter:
        `./update-versions.sh <APPLIKASJONSNAVN> <VERSION>` 
2. Hente ned oppdaterte Docker-images:`docker-compose pull`
3. Starte alle Docker-containerene: `docker-compose up -d`
