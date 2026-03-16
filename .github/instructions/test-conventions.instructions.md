---
applyTo: "src/test/java/**/*.java"
---

# fp-autotest Test Conventions

## Test Structure
- Tests use JUnit 5 with `@Tag` annotations for suite membership
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

## Package Organization
- `fpsak/foreldrepenger/` — foreldrepenger benefit tests (tagged fpsak + foreldrepenger)
- `fpsak/engangsstonad/` — engangsstønad benefit tests (tagged fpsak + engangsstonad)
- `fpsak/svangerskapspenger/` — svangerskapspenger benefit tests (tagged fpsak + svangerskapspenger)
- `fptilbake/` — tilbakekreving tests (tagged fptilbake + tilbakekreving)
- `fpkalkulus/` — beregning/calculation tests (tagged fpkalkulus)
- `fplos/` — oppgavestyring/LOS tests (tagged fplos)
- `verdikjedetester/` — end-to-end value chain tests (tagged verdikjede)

## Running Tests
```bash
mvn test -P <suite>                              # Full suite
mvn test -P <suite> -Dtest=<ClassName>           # Specific class
mvn test -P <suite> -Dtest="<Class>#<method>"    # Single method
```
