# fp-autotest — Integration Test Agent Instructions

This file helps Copilot find, recommend, and execute integration tests for Team Foreldrepenger's applications.

## How to Use

Paths below are relative to the fp-autotest repo root. Run commands from that root, also when the session started in a parent folder with several repos.

When a developer asks about integration tests:
- **Find tests by test suite or benefit** → read [test-conventions.instructions.md](.github/instructions/test-conventions.instructions.md)
- **Find tests by aksjonspunkt code** → read [AKSJONSPUNKT_MAPPING.md](AKSJONSPUNKT_MAPPING.md)
- **Create a sak that stops at an aksjonspunkt for manual testing** → use the `run-integration-tests` skill, section «Create a Sak That Stops at an Aksjonspunkt» (`-Dstopp.ved=<code>`). If the Docker services are not running, start them without asking
- **Test the application UI manually with Playwright** → use `lokal-utvikling/playwright/` only when the prompt explicitly asks for Playwright, and read [the local Playwright guide](lokal-utvikling/playwright/README.md) first. The guide's commands assume `cd lokal-utvikling/playwright`; from the repo root, prefix the path instead: `lokal-utvikling/playwright/scripts/familie.sh B1 termin auto mor` and `node lokal-utvikling/playwright/run.mjs scenarier/ny-soknad.mjs B1 mor` (`run.mjs` resolves scenarios and `.state/` relative to itself). When the request covers more than one case or family, ask the user whether to run them in parallel (at most 5 at a time) or one at a time before starting any of them
- **Find tests by DisplayName or browse the catalog** → read [TEST_CATALOG.md](TEST_CATALOG.md)
- **Run tests** (build, deploy, execute, lifecycle) → use the `run-integration-tests` skill in `.github/skills/run-integration-tests/`
