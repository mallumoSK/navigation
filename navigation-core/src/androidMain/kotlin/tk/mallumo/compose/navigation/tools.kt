package tk.mallumo.compose.navigation

import android.os.*
import androidx.activity.*
import tk.mallumo.compose.navigation.viewmodel.*

@Suppress("unused")
val ComponentActivity.composeNavigationRoot: Navigation
    get() {
        val vm = Store.root(this)

        return object : NavigationWrapper() {

            override val navigationId: String
                get() = navRootKey

            override val viewModelHolder: NavigationHolder
                get() = vm

            override val isPreviewMode: Boolean
                get() = false

            override val graph: Graph
                get() = Graph.Companion.ROOT
        }
    }

@Suppress("unused")
fun ArgumentsNavigation.toBundle(): Bundle = Bundle().also { b ->
    holderShort.entries.forEach {
        b.putShort(it.key, it.value)
    }
    holderInt.entries.forEach {
        b.putInt(it.key, it.value)
    }
    holderLong.entries.forEach {
        b.putLong(it.key, it.value)
    }
    holderFloat.entries.forEach {
        b.putFloat(it.key, it.value)
    }
    holderDouble.entries.forEach {
        b.putDouble(it.key, it.value)
    }

    holderString.entries.forEach {
        b.putString(it.key, it.value)
    }
    holderByte.entries.forEach {
        b.putByte(it.key, it.value)
    }
    holderChar.entries.forEach {
        b.putChar(it.key, it.value)
    }
    holderBoolean.entries.forEach {
        b.putBoolean(it.key, it.value)
    }

    holderShortArray.entries.forEach {
        b.putShortArray(it.key, it.value)
    }
    holderIntArray.entries.forEach {
        b.putIntArray(it.key, it.value)
    }
    holderLongArray.entries.forEach {
        b.putLongArray(it.key, it.value)
    }
    holderFloatArray.entries.forEach {
        b.putFloatArray(it.key, it.value)
    }
    holderDoubleArray.entries.forEach {
        b.putDoubleArray(it.key, it.value)
    }
    holderCharArray.entries.forEach {
        b.putCharArray(it.key, it.value)
    }
    holderBooleanArray.entries.forEach {
        b.putBooleanArray(it.key, it.value)
    }
    holderByteArray.entries.forEach {
        b.putByteArray(it.key, it.value)
    }
}

@Suppress("unused")
fun Bundle.toArgumentsNavigation(): ArgumentsNavigation = keySet()
    .mapNotNull { key ->
        get(key)?.let { key to it }
    }
    .toTypedArray()
    .let { ArgumentsNavigation(*it) }
