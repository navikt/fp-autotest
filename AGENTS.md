# fp-autotest — Integration Test Agent Instructions

This file helps Copilot find, recommend, and execute integration tests for Team Foreldrepenger's applications.

## How to Use

When a developer asks about integration tests:
- **Find tests by test suite or benefit** → read [test-conventions.instructions.md](.github/instructions/test-conventions.instructions.md)
- **Find tests by aksjonspunkt code** → read [AKSJONSPUNKT_MAPPING.md](AKSJONSPUNKT_MAPPING.md)
- **Create a sak or test family in the local environment** (stop at an aksjonspunkt, søk manually in søknadsappen, send inntektsmelding) → use the `testsak` skill in `.github/skills/testsak/`
- **Learn what test data triggers an aksjonspunkt** → read [katalog/README.md](katalog/README.md) and the entry in `katalog/aksjonspunkt/`
- **Find tests by DisplayName or browse the catalog** → read [TEST_CATALOG.md](TEST_CATALOG.md)
- **Run tests** (build, deploy, execute, lifecycle) → use the `run-integration-tests` skill in `.github/skills/run-integration-tests/`
