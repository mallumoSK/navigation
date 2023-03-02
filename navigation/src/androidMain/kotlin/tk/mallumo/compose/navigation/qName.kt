package tk.mallumo.compose.navigation

import kotlin.reflect.KClass

actual val  KClass<*>.qName: String  get() = qualifiedName?:simpleName!!
