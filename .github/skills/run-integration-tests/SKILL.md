---
name: run-integration-tests
description: Build, deploy, and run fp-autotest integration tests against a local Docker Compose environment. Use this skill when a developer wants to test local application changes, run specific test suites, or needs a sak that stops at a given aksjonspunkt for manual testing, or test persons (mor and far) for a søknad they fill out themselves.
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

   **Ask when the choice is open.** The mapping omits the package, and `Fodsel`, `Termin`, `Adopsjon`, `Klage` and `Revurdering` exist under several ytelser. Find each candidate with `grep -rn "void <method>(" src/test/java`, read its `@DisplayName` and body, and keep only tests that reach a stop call for the code (step 5). Compare ytelse, søker, familiehendelse, papir or digital søknad, and the scenario at the stop.

   If they differ on something the developer did not specify, ask before running (`ask_user` when available). Show one numbered list of ytelse and scenario, without test names, for example «Foreldrepenger: mor søker for 2 barn, bare 1 er registrert». Merge equal options and end with «Annet: beskriv scenarioet du trenger»; if picked, go to step 2 from «Explain briefly». Filter after each answer and ask again while differences remain. Pick yourself only when the developer says it does not matter.
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
   Use the fully qualified class name when it exists in several packages, e.g. `no.nav.foreldrepenger.autotest.fpsak.engangsstonad.Fodsel`.

   The test stops when the saksbehandler is about to solve that aksjonspunkt. Earlier aksjonspunkter are solved on the way. The test is reported as skipped/aborted, not failed.

   The stop is triggered in `Saksbehandler` by `hentAksjonspunkt(<code>)` and by `hentAksjonspunktbekreftelse`/`bekreftAksjonspunkt`. A new test written to reach an aksjonspunkt only needs to end with `saksbehandler.hentAksjonspunkt("<code>")`; no bekreftelse class is required. `harAksjonspunkt` does not stop.
6. **Report** the lines the test prints:
   ```
   STOPPET ved aksjonspunkt <code>
   Saksnummer: <n>
   fpsak-frontend: http://localhost:9000/fagsak/<n>/
   ```
   If `STOPPET` is missing, the test never reached the aksjonspunkt: say so, do not report a sak.

## Create a Family for a Søknad the Developer Fills Out

When a developer wants test persons for a søknad they fill out themselves (e.g. «lag ein søknad som eg fyller ut»), work out the scenario first. `ManuellSoknad` is the default template, not a limit: it creates mor and far in VTP without a søknad and sends inntektsmelding afterwards. When the scenario needs something it cannot do, write a temporary test based on it.

1. **Ask in one question** (`ask_user` when available) about what the developer did not specify, derived from the scenario rather than from the parameters of `ManuellSoknad`:
   - who is in the family and who applies: mor and far, mor and medmor, far and far, or one parent alone (aleineomsorg, no annan part)
   - familiehending: fødsel (how long ago), termin, or adopsjon, and number of barn
   - inntekt for each parent: arbeid, frilans, sjølvstendig næringsdrivande, or ingen
   - if anyone has arbeid: inntektsmelding `auto` (sent when the forespørsel exists) or `vent` (developer decides after sending the søknad). Ask about `auto`/`vent` unless the request answers it: «send (inn) inntektsmelding» means `auto` and that both parents have arbeid unless the developer says otherwise; «eg vel sjølv»/«vent med inntektsmelding» means `vent`.

   Read the request first and never ask about anything it already answers.

   Ask a follow-up only when an answer opens new choices. Pick defaults only when the developer says it does not matter.
2. **Choose the test.** If the answers fit the parameters of `ManuellSoknad` (mor and far, fødsel 2 veker sidan or termin, arbeid or ingen), run it as in step 4. Otherwise copy `src/test/java/no/nav/foreldrepenger/autotest/fpsak/foreldrepenger/ManuellSoknad.java` to `ManuellSoknadMellombels<Scenario>.java` in the same folder (the name is git-ignored), rename the class, and change only what the scenario needs, using `FamilieGenerator`, `PersonGenerator` (`mor()`, `medmor()`, `far()`) and `InntektGenerator`. Keep `@Tag("utvikling")`, the `familie.txt` output and the inntektsmelding handling, and adjust the labels in `familie.txt` to the roles. Tell the developer which file you made.
3. **Check the services** as in step 4 of «Create a Sak That Stops at an Aksjonspunkt».
4. **Run it.** Without `-Dmanuell.inntektsmelding` it ends once the family exists. With `auto` or `vent` it waits up to 1 hour, so run it in async mode. A new run stops an older one that is still waiting:
   ```bash
   mvn test -P utvikling -Dtest="<ManuellSoknad|ManuellSoknadMellombels<Scenario>>" -Dmanuell.familiehendelse=<fodsel|termin> -Dmanuell.mor=<arbeid|ingen> -Dmanuell.far=<arbeid|ingen> -Dmanuell.inntektsmelding=<auto|vent>
   ```
   A temporary test may drop or add `-Dmanuell.*` properties; use the ones it reads.
5. **When the log says `Familie opprettet, se`**, give the developer `cat target/manuell-soknad/familie.txt` instead of the fødselsnummer, which may be masked in your output. The søknad is at http://localhost:9101; log in as the parent who applies.
6. **With `auto`**, report the saksnummer the test logs. **With `vent`**, when the søknad is sent, ask who gets inntektsmelding (only parents with arbeid), then run `echo <mor|far|begge|ingen> > target/manuell-soknad/inntektsmelding` and report the saksnummer or error it logs.
7. **Delete the temporary test** once the run has ended (family made, and inntektsmelding sent or skipped). Never commit it.
8. **Leave the services running**, also on session exit and in spite of «Service Lifecycle Management», until the developer asks to stop them.

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
