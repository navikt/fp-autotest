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
node run.mjs scenarier/ny-soknad.mjs B1 mor
node run.mjs scenarier/behandle.mjs <sak>
node run.mjs scenarier/sjekk-sak.mjs B1 mor <sak>
scripts/fodselshendelse.sh B1 <åååå-mm-dd>
```

`familie.sh` lager en testfamilie med et unikt navn. Bruk saksnummeret fra innsendingen der kommandoene viser `<sak>`. `fodselshendelse.sh` sender en fødselshendelse for familien.

## Vite eller Docker

Uten innstilling velger `run.mjs` automatisk modus. Den sjekker om Vite-serverne svarer på port 9114 for søknad og port 9110 for oversikt. Sett `VITE=0` for å bruke Docker-imaget eller `VITE=1` for å bruke Vite. Vite-modus krever at serverne kjører.

## Standardscenarier

Kjør et scenario med `node run.mjs scenarier/<scenario>.mjs <argumenter>`.

| Scenario | Bruk |
|---|---|
| `ny-soknad.mjs` | Sender en førstegangssøknad med standardplan. |
| `soknad-med-plan.mjs` | Sender førstegangssøknad der planen endres før innsending (`SLETT`/`NY`). |
| `endring-legg-til.mjs` | Sender endringssøknad som legger til en periode i planen. |
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

Kjør det med `node run.mjs scenarier/lokalt/<scenario>.mjs <familie>`.

## Flere familier i én økt

Standard er å kjøre én familie av gangen. Skal du kjøre flere uavhengige familier, kan du lage dem i én Maven-kjøring med `ANTALL=<n>` og kjøre dem parallelt (høyst 5 samtidig):

```bash
ANTALL=2 scripts/familie.sh A termin auto mor
node run.mjs scenarier/ny-soknad.mjs A-01 mor > .state/A-01.out 2>&1 &
node run.mjs scenarier/ny-soknad.mjs A-02 mor > .state/A-02.out 2>&1 &
wait
```

Bruk et eget prefiks per økt (for eksempel `A` i én og `B` i en annen). Steg for samme forelder eller samme sak må alltid kjøres etter hverandre. På macOS kjører skallet ofte `zsh`: unngå variabelnavnet `status` (read-only i `zsh`), og bruk `bash -c '...'` ved parallelle løkker med `&`/`wait` (unngår `BG_NICE` og 1-indekserte arrays).

## Viktige fallgruver

- En handling får opptil 10 sekunder, en navigering opptil 20 sekunder. Et scenario stopper etter 150 sekunder som standard.
- Bruk et unikt familienavn for hver test. Gamle tilstandsfiler kan ellers koble kjøringen til feil familie.
- `scripts/familie.sh` varsler (uten å slette) når `.state/` har over 200 filer eller filer eldre enn 7 dager. Spør alltid brukeren før `.state/` slettes (`rm -rf .state`).
- Ikke styr samme forelder i samme app eller samme sak i fpsak fra flere kjøringer samtidig. Kjør stegene etter hverandre.
- Manuelle aksjonspunkt sender saken til godkjenning. En beslutter må godkjenne vedtaket.
- Termindatoen kan ikke være mer enn 21 dager tilbake i søknaden.
- Fødselsnumrene fra `scripts/familie.sh` er syntetiske testdata og kan skrives ut ved feilsøking.
- Skjermbilder lagres i `.state/skjermbilder/` og bør bare tas ved feil eller for å avklare noe.
- Oppgi om Vite-serveren eller Docker-imaget ble brukt når du rapporterer testresultater.
