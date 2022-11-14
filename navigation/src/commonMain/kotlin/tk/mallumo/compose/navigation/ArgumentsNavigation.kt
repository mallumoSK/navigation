package tk.mallumo.compose.navigation


class ArgumentsNavigation(vararg properties: Pair<String, Any> = arrayOf()) {

    internal val holderShort by lazy {
        mutableMapOf<String, Short>()
    }

    internal val holderInt by lazy {
        mutableMapOf<String, Int>()
    }

    internal val holderLong by lazy {
        mutableMapOf<String, Long>()
    }

    internal val holderFloat by lazy {
        mutableMapOf<String, Float>()
    }

    internal val holderDouble by lazy {
        mutableMapOf<String, Double>()
    }

    internal val holderString by lazy {
        mutableMapOf<String, String>()
    }

    internal val holderByte by lazy {
        mutableMapOf<String, Byte>()
    }

    internal val holderChar by lazy {
        mutableMapOf<String, Char>()
    }

    internal val holderBoolean by lazy {
        mutableMapOf<String, Boolean>()
    }

    internal val holderShortArray by lazy {
        mutableMapOf<String, ShortArray>()
    }

    internal val holderIntArray by lazy {
        mutableMapOf<String, IntArray>()
    }

    internal val holderLongArray by lazy {
        mutableMapOf<String, LongArray>()
    }

    internal val holderFloatArray by lazy {
        mutableMapOf<String, FloatArray>()
    }

    internal val holderDoubleArray by lazy {
        mutableMapOf<String, DoubleArray>()
    }

    internal val holderCharArray by lazy {
        mutableMapOf<String, CharArray>()
    }

    internal val holderBooleanArray by lazy {
        mutableMapOf<String, BooleanArray>()
    }

    internal val holderByteArray by lazy {
        mutableMapOf<String, ByteArray>()
    }

    val byte: GetterSetter<Byte> by lazy {
        GetterSetter(
            { key, default -> holderByte[key] ?: default ?: error("property '$key' of type Byte not found") },
            { key, value -> holderByte[key] = value }
        )
    }

    val boolean: GetterSetter<Boolean> by lazy {
        GetterSetter(
            { key, default -> holderBoolean[key] ?: default ?: error("property '$key' of type Boolean not found") },
            { key, value -> holderBoolean[key] = value }
        )
    }

    val char: GetterSetter<Char> by lazy {
        GetterSetter(
            { key, default -> holderChar[key] ?: default ?: error("property '$key' of type Char not found") },
            { key, value -> holderChar[key] = value }
        )
    }

    val short: GetterSetter<Short> by lazy {
        GetterSetter(
            { key, default -> holderShort[key] ?: default ?: error("property '$key' of type Short not found") },
            { key, value -> holderShort[key] = value }
        )
    }

    val int: GetterSetter<Int> by lazy {
        GetterSetter(
            { key, default -> holderInt[key] ?: default ?: error("property '$key' of type Int not found") },
            { key, value -> holderInt[key] = value }
        )
    }

    val long: GetterSetter<Long> by lazy {
        GetterSetter(
            { key, default -> holderLong[key] ?: default ?: error("property '$key' of type Long not found") },
            { key, value -> holderLong[key] = value }
        )
    }

    val float: GetterSetter<Float> by lazy {
        GetterSetter(
            { key, default -> holderFloat[key] ?: default ?: error("property '$key' of type Float not found") },
            { key, value -> holderFloat[key] = value }
        )

    }
    val double: GetterSetter<Double> by lazy {
        GetterSetter(
            { key, default -> holderDouble[key] ?: default ?: error("property '$key' of type Double not found") },
            { key, value -> holderDouble[key] = value }
        )
    }

    val booleanArray: GetterSetter<BooleanArray> by lazy {
        GetterSetter(
            { key, default ->
                holderBooleanArray[key] ?: default ?: error("property '$key' of type BooleanArray not found")
            },
            { key, value -> holderBooleanArray[key] = value }
        )
    }

    val charArray: GetterSetter<CharArray> by lazy {
        GetterSetter(
            { key, default -> holderCharArray[key] ?: default ?: error("property '$key' of type CharArray not found") },
            { key, value -> holderCharArray[key] = value }
        )
    }

    val shortArray: GetterSetter<ShortArray> by lazy {
        GetterSetter(
            { key, default ->
                holderShortArray[key] ?: default ?: error("property '$key' of type ShortArray not found")
            },
            { key, value -> holderShortArray[key] = value }
        )
    }

    val intArray: GetterSetter<IntArray> by lazy {
        GetterSetter(
            { key, default -> holderIntArray[key] ?: default ?: error("property '$key' of type IntArray not found") },
            { key, value -> holderIntArray[key] = value }
        )
    }

    val longArray: GetterSetter<LongArray> by lazy {
        GetterSetter(
            { key, default -> holderLongArray[key] ?: default ?: error("property '$key' of type LongArray not found") },
            { key, value -> holderLongArray[key] = value }
        )
    }

    val floatArray: GetterSetter<FloatArray> by lazy {
        GetterSetter(
            { key, default ->
                holderFloatArray[key] ?: default ?: error("property '$key' of type FloatArray not found")
            },
            { key, value -> holderFloatArray[key] = value }
        )

    }
    val doubleArray: GetterSetter<DoubleArray> by lazy {
        GetterSetter(
            { key, default ->
                holderDoubleArray[key] ?: default ?: error("property '$key' of type DoubleArray not found")
            },
            { key, value -> holderDoubleArray[key] = value }
        )
    }

    val string: GetterSetter<String> by lazy {
        GetterSetter(
            { key, default -> holderString[key] ?: default ?: error("property '$key' of type String not found") },
            { key, value -> holderString[key] = value }
        )
    }

