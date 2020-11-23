@file:Suppress("unused")

package tk.mallumo.compose.navigation

import android.os.Bundle
import androidx.core.os.bundleOf

fun tk.sample.app.ArgsMenuFrame.fill(bundle: Bundle): tk.sample.app.ArgsMenuFrame {
    valueX = bundle.getString("valueX", valueX)
    return this
}

fun tk.sample.app.ArgsMenuFrame.asBundle() = bundleOf(
	"valueX" to valueX,
)

fun tk.sample.app.ArgsSecondFrame.fill(bundle: Bundle): tk.sample.app.ArgsSecondFrame {
    item = bundle.getString("item", item)
    return this
}

fun tk.sample.app.ArgsSecondFrame.asBundle() = bundleOf(
	"item" to item,
)