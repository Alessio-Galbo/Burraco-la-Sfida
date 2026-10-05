// JUnit: riepilogo Torte (dom 22:00 → lun 13:00) e validazione degli orari scaricati; lo stato attivo non cambia.
package io.github.alessiogalbo.burraco.schedule

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.time.Instant
import java.time.OffsetDateTime

class RecapTest {
    private val root = File(System.getProperty("repoRoot") ?: "../..")
    private val json = File(root, "web/data/schedule.json").readText()
    private val schedule = Schedule.parse(json)
    private val engine = ScheduleEngine(schedule)
    private val torte = schedule.event("torte")!!

    private fun t(s: String): Instant = OffsetDateTime.parse(s).toInstant()

    @Test
    fun recapWindow() {
        assertEquals("riepilogo", torte.recap?.id)
        val w = engine.recap(torte, t("2026-10-12T10:00:00+02:00"))
        assertEquals(t("2026-10-11T22:00:00+02:00") to t("2026-10-12T13:00:00+02:00"), w)
        assertNotNull(engine.recap(torte, t("2026-10-11T22:00:00+02:00")))
        assertNull(engine.recap(torte, t("2026-10-11T21:59:00+02:00")))
        assertNull(engine.recap(torte, t("2026-10-12T13:00:00+02:00")))
        assertNull(engine.recap(torte, t("2026-10-10T10:00:00+02:00")))
        assertNull(engine.recap(schedule.event("corsa")!!, t("2026-10-12T10:00:00+02:00")))
    }

    @Test
    fun recapKeepsStatusWaiting() {
        val st = engine.status(torte, t("2026-10-12T10:00:00+02:00"))
        assertFalse(st.active)
        assertNull(st.phase)
        assertEquals(t("2026-10-16T16:00:00+02:00"), st.startsAt)
        // Nessun inizio (quindi nessuna notifica) dentro il riepilogo.
        val starts = engine.startsAfter(torte, t("2026-10-11T22:00:00+02:00")).map { it.first }
        assertTrue(starts.none { it.isBefore(t("2026-10-12T13:00:00+02:00")) })
    }

    @Test
    fun recapAcrossDstChange() {
        // 25 ottobre 2026: fine ora legale nella notte; il riepilogo resta dom 22:00 → lun 13:00 ora italiana.
        val w = engine.recap(torte, t("2026-10-26T12:00:00+01:00"))
        assertEquals(t("2026-10-25T22:00:00+01:00") to t("2026-10-26T13:00:00+01:00"), w)
    }

    @Test
    fun downloadedScheduleValidation() {
        assertNotNull(ScheduleCheck.parseValid(json))
        assertNull(ScheduleCheck.parseValid("<html>404</html>"))
        assertNull(ScheduleCheck.parseValid(json.replace("\"id\": \"baule\"", "\"id\": \"altro\"")))
        // Fasi fuori ordine: crema prima di forno.
        assertNull(ScheduleCheck.parseValid(json.replace("\"day\": 6, \"time\": \"16:00\"", "\"day\": 5, \"time\": \"20:00\"")))
        assertNull(ScheduleCheck.parseValid(json.replace("\"time\": \"13:00\"", "\"time\": \"25:00\"")))
    }
}
