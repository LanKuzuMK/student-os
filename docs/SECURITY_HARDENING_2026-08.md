# StudentOS Security Hardening Record — August 2026

## Scope

This record covers non-disruptive supply-chain and container-runtime hardening applied after the `v1.0.0-mkvteam` release. It does not disclose credentials, production configuration, user data, or internal diagnostic values. No Render secret or database record was changed.

## Applied Controls

| Area | Safeguard | Purpose |
| --- | --- | --- |
| Container runtime | The Tomcat process runs as a dedicated non-root `studentos` user. | Limits the impact of an application-level compromise inside the container. |
| Startup configuration | The entrypoint uses a restrictive file-creation mask and rejects invalid TCP port ranges. | Reduces permissive runtime files and avoids starting with an invalid listener configuration. |
| GitHub Actions | Every external action reference is pinned to a verified full commit SHA. | Avoids mutable action tags in the privileged image-publication path. |
| Package publication | Container publishing runs only for a published GitHub Release. | Prevents an ordinary branch push or ad hoc workflow dispatch from changing the public package. |
| Image transparency | The release build generates provenance and an SBOM. | Gives consumers metadata about how the registry image was built and what it contains. |
| Container verification | A separate GitHub Actions workflow builds the Docker image on main-branch changes and pull requests without pushing it. | Detects Dockerfile build regressions before a future release is eligible to publish an image. |
| Continuous review | Dependabot reviews Maven, Docker, and GitHub Actions dependencies weekly; vulnerability alerts and automated security-fix pull requests are enabled in GitHub. | Surfaces available dependency updates and available security patches for human review. |
| Runtime dependency patch | PostgreSQL JDBC was upgraded from `42.7.2` to the patched `42.7.13` release after Dependabot reported the affected version range. | Removes the reported SCRAM authentication CPU-exhaustion exposure in the application’s PostgreSQL driver. |
| Static analysis | CodeQL analyzes Java and workflow configuration on main-branch changes, pull requests, and a weekly schedule. | Adds automated detection for supported code and workflow risks. |

## Operator Controls That Remain Essential

Repository controls cannot replace safe operation. Production database URLs, email-provider credentials, and other secrets must remain only in Render runtime configuration. Use least-privilege access for both the database role and Render workspace members; rotate a credential immediately if it could have appeared in a log, commit, screenshot, or issue.

The image is intentionally public and reproducible, but it does not include a database, production accounts, or production environment variables. An operator who runs it must supply an independently controlled local or test configuration.

## Verification Plan

The repository source test suite continues to be the required regression check. The new CodeQL workflow provides automated security analysis; the revised image publication configuration will produce the hardened runtime image at the next published version tag. Before using that next image broadly, verify the successful workflow, inspect the published provenance/SBOM, and run the normal authenticated application smoke tests against a non-production environment.

## References

[1] [GitHub — Secure Use Reference](https://docs.github.com/en/actions/reference/security/secure-use)

[2] [GitHub — Artifact Attestations for Container Images](https://docs.github.com/actions/security-for-github-actions/using-artifact-attestations/using-artifact-attestations-to-establish-provenance-for-builds)

[3] [Docker — SBOM and Provenance Attestations with GitHub Actions](https://docs.docker.com/build/ci/github-actions/attestations/)
