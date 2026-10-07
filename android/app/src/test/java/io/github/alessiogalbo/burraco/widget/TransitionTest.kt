// JUnit: ordine in corso/in attesa e sveglia 1 s dopo il cambio di stato (regressione countdown negativo).
package io.github.alessiogalbo.burraco.widget

import io.github.alessiogalbo.burraco.schedule.EventOrder
import io.github.alessiogalbo.burraco.schedule.Schedule
import io.github.alessiogalbo.burraco.schedule.ScheduleEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.time.Instant
import java.time.OffsetDateTime

class TransitionTest {
    private val root = File(System.getProperty("repoRoot") ?: "../..")
    private val engine = ScheduleEngine(Schedule.parse(File(root, "web/data/schedule.json").readText()))

    private fun t(s: String): Instant = OffsetDateTime.parse(s).toInstant()

    @Test
    fun runningByEndThenWaitingByStart() {
        val now = t("2026-10-09T16:00:01+02:00") // venerdì: Torte appena iniziate, Baule in corso fino a sab 02:00
        val order = EventOrder.ordered(engine.statusAll(now))
        assertEquals(listOf("baule", "torte", "corsa"), order.map { it.event.id })
        assertEquals("baule", EventOrder.first(engine.statusAll(now)).event.id)
        // Prima dell'inizio delle Torte: Baule in corso, poi Torte (ven 16:00), poi Corsa (lun 12:00).
        val before = EventOrder.ordered(engine.statusAll(t("2026-10-09T15:59:59+02:00")))
        assertEquals(listOf("baule", "torte", "corsa"), before.map { it.event.id })
        assertEquals(listOf(true, false, false), before.map { it.active })
        // Sabato 03:00: Baule finito, Torte in corso prima di tutto.
        val sat = EventOrder.ordered(engine.statusAll(t("2026-10-10T03:00:00+02:00")))
        assertEquals(listOf("torte", "corsa", "baule"), sat.map { it.event.id })
    }

    @Test
    fun alarmOneSecondAfterStart() {
        val now = t("2026-10-09T15:59:59+02:00")
        val start = t("2026-10-09T16:00:00+02:00")
        assertEquals(start, RefreshPlan.nextRefresh(engine, now))
        val alarm = RefreshPlan.alarmAt(engine, now)
        assertEquals(start.plusSeconds(1), alarm)
        val torte = engine.statusAll(alarm).first { it.event.id == "torte" }
        assertTrue(torte.active)
        assertTrue(torte.until!!.isAfter(alarm))
        // Allo scatto ogni countdown punta al futuro: nessun Chronometer negativo.
        engine.statusAll(alarm).forEach { assertTrue(RefreshPlan.target(it).isAfter(alarm)) }
    }

    @Test
    fun alarmAfterEndAndAlwaysInFuture() {
        val now = t("2026-10-10T01:59:30+02:00") // Baule finisce sab 02:00
        assertEquals(t("2026-10-10T02:00:01+02:00"), RefreshPlan.alarmAt(engine, now))
        assertEquals(false, engine.statusAll(RefreshPlan.alarmAt(engine, now)).first { it.event.id == "baule" }.active)
        var at = t("2026-10-05T00:00:00+02:00")
        repeat(200) { val next = RefreshPlan.alarmAt(engine, at); assertTrue(next.isAfter(at)); at = next }
    }
}
