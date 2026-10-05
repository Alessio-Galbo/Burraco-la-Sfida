// Taglie del widget e punti di misura (dp) per modalità; scelta della taglia identica a quella di Android 12+.
package io.github.alessiogalbo.burraco.widget

import android.util.SizeF

/**
 * Struttura per fascia di ALTEZZA (bassa < 100dp, media < 180, alta < 280, molto alta); la larghezza cambia solo
 * spazi, nomi troncati e countdown, tranne le colonne strette (< 170dp) che usano la variante compatta.
 * MICRO/TINY compatte, STRIP riga bassa, LINES/MEDIUM/LARGE fasce media, alta, molto alta.
 */
enum class WidgetSize { MICRO, TINY, STRIP, LINES, MEDIUM, LARGE }

object WidgetSizes {
    private const val NARROW = 170f
    private val SINGLE = listOf(
        SizeF(40f, 40f) to WidgetSize.MICRO,
        SizeF(40f, 100f) to WidgetSize.TINY,
        SizeF(NARROW, 40f) to WidgetSize.STRIP,
        SizeF(NARROW, 100f) to WidgetSize.MEDIUM,
        SizeF(NARROW, 180f) to WidgetSize.LARGE,
    )
    private val ALL = listOf(
        SizeF(40f, 40f) to WidgetSize.MICRO,
        SizeF(100f, 40f) to WidgetSize.TINY,
        SizeF(280f, 40f) to WidgetSize.STRIP,
        SizeF(NARROW, 100f) to WidgetSize.LINES,
        SizeF(NARROW, 180f) to WidgetSize.MEDIUM,
        SizeF(NARROW, 280f) to WidgetSize.LARGE,
        // Stessi punti a 280dp: da lì in su vince sempre la fascia d'altezza, non la striscia bassa.
        SizeF(280f, 100f) to WidgetSize.LINES,
        SizeF(280f, 180f) to WidgetSize.MEDIUM,
        SizeF(280f, 280f) to WidgetSize.LARGE,
    )

    /** Punti di misura della modalità: ogni punto ha la sua RemoteViews nella mappa di Android 12+. */
    fun points(mode: WidgetMode): List<Pair<SizeF, WidgetSize>> = if (mode.kind == WidgetMode.Kind.ALL) ALL else SINGLE

    /**
     * Stessa regola di RemoteViews (Android 12+): tra i punti che entrano in [w]×[h] vince il più vicino
     * (distanza al quadrato); se nessuno entra, il più piccolo. Usata prima di Android 12 e per gli screenshot.
     */
    fun sizeFor(mode: WidgetMode, w: Float, h: Float): WidgetSize {
        val pts = points(mode)
        val fit = pts.filter { it.first.width <= w && it.first.height <= h }
        if (fit.isEmpty()) return pts.minBy { it.first.width * it.first.height }.second
        return fit.minBy { (w - it.first.width).let { d -> d * d } + (h - it.first.height).let { d -> d * d } }.second
    }
}
