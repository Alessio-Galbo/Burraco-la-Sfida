// Programma con sveglia esatta (Alarms, ricade su inesatta se non consentita) il prossimo avviso; la cancella se nulla è attivo.
package io.github.alessiogalbo.burraco.notify

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.github.alessiogalbo.burraco.Alarms
import io.github.alessiogalbo.burraco.schedule.ScheduleRepo
import java.time.Duration
import java.time.Instant

object NotifyScheduler {
    const val EXTRA_AT = "at"

    fun planner(c: Context): NotifyPlanner = NotifyPlanner(
        ScheduleRepo.engine(c),
        ScheduleRepo.schedule(c).events,
        { NotifyPrefs.event(c, it) },
        NotifyPrefs.phases(c),
        Duration.ofMinutes(NotifyPrefs.leadMinutes(c).toLong()),
    )

    private fun pending(c: Context, at: Long): PendingIntent {
        val i = Intent(c, NotifyReceiver::class.java).putExtra(EXTRA_AT, at)
        return PendingIntent.getBroadcast(c, 2, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    /** Riprogramma dal momento [after] (default adesso). */
    fun reschedule(c: Context, after: Instant = Instant.now()) {
        val next = planner(c).next(after) ?: return Alarms.cancel(c, pending(c, 0))
        val at = next.first.toEpochMilli()
        Alarms.set(c, at, pending(c, at))
    }
}
