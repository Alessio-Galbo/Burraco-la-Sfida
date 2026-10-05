// Motore orari puro (java.time): stato attivo/prossimo inizio e fase corrente per ogni evento.
package io.github.alessiogalbo.burraco.schedule

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

/** Stato di un evento: se attivo [until] = fine, altrimenti [startsAt] = prossimo inizio. */
data class EventStatus(
    val event: EventDef,
    val active: Boolean,
    val until: Instant?,
    val startsAt: Instant?,
    val phase: PhaseDef? = null,
    val phaseUntil: Instant? = null,
    val phaseFrom: Instant? = null,
    /** Inizio e fine dell'occorrenza mostrata (in corso o prossima). */
    val from: Instant? = null,
    val to: Instant? = null,
) {
    /** Istante del prossimo cambio di stato o di fase. */
    val nextChange: Instant get() = phaseUntil ?: until ?: startsAt!!
    /** Fine evento (se attivo) o inizio (se no) non confermati. */
    val estimated: Boolean get() = if (active) event.end.estimated else event.start.estimated
    /** La fase finisce con l'evento e quindi a un orario stimato. */
    val phaseEstimated: Boolean get() = phaseUntil != null && phaseUntil == until && event.end.estimated
}

class ScheduleEngine(private val schedule: Schedule) {
    private val zone = schedule.zone

    /** Occorrenza [inizio, fine) che inizia nella settimana spostata di [weekOffset] rispetto a quella di [now]. */
    private fun occurrence(e: EventDef, now: Instant, weekOffset: Long): Pair<LocalDateTime, LocalDateTime> {
        val monday = now.atZone(zone).toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val start = monday.atStartOfDay().plusWeeks(weekOffset).plusMinutes(e.start.weekMinute.toLong())
        return start to start.plusMinutes(span(e.start, e.end).toLong())
    }

    /** Minuti da [from] a [to] in avanti nel ciclo settimanale (mai zero). */
    private fun span(from: WeekTime, to: WeekTime): Int {
        val d = Math.floorMod(to.weekMinute - from.weekMinute, WeekTime.MINUTES_PER_WEEK)
        return if (d == 0) WeekTime.MINUTES_PER_WEEK else d
    }

    private fun LocalDateTime.instant(): Instant = atZone(zone).toInstant()

    /** Prima occorrenza (inizio, fine) con fine > [now]. */
    private fun current(e: EventDef, now: Instant): Pair<LocalDateTime, LocalDateTime> =
        (-1L..1L).map { occurrence(e, now, it) }.first { it.second.instant().isAfter(now) }

    fun status(e: EventDef, now: Instant): EventStatus {
        val (start, end) = current(e, now)
        val (a, b) = start.instant() to end.instant()
        if (a.isAfter(now)) return EventStatus(e, false, null, a, from = a, to = b)
        val starts = e.phases.map { start.plusMinutes(span(e.start, it.start).toLong() % WeekTime.MINUTES_PER_WEEK) }
        val idx = starts.indexOfLast { !it.instant().isAfter(now) }
        if (idx < 0) return EventStatus(e, true, b, null, from = a, to = b)
        val phaseEnd = starts.getOrNull(idx + 1) ?: end
        return EventStatus(e, true, b, null, e.phases[idx], phaseEnd.instant(), starts[idx].instant(), a, b)
    }

    /** Finestra [fine evento, recap.end) che contiene [now], altrimenti null. Lo stato dell'evento non cambia. */
    fun recap(e: EventDef, now: Instant): Pair<Instant, Instant>? {
        val r = e.recap ?: return null
        return (-1L..0L).map { occurrence(e, now, it).second }
            .map { it.instant() to it.plusMinutes(span(e.end, r.end).toLong()).instant() }
            .firstOrNull { !now.isBefore(it.first) && now.isBefore(it.second) }
    }

    fun statusAll(now: Instant): List<EventStatus> = schedule.events.map { status(it, now) }

    /** Istante del prossimo cambio (stato o fase) di qualunque evento. */
    fun nextChange(now: Instant): Instant = statusAll(now).minOf { it.nextChange }

    /** Inizi (evento e fasi) strettamente dopo [after], in ordine: coppie (istante, fase o null = inizio evento). */
    fun startsAfter(e: EventDef, after: Instant): List<Pair<Instant, PhaseDef?>> {
        val out = mutableListOf<Pair<Instant, PhaseDef?>>()
        for (w in 0L..2L) {
            val (start, _) = occurrence(e, after, w - 1)
            out += start.instant() to null
            e.phases.forEach { out += start.plusMinutes(span(e.start, it.start).toLong() % WeekTime.MINUTES_PER_WEEK).instant() to it }
        }
        return out.filter { it.first.isAfter(after) }.sortedBy { it.first }
    }
}