    val byteArray: GetterSetter<ByteArray> by lazy {
        GetterSetter(
            { key, default -> holderByteArray[key] ?: default ?: error("property '$key' of type ByteArray not found") },
            { key, value -> holderByteArray[key] = value }
        )
    }

    init {
        properties.forEach {
            when (it.second::class) {
                Int::class -> holderInt[it.first] = it.second as Int
                String::class -> holderString[it.first] = it.second as String
                Long::class -> holderLong[it.first] = it.second as Long
                Double::class -> holderDouble[it.first] = it.second as Double
                Float::class -> holderFloat[it.first] = it.second as Float
                ByteArray::class -> holderByteArray[it.first] = it.second as ByteArray
                Short::class -> holderShort[it.first] = it.second as Short
                Char::class -> holderChar[it.first] = it.second as Char
                Byte::class -> holderByte[it.first] = it.second as Byte
                Boolean::class -> holderBoolean[it.first] = it.second as Boolean

                IntArray::class -> holderIntArray[it.first] = it.second as IntArray
                LongArray::class -> holderLongArray[it.first] = it.second as LongArray
                DoubleArray::class -> holderDoubleArray[it.first] = it.second as DoubleArray
                FloatArray::class -> holderFloatArray[it.first] = it.second as FloatArray
                ShortArray::class -> holderShortArray[it.first] = it.second as ShortArray
                CharArray::class -> holderCharArray[it.first] = it.second as CharArray
                BooleanArray::class -> holderBooleanArray[it.first] = it.second as BooleanArray

                else -> error("unsupported property '${it.first}' of type ${it.second::class.qualifiedName}")
            }
        }
    }

    inline operator fun <reified T> get(key: String): T = when (T::class) {
        Int::class -> int[key]
        String::class -> string[key]
        Long::class -> long[key]
        Float::class -> float[key]
        Double::class -> double[key]
        Short::class -> short[key]
        ByteArray::class -> byteArray[key]
        Char::class -> char[key]
        Byte::class -> byte[key]
        Boolean::class -> boolean[key]

        IntArray::class -> intArray[key]
        LongArray::class -> longArray[key]
        FloatArray::class -> floatArray[key]
        DoubleArray::class -> doubleArray[key]
        ShortArray::class -> shortArray[key]
        CharArray::class -> charArray[key]
        BooleanArray::class -> booleanArray[key]
        else -> error("unsupported property '$key' of type ${T::class.qualifiedName}")
    } as T

    inline operator fun <reified T> set(key: String, value: T) {
        when (T::class) {
            Int::class -> int[key] = value as Int
            String::class -> string[key] = value as String
            Long::class -> long[key] = value as Long
            Double::class -> double[key] = value as Double
            Float::class -> float[key] = value as Float
            Short::class -> short[key] = value as Short
            ByteArray::class -> byteArray[key] = value as ByteArray
            Char::class -> char[key] = value as Char
            Byte::class -> byte[key] = value as Byte
            Boolean::class -> boolean[key] = value as Boolean

            IntArray::class -> intArray[key] = value as IntArray
            LongArray::class -> longArray[key] = value as LongArray
            DoubleArray::class -> doubleArray[key] = value as DoubleArray
            FloatArray::class -> floatArray[key] = value as FloatArray
            ShortArray::class -> shortArray[key] = value as ShortArray
            CharArray::class -> charArray[key] = value as CharArray
            BooleanArray::class -> booleanArray[key] = value as BooleanArray
            else -> error("unsupported property '$key' of type ${T::class.qualifiedName}")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArgumentsNavigation

        if (holderShort != other.holderShort) return false
        if (holderInt != other.holderInt) return false
        if (holderLong != other.holderLong) return false
        if (holderFloat != other.holderFloat) return false
        if (holderDouble != other.holderDouble) return false
        if (holderString != other.holderString) return false
        if (holderByte != other.holderByte) return false
        if (holderChar != other.holderChar) return false
        if (holderBoolean != other.holderBoolean) return false
        if (holderShortArray != other.holderShortArray) return false
        if (holderIntArray != other.holderIntArray) return false
        if (holderLongArray != other.holderLongArray) return false
        if (holderFloatArray != other.holderFloatArray) return false
        if (holderDoubleArray != other.holderDoubleArray) return false
        if (holderCharArray != other.holderCharArray) return false
        if (holderBooleanArray != other.holderBooleanArray) return false
        if (holderByteArray != other.holderByteArray) return false

        return true
    }

    override fun hashCode(): Int {
        var result = holderShort.hashCode()
        result = 31 * result + holderInt.hashCode()
        result = 31 * result + holderLong.hashCode()
        result = 31 * result + holderFloat.hashCode()
        result = 31 * result + holderDouble.hashCode()
        result = 31 * result + holderString.hashCode()
        result = 31 * result + holderByte.hashCode()
        result = 31 * result + holderChar.hashCode()
        result = 31 * result + holderBoolean.hashCode()
        result = 31 * result + holderShortArray.hashCode()
        result = 31 * result + holderIntArray.hashCode()
        result = 31 * result + holderLongArray.hashCode()
        result = 31 * result + holderFloatArray.hashCode()
        result = 31 * result + holderDoubleArray.hashCode()
        result = 31 * result + holderCharArray.hashCode()
        result = 31 * result + holderBooleanArray.hashCode()
        result = 31 * result + holderByteArray.hashCode()
        return result
    }

    class GetterSetter<T>(
        private val getter: (String, T?) -> T,
        private val setter: (String, T) -> Unit
    ) {

        operator fun get(key: String): T = get(key, null)

        operator fun get(key: String, default: T?): T = getter(key, default)

        operator fun set(key: String, value: T): Unit = setter(key, value)
    }
}
