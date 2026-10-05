// Stile grafico di un widget: tema (Gioco, Chiaro, Sistema), icone (Emoji o SVG) e riepilogo Torte, con i colori di ogni tema.
package io.github.alessiogalbo.burraco.widget

import io.github.alessiogalbo.burraco.R

/** Risorse di un tema: drawable di fondo, riga e punti, colori (risorse, così Sistema segue chiaro/scuro). */
class Palette(
    val bg: Int, val card: Int, val dotLive: Int, val dotNext: Int,
    val text: Int, val dim: Int, val accent: Int, val live: Int, val track: Int,
    val corsa: Int, val baule: Int, val torte: Int, val appTheme: Int,
) {
    fun event(id: String): Int = when (id) {
        "corsa" -> corsa
        "baule" -> baule
        "torte" -> torte
        else -> accent
    }
}

enum class WidgetTheme(val palette: Palette) {
    GAME(Palette(R.drawable.widget_bg, R.drawable.card_bg, R.drawable.dot_live, R.drawable.dot_next,
        R.color.text, R.color.text_dim, R.color.gold, R.color.live, R.color.surface_2,
        R.color.corsa, R.color.baule, R.color.torte, R.style.AppTheme)),
    LIGHT(Palette(R.drawable.widget_bg_l, R.drawable.card_bg_l, R.drawable.dot_live_l, R.drawable.dot_next_l,
        R.color.l_text, R.color.l_text_dim, R.color.l_accent, R.color.l_live, R.color.l_surface_2,
        R.color.l_corsa, R.color.l_baule, R.color.l_torte, R.style.AppTheme_Light)),
    SYSTEM(Palette(R.drawable.widget_bg_s, R.drawable.card_bg_s, R.drawable.dot_live_s, R.drawable.dot_next_s,
        R.color.s_text, R.color.s_text_dim, R.color.s_accent, R.color.s_live, R.color.s_surface_2,
        R.color.s_corsa, R.color.s_baule, R.color.s_torte, R.style.AppTheme_System)),
}

enum class IconStyle { EMOJI, SVG }

/** [recap]: 5° segmento "riepilogo" nella barra delle Torte (predefinito: attivo). */
data class WidgetStyle(val theme: WidgetTheme, val icons: IconStyle, val recap: Boolean = true) {
    val palette get() = theme.palette

    fun encode(): String = "${theme.name.lowercase()}:${icons.name.lowercase()}:${if (recap) "recap" else "norecap"}"

    /** Stesso tema con le icone proposte per quel tema (l'utente può poi cambiarle). */
    fun withTheme(t: WidgetTheme) = copy(theme = t, icons = suggestedIcons(t))

    companion object {
        val DEFAULT = WidgetStyle(WidgetTheme.GAME, IconStyle.EMOJI)

        /** Gioco con le emoji del gioco; Chiaro e Sistema con le icone SVG monocromatiche. */
        fun suggestedIcons(t: WidgetTheme) = if (t == WidgetTheme.GAME) IconStyle.EMOJI else IconStyle.SVG

        fun decode(s: String?): WidgetStyle? {
            val parts = s?.split(":") ?: return null
            val t = WidgetTheme.entries.firstOrNull { it.name.equals(parts[0], true) } ?: return null
            val i = IconStyle.entries.firstOrNull { it.name.equals(parts.getOrNull(1), true) } ?: suggestedIcons(t)
            return WidgetStyle(t, i, parts.getOrNull(2) != "norecap")
        }

        /** Disegno SVG dell'evento (icone Lucide convertite in VectorDrawable). */
        fun svg(eventId: String): Int = when (eventId) {
            "corsa" -> R.drawable.ic_ev_corsa
            "baule" -> R.drawable.ic_ev_baule
            else -> R.drawable.ic_ev_torte
        }
    }
}
