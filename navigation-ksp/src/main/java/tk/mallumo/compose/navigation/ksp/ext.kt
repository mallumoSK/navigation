package tk.mallumo.compose.navigation.ksp

import com.google.devtools.ksp.findActualType
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeParameter


/**
 * get all super types for a class declaration
 * Calling [getAllSuperTypes] requires type resolution therefore is expensive and should be avoided if possible.
 */
fun KSClassDeclaration.getAllSuperTypes(): Sequence<KSType> {

    fun KSTypeParameter.getTypesUpperBound(): Sequence<KSClassDeclaration> =
        this.bounds.asSequence().flatMap {
            when (val resolvedDeclaration = it.resolve().declaration) {
                is KSClassDeclaration -> sequenceOf(resolvedDeclaration)
                is KSTypeAlias -> sequenceOf(resolvedDeclaration.findActualType())
                is KSTypeParameter -> resolvedDeclaration.getTypesUpperBound()
                else -> throw IllegalStateException()
            }
        }

    return this.superTypes
        .asSequence()
        .mapNotNull { it.resolve() }
        .plus(
            this.superTypes
                .asSequence()
                .mapNotNull { it.resolve().declaration }
                .flatMap {
                    when (it) {
                        is KSClassDeclaration -> it.getAllSuperTypes()
                        is KSTypeAlias -> it.findActualType().getAllSuperTypes()
                        is KSTypeParameter -> it.getTypesUpperBound()
                            .flatMap { it.getAllSuperTypes() }
                        else -> throw IllegalStateException()
                    }
                }
        )
        .distinct()
}