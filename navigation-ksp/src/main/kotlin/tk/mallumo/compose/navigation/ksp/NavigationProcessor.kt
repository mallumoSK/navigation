package tk.mallumo.compose.navigation.ksp


import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import java.io.*
import kotlin.contracts.*

class NavigationProcessorProvider : SymbolProcessorProvider {
    override fun create(
        environment: SymbolProcessorEnvironment
    ): SymbolProcessor {
        return NavigationProcessor(environment)
    }
}

class NavigationProcessor(
    private var environment: SymbolProcessorEnvironment,
    private var invoked: Boolean = false
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

    private val commonSourcesOnly get() = environment.options["commonSourcesOnly"] == "true"

    private val nodes = hashMapOf<String, NavNode>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) return emptyList()

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
    @Suppress("SpellCheckingInspection")
    private fun generateCommonFile(
        pckg: String,
        name: String,
        ext: String
    ): File? {
        val key = "${pckg.replace('.', '/')}/$name.$ext"
        return environment.codeGenerator.generatedFile
            .firstOrNull { it.absolutePath.endsWith(key) }
            ?.let { file ->
                val rootDirPrefix = "/build/generated/ksp/"
                val sourceDirPrefix = "/kotlin/"
                file.absolutePath.indexOf(rootDirPrefix)
                    .takeIf { it > 0 }
                    ?.let { startRootIndex ->
                        val rootDir = File(file.absolutePath.substring(0, startRootIndex), rootDirPrefix)
                        val suffix = file.absolutePath.substring(rootDir.absolutePath.lastIndex)
                        suffix.indexOf(sourceDirPrefix)
                            .takeIf { it > 0 }
                            ?.let { sourceDirPrefixIndex ->
                                File(rootDir, "common/commonMain${suffix.substring(sourceDirPrefixIndex)}").apply {
                                    if (!parentFile.exists()) parentFile.mkdirs()
                                }

                            }
                    }
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
            @Suppress("unused")
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
        ext: String = "kt",
        content: () -> String
    ) {
        contract {
            callsInPlace(content, InvocationKind.EXACTLY_ONCE)
        }
        val data = """
@file:Suppress("unused")
package $basePackage

$imports

${content()}"""

        environment.codeGenerator.createNewFile(
            dependencies = dependencies,
            packageName = basePackage,
            fileName = name,
            extensionName = ext
        ).bufferedWriter().use {
            it.write(data.commented(commonSourcesOnly))
            it.flush()
        }
        if (commonSourcesOnly) {
            generateCommonFile(basePackage, name, ext)
                ?.writeText(data)
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
            imports = "import tk.mallumo.compose.navigation.ArgumentsNavigation"
        ) {
            navFunExt.toString()
        }

        output(
            name = "GeneratedNavigationContent",
            dependencies = dependencies,
            imports = """
import tk.mallumo.compose.navigation.*
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import tk.mallumo.compose.navigation.ImplNoteUtils.navNode"""
        ) {
            CodeGen.generateNavigationContent(environment.options)
        }

        output(
            name = "GeneratedImplNoteUtils",
            dependencies = dependencies,
            imports = """
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
import tk.mallumo.compose.navigation.ArgumentsNavigation"""
        ) {
            bundled.toString()
        }
    }
}

private fun String.commented(allLines: Boolean): String =
    if (!allLines) this
    else lines().joinToString(prefix = "//", separator = "\n//")
