# Lokal testing med Playwright

Playwright-hjelperne brukes til manuell testing av søknad og saksbehandling mot den lokale verdikjeden i `fp-autotest`. De kjøres bare lokalt, ikke i CI.

## Forutsetninger

- Den lokale verdikjeden i `lokal-utvikling/docker-compose-lokal` kjører.
- Node.js 20.11 eller nyere er installert.

## Første gangs oppsett

Kjør kommandoene fra repo-roten:

```bash
cd lokal-utvikling/playwright
npm install
npx playwright install chromium
```

## Kom i gang

Kjør kommandoene fra `lokal-utvikling/playwright`:

```bash
scripts/familie.sh B1 termin auto mor
OKT=B1 node run.mjs scenarier/ny-soknad.mjs B1 mor
OKT=B1 node run.mjs scenarier/behandle.mjs <sak>
OKT=B1 node run.mjs scenarier/sjekk-sak.mjs B1 mor <sak>
OKT=B1 scripts/fodselshendelse.sh B1 <åååå-mm-dd>
```

`familie.sh` lager en testfamilie med et unikt navn og en økt med samme navn. Bruk saksnummeret fra innsendingen der kommandoene viser `<sak>`. `fodselshendelse.sh` sender en fødselshendelse for familien.

## Økter

All tilstand (familiefiler, innlogginger, logger) ligger i `.state/okter/<økt>/`. Ingenting skrives direkte i `.state/`.

- `scripts/familie.sh` lager økta. Uten `OKT` får økta familienavnet. Skriptet skriver ut `OKT=<økt>` til slutt.
- `run.mjs`, `fodselshendelse.sh` og `stopp-familie.sh` krever `OKT`. Uten den stopper de og lister øktene som finnes.
- `familie.sh` beholder høyst 10 økter (`MAKS_OKTER=<n>` endrer taket). Når en ny økt lages, slettes de som er brukt minst nylig.
- En økt i bruk slettes aldri. Det gjelder økter med en Maven-prosess i bakgrunnen (`mvn.pid`) eller en `run.mjs` som kjører (`kjorer-<pid>.pid`). Er alle i bruk, varsler `familie.sh` og lar dem stå.
- Flere økter kan kjøre parallelt. Maven-oppstarten går én om gangen via låsen i `.state/okter/.mvn-lock`.

## Vite eller Docker

Uten innstilling velger `run.mjs` automatisk modus. Den sjekker om Vite-serverne svarer på port 9114 for søknad og port 9110 for oversikt. Sett `VITE=0` for å bruke Docker-imaget eller `VITE=1` for å bruke Vite. Vite-modus krever at serverne kjører.

## Synlig nettleser (VIS=1)

Standard kjøremodus er headless. Sett `VIS=1` eller `HEADED=1` for å åpne nettleseren synlig. Da legges det på 2 sekunder forsinkelse per Playwright-handling (`SLOWMO=2000`). Du kan overstyre forsinkelsen med `SLOWMO=<ms>`.

Ved suksess holdes vinduet åpent i 10 sekunder før det lukkes automatisk (kan overstyres med `SLUTTPAUSE=<sek>`), med mindre du setter `HOLD=1`. Ved feil holdes vinduet åpent til du lukker det. Sett `HOLD=0` for å lukke vinduet umiddelbart ved både suksess og feil.

```bash
OKT=B1 VIS=1 node run.mjs scenarier/ny-soknad.mjs B1 mor
OKT=B1 VIS=1 HOLD=1 node run.mjs scenarier/sjekk-sak.mjs B1 mor <sak>
```

**Ved én sak (for agenter)**: Når brukeren ber om å teste én enkelt sak og ikke har oppgitt om nettleseren skal vises, spør brukeren før oppstart om kjøringen skal gå med synlig nettleser (`VIS=1`) eller headless.

**Ved feil i frontend (for agenter)**: Hvis en headless-kjøring avdekker valideringsfeil, uventede eller rare tekster, `SIDEFEIL` eller uventet UI-tilstand, spør brukeren om nettleseren skal åpnes med `VIS=1` for det feilende caset. Kjør det feilende scenarioet på nytt med `VIS=1` (eller bruk `VIS=1 HOLD=1` på f.eks. `sjekk-sak.mjs` eller `sjekk-fpsak.mjs`) for å undersøke feilen.

