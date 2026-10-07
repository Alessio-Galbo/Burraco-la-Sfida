// GET HTTPS minimale (HttpURLConnection, timeout 10 s, anche condizionale): da chiamare solo fuori dal thread principale.
package io.github.alessiogalbo.burraco.net

import java.net.HttpURLConnection
import java.net.URL

/** Esito HTTP: [body] solo con 200; [etag] e [lastModified] per la richiesta condizionale successiva. */
data class HttpResult(val code: Int, val body: String?, val etag: String? = null, val lastModified: String? = null)

object Http {
    private const val TIMEOUT_MS = 10_000
    private const val MAX_BYTES = 256 * 1024

    /** Corpo della risposta se 200, null per qualunque altro codice o errore di rete (mai eccezioni). */
    fun get(url: String, accept: String? = null, agent: String = "BlS-Tracker"): String? =
        request(url, accept, agent)?.takeIf { it.code == HttpURLConnection.HTTP_OK }?.body

    /** Risposta con codice, corpo (solo se 200) e validatori ETag / Last-Modified; null se errore di rete. */
    fun request(
        url: String, accept: String? = null, agent: String = "BlS-Tracker", headers: Map<String, String> = emptyMap(),
    ): HttpResult? = runCatching {
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            conn.connectTimeout = TIMEOUT_MS
            conn.readTimeout = TIMEOUT_MS
            conn.instanceFollowRedirects = true
            conn.useCaches = false
            conn.setRequestProperty("User-Agent", agent)
            if (accept != null) conn.setRequestProperty("Accept", accept)
            headers.forEach { (k, v) -> conn.setRequestProperty(k, v) }
            val code = conn.responseCode
            val body = if (code != HttpURLConnection.HTTP_OK) null
            else String(conn.inputStream.use { it.readNBytesCompat(MAX_BYTES) }, Charsets.UTF_8)
            HttpResult(code, body, conn.getHeaderField("ETag"), conn.getHeaderField("Last-Modified"))
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
