package tk.mallumo.compose.navigation.viewmodel

import kotlin.reflect.KClass

actual object ViewModelFactory {

    internal actual val initializers: MutableMap<String, () -> SharedViewModel> = mutableMapOf()

    actual fun <T : SharedViewModel> register(
        clazz: KClass<T>,
        instanceCall: () -> T
    ) {
        initializers[clazz.simpleName!!] = instanceCall
    }

    @Suppress("UNCHECKED_CAST")
    actual fun <T : SharedViewModel> instanceOf(clazz: KClass<T>): T {
        return (initializers[clazz.simpleName!!]?.invoke() ?: error("unregistred viewmodel of ${clazz.simpleName}")) as T
    }
}
