package tk.mallumo.compose.navigation.ksp

import org.jetbrains.kotlin.ksp.symbol.KSClassDeclaration
import org.jetbrains.kotlin.ksp.symbol.KSFunctionDeclaration
import org.jetbrains.kotlin.ksp.symbol.KSType

class NavNode(
    declaration: KSFunctionDeclaration,
    val name: String = declaration.simpleName.asString(),
    val fullName: String = declaration.qualifiedName!!.asString(),
    val args: KSClassDeclaration? = declaration.annotations
        .first { it.shortName.asString() == NavigationProcessor.composableNavNodeName }
        .arguments
        .first { it.name!!.asString() == "args" }.value
        .let {
            if (it is KSType && it.declaration is KSClassDeclaration) it.declaration as KSClassDeclaration
            else null
        }
)