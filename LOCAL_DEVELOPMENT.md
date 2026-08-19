# Local development with Neon PostgreSQL

Student OS reads its production-style PostgreSQL configuration from `DATABASE_URL`. For local development, use a personal `.env` file that remains outside version control. Neon provides a complete connection string from the project dashboard’s **Connect** panel; copy that string unchanged, including its SSL options. Neon requires encrypted database connections. [1]

| File | Purpose | Version-control status |
|---|---|---|
| `.env.example` | Safe placeholder template for the expected variables. | Tracked |
| `.env` | Your real Neon connection string and optional local overrides. | Ignored |
| `render.yaml` | Render service configuration. Set its `DATABASE_URL` separately in Render. | Tracked |

## Configure the local secret

Copy the example file, then replace the placeholder value with the **complete pooled connection string** from Neon. Do not quote or URL-decode parts of the connection string, and do not commit this file.

```sh
cp .env.example .env
```

Your `.env` should follow this shape:

```dotenv
DATABASE_URL=postgresql://<role>:<password>@<endpoint-pooler>.<region>.aws.neon.tech/<database>?sslmode=require&channel_binding=require
DB_POOL_SIZE=5
```

## Run locally with Docker

Docker’s `--env-file` option loads the ignored local file into the container. The service listens on port `10000` in this command, and is exposed at `http://localhost:8080`.

```sh
docker build -t student-os .
docker run --rm --env-file .env -p 8080:10000 student-os
```

Open `http://localhost:8080/auth/login` after the container starts. A deliberately invalid sign-in can confirm the query path without creating a user.

## Run with a local Tomcat installation

Export variables from `.env` only in the current shell, then deploy the generated WAR to a Jakarta-compatible Tomcat 11 installation. Do not place the connection string in source code, `pom.xml`, or Tomcat configuration committed to Git.

```sh
set -a
. ./.env
set +a
mvn clean package
cp target/student-os.war "$CATALINA_HOME/webapps/ROOT.war"
"$CATALINA_HOME/bin/catalina.sh" run
```

## Troubleshooting

| Symptom | Likely cause | Resolution |
|---|---|---|
| `Unable to initialize the PostgreSQL connection pool` | Missing or malformed `DATABASE_URL`. | Re-copy the full Neon connection string from the Connect panel. |
| SSL or certificate error | SSL options were removed from the URL. | Restore `sslmode=require` and any other query parameters that Neon provides. |
| Application loads but sign-in fails | The database is reachable but the credentials are not an existing app user. | Use a known account or complete registration; a rejected unknown login is expected. |
| Connection saturation | Too many concurrent connections for the Neon compute. | Use the pooled Neon URL and keep `DB_POOL_SIZE` modest. |

## References

[1] [Neon — Connect from any application](https://neon.com/docs/connect/connect-from-any-app)
