Dokka Integration (Gradle Kotlin DSL)

Apply Dokka plugin and configure a documentation task for Kotlin Multiplatform projects.

plugins {
    id("org.jetbrains.dokka") version "1.8.20" // adjust version as needed
}

kotlin {
    // targets...
}

tasks.register("dokkaHtmlMultiModule") {
    // Dokka tasks are configured per-platform; use dokkaHtml or dokkaHtmlMultiModule depending on setup
}

Example (build.gradle.kts snippet):

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka") version "1.8.20"
}

tasks.dokkaHtml {
    outputDirectory.set(buildDir.resolve("dokka"))
}

CI tip: run `gradlew dokkaHtml` or include it in the `check` lifecycle to publish docs as part of your pipeline.