package tk.mallumo.compose.navigation.viewmodel

import kotlinx.coroutines.*

expect abstract class SharedViewModel() {

    protected val scope: CoroutineScope
    internal var internalScopeRef: CoroutineScope

    abstract fun onRelease()
    internal fun releaseInternal()
}


