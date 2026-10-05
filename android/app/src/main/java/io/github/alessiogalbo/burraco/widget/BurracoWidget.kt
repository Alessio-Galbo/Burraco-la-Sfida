// AppWidgetProvider unico: aggiorna ogni istanza secondo la sua modalità e dimensione, poi programma il prossimo giro.
package io.github.alessiogalbo.burraco.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import io.github.alessiogalbo.burraco.net.ScheduleSync
import io.github.alessiogalbo.burraco.ui.GameLauncher
import java.time.Instant

class BurracoWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { update(context, manager, it) }
        WidgetScheduler.scheduleNext(context)
        // Aggiornamento periodico: orari dal sito al massimo ogni 24 h, il ricevitore resta vivo fino alla fine.
        val pending = goAsync()
        if (!ScheduleSync.maybeRun(context) { pending.finish() }) pending.finish()
    }

    override fun onAppWidgetOptionsChanged(context: Context, manager: AppWidgetManager, id: Int, options: Bundle) {
        update(context, manager, id)
    }

    override fun onDeleted(context: Context, ids: IntArray) = WidgetPrefs.remove(context, ids)

    override fun onDisabled(context: Context) = WidgetScheduler.cancel(context)

    companion object {
        fun update(c: Context, manager: AppWidgetManager, id: Int) {
            val mode = WidgetPrefs.mode(c, id)
            val style = WidgetPrefs.style(c, id)
            val now = Instant.now()
            val click = GameLauncher.widgetClick(c)
            val views = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                WidgetViews.responsive(c, mode, now, click, style)
            } else {
                // Prima di Android 12: in verticale conta minWidth × maxHeight.
                val o = manager.getAppWidgetOptions(id)
                val w = o.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 250).toFloat()
                val h = o.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 110).toFloat()
                WidgetViews.build(c, mode, WidgetSizes.sizeFor(mode, w, h), now, click, style)
            }
            manager.updateAppWidget(id, views)
        }

        fun ids(c: Context): IntArray =
            AppWidgetManager.getInstance(c).getAppWidgetIds(ComponentName(c, BurracoWidget::class.java))

        /** Aggiorna tutte le istanze e riprogramma l'allarme. */
        fun updateAll(c: Context) {
            val m = AppWidgetManager.getInstance(c)
            ids(c).forEach { update(c, m, it) }
            WidgetScheduler.scheduleNext(c)
        }
    }
}
