# Fpsak-autotest
Fpsak-autotest har to testfunksjoner for øyeblikket: Teste spesifikke applikasjoner og teste hele verdikjeden. Navnet fpsak-autotest
er fra gammelt av, hvor fpsak-autotest ble bare brukt til å teste fpsak. Nå i senere tid blir repoet også brukt til å teste resten av verdikjeden.
Da kan man dele fpsak-autotest i to, basert på hva den tester, altså: **fpsak spesifikke tester** og **verdikjedetester**.

For lokalt oppsett eller utvikling se [utvikling lokalt](docs).

## Verdikjede [![](https://github.com/navikt/fpsak-autotest/actions/workflows/trigger.yml/badge.svg)](https://github.com/navikt/fpsak-autotest/actions/workflows/trigger.yml)
Verdikjeden som disse testene kjøres på er listet opp i tabellen nedunder med status om bygg og promotering i Github Action. 
For denne verdikjeden kjøres det et sett med verdikjedetester for ytelsene engangsstønad, foreldrepenger og svangeskapspenger. 
Alle disse testene befinner seg under "fpsak-autotest/test/verdikjedetester" og er ellers tagget med taggen "_verdikjede_".

Disse verdikjedetestene blir trigget av alle prosjektene/applikasjonene som er nevnt i listen under ved endring på master (med unntak av fp-sak og fp-abakus).

### Status
| Prosjekt        | Status                                                                 |
|:----------------|:-----------------------------------------------------------------------|
| vtp             | [![](https://github.com/navikt/vtp/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/vtp/actions?query=workflow%3A%22Bygg+og+deploy%22) |
| fp-abakus       | [![](https://github.com/navikt/fp-abakus/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/fp-abakus/actions?query=workflow%3A%22Bygg+og+deploy%22) |
| fp-sak          | [![](https://github.com/navikt/fp-sak/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/fp-sak/actions?query=workflow%3A%22Bygg+og+deploy%22) [![Promote](https://github.com/navikt/fp-sak/workflows/Promote/badge.svg)](https://github.com/navikt/fp-sak/actions?query=workflow%3APromote) |
| fp-fordel       | [![](https://github.com/navikt/fpfordel/actions/workflows/build.yml/badge.svg)](https://github.com/navikt/fpfordel/actions/workflows/build.yml) |
| fp-info         | [![](https://github.com/navikt/fpinfo/actions/workflows/build.yml/badge.svg)](https://github.com/navikt/fpinfo/actions/workflows/build.yml) |
| fpsoknad-mottak | [![](https://github.com/navikt/fpsoknad-mottak/actions/workflows/build.yml/badge.svg)](https://github.com/navikt/fpsoknad-mottak/actions/workflows/build.yml) |
| fpdokgen        | [![](https://github.com/navikt/fp-dokgen/actions/workflows/build.yml/badge.svg)](https://github.com/navikt/fp-dokgen/actions/workflows/build.yml) |
| fp-formidling   | [![](https://github.com/navikt/fp-formidling/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/fp-formidling/actions?query=workflow%3A%22Bygg+og+deploy%22) [![Promote](https://github.com/navikt/fp-formidling/workflows/Promote/badge.svg)](https://github.com/navikt/fp-formidling/actions?query=workflow%3APromote) |
| fpoppdrag       | [![](https://github.com/navikt/fpoppdrag/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/fpoppdrag/actions?query=workflow%3A%22Bygg+og+deploy%22) [![Promote](https://github.com/navikt/fpoppdrag/workflows/Promote/badge.svg)](https://github.com/navikt/fpoppdrag/actions?query=workflow%3APromote) |
| fptilbake       | [![](https://github.com/navikt/fptilbake/workflows/Bygg%20og%20deploy%20Fptilbake/badge.svg)](https://github.com/navikt/fptilbake/actions?query=workflow%3A%22Bygg+og+deploy+Fptilbake%22) [![Promote](https://github.com/navikt/fptilbake/workflows/Promote/badge.svg)](https://github.com/navikt/fptilbake/actions?query=workflow%3APromote) |
| fp-risk         | [![](https://github.com/navikt/fp-risk/workflows/Bygg%20og%20deploy/badge.svg)](https://github.com/navikt/fp-risk/actions?query=workflow%3A%22Bygg+og+deploy%22) [![Promote](https://github.com/navikt/fp-risk/workflows/Promote/badge.svg)](https://github.com/navikt/fp-risk/actions?query=workflow%3APromote) |
| fpabonnent         | [![](https://github.com/navikt/fpabonnent/actions/workflows/build.yml/badge.svg)](https://github.com/navikt/fpabonnent/actions/workflows/build.yml) |



## Fpsak spesifikke tester [![](https://github.com/navikt/fpsak-autotest/workflows/Kjører%20autotestene%20for%20fpsak/badge.svg)](https://github.com/navikt/fpsak-autotest/actions?query=workflow%3A%22Kjører+autotestene+for+fpsak%22)
Fpsak spesifikke tester er tagget med taggen "_fpsak_" og tester fpsak med fokus på forskjellige ytelser og komponenter i fpsak.
Disse testene blir kjørt Github Action ved merge til master i fp-sak- og fp-abakus-prosjektet.

## Arkitektur og oppbygning av fpsak-autotest
For å se hvordan fpsak-autotest er bygget gå [her](docs/arkitektur.md).
