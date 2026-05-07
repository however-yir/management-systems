# Aurora Mall Project Ownership Checklist (35 items)

Use this list to turn the fork into a clearly independent project.

## A. Identity and Branding

1. Rename remote repository to `aurora-mall`.
2. Update repository description to your own product positioning.
3. Replace GitHub Topics with your own stack and domain tags.
4. Replace all template page titles with your project name.
5. Replace all favicon and logo references with `aurora-*` assets.
6. Remove dead references to legacy logo file names in CSS.
7. Unify footer copyright owner and organization naming.
8. Add a brand policy file (`AURORA_BRAND_POLICY.md`).

## B. Package and Namespace

9. Ensure all Java package declarations use `io.howeveryir.auroramall`.
10. Ensure all imports no longer reference `ltd.newbee.mall`.
11. Align MyBatis XML namespaces with migrated package paths.
12. Rename class identifiers that still contain old product naming.
13. Align module names in build artifacts and folder naming.

## C. Build and Dependency Management

14. Lock Maven coordinates to your own `groupId` and `artifactId`.
15. Upgrade Java target from 8 to 17 on a dedicated migration branch.
16. Upgrade Spring Boot to 3.x with Jakarta migration plan.
17. Replace deprecated `mysql-connector-java` usage with current artifact coordinates.
18. Add dependency vulnerability scanning in CI.

## D. Config and Secret Hygiene

19. Keep `.env.example` with placeholders only, never real credentials.
20. Route DB/Redis/Ollama endpoints through environment variables.
21. Separate `local/dev/staging/prod` property files with clear intent.
22. Add `.env*` and upload directories to `.gitignore`.
23. Document required environment variables in README.
24. Add startup-time validation for mandatory secrets in production profile.

## E. Data and Initialization

25. Remove legacy brand words from SQL seed data.
26. Replace default weak admin credentials in seed scripts.
27. Add idempotent migration scripts for future schema evolution.
28. Split demo seed data from production baseline schema.

## F. Architecture and Code Quality

29. Split large service classes (order/cart/user) by domain responsibility.
30. Introduce DTOs for controller boundaries to reduce coupling.
31. Extract payment and inventory workflows into dedicated modules.
32. Introduce unified exception and error-code strategy.
33. Add API contracts for admin operations and mall flows.

## G. Verification and Delivery

34. Add route-template-static consistency checks in CI.
35. Add automated tests for login, checkout, and admin CRUD critical paths.
