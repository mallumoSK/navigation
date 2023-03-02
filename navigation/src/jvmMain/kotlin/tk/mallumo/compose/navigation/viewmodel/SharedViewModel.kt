package tk.mallumo.compose.navigation.viewmodel

import kotlinx.coroutines.*

private val viewModelScope = CoroutineScope(Dispatchers.Main)

actual abstract class SharedViewModel {

    protected actual val scope: CoroutineScope = viewModelScope + createViewModelScope(this::class)
    internal actual val internalScopeRef: CoroutineScope
        get() = scope

    actual abstract fun onRelease()

    internal actual fun releaseInternal() {
        onRelease()
        releaseScope()
    }


}

