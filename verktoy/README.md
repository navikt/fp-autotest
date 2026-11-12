# testsak-verktøyet (prototype)

Kommandolinjeverktøy som lager og følger testsaker i det lokale docker compose-miljøet, med aktørene i fp-autotest. Hvert kall kjører ett steg og avslutter. Skillen `.github/skills/testsak` forteller KI-agenter hvordan stegene brukes.

```sh
verktoy/testsak.sh hjelp
verktoy/testsak.sh lag_familie '{"mor_arbeid":"under_6g","far_arbeid":"under_6g","fodt_dager_siden":56}'
echo '{"saksnummer":"402"}' | verktoy/testsak.sh status
```

Koden ligger i rotprosjektet, i `src/main/java/no/nav/foreldrepenger/autotest/verktoy`. `testsak.sh` kompilerer prosjektet og skriver classpath til `target/verktoy-classpath.txt` når kildekoden er nyere enn den fila. Parametrene er et JSON-objekt, som argument eller på stdin. Exit-koden er 1 når steget feiler.

## Steg

| Steg | Hva |
|---|---|
| `miljo_status` | Sjekker at fpsak, VTP, fpabakus og fpkalkulus svarer. Med `vent_sekunder` venter steget til alt er oppe. |
| `status` | Viser behandlingsstatus, ventestatus, åpne aksjonspunkter og lenke til fpsak-frontend for et saksnummer. |
| `vent_til_aksjonspunkt` | Venter til et aksjonspunkt er åpent, eller til tiden går ut (standard 60 s). |
| `vent_pa_behandling` | Venter til behandlingen er avsluttet (`OK`) eller står på et manuelt aksjonspunkt (`STOPP`), ellers `TIMEOUT` (standard 60 s). Brukes før far søker når mors sak må være ferdig. |
| `lag_familie` | Registrerer mor, eventuelt far, sivilstand, arbeid og barn i VTP for fødsel, termin eller adopsjon. Arbeid kan være arbeidsforhold under eller over 6G, to arbeidsforhold, frilans, selvstendig næringsdrivende arbeid kombinert med frilans eller SN, arbeidsforhold med stillingsprosent 0 eller bare sykepenger. Returnerer `familie_id`, fnr og lenke til søknadsappen for valgt `ytelse` (FP, ES eller SVP). Ytelsen blir standard for de andre stegene. |
| `send_soknad` | Sender FP-, ES- eller SVP-søknad for mor eller far. Malen følger familiens hendelse. Frilans og næring kommer med når søkeren har det, og `varig_endring` oppgir varig endret næring. For FP kan søknaden ha en egen felles uttaksplan (`perioder`) med kvoter, mors aktivitet, utsettelse, overføring, gradering og samtidig uttak. SVP sender én tilrettelegging (ingen, delvis eller hel) per arbeidsgiver og for næringen. |
| `vent_pa_soknad` | Venter til en forelder har sendt søknad manuelt i søknadsappen for familiens ytelse, og returnerer saksnummeret. |
| `send_inntektsmelding` | Venter på forespørselen og sender inntektsmelding fra alle arbeidsgivere, eventuelt med avvikende månedsinntekt (samme beløp fra hver arbeidsgiver) eller egen startdato. Ved SVP sendes SVP-inntektsmeldinger. Søker uten ordinært arbeidsforhold får ingen inntektsmelding, uten at steget feiler. |
| `folg_opp` | Venter på manuelle søknader fra mor og/eller far (standard inntil 30 min) og sender inntektsmelding med a-inntekt straks forespørselen kommer. Kjør den i egen terminal eller i bakgrunnen. |
| `familie` | Viser fnr for foreldrene, hendelsesdato og lenke til søknadsappen. |

Parametrene står i `verktoy/testsak.sh hjelp`.

Byggeklossene dekker blant annet oppskriftene for 5027 (FP og ES), 5038, 5060, 5074 og 5091 (SVP). Det finnes ingen ferdige scenarioer: agenten setter sammen stegene etter oppskriftene i katalogen. Familier og saker lagres i `verktoy/.tilstand.json` (ignorert av git; overstyr med `FP_AUTOTEST_TILSTAND`), slik at `familie_id` og saksnummer kan brukes i senere kall, både fra agenten i sandkassa og fra din egen terminal. `familie_id` er tilfeldig (for eksempel `F-7k3q`), så en gjettet id feiler i stedet for å treffe en annen familie. Familier som ikke lenger finnes i VTP (etter omstart av miljøet), slettes sammen med sakene sine første gang de brukes.

Katalogen finnes relativt til `target/classes` (`katalog/` i rotmappa). Overstyr med miljøvariabelen `FP_AUTOTEST_KATALOG`.

## Bygg manuelt

```sh
mvn compile dependency:build-classpath -Dmdep.outputFile=target/verktoy-classpath.txt -Dmdep.includeScope=runtime
```

`verktoy/logback.xml` gir loggen til stderr, slik at stdout bare har svaret fra steget. I cplt-sandkassa er `~/.m2/settings.xml` ikke lesbar; `testsak.sh` bygger da offline med en tom settings-fil.

## Prøv i en agent

- «Lag en sak der jeg kan teste 5038»
- «Far søker foreldrepenger og oppgir aleneomsorg, men er gift med mor. Stopp der saksbehandler må vurdere aleneomsorgen.»
- «Jeg vil søke foreldrepenger for et født barn for både mor og far. Søknadene fyller jeg ut selv.»
- «Lag en SVP-sak for mor med to arbeidsforhold og delvis tilrettelegging.»
- «Lag en adopsjonssak der mor er frilanser.»
- «Hvor står sak 301?»
