package cz.jscelectronics.adeon.utilities

import java.security.NoSuchAlgorithmException
import java.util.*

fun computeMd5(s: String): String {
    try {
        // Create MD5 Hash
        val digest = java.security.MessageDigest.getInstance("MD5")
        digest.update(s.toByteArray())
        val messageDigest = digest.digest()

        // Create Hex String
        return messageDigest.joinToString(separator = "") {
            String.format("%02X", it).lowercase(Locale.US)
        }
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }

    return ""
}