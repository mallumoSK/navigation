package tk.mallumo.compose.navigation

import androidx.compose.runtime.*
import kotlin.reflect.*

val LocalNavigation = staticCompositionLocalOf<Navigation> { error("Unexpected error") }

@Suppress("unused")
val LocalNavigationArgs = staticCompositionLocalOf<NavigationArgs> {
    error("arguments are not defined for furrent node")
}

abstract class NavigationArgs(
    private val args: () -> Any?
) {
    fun <T : Any> args(type: KClass<T>): T {
        val local = args() ?: throw IllegalStateException("Navigation node has no arguments")
        if (local::class != type) throw Exception("Invalid type of args bound to Navigation node, must be ${local::class.qName}")

        @Suppress("UNCHECKED_CAST")
        return local as T
    }

    @Composable
    inline fun <reified T : Any> rememberArgs(clazz: KClass<T>): T = remember {
        args(clazz)
    }
}

inline fun <reified T : Any> NavigationArgs.args() = args(T::class)
