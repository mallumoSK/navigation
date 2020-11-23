package tk.mallumo.compose.navigation

import androidx.lifecycle.ViewModel
import kotlin.reflect.KClass

class EmptyViewModel : ViewModel()

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class ComposableNavNode(val args: KClass<*> = Unit::class)


@DslMarker
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class ExtNavMarker


