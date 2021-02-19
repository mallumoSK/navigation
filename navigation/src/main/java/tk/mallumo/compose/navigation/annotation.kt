package tk.mallumo.compose.navigation

import kotlin.reflect.KClass


class EmptyViewModel : NavigationViewModel() {
    override fun onCleared() {
    }
}

@Suppress("unused")
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class ComposableNavNode(val args: KClass<*> = Unit::class)

@DslMarker
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class ExtNavMarker


