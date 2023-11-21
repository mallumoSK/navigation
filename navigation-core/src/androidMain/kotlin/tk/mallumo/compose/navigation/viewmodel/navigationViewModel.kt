package tk.mallumo.compose.navigation.viewmodel

import android.app.*
import android.content.*
import androidx.activity.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import androidx.lifecycle.*
import tk.mallumo.compose.navigation.*
import kotlin.reflect.*

@Composable
actual fun <VM : SharedViewModel> globalViewModel(
    modelClass: KClass<VM>,
    key: String?
): VM {
    val id = key
        ?.let { "${modelClass.qName}::$it" }
        ?: modelClass.qName

    val ctx = LocalContext.current
    return remember(id) {
        ctx.getViewModel(modelClass, id)
    }
}

internal fun <VM : SharedViewModel> Context.getViewModel(modelClass: KClass<VM>, key: String): VM {
    @Suppress("UNCHECKED_CAST")
    return Store.get(this).viewModels.getOrPut(key) {
        ViewModelFactory.instanceOf(modelClass)
    } as VM
}

internal class Store private constructor(context: Context) :
    AndroidViewModel(context.applicationContext as Application) {
    val viewModels = mutableMapOf<String, SharedViewModel>()

    companion object {
        private lateinit var instance: Store
        fun get(context: Context): Store {
            if (!::instance.isInitialized) {
                instance = Store(context)
            }
            return instance
        }

        fun <T : SharedViewModel> release(entry: T) {
            if (::instance.isInitialized) {
                instance.release(entry)
            }
        }

        fun release(entry: String) {
            if (::instance.isInitialized) {
                instance.release(entry)
            }
        }

        fun root(componentActivity: ComponentActivity): NavigationHolder {
            return get(componentActivity.applicationContext)
                .viewModels.getOrPut(navRootKey) {
                    NavigationHolder()
                } as NavigationHolder
        }
    }

    private fun <T : SharedViewModel> release(entry: T) {
        viewModels.entries.firstOrNull { it.value == entry }
            ?.also {
                viewModels.remove(it.key)?.releaseInternal()
            }
    }

    private fun release(key: String) {
        viewModels.remove(key)?.releaseInternal()

    }
}
