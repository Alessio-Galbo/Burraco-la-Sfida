// Orari in uso: schedule.json scaricato dal sito (filesDir) se valido e non più vecchio, altrimenti quello negli asset.
package io.github.alessiogalbo.burraco.schedule

import android.content.Context
import java.io.File

object ScheduleRepo {
    const val FILE = "schedule.json"
    @Volatile private var cached: ScheduleEngine? = null
    @Volatile private var schedule: Schedule? = null

    fun schedule(context: Context): Schedule = schedule ?: synchronized(this) {
        schedule ?: load(context).also { schedule = it }
    }

    fun engine(context: Context): ScheduleEngine =
        cached ?: ScheduleEngine(schedule(context)).also { cached = it }

    /** File scaricato (può non esistere). */
    fun downloaded(context: Context): File = File(context.filesDir, FILE)

    /** Dimentica gli orari in memoria: la prossima lettura ricarica file scaricato o asset. */
    fun reset() = synchronized(this) { schedule = null; cached = null }

    private fun load(c: Context): Schedule {
        val bundled = Schedule.parse(c.assets.open(FILE).bufferedReader(Charsets.UTF_8).use { it.readText() })
        val online = runCatching { downloaded(c).takeIf { it.exists() }?.readText(Charsets.UTF_8) }.getOrNull()
            ?.let { ScheduleCheck.parseValid(it) }
        // Dopo un aggiornamento dell'app gli asset possono essere più recenti della copia scaricata.
        return online?.takeIf { it.updated >= bundled.updated } ?: bundled
    }
}
