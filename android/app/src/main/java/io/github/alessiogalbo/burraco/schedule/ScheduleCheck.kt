// Validazione di uno schedule.json (es. scaricato dal sito): eventi attesi presenti, orari e fasi coerenti.
package io.github.alessiogalbo.burraco.schedule

import java.time.Instant

object ScheduleCheck {
    val REQUIRED = listOf("corsa", "baule", "torte")

    /** Schedule letto con lo stesso parser del motore, oppure null se non valido. */
    fun parseValid(json: String): Schedule? = runCatching { Schedule.parse(json) }.getOrNull()?.takeIf { valid(it) }

    fun valid(s: Schedule): Boolean {
        if (REQUIRED.any { s.event(it) == null } || s.event("torte")!!.phases.isEmpty()) return false
        if (!s.events.all { eventOk(it) }) return false
        return runCatching { ScheduleEngine(s).statusAll(Instant.now()) }.isSuccess
    }

    private fun timeOk(t: WeekTime) = t.day in 1..7 && t.minute in 0 until WeekTime.MINUTES_PER_DAY

    private fun after(from: WeekTime, to: WeekTime) = Math.floorMod(to.weekMinute - from.weekMinute, WeekTime.MINUTES_PER_WEEK)

    /** Fasi: la prima all'inizio evento, poi in ordine e tutte prima della fine; riepilogo dentro la settimana. */
    private fun eventOk(e: EventDef): Boolean {
        val times = listOf(e.start, e.end) + e.phases.map { it.start } + listOfNotNull(e.recap?.end)
        if (!times.all { timeOk(it) }) return false
        val len = after(e.start, e.end).takeIf { it > 0 } ?: WeekTime.MINUTES_PER_WEEK
        val offs = e.phases.map { after(e.start, it.start) }
        if (offs.isNotEmpty() && offs.first() != 0) return false
        if (offs.zipWithNext().any { (a, b) -> a >= b } || offs.any { it >= len }) return false
        val r = e.recap ?: return true
        return after(e.end, r.end).let { it > 0 && it + len <= WeekTime.MINUTES_PER_WEEK }
    }
}
