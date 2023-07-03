import org.jetbrains.kotlin.gradle.tasks.*
import java.util.*

plugins {
    kotlin("jvm")
    id("maven-publish")
}

val toolkit by lazy {
    Toolkit.get(extensions = extensions.extraProperties)
}

group = "tk.mallumo"
version = toolkit["version.navigation.ksp"]

dependencies {
    api("com.google.devtools.ksp:symbol-processing-api:${toolkit["version.ksp"]}")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

val prop = Properties().apply {
    project.rootProject.file("local.properties").reader().use {
        load(it)
    }
}

publishing {
    val rName = prop["repsy.name"] as String
    val rKey = prop["repsy.key"] as String
    repositories {
        maven {
            name = "repsy.io"
            url = uri("https://repo.repsy.io/mvn/${rName}/public")
            credentials {
                username = rName
                password = rKey
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "tk.mallumo"
            artifactId = "navigation-ksp"
            version = toolkit["version.navigation.ksp"]
        }
    }
}
