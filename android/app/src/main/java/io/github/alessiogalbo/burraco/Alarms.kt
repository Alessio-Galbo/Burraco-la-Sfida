// Sveglie esatte anche in Doze (USE_EXACT_ALARM / SCHEDULE_EXACT_ALARM); se non consentite ricade su quelle inesatte.
package io.github.alessiogalbo.burraco

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build

object Alarms {
    /** Vero se il sistema permette le sveglie esatte (sempre prima di Android 12). */
    fun exactAllowed(am: AlarmManager): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || am.canScheduleExactAlarms()

    /** Programma [pi] a [atMs] (RTC, sveglia il telefono): esatta se consentito, altrimenti inesatta. */
    fun set(c: Context, atMs: Long, pi: PendingIntent) {
        val am = c.getSystemService(AlarmManager::class.java)
        val exact = exactAllowed(am) && runCatching {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMs, pi)
        }.isSuccess
        if (!exact) am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMs, pi)
    }

    fun cancel(c: Context, pi: PendingIntent) = c.getSystemService(AlarmManager::class.java).cancel(pi)
}
