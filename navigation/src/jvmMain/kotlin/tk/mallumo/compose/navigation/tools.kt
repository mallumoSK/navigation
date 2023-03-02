package tk.mallumo.compose.navigation

import kotlinx.coroutines.flow.*
import tk.mallumo.compose.navigation.viewmodel.*

@Suppress("unused")
val composeNavigationRoot: Navigation
    get() {
        val vm = getViewModel(NavigationHolder::class, navRootKey)

        return object : NavigationWrapper() {

            override val navigationId: String
                get() = navRootKey

            override val viewModelHolder: NavigationHolder
                get() = vm

            override val isPreviewMode: Boolean
                get() = false

        }
    }
