import tk.mallumo.compose.navigation.ComposableNavNode

data class ArgTestC(var x: String = "xx", var y: String = "yy" )

@ComposableNavNode(ArgTestC::class)
fun TestC(){

}