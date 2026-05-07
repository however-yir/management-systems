# Aurora Mall Development Guide

## Goals

Aurora Mall is maintained as a practical commerce codebase that can be extended into a portfolio or production-style project.

## Current Priorities

- break large service implementations into clearer domain modules
- remove remaining upstream naming from templates and assets
- add automated tests for admin and checkout flows
- move more constants into configuration properties

## Build Commands

```bash
mvn clean package
mvn spring-boot:run
```

## Configuration Strategy

- `application.properties` contains environment placeholders
- `application-dev.properties` keeps development overrides
- `application-prod.properties` keeps production overrides
- upload storage is driven by `APP_UPLOAD_DIR`

## Notes for Further Refactoring

- introduce DTOs for controller boundaries
- isolate order workflow state transitions
- extract upload, catalog, and homepage configuration services into separate bounded modules
