// Programma con AlarmManager (inesatto, consentito in idle) il prossimo avviso; lo cancella se nulla è attivo.
package io.github.alessiogalbo.burraco.notify

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
        val am = c.getSystemService(AlarmManager::class.java)
        val next = planner(c).next(after)
        if (next == null) {
            am.cancel(pending(c, 0))
            return
        }
        val at = next.first.toEpochMilli()
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pending(c, at))
    }
}
