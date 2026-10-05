// Configurazione del widget (al posizionamento e da "riconfigura"): modalità, stile e icone per appWidgetId.
package io.github.alessiogalbo.burraco.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import io.github.alessiogalbo.burraco.R
import io.github.alessiogalbo.burraco.schedule.ScheduleRepo
import io.github.alessiogalbo.burraco.ui.Insets
import io.github.alessiogalbo.burraco.ui.StylePicker
import io.github.alessiogalbo.burraco.ui.Texts
import io.github.alessiogalbo.burraco.ui.WidgetPreview

class WidgetConfigActivity : Activity() {
    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var mode: WidgetMode
    private lateinit var style: WidgetStyle
    private val buttons = mutableListOf<Pair<Button, WidgetMode>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(WidgetPrefs.defaultStyle(this).palette.appTheme)
        super.onCreate(savedInstanceState)
        widgetId = intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId) ?: widgetId
        val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(RESULT_CANCELED, result)
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return finish()
        setContentView(R.layout.activity_config)
        Insets.edgeToEdge(this, WidgetPrefs.defaultStyle(this).theme)
        Insets.pad(findViewById(R.id.config_root))
        mode = WidgetPrefs.mode(this, widgetId)
        style = WidgetPrefs.style(this, widgetId)
        val list = findViewById<LinearLayout>(R.id.config_options)
        val options = mutableListOf(getString(R.string.mode_all) to WidgetMode.ALL, getString(R.string.mode_next) to WidgetMode.NEXT)
        ScheduleRepo.schedule(this).events.forEach { options += Texts.withIcon(this, it) to WidgetMode.single(it.id) }
        options.forEach { (label, m) ->
            val b = LayoutInflater.from(this).inflate(R.layout.item_option, list, false) as Button
            b.text = label
            b.setOnClickListener { mode = m; refresh() }
            buttons += b to m
            list.addView(b)
        }
        StylePicker(findViewById(R.id.config_style), style) { style = it; refresh() }
        findViewById<Button>(R.id.config_save).setOnClickListener { save(result) }
        refresh()
    }

    /** Evidenzia la modalità scelta e ridisegna l'anteprima alla misura del widget (4×2 se non nota). */
    private fun refresh() {
        buttons.forEach { (b, m) -> b.isSelected = m == mode }
        val o = AppWidgetManager.getInstance(this).getAppWidgetOptions(widgetId)
        val w = o.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0).takeIf { it > 0 } ?: 280
        val h = o.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 0).takeIf { it > 0 } ?: 140
        WidgetPreview.show(this, findViewById<FrameLayout>(R.id.config_preview), mode, style, w.coerceIn(70, 320), h.coerceIn(50, 220))
    }

    private fun save(result: Intent) {
        WidgetPrefs.setMode(this, widgetId, mode)
        WidgetPrefs.setStyle(this, widgetId, style)
        BurracoWidget.update(this, AppWidgetManager.getInstance(this), widgetId)
        WidgetScheduler.scheduleNext(this)
        setResult(RESULT_OK, result)
        finish()
    }
}
