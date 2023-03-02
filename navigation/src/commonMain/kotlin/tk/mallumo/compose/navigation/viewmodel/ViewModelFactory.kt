package tk.mallumo.compose.navigation.viewmodel

import kotlin.reflect.*

expect object ViewModelFactory {
    internal val initializers:MutableMap<String, (() -> SharedViewModel)>
    fun <T : SharedViewModel> register(clazz: KClass<T>, instanceCall: () -> T)
    fun <T : SharedViewModel> instanceOf(clazz:KClass<T>):T

}
