import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.vanniktech.maven.publish) apply false
}

val publishableProjects = setOf(
    ":kernel", ":set", ":logic", ":relation",
    ":function", ":algebra", ":category",
)

allprojects {
    group = "io.github.KrystianYCSilva"
    version = "0.1.0-alpha.1"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        buildUponDefaultConfig = true
        allRules = false
    }

    if (path in publishableProjects) {
        apply(plugin = "com.vanniktech.maven.publish")
    }

    plugins.withId("com.vanniktech.maven.publish") {
        extensions.configure<MavenPublishBaseExtension>("mavenPublishing") {
            publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
            signAllPublications()

            pom {
                name.set("mathsets-kt-${project.name}")
                description.set("Mathematical foundations in Kotlin Multiplatform — ${project.name} module.")
                url.set("https://github.com/KrystianYCSilva/math-theory-lib")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("KrystianYCSilva")
                        name.set("Krystian Yago C. Silva")
                    }
                }
                scm {
                    url.set("https://github.com/KrystianYCSilva/math-theory-lib")
                    connection.set("scm:git:https://github.com/KrystianYCSilva/math-theory-lib.git")
                    developerConnection.set("scm:git:git@github.com:KrystianYCSilva/math-theory-lib.git")
                }
            }
        }
    }
}
