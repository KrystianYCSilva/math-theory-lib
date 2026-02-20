---
description: |
  Deployment workflow and procedures.
  Use when: preparing for a release or deploying the application.
---

# Deployment Workflow

## 1. Prerequisites
Before any release or publication, ensure all tests across all platforms (JVM, JS, Native) pass. Run `./gradlew check`.
Additionally, ensure all code conforms to the code style (Detekt).

## 2. API Documentation
Run `./gradlew dokkaHtml` to generate the HTML documentation for the library. This should be deployed alongside the release, ideally on GitHub Pages or a similar static host.

## 3. Maven Publishing (KMP)
Since `mathsets-kt` is a Kotlin Multiplatform library, it relies on the `maven-publish` plugin to distribute artifacts. 
To publish to a local Maven repository for testing:
*   `./gradlew publishToMavenLocal`

For actual releases (e.g., to Maven Central or a private repository):
*   Ensure the version in `build.gradle.kts` / `libs.versions.toml` is correctly bumped (Semantic Versioning).
*   Set up necessary signing keys (`SIGNING_KEY_ID`, `SIGNING_PASSWORD`) and publishing credentials (`OSSRH_USERNAME`, `OSSRH_PASSWORD`) in the environment or `local.properties`.
*   Run the publication task (e.g., `./gradlew publishAllPublicationsToSonatypeRepository`).

## 4. Release Notes
Every version must be documented in a `CHANGELOG.md` or as GitHub Releases, detailing:
*   New mathematical modules or constructions added.
*   Performance improvements or refactorings in the Kernel.
*   Bug fixes and breaking API changes.