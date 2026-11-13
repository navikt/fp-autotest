---
name: run-integration-tests
description: Build, deploy, and run fp-autotest integration tests against a local Docker Compose environment. Use this skill when a developer wants to test local application changes, run specific test suites, or needs a sak that stops at a given aksjonspunkt for manual testing.
---

# Run Integration Tests

All paths in this skill are relative to the fp-autotest repo root. Run every `mvn` and `docker` command from that root, also when the session started in a parent folder that contains several repos (set the working directory to the fp-autotest checkout instead of assuming the current one).

## Running Tests

### Run a full suite
```bash
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
2. **If the code is not in the mapping**, say so. Do not guess a test, and do not suggest other codes: neighbouring codes are unrelated. Instead, find out how the aksjonspunkt is created:
   - The definition is in `navikt/fp-sak`: `behandlingslager/domene/src/main/java/no/nav/foreldrepenger/behandlingslager/behandling/aksjonspunkt/AksjonspunktDefinisjon.java`, with the codes in `AksjonspunktKodeDefinisjon.java` in the same folder. Find the constant for the code, then search for where that constant is utledet.
   - Use a local checkout if there is one: `../fp-sak` from the fp-autotest root, or `fp-sak` next to `fp-autotest` when the session started in a parent folder.
   - Without a local checkout, use GitHub: read the file with `gh api -H "Accept: application/vnd.github.raw" repos/navikt/fp-sak/contents/<path>` (or `https://raw.githubusercontent.com/navikt/fp-sak/master/<path>`), and find usages with `gh search code "<CONSTANT_NAME>" --repo navikt/fp-sak`.
   - Beregning aksjonspunkter (e.g. 5038, 5046, 5058, 5059) are utledet in `navikt/ft-beregning` / `navikt/fp-kalkulus`, not in fp-sak. There they are avklaringsbehov with string codes, not numbers (5038 is `FASTSETT_BG_AT_FL` in ft-beregning's `AvklaringsbehovDefinisjon`). Find the name via the mapping in fp-sak's `BeregningAksjonspunktResultatMapper`, then search ft-beregning for that name and the `AvklaringsbehovUtleder*` classes.
   - If neither a local checkout nor GitHub is reachable (no `gh` auth, network denied), say so and ask the developer. Do not guess the scenario.

   Explain briefly which scenario triggers it, and ask whether the developer wants a new test in fp-autotest for that scenario. If yes, add it to `src/test/java/no/nav/foreldrepenger/autotest/fpsak/foreldrepenger/Aksjonspunkter.java`, then add the test to `AKSJONSPUNKT_MAPPING.md`.
3. **Pick the profile from the test class's `@Tag`s, not from its package.** A profile only runs classes whose tags match its `includeTestsTags` in `pom.xml`. Example: `Aksjonspunkter` is tagged `util` and `foreldrepenger`, so `-P fpsak` runs 0 tests; use `-P foreldrepenger`. If the run reports `Tests run: 0`, check the tags.
4. **Check that the services are running** (`docker ps`). If they are not, start them yourself without asking, using the commands under Prerequisites, and wait until they are up before running the test. Leave them running afterwards: the developer needs the sak.

   If the sandbox denies the Docker check (`operation not permitted` on `~/.docker`, or the Docker socket is blocked), skip the check and go straight to step 5. The test talks to the services over HTTP and needs no Docker access. If the test fails because a service cannot be reached, report that the environment is probably not running and that it has to be started outside the sandbox.
5. **Run one test at a time:**
   ```bash
   mvn test -P <profile> -Dtest="<ClassName>#<methodName>" -Dstopp.ved=<code>
   ```
   The test stops when the saksbehandler is about to solve that aksjonspunkt. Earlier aksjonspunkter are solved on the way. The test is reported as skipped/aborted, not failed.

   The stop is triggered in `Saksbehandler` by `hentAksjonspunkt(<code>)` and by `hentAksjonspunktbekreftelse`/`bekreftAksjonspunkt`. A new test written to reach an aksjonspunkt only needs to end with `saksbehandler.hentAksjonspunkt("<code>")`; no bekreftelse class is required. `harAksjonspunkt` does not stop.
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
   cd lokal-utvikling
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
   cd lokal-utvikling/docker-compose-lokal
   docker compose up --detach
   ```
   Wait for all services to be healthy: `docker compose ps`

5. **Run the tests** (from the fp-autotest root):
   ```bash
   cd <fp-autotest root>
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
cd lokal-utvikling
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
cd lokal-utvikling
./setup-lokal-utvikling.sh fpsak     # run fpsak in IDE, rest in Docker
cd docker-compose-lokal
docker compose up --detach --scale fpsak=0
```

## Service Lifecycle Management

**Default behavior:** Shut down all services after tests complete unless the user explicitly asks to keep them running for more tests.

**Exception: a sak created with `-Dstopp.ved`.** Never shut down services after such a run, also not on session exit: the sak lives in the running containers, and `docker compose down` removes it. Leave them up until the user asks to shut them down.

### After running tests
- If tests **pass** and user has no more tests: shut down services.
- If tests **fail** and user wants to fix and retry: keep services running.
- If user says "run more tests" or "keep running": leave services up.

### Shutdown (default after tests complete)
```bash
cd lokal-utvikling/docker-compose-lokal
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
1. Shut down all services: `docker compose down`, unless a sak was created with `-Dstopp.ved` in this session (see the exception above)
2. This removes containers but preserves images for next session.

**Ask the user** before shutting down if there is any ambiguity about whether more tests will be run.
