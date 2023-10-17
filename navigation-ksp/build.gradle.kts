plugins {
    kotlin("jvm")
    id("maven-publish")
}

group = "tk.mallumo"
version = Deps.version.navigation.ksp

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))

dependencies {
    api(Deps.dependency.ksp)
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
            groupId =  Deps.group
            artifactId = "navigation-ksp"
            version = Deps.version.navigation.ksp
            from(components["java"])
        }
    }
}
