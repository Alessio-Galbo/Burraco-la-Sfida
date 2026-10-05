// Riempie le RemoteViews di un evento in qualunque layout: le viste assenti nel layout vengono ignorate.
package io.github.alessiogalbo.burraco.widget

import android.content.Context
import android.view.View
import android.widget.RemoteViews
import io.github.alessiogalbo.burraco.R
import io.github.alessiogalbo.burraco.schedule.EventStatus
import io.github.alessiogalbo.burraco.ui.Texts
import java.time.Duration
import java.time.Instant

object WidgetBind {
    /** Testo con punto colorato davanti (verde in corso, accento in arrivo). */
    private fun dotted(rv: RemoteViews, id: Int, text: String, s: EventStatus, st: WidgetStyle) {
        rv.setTextViewText(id, text)
        rv.setTextViewCompoundDrawablesRelative(id, WidgetPaint.dot(s, st), 0, 0, 0)
    }

    /** "ven 16:00 → ≈lun 13:00": inizio e fine dell'occorrenza mostrata. */
    private fun span(c: Context, s: EventStatus): String =
        c.getString(
            R.string.span_fmt, Texts.time(c, s.from!!, s.event.start.estimated), Texts.time(c, s.to!!, s.event.end.estimated),
            Duration.between(s.from, s.to).let { c.getString(R.string.days_hours, it.toDays(), it.toHours() % 24) },
        )

    /**
     * Id usati dai layout: icon, name, accent, dot, status (stato), sub (fase o orario), dsub (orario),
     * pname (nome fase), span (inizio → fine), label ("fine tra"), line
     * (fase o "fine tra · ora"), prog (barretta), segs, chrono/when.
     */
    fun bind(c: Context, rv: RemoteViews, s: EventStatus, now: Instant, st: WidgetStyle) {
        val detail = Texts.detail(c, s)
        rv.setTextViewText(R.id.icon, s.event.icon)
        rv.setTextViewText(R.id.name, Texts.eventName(c, s.event))
        rv.setTextViewText(R.id.short_name, Texts.shortName(c, s.event))
        rv.setImageViewResource(R.id.dot, WidgetPaint.dot(s, st))
        dotted(rv, R.id.status, Texts.status(c, s), s, st)
        dotted(rv, R.id.sub, s.phase?.let { Texts.phaseName(c, it) } ?: detail, s, st)
        dotted(rv, R.id.dsub, detail, s, st)
        rv.setViewVisibility(R.id.pname, if (s.phase == null) View.GONE else View.VISIBLE)
        rv.setTextViewText(R.id.pname, s.phase?.let { Texts.phaseName(c, it) } ?: "")
        rv.setTextViewText(R.id.span, span(c, s))
        dotted(rv, R.id.label, Texts.label(c, s), s, st)
        rv.setTextViewText(R.id.line, Texts.line(c, s))
        WidgetPaint.event(c, rv, s, st)
        WidgetCountdown.progress(c, rv, s, now, st)
        WidgetSegments.bind(c, rv, s, now, st)
        WidgetCountdown.bind(c, rv, s, now, st)
        recap(c, rv, s, now, st)
    }

    /** Riepilogo Torte in corso: l'evento resta "in attesa", sottoriga grigia "Riepilogo · fino a lun 13:00". */
    private fun recap(c: Context, rv: RemoteViews, s: EventStatus, now: Instant, st: WidgetStyle) {
        val (_, end) = WidgetSegments.recapNow(c, s, now, st) ?: return
        val text = c.getString(R.string.recap_line_fmt, Texts.time(c, end))
        rv.setTextViewText(R.id.line, text)
        dotted(rv, R.id.sub, text, s, st)
    }
}
