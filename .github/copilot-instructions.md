# fp-autotest

Centralized integration and verdikjede test repo for Team Foreldrepenger.

## Shared context

- Source of truth for shared domain, architecture, and conventions: `navikt/fp-context`
- Copilot Space: `navikt/TeamForeldrepenger`

## Repo-specific context

| Topic | Details                                            |
|---|----------------------------------------------------|
| Role | Integration tests for all fp backend apps          |
| Tech stack | JUnit 6, Maven profiles, Docker Compose, Allure    |
| Consumers | All fp backend repos (verification step)           |
| Data | Test scenarios built via `fp-autotest` → VTP mocks |

## Key references

| Need | Read |
|---|---|
| Find tests by aksjonspunkt | `AKSJONSPUNKT_MAPPING.md` |
| Browse test catalog | `TEST_CATALOG.md` |
| Run tests (build, deploy, execute) | skill `run-integration-tests` |
| Test code conventions | `.github/instructions/test-conventions.instructions.md` |

## Project structure

| Path | Purpose |
|---|---|
| `src/main/java/` | Test actors such as Saksbehandler and Gosys, plus API clients |
| `src/test/java/` | Test classes; see `TEST_CATALOG.md` |
| `lokal-utvikling/` | Local scripts and docker-compose |
| `pipeline/` | CI/CD docker-compose and scripts |
| `pom.xml` | Maven config with test profiles |
