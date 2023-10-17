package tk.mallumo.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import tk.mallumo.compose.navigation.*

data class ChildTestArgs(var mapId: Long = -1)

@Composable
@ComposableNavNode
fun ChildTestHomeUI() {

    NavigationChildChild0(Node.ChildTestHomeInnerUI)
}

@Composable
@ComposableNavNode
fun ChildTestHomeInnerUI() {
    val nav = LocalNavigation.current
    Button({
        nav.parentNavigation?.navTo_ChildTestTargetHome2(ChildTestArgs(2))
    }) {
        Text("nav to ChildTestTargetHome2")
    }
}


@Composable
@ComposableNavNode(ChildTestArgs::class)
fun ChildTestTargetHome2() {
    val args = LocalNavigationArgs.current.rememberArgs<ChildTestArgs>()

    Column {
        Text("current: ChildTestTargetHome2 :: {${args.mapId}}")
        NavigationChildChild2(Node.ChildTestTargetHomeChild, args.asBundle())
    }

}


@Composable
@ComposableNavNode(ChildTestArgs::class)
fun ChildTestTargetHomeChild() {
    val args = LocalNavigationArgs.current.rememberArgs<ChildTestArgs>()
    Text("current: ChildTestTargetHomeChild :: {${args.mapId}}")
//    NavigationChildChild0()
}
