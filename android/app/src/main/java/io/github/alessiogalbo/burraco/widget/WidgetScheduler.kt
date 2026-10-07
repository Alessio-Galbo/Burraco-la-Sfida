// Sveglia esatta (Alarms) 1 s dopo il prossimo cambio di stato, fase, riepilogo, ora o soglia 24 h dei widget.
package io.github.alessiogalbo.burraco.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import io.github.alessiogalbo.burraco.Alarms
import io.github.alessiogalbo.burraco.schedule.ScheduleRepo
import java.time.Instant

object WidgetScheduler {
    private fun pending(c: Context): PendingIntent {
        val i = Intent(c, BurracoWidget::class.java).setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, BurracoWidget.ids(c))
        return PendingIntent.getBroadcast(c, 1, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    /** Da chiamare dopo OGNI disegno dei widget: sostituisce la sveglia precedente con quella del prossimo cambio. */
    fun scheduleNext(c: Context) {
        if (BurracoWidget.ids(c).isEmpty()) return cancel(c)
        val at = RefreshPlan.alarmAt(ScheduleRepo.engine(c), Instant.now())
        Alarms.set(c, at.toEpochMilli(), pending(c))
    }

    fun cancel(c: Context) = Alarms.cancel(c, pending(c))
}
