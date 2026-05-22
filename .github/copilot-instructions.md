# fp-autotest

Centralized integration / value-chain test repo for Team Foreldrepenger.
Tests the full backend ecosystem (fp-sak + 12 supporting apps) against a
Docker Compose stack with [VTP](https://github.com/navikt/vtp) mocking
external services.

## Context

- [fp-context](https://github.com/navikt/fp-context) — team domain,
  architecture, conventions. Source of truth.
- Copilot Space: navikt / **TeamForeldrepenger**.
- For test suites, catalog, aksjonspunkt mapping, and run commands see
  [`AGENTS.md`](AGENTS.md), `TEST_CATALOG.md`, `AKSJONSPUNKT_MAPPING.md`,
  and the `run-integration-tests` skill in `.github/skills/`.

## Local development against locally built application images

The lokal-utvikling scripts build/start the full stack; one or more apps can
be swapped to a locally built image or run from your IDE.

### Quick start

```bash
cd lokal-utvikling
./lokal-utvikling-fpsak.sh           # full stack with fpsak from registry
./lokal-utvikling-ide.sh fpsak       # stack without fpsak (run it in IDE)
./setup-lokal-utvikling.sh fpsak     # redirects container traffic to host for fpsak
```

### Swap in a locally built image

```bash
cd ~/git/fp-sak && mvn clean install -DskipTests && docker build -t fp-sak .
cd ~/git/fp-autotest/lokal-utvikling && ./setup-lokal-utvikling.sh
# edit docker-compose-lokal/.env: FPSAK_IMAGE=fp-sak:latest
cd docker-compose-lokal && docker compose up --detach
docker compose ps                                          # wait for healthy
cd ~/git/fp-autotest && mvn test -P fpsak -Dtest=Fodsel
```

### .env variable mapping (canonical)

| Repository | Docker build tag | .env variable |
|------------|------------------|---------------|
| fp-sak | fp-sak | FPSAK_IMAGE |
| fp-abakus | fp-abakus | FPABAKUS_IMAGE |
| fp-kalkulus | fp-kalkulus | FPKALKULUS_IMAGE |
| fptilbake | fptilbake | FPTILBAKE_IMAGE |
| fpoppdrag | fpoppdrag | FPOPPDRAG_IMAGE |
| fp-formidling | fp-formidling | FPFORMIDLING_IMAGE |
| fp-dokgen | fp-dokgen | FPDOKGEN_IMAGE |
| fp-risk | fp-risk | FPRISK_IMAGE |
| fp-mottak | fp-mottak | FPMOTTAK_IMAGE |
| fp-oversikt | fp-oversikt | FPOVERSIKT_IMAGE |
| fp-soknad | fp-soknad | FPSOKNAD_IMAGE |
| fplos | fplos | FPLOS_IMAGE |
| fp-inntektsmelding | fp-inntektsmelding | FPINNTEKTSMELDING_IMAGE |
| fp-tilgang | fp-tilgang | FPTILGANG_IMAGE |

### Shut down

```bash
./lokal-utvikling-fpsak.sh down
# or:  cd docker-compose-lokal && docker compose down
```

## Project structure

```
src/main/java/        Test actors (Saksbehandler, Gosys) and API clients
src/test/java/        All test classes (see TEST_CATALOG.md)
lokal-utvikling/      Local dev scripts and docker-compose
pipeline/             CI/CD docker-compose and scripts
pom.xml               Maven config with test profiles
AGENTS.md             Agent entry point + suite listing
```

## Conventions

- JUnit 5 with `@Tag` for suite membership; classes extend `VerdikjedeTestBase`
- `@DisplayName` (Norwegian) + `@Description` for scenarios
- "Aktører" (Saksbehandler, Gosys) drive the system through API clients
- Allure reporting; Java 25, Maven, Docker Compose
