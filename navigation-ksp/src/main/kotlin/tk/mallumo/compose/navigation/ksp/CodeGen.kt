package tk.mallumo.compose.navigation.ksp

import com.google.devtools.ksp.symbol.*

object CodeGen {

    fun generateNavNodeExt(
        navNodeExt: StringBuilder,
        node: NavNode
    ) {
        val seeArgs = node.args?.qualifiedName?.asString()?.let {
            "@see $it"
        } ?: ""
        navNodeExt.append(
            """
/**
 * @see ${node.fullName}
 * $seeArgs
 */
val Node.Companion.${node.name} get() = Node("${node.fullName}") //args: ${node.args?.qualifiedName?.asString()}
"""
        )
    }

    fun generateNavFunExt(
        navFunExt: StringBuilder,
        node: NavNode
    ) {
        navFunExt += """
/**
 * @see ${node.fullName}
 */
@ExtNavMarker
@Suppress("FunctionName")
fun Navigation.navTo_${node.name}(args: ArgumentsNavigation = ArgumentsNavigation(), clearTop: Boolean = false) {
    navigateTo(Node.${node.name}, args, clearTop)
}
"""
        node.args?.qualifiedName?.asString()?.also { args ->
            navFunExt += """
/**
 * @see ${node.fullName}
 */
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
                composite(node) { ${node.fullName}() }
"""
        )
    }

    fun generateNavigationContent(useMaterial3:Boolean,
                                  options: Map<String, String>,
                                  itemsVM: List<KSClassDeclaration>) = buildString {

        val factoryContent = itemsVM.joinToString("\n") {
            val clazz = it.qualifiedName!!.asString()
            "\tViewModelFactory.register($clazz::class){${clazz}()}"
        }
        val navigationChildren = options["child"]
            ?.split(' ')
            ?.asSequence()
            ?.map { it.trim() }
            ?.filter { it.length > 1 }
            ?.filter { row -> row.all { it.isLetterOrDigit() || it == '_' } }
            ?.map { it[0].uppercaseChar() + it.substring(1) }
            ?.toSortedSet()
            ?: setOf()

        val materialColors = buildString {
            if(useMaterial3)append("colorScheme")
            else append("colors")
        }

        @Suppress("SpellCheckingInspection")
        append(
            """
private const val navChildKey = "child"

@ExtNavMarker
@Composable
fun NavigationRoot(
    startupNode: Node,
    startupArgs: ArgumentsNavigation? = null,
    animation: FiniteAnimationSpec<Float> = tween()
) {
    
    setupViewModelFactory()

    val navigation = Navigation.rememberNavigationComposite(startupNode, startupArgs)
    val currentNode = navigation.currentNode.collectAsState()

    Crossfade(targetState = currentNode.value, animationSpec = animation) {
        Surface(color = MaterialTheme.${materialColors}.background) {
            CompositionLocalProvider(LocalNavigation provides navigation) {
                navNode(node = it)
            }
        }
    }
}

@Composable
private fun setupViewModelFactory() {
$factoryContent
}

@ExtNavMarker
@Composable
private fun NavigationChild(
    startupNode: Node,
    startupArgs: ArgumentsNavigation? ,
    animation: FiniteAnimationSpec<Float>,
    childKey:String
) {

    val navigation = Navigation.rememberNavigationComposite(startupNode, startupArgs, childKey)
    val currentNode = navigation.currentNode.collectAsState()

    Crossfade(targetState = currentNode.value, animationSpec = animation) {
        Surface(color = MaterialTheme.${materialColors}.background) {
            CompositionLocalProvider(LocalNavigation provides navigation) {
                navNode(node = it)
            }
        }
    }
}"""
        )

        navigationChildren.forEach { id ->
            append(
                """

@ExtNavMarker
@Composable
fun NavigationChild$id(
    startupNode: Node,
    startupArgs: ArgumentsNavigation? = null,
    animation: FiniteAnimationSpec<Float> = tween()
) {
    val parentNavigation = LocalNavigation.current

    val childKey = remember {
        "${'$'}{parentNavigation.navigationId}[${'$'}{parentNavigation.nodeIdentifier}][${'$'}navChildKey.${id}]"
    }
    
    NavigationChild(startupNode, startupArgs, animation, childKey)
}"""
            )
        }
    }

