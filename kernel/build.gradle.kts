plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    // Target Configuration
    jvm()

    // Native Targets (Host based + standard targets)
    linuxX64()
    macosArm64()
    macosX64()
    mingwX64() // Windows Native

    // JS / Wasm (Future proofing)
    js(IR) {
        browser()
        nodejs()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(libs.bignum)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.property)
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
