// Calcola i prossimi avvisi (inizio eventi e cambi fase Torte) secondo le preferenze, senza dipendere da Android.
package io.github.alessiogalbo.burraco.notify

import io.github.alessiogalbo.burraco.schedule.EventDef
import io.github.alessiogalbo.burraco.schedule.PhaseDef
import io.github.alessiogalbo.burraco.schedule.ScheduleEngine
import java.time.Duration
import java.time.Instant

/** Un avviso: [start] = inizio dell'evento o della fase, [phase] null = inizio evento. */
data class NotifyItem(val event: EventDef, val phase: PhaseDef?, val start: Instant)

class NotifyPlanner(
    private val engine: ScheduleEngine,
    private val events: List<EventDef>,
    private val eventOn: (String) -> Boolean,
    private val phasesOn: Boolean,
    private val lead: Duration,
) {
    /** Inizi abilitati (evento o fase, la prima fase coincide con l'evento) dopo [after]. */
    private fun candidates(after: Instant): List<NotifyItem> = events.flatMap { e ->
        engine.startsAfter(e, after).mapNotNull { (t, p) ->
            val isFirstPhase = p != null && p == e.phases.firstOrNull()
            when {
                p == null && eventOn(e.id) -> NotifyItem(e, null, t)
                p != null && !isFirstPhase && phasesOn -> NotifyItem(e, p, t)
                else -> null
            }
        }
    }

    /** Prossimo istante di avviso dopo [after] e gli avvisi che scattano in quell'istante (o null se nessuno). */
    fun next(after: Instant): Pair<Instant, List<NotifyItem>>? {
        val items = candidates(after.plus(lead)).map { it to it.start.minus(lead) }.filter { it.second.isAfter(after) }
        val at = items.minOfOrNull { it.second } ?: return null
        return at to items.filter { it.second == at }.map { it.first }
    }
}
