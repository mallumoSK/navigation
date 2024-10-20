plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.core)
    alias(libs.plugins.compose.kotlin)
    application
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
    testImplementation(kotlin("test"))

    implementation(project(":navigation-core"))
//    ksp(project(":navigation-ksp"))
    implementation(compose.desktop.currentOs)
    implementation(compose.runtime)
    implementation(compose.desktop.currentOs)

    implementation(libs.me.log)
}
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
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
