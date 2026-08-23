# Render Credential Management for StudentOS

## Purpose

This guide defines how to provide production configuration to the StudentOS Docker service on Render without putting credentials in Git, the Docker image, release artifacts, build logs, or local documentation.

StudentOS reads `DATABASE_URL` at runtime. The tracked `render.yaml` intentionally declares that key with `sync: false`, which leaves the value to be supplied in the Render Dashboard rather than source control.

## Configuration Boundary

| Configuration type | Correct location | Never place it in |
| --- | --- | --- |
| Production `DATABASE_URL` | Render service or production-scoped environment group | Git, `render.yaml`, Dockerfile, image labels, release notes, or browser screenshots |
| Production email/API credentials | Render service or production-scoped environment group | Source code, client-side JavaScript, Docker build arguments, or `.env.example` |
| Non-secret runtime tuning such as `DB_POOL_SIZE` | `render.yaml` or the Render service configuration | A secret store is unnecessary unless the value becomes sensitive |
| Local test connection values | Ignored local `.env` file | Any branch, commit, issue, pull request, or shared archive |

> **Core rule:** a container can connect to whatever database its runtime environment specifies. Treat the production database URL as a production password, not as ordinary configuration.

## Safe Render Setup

1. In the Render Dashboard, open the StudentOS web service and select **Environment**.
2. Add or replace `DATABASE_URL` there. Keep the value out of `render.yaml`; the existing `sync: false` declaration is the correct source-controlled placeholder.
3. Keep the service on the existing Docker runtime. Do not add a Dockerfile `ARG` or `ENV` instruction for any credential, and never copy an `.env` file into the image. Render documents that Docker-service environment variables can also be available as build arguments, so consuming secret build arguments can place sensitive material in an image layer.[1] [2]
4. Save and deploy, then validate only non-sensitive evidence: service health, application availability, and the absence of connection errors. Do not print environment variables or connection strings to logs.
5. Grant Render workspace access on a least-privilege basis. Use a protected production environment and an environment-scoped group when your Render plan/workspace setup supports them, so staging cannot accidentally reuse production values.[1] [3]

## Database-Credential Practices

Use a dedicated application database role rather than a personal owner credential. Give it only the permissions StudentOS requires, keep the production database separate from local and test databases, and require encrypted connections as supplied by the managed database provider.

When a credential must be replaced, first create the new provider credential, add the replacement in Render, deploy, and verify the service. Only then revoke the old credential. This sequence avoids an avoidable outage while ensuring the previous credential stops working afterward.

If a credential is suspected to have appeared in Git, a build log, or a screenshot, treat it as exposed: rotate or revoke it immediately, then remove it from the affected location and review the repository history. Removing a value from the current file alone does not invalidate an already copied credential.

## Docker Image Guardrails

StudentOS already uses a strict `.dockerignore` allowlist for the published image. Maintain these rules:

- Do not add `.env`, credential files, private configuration folders, database exports, or local archives to the image build context.
- Do not use `ARG` or `ENV` for production secrets in the Dockerfile.
- Do not run diagnostic commands that echo secrets during an image build or service startup.
- Use a versioned image tag such as `v1.0.0-mkvteam` for controlled review. The image still requires each operator to provide their own runtime configuration.

## Routine Checklist

| Before deployment | After deployment | During an incident |
| --- | --- | --- |
| Confirm Git contains no local environment or key files. | Confirm the service health path and expected user flows work. | Rotate the exposed value at its provider first. |
| Confirm production values exist only in Render’s protected configuration. | Check logs for generic errors only; never paste secret-bearing logs into issues. | Replace the Render value, deploy, verify, then revoke the old value. |
| Confirm staging/test does not point at production data. | Review who can view or change production settings. | Remove any accidental repository/log exposure and review commit history. |

## References

[1] [Render — Environment Variables and Secrets](https://render.com/docs/configure-environment-variables)

[2] [Render — Using Secrets with Docker](https://render.com/docs/docker-secrets)

[3] [Render — Projects and Environments](https://render.com/docs/projects)
