package tk.mallumo.compose.navigation.ksp

import org.jetbrains.kotlin.ksp.getAllSuperTypes
import org.jetbrains.kotlin.ksp.getDeclaredProperties
import org.jetbrains.kotlin.ksp.isAbstract
import org.jetbrains.kotlin.ksp.processing.CodeGenerator
import org.jetbrains.kotlin.ksp.processing.KSPLogger
import org.jetbrains.kotlin.ksp.processing.Resolver
import org.jetbrains.kotlin.ksp.processing.SymbolProcessor
import org.jetbrains.kotlin.ksp.symbol.KSClassDeclaration
import org.jetbrains.kotlin.ksp.symbol.KSFunctionDeclaration
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


    lateinit var bundled: StringBuilder
    lateinit var argsConstructor: StringBuilder
    lateinit var argsDestructor: StringBuilder

    lateinit var navNodeExt: StringBuilder
    lateinit var navFunExt: StringBuilder
    lateinit var navCompositeDeclaration: StringBuilder


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
        val args = buildNavArgs(nodes)
        generate(nodes, args)
    }

    private fun buildNavArgs(nodes: List<NavNode>) =
        nodes.mapNotNull { it.args }
            .associateBy({ it }, { current -> // map all properties to annotated class
                current.getAllSuperTypes() // find parents of annotated class
                    .map { it.declaration }
                    .filterIsInstance<KSClassDeclaration>()
                    .plusElement(current)
                    .map { property ->
                        property.getDeclaredProperties() // find all properties of class
                            .asSequence()
                            .filter { !it.isAbstract() }
                            .filter { it.getter != null }
                            .filter { it.setter != null }
                            .filter { it.extensionReceiver == null }
                            .map { PropertyTypeHolder.get(it) } // get only usable properties
                            .filterNotNull()
                    }
                    .flatten()
            })

    private fun buildNavNodes(resolver: Resolver): List<NavNode> {
        return resolver.getSymbolsWithAnnotation(composableNavNodeNameFull)
            .filterIsInstance<KSFunctionDeclaration>()
            .map { NavNode(it) }
    }


    private fun generate(
        nodes: List<NavNode>,
        args: Map<KSClassDeclaration, Sequence<PropertyTypeHolder>>
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
            CodeGen.generateArgsDestructor(argsDestructor, node)
        }

        bundled.apply {
            args.entries.forEach {
                CodeGen.generateBundleFill(it, this)
                CodeGen.generateAsBundle(it, this)
            }
        }

    }


    override fun finish() {
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
            fileName = "GeneratedNavComposite.kt",
            imports = listOf(
                "androidx.compose.runtime.Composable",
                "androidx.compose.runtime.Providers",
                "androidx.compose.runtime.remember",
                "androidx.compose.runtime.onDispose",
                "tk.mallumo.compose.navigation.ImplNoteUtils.navNode",
                "androidx.compose.animation.Crossfade",
                "androidx.compose.material.MaterialTheme",
                "androidx.compose.material.Surface",
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

        codeWriter.write(true)
    }

}