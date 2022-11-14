package tk.mallumo.compose.navigation.ksp

import com.google.devtools.ksp.symbol.*

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
    override fun toString(): String {
        return "$qualifiedName : $propertyName"
    }

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

            "kotlin.BooleanArray",
            "kotlin.ByteArray",
            "kotlin.CharArray",
            "kotlin.DoubleArray",
            "kotlin.FloatArray",
            "kotlin.IntArray",
            "kotlin.LongArray",
            "kotlin.ShortArray"
        )


        /**
         * PropertyTypeHolder instance creator
         * @return if is property supported, then returns instance, otherwise returns null
         */
        fun get(prop: KSPropertyDeclaration): PropertyTypeHolder? {

            val name = prop.simpleName.asString()
            val declaration = prop.type.resolve().declaration
            val typeName = declaration.qualifiedName?.asString() ?: return null

            return if (typeName !in directTypes) null
            else PropertyTypeHolder(name, typeName)
        }
    }
}
