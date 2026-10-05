// GET HTTPS minimale (HttpURLConnection, timeout 10 s): da chiamare solo fuori dal thread principale.
package io.github.alessiogalbo.burraco.net

import java.net.HttpURLConnection
import java.net.URL

object Http {
    private const val TIMEOUT_MS = 10_000
    private const val MAX_BYTES = 256 * 1024

    /** Corpo della risposta se 200, null per qualunque altro codice o errore di rete (mai eccezioni). */
    fun get(url: String, accept: String? = null, agent: String = "BlS-Tracker"): String? = runCatching {
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            conn.connectTimeout = TIMEOUT_MS
            conn.readTimeout = TIMEOUT_MS
            conn.instanceFollowRedirects = true
            conn.setRequestProperty("User-Agent", agent)
            if (accept != null) conn.setRequestProperty("Accept", accept)
            if (conn.responseCode != HttpURLConnection.HTTP_OK) return@runCatching null
            val bytes = conn.inputStream.use { it.readNBytesCompat(MAX_BYTES) }
            String(bytes, Charsets.UTF_8)
        } finally {
            conn.disconnect()
        }
    }.getOrNull()

    /** Legge al massimo [max] byte (readNBytes è solo da Android 13). */
    private fun java.io.InputStream.readNBytesCompat(max: Int): ByteArray {
        val out = java.io.ByteArrayOutputStream()
        val buf = ByteArray(8192)
        while (out.size() < max) {
            val n = read(buf, 0, minOf(buf.size, max - out.size()))
            if (n < 0) break
            out.write(buf, 0, n)
        }
        return out.toByteArray()
    }
}
