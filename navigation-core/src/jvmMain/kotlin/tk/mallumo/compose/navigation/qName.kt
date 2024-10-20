package tk.mallumo.compose.navigation

import kotlin.reflect.*

actual val KClass<*>.qName: String get() = qualifiedName ?: simpleName!!
