// Modalità di ciascun widget (per appWidgetId), ricordata in SharedPreferences.
package io.github.alessiogalbo.burraco.widget

import android.content.Context

/** Cosa mostra un widget: tutti gli eventi, il prossimo/in corso, oppure un solo evento ([eventId]). */
data class WidgetMode(val kind: Kind, val eventId: String? = null) {
    enum class Kind { ALL, NEXT, SINGLE }

    fun encode(): String = if (kind == Kind.SINGLE) "single:$eventId" else kind.name.lowercase()

    companion object {
        val ALL = WidgetMode(Kind.ALL)
        val NEXT = WidgetMode(Kind.NEXT)
        fun single(id: String) = WidgetMode(Kind.SINGLE, id)

        fun decode(s: String?): WidgetMode = when {
            s == null -> ALL
            s.startsWith("single:") -> single(s.removePrefix("single:"))
            s == "next" -> NEXT
            else -> ALL
        }
    }
}

object WidgetPrefs {
    private const val FILE = "widgets"

    private fun prefs(c: Context) = c.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun mode(c: Context, id: Int): WidgetMode = WidgetMode.decode(prefs(c).getString("mode_$id", null))

    fun setMode(c: Context, id: Int, mode: WidgetMode) {
        prefs(c).edit().putString("mode_$id", mode.encode()).apply()
    }

    /** Stile del widget; se non scelto, lo stile predefinito dell'app. */
    fun style(c: Context, id: Int): WidgetStyle = WidgetStyle.decode(prefs(c).getString("style_$id", null)) ?: defaultStyle(c)

    fun setStyle(c: Context, id: Int, style: WidgetStyle) {
        prefs(c).edit().putString("style_$id", style.encode()).apply()
    }

    /** Stile predefinito (app e nuovi widget), scelto nella schermata principale. */
    fun defaultStyle(c: Context): WidgetStyle =
        WidgetStyle.decode(prefs(c).getString("style_default", null)) ?: WidgetStyle.DEFAULT

    fun setDefaultStyle(c: Context, style: WidgetStyle) {
        prefs(c).edit().putString("style_default", style.encode()).apply()
    }

    fun remove(c: Context, ids: IntArray) {
        val e = prefs(c).edit()
        ids.forEach { e.remove("mode_$it"); e.remove("style_$it") }
        e.apply()
    }
}
