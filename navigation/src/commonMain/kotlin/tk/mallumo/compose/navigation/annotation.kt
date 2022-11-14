package tk.mallumo.compose.navigation

import kotlin.reflect.*

@Suppress("unused")
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class ComposableNavNode(val args: KClass<*> = Unit::class)

@Suppress("unused")
@DslMarker
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class ExtNavMarker


