// Screenshot ripetibili dei widget: renderizza fuori schermo le RemoteViews a misure in dp e salva PNG trasparenti.
package io.github.alessiogalbo.burraco.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.FrameLayout
import io.github.alessiogalbo.burraco.widget.WidgetMode
import io.github.alessiogalbo.burraco.widget.WidgetPrefs
import io.github.alessiogalbo.burraco.widget.WidgetSizes
import io.github.alessiogalbo.burraco.widget.WidgetStyle
import io.github.alessiogalbo.burraco.widget.WidgetViews
import java.io.File
import java.time.Instant

/**
 * adb shell am start -n io.github.alessiogalbo.burraco/.ui.MainActivity --es shot_modes all,next,single:torte
 *   --es shot_sizes 70x70,280x140 → PNG in /sdcard/Android/data/io.github.alessiogalbo.burraco/files/shots/,
 *   nome "<modalità>_<w>x<h>.png" (":" diventa "-"); alla fine scrive done.txt.
 *   Facoltativo --es shot_styles game:emoji,light:svg → nome "<modalità>_<stile>_<w>x<h>.png";
 *   --es shot_at 2026-10-10T08:00:00Z (istante simulato), --es shot_scale 2.0 (PNG più grande, testo nitido).
 */
object ShotRenderer {
    const val EXTRA_MODES = "shot_modes"
    const val EXTRA_SIZES = "shot_sizes"
    const val EXTRA_STYLES = "shot_styles"
    const val EXTRA_AT = "shot_at"
    const val EXTRA_SCALE = "shot_scale"

    fun run(a: Activity, i: Intent, real: Instant = Instant.now()) {
        val now = i.getStringExtra(EXTRA_AT)?.let { Instant.parse(it) } ?: real
        val k = i.getStringExtra(EXTRA_SCALE)?.toFloatOrNull() ?: 1f
        val dir = File(a.getExternalFilesDir(null), "shots").apply { deleteRecursively(); mkdirs() }
        val modes = i.getStringExtra(EXTRA_MODES).orEmpty().split(",").filter { it.isNotBlank() }
        val sizes = i.getStringExtra(EXTRA_SIZES).orEmpty().split(",").filter { it.contains("x") }
            .map { it.split("x").let { (w, h) -> w.toInt() to h.toInt() } }
        val styles = i.getStringExtra(EXTRA_STYLES).orEmpty().split(",").filter { it.isNotBlank() }
        val named = styles.map { WidgetStyle.decode(it) to "_${it.replace(':', '-')}" }
            .ifEmpty { listOf(WidgetPrefs.defaultStyle(a) to "") }
        for (m in modes) for ((st, tag) in named) for ((w, h) in sizes) {
            save(a, WidgetMode.decode(m), st ?: WidgetStyle.DEFAULT, w, h, now, k, File(dir, "${m.replace(':', '-')}${tag}_${w}x$h.png"))
        }
        File(dir, "done.txt").writeText("ok")
    }

    private fun save(a: Activity, mode: WidgetMode, st: WidgetStyle, wDp: Int, hDp: Int, now: Instant, k: Float, out: File) {
        val d = a.resources.displayMetrics.density
        val w = (wDp * d).toInt()
        val h = (hDp * d).toInt()
        val parent = FrameLayout(a)
        val size = WidgetSizes.sizeFor(mode, wDp.toFloat(), hDp.toFloat())
        val v = WidgetViews.build(a, mode, size, now, null, st).apply(a, parent)
        parent.addView(v, FrameLayout.LayoutParams(w, h))
        parent.measure(View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY))
        parent.layout(0, 0, w, h)
        val bmp = Bitmap.createBitmap((w * k).toInt(), (h * k).toInt(), Bitmap.Config.ARGB_8888)
        parent.draw(Canvas(bmp).apply { scale(k, k) })
        out.outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        File(out.parentFile, out.name + ".txt").writeText(size.name)
    }
}
