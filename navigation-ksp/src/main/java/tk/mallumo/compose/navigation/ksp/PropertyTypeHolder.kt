package tk.mallumo.compose.navigation.ksp


import org.jetbrains.kotlin.ksp.symbol.KSClassDeclaration
import org.jetbrains.kotlin.ksp.symbol.KSPropertyDeclaration

/**
 * This class holding info about property for speedup processing
 *
 * @param propertyName simple name of property
 * @param qualifiedName qualifiedName of property type or supertype which bundle supports
 */
data class PropertyTypeHolder(
    val propertyName: String,
    val qualifiedName: String
) {
    companion object {
        /**
         * object types which Bundle supports directly
         */
        private val directTypes = arrayOf(
            "kotlin.Boolean",
            "kotlin.Byte",
            "kotlin.Char",
            "kotlin.Double",
            "kotlin.Float",
            "kotlin.Int",
            "kotlin.Long",
            "kotlin.Short",
            "kotlin.String",
            "kotlin.CharSequence",
            "kotlin.BooleanArray",
            "kotlin.ByteArray",
            "kotlin.CharArray",
            "kotlin.DoubleArray",
            "kotlin.FloatArray",
            "kotlin.IntArray",
            "kotlin.LongArray",
            "kotlin.ShortArray",
            "android.util.Size",
            "android.util.SizeF",
            "android.os.Bundle",
            "android.os.Parcelable",
        )

        /**
         * object types which Bundle supports indirectly by dependents
         */
        private val sharedTypes = arrayOf(
            "android.os.IBinder",
            "android.os.Parcelable"
        )

        /**
         * PropertyTypeHolder instance creator
         * @return if is property supported, then returns instance, otherwise returns null
         */
        fun get(prop: KSPropertyDeclaration): PropertyTypeHolder? {

            val name = prop.simpleName.asString()
            val declaration = prop.type?.resolve()?.declaration
            val typeName = declaration?.qualifiedName?.asString() ?: return null

            return when {
                typeName in directTypes -> {
                    PropertyTypeHolder(name, typeName)
                }
                declaration is KSClassDeclaration -> {
                    val parentTypes =
                        declaration.superTypes.map { it.resolve()?.declaration?.qualifiedName?.asString() }
                    sharedTypes.firstOrNull { it in parentTypes }?.let {
                        PropertyTypeHolder(name, it)
                    }
                }
                else -> null
            }
        }
    }
}
