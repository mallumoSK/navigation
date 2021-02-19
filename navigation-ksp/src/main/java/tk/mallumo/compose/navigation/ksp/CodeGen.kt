package tk.mallumo.compose.navigation.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration

object CodeGen {

    fun generateNavNodeExt(
        navNodeExt: StringBuilder,
        node: NavNode
    ) {
        navNodeExt.append(
            """
val Node.Companion.${node.name} get() = Node("${node.fullName}") //args: ${node.args?.qualifiedName?.asString()}
"""
        )
    }

    fun generateNavFunExt(
        navFunExt: StringBuilder,
        node: NavNode
    ) {
        navFunExt += """
@ExtNavMarker
@Suppress("FunctionName")
fun Navigation.navTo_${node.name}(args: Bundle = Bundle(), clearTop: Boolean = false) {
    navigateTo(Node.${node.name}, args, clearTop)
}
"""
        node.args?.qualifiedName?.asString()?.also { args ->
            navFunExt += """
@ExtNavMarker
@Suppress("FunctionName")
fun Navigation.navTo_${node.name}(args: $args, clearTop: Boolean = false) {
    navigateTo(Node.${node.name}, args.asBundle(), clearTop)
}
"""
        }
    }

    fun generateCompositeDeclaration(
        navCompositeDeclaration: StringBuilder,
        node: NavNode
    ) {
        navCompositeDeclaration.append(
            """
            "${node.fullName}" ->
                composite(navigationComposite, node) { ${node.fullName}() }
"""
        )
    }

    fun generateNavigationContent() = buildString {
        append(
            """
@Composable
fun ComponentActivity.NavigationContent(
    startupNode: Node,
    startupArgs: Bundle? = null,
    animation: AnimationSpec<Float> = tween()
) {
    val navigationComposite = remember {
        NavigationComposite.get(this, startupNode, startupArgs)
    }
    val currentNode = navigationComposite.currentNode.collectAsState()
    Crossfade(current = currentNode.value, animation = animation) {
        Surface(color = MaterialTheme.colors.background) {
            navNode(navigationComposite = navigationComposite, node = it)
        }
    }
}"""
        )
    }

    fun generateComposite(
        declaration: StringBuilder,
        argsConstructor: StringBuilder,
        argsDestructor: StringBuilder
    ) = buildString {
        append(
            """
@SuppressLint("ComposableNaming")
object ImplNoteUtils {

    @Suppress("RemoveRedundantQualifierName")
    @Composable
    fun navNode(navigationComposite: NavigationComposite, node: ImplNode) {
        when (node.frameID) {
$declaration
        }
    }

    @Composable
    private fun composite(
        navigationComposite: NavigationComposite,
        node: ImplNode,
        content: @Composable () -> Unit
    ) {
        val argsItem: Any? = remember { buildArgsItem(node) }

        val nav = remember {
            Navigation(navigationComposite, node.args, node.identifier) {
                argsItem
            }
        }
        Providers(AmbientNavigation provides nav) {
            content()
        }

        onDispose {
            saveArgsItem(node, argsItem)
        }
    }



    private fun buildArgsItem(node: ImplNode): Any? = when (node.frameID) {
$argsConstructor
        else -> null
    }

    private fun saveArgsItem(node: ImplNode, argsItem: Any?) {
        when (argsItem) {
$argsDestructor
        }
    }
}    
"""
        )
    }

