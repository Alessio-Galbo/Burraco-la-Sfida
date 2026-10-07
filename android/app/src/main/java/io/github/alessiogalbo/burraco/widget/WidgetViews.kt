// Costruisce le RemoteViews del widget per modalità e taglia (mappa di taglie su Android 12+).
package io.github.alessiogalbo.burraco.widget

import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import io.github.alessiogalbo.burraco.R
import io.github.alessiogalbo.burraco.schedule.EventOrder
import io.github.alessiogalbo.burraco.schedule.EventStatus
import io.github.alessiogalbo.burraco.schedule.ScheduleRepo
import java.time.Instant

object WidgetViews {
    /** Evento da mostrare per "prossimo o in corso" o "singolo". */
    fun pick(all: List<EventStatus>, mode: WidgetMode): EventStatus = when (mode.kind) {
        WidgetMode.Kind.SINGLE -> all.firstOrNull { it.event.id == mode.eventId } ?: all.first()
        else -> EventOrder.first(all)
    }

    private fun singleLayout(size: WidgetSize) = when (size) {
        WidgetSize.MICRO -> R.layout.widget_micro
        WidgetSize.TINY -> R.layout.widget_small
        WidgetSize.STRIP -> R.layout.widget_strip
        WidgetSize.LINES, WidgetSize.MEDIUM -> R.layout.widget_medium
        WidgetSize.LARGE -> R.layout.widget_large
    }

    /** "Tutti": contenitore e riga per taglia (le righe si dividono lo spazio con layout_weight). */
    private fun allLayouts(size: WidgetSize) = when (size) {
        WidgetSize.MICRO -> R.layout.widget_rows to R.layout.row_micro
        WidgetSize.TINY -> R.layout.widget_rows to R.layout.row_compact
        WidgetSize.STRIP -> R.layout.widget_strip_all to R.layout.cell_strip
        WidgetSize.LINES -> R.layout.widget_rows to R.layout.row_line
        WidgetSize.MEDIUM -> R.layout.widget_rows to R.layout.row_event
        else -> R.layout.widget_rows to R.layout.row_event_large
    }

    fun build(
        c: Context, mode: WidgetMode, size: WidgetSize, now: Instant, click: PendingIntent?, st: WidgetStyle,
    ): RemoteViews {
        val all = ScheduleRepo.engine(c).statusAll(now)
        val rv = if (mode.kind == WidgetMode.Kind.ALL) rows(c, all, size, now, st) else {
            RemoteViews(c.packageName, singleLayout(size)).also { WidgetBind.bind(c, it, pick(all, mode), now, st) }
        }
        WidgetPaint.root(rv, st)
        if (click != null) rv.setOnClickPendingIntent(R.id.root, click)
        return rv
    }

    private fun rows(c: Context, all: List<EventStatus>, size: WidgetSize, now: Instant, st: WidgetStyle): RemoteViews {
        val (root, row) = allLayouts(size)
        val rv = RemoteViews(c.packageName, root)
        rv.removeAllViews(R.id.rows)
        EventOrder.ordered(all).forEach { s -> rv.addView(R.id.rows, RemoteViews(c.packageName, row).also { WidgetBind.bind(c, it, s, now, st) }) }
        return rv
    }

    /** Android 12+: una RemoteViews per punto di misura, il launcher sceglie quella adatta allo spazio. */
    fun responsive(c: Context, mode: WidgetMode, now: Instant, click: PendingIntent?, st: WidgetStyle): RemoteViews {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return build(c, mode, WidgetSize.MEDIUM, now, click, st)
        return RemoteViews(WidgetSizes.points(mode).associate { (pt, size) -> pt to build(c, mode, size, now, click, st) })
    }
}
