package tk.mallumo.compose.navigation

import androidx.compose.runtime.staticCompositionLocalOf

val LocalNavigation = staticCompositionLocalOf<Navigation> { error("Unexpected error") }