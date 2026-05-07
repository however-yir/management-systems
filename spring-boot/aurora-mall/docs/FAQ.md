# Aurora Mall FAQ

## Why does Aurora Mall still look partly legacy inside the code?

The repository is being reworked from an upstream codebase. Visible branding and configuration have already been updated, while deeper service and naming cleanup is still ongoing.

## Where is the upload directory configured?

Use `APP_UPLOAD_DIR`. If unset, Aurora Mall writes to `$HOME/.aurora-mall/upload/`.

## Local Database Name

Create `aurora_mall_db` and import `src/main/resources/aurora_mall_schema.sql`.

## Running Without Docker

Yes. Start MySQL locally, export the environment variables from `.env.example`, then run `mvn spring-boot:run`.
