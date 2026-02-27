package gg
import tk.mallumo.compose.navigation.ComposableNavNode

data class ArgTestJS(var x: String = "xx", var y: String = "yy" )

@ComposableNavNode(ArgTestJS::class)
fun TestJS(){

}
@ComposableNavNode
fun TestJS2(){

}