**Parallell kjøring**: Kjør alltid headless ved parallelle kjøringer. Ikke bruk `VIS=1` i parallell. Hvis et case feiler i en parallell kjøring, spør brukeren om det ene feilende caset skal åpnes alene med `VIS=1`.

**Hvis synlig nettleser blokkeres (sandbox / manglende display)**: Hvis `VIS=1` feiler ved oppstart av Chromium på grunn av sandbox eller manglende display, skal agenten si tydelig fra til brukeren, gjengi feilmeldingen og oppgi kommandoen brukeren kan kjøre selv utenfor sandboxen. Ikke fall stille tilbake til headless.

**Status underveis (for agenter)**: Gi brukeren korte tilbakemeldinger når noe tar tid, for eksempel hvis nye scenariofiler må skrives, testfamilier settes opp, eller flere steg kjøres etter hverandre.

## Standardscenarier

Kjør et scenario med `OKT=<økt> node run.mjs scenarier/<scenario>.mjs <argumenter>`.

| Scenario | Bruk |
|---|---|
| `ny-soknad.mjs` | Sender en førstegangssøknad med standardplan. |
| `soknad-med-plan.mjs` | Sender førstegangssøknad der planen endres før innsending (`SLETT`/`NY`). |
| `endring-legg-til.mjs` | Sender endringssøknad som legger til en periode i planen. |
| `endring-legg-til-ferie.mjs` | Legger til en ferieperiode i planen i en endringssøknad. |
| `endring-slett-periode.mjs` | Sender endringssøknad som sletter en periode eller bytter den ut med ferie. |
| `behandle.mjs` | Behandler saken og fatter vedtak. |
| `los-dokumentasjon.mjs` | Løser aksjonspunktet «Kontroller dokumentasjon». |
| `los-uttak.mjs` | Løser uttaksaksjonspunkt og godkjenner uttaket. |
| `sjekk-sak.mjs` | Viser oversikt og status i fpsak. |
| `sjekk-fpsak.mjs` | Viser status og uttak i fpsak. |
| `uttak-api.mjs` | Viser uttaket fra fpsak-API-et per periode. |
| `dump-steg.mjs` | Viser tekst og felt på et søknadssteg. |
| `dump-fpsak.mjs` | Viser tekst og felt i fpsak. |

## Egne scenarier

Ikke lag engangsskript i midlertidige mapper. Gjenbruk `lib/` og legg midlertidige scenarier i `scenarier/lokalt/`. Mappa er git-ignorert. Et scenario kan for eksempel sende en søknad slik:

```js
import * as S from '../../lib/soknad.mjs';
export default async ({ som, familie, args }) => {
  const fam = familie(args[0]);
  const p = await som('B1-mor', 'soknad', fam.mor);
  await S.slettUtkast(p);
  await S.start(p);
  await S.fyllUt(p, { termin: fam.termindato, annenPart: { fnr: fam.far }, send: true });
};
```

Kjør det med `OKT=<økt> node run.mjs scenarier/lokalt/<scenario>.mjs <familie>`.

### Venting og mønstre i scenarier

Unngå hardkodede pauser (`waitForTimeout`, `setTimeout`) og `waitForLoadState('networkidle')` i nye scenarier. Faste pauser gjør kjøringene unødvendig trege og feiler når maskinen er under last.

Bruk i stedet:

