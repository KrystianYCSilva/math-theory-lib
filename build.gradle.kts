plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
}

allprojects {
    group = "com.mathsets"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    
    detekt {
        buildUponDefaultConfig = true
        allRules = false
        // config.setFrom(rootProject.files("config/detekt/detekt.yml")) // To be created later
    }
}
