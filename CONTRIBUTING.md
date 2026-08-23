# Contributing to StudentOS

StudentOS welcomes focused, verifiable improvements from the project team and approved collaborators. Contributions should improve the application, its tests, its accessibility, or its public documentation without exposing secrets or misrepresenting authorship.

## Contribution Principles

1. Use **your own GitHub account** and make commits that reflect work you actually performed.
2. Keep changes small enough to review and explain.
3. Do not commit passwords, reset codes, database URLs, API keys, personal account data, or private screenshots.
4. Do not create or delete production users, roles, reports, or content for a test without owner approval and a documented cleanup plan.
5. Record real testing results honestly. If an issue was not tested, say so.

## Recommended Workflow

```text
Create branch → make one focused change → test it → commit from your account → open pull request → review → merge
```

### Branch naming

Use a short branch name that explains the work.

```text
feature/profile-improvement
fix/mobile-navigation
docs/mobile-review
test/student-workflow
```

### Commit messages

Use clear messages that describe the real change.

```text
feat: add profile link validation
fix: improve mobile navigation contrast
docs: clarify local setup steps
test: record mobile usability review
```

## Pull Request Checklist

Before opening a pull request, confirm the following:

- [ ] The change is my own work or accurately credits all collaborators.
- [ ] No secret, credential, reset code, or sensitive personal data is included.
- [ ] Relevant Java tests and packaging pass for code changes.
- [ ] Documentation reflects only verified behavior.
- [ ] The pull request description explains what changed, how it was tested, and any remaining limitation.

## Code and Documentation Style

- Java code should follow standard Java conventions and preserve controller, DAO, model, and view separation.
- Database queries must remain parameterized and must preserve ownership and role validation.
- JSP pages should use the shared StudentOS visual system and remain readable in both light and dark modes.
- Responsive changes should preserve keyboard focus, clear labels, and usable mobile navigation.
- Markdown should be concise, factual, and safe for the repository’s intended audience.

## Planned Team Review Areas

The following are planned areas of collaboration, not a claim of completed work:

| Team member | Planned review area |
| --- | --- |
| Khon Sokkheng | Quality assurance and user-acceptance testing |
| Nhouv Vanne | Mobile usability and documentation review |

## Security Concerns

Do not open a public issue containing a security weakness, personal account information, or infrastructure secret. Follow [SECURITY.md](SECURITY.md) instead.
