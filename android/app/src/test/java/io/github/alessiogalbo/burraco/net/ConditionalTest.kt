// JUnit: richiesta condizionale degli orari (ETag / Last-Modified), 304 = successo senza toccare il file locale.
package io.github.alessiogalbo.burraco.net

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.Files

class ConditionalTest {
    private val dir: File = Files.createTempDirectory("bls").toFile()
    private val file = File(dir, "schedule.json")
    private val ok: (String) -> Boolean = { it.startsWith("{") }

    @Test
    fun headersOnlyWithLocalFile() {
        val v = Conditional.Validators("\"abc\"", "Wed, 07 Oct 2026 10:00:00 GMT")
        assertEquals(emptyMap<String, String>(), Conditional.headers(v, haveFile = false))
        assertEquals(mapOf("If-None-Match" to "\"abc\"", "If-Modified-Since" to v.lastModified), Conditional.headers(v, true))
        assertEquals(emptyMap<String, String>(), Conditional.headers(Conditional.Validators(null, null), true))
    }

    @Test
    fun notModifiedKeepsFile() {
        file.writeText("{old}")
        val stamp = file.lastModified()
        assertEquals(Conditional.Outcome.NOT_MODIFIED, Conditional.apply(HttpResult(304, null), file, ok))
        assertEquals("{old}", file.readText())
        assertEquals(stamp, file.lastModified())
        // 304 senza copia locale non vale come successo.
        file.delete()
        assertEquals(Conditional.Outcome.FAILED, Conditional.apply(HttpResult(304, null), file, ok))
    }

    @Test
    fun fullResponses() {
        assertEquals(Conditional.Outcome.FAILED, Conditional.apply(null, file, ok))
        assertEquals(Conditional.Outcome.FAILED, Conditional.apply(HttpResult(500, null), file, ok))
        assertEquals(Conditional.Outcome.FAILED, Conditional.apply(HttpResult(200, "broken"), file, ok))
        assertTrue(!file.exists())
        assertEquals(Conditional.Outcome.WRITTEN, Conditional.apply(HttpResult(200, "{a}", "e1"), file, ok))
        assertEquals(Conditional.Outcome.SAME, Conditional.apply(HttpResult(200, "{a}", "e1"), file, ok))
        assertEquals(Conditional.Outcome.WRITTEN, Conditional.apply(HttpResult(200, "{b}", "e2"), file, ok))
        assertEquals("{b}", file.readText())
    }
}
