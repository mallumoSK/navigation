package tk.mallumo.compose.navigation.ksp


import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class NavigationProcessorProvider : SymbolProcessorProvider {
    override fun create(
        environment: SymbolProcessorEnvironment
    ): SymbolProcessor {
        return NavigationProcessor(environment.codeGenerator, environment.options)
    }
}

class NavigationProcessor(
   private var codeGenerator: CodeGenerator,
   private  var options: Map<String, String>,
   private var invoked:Boolean = false
) : SymbolProcessor {

    companion object {
        private const val basePackage = "tk.mallumo.compose.navigation"
        const val composableNavNodeName = "ComposableNavNode"
        private const val composableNavNodeNameFull = "$basePackage.$composableNavNodeName"
    }

    private lateinit var bundled: StringBuilder
    private lateinit var argsConstructor: StringBuilder
    private lateinit var argsDestructor: StringBuilder

    private lateinit var navNodeExt: StringBuilder
    private lateinit var navFunExt: StringBuilder
    private lateinit var navCompositeDeclaration: StringBuilder


    val nodes = hashMapOf<String, NavNode>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if(invoked) return emptyList()

        val symbols = resolver.getSymbolsWithAnnotation(composableNavNodeNameFull)

        val valid = symbols.filterIsInstance<KSFunctionDeclaration>()
            .map { it.qualifiedName!!.asString() to NavNode(it) }
        val invalid = symbols.filterNot { it is KSFunctionDeclaration }
        nodes.putAll(valid)
        invoked = true
        return invalid.toList()
    }

    override fun finish() {
        val items = nodes.entries.map { it.value }
        if (items.isNotEmpty()) {
            generate(items)
            writeSources(items)
        }
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

    @OptIn(ExperimentalContracts::class)
    private fun output(
        name: String,
        dependencies: Dependencies,
        imports: String = "",
        content: () -> String
    ) {
        contract {
            callsInPlace(content, InvocationKind.EXACTLY_ONCE)
        }
        codeGenerator.createNewFile(
            dependencies = dependencies,
            packageName = basePackage,
            fileName = name,
            extensionName = "kt"
        ).bufferedWriter().use {
            it.write(
                """
@file:Suppress("unused")
package $basePackage

$imports

${content()}"""
            )
            it.flush()
        }
    }

    private fun writeSources(nodes: List<NavNode>) {
        val files = nodes.map { it.files }
            .flatten()
            .filterNotNull()
            .distinctBy { it.filePath }
            .toTypedArray()
        val dependencies = Dependencies(true, *files)

        output(
            name = "GeneratedNavNodeExt",
            dependencies = dependencies
        ) {
            navNodeExt.toString()
        }

        output(
            name = "GeneratedNavFunExt",
            dependencies = dependencies,
            imports = "import android.os.Bundle"
        ) {
            navFunExt.toString()
        }

        output(
            name = "GeneratedNavigationContent",
            dependencies = dependencies,
            imports = """
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import tk.mallumo.compose.navigation.ImplNoteUtils.navNode"""
        ) {
            CodeGen.generateNavigationContent()
        }

        output(
            name = "GeneratedImplNoteUtils",
            dependencies = dependencies,
            imports = """
import android.annotation.SuppressLint
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect"""
        ) {
            CodeGen.generateComposite(
                navCompositeDeclaration,
                argsConstructor,
                argsDestructor
            )
        }

        output(
            name = "GeneratedBundled",
            dependencies = dependencies,
            imports = """
import android.os.Bundle
import androidx.core.os.bundleOf"""
        ) {
            bundled.toString()
        }
    }
}