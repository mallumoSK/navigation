@Suppress("SpellCheckingInspection", "ClassName")
object Deps {
    const val group = "tk.mallumo"


    object version {
        const val kotlin = "1.9.10"
        const val agp = "8.0.2"
        const val ksp = "1.9.10-1.0.13"
        const val log = "1.9.10-1.0.0"

        object compose {
            const val desktop = "1.5.3"
            const val android = "1.5.3"

//            const val compiller = "1.5.0"
//            const val runtime = "1.5.0"
//            const val ui = "1.5.0"
//            const val foundation = "1.5.0"
//            const val material = "1.5.0"
        }

        object navigation {
            const val core = "${version.ksp}-1.0.5"
            const val ksp = core
        }
    }

    object lib {
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${version.kotlin}"
        const val ksp = "com.google.devtools.ksp:symbol-processing-api:${version.ksp}"
        const val composeActivity = "androidx.activity:activity-compose:1.8.0"
    }

    object dependency {
        const val log = "tk.mallumo:log:${version.log}"
        const val ksp = "com.google.devtools.ksp:symbol-processing-api:${version.ksp}"
    }
}
