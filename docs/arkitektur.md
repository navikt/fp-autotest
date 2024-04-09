# Strukturell oppbygning av fp-autotest
Denne README-filen tar for seg hvordan FPSAK-AUTOTEST er strukturert og bygget opp. 

###  Mappestruktur
```
>resources/
	|-allure    (inneholder oppsett for allure rapporten som publiseres til GitHub)
	|-keystore  (keystore og truststore for oppsett i pipeline og lokalt)	
	|-pipeline  (docker compose for kjøring av verdikjeden i GA)
>src/main/java
	|-no.nav.foreldrepenger.autotest
			|-aktoerer  (se Aktører)
			|-klienter  (Se Klienter)
			|-util		
>src/test/java
	|-no.nav.foreldrepenger.autotest
			|-foreldrepenger        (Tester mot sut)
			|-internal	        (Tester mot AutoTest)
```
### Tags

Tester kan tagges med annotations @Tag på metoder eller klasser for å vise hvilket område de tilhører

Eksempler:
	* fpsak
	* test
	* verdikjede

### Aktører

Aktører er abstraksjoner over API laget. Aktører skal ha metoder som samsvarer med handlinger som forskjellige aktører kan ha
Aktører bruker en eller flere klienter

Eksempler:
	+ Saksbehandler
	+ Gosys
	+ FpFordel
	
### Klienter

Klienter er API klienter mot systemer
Klienter kan ha underklienter når en slik inndeling er hensiktsmessig (se fpsak)
Hver klient/underklient har en package med dto-er for sending og mottak

Eksempler:

>fpsak
|---->fagsak
|		|-FagsakKlient
|		|->dto
|			|-Sak
|			|-Søk
