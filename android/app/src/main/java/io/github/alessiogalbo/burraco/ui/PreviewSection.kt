// Sezione "Anteprima widget": applica le stesse RemoteViews del widget a misure fisse.
package io.github.alessiogalbo.burraco.ui

import android.app.Activity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import io.github.alessiogalbo.burraco.R
import io.github.alessiogalbo.burraco.widget.WidgetMode
import io.github.alessiogalbo.burraco.widget.WidgetSize
import io.github.alessiogalbo.burraco.widget.WidgetStyle
import java.time.Instant

class PreviewSection(private val activity: Activity, private val list: LinearLayout) {
    /** Un widget d'esempio: modalità, taglia e misura in dp (come le celle della Home). */
    data class Spec(val mode: WidgetMode, val size: WidgetSize, val w: Int, val h: Int)

    private fun dp(v: Int): Int = WidgetPreview.dp(activity, v)

    fun render(style: WidgetStyle, now: Instant = Instant.now()) {
        list.removeAllViews()
        block(R.string.preview_all, listOf(Spec(WidgetMode.ALL, WidgetSize.MEDIUM, 300, 150)), style, now)
        block(R.string.preview_next, listOf(Spec(WidgetMode.NEXT, WidgetSize.MEDIUM, 180, 170)), style, now)
        block(R.string.preview_single, listOf(Spec(WidgetMode.single("torte"), WidgetSize.STRIP, 300, 60)), style, now)
    }

    private fun block(label: Int?, specs: List<Spec>, style: WidgetStyle, now: Instant) {
        val v = LayoutInflater.from(activity).inflate(R.layout.item_preview, list, false)
        val title = v.findViewById<TextView>(R.id.preview_label)
        if (label == null) title.visibility = TextView.GONE else title.setText(label)
        val box = v.findViewById<LinearLayout>(R.id.preview_box)
        specs.forEach { s ->
            val view = WidgetPreview.view(activity, box, s.mode, style, s.w, s.h, s.size, now)
            val lp = LinearLayout.LayoutParams(dp(s.w), dp(s.h))
            lp.bottomMargin = dp(8)
            box.addView(view, lp)
        }
        list.addView(v)
    }
}
