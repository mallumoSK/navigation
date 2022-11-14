plugins {
//    kotlin("multiplatform")
    kotlin("jvm")
    application
//    id("com.google.devtools.ksp")
    id("org.jetbrains.compose")
}

val toolkit by lazy {
    Toolkit.get(extensions = extensions.extraProperties)
}

kotlin {
//    sourceSets.main {
//        kotlin.srcDir("build/generated/ksp/common/commonMain/kotlin")
//    }

    sourceSets.main {
        kotlin.srcDir("src/jvmMain/kotlin")
    }
}


dependencies {
    implementation(project(":test:common"))
    implementation(compose.desktop.currentOs)
    testImplementation(kotlin("test"))

    implementation(project(":navigation"))
//    ksp(project(":navigation-ksp"))
    implementation(compose.desktop.currentOs)

    implementation("tk.mallumo:log:${toolkit["version.log"]}")
    implementation("tk.mallumo:utils:${toolkit["version.utils"]}")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}

//ksp {
////    allWarningsAsErrors = true
////    allowSourcesFromOtherPlugins = true
//    arg("child", "child0 child1 child2 child3")
//
//}
