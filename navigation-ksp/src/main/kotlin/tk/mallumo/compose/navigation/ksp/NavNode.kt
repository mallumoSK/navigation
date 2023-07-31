package tk.mallumo.compose.navigation.ksp

import com.google.devtools.ksp.*
import com.google.devtools.ksp.symbol.*

class NavNode(
    declaration: KSFunctionDeclaration,
    val name: String = declaration.simpleName.asString(),
    val fullName: String = declaration.qualifiedName!!.asString(),
    val args: KSClassDeclaration? = extractArgs(declaration),
    val files: List<KSFile?> = listOf(declaration.containingFile, args?.containingFile),
    val argsProperties: Sequence<PropertyTypeHolder>? = extractArgsProperties(args)
) {
    companion object {

        private fun extractArgs(declaration: KSFunctionDeclaration): KSClassDeclaration? {
            return declaration.annotations
                .first { it.shortName.asString() == NavigationProcessor.composableNavNodeName }
                .arguments
                .first { it.name!!.asString() == "args" }
                .value
                .let {
                    if (it is KSType
                        && it.declaration is KSClassDeclaration
                        && it.declaration.qualifiedName?.asString() != "kotlin.Unit"
                    ) it.declaration as KSClassDeclaration
                    else null
                }
        }

        private fun extractArgsProperties(args: KSClassDeclaration?): Sequence<PropertyTypeHolder>? {
            return args?.getAllSuperTypes() // find parents of annotated class
                ?.map { it.declaration }
                ?.filterIsInstance<KSClassDeclaration>()
                ?.plusElement(args)
                ?.map { property ->
                    property.getDeclaredProperties() // find all properties of class
                        .filter { !it.isAbstract() }
                        .filter { it.getter != null }
                        .filter { it.setter != null }
                        .filter { it.extensionReceiver == null }
                        .map { PropertyTypeHolder.get(it) } // get only usable properties
                        .filterNotNull()
                }
                ?.flatten()
        }
    }

    override fun toString(): String {
        return "$fullName-${args?.qualifiedName?.asString()}-${argsProperties?.joinToString { it.toString() }}"
    }
}
