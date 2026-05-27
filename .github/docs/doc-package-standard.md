# Doc Package Standard — docql

## Overview

A **doc package** is the unit of documentation in docql. It is a versioned, structured bundle of files — owned by one product team, published as a whole, and queryable by AI agents and human developers through the docql API.

This document defines what a conformant doc package looks like: its structure, required files, supported formats, versioning rules, and the metadata it must carry.

---

## Conceptual Model

Think of a doc package as the team's living product manual — not a user guide for end customers, but a technical brief for any developer or AI agent that needs to understand, integrate with, or reason about the team's product.

A package always answers:
- **What is this product?** (purpose, domain, key concepts)
- **What can it do?** (features, capabilities, APIs)
- **How does it interact with others?** (integration points, contracts, dependencies)
- **What is the current version?** (what changed, what is stable, what is deprecated)

---

## Package Metadata

Every package is identified by the following fields. These are set at publish time and are immutable for the lifetime of that version.

| Field | Type | Required | Description |
|---|---|---|---|
| `team` | string | ✅ | The owning team identifier. Slug-style. e.g. `payments`, `identity`, `data-platform` |
| `product` | string | ✅ | The product or service identifier. Slug-style. e.g. `payment-gateway`, `auth-service` |
| `version` | string | ✅ | SemVer string. e.g. `1.3.0`, `2.0.0-rc1`. Dates are allowed but SemVer is preferred. |
| `tags` | list of strings | ❌ | Free-form labels for filtering. e.g. `["java", "kafka", "public-api"]` |
| `indexFilePath` | string | ✅ | Relative path to the index file within the package. e.g. `index.md` |
| `files` | list of DocFile | ✅ | The actual documentation files. At least one required. |

---

## Required File: `index.md`

Every doc package must include an index file. By convention this is `index.md` at the root of the package, referenced by the `indexFilePath` field.

The index file is **the entry point** for any AI agent or human reading the package. It must:
1. Describe the product in 1–3 sentences (suitable for embedding in an AI prompt).
2. State the current version.
3. List all detail topics as a table of contents with links to detail files.

### Template

```markdown
# {Product Name}

> {One-sentence description of the product — what it is, what problem it solves.}

**Team**: {team-slug}  
**Version**: {semver}  
**Tags**: {comma-separated tags}

---

## What This Product Does

{2–5 sentence description. Write this for an AI agent that has never encountered this product before.
Include the core domain, the main responsibilities, and any non-obvious design decisions.}

---

## Table of Contents

| Topic | File | Description |
|---|---|---|
| Overview | [overview.md](overview.md) | Domain model, key concepts, glossary |
| API Reference | [api.md](api.md) | REST endpoints, request/response schemas |
| Events | [events.md](events.md) | Kafka/message topics produced and consumed |
| Integration Guide | [integration.md](integration.md) | How to integrate with this service |
| Configuration | [configuration.md](configuration.md) | Config properties and environment variables |
| Changelog | [changelog.md](changelog.md) | What changed in this version |
| Known Limitations | [limitations.md](limitations.md) | Edge cases, known issues, deprecations |

---

## Quick Facts

| Property | Value |
|---|---|
| Primary language | Java 21 |
| Communication protocol | REST / Kafka |
| Main dependencies | {list key upstream services} |
| Owned by | {team name} |
| Slack / contact | {optional} |
```

---

## Detail Pages

Detail pages are regular Markdown files referenced from the index. They are free-form but should follow these guidelines:

- **Target audience is a machine first.** Write clearly and precisely. Avoid colloquialisms or ambiguous pronouns.
- **Include structured data where possible.** Tables, code blocks, and YAML frontmatter are all machine-parseable.
- **Avoid duplication.** Each fact should live in exactly one file and be linked from the index.
- **Use descriptive headings.** AI agents navigate by heading, not by page order.

### Recommended Detail Files

While the exact files depend on the product, these are the most valuable for AI agent consumption:

| File | Purpose |
|---|---|
| `overview.md` | Domain model, key entities, glossary of terms |
| `api.md` or `openapi.yaml` | REST API contract (OpenAPI preferred for machine parsing) |
| `events.md` | Async messaging: topics, schemas, producer/consumer roles |
| `integration.md` | How external teams or systems should integrate |
| `configuration.md` | All config properties, environment variables, defaults |
| `changelog.md` | Version-specific summary of changes |
| `limitations.md` | Known issues, deprecated paths, breaking changes |
| `adr/` | Architecture Decision Records — why decisions were made |

---

## Supported Formats

| Format | Extension | Notes |
|---|---|---|
| Markdown | `.md` | Primary format. Rendered and indexed as-is. |
| OpenAPI / Swagger | `.yaml`, `.json` | Preferred for API contracts. Parseable by AI agents and tooling. |
| YAML | `.yaml`, `.yml` | Config schemas, event schemas, structured data. |
| HTML | `.html` | Supported but discouraged — harder for AI agents to parse meaningfully. |
| Javadoc | `/javadoc/**` | Supported as supplemental files. Not full-text indexed by default. |

**Recommendation**: Prefer Markdown and OpenAPI. Avoid generated HTML where a structured alternative exists.

---

## Versioning Rules

- **SemVer is required for production packages**: `MAJOR.MINOR.PATCH` with optional pre-release suffix.
- `PATCH` bump: non-breaking documentation corrections, typo fixes, added detail.
- `MINOR` bump: new features, new endpoints, new files in the package.
- `MAJOR` bump: breaking changes to APIs, contracts, or domain model.
- Multiple versions of the same `team/product` can coexist in the registry. Clients can request a specific version or resolve to `latest`.
- Pre-release versions (e.g., `2.0.0-rc1`) are valid and queryable by exact version. They are not returned by `/latest`.

---

## Pushing a Package

### Manual Upload (REST API)

```http
POST /packages
Content-Type: application/json

{
  "team": "payments",
  "product": "payment-gateway",
  "version": "1.3.0",
  "tags": ["java", "rest", "public-api"],
  "indexFilePath": "index.md",
  "files": [
    {
      "path": "index.md",
      "title": "Payment Gateway",
      "content": "# Payment Gateway\n..."
    },
    {
      "path": "api.md",
      "title": "API Reference",
      "content": "# API Reference\n..."
    }
  ]
}
```

### CI/CD Pipeline (Recommended)

The recommended approach is to automate publishing as the final step of a team's release pipeline. The general pattern is:

1. Team merges a change that bumps their doc package version.
2. CI pipeline runs, builds/generates documentation.
3. A pipeline step calls `POST /packages` with the assembled payload.
4. docql stores, indexes, and makes the new version immediately queryable.

A `docql-client` CLI/plugin is planned to simplify step 3.

### What to Push

Push the contents of your repository's documentation folder — typically `.github/docs/` or `/docs/` — plus any generated assets (OpenAPI spec, Javadoc). The folder structure maps directly to the `files[].path` field in the request.

---

## Example Package Layout

```
.github/docs/
├── index.md              ← required, must be set as indexFilePath
├── overview.md
├── api.md
├── events.md
├── integration.md
├── configuration.md
├── changelog.md
├── limitations.md
└── adr/
    ├── 001-use-kafka.md
    └── 002-auth-strategy.md
```

This folder pushed as a package with `indexFilePath: "index.md"` is a fully conformant doc package.