    fun generateComposite(
        declaration: StringBuilder,
        argsConstructor: StringBuilder,
        argsDestructor: StringBuilder
    ) = buildString {
        append(
            """
object ImplNoteUtils {

    @Composable
    fun navNode(node: NodeInfo) {
        when (node.id) {
$declaration
        }
    }

    @Composable
    private fun composite(
        node: NodeInfo,
        content: @Composable () -> Unit
    ) {
        val args = remember {
            buildArgsItem(node)
        }
        
        val navArgs = remember {
            navigationArgsInstance { args }
        }

        CompositionLocalProvider(LocalNavigationArgs provides navArgs) {
            content()
        }

        DisposableEffect(node) {
            onDispose {
                saveArgsItem(node, args)
            }
        }
    }

    private fun navigationArgsInstance(body: () -> Any?): NavigationArgs {
        return object : NavigationArgs(body) {}
    }

    private fun buildArgsItem(node: NodeInfo): Any? = when (node.id) {
$argsConstructor
        else -> null
    }   
"""
        )
        if (argsDestructor.isBlank()) {
            append(
                """
    @Suppress("UNUSED_PARAMETER")                
    private fun saveArgsItem(node: NodeInfo, argsItem: Any?) {}
}
"""
            )
        } else {
            append(
                """
    private fun saveArgsItem(node: NodeInfo, argsItem: Any?) {
        when (argsItem) {
$argsDestructor
        }
    }
} 
"""
            )
        }
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
            append("fun $fullName.fill(bundle: ArgumentsNavigation): $fullName {\n")
            entry.second.forEach {
                val fieldName = it.propertyName
                this += when (it.qualifiedName) {
                    "kotlin.Boolean" -> "\t$fieldName = bundle.boolean[\"$fieldName\", $fieldName]"
                    "kotlin.Byte" -> "\t$fieldName = bundle.byte[\"$fieldName\", $fieldName]"
                    "kotlin.Char" -> "\t$fieldName = bundle.char[\"$fieldName\", $fieldName]"
                    "kotlin.Double" -> "\t$fieldName = bundle.double[\"$fieldName\", $fieldName]"
                    "kotlin.Float" -> "\t$fieldName = bundle.float[\"$fieldName\", $fieldName]"
                    "kotlin.Int" -> "\t$fieldName = bundle.int[\"$fieldName\", $fieldName]"
                    "kotlin.Long" -> "\t$fieldName = bundle.long[\"$fieldName\", $fieldName]"
                    "kotlin.Short" -> "\t$fieldName = bundle.short[\"$fieldName\", $fieldName]"
                    "kotlin.String" -> "\t$fieldName = bundle.string[\"$fieldName\", $fieldName]"
                    "kotlin.BooleanArray" -> "\t$fieldName = bundle.booleanArray[\"$fieldName\", $fieldName]"
                    "kotlin.ByteArray" -> "\t$fieldName = bundle.byteArray[\"$fieldName\", $fieldName]"
                    "kotlin.CharArray" -> "\t$fieldName = bundle.charArray[\"$fieldName\", $fieldName]"
                    "kotlin.DoubleArray" -> "\t$fieldName = bundle.doubleArray[\"$fieldName\", $fieldName]"
                    "kotlin.FloatArray" -> "\t$fieldName = bundle.floatArray[\"$fieldName\", $fieldName]"
                    "kotlin.IntArray" -> "\t$fieldName = bundle.intArray[\"$fieldName\", $fieldName]"
                    "kotlin.LongArray" -> "\t$fieldName = bundle.longArray[\"$fieldName\", $fieldName]"
                    "kotlin.ShortArray" -> "\t$fieldName = bundle.shortArray[\"$fieldName\", $fieldName]"
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
            append("\nfun ${entry.first.qualifiedName!!.asString()}.asBundle() = ArgumentsNavigation().apply{\n")
            entry.second.forEach {
                val fieldName = it.propertyName
                appendLine(
                    when (it.qualifiedName) {
                        "kotlin.Boolean" -> "\tthis.boolean[\"$fieldName\"] = this@asBundle.$fieldName"
                        "kotlin.Byte" -> "\tthis.byte[\"$fieldName\"] = this@asBundle.$fieldName"
                        "kotlin.Char" -> "\tthis.char[\"$fieldName\"] = this@asBundle.$fieldName"
                        "kotlin.Double" -> "\tthis.double[\"$fieldName\"] = this@asBundle.$fieldName"
                        "kotlin.Float" -> "\tthis.float[\"$fieldName\"] = this@asBundle.$fieldName"
                        "kotlin.Int" -> "\tthis.int[\"$fieldName\"] = this@asBundle.$fieldName"
                        "kotlin.Long" -> "\tthis.long[\"$fieldName\"] = this@asBundle.$fieldName"
                        "kotlin.Short" -> "\tthis.short[\"$fieldName\"] = this@asBundle.$fieldName"
                        "kotlin.String" -> "\tthis.string[\"$fieldName\"] = this@asBundle.$fieldName"
                        "kotlin.BooleanArray" -> "\tthis.booleanArray[\"$fieldName\"] = this@asBundle.$fieldName"
                        "kotlin.ByteArray" -> "\tthis.byteArray[\"$fieldName\"] = this@asBundle.$fieldName"
                        "kotlin.CharArray" -> "\tthis.charArray[\"$fieldName\"] = this@asBundle.$fieldName"
                        "kotlin.DoubleArray" -> "\tthis.doubleArray[\"$fieldName\"] = this@asBundle.$fieldName"
                        "kotlin.FloatArray" -> "\tthis.floatArray[\"$fieldName\"] = this@asBundle.$fieldName"
                        "kotlin.IntArray" -> "\tthis.intArray[\"$fieldName\"] = this@asBundle.$fieldName"
                        "kotlin.LongArray" -> "\tthis.longArray[\"$fieldName\"] = this@asBundle.$fieldName"
                        "kotlin.ShortArray" -> "\tthis.shortArray[\"$fieldName\"] = this@asBundle.$fieldName"
                        else -> throw RuntimeException("unexpected type: $it")
                    }
                )
            }
            append("}\n")
        }
    }

    fun generateArgsConstructor(builder: StringBuilder, node: NavNode) {
        node.args
            ?.takeIf { it.simpleName.asString() != "Unit" }
            ?.also {
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
