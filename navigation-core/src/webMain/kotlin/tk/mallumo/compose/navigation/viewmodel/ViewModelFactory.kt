@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package tk.mallumo.compose.navigation.viewmodel

import tk.mallumo.compose.navigation.*
import kotlin.reflect.*

internal val viewModels = mutableMapOf<String, SharedViewModel>()


actual object ViewModelFactory {

    internal actual val initializers: MutableMap<String, () -> SharedViewModel> = mutableMapOf()

    init {
        register(NavigationHolder::class) {
            NavigationHolder()
        }
    }

    actual fun <T : SharedViewModel> register(
        clazz: KClass<T>,
        instanceCall: () -> T
    ) {
        initializers[clazz.simpleName!!] = instanceCall
    }

    @Suppress("UNCHECKED_CAST")
    actual fun <T : SharedViewModel> instanceOf(clazz: KClass<T>): T {
        return (initializers[clazz.simpleName!!]?.invoke()
            ?: error("unregistred viewmodel of ${clazz.simpleName}")) as T
    }

    actual fun <T : SharedViewModel> release(instance: T) {
        viewModels.entries.firstOrNull { it.value == instance }
            ?.also {
                viewModels.remove(it.key)?.releaseInternal()
            }
    }
}
