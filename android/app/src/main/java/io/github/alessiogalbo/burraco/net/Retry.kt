// Regola degli intervalli di rete: dopo un successo si aspetta l'intervallo lungo, dopo un fallimento solo 2 ore.
package io.github.alessiogalbo.burraco.net

object Retry {
    const val FAIL_MS = 2 * 60 * 60 * 1000L

    /** Vero se a [now] si può riprovare: [last] = ultimo tentativo (0 = mai), [ok] = esito di quel tentativo. */
    fun due(last: Long, ok: Boolean, now: Long, everyMs: Long): Boolean {
        if (last <= 0L || last > now) return true // mai provato, oppure orologio tornato indietro
        return now - last >= if (ok) everyMs else FAIL_MS
    }
}
