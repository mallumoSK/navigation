@file:Suppress("unused")

package tk.mallumo.compose.navigation.viewmodel

import kotlinx.coroutines.*


actual abstract class SharedViewModel {

    @Suppress("unused")
    protected actual val scope: CoroutineScope get() = internalScopeRef

    internal actual var internalScopeRef: CoroutineScope = createViewModelScope(this::class)

    actual abstract fun onRelease()

    internal actual fun releaseInternal() {
        onRelease()
        releaseScope()
    }
}