    /**
     * generate extension function 'fill' for every annotated class
     * @param entry prepared properties which will be used for generating
     */
    fun generateBundleFill(
        entry: Pair<KSClassDeclaration, Sequence<PropertyTypeHolder>>,
        builder: StringBuilder
    ) {
        builder.apply {
            val fullName = entry.first.qualifiedName!!.asString()
            append("fun $fullName.fill(bundle: Bundle): $fullName {\n")
            entry.second.forEach {
                val fieldName = it.propertyName
                this += when (it.qualifiedName) {
                    "kotlin.Boolean" -> "\t$fieldName = bundle.getBoolean(\"$fieldName\", $fieldName)"
                    "kotlin.Byte" -> "\t$fieldName = bundle.getByte(\"$fieldName\", $fieldName)"
                    "kotlin.Char" -> "\t$fieldName = bundle.getChar(\"$fieldName\", $fieldName)"
                    "kotlin.Double" -> "\t$fieldName = bundle.getDouble(\"$fieldName\", $fieldName)"
                    "kotlin.Float" -> "\t$fieldName = bundle.getFloat(\"$fieldName\", $fieldName)"
                    "kotlin.Int" -> "\t$fieldName = bundle.getInt(\"$fieldName\", $fieldName)"
                    "kotlin.Long" -> "\t$fieldName = bundle.getLong(\"$fieldName\", $fieldName)"
                    "kotlin.Short" -> "\t$fieldName = bundle.getShort(\"$fieldName\", $fieldName)"
                    "kotlin.String" -> "\t$fieldName = bundle.getString(\"$fieldName\", $fieldName)"
                    "kotlin.CharSequence" -> "\t$fieldName = bundle.getCharSequence(\"$fieldName\", $fieldName)"
                    "kotlin.BooleanArray" -> "\t$fieldName = bundle.getBooleanArray(\"$fieldName\") ?: $fieldName"
                    "kotlin.ByteArray" -> "\t$fieldName = bundle.getByteArray(\"$fieldName\") ?: $fieldName"
                    "kotlin.CharArray" -> "\t$fieldName = bundle.getCharArray(\"$fieldName\") ?: $fieldName"
                    "kotlin.DoubleArray" -> "\t$fieldName = bundle.getDoubleArray(\"$fieldName\") ?: $fieldName"
                    "kotlin.FloatArray" -> "\t$fieldName = bundle.getFloatArray(\"$fieldName\") ?: $fieldName"
                    "kotlin.IntArray" -> "\t$fieldName = bundle.getIntArray(\"$fieldName\") ?: $fieldName"
                    "kotlin.LongArray" -> "\t$fieldName = bundle.getLongArray(\"$fieldName\") ?: $fieldName"
                    "kotlin.ShortArray" -> "\t$fieldName = bundle.getShortArray(\"$fieldName\") ?: $fieldName"
                    "android.util.Size" -> "\t$fieldName = bundle.getSize(\"$fieldName\") ?: $fieldName"
                    "android.util.SizeF" -> "\t$fieldName = bundle.getSizeF(\"$fieldName\") ?: $fieldName"
                    "android.os.Bundle" -> "\t$fieldName = bundle.getBundle(\"$fieldName\") ?: $fieldName"
                    "android.os.IBinder" -> "\t$fieldName = bundle.getIBinder(\"$fieldName\") ?: $fieldName"
                    "android.os.Parcelable" -> "\t$fieldName = bundle.getParcelable(\"$fieldName\") ?: $fieldName"
                    else -> throw RuntimeException("unexpected type: $it")
                }
            }
            append("\treturn this\n")
            append("}")
        }
    }

    /**
     * generate extension function 'asBundle' for every annotated class
     * @param entry prepared properties which will be used for generating
     */
    fun generateAsBundle(
        entry: Pair<KSClassDeclaration, Sequence<PropertyTypeHolder>>,
        builder: StringBuilder
    ) {
        builder.apply {
            append("\nfun ${entry.first.qualifiedName!!.asString()}.asBundle() = bundleOf(\n")
            entry.second.forEach {
                it.propertyName.let { fieldName ->
                    this += "\t\"$fieldName\" to $fieldName,"
                }
            }
            append(")\n")
        }
    }

    fun generateArgsConstructor(builder: StringBuilder, node: NavNode) {
        node.args?.also {
            builder += """        "${node.fullName}" -> ${it.qualifiedName!!.asString()}().fill(node.args)"""
        }
    }

    fun generateArgsDestructor(builder: StringBuilder, node: NavNode) {
        node.args?.also {
            builder += """            is ${it.qualifiedName!!.asString()} -> node.args = argsItem.asBundle()"""
        }
    }
}


private operator fun StringBuilder.plusAssign(s: String) {
    appendLine(s)
}