#!/bin/bash

# Script to initialize a new Kotlin Multiplatform library module.

set -e

MODULE_NAME=$1

if [ -z "$MODULE_NAME" ]; then
  echo "Error: Module name not provided."
  echo "Usage: $0 <your-module-name>"
  exit 1
fi

echo "Creating module: $MODULE_NAME"

# Create source set directories
mkdir -p "$MODULE_NAME/src/commonMain/kotlin"
mkdir -p "$MODULE_NAME/src/commonTest/kotlin"
mkdir -p "$MODULE_NAME/src/jvmMain/kotlin"
mkdir -p "$MODULE_NAME/src/jvmTest/kotlin"
mkdir -p "$MODULE_NAME/src/iosMain/kotlin"
mkdir -p "$MODULE_NAME/src/iosTest/kotlin"
mkdir -p "$MODULE_NAME/src/jsMain/kotlin"
mkdir -p "$MODULE_NAME/src/jsTest/kotlin"

# Create a minimal build.gradle.kts file
cat > "$MODULE_NAME/build.gradle.kts" <<EOL
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    // id("com.android.library") // Uncomment if Android target is needed
}

kotlin {
    // Enforce a strict, explicit API surface.
    // Use explicitApiWarning() during early development.
    explicitApi()

    // Target for JVM
    jvm {
        withJava()
        jvmToolchain(8) // Ensure Java 8 compatibility for legacy systems
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    // Targets for iOS
    val iosX64 by creating { binaries.framework { baseName = "$MODULE_NAME" } }
    val iosArm64 by creating { binaries.framework { baseName = "$MODULE_NAME" } }
    val iosSimulatorArm64 by creating { binaries.framework { baseName = "$MODULE_NAME" } }

    // Target for JavaScript
    js(IR) {
        browser()
        nodejs()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Define common dependencies here
                // implementation("io.insert-koin:koin-core:...")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64.compilations["main"].defaultSourceSet.dependsOn(this)
            iosArm64.compilations["main"].defaultSourceSet.dependsOn(this)
            iosSimulatorArm64.compilations["main"].defaultSourceSet.dependsOn(this)
        }
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64.compilations["test"].defaultSourceSet.dependsOn(this)
            iosArm64.compilations["test"].defaultSourceSet.dependsOn(this)
            iosSimulatorArm64.compilations["test"].defaultSourceSet.dependsOn(this)
        }
        val jsMain by getting
        val jsTest by getting
    }
}

// android { // Uncomment if Android target is needed
//     namespace = "com.example.$MODULE_NAME"
//     compileSdk = 34
//     defaultConfig {
//         minSdk = 21
//     }
//     compileOptions {
//         sourceCompatibility = JavaVersion.VERSION_1_8
//         targetCompatibility = JavaVersion.VERSION_1_8
//     }
// }

println("Module '$MODULE_NAME' created successfully.")
EOL

echo "Done."
echo "Next steps:"
echo "1. Add '$MODULE_NAME' to your settings.gradle.kts file: include("$MODULE_NAME")"
echo "2. Sync your Gradle project."
