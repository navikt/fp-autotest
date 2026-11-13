---
applyTo: "src/test/java/**/*.java"
---

# fp-autotest Test Conventions

## Test Structure
- Tests use JUnit with `@Tag` annotations for suite membership
- Test classes extend `VerdikjedeTestBase`
- Tests use "aktører" (actors like `Saksbehandler`) to interact with applications via API clients

## Required Annotations
- `@Tag("<suite>")` — at class level: `fpsak`, `fptilbake`, `fpkalkulus`, `fplos`, `verdikjede`
- `@Tag("<benefit>")` — at class level: `foreldrepenger`, `engangsstonad`, `svangerskapspenger`
- `@DisplayName("...")` — on every test method (Norwegian, descriptive)
- `@Description("...")` — on every test method (detailed scenario description)

## Aksjonspunkt Handling
- Tests interact with aksjonspunkter via `AksjonspunktBekreftelse` subclasses
- Create instances with `new <BekreftelseName>()`, configure, then pass to `saksbehandler.bekreftAksjonspunkt(...)`
- Each subclass overrides `aksjonspunktKode()` returning the aksjonspunkt code
- Standard aksjonspunkter 5015 (ForeslåVedtak) and 5016 (FatterVedtak) are used in most tests

## Uttaksplan with Two Parents
`FellesUttaksplanDto.UttakPeriodeDto(fom, tom, søker, annenPart, annenPartEøs)` is seen from the søker's side, not from mor and far.

Three fields decide who a period belongs to, and they must agree:

| Field | Values | Meaning |
|---|---|---|
| `rolle` on the søknad (`BrukerRolle`) | `MOR`, `FAR`, `MEDMOR` | Who sends the søknad |
| Side in `UttakPeriodeDto` | `søker`, `annenPart` | Whose period it is, relative to the sender |
| `UttakDto.forelder` (`Rolle`) | `MOR`, `FAR_MEDMOR` | Which parent the period belongs to |

- On the `søker` side, `forelder` must match the søknad's `rolle`: `MOR` → `Rolle.MOR`, `FAR` or `MEDMOR` → `Rolle.FAR_MEDMOR`. On the `annenPart` side it is the other parent. fp-soknad maps the roles the same way (`EndringsdatoFilter`, `SendSøknadTilOversiktTask`).
- fp-soknad does not validate that `forelder` matches the side. A mismatch is accepted and gives wrong data, not an error.
- fp-soknad sends only the `søker` side to fpsak (`FpsakUttaksperioder.søkersPerioder`). The `annenPart` side only goes to fp-oversikt. fpsak does not read `forelder`; it uses `kontoType`, `utsettelseÅrsak` and `overføringÅrsak` on the søker side.
- The helpers in `UttaksperioderMaler` (`uttaksperiode`, `utsettelsesperiode`, `overføringsperiode` and others) put the period on the `søker` side, whatever `Rolle` you pass. In mor's søknad, `uttaksperiode(Rolle.FAR_MEDMOR, FEDREKVOTE, ...)` reaches fpsak as mor's own fedrekvote without overføringsårsak, which gives aksjonspunkt 5071.
- Put the other parent's period on the `annenPart` side: `new UttakPeriodeDto(p.fom(), p.tom(), null, p.søker(), null)`, or `UttakPeriodeBuilder.periode(fom, tom).medAnnenPart(...)`.
- Samtidig uttak: fill in both `søker` and `annenPart` in the same period.
- The other parent applies with the same plan via `UttakMaler.byttPerspektiv(uttaksplan)`. It drops `annenPartEøs`, and with it periods that only have EØS uttak.
- `annenPartEøs` holds the other parent's uttak in an EØS country. Set it to `null` unless the test is about EØS. To send it, fill only `annenPartEøs` in the period, as `VerdikjedeForeldrepenger` does when far's endringssøknad sends mor's EØS periods back with the full plan.

Example: `MorOgFarSammen#morOgFar_fødsel_ettArbeidsforholdHver_kobletsak_kantTilKant`.

## Package Organization
- `fpsak/foreldrepenger/` — foreldrepenger benefit tests (tagged fpsak + foreldrepenger)
- `fpsak/engangsstonad/` — engangsstønad benefit tests (tagged fpsak + engangsstonad)
- `fpsak/svangerskapspenger/` — svangerskapspenger benefit tests (tagged fpsak + svangerskapspenger)
- `fptilbake/` — tilbakekreving tests (tagged fptilbake + tilbakekreving and benefit)
- `fpkalkulus/` — beregning/calculation tests (tagged fpkalkulus)
- `fplos/` — oppgavestyring/LOS tests (tagged fplos)
- `verdikjedetester/` — end-to-end value chain tests (tagged verdikjede and benefit)

## Running Tests
```bash
mvn test -P <suite>                              # Full suite
mvn test -P <suite> -Dtest=<ClassName>           # Specific class
mvn test -P <suite> -Dtest="<Class>#<method>"    # Single method
```
