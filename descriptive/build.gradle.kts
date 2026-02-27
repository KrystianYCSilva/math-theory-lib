plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()

    linuxX64()
    macosArm64()
    macosX64()
    mingwX64()

    js(IR) { browser(); nodejs() }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":kernel"))
                implementation(project(":set"))
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

