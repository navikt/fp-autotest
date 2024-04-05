# Fp-autotest [![Utfører tester mot SUT](https://github.com/navikt/fp-autotest/actions/workflows/trigger.yml/badge.svg?branch=master)](https://github.com/navikt/fp-autotest/actions/workflows/trigger.yml)
Fp-autotest har to testfunksjoner for øyeblikket: Teste spesifikke applikasjoner og teste hele verdikjeden.

Da kan man dele fp-autotest i to, basert på hva den tester, altså: **tester spesifikk for enkelte applikasjoner** og **verdikjedetester**.

For lokalt oppsett eller utvikling se [utvikling lokalt](docs/utvikleroppsett/README.md).

## Verdikjede 
Verdikjeden som disse testene kjøres på er listet opp i tabellen nedunder med status om bygg og promotering i Github Action. 
For denne verdikjeden kjøres det et sett med verdikjedetester for ytelsene engangsstønad, foreldrepenger og svangeskapspenger. 
Alle disse testene befinner seg under "fp-autotest/test/verdikjedetester" og er ellers tagget med taggen "_verdikjede_".

Disse verdikjedetestene blir trigget av alle prosjektene/applikasjonene som er nevnt i listen under ved endring på master.

## Tester spesifikk for enkelte applikasjoner
For øyeblikket har vi to test suits som tester enten fpsak eller fptilbake. Vi har dermed to profiler som kan brukes for å kjøre tester mot disse systemene:

* "_fpsak_": Ved å kjøre testene med denne profilen så kjøres det tester mot FPSAK. Disse testene tester forskjellig ytelser og basefunksjonalitet i fpsak. Ettersom fpsak er helt avhengig av fpabakus, så trigger både fpsak og fpabakus disse testene ved master merge og før en potensiell deploy.
* "_fptilbake_": Ved å kjøre testene med denne profilen så kjøres det tester mot FPTILBAKE. Disse testene trigges bare av fptilbake før en potensiell deploy. 


### Status
| Prosjekt          | Status                                                                                                                                                                                                                                                                                                                                         |
|:------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| vtp               | [![](https://github.com/navikt/vtp/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/vtp/actions?query=workflow%3A%22Bygg+og+deploy%22)                                                                                                                                                                                       |
| fp-abakus         | [![](https://github.com/navikt/fp-abakus/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/fp-abakus/actions?query=workflow%3A%22Bygg+og+deploy%22)                                                                                                                                                                           |
| fp-sak            | [![](https://github.com/navikt/fp-sak/actions/workflows/build.yml/badge.svg)](https://github.com/navikt/fp-sak/actions/workflows/build.yml) [![Promote](https://github.com/navikt/fp-sak/actions/workflows/promote.yml/badge.svg)](https://github.com/navikt/fp-sak/actions/workflows/promote.yml)                                             |
| fp-fordel         | [![](https://github.com/navikt/fpfordel/actions/workflows/build.yml/badge.svg)](https://github.com/navikt/fpfordel/actions/workflows/build.yml)                                                                                                                                                                                                |
| fp-oversikt       | [![](https://github.com/navikt/fp-oversikt/actions/workflows/build.yml/badge.svg)](https://github.com/navikt/fpoversikt/actions/workflows/build.yml)                                                                                                                                                                                           |
| fpsoknad-mottak   | [![](https://github.com/navikt/fpsoknad-mottak/actions/workflows/build.yml/badge.svg)](https://github.com/navikt/fpsoknad-mottak/actions/workflows/build.yml)                                                                                                                                                                                  |
| fpdokgen          | [![](https://github.com/navikt/fp-dokgen/actions/workflows/build.yml/badge.svg)](https://github.com/navikt/fp-dokgen/actions/workflows/build.yml)                                                                                                                                                                                              |
| fp-formidling     | [![](https://github.com/navikt/fp-formidling/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/fp-formidling/actions?query=workflow%3A%22Bygg+og+deploy%22) [![Promote](https://github.com/navikt/fp-formidling/workflows/Promote/badge.svg)](https://github.com/navikt/fp-formidling/actions?query=workflow%3APromote)       |
| fpoppdrag         | [![](https://github.com/navikt/fpoppdrag/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/fpoppdrag/actions?query=workflow%3A%22Bygg+og+deploy%22) [![Promote](https://github.com/navikt/fpoppdrag/workflows/Promote/badge.svg)](https://github.com/navikt/fpoppdrag/actions?query=workflow%3APromote)                       |
| fptilbake         | [![](https://github.com/navikt/fptilbake/workflows/Bygg%20og%20deploy%20Fptilbake/badge.svg)](https://github.com/navikt/fptilbake/actions?query=workflow%3A%22Bygg+og+deploy+Fptilbake%22) [![Promote](https://github.com/navikt/fptilbake/workflows/Promote/badge.svg)](https://github.com/navikt/fptilbake/actions?query=workflow%3APromote) |
| fp-risk           | [![](https://github.com/navikt/fp-risk/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/fp-risk/actions?query=workflow%3A%22Bygg+og+deploy%22) [![Promote](https://github.com/navikt/fp-risk/workflows/Promote/badge.svg)](https://github.com/navikt/fp-risk/actions?query=workflow%3APromote)                               |
| fpabonnent        | [![](https://github.com/navikt/fpabonnent/actions/workflows/build.yml/badge.svg)](https://github.com/navikt/fpabonnent/actions/workflows/build.yml)                                                                                                                                                                                            |

## Interne avhengigheter til fp-autotest
Fpsak-autotest er avhengig av følgende repo:
* [Vtp](https://github.com/navikt/vtp) – Brukes for virtualisere eksterne tjenester
* [Fpsoknad-felles](https://github.com/navikt/fpsoknad-felles) – felles søknadsobjekt/søknadsDTO som trekkes inn indirekte. Denne avhengigheten er brukt i dokumentgenerator


## Arkitektur og oppbygning av fp-autotest
For å se hvordan fp-autotest er bygget gå [her](docs/arkitektur.md).

## Swagger
Felles swagger for alle fss applikasjoner er tilgjengelig på http://localhost:9200

test
test
