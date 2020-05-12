# Verdikjede [![](https://github.com/navikt/fpsak-autotest/workflows/Kjører%20Autotestene%20for%20verdikjeden/badge.svg)](https://github.com/navikt/fpsak-autotest/actions?query=workflow%3A%22Kjører+Autotestene+for+verdikjeden%22)
Verdikjedetestene for ytelsene engangsstønad, foreldrepenger og svangeskapspenger befinner seg under "fpsak-autotest/test/foreldrepenger". 
Verdikjeden som disse testene kjøres på er listet opp i tabellen nedunder med status om bygg og promotering i GA. 
## Status
| Prosjekt        | Status                                                                 |
|:----------------|:-----------------------------------------------------------------------|
| fp-sak          | [![](https://github.com/navikt/fp-sak/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/fp-sak/actions?query=workflow%3A%22Bygg+og+deploy%22) [![Promote](https://github.com/navikt/fp-sak/workflows/Promote/badge.svg)](https://github.com/navikt/fp-sak/actions?query=workflow%3APromote) |
| fp-formidling   | [![](https://github.com/navikt/fp-formidling/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/fp-formidling/actions?query=workflow%3A%22Bygg+og+deploy%22) [![Promote](https://github.com/navikt/fp-formidling/workflows/Promote/badge.svg)](https://github.com/navikt/fp-formidling/actions?query=workflow%3APromote) |
| fp-abakus       | [![](https://github.com/navikt/fp-abakus/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/fp-abakus/actions?query=workflow%3A%22Bygg+og+deploy%22) |
| vtp             | [![](https://github.com/navikt/vtp/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/vtp/actions?query=workflow%3A%22Bygg+og+deploy%22) |

# Docker-compose og lokal utviklling
Docker-compose filer befinner i mappen *"resources/pipeline/"*. Her befinner det seg for øyeblikket to filer.

* *docker-compose.yml*: Brukes av Github Action til å sette opp verdikjeden eller utvikling av tester.
* *fpsak-docker-compose.yml*: Brukes bare av Jenkins og skal ikke brukes for lokal utvikling.

## Oppsett for foreldrepenger
1. Gå til katalogen der du vil ha kodeprosjektene dine og kjør `git clone git@github.com:navikt/fpsak-autotest.git`
2. Kjør `cd fpsak-autotest`
3. Kjør `resources/keystore/make-dummy-keystore.sh`
4. Kjør `resources/pipeline/update-versions.sh`

## Kjøring av foreldepenger-verdikjede
For å kjøre opp verdikjeden kan du gå til katalogen hvor *docker-compose.yml* filen befinner seg og kjøre:

1. Hente ned oppdaterte Docker-images:`docker-compose pull`
2. Starte alle Docker-containerene: `docker-compose up -d`

# AutoTest
// TODO: Skriv om eller fjern.
###  Mappestruktur
```
>src/main/java
	|-no.nav.foreldrepenger.autotest
			|-aktoerer  (se Aktører)
			|-klienter  (Se Klienter)
			|-util		
>src/test/java
	|-no.nav.foreldrepenger.autotest
			|-internal	        (Tester mot AutoTest)
			|-foreldrepenger        (Tester mot sut)
			|-example	        (Testeksempler)
```
### Tags

Tester kan tagges med annotations @Tag på metoder eller klasser for å vise hvilket område de tilhører

Eksempler:
	* spberegning
	* fpsak
	* test
	* verdikjede

### Aktører

Aktører er abstraksjoner over API laget. Aktører skal ha metoder som samsvarer med handlinger som foskjellige aktører kan ha
Aktører bruker en eller flere klienter

Eksempler:
	+ Saksbehandler
	+ Gosys
	+ FpFordel
	
### Klienter

Klienter er API klienter mot systemer
Klienter kan ha underklineter når en slik indeling er hensiktsmessig (se fpsak)
Hver klient/underklient har en package med dto-er for sending og mottak

Eksempler:

>fpsak
|---->fagsak
|		|-FagsakKlient
|		|->dto
|			|-Sak
|			|-Søk
>openam
|----OpenAMKlient

		
