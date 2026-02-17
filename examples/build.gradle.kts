plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()
    js(IR) { browser(); nodejs() }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":kernel"))
                implementation(project(":logic"))
                implementation(project(":set"))
                implementation(project(":relation"))
                implementation(project(":function"))
                implementation(project(":construction"))
                implementation(project(":ordinal"))
                implementation(project(":cardinal"))
                implementation(project(":descriptive"))
                implementation(project(":combinatorics"))
                implementation(project(":forcing"))
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
