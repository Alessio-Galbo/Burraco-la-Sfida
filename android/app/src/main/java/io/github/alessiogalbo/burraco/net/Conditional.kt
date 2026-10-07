// Richiesta condizionale (If-None-Match / If-Modified-Since) e applicazione della risposta al file locale: 304 = successo.
package io.github.alessiogalbo.burraco.net

import java.io.File

object Conditional {
    /** Validatori dell'ultima risposta 200 salvata. */
    data class Validators(val etag: String?, val lastModified: String?)

    enum class Outcome { FAILED, NOT_MODIFIED, SAME, WRITTEN }

    /** Header condizionali; nessuno se il file locale manca (serve il corpo completo). */
    fun headers(v: Validators, haveFile: Boolean): Map<String, String> = if (!haveFile) emptyMap() else buildMap {
        v.etag?.takeIf { it.isNotBlank() }?.let { put("If-None-Match", it) }
        v.lastModified?.takeIf { it.isNotBlank() }?.let { put("If-Modified-Since", it) }
    }

    /**
     * 304 con file presente = nulla da scaricare (file invariato); 200 valido = scrive (atomico) solo se diverso;
     * qualunque altro caso = fallimento (si riprova tra 2 h). [valid] valida il corpo con il parser del motore.
     */
    fun apply(r: HttpResult?, file: File, valid: (String) -> Boolean): Outcome {
        if (r == null) return Outcome.FAILED
        if (r.code == 304) return if (file.exists()) Outcome.NOT_MODIFIED else Outcome.FAILED
        val body = r.body?.takeIf { r.code == 200 && valid(it) } ?: return Outcome.FAILED
        if (file.exists() && file.readText(Charsets.UTF_8) == body) return Outcome.SAME
        val tmp = File(file.parentFile, file.name + ".tmp")
        tmp.writeText(body, Charsets.UTF_8)
        if (!tmp.renameTo(file) && !(file.delete() && tmp.renameTo(file))) return Outcome.FAILED.also { tmp.delete() }
        return Outcome.WRITTEN
    }
}
