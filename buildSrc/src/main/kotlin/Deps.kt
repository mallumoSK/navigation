@Suppress("SpellCheckingInspection", "ClassName")
object Deps {
    const val group = "tk.mallumo"


    object version {
        const val kotlin = "1.9.0"
        const val agp = "8.0.2"
        const val ksp = "1.9.0-1.0.12"

        object compose {
            const val desktop = "1.4.3"
            const val android = "1.5.0"

            const val compiller = "1.4.4"
            const val runtime = "1.4.3"
            const val ui = "1.4.3"
            const val foundation = "1.4.3"
            const val material = "1.4.3"
        }

    }

    object core {
        const val version = "${Deps.version.ksp}-1.0.0"
        const val artifact = "navigation"
    }

    object ksp {
        const val version = core.version
        const val artifact = "navigation-ksp"
    }

    object lib {
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${version.kotlin}"
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
        const val ksp = "com.google.devtools.ksp:symbol-processing-api:${version.ksp}"
        const val composeActivity = "androidx.activity:activity-compose:1.7.2"
    }
}
