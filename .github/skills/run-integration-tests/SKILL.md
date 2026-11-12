---
name: run-integration-tests
description: Build, deploy, and run fp-autotest integration tests against a local Docker Compose environment. Use this skill when a developer wants to test local application changes, run specific test suites, or needs a sak that stops at a given aksjonspunkt for manual testing.
---

# Run Integration Tests

## Running Tests

### Run a full suite
```bash
cd ~/git/fp-autotest
mvn test -P <suite>
```
Available suites: `fpsak`, `fptilbake`, `fpkalkulus`, `fplos`, `verdikjede`
Sub-suites: `foreldrepenger`, `engangsstonad`, `svangerskapspenger`

### Run a specific test class
```bash
mvn test -P <suite> -Dtest=<ClassName>
```

### Run a single test method
```bash
mvn test -P <suite> -Dtest="<ClassName>#<methodName>"
```

### Run with parallelism
```bash
mvn test -P <suite> -Djuipter.parallelism=4
```

## Create a Sak That Stops at an Aksjonspunkt

When a developer asks for a sak at a given aksjonspunkt (e.g. "jeg trenger en sak med 5038"):

1. **Find a test.** Look up the code in `AKSJONSPUNKT_MAPPING.md`. Prefer a short, simple test, such as one in `Fodsel` or `Aksjonspunkter`, over a long verdikjede test.
2. **If the code is not in the mapping**, say so. Do not guess a test, and do not suggest other codes: neighbouring codes are unrelated. Instead, find out in fp-sak (`navikt/fp-sak`, e.g. `../fp-sak` if checked out) how the aksjonspunkt is created: look up the code in `AksjonspunktDefinisjon` and find where it is utledet. Explain briefly which scenario triggers it, and ask whether the developer wants a new test in fp-autotest for that scenario. If yes, add it to `src/test/java/no/nav/foreldrepenger/autotest/fpsak/foreldrepenger/Aksjonspunkter.java`, then add the test to `AKSJONSPUNKT_MAPPING.md`.
3. **Pick the profile from the test class's `@Tag`s, not from its package.** A profile only runs classes whose tags match its `includeTestsTags` in `pom.xml`. Example: `Aksjonspunkter` is tagged `util` and `foreldrepenger`, so `-P fpsak` runs 0 tests; use `-P foreldrepenger`. If the run reports `Tests run: 0`, check the tags.
4. **Check that the services are running** (see Prerequisites). Leave them running afterwards: the developer needs the sak.
5. **Run one test at a time:**
   ```bash
   mvn test -P <profile> -Dtest="<ClassName>#<methodName>" -Dstopp.ved=<code>
   ```
   The test stops when the saksbehandler is about to solve that aksjonspunkt. Earlier aksjonspunkter are solved on the way. The test is reported as skipped/aborted, not failed.
6. **Report** the lines the test prints:
   ```
   STOPPET ved aksjonspunkt <code>
   Saksnummer: <n>
   fpsak-frontend: http://localhost:9000/fagsak/<n>/
   ```
   If `STOPPET` is missing, the test never reached the aksjonspunkt: say so, do not report a sak.

## Building and Testing Local Application Changes

When a developer wants to test local changes to an application:

