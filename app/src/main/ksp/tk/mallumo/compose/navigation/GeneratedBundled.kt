@file:Suppress("unused")

package tk.mallumo.compose.navigation

import android.os.Bundle
import androidx.core.os.bundleOf

fun tk.sample.app.MenuFrameArgs.fill(bundle: Bundle): tk.sample.app.MenuFrameArgs {
    valueX = bundle.getString("valueX", valueX)
    return this
}

fun tk.sample.app.MenuFrameArgs.asBundle() = bundleOf(
    "valueX" to valueX,
)

fun tk.sample.app.SecondFrameArgs.fill(bundle: Bundle): tk.sample.app.SecondFrameArgs {
    item = bundle.getString("item", item)
    return this
}

fun tk.sample.app.SecondFrameArgs.asBundle() = bundleOf(
    "item" to item,
)