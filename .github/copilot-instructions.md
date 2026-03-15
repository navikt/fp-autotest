# Copilot Instructions for fp-autotest

## What is fp-autotest?

fp-autotest is the centralized integration and value-chain test repository for Team Foreldrepenger (teamforeldrepenger). It tests the entire "foreldrepenger" application ecosystem — a set of 13+ Java microservices that together handle Norwegian parental benefits (foreldrepenger), lump-sum grants (engangsstønad), and pregnancy benefits (svangerskapspenger).

All integration tests run against a full application stack orchestrated via Docker Compose, with [VTP](https://github.com/navikt/vtp) as a mock/virtualizer for external services.

## Test Organization

### Test Suites (Maven profiles)

Tests are organized using JUnit 5 `@Tag` annotations and Maven profiles. Run a suite with: `mvn test -P <profile>`

| Profile | Tags | What it tests | Packages |
|---------|------|---------------|----------|
| `fpsak` | fpsak | Core case processing for all benefit types | `fpsak/foreldrepenger/`, `fpsak/engangsstonad/`, `fpsak/svangerskapspenger/` |
| `fptilbake` | fptilbake, tilbakekreving | Debt recovery/reclaim processing | `fptilbake/foreldrepenger/`, `fptilbake/engangsstonad/`, `fptilbake/svangerskapspenger/` |
| `fpkalkulus` | fpkalkulus | Benefit calculation service | `fpkalkulus/foreldrepenger/`, `fpkalkulus/svangerskapspenger/` |
| `fplos` | fplos | Case queue management (LOS) | `fplos/` |
| `verdikjede` | verdikjede | End-to-end value chain tests | `verdikjedetester/` |

Sub-profiles for specific benefit types: `foreldrepenger`, `engangsstonad`, `svangerskapspenger`

### Which apps trigger which suites (in CI)

| Application | Test Suites |
|-------------|-------------|
| fp-sak | `fpsak`, `verdikjede` |
| fp-abakus | `fpsak`, `verdikjede` |
| fp-kalkulus | `fpkalkulus`, `fpsak`, `verdikjede` |
| fptilbake | `fptilbake`, `verdikjede` |
| fplos | `fplos` |
| fp-formidling, fpoppdrag, fp-mottak, fp-risk, fp-oversikt, fp-dokgen, fp-inntektsmelding, fp-soknad | `verdikjede` |

## How to Run Tests

### Run a full suite
```bash
mvn test -P fpsak
mvn test -P fptilbake
mvn test -P fpkalkulus
mvn test -P verdikjede
```

### Run a specific test class
```bash
mvn test -P fpsak -Dtest=Fodsel
mvn test -P fpsak -Dtest=ArbeidsforholdVarianter
```

### Run a single test method
```bash
mvn test -P fpsak -Dtest="Fodsel#morSøkerFødselMedEttArbeidsforhold"
```

### Run with parallelism
```bash
mvn test -P fpsak -Djuipter.parallelism=4
```

## Local Development Setup

### Quick start for testing against remote images
```bash
cd lokal-utvikling
./lokal-utvikling-fpsak.sh       # Sets up fpsak and dependencies
# or
./lokal-utvikling-ide.sh fpsak   # Sets up everything except fpsak (run fpsak in IDE)
```

### Testing with a locally built application image

To test local changes in an application (e.g., fp-sak):

1. Build the app locally:
   ```bash
   cd ~/git/fp-sak
   mvn clean install -DskipTests
   docker build -t fp-sak .
   ```

2. Generate a fresh `.env` by running the setup script:
   ```bash
   cd ~/git/fp-autotest/lokal-utvikling
   ./setup-lokal-utvikling.sh
   ```

3. Edit `lokal-utvikling/docker-compose-lokal/.env` to use the local image:
   ```
   FPSAK_IMAGE=fp-sak:latest
   ```
   (Replace the default GAR reference with the local tag)

4. Start all services (all are needed regardless of test suite):
   ```bash
   cd docker-compose-lokal
   docker compose up --detach
   ```
   Wait for all to be healthy: `docker compose ps`

5. Run tests (from fp-autotest root):
   ```bash
   cd ~/git/fp-autotest
   mvn test -P fpsak -Dtest=ArbeidsforholdVarianter
   ```

### .env variable mapping for local builds

| Repository | Docker build tag | .env variable |
|------------|-----------------|---------------|
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

### IDE debugging setup

Use `setup-lokal-utvikling.sh` to run specific apps from the IDE while the rest run in Docker:
```bash
cd lokal-utvikling
./setup-lokal-utvikling.sh fpsak          # Run fpsak in IDE, rest in Docker
./setup-lokal-utvikling.sh fpsak fptilbake # Run both in IDE
```

The script redirects Docker container traffic to `host.docker.internal` on the app's port.

### Shut down
```bash
./lokal-utvikling-fpsak.sh down
# or in docker-compose-lokal/:
docker compose down
```

## Project Structure

```
fp-autotest/
├── src/main/java/        # Test actors (Saksbehandler, Gosys) and API clients
├── src/test/java/        # All test classes (see AGENTS.md for complete catalog)
├── lokal-utvikling/      # Local development scripts and docker-compose
├── pipeline/             # CI/CD docker-compose and scripts
├── pom.xml               # Maven config with test profiles
└── AGENTS.md             # Complete searchable test catalog
```

## Conventions

- Tests use JUnit 5 with `@Tag` annotations for suite membership
- Test classes extend `VerdikjedeTestBase`
- `@DisplayName` provides human-readable test names (Norwegian)
- `@Description` provides detailed scenario descriptions
- Tests use "aktører" (actors like Saksbehandler) to interact with the applications via API clients
- Allure is used for test reporting
- Java 25, Maven, Docker Compose