1. **Build the application** (in the application's repo directory):
   ```bash
   mvn clean install -DskipTests
   docker build -t <repo-name> .
   ```

2. **Generate a fresh `.env` file** by running the setup script in fp-autotest:
   ```bash
   cd ~/git/fp-autotest/lokal-utvikling
   ./setup-lokal-utvikling.sh
   ```
   This creates/regenerates `lokal-utvikling/docker-compose-lokal/.env` with the latest remote image references.

3. **Edit `.env`** to point to the locally built image instead of the remote one.
   Change the relevant `*_IMAGE` variable from the GAR reference to the local tag.
   Example for fp-sak:
   ```
   # Change from:
   FPSAK_IMAGE=europe-north1-docker.pkg.dev/.../navikt/fp-sak:latest
   # To:
   FPSAK_IMAGE=fp-sak:latest
   ```

4. **Start all services** via docker-compose (all services are needed regardless of which suite you run):
   ```bash
   cd ~/git/fp-autotest/lokal-utvikling/docker-compose-lokal
   docker compose up --detach
   ```
   Wait for all services to be healthy: `docker compose ps`

5. **Run the tests** (from the fp-autotest root):
   ```bash
   cd ~/git/fp-autotest
   mvn test -P <suite> -Dtest=<TestClass>
   ```

## Application → Image Variable and Suite Mapping

| Repository | Docker build tag | .env variable | Docker Compose service | Recommended test suites |
|------------|-----------------|---------------|----------------------|------------------------|
| fp-sak | fp-sak | FPSAK_IMAGE | fpsak | `fpsak`, `verdikjede` |
| fp-abakus | fp-abakus | FPABAKUS_IMAGE | fpabakus | `fpsak`, `verdikjede` |
| fp-kalkulus | fp-kalkulus | FPKALKULUS_IMAGE | fpkalkulus | `fpkalkulus`, `fpsak`, `verdikjede` |
| fptilbake | fptilbake | FPTILBAKE_IMAGE | fptilbake | `fptilbake`, `verdikjede` |
| fpoppdrag | fpoppdrag | FPOPPDRAG_IMAGE | fpoppdrag | `verdikjede` |
| fp-formidling | fp-formidling | FPFORMIDLING_IMAGE | fpformidling | `verdikjede` |
| fp-dokgen | fp-dokgen | FPDOKGEN_IMAGE | fpdokgen | `verdikjede` |
| fp-risk | fp-risk | FPRISK_IMAGE | fprisk | `verdikjede` |
| fp-mottak | fp-mottak | FPMOTTAK_IMAGE | fpmottak | `verdikjede` |
| fp-oversikt | fp-oversikt | FPOVERSIKT_IMAGE | fpoversikt | `verdikjede` |
| fp-soknad | fp-soknad | FPSOKNAD_IMAGE | fpsoknad | `verdikjede` |
| fplos | fplos | FPLOS_IMAGE | fplos | `fplos` |
| fp-inntektsmelding | fp-inntektsmelding | FPINNTEKTSMELDING_IMAGE | fpinntektsmelding | `verdikjede` |
| fp-inntektsmelding-api | fp-inntektsmelding-api | FPINNTEKTSMELDINGAPI_IMAGE | fpinntektsmeldingapi | `verdikjede` |
| fp-tilgang | fp-tilgang | FPTILGANG_IMAGE | fptilgang | `verdikjede` |

## Filtering by ytelse

All suites support ytelse sub-filtering via JUnit tags: foreldrepenger, engangsstonad, svangerskapspenger

 ```bash
 mvn test -P <suite> -Dgroups="foreldrepenger"
 mvn test -P <suite> -Dgroups="foreldrepenger | svangerskapspenger"
```

## Prerequisites

**All services must be running** before executing any test suite. Start the full environment with:
```bash
cd ~/git/fp-autotest/lokal-utvikling
./setup-lokal-utvikling.sh
cd docker-compose-lokal
docker compose up --detach
```
This starts all services (oracle, postgres, vtp, fpsak, fpabakus, fpkalkulus, fplos, fpformidling, fpdokgen, fpoppdrag, fptilbake, fprisk, fpmottak, fpsoknad, fpoversikt, fpinntektsmelding, fpinntektsmeldingapi, fptilgang).

Wait for all services to be healthy before running tests:
```bash
docker compose ps
```

For IDE debugging (run specific apps outside Docker):
```bash
cd ~/git/fp-autotest/lokal-utvikling
./setup-lokal-utvikling.sh fpsak     # run fpsak in IDE, rest in Docker
cd docker-compose-lokal
docker compose up --detach --scale fpsak=0
```

## Service Lifecycle Management

**Default behavior:** Shut down all services after tests complete unless the user explicitly asks to keep them running for more tests.

### After running tests
- If tests **pass** and user has no more tests: shut down services.
- If tests **fail** and user wants to fix and retry: keep services running.
- If user says "run more tests" or "keep running": leave services up.

### Shutdown (default after tests complete)
```bash
cd ~/git/fp-autotest/lokal-utvikling/docker-compose-lokal
docker compose down
```

### Rebuild cycle (code change → rebuild → retest)
When the user makes code changes and wants to retest:
1. Shut down services: `docker compose down`
2. Rebuild the changed app (in its repo): `mvn clean install -DskipTests && docker build -t <repo-name> .`
3. Start all services again: `docker compose up --detach`
4. Re-run the tests

Alternatively, restart only the changed service (faster but less safe):
```bash
docker compose up --detach --force-recreate <service-name>
```

### On session exit
When the Copilot session ends or the user is done:
1. Shut down all services: `docker compose down`
2. This removes containers but preserves images for next session.

**Ask the user** before shutting down if there is any ambiguity about whether more tests will be run.
