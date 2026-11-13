# fp-autotest — Integration Test Agent Instructions

This file helps Copilot find, recommend, and execute integration tests for Team Foreldrepenger's applications.

## How to Use

Paths below are relative to the fp-autotest repo root. Run commands from that root, also when the session started in a parent folder with several repos.

When a developer asks about integration tests:
- **Find tests by test suite or benefit** → read [test-conventions.instructions.md](.github/instructions/test-conventions.instructions.md)
- **Find tests by aksjonspunkt code** → read [AKSJONSPUNKT_MAPPING.md](AKSJONSPUNKT_MAPPING.md)
- **Create a sak that stops at an aksjonspunkt for manual testing** → use the `run-integration-tests` skill, section «Create a Sak That Stops at an Aksjonspunkt» (`-Dstopp.ved=<code>`). If the Docker services are not running, start them without asking
- **Find tests by DisplayName or browse the catalog** → read [TEST_CATALOG.md](TEST_CATALOG.md)
- **Run tests** (build, deploy, execute, lifecycle) → use the `run-integration-tests` skill in `.github/skills/run-integration-tests/`
