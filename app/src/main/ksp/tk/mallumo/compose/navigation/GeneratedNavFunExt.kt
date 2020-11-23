@file:Suppress("unused")

package tk.mallumo.compose.navigation

import android.os.Bundle


@ExtNavMarker
@Suppress("FunctionName")
fun Navigation.navTo_MenuFrameUI(args: Bundle = Bundle(), clearTop: Boolean = false) {
    navigateTo(Node.MenuFrameUI, args, clearTop)
}


@ExtNavMarker
@Suppress("FunctionName")
fun Navigation.navTo_MenuFrameUI(args: tk.sample.app.MenuFrameArgs, clearTop: Boolean = false) {
    navigateTo(Node.MenuFrameUI, args.asBundle(), clearTop)
}


@ExtNavMarker
@Suppress("FunctionName")
fun Navigation.navTo_SecondFrameUI(args: Bundle = Bundle(), clearTop: Boolean = false) {
    navigateTo(Node.SecondFrameUI, args, clearTop)
}


@ExtNavMarker
@Suppress("FunctionName")
fun Navigation.navTo_SecondFrameUI(args: tk.sample.app.SecondFrameArgs, clearTop: Boolean = false) {
    navigateTo(Node.SecondFrameUI, args.asBundle(), clearTop)
}

