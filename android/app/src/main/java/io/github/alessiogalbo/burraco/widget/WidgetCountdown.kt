// Countdown del widget (Chronometer live sotto 24 h, "2g 3h" sopra) e barretta di avanzamento nel colore evento.
package io.github.alessiogalbo.burraco.widget

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.widget.RemoteViews
import io.github.alessiogalbo.burraco.R
import io.github.alessiogalbo.burraco.schedule.EventStatus
import java.time.Duration
import java.time.Instant

object WidgetCountdown {
    fun bind(c: Context, rv: RemoteViews, s: EventStatus, now: Instant, st: WidgetStyle) {
        // Lo stato è calcolato su [now]: il bersaglio è sempre nel futuro, il base del Chronometer mai nel passato.
        val left = Duration.between(now, RefreshPlan.target(s)).coerceAtLeast(Duration.ZERO)
        val live = left < RefreshPlan.LIVE_LIMIT
        rv.setViewVisibility(R.id.chrono, if (live) View.VISIBLE else View.GONE)
        rv.setViewVisibility(R.id.`when`, if (live) View.GONE else View.VISIBLE)
        WidgetPaint.countdown(c, rv, s, st)
        if (live) {
            val base = SystemClock.elapsedRealtime() + left.toMillis()
            rv.setChronometer(R.id.chrono, base, c.getString(R.string.chrono_format), true)
            rv.setChronometerCountDown(R.id.chrono, true)
        } else {
            rv.setTextViewText(R.id.`when`, c.getString(R.string.days_hours, left.toDays(), left.toHours() % 24))
        }
    }

    /** Barretta: avanzamento della fase Torte o dell'evento in corso, vuota se in attesa; colori del tema (12+). */
    fun progress(c: Context, rv: RemoteViews, s: EventStatus, now: Instant, st: WidgetStyle) {
        val from = s.phaseFrom ?: s.from.takeIf { s.active }
        val to = s.phaseUntil ?: s.to.takeIf { s.active }
        val part = if (from == null || to == null) 0 else {
            (Duration.between(from, now).toMillis() * 1000 / Duration.between(from, to).toMillis().coerceAtLeast(1))
                .toInt().coerceIn(0, 1000)
        }
        // Solo per eventi in corso senza fasi (le Torte hanno la barra a segmenti); in attesa nessuna barra.
        rv.setViewVisibility(R.id.prog, if (s.active && s.phase == null) View.VISIBLE else View.GONE)
        rv.setProgressBar(R.id.prog, 1000, part, false)
        WidgetPaint.progress(c, rv, s, st)
    }
}
