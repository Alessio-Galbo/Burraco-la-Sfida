// Applica lo stile a una RemoteViews: fondi, colori di testi e barre, punti di stato, icona emoji o SVG.
package io.github.alessiogalbo.burraco.widget

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import io.github.alessiogalbo.burraco.R
import io.github.alessiogalbo.burraco.schedule.EventStatus

object WidgetPaint {
    private val dynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    /** Colore da risorsa: su Android 12+ lo risolve chi mostra il widget (Sistema segue chiaro/scuro). */
    fun color(c: Context, rv: RemoteViews, id: Int, method: String, res: Int) {
        if (dynamic) rv.setColor(id, method, res) else rv.setInt(id, method, c.getColor(res))
    }

    /** Tinta (ColorStateList) da risorsa: solo Android 12+, prima resta quella del layout. */
    fun tint(c: Context, rv: RemoteViews, id: Int, method: String, res: Int) {
        if (dynamic) rv.setColorStateList(id, method, res)
    }

    fun root(rv: RemoteViews, st: WidgetStyle) = rv.setInt(R.id.root, "setBackgroundResource", st.palette.bg)

    fun dot(s: EventStatus, st: WidgetStyle) = if (s.active) st.palette.dotLive else st.palette.dotNext

    /** Colori e icona di un evento in qualunque layout (le viste assenti vengono ignorate). */
    fun event(c: Context, rv: RemoteViews, s: EventStatus, st: WidgetStyle) {
        val p = st.palette
        val ev = p.event(s.event.id)
        rv.setInt(R.id.card, "setBackgroundResource", p.card)
        listOf(R.id.name, R.id.short_name).forEach { color(c, rv, it, "setTextColor", p.text) }
        listOf(R.id.status, R.id.sub, R.id.dsub, R.id.label, R.id.span).forEach { color(c, rv, it, "setTextColor", p.dim) }
        color(c, rv, R.id.line, "setTextColor", if (s.phase != null) ev else p.dim)
        color(c, rv, R.id.pname, "setTextColor", ev)
        color(c, rv, R.id.accent, "setColorFilter", ev)
        val svg = st.icons == IconStyle.SVG
        rv.setViewVisibility(R.id.icon, if (svg) View.GONE else View.VISIBLE)
        rv.setViewVisibility(R.id.icon_img, if (svg) View.VISIBLE else View.GONE)
        if (svg) {
            rv.setImageViewResource(R.id.icon_img, WidgetStyle.svg(s.event.id))
            color(c, rv, R.id.icon_img, "setColorFilter", ev)
        }
    }

    /** Countdown: verde "in corso" o accento "in arrivo" del tema. */
    fun countdown(c: Context, rv: RemoteViews, s: EventStatus, st: WidgetStyle) {
        val res = if (s.active) st.palette.live else st.palette.accent
        color(c, rv, R.id.chrono, "setTextColor", res)
        color(c, rv, R.id.`when`, "setTextColor", res)
    }

    /** Barretta di avanzamento: colore evento e traccia del tema (Android 12+). */
    fun progress(c: Context, rv: RemoteViews, s: EventStatus, st: WidgetStyle) {
        tint(c, rv, R.id.prog, "setProgressTintList", st.palette.event(s.event.id))
        tint(c, rv, R.id.prog, "setProgressBackgroundTintList", st.palette.track)
    }
}
