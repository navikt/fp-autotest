---
name: testsak
description: Lag en testsak eller testfamilie i det lokale fp-miljøet (docker compose med fpsak og VTP), som stopper der utvikleren vil teste manuelt. Bruk når noen ber om en sak som stopper på et aksjonspunkt, vil søke selv i søknadsappen og trenger familie og inntektsmelding, eller spør hvor en lokal sak står.
---

# Lag en testsak

Stegene kjøres med `verktoy/testsak.sh` fra rotmappa i fp-autotest. Skriptet kompilerer prosjektet selv når kildekoden er endret, så første kall kan ta et par minutter.

```sh
verktoy/testsak.sh hjelp
verktoy/testsak.sh lag_familie '{"mor_arbeid":"under_6g","far_arbeid":"under_6g","fodt_dager_siden":56}'
```

`hjelp` lister stegene og parametrene deres. Exit-kode 1 og en linje som starter med `FEIL:` betyr at steget feilet.

Kjør ett steg per kommando, og les utskriften før neste steg. `familie_id` står i utskriften fra `lag_familie`, og saksnummer i utskriften fra `send_soknad`. Ikke gjett dem, og ikke kjed `lag_familie` med andre steg (`&&`). En gjettet `familie_id` gir `Ukjent familie_id`. `<familie_id>` og `<saksnummer>` i eksemplene skal byttes ut. Er familien borte fra VTP (miljøet er startet på nytt), sier steget fra og sletter den fra tilstanden: lag en ny familie.

## Slik gjør du det

Avgjør først hva utvikleren ber om:

- **Hvor står en sak** («hvor står sak 301?»): kjør `status` med saksnummeret og vis svaret. Ferdig.
- **Søke selv** i søknadsappen: start miljøet (steg 1), og gå så til steg 5.
- **Sak som stopper på et aksjonspunkt**, eller et tema eller skjermbilde i fpsak: følg steg 1–4 og 6.

1. Kjør `miljo_status`. Er alt oppe, gå videre. Ellers start miljøet slik skillen `run-integration-tests` beskriver, og si fra at du gjør det:
   - Finn mappa miljøet ble startet fra sist: `docker ps -a --format '{{.Label "com.docker.compose.project.working_dir"}}' | sort -u`. Den kan ligge i en annen checkout enn denne. Bruk den, ellers blir det to compose-prosjekter som slåss om portene.
   - Finnes ingen slik mappe, og heller ikke `lokal-utvikling/docker-compose-lokal/` her, kjør `./setup-lokal-utvikling.sh` i `lokal-utvikling/`. Ikke kjør skriptet når mappa finnes: det sletter mappa og overskriver `.env`, der utvikleren kan ha pekt på lokalt bygde images.
   - Finn apper som kjører i IDE-en før du starter noe. Setup-skriptet peker de andre tjenestene til `host.docker.internal` for slike apper: `grep -ho 'host.docker.internal:[0-9]*/[a-z-]*' *.yml *.env | sort -u` i mappa. Appnavnet står etter porten. Har utvikleren sagt i samtalen at en app kjører i IDE-en, gjelder den også.
   - Start med `docker compose up --detach --scale <app>=0` (én `--scale` per IDE-app). Uten `--scale` starter compose også appen i docker, og den slåss med IDE-appen om porten.
   - Kan du ikke lese mappa (for eksempel «Operation not permitted» i sandkassa), gi utvikleren kommandoene til egen terminal og vent til utvikleren sier fra.
   - Vent med `miljo_status '{"vent_sekunder":120}'`. Steget returnerer når alt er oppe, eller etter 120 sekunder. Er noe fortsatt nede, kjør det inntil to ganger til. Er det fortsatt nede da, vis utskriften og stopp.
   - Feiler oppstarten (docker kjører ikke, innlogging mot image-registeret mangler), vis feilen og stopp.
