# Development Guide

This document covers development setup and practices for the **EnduranceTrio** project. For
an overview of the project, see the [main README.md](../README.md).

## Table of Contents

1. [Technology Stack](#technology-stack)
2. [Repository and Release Topology](#repository-and-release-topology)
3. [API Key Management](#api-key-management)
4. [Database](#database)
5. [Tracker Domain Development](#tracker-domain-development)
6. [Installation](#installation)
7. [Run the application](#run-the-application)
8. [Code & Naming Conventions](#code--naming-conventions)
9. [Testing](#testing)
10. [Programmatic Version Management](#programmatic-version-management)
11. [Building Custom Images with Workflow Dispatch](#building-custom-images-with-workflow-dispatch)
12. [SonarQube Cloud Configuration](#sonarqube-cloud-configuration)

## Technology Stack

- **Java 21** - Latest LTS version for optimal performance and features
- **Spring Boot 4.0.0** - Modern application framework with latest stable features
- **Spring Data JPA** - Robust data persistence and repository abstraction
- **PostgreSQL 18** - An advanced Open Source Relational Database for data persistence
- **H2 Database** - In-memory database for testing
- **Flyway** - Database migration and version control
- **Spring Security** - API key authentication and security configuration
- **SpringDoc OpenAPI** - Automated Swagger/OpenAPI documentation generation
- **Maven** - Dependency management and build automation
- **Webpack** - Frontend asset bundling, triggered automatically by the Maven build

## Repository and Release Topology

EnduranceTrio uses a dual-repository model:

- **Source repository (personal account):**
  [`EnduranceCode/endurancetrio-community`](https://github.com/EnduranceCode/endurancetrio-community)
- **Release and image publishing repository (organization account):**
  [`endurancetrio/endurancetrio-community`](https://github.com/endurancetrio/endurancetrio-community)

This means day-to-day development and documentation updates happen in the personal repository,
while release publishing and Docker image publication are executed in the organization repository.

Docker images are published to:

`ghcr.io/endurancetrio/endurancetrio-community`

> **Note:** Deployment should use image tags published from the organization repository.

## API Key Management

### Overview

The **EnduranceTrio** REST API uses secure API key authentication. All API keys are stored
as bcrypt hashes in the database for enhanced security. This section explains how to generate secure
API keys, create their bcrypt hashes, and store them in the database.

### Generate Secure API keys

Generate cryptographically secure random API keys, with `openssl`, executing the following command:

```shell
openssl rand -base64 32
```

The above command generates a 32-character secure API key. A 48-character secure API key (even more
secure), can be generated executing the following command:

```shell
openssl rand -base64 48
```

### Generate bcrypt Hashes

Using **Python3** is the recommended method to generate bcrypt hashes. On Ubuntu/Debian, ensure
the bcrypt library is installed with the following command:

```shell
sudo apt update && sudo apt install python3-bcrypt
```

Then, replace the placeholder in the below command as appropriate and execute it to generate
the bcrypt hash from the previously generated API key.

```shell
python3 -c "import bcrypt; print(bcrypt.hashpw('{API_KEY}'.encode('utf-8'), bcrypt.gensalt(rounds=12)).decode('utf-8'))"
```

> **Placeholder Definition**
>
> + **{API_KEY}** : The API key

### Initialize First Account via Environment Variables

The application supports automatic initialization of the first tracker account using environment
variables. This is the recommended approach for initial setup.

The service responsible for initializing the first tracker account will be executed upon application
first startup. This service checks for the presence of environment variables `FIRST_OWNER` and
`FIRST_HASH` during application startup. If both variables are provided and valid, it creates
the initial tracker account in the database. If an account with the provided owner name already
exists in the database, its key hash will be overridden with the provided key hash.

### Complete Workflow

```shell
# 1. Generate a secure API key
API_KEY=$(openssl rand -base64 32)
echo "Generated API Key: ${API_KEY}"

# 2. Set environment variables and start application
export FIRST_OWNER="system"
export FIRST_HASH=$(python3 -c "import bcrypt; print(bcrypt.hashpw('${API_KEY}'.encode('utf-8'), bcrypt.gensalt(rounds=12)).decode('utf-8'))")

# 3. Check if the environment variables are correct
echo "First Owner: ${FIRST_OWNER}"
echo "First Hash: ${FIRST_HASH}"

# IMPORTANT: Store the raw API key securely - you won't be able to retrieve it later!
# Only the bcrypt hash should be stored in the database.
```

### Store Hashes in the Database

Access the database console, replace the placeholders in the below SQL command as appropriate and
execute it to insert the new account into the `tracker_account` table (see the
[database section](#database)).

```sql
INSERT INTO tracker_account (owner, account_key, enabled, version, created_at)
    VALUES ('{OWNER}', '{API_KEY_HASH}', TRUE, 0, CURRENT_TIMESTAMP);
```

> **Placeholder Definition**
>
> + **{OWNER}** : The name of the owner/user of the API key
> + **{API_KEY_HASH}** : The bcrypt hash of the API key (not the raw API key)

### Security Best Practices

1. Key Generation
    - Use cryptographically secure random generators
    - Minimum 32 characters length
    - Base64 encoding for URL-safe characters
2. Hash Storage
    - Always use bcrypt with cost factor 12
    - Never store raw API keys in the database
    - Include account name and creation timestamp
3. Operational Security
    - Securely transmit the raw API key to the end user once
    - Implement key rotation policies
    - Monitor and audit API key usage
    - Store the raw key securely during initial distribution
    - When the application initial startup is completed, unset the environment variables
      `FIRST_OWNER` and `FIRST_HASH`.

### Verification

You can verify API keys work by testing with the provided endpoints using the
`Authorization: Bearer api-key-here` and `ET-Owner: account-name-here` headers as shown
in the [API Endpoints](../README.md#api-endpoints) section of the [main README.md](../README.md).

## Database

The application uses a [PostgreSQL](https://www.postgresql.org/) database and an *H2 in-memory
database*, configured with PostgreSQL compatibility mode for testing purposes.

All database schema changes are managed with [Flyway](https://github.com/flyway/flyway). Migration
scripts are located in the `endurancetrio-data/src/main/resources/db/migration` folder and are
automatically executed on application startup. Migrations support both H2 (test)
and PostgreSQL (development and production).

The file [`DATABASE.md`](../endurancetrio-data/src/main/resources/db/DATABASE.md) documents the
development and management of the application's database.

### Database, Schema, and Application User Setup

Login into the [PostgreSQL](https://www.postgresql.org/) server, replace the placeholders in the
commands below as appropriate, and execute them
to [create](https://www.postgresql.org/docs/current/sql-createdatabase.html) the database
for the **EnduranceTrio** application:

```shell
sudo -u postgres psql
```

```sql
CREATE DATABASE {DATABASE_NAME} ENCODING 'UTF8' LOCALE_PROVIDER icu ICU_LOCALE 'und' TEMPLATE template0;
```

> **Placeholder Definition**
>
> + **{DATABASE_NAME}** : The name chosen for the new database;

Confirm that the database was created, then connect to it:

```sql
\l
\c {DATABASE_NAME}
```

Create the schema for the **EnduranceTrio** application:

```sql
CREATE SCHEMA IF NOT EXISTS {SCHEMA_NAME};
```

> **Placeholder Definition**
>
> + **{SCHEMA_NAME}** : The name chosen for the new schema;

Confirm the schema creation:

```sql
\dn
```

Install the PostgreSQL `pg_trgm` extension in the application schema. This extension is required by
the accent-folded athlete search indexes created by Flyway. Install it as a PostgreSQL administrator
so the application user does not need database-wide `CREATE` permission:

```sql
CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA {SCHEMA_NAME};
```

Confirm the extension installation:

```sql
SELECT extname, extnamespace::regnamespace FROM pg_extension WHERE extname = 'pg_trgm';
```

The result should show `pg_trgm` in `{SCHEMA_NAME}`. The extension must be installed in the
application schema because Flyway resolves the `gin_trgm_ops` operator class using that schema.

[Create a user](https://www.postgresql.org/docs/current/sql-createuser.html) for the
**EnduranceTrio** database/schema management:

```sql
CREATE USER {USERNAME} WITH PASSWORD '{PASSWORD}';
```

> **Placeholder Definition**
>
> + **{USERNAME}** : The new account name in the PostgreSQL Server;
> + **{PASSWORD}** : The password of the new account in the PostgreSQL Server.

Confirm that the user creation:

```sql
\du
```

[Grant](https://www.postgresql.org/docs/current/sql-grant.html) the necessary permissions to the
**EnduranceTrio** schema user:

```sql
GRANT CONNECT ON DATABASE {DATABASE_NAME} TO {USERNAME};
GRANT USAGE ON SCHEMA {SCHEMA_NAME} TO {USERNAME};
GRANT CREATE ON SCHEMA {SCHEMA_NAME} TO {USERNAME};
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA {SCHEMA_NAME} TO {USERNAME};
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA {SCHEMA_NAME} TO {USERNAME};
ALTER DEFAULT PRIVILEGES IN SCHEMA {SCHEMA_NAME} GRANT ALL PRIVILEGES ON TABLES TO {USERNAME};
ALTER DEFAULT PRIVILEGES IN SCHEMA {SCHEMA_NAME} GRANT ALL PRIVILEGES ON SEQUENCES TO {USERNAME};
```

> **Placeholder Definition**
>
> + **{DATABASE_NAME}** : The name chosen for the new database;
> + **{SCHEMA_NAME}** : The name chosen for the new schema;
> + **{USERNAME}** : The account name in the PostgreSQL Server to whom the privileges will be assigned.

Confirm that the privileges were granted:

```sql
\l
```

Once all the commands were executes, exit the PostgreSQL server:

```sql
\q
```

### Dropping the Database, Schema, and Application User

To start fresh from a clean slate (e.g., after many incremental Flyway migrations, to reset
corrupted data, or to exercise the whole setup again), the database, schema, and application user
created in the [previous section](#database-schema-and-application-user-setup) can be dropped and
recreated. The steps below reverse, in order, those setup steps.

> **Warning:**
>
> All commands in this section are **destructive and irreversible**. The database, schema,
> and application user are permanently removed. Ensure the application is stopped and that you
> no longer need any of the stored data before proceeding.

1. **Stop the application**

    Ensure that no instance of the **EnduranceTrio** application is running, so that no active
    connections are held on the database:

    - Stop the `EnduranceTrioApplication` run configuration in IntelliJ, or
    - Interrupt the `./launch-app.sh` process started in a terminal.

2. **Login into the PostgreSQL server** as an administrator:

    ```shell
    sudo -u postgres psql
    ```

3. **Drop the database**

    Replace the placeholder in the below command as appropriate and execute it to
    [drop](https://www.postgresql.org/docs/current/sql-dropdatabase.html) the **EnduranceTrio**
    database. The `WITH (FORCE)` option (PostgreSQL 13 or later) automatically terminates any
    lingering connections that would otherwise block the drop:

    ```sql
    DROP DATABASE IF EXISTS {DATABASE_NAME} WITH (FORCE);
    ```

    > **Placeholder Definition**
    >
    > + **{DATABASE_NAME}** : The name chosen for the new database;

    Confirm that the database was dropped:

    ```sql
    \l
    ```

    > **Note:**
    >
    > Dropping the database also removes the application schema and all its objects (tables,
    > sequences, indexes, and the `pg_trgm` extension installed in that schema), so no separate
    > schema or extension cleanup is required.

    For PostgreSQL versions older than 13, terminate active connections manually before dropping
    the database:

    ```sql
    SELECT pg_terminate_backend(pid)
    FROM pg_stat_activity
    WHERE datname = '{DATABASE_NAME}' AND pid <> pg_backend_pid();
    ```

4. **Drop the application user**

    Replace the placeholder in the below command as appropriate and execute it to
    [drop](https://www.postgresql.org/docs/current/sql-dropuser.html) the **EnduranceTrio**
    database/schema management user:

    ```sql
    DROP ROLE IF EXISTS {USERNAME};
    ```

    > **Placeholder Definition**
    >
    > + **{USERNAME}** : The account name in the PostgreSQL Server;

    Confirm that the user was dropped:

    ```sql
    \du
    ```

    If the drop fails with *"cannot be dropped because some objects depend on it"* (for example,
    because the user owns objects in another database), first reassign or drop those objects while
    still connected as an administrator, and then retry:

    ```sql
    REASSIGN OWNED BY {USERNAME} TO postgres;
    DROP OWNED BY {USERNAME};
    DROP ROLE IF EXISTS {USERNAME};
    ```

5. **Exit the PostgreSQL server**

    ```sql
    \q
    ```

6. **Recreate the database, schema, and application user**

    Follow the [Database, Schema, and Application User Setup](#database-schema-and-application-user-setup)
    section to recreate the database, schema, `pg_trgm` extension, and user from scratch. Flyway
    then recreates all tables and baseline data on the next application startup.

    To recreate the initial tracker account on startup, set the `FIRST_OWNER` and `FIRST_HASH`
    environment variables again before starting the application (see the
    [Initialize First Account via Environment Variables](#initialize-first-account-via-environment-variables)
    section).

    > **Note:**
    >
    > For local development the default values are the database `dev_endurancetrio_community` set in
    > `application-local.yaml` and the schema `endurancetrio_hub` set in `application.yaml`. The
    > database user is the one configured in `application-secrets.yaml`.

### Troubleshooting

**Connection refused**: Ensure PostgreSQL is running:

```bash
sudo systemctl status postgresql
```

**Permission denied**: Verify user has proper grants:

```sql
SELECT * FROM information_schema.role_table_grants  WHERE grantee = '{USERNAME}';
```

If the error mentions `permission denied to create extension "pg_trgm"`, install the extension as a
PostgreSQL administrator:

```sql
CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA {SCHEMA_NAME};
```

Alternatively, grant the application user database-level `CREATE` permission and restart the
application so Flyway can install the trusted extension:

```sql
GRANT CREATE ON DATABASE {DATABASE_NAME} TO {USERNAME};
```

The failed Flyway migration is rolled back and will be retried on the next application startup.

## Tracker Domain Development

The Tracker domain (IoT telemetry ingestion and GeoJSON route management) is developed in its own
[`endurancetrio-tracker`](https://github.com/EnduranceCode/endurancetrio-tracker) repository and
incorporated into this project via `git merge`.

This dual-repo approach allows the Tracker to evolve independently while its code is periodically
merged back into this repository. The Tracker API surface is fully integrated and documented in
[`docs/api-endpoints-tracker.md`](./api-endpoints-tracker.md).

The complete integration workflow, merge steps, and conflict resolution are documented in
[`docs/tracker-integration.md`](./tracker-integration.md).

### Issue Tracking

Issues discovered in Tracker-domain code during integration should be filed in the
[`endurancetrio-tracker`](https://github.com/EnduranceCode/endurancetrio-tracker) issue tracker.

## Installation

### 1. Prerequisites

- [Java 21](https://javaalmanac.io/jdk/21/) or higher
- [Apache Maven](https://maven.apache.org/)
- [PostgreSQL](https://www.postgresql.org/)

### 2. Clone the repository

```shell
git clone git@github.com:EnduranceCode/endurancetrio-community.git
cd endurancetrio-community
```

#### 3. Configure application secrets

Create the `application-secrets.yaml` configuration file from the provided
[template](../endurancetrio-app/src/main/resources/template-secrets.yaml), with the following
command:

```shell
cp endurancetrio-app/src/main/resources/template-secrets.yaml endurancetrio-app/src/main/resources/application-secrets.yaml
```

Now, edit the `application-secrets.yaml` file:

- **Set database credentials**: replace `{USER}` and `{PASSWORD}` with your desired values.
- **Set Google AdSense publisher ID** (optional): replace `{GOOGLE_ADSENSE_ID}` with your
  Google AdSense publisher ID (e.g., `ca-pub-xxxxxxxxxxxxxx`).
- **Set Ko-fi and social IDs** (optional): replace `{KOFI_USER_ID}`, `{FACEBOOK_PAGE_ID}`,
  and `{TWITTER_SITE}` with your platform identifiers.

> **Security Notice**
>
> Never commit the application-secrets.yaml file, or any file containing credentials, to version
> control.
> Ensure that your `.gitignore` rules prevent accidental commits of sensitive configuration.

### 4. Build the project

From the repository root, run the following command to compile the application and install
its dependencies:

```shell
./mvnw clean install
```

The Maven build automatically installs the required Node.js/npm toolchain for the app module,
executes `npm ci`, bundles the frontend with Webpack, and packages the generated assets under the
Spring Boot static resource path in the final JAR.

The default Maven frontend build uses the production Webpack mode. Production assets are minified,
do not emit source maps, and optimize raster images copied by Webpack. Development builds keep
source maps enabled for browser debugging.

The selected Webpack script is controlled by the Maven property `frontend.build.script`.
The app module defaults it to `build:prod`, which keeps standard Maven packaging and
`./launch-app.sh` aligned with production-safe frontend output.

Frontend source files live in `endurancetrio-app/src/main/resources/webpack/`.
Generated frontend assets are written to
`endurancetrio-app/target/generated-resources/frontend/static/` during the build.

For standalone frontend work inside `endurancetrio-app/src/main/resources/webpack/`, the available
npm scripts are:

- `npm run generate:icons` - generate icon CSS classes from `icons.config.json`
- `npm run build:dev` - development bundle with source maps
- `npm run build:prod` - production bundle without source maps
- `npm run build:watch` - continuous development rebuilds with source maps

Icons are generated via `scripts/generate-icons.js`. The `prebuild:*` hooks run it automatically
before every webpack build, so you rarely need to call it directly. To add a new icon, edit
`icons.config.json` (add an MDI name or a custom path in `src/icons/`) and rebuild - the prebuild
hook regenerates the SCSS.

## Run the application

The application uses Spring Boot profiles for environment-specific configuration:

- `application-local.yaml` – Active during local development.
- `application-dev.yaml` – For development environments.
- `application-prod.yaml` – For production environments.

You can manually activate a profile when running the application with `spring-boot:run`:

```shell
-Dspring-boot.run.profiles=local
```

Or, for standard JAR execution:

```shell
-Dspring.profiles.active=dev
```

A helper script, `launch-app.sh`, is provided to streamline local development. It performs a full
Maven build, including frontend asset generation, and then starts the application using the
packaged JAR with the **local** profile enabled:

```shell
./launch-app.sh
```

This project also includes IntelliJ run configurations stored in the `.run/` folder. After opening
the project in [IntelliJ](https://www.jetbrains.com/idea/), the available run configurations are:

| Configuration              | Type        | Purpose                                                                                                                                                                 |
|----------------------------|-------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `EnduranceTrioApplication` | Application | Starts Spring Boot with the `local` profile. Pre-launch tasks: `Make` (Java compilation) + `GenerateFrontendAssets` (one-time frontend build).                          |
| `GenerateFrontendAssets`   | Maven       | One-time development frontend build (`build:dev` with source maps). Invoked automatically as pre-launch task by `EnduranceTrioApplication`.                             |
| `FrontendAssetsWatch`      | npm         | Runs `npm run build:watch` — continuously rebuilds SCSS and JS on change, writing output directly to `target/classes/static/`. Run in a separate tab alongside the app. |

### Hot Reload

The project is configured for fast development feedback using Spring Boot DevTools and an inline
LiveReload client:

1. **Spring Boot DevTools** starts an embedded LiveReload WebSocket server on port `35729`
   (visible in the logs as `LiveReload server is running on port 35729`)
2. An inline script in the HTML `<head>` (only rendered on the `local` profile) connects to this
   server and listens for reload commands
3. When a file changes, DevTools sends a reload notification and the browser auto-refreshes

#### Template changes (`.html`)

Templates are read directly from the source tree using a `file:` prefix (configured in
`application-local.yaml`). Saving a template triggers a LiveReload signal via DevTools and
the browser auto-refreshes — no build step, compilation, or manual refresh is needed.

#### XML sitemap template (`.xml`)

`templates/sitemap.xml` is rendered in XML template mode (`TemplateMode.XML`) by
`SitemapController`. XML mode ignores the `data-th-*` prefix used in HTML templates, so the sitemap
uses `th:*` attributes and declares `xmlns:th`. Because the file is a Thymeleaf template, it carries
`th:*` attributes that exist in no published schema, so XML schema validation (including IntelliJ
IDEA's, which additionally cannot download the sitemaps.org schema on restricted networks) reports
warnings on it. This is expected and harmless: the rendered `/sitemap.xml` output is the valid
document, as verified by `SitemapControllerTest`. To silence the IntelliJ warnings once per machine,
press Alt+Enter on the reported URI and choose *Ignore external resource*.

#### Frontend changes (`.scss`, `.js`)

For continuous frontend rebuilds, run `FrontendAssetsWatch` in a separate IntelliJ run tab.
Webpack watches the source files and rebuilds to `target/classes/static/` on every change
(the `WEBPACK_OUTPUT_DIR` env var is set in the run config). DevTools detects the new files
there and triggers a browser auto-refresh.

#### What triggers a browser refresh

| Change                       | What happens                                                                 |
|------------------------------|------------------------------------------------------------------------------|
| Thymeleaf template (`.html`) | DevTools detects the change → browser auto-refreshes                         |
| Frontend (`.scss`, `.js`)    | Webpack watch rebuilds → DevTools detects → browser auto-refreshes           |
| Java class                   | IntelliJ offers a hotswap button (no restart needed for method body changes) |

#### Profile safety

All hot-reload features are scoped to the `local` profile only:

- `spring.thymeleaf.cache: false` — only in `application-local.yaml`; production uses `cache: true`
- LiveReload client script — gated on `@environment.acceptsProfiles('local')` in the Thymeleaf
  template
- DevTools — only active when running from an IDE or `spring-boot:run`; excluded from the
  production JAR by the Spring Boot Maven plugin

### Testing Error Pages

A test controller, gated to the `local` profile, provides endpoints for verifying the layout
and behavior of error pages during development.

| URL                                 | HTTP Code | Error Page  |
|-------------------------------------|-----------|-------------|
| `/{language:en\|pt}/test/error-403` | 403       | `error/403` |
| `/{language:en\|pt}/test/error-404` | 404       | `error/404` |
| `/{language:en\|pt}/test/error-500` | 500       | `error/500` |

- **403** — throws an `EnduranceTrioException` with code 403, caught by the exception handler.
- **404** — throws an `EnduranceTrioException` with code 404, caught by the exception handler.
- **500** — throws an unhandled `RuntimeException`, caught by the catch-all handler.

Alternatively, to trigger a 403 through Spring Security's actual CSRF rejection rather than the test
controller, paste the following in the browser DevTools console:

```javascript
document.body.appendChild(
  Object.assign(document.createElement('form'), {method:'POST', action:'/en/events'})
).submit();
```

This submits a form without the `_csrf` token, causing an `AccessDeniedException` that the servlet
container forwards to the `/error` path, where `EnduranceTrioErrorController` renders the 403 page.
Replace `/en/events` with `/pt/events` for the Portuguese version.

#### Profile safety

The controller is annotated with `@Profile("local")`, so it is never instantiated outside
the `local` profile and poses no risk in production deployments.

#### Notes

- LiveReload will **not** auto-refresh error pages because the browser URL does not change on
  error. Manually refresh to see error-page updates.
- The language selector works on error pages: switching between `en` and `pt` in the URL
  changes the rendered locale.

### Running the application (IntelliJ)

1. Select `EnduranceTrioApplication` from the run configuration dropdown
2. Click `Run` (`Shift + F10`) — the app starts with a one-time frontend build
3. If editing SCSS or JS, also run `FrontendAssetsWatch` in a separate tab

### Running the application (CLI)

Use `launch-app.sh` which performs a full Maven build (including frontend asset generation)
and then starts the application using the packaged JAR with the `local` profile. For continuous
frontend rebuilds in a separate terminal, set `WEBPACK_OUTPUT_DIR` to point at
`target/classes/static/` so webpack writes directly to the path Spring Boot serves:

```shell
WEBPACK_OUTPUT_DIR=$(pwd)/endurancetrio-app/target/classes/static \
  npm --prefix endurancetrio-app/src/main/resources/webpack run build:watch
```

## Code & Naming Conventions

### Frontend Styling

- Use [`docs/color-system.md`](./color-system.md) as the canonical guide for frontend color selection
  and semantic usage.
- Keep EnduranceTrio palette tokens in
  `endurancetrio-app/src/main/resources/webpack/src/scss/utilities/endurancetrio-variables.scss`.
- Keep Bulma utility customization in
  `endurancetrio-app/src/main/resources/webpack/src/scss/utilities/endurance-bulma-utilities.scss`.
- Import the local Bulma utilities wrapper when a stylesheet needs configured members from
  `bulma/sass/utilities`.
- Import Bulma modules directly when the needed Sass members are outside that wrapper, such as
  `css-variables`, `mixins`, `extends`, or element/component styles.
- Use the `endurance-` prefix for project-maintained SCSS customizations that adapt Bulma or
  third-party code, including Klaro, switch-control, and theme entrypoints.

### Controllers, Services and Repositories

This section establishes naming guidelines for controllers, services and repositories, based on clarity,
maintainability, and semantic meaning.

#### General Principles

- **Interfaces define contracts**; implementations should have meaningful names.
- Use suffixes that reflect the role or nature of the implementation (e.g., `Main`, `Cached`, `Remote`).
- Avoid generic suffixes like `Impl` unless absolutely necessary.
- Keep naming consistent across layers.

#### Controllers

##### REST Controllers

- **Interface**:
    - Purpose: Hold OpenAPI annotations and define endpoint contracts.
    - Naming: `DomainAPI` (e.g., `UserAPI`).
- **Implementation**:
    - Annotate with `@EnduranceTrioRestController`.
    - Naming: `DomainRestController` (e.g., `UserRestController`).

```java
public interface UserAPI {
  /* OpenAPI annotations */
}

@EnduranceTrioRestController
public class UserRestController implements UserAPI {
  /* endpoints */
}
```

##### Web Controllers

- **Interface**: Optional (usually not needed unless for documentation or testing).
    - Naming: `DomainWeb` (e.g., `UserWeb`).
- **Implementation**:
    - Annotate with `@EnduranceTrioWebController`.
    - Naming: `EntityWebController` (e.g., `UserWebController`).

```java
@EnduranceTrioWebController
public class UserWebController {
  /* Thymeleaf views */
}
```

##### Web Layer Method Ordering

To facilitate navigation between the code and the live documentation, the order of the methods
within the Controller implementation must strictly follow the display order
of the Swagger/OpenAPI web page.

- **Sequence**: Methods should appear in the class in the same sequence they are rendered in the UI
  (typically sorted by path and then by HTTP verb: `GET`, `POST`, `PUT`, `DELETE`).
    - *Example*: A `GET` request for `/users` must appear before a `GET` request for `/users/{id}`.
    - *Example*: `/users/active` must appear before `/users/{id}`.
- **Grouping**: Do not scatter related endpoints; keep the code structure linear
  to the documentation output.

#### Service Layer

- **Interface**:
    - Naming: `DomainService` (e.g., `UserService`).
- **Implementation**:
    - Annotate with `@Service`.
    - Naming:
        - If single implementation: `DomainServiceMain` (e.g., `UserServiceMain`).
        - If multiple implementations: Use descriptive suffixes (e.g., `UserServiceCached`, `UserServiceRemote`).

```java
public interface UserService {
  /* business logic */
}

@Service
public class UserServiceMain implements UserService {
  /* implementation */
}
```

> **Why not `Impl`?**
> `Impl` is semantically weak. Descriptive suffixes improve readability and convey purpose.

##### Service Layer Method Ordering (CRUD Standard)

To promote maintainability, facilitate quick navigation through business logic as well as to impose
semantic structure and align methods with the entity lifecycle, the methods within the Service
implementation must be ordered based on their primary **CRUD** operation (Create, Read, Update,
Delete).

- **Primary Grouping**: Methods must first be grouped by the **Domain Entity** they primarily
operate on.
- **Secondary Ordering (Within Group)**: Within each entity group, methods must be strictly ordered
by the CRUD operation they perform:
    1.  **CREATE** Operations (e.g., `createUser`, `addAddress`)
    2.  **READ** Operations (e.g., `findById`, `findAllActive`, `existsByUsername`)
    3.  **UPDATE** Operations (e.g., `updateStatus`, `modifyPassword`)
    4.  **DELETE** Operations (e.g., `deleteById`, `removeAllInactive`)
    5.  **UTILITY/AD-HOC** Operations (Any business logic that does not fit CRUD, placed at the end).

- **Tertiary Ordering (Within CRUD Group)**: If multiple methods fall into the same CRUD category
(e.g., multiple **READ** methods), they must be ordered **alphabetically by their method name**.

- **Example (User Entity)**:
    1.  `addRole(Long userId, Role role)` (CREATE)
    2.  `create(User user)` (CREATE)
    3.  `findAllActive()` (READ)
    4.  `findById(Long id)` (READ)
    5.  `updateEmail(Long id, String newEmail)` (UPDATE)
    6.  `deleteById(Long id)` (DELETE)
    7.  `sendPasswordResetEmail(String email)` (UTILITY)

> **Rationale**: This order helps developers quickly locate methods based on what they *do*
> to the entity, providing a clear map of the entity's data lifecycle within the application.

##### Private Method Ordering

Private methods, which support the public contract, must follow a consistent placement
and ordering standard to improve internal code readability.

1.  **Placement**: All private methods must be placed **after** all public methods of the class.
2.  **Ordering**: Private methods must be ordered **alphabetically by their method name**. This
provides the simplest and most predictable structure for implementation details.

#### Repository Layer

- Prefer Spring Data JPA interfaces:

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {}
```

##### Repository Layer Method Ordering

For Repository interfaces extending Spring Data JPA base classes (e.g., `JpaRepository`),
a comprehensive method ordering rule is not strictly necessary, as the core CRUD contract
is inherited.

- **Inherited Methods**: The inherited methods (`save`, `findById`, `findAll`, `deleteById`, etc.)
are implicitly defined first.
- **Custom Methods**: Any custom query methods (defined by name or with `@Query`) must be strictly
  ordered by the CRUD operation they perform. If multiple methods fall into the same CRUD category
  (e.g., multiple **READ** methods), they must be ordered **alphabetically by their method name**.

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  // Custom query methods must be in alphabetical order:

  boolean existsByUsername(String username);

  List<User> findAllByStatus(UserStatus status);

  Optional<User> findByEmail(String email);
}
```

### Entity Layer

This section documents how JPA entities should be created and managed in the `endurancetrio-data`
module.

#### Package Structure

Each domain group follows a consistent package layout under
`com.endurancetrio.data.{domain}.model`:

```
{domain}/
  model/
    converter/          → JPA AttributeConverter implementations
    entity/             → JPA entity classes
    enumerator/         → Code-backed enums stored as VARCHAR columns
```

#### Base Classes

Two base class options are available, chosen based on the primary key strategy:

**1. `BaseEntity<Long>`** — for entities with a surrogate, sequence-generated `id`:

- **`id`** — auto-generated primary key via `GenerationType.SEQUENCE`
- **`version`** — optimistic locking field (`@Version`)
- **`createdAt` / `updatedAt`** — auditing timestamps via `@CreatedDate` / `@LastModifiedDate`
- **`equals()` / `hashCode()`** — ID-based equality (same class + same non-null ID)

```java
public class MyEntity extends BaseEntity<Long> { ... }
```

Only entities that need a non-`Long` ID type should extend `BaseEntity` with a different type
parameter; currently all entities use `Long`.

**2. `AuditableEntity`** — for entities with a natural (business) primary key that is assigned
rather than generated:

- **`version`** — optimistic locking field (`@Version`)
- **`createdAt` / `updatedAt`** — auditing timestamps via `@CreatedDate` / `@LastModifiedDate`
- **No auto-generated ID** — the entity defines its own `@Id` on a natural key field

```java
public class MyEntity extends AuditableEntity { ... }
```

#### Entity Declaration

```java

@Entity(name = "MyEntity")
@Table(name = "my_entity")
@SequenceGenerator(
    name = "seq_endurancetrio_generator",
    sequenceName = "seq_my_entity_id",
    allocationSize = 1
)
public class MyEntity extends BaseEntity<Long> {

  @Serial
  private static final long serialVersionUID = 1L;
```

- `@Entity(name = "...")` must use the simple PascalCase class name.
- `@Table(name = "...")` must use snake_case, plural table names matching the database schema.
- `@SequenceGenerator` must use the shared generator name `seq_endurancetrio_generator` and a
  sequence name following the pattern `seq_{table}_id`.
- Every entity must declare `@Serial private static final long serialVersionUID = 1L`.

#### Natural Key Entities

Entities with a natural (business) primary key extend `AuditableEntity` directly rather than
`BaseEntity<Long>`. This pattern is used when the primary key is assigned in application code and
has business meaning (e.g., `TrackerAccount` keyed by `owner`).

```java
@Entity
@Table(name = "my_natural_entity")
public class MyNaturalEntity extends AuditableEntity {

  @Id
  @Column(name = "natural_key", nullable = false, unique = true, length = 50)
  private String naturalKey;
```

- `@Entity` does **not** require a `name` attribute; the class name is used by default.
- No `@SequenceGenerator` is declared — the identifier is assigned, not generated.
- The `@Id` field is the natural key and must be annotated with `@Column(unique = true)`.
- `equals()` and `hashCode()` are based on the natural key field(s), not delegated to `super`.
- Use `@Column(length = ...)` to constrain the natural key column size.

#### Javadoc Convention

Every entity must have a class-level Javadoc block describing what the entity represents, followed
by a `<ul>` documenting each field with a link to its getter:

```java
/**
 * The {@link MyEntity} entity represents ...
 * <p>
 * The {@link MyEntity}'s fields are defined as follows:
 * <ul>
 *   <li>
 *     {@link #getId() id} : the unique identifier ...
 *   </li>
 *   <li>
 *     {@link #getFieldName() fieldName} : the description ...
 *   </li>
 * </ul>
 */
```

#### Constructor

Always provide a public no-argument constructor that calls `super()`. Initialize collection fields
to `new HashSet<>()` in the constructor:

```java
public MyEntity() {
  super();
  this.children = new HashSet<>();
}
```

#### Field & Column Naming

- Fields follow camelCase in Java.
- `@Column(name = "...")` uses snake_case matching the database column.
- Nullable columns omit `nullable`; required columns use `nullable = false`.
- Boolean fields use the `is` prefix for the field name (e.g., `isActive`) and `get`/`set` prefix
  for accessors (e.g., `getActive()`, `setActive()`).
- Enum fields are annotated with `@Convert(converter = XxxConverter.class)` and stored as VARCHAR
  codes, never as ORDINAL.
- Validation annotations (`@Pattern`, `@AssertTrue`) are placed on the field directly.

```java

@Column(name = "full_name", nullable = false)
private String fullName;

@Column(name = "is_active", nullable = false)
private Boolean isActive;

@Column(name = "race_type", nullable = false)
@Convert(converter = RaceTypeConverter.class)
private RaceType raceType;
```

#### Associations

All associations use `FetchType.LAZY`. Use `Set<>` (not `List`) for collection fields:

| Annotation    | Cascade                  | Example                                                         |
|---------------|--------------------------|-----------------------------------------------------------------|
| `@ManyToOne`  | None                     | `@ManyToOne(fetch = FetchType.LAZY)`                            |
| `@OneToOne`   | `ALL` + `orphanRemoval`  | `@OneToOne(fetch = LAZY, cascade = ALL, orphanRemoval = true)`  |
| `@OneToMany`  | `ALL` + `orphanRemoval`  | `@OneToMany(fetch = LAZY, cascade = ALL, orphanRemoval = true)` |
| `@ManyToMany` | `PERSIST, MERGE` or none | `@ManyToMany(fetch = FetchType.LAZY)`                           |

- `@OneToMany` with `@JoinColumn` (foreign key on child table) is preferred over `mappedBy` when the
  child has no inverse reference.
- `@ManyToMany` always uses a `@JoinTable` with explicit join column names.

#### Bidirectional Helper Methods

When both sides of a relationship exist, provide `addXxx` / `removeXxx` helper methods that maintain
consistency on both sides:

```java
public void addChild(Child child) {
  if (child != null && this.children.add(child)) {
    child.setParent(this);
  }
}

public void removeChild(Child child) {
  if (child != null && this.children.remove(child)) {
    child.setParent(null);
  }
}
```

#### Method Ordering

Methods in an entity class follow this order:

1. Javadoc + class declaration
2. `@Serial` / `serialVersionUID`
3. Fields (simple columns first, associations last)
4. Constructor(s)
5. `@AssertTrue` validation methods
6. Bidirectional helper methods (`addXxx` / `removeXxx`)
7. Getters and setters (alphabetically by field name)
8. `equals(Object)` → delegates to `super.equals(o)` (or natural key check for `AuditableEntity` entities)
9. `hashCode()` → delegates to `super.hashCode()` (or natural key hash for `AuditableEntity` entities)
10. `toString()` → uses `StringJoiner`

#### equals(), hashCode() and toString()

- **Surrogate key entities** (extending `BaseEntity`): `equals()` and `hashCode()` must delegate to
  `super` (the `BaseEntity` ID-based contract):

```java
@Override
public boolean equals(Object o) {
  return super.equals(o);
}

@Override
public int hashCode() {
  return super.hashCode();
}
```

- **Natural key entities** (extending `AuditableEntity` directly): `equals()` and `hashCode()` must
  be implemented based on the natural key field(s):

```java
@Override
public boolean equals(Object o) {
  if (this == o) {
    return true;
  }

  if (!(o instanceof NaturalKeyEntity that)) {
    return false;
  }

  Class<?> thisClass = org.hibernate.Hibernate.getClass(this);
  Class<?> thatClass = org.hibernate.Hibernate.getClass(that);

  if (thisClass != thatClass) {
    return false;
  }

  return naturalKey != null && naturalKey.equals(that.getNaturalKey());
}

@Override
public int hashCode() {
  return Objects.hash(naturalKey);
}
```

- `toString()` must use `StringJoiner` with the pattern
  `EntityName.class.getSimpleName() + "[", "]"`. Include all scalar fields and, for associations,
  only the referenced entity's ID (using
  `Optional.ofNullable(...).map(X::getId).orElse(null)`):

```java

@Override
public String toString() {
  return new StringJoiner(", ", MyEntity.class.getSimpleName() + "[", "]")
      .add("id=" + this.getId())
      .add("name='" + name + "'")
      .add("parentId=" + Optional.ofNullable(parent).map(Parent::getId).orElse(null))
      .toString();
}
```

#### Enums

All persisted enums must use a code-based pattern with a dedicated JPA `AttributeConverter`:

```java
public enum MyEnum {
  VALUE_A("VALUE_A"),
  VALUE_B("VALUE_B");

  private final String code;

  MyEnum(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  @Override
  public String toString() {
    return code;
  }
}
```

**Converter:**

```java

@Converter
public class MyEnumConverter implements AttributeConverter<MyEnum, String> {

  @Override
  public String convertToDatabaseColumn(MyEnum value) {
    return Optional.ofNullable(value).map(MyEnum::getCode).orElse(null);
  }

  @Override
  public MyEnum convertToEntityAttribute(String code) {
    return Stream.of(MyEnum.values())
        .filter(e -> e.getCode().equals(code))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            "The value '" + code + "' returned from the database is not a valid MyEnum code"));
  }
}
```

- Enums live in `{domain}.model.enumerator`.
- Converters live in `{domain}.model.converter`.
- Never use `EnumType.ORDINAL` or `EnumType.STRING` — always go through a converter.

#### Inheritance

Use `@Inheritance(strategy = InheritanceType.JOINED)` when entities share a common base table:

```java

@Entity(name = "Race")
@Table(name = "race")
@Inheritance(strategy = InheritanceType.JOINED)
public class Race extends BaseEntity<Long> { ...
}
```

#### Historical Reference Entities

Entities that exist solely to map database tables for historical data (not used in application
logic) must be documented as such in their Javadoc:

```java
/**
 * Historical reference entity that documents the {@code old_table} table schema.
 * <p>
 * This entity exists solely to map the database table for historical data storage.
 * It is NOT used in application code.
 * <p>
 * Do NOT import or reference this entity in business logic.
 */
```

### Configuration File Key Ordering

All `application-*.yaml` and `*.properties` files must follow a consistent key ordering convention
to reduce merge conflicts and make key locations predictable.

#### Rules

1. **Root-level keys**: ordered alphabetically.
2. **Nested keys**: ordered alphabetically within each section.
3. **Exception — `username`/`password` pairs**: kept as a logical credentials pair, with `username`
   before `password`, regardless of alphabetical order.
4. **`.properties` files**: follow the same alphabetical ordering rule.

#### Example (`application-test.yaml`)

```yaml
logging:
  level:
    org.hibernate.SQL: debug
    org.hibernate.orm.jdbc.bind: trace

spring:
  datasource:
    password: test
    url: jdbc:h2:mem:endurancetrio_tracker;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;COLLATION=und
    username: sa
```

> `username` appears before the alphabetically-earlier `password` because they form a credentials
> pair.

## Testing

This section documents the testing standards, conventions, and practices for the project. All tests
must comply with these rules.

### Testing Approach

All tests are **pure unit tests** — fast, isolated, and requiring no Spring context. There are three
categories:

| Category                  | Description                                                            | Framework                    |
|---------------------------|------------------------------------------------------------------------|------------------------------|
| **Plain POJO tests**      | Entities, DTOs, responses — verify constructors, accessors, `toString` | JUnit 5 only                 |
| **Mockito-based tests**   | Services, mappers — mock dependencies to isolate the class under test  | JUnit 5 + Mockito            |
| **Bean Validation tests** | Custom validation annotations — programmatic `Validator` setup         | JUnit 5 + Jakarta Validation |

No test currently loads a Spring application context (`@SpringBootTest`, `@DataJpaTest`,
`@WebMvcTest`, etc.). If integration tests are added in the future, they must use the existing
[test configuration](#test-configuration).

### Test Frameworks & Dependencies

| Dependency                            | Scope                            | Used In Module           |
|---------------------------------------|----------------------------------|--------------------------|
| **JUnit 5** (Jupiter)                 | Inherited via Spring Boot parent | All modules              |
| **Mockito**                           | Inherited via Spring Boot parent | `endurancetrio-business` |
| `spring-boot-starter-data-jpa-test`   | Test                             | `endurancetrio-data`     |
| `spring-boot-starter-flyway-test`     | Test                             | `endurancetrio-data`     |
| `com.h2database:h2`                   | Test                             | `endurancetrio-data`     |
| `spring-boot-starter-validation-test` | Test                             | `endurancetrio-business` |
| `spring-boot-starter-actuator-test`   | Test                             | `endurancetrio-app`      |
| `spring-boot-starter-webmvc-test`     | Test                             | `endurancetrio-app`      |
| `spring-boot-starter-security-test`   | Test                             | `endurancetrio-app`      |

Module-specific test starters are declared **per module** based on what that module needs to test.
JUnit 5 and Mockito are inherited from the Spring Boot parent POM via `spring-boot-starter-test`.

### Test Class Naming

All test classes must follow the pattern:

```
{ClassUnderTest}Test.java
```

| Test Class                   | Tests Class              |
|------------------------------|--------------------------|
| `TrackerAccountTest`         | `TrackerAccount`         |
| `RouteServiceMainTest`       | `RouteServiceMain`       |
| `DeviceTelemetryMapperTest`  | `DeviceTelemetryMapper`  |
| `RouteSegmentsValidatorTest` | `RouteSegmentsValidator` |

Service implementations use the `Main` suffix (not `Impl`), so their tests follow the same naming
(e.g., `RouteServiceMain` → `RouteServiceMainTest`).

### Test Class Declaration

- Class must be **package-private** (no `public` modifier).
- No class-level annotation for plain POJO tests.
- `@ExtendWith(MockitoExtension.class)` must be present on Mockito-based tests.
- Bean Validation tests use a programmatic `Validator` bootstrap in `@BeforeEach` (no Mockito
  extension required).

```java
// Plain POJO test — no annotation
class TrackerAccountTest { ...
}

// Mockito test — single annotation
@ExtendWith(MockitoExtension.class)
class RouteServiceMainTest { ...
}

// Bean Validation test — programmatic Validator, no Mockito
class RouteSegmentsValidatorTest { ...
}
```

### Test Structure

#### Field Conventions

- **Constants**: `private static final` fields at the top of the class.
- **Test fixture objects**: declared as `private` instance fields.
- **Mock dependencies**: annotated with `@Mock`.
- **Class under test**: annotated with `@InjectMocks`, field named `underTest`.

```java
private static final String OWNER = "system";
private static final Long DEVICE_ID = 12345L;

@Mock
private RouteMapper routeMapper;

@Mock
private RouteRepository routeRepository;

@InjectMocks
private RouteServiceMain underTest;
```

#### Setup Method

A single `@BeforeEach void setUp()` method must create all test fixtures:

```java
@BeforeEach
void setUp() {
    testAccount = new TrackerAccount(OWNER, KEY, true);
    var device = new DeviceTelemetry();
    device.setId(DEVICE_ID);
    existingDevices = Set.of(device);
}
```

#### Test Method Naming

Test methods use `camelCase` describing the scenario:

| Pattern           | Examples                                                                               |
|-------------------|----------------------------------------------------------------------------------------|
| POJO/Record tests | `dtoShouldRetainValues`, `entityShouldRetainValues`                                    |
| Happy path        | `create`, `update`, `findAll`, `findById`, `save`, `validateKey`                       |
| Error/edge case   | `createWithIdShouldThrow`, `updateWithNonExistingRoute`, `validateKeyWithUnknownOwner` |
| Empty data        | `findAllWhenNoRoutesExist`, `findMostRecentRecordForEachDeviceWithEmptyData`           |
| Null safety       | `mapNullDTO`, `mapNullEntity`, `updateEntityWithNullDTO`                               |
| Validation        | `isValid`, `isValidWithInvalidFirstOrder`                                              |

### Mocking Standards

#### Annotations

- `@Mock` for all dependencies.
- `@InjectMocks` for the class under test.

```java

@Mock
private RouteMapper routeMapper;

@Mock
private RouteRepository routeRepository;

@InjectMocks
private RouteServiceMain underTest;
```

#### Stubbing Patterns

| Scenario              | Pattern                                                  |
|-----------------------|----------------------------------------------------------|
| Return value          | `when(mock.method(...)).thenReturn(value)`               |
| Return based on input | `when(mock.method(...)).thenAnswer(invocation -> ...)`   |
| Void method           | `doAnswer(invocation -> { ... }).when(mock).method(...)` |
| Argument matchers     | `any()`, `anySet()`, `any(RouteDTO.class)`               |
| Custom matching       | `argThat(arg -> condition)`                              |

#### Verification

```java
verify(repository, times(1)).

save(entity);

verify(mapper, never()).

map(any());
```

#### Private Field Injection

Use `ReflectionTestUtils.setField()` only when the class under test has `@Value`-injected fields
that would normally be set by Spring:

```java
ReflectionTestUtils.setField(underTest, "firstOwner",OWNER);
```

### Assertion Style

The project's tests currently use `org.junit.jupiter.api.Assertions.*` static imports for all
assertions. JUnit 5 assertions (`assertEquals`, `assertNotNull`, `assertThrows`, etc.) remain the
default and are preferred for consistency with existing test classes.

Other assertion libraries may be used in new tests at the developer's discretion. Notable options:

- **AssertJ** — fluent API with better diagnostics (e.g., `usingRecursiveComparison` for DTO
  comparison, collection extraction, no parameter-order ambiguity). Already available on the
  classpath via `spring-boot-starter-test`.
- **Hamcrest** — matcher-based, legacy. Not recommended.
- **Truth** — Google's assertion library. Not recommended.

| Assertion                                  | Usage                     |
|--------------------------------------------|---------------------------|
| `assertEquals(expected, actual)`           | Value equality            |
| `assertNotNull(actual)`                    | Non-null check            |
| `assertNull(actual)`                       | Null check                |
| `assertTrue(condition)`                    | Boolean condition         |
| `assertFalse(condition)`                   | Negated boolean condition |
| `assertThrows(ExceptionClass, executable)` | Exception assertion       |
| `assertDoesNotThrow(executable)`           | No-exception assertion    |

```java
assertNotNull(result);

assertEquals(ROUTE_ID, result.id());

assertThrows(EnduranceTrioException .class, () ->underTest.

create(invalidDTO));

verify(routeRepository, times(1)).

save(expectedEntity);
```

### Bean Validation Tests

Custom validation constraints must be tested with a programmatic `Validator` instance bootstrapped
via `jakarta.validation.Validation`. The class must be **package-private** and requires no
`@ExtendWith` annotation.

- The `Validator` field must be named `underTest`.
- Use a `try-with-resources` block in `@BeforeEach` to acquire and close the
  `ValidatorFactory` (it implements `AutoCloseable`).
- Test both valid and invalid inputs — assert the violation set size and individual violation
  messages.

```java
class RouteSegmentsValidatorTest {

  private Validator underTest;

  @BeforeEach
  void setUp() {
    try (var factory = Validation.buildDefaultValidatorFactory()) {
      underTest = factory.getValidator();
    }
  }

  @Test
  void isValid() {
    var violations = underTest.validate(validRoute);
    assertTrue(violations.isEmpty());
  }

  @Test
  void isValidWithInvalidFirstOrder() {
    Set<ConstraintViolation<RouteDTO>> result = underTest.validate(invalidRoute);
    assertEquals(1, result.size());
    assertEquals("Route must start with a segment of order 1",
        result.iterator().next().getMessage()
    );
  }
}
```

### Controller & Security Tests (Pure Unit)

The `endurancetrio-app` module tests controllers, security filters, security configuration,
and exception handlers using pure Mockito — no Spring context is loaded.

#### Controller Tests

Controller tests use `@ExtendWith(MockitoExtension.class)` with `@Mock` on the service interface
and `@InjectMocks` on the controller implementation. The controller methods are called directly;
`ResponseEntity<EnduranceTrioResponse<T>>` status codes and body structure are asserted.

```java

@ExtendWith(MockitoExtension.class)
class RouteRestControllerTest {

  @Mock
  private RouteService routeService;

  @InjectMocks
  private RouteRestController underTest;

  @Test
  void findAllShouldReturnList() {
    when(routeService.findAll()).thenReturn(List.of(testRoute));

    ResponseEntity<EnduranceTrioResponse<List<RouteDTO>>> response = underTest.findAll();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(200, response.getBody().status());
    assertEquals(1, response.getBody().data().size());
  }
}
```

#### Security Filter Tests

Security filter tests use `mock(HttpServletRequest.class)` for the request,
`MockHttpServletResponse` for the response, and `mock(FilterChain.class)` for the chain.
Various header combinations (valid, missing, malformed) are exercised.

```java

@ExtendWith(MockitoExtension.class)
class EnduranceTrioAuthFilterTest {

  @Mock
  private AuthenticationManager authManager;

  @InjectMocks
  private EnduranceTrioAuthFilter underTest;

  @BeforeEach
  void setUp() {
    request = mock(HttpServletRequest.class);
    response = new MockHttpServletResponse();
    filterChain = mock(FilterChain.class);
  }

  @Test
  void missingAuthHeaderShouldSkipAuth() {
    when(request.getHeader("Authorization")).thenReturn(null);
    when(request.getHeader("ET-Owner")).thenReturn(null);

    underTest.doFilterInternal(request, response, filterChain);

    verify(authManager, never()).authenticate(any());
    verify(filterChain).doFilter(request, response);
  }
}
```

#### Security Configuration Tests

Configuration tests directly instantiate the config class and use 
`mock(HttpSecurity.class, RETURNS_SELF)` for the fluent `HttpSecurity` API.

```java

@ExtendWith(MockitoExtension.class)
class AppSecurityConfigTest {

  @Mock
  private EnduranceTrioAuthProvider authProvider;

  @Test
  void corsConfigurationSourceShouldConfigureAllowedOrigins() {
    AppSecurityConfig config = new AppSecurityConfig("http://localhost:3000",
        authProvider, entryPoint
    );

    CorsConfigurationSource source = config.corsConfigurationSource();
    CorsConfiguration corsConfig = source.getCorsConfiguration(
        new MockHttpServletRequest("GET", "/api/test"));

    assertNotNull(corsConfig.getAllowedOrigins());
    assertTrue(corsConfig.getAllowedOrigins().contains("http://localhost:3000"));
  }

  @Test
  void securityFilterChainAPIShouldBuildSuccessfully() {
    AppSecurityConfig config = new AppSecurityConfig("*", authProvider, entryPoint);
    HttpSecurity http = mock(HttpSecurity.class, RETURNS_SELF);
    when(http.build()).thenReturn(mock(DefaultSecurityFilterChain.class));

    assertSame(chain, config.securityFilterChainAPI(http));
  }
}
```

#### Exception Handler Tests

Exception handler tests call `@ControllerAdvice` methods directly, asserting that each
`EnduranceTrioError` code maps to the correct HTTP status.

```java

@ExtendWith(MockitoExtension.class)
class EnduranceTrioExceptionHandlerAPITest {

  @InjectMocks
  private EnduranceTrioExceptionHandlerAPI underTest;

  @Test
  void handledExceptionShouldReturnMappedHttpStatus() {
    EnduranceTrioException exception = new EnduranceTrioException(
        new ErrorDTO(EnduranceTrioError.NOT_FOUND));

    ResponseEntity<Object> response = underTest.handledException(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}
```

### Test Configuration

The file `endurancetrio-app/src/test/resources/application-test.yaml` exists and configures H2
(PostgreSQL compatibility mode) with Flyway pointing to `h2` migration scripts. This configuration
is intended for future integration tests (`@DataJpaTest`, `@SpringBootTest`, etc.) but is **not
currently activated** by any test.

```yaml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:endurancetrio_tracker;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;COLLATION=und
    username: user
    password: password
  flyway:
    baseline-on-migrate: true
    baseline-version: 0
    enabled: true
    locations:
      - classpath:db/migration/ddl/h2
      - classpath:db/migration/dml/h2
    schemas: endurancetrio_tracker
```

### Flyway in Tests

Flyway migration scripts are **duplicated** for H2 alongside PostgreSQL:

- `src/main/resources/db/migration/ddl/h2/` — H2-specific DDL
- `src/main/resources/db/migration/dml/h2/` — H2-specific DML

When adding a new migration, the H2 variant must be added in the same version as the PostgreSQL
variant. The `application-test.yaml` configures Flyway to use these H2 scripts.

### Future: Spring Context Integration Tests

The sections above document the **current** pure-Mockito test patterns for the `endurancetrio-app`
module (controllers, security, exception handlers). The following starters are also declared in
`endurancetrio-app/pom.xml` and ready for use when Spring-context integration tests are added:

- `spring-boot-starter-webmvc-test` — `@WebMvcTest` + `MockMvc` for controller-layer tests
- `spring-boot-starter-security-test` — Security-aware test utilities (e.g., `@WithMockUser`,
  `RequestPostProcessor`)
- `spring-boot-starter-actuator-test` — Actuator endpoint testing

When writing integration tests that require a Spring context:

1. Use `@ActiveProfiles("test")` to activate the H2/Flyway configuration.
2. Prefer slice tests (`@WebMvcTest`, `@DataJpaTest`) over full `@SpringBootTest`.
3. Do **not** use `@MockBean` — keep Mockito-based service tests pure (no Spring context).
4. Add a `@TestMethodOrder(MethodOrderer.OrderAnnotation.class)` + `@Order(N)` only when test
   ordering is explicitly required.

## Programmatic Version Management

Community releases are versioned independently of any integrated repositories (such as the
[`endurancetrio-tracker`](https://github.com/EnduranceCode/endurancetrio-tracker) repository).
Merging integrated repositories into Community does not automatically warrant a Community version
bump. The version is only incremented when the impact of changes (integrations, Community-specific
features, fixes) justifies a new release.

### Version Update

The project provides the `set-version.sh` script to update the version across **both** the Maven
POM files and the frontend npm files (`package.json` and `package-lock.json`). Replace the
placeholder in the command below and execute it:

```shell
./set-version.sh {VERSION_NUMBER}
```

> **Placeholder Definition**
>
> + **{VERSION_NUMBER}** : The new Semantic Version number to be applied across all Maven modules
>   and the frontend `package.json` and `package-lock.json`. The `-SNAPSHOT` suffix (if present) is
>   automatically stripped for the npm files, since npm does not use Maven snapshot conventions.

The script also accepts an optional `--commit` flag to automatically create a git commit:

```shell
./set-version.sh {VERSION_NUMBER} --commit
```

#### Manual fallback

If you prefer to update the version manually (or need to revert changes), the Maven commands are
still available:

```shell
./mvnw versions:set -DnewVersion={VERSION_NUMBER}
```

To revert uncommitted changes made by `versions:set`:

```shell
./mvnw versions:revert
```

To commit the changes made by `versions:set` (removes the backup files):

```shell
./mvnw versions:commit
```

> **Note:** When using the manual Maven commands, remember to also update the `"version"` field
> in both `endurancetrio-app/src/main/resources/webpack/package.json` and
> `endurancetrio-app/src/main/resources/webpack/package-lock.json` to match.

### Version Tagging

All releases must be tagged using **annotated** tags (created with the `-a` flag) following
the pattern:

    community-vX.Y.Z

> ***X*** : Major version number
>
> ***Y*** : Minor version number
>
> ***Z*** : Patch version number

For example, version `1.0.0` is tagged as `community-v1.0.0`.

Create an annotated tag with a release message:

```shell
git tag -a community-vX.Y.Z -m "vX.Y.Z"
```

Push the tag to the remote repository:

```shell
git push origin community-vX.Y.Z
```

### Tag Rules

- Tags are immutable
- Tags represent released state only
- Tags are the ONLY safe merge anchors for integration merges
- Namespacing avoids tag collisions between integrated repositories (e.g., `tracker-vX.Y.Z`
  tags in the Tracker repo)

## Building Custom Images with Workflow Dispatch

The GitHub Actions workflow in `.github/workflows/publish-image.yml` builds and publishes Docker
images to `ghcr.io/endurancetrio/endurancetrio-community` from the **EnduranceTrio**
[organization repository](https://github.com/endurancetrio/endurancetrio-community).

This workflow authenticates to GHCR with two repository secrets:

- `GHCR_USERNAME`
- `GHCR_SECRET`

It supports two triggers:

| Trigger           | Image tag format        | Use case                         |
|-------------------|-------------------------|----------------------------------|
| Workflow dispatch | `sha-<commit>-<suffix>` | Staging builds, feature branches |
| Release           | `X.Y.Z` (e.g., `0.4.0`) | Production releases              |

### How to trigger a manual build for staging

1. Go to the **Actions** tab of the `endurancetrio/endurancetrio-community` repository on GitHub:
   `https://github.com/endurancetrio/endurancetrio-community/actions`

2. Select the **"Build and Publish Docker Image"** workflow from the left sidebar.

3. Click the **"Run workflow"** dropdown button.

4. Fill in the form:

   | Field          | Example value            | Description                                  |
   |----------------|--------------------------|----------------------------------------------|
   | **Branch**     | `feature/my-new-feature` | The branch, tag, or commit SHA to build from |
   | **Tag suffix** | `stg-my-new-feature`     | A short identifier appended to the image tag |

5. Click **"Run workflow"**.

The workflow checks out the specified branch, builds the application with Maven, builds the
Docker image, and pushes it to GHCR.

The resulting image tag will be:

```text
ghcr.io/endurancetrio/endurancetrio-community:sha-<7-char-commit-hash>-stg-my-new-feature
```

### Deploying the built image

Update the `.env` file on the server:

```shell
STG_VERSION=sha-abc1234-stg-my-new-feature
```

Then redeploy the staging container:

```shell
docker compose -p endurancetrio-community up -d stg-endurancetrio-community
```

### Package Visibility

Container images published to GHCR from forked repositories default to **private** visibility,
even when the source repository is public. This is a known GHCR behavior for forks.

To make a newly published image accessible, the package's visibility must be changed to **public**
via the GitHub web UI. This is a **one-time** operation per package - once set, all future versions
pushed under the same package name inherit the public visibility.

**Procedure:**

1. Navigate to the organization's packages page:
   `https://github.com/orgs/endurancetrio/packages?repo_name=endurancetrio-community`

2. Click on the **`endurancetrio-community`** container package.

3. Click **Package settings** in the right sidebar.

4. In the **Danger Zone** section, click **Change visibility**, select **Public**, confirm
   by typing the package name, then click **"I understand the consequences, change package
   visibility"**.

> **Warning:** Once a package is made public, it cannot be reverted to private.

> **Note:** There is currently no API endpoint to change GHCR package visibility programmatically
> ([GitHub Community Discussion #22009][ghcr-visibility-discussion]). > The web UI is the only
> method available.

[ghcr-visibility-discussion]: https://github.com/orgs/community/discussions/22009

### GHCR credentials setup and renewal

#### First-time setup

1. Sign in to GitHub with an account that has permission to publish packages to the
   **EnduranceTrio** organization namespace.
2. Open **Settings -> Developer settings -> Personal access tokens -> Fine-grained tokens**.
3. Create a token with these settings:
   - **Resource owner:** the account creating the token
   - **Repository access:** `Only select repositories`
   - **Selected repository:** `endurancetrio/endurancetrio-community`
   - **Repository permissions:** `Packages: Read and write`
4. Set an expiration date according to your security policy.
5. Generate the token and copy it immediately (GitHub does not show it again).

Then configure repository secrets in `endurancetrio/endurancetrio-community`:

1. Open **Settings -> Secrets and variables -> Actions -> Repository secrets**.
2. Create/update `GHCR_USERNAME` with the GitHub username of the account that owns the token.
3. Create/update `GHCR_SECRET` with the generated token value.

#### Renewal procedure (token expiration/rotation)

When the token expires (or must be rotated):

1. Sign in to the account that owns the currently configured token.
2. Open **Settings -> Developer settings -> Personal access tokens -> Fine-grained tokens**.
3. Regenerate the existing token or create a replacement with the same permissions.
4. In `endurancetrio/endurancetrio-community`, update the `GHCR_SECRET` repository secret.
5. Keep `GHCR_USERNAME` aligned with the token owner username.
6. Trigger the workflow manually to validate credentials.

#### Quick validation

After setting or rotating secrets, run a manual workflow dispatch and confirm these steps pass:

- `Log in to GHCR`
- `Build and push Docker image`

If login fails with `unauthorized`/`denied`, verify token scope and organization membership.

For broader deployment validation (health checks, logs, rollback flow), follow
[`docs/deployment.md`](./deployment.md).

## SonarQube Cloud Configuration

The **EnduranceTrio** project uses [SonarQube Cloud](https://sonarcloud.io/) for continuous code quality
inspection. The repository is linked to SonarQube Cloud via the GitHub App integration, which
automatically decorates pull requests with analysis results.

Two analysis methods are available, configured by separate files:

| Method                      | Config file                | Used by                                                             |
|-----------------------------|----------------------------|---------------------------------------------------------------------|
| **Automatic Analysis**      | `.sonarcloud.properties`   | SonarCloud's built-in scanner (push/PR on default branch)           |
| **CLI / CI-based analysis** | `sonar-project.properties` | `mvn verify sonar:sonar` locally, or future GitHub Actions workflow |

Both files are kept aligned and should always define the same exclusions.

### Repository Configuration

The following configuration is in place for the project:

1. **`pom.xml`** — the `<sonar.organization>` property is set in the root POM to identify the
   SonarQube Cloud organization (`endurancecode`). This property is required by the
   sonar-maven-plugin for CLI/CI analysis.
2. **`.sonarcloud.properties`** — defines exclusions for **Automatic Analysis**:
   - `sonar.exclusions` — excludes SQL migration scripts from analysis
   - `sonar.cpd.exclusions` — excludes SQL migration scripts from duplication detection
3. **`sonar-project.properties`** — defines the project key, host URL, and all exclusions for
   **CLI/CI analysis**:
   - `sonar.exclusions` — excludes SQL migration scripts from analysis
   - `sonar.cpd.exclusions` — excludes SQL migration scripts from duplication detection
   - `sonar.coverage.exclusions` — excludes boilerplate code (`**/model/entity/**`,
     `**/model/enumerator/**`, `**/config/**`) from coverage metrics
   - `sonar.coverage.jacoco.xmlReportPaths` — path to the JaCoCo XML report

### Manual Analysis (CLI)

A manual analysis can be triggered from the command line. Since it uses the same sonar-maven-plugin
as the GitHub Actions workflow, it conflicts with Automatic Analysis.

1. Generate a SonarQube Cloud token at **[Account → Security](https://sonarcloud.io/account/security)**
   with the **Analyze Projects** permission.
2. Set it as an environment variable:

   ```shell
   export SONAR_TOKEN=your_sonarcloud_token
   ```

3. **Disable Automatic Analysis** in the SonarQube Cloud project settings
   (**Project Settings → Administration → Analysis Method → "Other CI"**). If Automatic Analysis is
   enabled, the scanner will fail with a conflict error.

4. Run the analysis from the repository root:

   ```shell
   ./mvnw verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=endurancecode_endurancetrio-community
   ```

   This will:
   - Build the project and run all tests (generating the JaCoCo coverage report)
   - Execute the SonarScanner, which reads `sonar-project.properties` from the repository root
   - Send the analysis results (including coverage) to SonarQube Cloud
   - Apply all configured exclusions (CPD, analysis, and coverage)

5. After the manual analysis completes, **re-enable Automatic Analysis** from the same settings page
   if you want to resume automatic scans on push.

Results are available in the
[SonarQube Cloud dashboard](https://sonarcloud.io/project/overview?id=endurancecode_endurancetrio-community).
