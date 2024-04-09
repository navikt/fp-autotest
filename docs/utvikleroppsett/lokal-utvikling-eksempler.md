# Eksempler - Docker for lokal utvikling

Her er det listet flere eksempler på hvordan en kan bruke `docker compose` for lokal utvikling – da med tanke på at en
ønsker å kjøre opp applikasjonene i en IDE for å kunne debugge. Disse eksemplene dekker de fleste bruksområdene – skulle de ikke det,
ta kontakt med ansvarlig.

Rekkefølgen som en kjører opp applikasjonene er veldig viktig, og må tas hensyn til. Avhengighetene til hver applikasjon 
er listet under.

    <ingen avhengigheter>   <----   postgres, oracle og vtp
    postgres og vtp         <----   fpabakus
    oracle og fpabakus      <----   fpsak
    fpsak                   <----   fpformidlding/fpoppdrag/fptilbake/fpfrontend/fprisk/fpabonnent

Eksemplene nedenfor kjører opp den MINSTE verdikjeden for hver. Dette gjøres fordi det er godt kjent at en del av PCene
har dårlig specs og ikke tåler at hele verdikjeden blir kjørt opp. Den minste verdikjeden betyr at Docker setter bare opp de 
nødvendige applikasjonene som trengs for å kunne kjøre angitt applikasjon. 

*   Minste verdikjede for FPSAK: postgres, oracle, fpabakus og vtp.
*   IKKE minste verdikjede for FPSAK: postgres, oracle, fpabakus, vtp, fpformidling, fpfrontend, ...

Hvis en ønsker å kjøre opp hele verdikjeden med ingen applikasjoner utenfor i IDE kan `resources/pipeline/compose.yml` kjøres.


## Eksempel 1: FPSAK kjørende i IDE
Her ønsker vi å kjøre opp fpsak i IDE, mens resten av dens avhengigheter kjøres opp av docker-compose. Siden lokal utvikling
pågår oftere i fpsak er det laget et eget script for å få opp alt på en kjøring. Følg følgende oppskrift får å få opp avhengighetene:

1) Gå til mappen lokal-utvikling/: `cd lokal-utvikling`.

2) Hvis du ønsker å bruke siste versjon av avhengighetene hopp over dette steget og gå direkte til
steg 3). Ønsker du å bruke en annen versjon enn den siste for noen av avhengighetene kjører du denne kommandoen:
    ```bash
    ./update-versions.sh <APPLIKASJONSNAVN> <VERSION>
    ```

3) Kjør scriptet `lokal-utvikling-fpsak.sh` i som ligger i mappen _"lokal-utvikling/"_.
    1) For Mac skriv følgende i terminalen: `./lokal-utvikling-fpsak.sh`
    2) For Windows skriv følgende i terminalen: `sh lokal-utvikling-fpsak.sh`

4) Kjør deretter opp _FPSAK_ i ønsket IDE.


## Eksempel 2: Applikasjoner som ikke er "løvnoder" kjørende i IDE (e.g. FPABAKUS)
Med "applikasjoner som ikke er løvnoder" menes det applikasjoner som må startes opp før de andre applikasjonen kan starte.
Eksempler på dette er POSTGRES, ORACLE, VTP og FPABAKUS. POSTGRES, ORACLE og VTP har ingen avhengigheter og kan bare kjøre
opp først, mens FPABAKUS har avhengigheter til VTP og POSTGRES. I dette eksempelet skal vi ta for oss FPABAKUS:

1) Gå til mappen lokal-utvikling/: `cd lokal-utvikling`.

2) Kjør følgende kommando for å generere `docker-compose-lokal/`.
    ```bash
    ./setup-lokal-utvikling.sh fpabakus
    ```

3) Kjør `cd docker-compose-lokal` for å komme inn i mappen som nettopp ble laget av scriptet over.

    NB: Nå er det viktig å kjøre applikasjonene opp i riktig rekkefølge, både lokalt og i Docker. 
    * **fpabakus** har avhengighet til postgres og vtp.
    * **fpsak** er avhengig av fpabakus, vtp og oracle.
   
    Her må da avhengighetene til FPABAKUS først kjøres opp før FPABAKUS kan kjøres, og FPABAKUS må kjøres opp før FPSAK
    kan kjøres opp. Rekkefølgen blir da:
    1) VTP + POSTGRES (docker-compose) --> FPABAKUS (IDE) --> ORACLE + FPSAK (docker-compose)

4) Sett versjon som skal kjøres opp med å kjøre scriptet "*update-versions.sh*":
    1) For Mac skriv følgende i terminalen: `./update-versions.sh`
    2) For Windows skriv følgende i terminalen: `sh update-versions.sh`
    
    Ønskes en annen versjon enn siste versjon, gjør som i eksempel 1 og 2 (steg 2).

5) Kjør først opp VTP + POSTGRES gjennom docker-compose:

    ```bash
    docker compose up --quiet-pull --detach postgres vtp
   ```
    
6) Kjør deretter opp FPABAKUS i IDE.

7) Kjøre deretter opp resten av verdikjeden. 

    ```bash
   docker compose up --quiet-pull --detach --scale fpabakus=0
   ```


## Eksempel 3: Mer enn 1 applikasjon utenfor Docker Compose (e.g. FPSAK og FPFORMIDLING)
Her ønsker vi å kjøre både FPSAK og FPFORMIDLING opp lokalt i IDE, mens resten av avhengighetene skal Docker Compose ta seg
av. Her lages det ikke et script: Dette gjøres på den manuelle måten ettersom bruksområdet ikke er så stort.

1) Gå til mappen lokal-utvikling/: `cd lokal-utvikling`.

2) Kjør følgende kommando for å generere `docker-compose-lokal/`.
    ```bash
    ./setup-lokal-utvikling.sh fpsak fpformidling
    ```

3) Kjør `cd docker-compose-lokal` for å komme inn i mappen som nettopp ble laget av scriptet over.

    NB: Nå er det viktig å kjøre applikasjonene opp i riktig rekkefølge, både lokalt og i Docker.  
    * **fpsak** er avhengig av fpabakus (med avhengigheter), vtp og oracle.
    * **fpformidling** er avhengig av fpsak (med avhengigheter), vtp og postgres.
   
   Her må alle avhengighetene til FPSAK kjøres opp først, så FPSAK, og deretter FPFORMIDLING. Eksempel på rekkefølge:
   1) FPABAKUS (med avhengigheter), VTP, ORACLE (i Docker Compose) --> FPSAK (IDE) --> FPFORMIDLING (IDE)
    
4) Sett versjon som skal kjøres opp med å kjøre scriptet "*update-versions.sh*":
    1) For Mac skriv følgende i terminalen: `./update-versions.sh`
    2) For Windows skriv følgende i terminalen: `sh update-versions.sh`
    
    Ønskes en annen versjon enn siste versjon, gjør som i eksempel 1 og 2 (steg 2).
    
5) Kjør opp avhengighetene til FPSAK:
    ```bash
    docker compose up --quiet-pull --detach --scale fpsak=0 fpsak
    ```

6) Kjør deretter opp FPSAK i ønsket IDE.

7) Kjør opp avhengighetene til FPFORMIDLING (ble gjort i steg 5 og 6)

8) Kjør deretter opp FPFORMIDLING i ønsket IDE.

9) Kjør opp resten av verdikjeden
    ```bash
    docker compose up --quiet-pull --detach --scale fpsak=0 --scale fpformidling=0
    ```
