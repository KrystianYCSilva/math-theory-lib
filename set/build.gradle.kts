plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()
    js(IR) { browser(); nodejs() }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
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