- **Kalender som standardvisning**: Hjelperne i `lib/soknad.mjs` (`leggTilPeriode`, `leggTilFerie`, `leggTilUtsettelse`, `leggTilPause`, samt `slettPeriode` når dato oppgis) bruker kalendervisningen som standard (slik de fleste brukere gjør i produksjon). For direkte kalenderoperasjoner finnes også `velgDatoerIKalender()`, `velgEnkeltdagerIKalender()`, `slettDagerIKalender()`, `fjernAlt()` og `kalenderDagFarge()`. Sett `VISNING=liste` (eller `{ visning: 'liste' }`) hvis du spesifikt vil teste listevisningen. `planSomTekst()` bytter tilbake til kalendervisningen etter utlesing.
- **UI-endringer i søknad og oversikt**: stol på Playwrights innebygde auto-waiting på `click`/`fill`/`check`, eller vent på det konkrete elementet som dukker opp eller forsvinner (`locator.waitFor()`, `state: 'hidden'` på dialoger og `.navds-loader, .aksel-loader, [aria-busy="true"]`).
- **Valgfrie felt og knapper**: sjekk `if (await loc.count())` før `.check()`/`.click()` med `.catch()`. Uten `.count()`-sjekk venter Playwright i hele standard-timeouten (10 sekunder) i stillhet når elementet ikke finnes.
- **Oppdatere siden i fpsak**: bruk `F.lastPåNytt(p)` fremfor `p.reload()`.
- **Vente på prosessering i fpsak** (etter innsending av søknad, endring eller aksjonspunkt): bruk `F.ventPåBehandling(p, 'Revurdering')` (eller `'Førstegangsbehandling'`), som poller fpsak-API-et i stedet for faste pauser og full sidelast.
- **Lese uttak fra fpsak**: bruk `F.uttakFraApi(p)` fremfor `F.uttak(p)` når du ikke trenger å teste selve uttakspanelet i UI.
- **Saker med flere behandlinger**: kall `F.velgBehandling(p, 'Revurdering')` (eller `'Førstegangsbehandling'`) før du klikker faner i fpsak, ellers blir siden stående på «Velg behandling» og timer ut etter 10 sekunder.

## Flere familier i én økt

Standard er å kjøre én familie av gangen. Skal du kjøre flere uavhengige familier, kan du lage dem i én Maven-kjøring med `ANTALL=<n>` og kjøre dem parallelt (høyst 5 samtidig):

```bash
ANTALL=2 scripts/familie.sh A termin auto mor
OKT=A node run.mjs scenarier/ny-soknad.mjs A-01 mor > .state/okter/A/A-01.out 2>&1 &
OKT=A node run.mjs scenarier/ny-soknad.mjs A-02 mor > .state/okter/A/A-02.out 2>&1 &
wait
```

Familiene fra ett `familie.sh`-kall deler økt (her `A`). Bruk `ANTALL` fremfor mange separate kall, ellers fyller du opp taket på 10 økter raskt. Steg for samme forelder eller samme sak må alltid kjøres etter hverandre. På macOS kjører skallet ofte `zsh`: unngå variabelnavnet `status` (read-only i `zsh`), og bruk `bash -c '...'` ved parallelle løkker med `&`/`wait` (unngår `BG_NICE` og 1-indekserte arrays).

Kjør `OKT=<økt> scripts/stopp-familie.sh <navn>` når en kjøring avsluttes eller avbrytes, slik at bakgrunnsprosessen `ManuellSoknad` stoppes. `scripts/familie.sh` med `auto` eller `vent` returnerer så snart familiene er opprettet. Ikke vent på `BUILD SUCCESS` i loggen, den kommer først etter at alle søknadene er sendt.

## Viktige fallgruver

- En handling får opptil 10 sekunder, en navigering opptil 20 sekunder. Et scenario stopper etter 150 sekunder som standard.
- Bruk et unikt familienavn for hver test. Gamle tilstandsfiler kan ellers koble kjøringen til feil familie.
- Ikke slett `.state/` eller økter for hånd. `scripts/familie.sh` rydder selv, se [Økter](#økter).
- Ikke styr samme forelder i samme app eller samme sak i fpsak fra flere kjøringer samtidig. Kjør stegene etter hverandre.
- Manuelle aksjonspunkt sender saken til godkjenning. En beslutter må godkjenne vedtaket.
- Termindatoen kan ikke være mer enn 21 dager tilbake i søknaden.
- Ved sletting eller endring av perioder på termin- eller fødselsdato i planen, ta med bindestrek i radstarten (for eksempel `'24. des. 26 -'`), ellers treffer velgeren hendelsesraden («Termin»/«Fødsel») i stedet for perioden.
- `scripts/familie.sh` kjører offline `surefire:test` når `ManuellSoknad.class` er nyere enn `ManuellSoknad.java` og `pom.xml`. Hvis du har endret andre Java-klasser i `fp-autotest`, kjør med `MVN_BUILD=1 scripts/familie.sh ...` for full kompilering.
- Fødselsnumrene fra `scripts/familie.sh` er syntetiske testdata og kan skrives ut ved feilsøking.
- Oppgi om Vite-serveren eller Docker-imaget ble brukt når du rapporterer testresultater.
