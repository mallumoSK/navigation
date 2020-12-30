package tk.mallumo.compose.navigation.ksp

import java.security.MessageDigest

/**
 * Hashing Utils
 * @author Sam Clarke <www.samclarke.com>
 * @license MIT
 */
@Suppress("unused")
object HashUtils {
    fun String.sha512() = hashString("SHA-512", this)

    fun String.sha256() = hashString("SHA-256", this)

    fun String.sha1() = hashString("SHA-1", this)

    /**
     * Supported algorithms on Android:
     *
     * Algorithm	Supported API Levels
     * MD5          1+
     * SHA-1	    1+
     * SHA-224	    1-8,22+
     * SHA-256	    1+
     * SHA-384	    1+
     * SHA-512	    1+
     */
    private fun hashString(type: String, input: String): String {
        val HEX_CHARS = "0123456789ABCDEF"
        val bytes = MessageDigest.getInstance(type)
            .digest(input.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        return result.toString()
    }
}