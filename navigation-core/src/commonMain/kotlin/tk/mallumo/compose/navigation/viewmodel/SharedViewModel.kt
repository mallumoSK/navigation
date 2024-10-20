@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package tk.mallumo.compose.navigation.viewmodel

import kotlinx.coroutines.*

expect abstract class SharedViewModel() {

    protected val scope: CoroutineScope
    internal var internalScopeRef: CoroutineScope

    abstract fun onRelease()
    internal fun releaseInternal()
}


