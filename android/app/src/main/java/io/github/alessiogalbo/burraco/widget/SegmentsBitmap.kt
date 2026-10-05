// Bitmap ALPHA_8 della barra fasi Torte (tinte poi col tema): traccia, riempimento o riepilogo tratteggiato.
package io.github.alessiogalbo.burraco.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

object SegmentsBitmap {
    private const val W = 600
    private const val H = 10
    private const val GAP = 8f
    private const val DASH = 12f
    private const val DASH_GAP = 6f
    /** Opacità del tratteggio non ancora trascorso del riepilogo (su 255). */
    private const val DIM = 90

    enum class Layer { TRACK, FILL, RECAP }

    /**
     * Segmenti larghi in proporzione a [mins]; se [recap] l'ultimo è il riepilogo (solo nel livello RECAP).
     * [cur] è il segmento in corso e [part] (0..1) la parte già trascorsa.
     */
    fun draw(mins: List<Int>, recap: Boolean, cur: Int, part: Float, layer: Layer): Bitmap {
        val total = mins.sum().toFloat().coerceAtLeast(1f)
        val usable = W - GAP * (mins.size - 1)
        val bmp = Bitmap.createBitmap(W, H, Bitmap.Config.ALPHA_8)
        val cv = Canvas(bmp)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        var x = 0f
        mins.forEachIndexed { i, m ->
            val w = usable * m / total
            val done = when { i < cur -> 1f; i == cur -> part.coerceIn(0f, 1f); else -> 0f }
            val isRecap = recap && i == mins.lastIndex
            when {
                isRecap && layer == Layer.RECAP -> dashes(cv, paint, x, w, x + w * done)
                isRecap || layer == Layer.RECAP -> Unit
                layer == Layer.TRACK -> bar(cv, paint, x, w, 1f)
                done > 0f -> bar(cv, paint, x, w, done)
            }
            x += w + GAP
        }
        return bmp
    }

    /** Segmento arrotondato pieno fino alla frazione [amount]. */
    private fun bar(cv: Canvas, paint: Paint, x: Float, w: Float, amount: Float) {
        paint.alpha = 255
        cv.save()
        cv.clipRect(x, 0f, x + w * amount, H.toFloat())
        cv.drawRoundRect(RectF(x, 0f, x + w, H.toFloat()), H / 2f, H / 2f, paint)
        cv.restore()
    }

    /** Tratteggio: pieno fino a [doneX], tenue dopo. */
    private fun dashes(cv: Canvas, paint: Paint, x: Float, w: Float, doneX: Float) {
        var d = x
        while (d < x + w) {
            val end = minOf(d + DASH, x + w)
            paint.alpha = if (d < doneX) 255 else DIM
            cv.drawRoundRect(RectF(d, 0f, end, H.toFloat()), H / 2f, H / 2f, paint)
            d = end + DASH_GAP
        }
    }
}
