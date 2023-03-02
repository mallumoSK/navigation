package tk.mallumo.compose.navigation.viewmodel

import tk.mallumo.compose.navigation.*
import kotlin.reflect.KClass
import kotlin.reflect.full.*

actual object ViewModelFactory {

    internal actual val initializers: MutableMap<String, () -> SharedViewModel> = mutableMapOf()

    actual fun <T : SharedViewModel> register(
        clazz: KClass<T>,
        instanceCall: () -> T
    ) {
        initializers[clazz.qName] = instanceCall
    }

    @Suppress("UNCHECKED_CAST")
    actual fun <T : SharedViewModel> instanceOf(clazz: KClass<T>): T {
        return (initializers[clazz.qName]?.invoke() ?: clazz.createInstance()) as T
    }

}
