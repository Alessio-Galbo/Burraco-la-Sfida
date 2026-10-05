// Anteprima di un widget dentro l'app: stesse RemoteViews del widget, applicate a una misura in dp.
package io.github.alessiogalbo.burraco.ui

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import io.github.alessiogalbo.burraco.widget.WidgetMode
import io.github.alessiogalbo.burraco.widget.WidgetSize
import io.github.alessiogalbo.burraco.widget.WidgetSizes
import io.github.alessiogalbo.burraco.widget.WidgetStyle
import io.github.alessiogalbo.burraco.widget.WidgetViews
import java.time.Instant

object WidgetPreview {
    fun dp(a: Activity, v: Int): Int = (v * a.resources.displayMetrics.density).toInt()

    /** Vista del widget per [mode] e [style]; la taglia, se non data, è quella che Android sceglierebbe a w×h dp. */
    fun view(a: Activity, parent: ViewGroup, mode: WidgetMode, style: WidgetStyle, w: Int, h: Int,
             size: WidgetSize = WidgetSizes.sizeFor(mode, w.toFloat(), h.toFloat()), now: Instant = Instant.now()): View =
        WidgetViews.build(a, mode, size, now, null, style).apply(a, parent)

    /** Sostituisce il contenuto di [box] con l'anteprima centrata. */
    fun show(a: Activity, box: FrameLayout, mode: WidgetMode, style: WidgetStyle, w: Int, h: Int) {
        box.removeAllViews()
        val lp = FrameLayout.LayoutParams(dp(a, w), dp(a, h), Gravity.CENTER_HORIZONTAL)
        box.addView(view(a, box, mode, style, w, h), lp)
    }
}
