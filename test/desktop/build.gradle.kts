plugins {
//    kotlin("multiplatform")
    kotlin("jvm")
    application
//    id("com.google.devtools.ksp")
    id("org.jetbrains.compose")
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

    implementation(project(":navigation-core"))
//    ksp(project(":navigation-ksp"))
    implementation(compose.desktop.currentOs)

    implementation(Deps.dependency.log)
}

java.toolchain. languageVersion.set(JavaLanguageVersion.of(11))

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
