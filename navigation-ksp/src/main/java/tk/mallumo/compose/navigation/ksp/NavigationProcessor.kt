package tk.mallumo.compose.navigation.ksp


import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import tk.mallumo.compose.navigation.ksp.HashUtils.sha1
import tk.mallumo.layout.inflater.CodeWriter
import java.io.File

class NavigationProcessor : SymbolProcessor {

    /**
     * helper of file/class management
     */
    private lateinit var codeWriter: CodeWriter

    private lateinit var options: Map<String, String>

    companion object {
        private const val basePackage = "tk.mallumo.compose.navigation"
        const val composableNavNodeName = "ComposableNavNode"
        private const val composableNavNodeNameFull = "$basePackage.$composableNavNodeName"
        private const val errProjectOutDir =
                "Inside yours gradle.build must be defined constant (output): \n'ksp.arg(\"NavigationSrcOut\", \"\${projectDir.absolutePath}/src/main/ksp\")'"

    }


    private lateinit var bundled: StringBuilder
    private lateinit var argsConstructor: StringBuilder
    private lateinit var argsDestructor: StringBuilder

    private lateinit var navNodeExt: StringBuilder
    private lateinit var navFunExt: StringBuilder
    private lateinit var navCompositeDeclaration: StringBuilder


    override fun init(
        options: Map<String, String>,
        kotlinVersion: KotlinVersion,
        codeGenerator: CodeGenerator,
        logger: KSPLogger
    ) {
        this.options = options
        this.codeWriter = CodeWriter(
            directory = File(
                options["NavigationSrcOut"] ?: throw RuntimeException(
                    errProjectOutDir
                )
            ),
            rootPackage = "tk.mallumo.compose.navigation"
        )
    }


    override fun process(resolver: Resolver) {
        val nodes = buildNavNodes(resolver)
        if (nodes.isNotEmpty()) {
            val hash = nodes.joinToString("\n") { it.toString().sha1() }
            if (hash != codeWriter.readTmpFile("hash.tmp")) {
                generate(nodes)
                write(hash)
            }
        }
    }

    private fun buildNavNodes(resolver: Resolver): List<NavNode> {
        return resolver.getSymbolsWithAnnotation(composableNavNodeNameFull)
            .filterIsInstance<KSFunctionDeclaration>()
            .map { NavNode(it) }
    }


    private fun generate(
        nodes: List<NavNode>
    ) {
        bundled = StringBuilder()
        argsConstructor = StringBuilder()
        argsDestructor = StringBuilder()

        navNodeExt = StringBuilder()
        navFunExt = StringBuilder()
        navCompositeDeclaration = StringBuilder()

        nodes.forEach { node ->
            CodeGen.generateNavNodeExt(navNodeExt, node)
            CodeGen.generateNavFunExt(navFunExt, node)
            CodeGen.generateCompositeDeclaration(navCompositeDeclaration, node)
            CodeGen.generateArgsConstructor(argsConstructor, node)

        }
        nodes.distinctBy { it.args?.qualifiedName?.asString() }
            .forEach { node ->
                CodeGen.generateArgsDestructor(argsDestructor, node)
            }


        bundled.apply {
            nodes.asSequence()
                .filterNot { it.args == null }
                .map { it.args!! to it.argsProperties!! }
                .distinctBy { it.first.qualifiedName!!.asString() }
                .forEach {
                    CodeGen.generateBundleFill(it, this)
                    CodeGen.generateAsBundle(it, this)
                }
        }
    }

    private fun write(hash: String) {
        codeWriter.add(
            basePackage,
            fileName = "GeneratedNavNodeExt.kt",
        ) { append(navNodeExt) }

        codeWriter.add(
            basePackage,
            fileName = "GeneratedNavFunExt.kt",
            imports = listOf("android.os.Bundle")
        ) { append(navFunExt) }

        codeWriter.add(
            basePackage,
            fileName = "GeneratedNavigationContent.kt",
            imports = listOf(
                "android.os.Bundle",
                "androidx.activity.ComponentActivity",
                "androidx.compose.animation.Crossfade",
                "androidx.compose.material.MaterialTheme",
                "androidx.compose.material.Surface",
                "androidx.compose.runtime.Composable",
                "androidx.compose.runtime.remember",
                "tk.mallumo.compose.navigation.ImplNoteUtils.navNode",
                "androidx.compose.runtime.collectAsState",
                "androidx.compose.animation.core.AnimationSpec",
                "androidx.compose.animation.core.tween"
            )
        ) {
            append(CodeGen.generateNavigationContent())
        }

        codeWriter.add(
            basePackage,
            fileName = "GeneratedImplNoteUtils.kt",
            imports = listOf(
                "android.annotation.SuppressLint",
                "androidx.compose.runtime.Composable",
                "androidx.compose.runtime.Providers",
                "androidx.compose.runtime.onDispose",
                "androidx.compose.runtime.remember"
            )
        ) {
            append(
                CodeGen.generateComposite(
                    navCompositeDeclaration,
                    argsConstructor,
                    argsDestructor
                )
            )
        }

        codeWriter.add(
            basePackage,
            fileName = "GeneratedBundled.kt",
            imports = listOf(
                "android.os.Bundle",
                "androidx.core.os.bundleOf"
            )
        ) { append(bundled) }

        codeWriter.write(deleteOld = true)
        codeWriter.writeTmpFile("hash.tmp", hash)
    }

    override fun finish() {

    }
}