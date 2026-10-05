// Allarme inesatto (setAndAllowWhileIdle, niente permesso exact) al prossimo cambio di stato, fase, ora o soglia 24 h.
package io.github.alessiogalbo.burraco.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import io.github.alessiogalbo.burraco.schedule.ScheduleRepo
import java.time.Duration
import java.time.Instant

object WidgetScheduler {
    private const val MARGIN_MS = 1_000L

    /** Prossimo istante in cui il contenuto dei widget cambia. */
    fun nextRefresh(c: Context, now: Instant): Instant {
        val engine = ScheduleRepo.engine(c)
        // Fine del riepilogo Torte: la barra e la sottoriga "Riepilogo" spariscono.
        val recapEnds = engine.statusAll(now).mapNotNull { engine.recap(it.event, now)?.second }
        val candidates = recapEnds + engine.statusAll(now).flatMap { s ->
            val t = WidgetCountdown.target(s)
            // Oltre 24 h il testo "2g 3h" cambia quando le ore intere rimaste scendono di uno.
            val h = Duration.between(now, t).toHours()
            listOf(s.nextChange, t, t.minus(WidgetCountdown.LIVE_LIMIT), t.minus(Duration.ofHours(h)), t.minus(Duration.ofHours(h - 1)))
        }
        return candidates.filter { it.isAfter(now) }.min()
    }

    private fun pending(c: Context): PendingIntent {
        val i = Intent(c, BurracoWidget::class.java).setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, BurracoWidget.ids(c))
        return PendingIntent.getBroadcast(c, 1, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    fun scheduleNext(c: Context) {
        if (BurracoWidget.ids(c).isEmpty()) return cancel(c)
        val at = nextRefresh(c, Instant.now()).toEpochMilli() + MARGIN_MS
        c.getSystemService(AlarmManager::class.java)
            .setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pending(c))
    }

    fun cancel(c: Context) {
        c.getSystemService(AlarmManager::class.java).cancel(pending(c))
    }
}
