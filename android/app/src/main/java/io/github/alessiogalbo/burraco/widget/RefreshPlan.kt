// Calcolo puro (testabile in JUnit) del prossimo istante in cui i widget cambiano e dell'ora della sveglia (+1 s).
package io.github.alessiogalbo.burraco.widget

import io.github.alessiogalbo.burraco.schedule.EventStatus
import io.github.alessiogalbo.burraco.schedule.ScheduleEngine
import java.time.Duration
import java.time.Instant

object RefreshPlan {
    /** Sotto questa soglia il countdown è un Chronometer live; sopra "2g 3h" (aggiornato ogni ora). */
    val LIVE_LIMIT: Duration = Duration.ofHours(24)

    /** La sveglia scatta 1 s DOPO il cambio, così lo stato ricalcolato è già quello nuovo. */
    val MARGIN: Duration = Duration.ofSeconds(1)

    /** Bersaglio del countdown: fine evento se attivo, altrimenti inizio. */
    fun target(s: EventStatus): Instant = if (s.active) s.until!! else s.startsAt!!

    /** Prossimo istante in cui il contenuto dei widget cambia (stato, fase, fine riepilogo, soglia 24 h, ora intera). */
    fun nextRefresh(engine: ScheduleEngine, now: Instant): Instant {
        val all = engine.statusAll(now)
        // Fine del riepilogo Torte: la barra e la sottoriga "Riepilogo" spariscono.
        val recapEnds = all.mapNotNull { engine.recap(it.event, now)?.second }
        val candidates = recapEnds + all.flatMap { s ->
            val t = target(s)
            // Oltre 24 h il testo "2g 3h" cambia quando le ore intere rimaste scendono di uno.
            val h = Duration.between(now, t).toHours()
            listOf(s.nextChange, t, t.minus(LIVE_LIMIT), t.minus(Duration.ofHours(h)), t.minus(Duration.ofHours(h - 1)))
        }
        return candidates.filter { it.isAfter(now) }.min()
    }

    /** Istante della prossima sveglia dei widget. */
    fun alarmAt(engine: ScheduleEngine, now: Instant): Instant = nextRefresh(engine, now).plus(MARGIN)
}
