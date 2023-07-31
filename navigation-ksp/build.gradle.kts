plugins {
    kotlin("jvm")
    id("maven-publish")
}

group = Deps.group
version = Deps.ksp.version

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))

dependencies {
    api(Deps.lib.ksp)
}

publishing {
    val rName = propertiesLocal["repsy.name"]
    val rKey = propertiesLocal["repsy.key"]
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
            groupId = Deps.group
            artifactId = Deps.ksp.artifact
            version = Deps.ksp.version
        }
    }
}
