# fp-autotest — Integration Test Agent Instructions

This file helps Copilot find, recommend, and execute integration tests for Team Foreldrepenger's applications.

## How to Use

When a developer asks about integration tests:
- **Find tests by test suite or benefit** → read [test-conventions.instructions.md](.github/instructions/test-conventions.instructions.md)
- **Find tests by aksjonspunkt code** → read [AKSJONSPUNKT_MAPPING.md](AKSJONSPUNKT_MAPPING.md)
- **Create a sak that stops at an aksjonspunkt for manual testing** → pick a test for the code in [AKSJONSPUNKT_MAPPING.md](AKSJONSPUNKT_MAPPING.md) (prefer a short, simple one) and run it with `-Dstopp.ved=<code>`, e.g. `mvn test -P <suite> -Dtest="Fodsel#morSøkerFødselMedEttArbeidsforhold_AvvikIBeregning" -Dstopp.ved=5038`. The test stops when the saksbehandler is about to solve that aksjonspunkt, prints saksnummer and fpsak-frontend link, and is reported as aborted, not failed. Earlier aksjonspunkter are solved by the test on the way. Run one test at a time.
- **Find tests by DisplayName or browse the catalog** → read [TEST_CATALOG.md](TEST_CATALOG.md)
- **Run tests** (build, deploy, execute, lifecycle) → use the `run-integration-tests` skill in `.github/skills/run-integration-tests/`