2. Finn aksjonspunktet. Har utvikleren oppgitt en kode, bruk den. Ellers: bruk «Finn aksjonspunkt etter tema» i `katalog/README.md` til å oversette skjermbilde eller tema til kode. Treffer flere rader, avgjør kolonnen «Velg koden når» om mulig. Passer fortsatt flere, eller ingen, spør utvikleren og foreslå kodene fra tabellen. Dette er det eneste spørsmålet du stiller før du har lest katalogfilen.
3. Les `katalog/aksjonspunkt/<kode>.md`. Gå gjennom tabellen «Valg å avklare»:
   - Spør alltid om radene «Ytelse» og «Variant» når utvikleren ikke har nevnt dem og tabellen har flere alternativer. De avgjør hvilken sak utvikleren får, og standarden er bare den testene dekker best.
   - For de andre valgene: spør bare om valg utvikleren ikke allerede har svart på, og bare der standarden kan gi feil sak for det utvikleren beskrev. Antall dager, beløp og lignende tar du fra standarden.
   - Samle alle spørsmålene i én melding, med alternativene og standarden for hvert valg.
   - Sier utvikleren «bare lag den»: bruk standardene uten å spørre, og si hvilke valg du tok.
   - Står det «krever test» i CLI-kolonnen for valget utvikleren trenger, si at verktøyet ikke dekker det. Tilby i stedet manuell søknad (steg 5) med det som må fylles ut i søknadsappen, eller pek på testen i `basert_på` i frontmatter.
4. Bygg saken etter «Minste oppskrift» og CLI-kolonnen i valgtabellen:
   - `lag_familie`, med `ytelse` for ES og SVP. SVP krever termin og arbeid hos mor.
   - `send_soknad`. Krever oppskriften en bestemt uttaksplan, send den som `perioder` (samme plan til begge foreldrene).
   - `send_inntektsmelding` når søkeren har et ordinært arbeidsforhold (`under_6g`, `over_6g`, `to_arbeidsforhold`, `arbeid_og_frilans`, `arbeid_og_sn`, `stillingsprosent_0`). Sier oppskriften «Ingen inntektsmelding», hopp over steget. Sender du det likevel, svarer det «Ingen inntektsmelding» uten å feile.
   - Avslutt med `vent_til_aksjonspunkt` for koden.
   - Skal begge foreldrene søke (for eksempel far med aktivitetskrav): send mors søknad og inntektsmelding først. Kjør så `vent_pa_behandling` for mors sak. `OK` betyr at mors behandling er avsluttet: send fars søknad. `STOPP` betyr at mors sak står på et aksjonspunkt: vis utskriften og stopp. `TIMEOUT`: kjør steget på nytt, inntil to ganger til, og vis så utskriften og stopp.
   - Står saken på vent med 7008 (søknaden kom for tidlig), ligger hendelsen for langt fram i tid. Flytt datoen bakover og lag saken på nytt.
   - Kom aksjonspunktet ikke: vis `status`. Står saken på et annet aksjonspunkt som kommer tidligere i flyten (se tabellen «Flyt for førstegangsbehandling»), si det. Verktøyet kan ikke løse aksjonspunkter, så utvikleren må løse det i fpsak-frontend først.
   - Står det ikke «kontrollert» ved CLI-oppskriften, si at den ikke er kjørt før, og si fra om den ikke virket.
5. Vil utvikleren fylle ut søknaden selv, send ingen søknad. Lag familien med `lag_familie`, med `ytelse` for ES og SVP, så lenken til søknadsappen og `folg_opp` stemmer. Skriv ut kommandoen lag_familie gir under «Vis fnr» nøyaktig slik, alene i en kodeblokk, så utvikleren kan lime den inn i Copilot CLI:

   ```
   !/sti/til/fp-autotest/verktoy/testsak.sh familie '{"familie_id":"<familie_id>"}'
   ```

   Fnr i verktøyutskriften din er maskert, så ikke gjengi dem. Start så `folg_opp` i bakgrunnen (asynkront, så du ikke blokkerer samtalen). Skal bare én av foreldrene søke, send `forelder` (`mor` eller `far`). Ellers venter steget på søknad fra begge når familien har far og ytelsen er FP. Steget henter ytelsen fra familien, venter på søknaden fra hver forelder og sender inntektsmelding straks forespørselen kommer. Si fra til utvikleren at inntektsmeldingen sendes automatisk, og vis saksnumrene når steget er ferdig. Kan du ikke kjøre i bakgrunnen, gi utvikleren `folg_opp`-kommandoen lag_familie skriver ut, til egen terminal, med `forelder` lagt til ved behov. Trengs avvikende månedsinntekt eller en annen startdato enn standard, bruk heller `vent_pa_soknad` og `send_inntektsmelding`, én forelder om gangen.
6. Når saken finnes, vis saksnummer, åpne aksjonspunkter og lenken til fpsak-frontend.

Ikke gjett hva som utløser et aksjonspunkt. Står det ikke i katalogen, si det.
