# Vision — docql

## The Problem

Modern software organisations produce enormous amounts of product knowledge — API contracts, architecture decisions, onboarding guides, integration patterns, known limitations. Today that knowledge lives in human-centric wikis, slide decks, Confluence pages, and tribal memory. It was designed to be read by people, not queried by machines.

AI coding assistants and enterprise AI subscriptions have become central to how developers work. But these agents are only as good as the context they have access to. Without structured, queryable, up-to-date product documentation, AI agents are forced to guess, hallucinate, or fall back to generic knowledge. The alternative — training a single monolithic model on everything a company knows — is expensive, slow to update, and creates a single point of failure.

## The Solution

**docql is a distributed, AI-first documentation registry.**

The core idea is simple but powerful:

- Each team owns and publishes a versioned **doc package** — a structured bundle of documentation files describing their product.
- docql stores, indexes, and exposes those packages through a REST API.
- AI agents query docql at runtime to retrieve the exact context they need: "what does team X's service Y do?", "what is the API contract for version Z?", "what changed between 1.2.0 and 1.3.0?".

Documentation is **distributed** (each team maintains their own) and **shared** (any agent or human across the organisation can read it). There is no central team responsible for knowing everything.

## Target Users

### Primary Consumers — AI Agents
The primary consumer of docql is a machine. IDE-embedded agents, chat assistants, CI bots, and autonomous developer tools query docql to:
- Understand a product's purpose, domain model, and public API.
- Resolve ambiguity between what two teams expect at their integration boundary.
- Retrieve the latest spec without relying on outdated training data.
- Feed context into a prompt before generating code, tests, or documentation.

### Secondary Consumers — Human Developers
Human developers benefit from the same registry. They can browse what other teams have published, check the canonical definition of a contract, or read docs as part of onboarding. The API and eventual UI serve both consumers, but the data model and retrieval design are optimised for machine ingestion.

### Publishers — Product Teams
Any team responsible for a product is a publisher. They push a doc package through their CI/CD pipeline (or manually) whenever their documentation changes. The act of publishing is lightweight: push a folder, tag it with team, product, and version, and docql does the rest.

## The Long-Term Ambition

The enterprise use case is the first step. The broader ambition is to define **an open standard** for machine-readable product documentation that any library, open-source project, or enterprise team in the world can adopt.

Just as `package.json` tells the Node.js ecosystem about a library's dependencies, a conformant docql package tells any AI agent what a library or service does, what its API looks like, and what its current version says. IDE-embedded agents become permanently up to date on any library they encounter — not because they were trained on it, but because they can query it live.

## Differentiator

Existing documentation tools (wikis, knowledge bases, internal portals) share a common assumption: documentation is read by humans. Their search is keyword-based, their structure is hierarchical for navigation, and their rendering is optimised for a browser.

docql makes a different assumption: **documentation is consumed by machines**.

| Dimension | Human-centric tools | docql |
|---|---|---|
| Primary consumer | Human reader | AI agent |
| Update cycle | Manual, irregular | CI/CD-driven, versioned |
| Search | Keyword / navigation | Full-text + structured (team, product, version, tag) |
| Ownership model | Central team or wiki admins | Each team owns their own docs |
| Versioning | Usually none | SemVer per doc package |
| Format | Rich text / HTML | Markdown + OpenAPI + structured formats |
| Integration | Manual copy-paste into prompts | REST API queried at agent runtime |
| Cost model | Expensive centralised training | Lightweight retrieval at query time |

## Success Criteria

A successful adoption means:

1. Teams can push doc packages autonomously through their existing CI/CD without a central gatekeeper.
2. AI agents can retrieve the right context for a given team/product/version in a single API call.
3. The documentation stays current because pushing it is part of the normal release process.
4. Engineers report that AI suggestions improve in quality and accuracy for domain-specific questions.
5. The organisation can demonstrate a measurable reduction in context-switching, onboarding time, and AI hallucination rate for domain-specific queries.

