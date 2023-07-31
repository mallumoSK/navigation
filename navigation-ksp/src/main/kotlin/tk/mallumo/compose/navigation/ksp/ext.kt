package tk.mallumo.compose.navigation.ksp

import com.google.devtools.ksp.*
import com.google.devtools.ksp.symbol.*


/**
 * get all super types for a class declaration
 * Calling [getAllSuperTypes] requires type resolution therefore is expensive and should be avoided if possible.
 */
fun KSClassDeclaration.getAllSuperTypes(): Sequence<KSType> {

    fun KSTypeParameter.getTypesUpperBound(): Sequence<KSClassDeclaration> =
        this.bounds.flatMap {
            when (val resolvedDeclaration = it.resolve().declaration) {
                is KSClassDeclaration -> sequenceOf(resolvedDeclaration)
                is KSTypeAlias -> sequenceOf(resolvedDeclaration.findActualType())
                is KSTypeParameter -> resolvedDeclaration.getTypesUpperBound()
                else -> throw IllegalStateException()
            }
        }

    return this.superTypes
        .map { it.resolve() }
        .plus(
            this.superTypes
                .map { it.resolve().declaration }
                .flatMap { ksDeclaration ->
                    when (ksDeclaration) {
                        is KSClassDeclaration -> ksDeclaration.getAllSuperTypes()
                        is KSTypeAlias -> ksDeclaration.findActualType().getAllSuperTypes()
                        is KSTypeParameter -> ksDeclaration.getTypesUpperBound()
                            .flatMap { it.getAllSuperTypes() }

                        else -> throw IllegalStateException()
                    }
                }
        )
        .distinct()
}
