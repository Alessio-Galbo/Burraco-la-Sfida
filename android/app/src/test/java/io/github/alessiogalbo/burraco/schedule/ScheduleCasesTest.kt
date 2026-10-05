// JUnit: il motore orari deve passare tutti i casi condivisi di tests/schedule-cases.json (confronto istanti).
package io.github.alessiogalbo.burraco.schedule

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.time.Instant
import java.time.OffsetDateTime

class ScheduleCasesTest {
    private val root = File(System.getProperty("repoRoot") ?: "../..")
    private val schedule = Schedule.parse(File(root, "web/data/schedule.json").readText())
    private val engine = ScheduleEngine(schedule)

    private fun t(s: String): Instant = OffsetDateTime.parse(s).toInstant()

    @Test
    fun sharedCases() {
        val cases = JSONObject(File(root, "tests/schedule-cases.json").readText()).getJSONArray("cases")
        assertTrue(cases.length() > 0)
        var checked = 0
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val name = c.getString("name")
            val now = t(c.getString("now"))
            val expect = c.getJSONObject("expect")
            for (id in expect.keys()) {
                val exp = expect.getJSONObject(id)
                val st = engine.status(schedule.event(id)!!, now)
                val msg = "$name / $id"
                assertEquals(msg, exp.getBoolean("active"), st.active)
                if (exp.has("until")) assertEquals(msg, t(exp.getString("until")), st.until)
                if (exp.has("startsAt")) assertEquals(msg, t(exp.getString("startsAt")), st.startsAt)
                if (exp.has("phase")) assertEquals(msg, exp.getString("phase"), st.phase?.id)
                else if (!st.active) assertNull(msg, st.phase)
                if (exp.has("phaseUntil")) assertEquals(msg, t(exp.getString("phaseUntil")), st.phaseUntil)
                checked++
            }
        }
        println("Casi verificati: ${cases.length()} istanti, $checked stati evento")
    }

    @Test
    fun nextChangeIsInFuture() {
        val now = t("2026-10-05T20:33:11+02:00")
        assertEquals(t("2026-10-07T02:00:00+02:00"), engine.nextChange(now))
        val starts = engine.startsAfter(engine.statusAll(now).last().event, now)
        assertEquals(t("2026-10-09T16:00:00+02:00"), starts.first().first)
        assertTrue(starts.all { it.first.isAfter(now) })
    }
}
