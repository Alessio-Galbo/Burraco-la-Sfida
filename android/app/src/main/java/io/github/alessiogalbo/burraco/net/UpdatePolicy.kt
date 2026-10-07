// Regole pure del controllo nuova versione: intervalli (apertura 1 h, background 24 h) e notifica una sola volta per versione.
package io.github.alessiogalbo.burraco.net

object UpdatePolicy {
    /** All'apertura dell'app: al massimo un controllo all'ora (2 h dopo un errore, vedi [Retry]). */
    const val OPEN_MS = 60 * 60 * 1000L

    /** In background (widget o sveglia giornaliera): una volta al giorno. */
    const val DAILY_MS = 24 * OPEN_MS

    /** Vero se [tag] è più nuova di [current] e non è già stata notificata ([lastNotified]). */
    fun shouldNotify(tag: String, current: String, lastNotified: String?): Boolean =
        Semver.newer(tag, current) && tag != lastNotified
}
