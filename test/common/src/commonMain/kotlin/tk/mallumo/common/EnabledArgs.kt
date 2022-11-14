package tk.mallumo.common

open class EnabledArgs(
    var char: Char = '1',
    var boolean: Boolean = false,
    var byte: Byte = 0x3,
    var short: Short = 8,
    var int: Int = 2,
    var long: Long = 3,
    var double: Double = 2.4,
    var float: Float = 5F,
    var string: String = "125",
    var charArray: CharArray = charArrayOf('0', '2'),
    var booleanArray: BooleanArray = booleanArrayOf(false, true),
    var byteArray: ByteArray = byteArrayOf(0x4, 0x5),
    var shortArray: ShortArray = shortArrayOf(5, 8),
    var intArray: IntArray = intArrayOf(5, 555),
    var longArray: LongArray = longArrayOf(4698, 8546),
    var floatArray: FloatArray = floatArrayOf(4F, 88.22F),
    var doubleArray: DoubleArray = doubleArrayOf(8465.6845, 8956.645),
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EnabledArgs

        if (char != other.char) return false
        if (boolean != other.boolean) return false
        if (byte != other.byte) return false
        if (short != other.short) return false
        if (int != other.int) return false
        if (long != other.long) return false
        if (double != other.double) return false
        if (float != other.float) return false
        if (string != other.string) return false
        if (!charArray.contentEquals(other.charArray)) return false
        if (!booleanArray.contentEquals(other.booleanArray)) return false
        if (!byteArray.contentEquals(other.byteArray)) return false
        if (!shortArray.contentEquals(other.shortArray)) return false
        if (!intArray.contentEquals(other.intArray)) return false
        if (!longArray.contentEquals(other.longArray)) return false
        if (!floatArray.contentEquals(other.floatArray)) return false
        if (!doubleArray.contentEquals(other.doubleArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = char.hashCode()
        result = 31 * result + boolean.hashCode()
        result = 31 * result + byte
        result = 31 * result + short
        result = 31 * result + int
        result = 31 * result + long.hashCode()
        result = 31 * result + double.hashCode()
        result = 31 * result + float.hashCode()
        result = 31 * result + string.hashCode()
        result = 31 * result + charArray.contentHashCode()
        result = 31 * result + booleanArray.contentHashCode()
        result = 31 * result + byteArray.contentHashCode()
        result = 31 * result + shortArray.contentHashCode()
        result = 31 * result + intArray.contentHashCode()
        result = 31 * result + longArray.contentHashCode()
        result = 31 * result + floatArray.contentHashCode()
        result = 31 * result + doubleArray.contentHashCode()
        return result
    }
}
