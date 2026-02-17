---
name: security
description: |
  Documentation file for agent operations and skill usage.
  Use when: you need procedural guidance for agent execution and context management.
---

# Security Review Checklist (OWASP)

## Injection
-   [ ] Are SQL queries parameterized? (No string concatenation).
-   [ ] Is user input sanitized before rendering (XSS)?

## Sensitive Data
-   [ ] Are secrets (API keys, passwords) hardcoded? *Fix*: Use env vars.
-   [ ] Is PII (Personal Identifiable Information) logged? *Fix*: Mask data.

## Authentication/Authorization
-   [ ] Is access control performed on the server side? (Client-side checks are bypassable).
-   [ ] Are direct object references (ID=123) checked for ownership? (IDOR).

## Dependencies
-   [ ] Are libraries outdated or have known vulnerabilities? (CVEs).

