# Verdikjede
Verdikjedetestene for ytelsene engangsstønad, foreldrepenger og svangeskapspenger befinner seg under "fpsak-autotest/test/foreldrepenger". 
Verdikjeden som disse testene kjøres på er listet opp i tabellen nedunder med status om bygg og promotering i GA. 
## Status

| Prosjekt        | Status                                                                 |
|:----------------|:-----------------------------------------------------------------------|
| fpsak-autotes   | [![](https://github.com/navikt/fpsak-autotest/workflows/Kjører%20Autotestene%20for%20verdikjeden/badge.svg)](https://github.com/navikt/fpsak-autotest/actions?query=workflow%3A%22Kjører+Autotestene+for+verdikjeden%22) |
| fp-sak          | [![](https://github.com/navikt/fp-sak/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/fp-sak/actions?query=workflow%3A%22Bygg+og+deploy%22) [![Promote](https://github.com/navikt/fp-sak/workflows/Promote/badge.svg)](https://github.com/navikt/fp-sak/actions?query=workflow%3APromote) |
| fp-formidling   | [![](https://github.com/navikt/fp-formidling/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/fp-formidling/actions?query=workflow%3A%22Bygg+og+deploy%22) [![Promote](https://github.com/navikt/fp-formidling/workflows/Promote/badge.svg)](https://github.com/navikt/fp-formidling/actions?query=workflow%3APromote) |
| fp-abakus       | [![](https://github.com/navikt/fp-abakus/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/fp-abakus/actions?query=workflow%3A%22Bygg+og+deploy%22) |
| vtp             | [![](https://github.com/navikt/vtp/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/vtp/actions?query=workflow%3A%22Bygg+og+deploy%22) |


# AutoTest

## 1 Installasjon

Instalasjonsinformasjon for AutoTest

1. cd <root dir>
2. mvn clean install

### 1.1 Konfigurering

#### 1.1.1 Miljøvariabler

liste over miljøvariabler som brukes under kjøring

AUTOTEST_EVN=<localhost,U89,T10> (Brukes i 1.1.3 Miljø Konfigurasjon)

#### 1.1.2 Generell Konfigurasjon

Generell konfigurasjon ligger under src/main/resources/

1. Kopier config.properties.example til config.properties
2. Fyll inn config.properties med brukere fra ida.adeo.no

#### 1.1.3 Miljø Konfigurasjon

Hvilket miljø som blir testet blir spesifiesrt under 1.1.1 Miljøvariabler
Miljøkonfigurasjon ligger under src/main/resources/<Miljø>

## 2 Kjøre Tester

mvn test -P fpsak -ff -Dit.test=NameOfTheFailedIntegrationTest verify

### 2.1 Lokalt

Set env AUTOTEST_EVN=localhost (se 1.1.1 Miljøvariabler)

### 2.2 Via Jenkins

//TODO Link til job

### 2.3 Mot Miljø

Set env AUTOTEST_EVN=<Miljø> (se 1.1.1 Miljøvariabler)

## 3 Utvikle Tester

Tester 

### 3.1 Mappestruktur

>src/main/java
|			|-no.nav.foreldrepenger.autotest
|										|-aktoerer  (se 3.3 Aktører)
|										|-klienter  (Se 3.4 Klienter)
|										|-util		
>src/test/java
|			|-no.nav.foreldrepenger.autotest
|										|-internal	(tester mot AutoTest)
|										|-sut		(Tester mot sut)
|										|-example	(Testeksempler)

### 3.2 Tags

Tester kan tagges med annotations @Tag på metoder eller klasser for å vise hvilket område de tilhører

Eksempler:
	+ spberegning
	+ fpsak
	+ test

### 3.3 Aktører

Aktører er abstraksjoner over API laget. Aktører skal ha metoder som samsvarer med handlinger som foskjellige aktører kan ha
Aktører bruker en eller flere klienter

Eksempler:
	+ Saksbehandler
	+ Gosys
	+ FpFordel
	
### 3.4 Klienter

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

		
