# fp-autotest — Integration Test Agent Instructions

This file helps Copilot find, recommend, and execute integration tests for Team Foreldrepenger's applications.

## How to Use

When a developer asks about integration tests:
- **Find tests by aksjonspunkt code** → read [AKSJONSPUNKT_MAPPING.md](AKSJONSPUNKT_MAPPING.md)
- **Find tests by DisplayName or browse the catalog** → read [TEST_CATALOG.md](TEST_CATALOG.md)
- **Run tests** (build, deploy, execute, lifecycle) → use the `run-integration-tests` skill in `.github/skills/run-integration-tests/`

## Available Test Suites

| Suite | Tags | Run command |
|-------|------|------------|
| fpsak | fpsak | `mvn test -P fpsak` |
| fptilbake | fptilbake, tilbakekreving | `mvn test -P fptilbake` |
| fpkalkulus | fpkalkulus | `mvn test -P fpkalkulus` |
| fplos | fplos | `mvn test -P fplos` |
| verdikjede | verdikjede | `mvn test -P verdikjede` |

Sub-suites: `foreldrepenger`, `engangsstonad`, `svangerskapspenger`

Run a specific test: `mvn test -P <suite> -Dtest="ClassName#methodName"`

