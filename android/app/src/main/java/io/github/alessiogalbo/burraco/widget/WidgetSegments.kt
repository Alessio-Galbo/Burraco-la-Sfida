// Barra delle fasi Torte: segmenti proporzionali alla durata, avanzamento nella fase corrente, riepilogo facoltativo.
package io.github.alessiogalbo.burraco.widget

import android.content.Context
import android.view.View
import android.widget.RemoteViews
import io.github.alessiogalbo.burraco.R
import io.github.alessiogalbo.burraco.schedule.EventDef
import io.github.alessiogalbo.burraco.schedule.EventStatus
import io.github.alessiogalbo.burraco.schedule.ScheduleRepo
import io.github.alessiogalbo.burraco.schedule.WeekTime
import io.github.alessiogalbo.burraco.widget.SegmentsBitmap.Layer
import java.time.Duration
import java.time.Instant

object WidgetSegments {
    private fun minutes(from: WeekTime, to: WeekTime) = Math.floorMod(to.weekMinute - from.weekMinute, WeekTime.MINUTES_PER_WEEK)

    /** Minuti di ogni fase (fino alla successiva, l'ultima fino alla fine evento) e, se [recap], del riepilogo. */
    fun durations(e: EventDef, recap: Boolean = false): List<Int> = e.phases.indices.map { i ->
        minutes(e.phases[i].start, e.phases.getOrNull(i + 1)?.start ?: e.end)
    } + listOfNotNull(e.recap?.takeIf { recap }?.let { minutes(e.end, it.end) })

    private fun fraction(from: Instant?, to: Instant?, now: Instant): Float {
        if (from == null || to == null) return 0f
        return Duration.between(from, now).toMillis().toFloat() / Duration.between(from, to).toMillis().coerceAtLeast(1)
    }

    /** Finestra del riepilogo in corso, solo se lo stile lo mostra. */
    fun recapNow(c: Context, s: EventStatus, now: Instant, st: WidgetStyle): Pair<Instant, Instant>? =
        if (st.recap && s.event.recap != null) ScheduleRepo.engine(c).recap(s.event, now) else null

    fun bind(c: Context, rv: RemoteViews, s: EventStatus, now: Instant, st: WidgetStyle) {
        val idx = s.phase?.let { s.event.phases.indexOf(it) } ?: -1
        val recap = st.recap && s.event.recap != null
        val inRecap = recapNow(c, s, now, st)
        val show = idx >= 0 || inRecap != null
        rv.setViewVisibility(R.id.segs, if (show) View.VISIBLE else View.GONE)
        if (!show) return
        val mins = durations(s.event, recap)
        val (cur, part) = if (inRecap != null) mins.lastIndex to fraction(inRecap.first, inRecap.second, now)
        else idx to fraction(s.phaseFrom, s.phaseUntil, now)
        // Bitmap bianche tinte con le risorse del tema: traccia e riempimento seguono chiaro/scuro (Sistema).
        rv.setImageViewBitmap(R.id.segs_track, SegmentsBitmap.draw(mins, recap, cur, part, Layer.TRACK))
        rv.setImageViewBitmap(R.id.segs_fill, SegmentsBitmap.draw(mins, recap, cur, part, Layer.FILL))
        WidgetPaint.color(c, rv, R.id.segs_track, "setColorFilter", st.palette.track)
        WidgetPaint.color(c, rv, R.id.segs_fill, "setColorFilter", st.palette.event(s.event.id))
        rv.setViewVisibility(R.id.segs_recap, if (recap) View.VISIBLE else View.GONE)
        if (!recap) return
        rv.setImageViewBitmap(R.id.segs_recap, SegmentsBitmap.draw(mins, true, cur, part, Layer.RECAP))
        WidgetPaint.color(c, rv, R.id.segs_recap, "setColorFilter", st.palette.dim)
    }
}
