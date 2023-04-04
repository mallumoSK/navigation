package tk.mallumo.compose.navigation.viewmodel

import kotlinx.coroutines.*

private val viewModelScope = CoroutineScope(Dispatchers.Main)

actual abstract class SharedViewModel {

    protected actual val scope: CoroutineScope get() = internalScopeRef

    internal actual var internalScopeRef: CoroutineScope = viewModelScope + createViewModelScope(this::class)

    actual abstract fun onRelease()

    internal actual fun releaseInternal() {
        onRelease()
        releaseScope()
    }
}

