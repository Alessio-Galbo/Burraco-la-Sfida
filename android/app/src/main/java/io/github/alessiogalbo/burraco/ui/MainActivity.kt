// Schermata principale: banner nuova versione, stato attuale, apri gioco, notifiche, aggiungi widget, anteprima, link e avviso fan.
package io.github.alessiogalbo.burraco.ui

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import io.github.alessiogalbo.burraco.R
import io.github.alessiogalbo.burraco.net.ScheduleSync
import io.github.alessiogalbo.burraco.notify.NotifyScheduler
import io.github.alessiogalbo.burraco.schedule.EventOrder
import io.github.alessiogalbo.burraco.schedule.ScheduleRepo
import io.github.alessiogalbo.burraco.widget.BurracoWidget
import io.github.alessiogalbo.burraco.widget.PinReceiver
import io.github.alessiogalbo.burraco.widget.WidgetBind
import io.github.alessiogalbo.burraco.widget.WidgetPrefs
import io.github.alessiogalbo.burraco.widget.WidgetStyle
import java.time.Instant

class MainActivity : Activity() {
    private lateinit var preview: PreviewSection
    private lateinit var style: WidgetStyle

    override fun onCreate(savedInstanceState: Bundle?) {
        style = WidgetPrefs.defaultStyle(this)
        setTheme(style.palette.appTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Insets.edgeToEdge(this, style.theme, blueHeader = true)
        Insets.headerAndScroll(findViewById(R.id.main_root), findViewById(R.id.header), findViewById(R.id.main_scroll))
        preview = PreviewSection(this, findViewById(R.id.preview_list))
        NotifySection(this).bind()
        // Stile predefinito: salvato e applicato subito all'app (ricreata col nuovo tema) e ai nuovi widget.
        StylePicker(findViewById(R.id.main_style), style) { WidgetPrefs.setDefaultStyle(this, it); recreate() }
        findViewById<TextView>(R.id.open_game).setOnClickListener { GameLauncher.open(this) }
        findViewById<View>(R.id.share).setOnClickListener { GameLauncher.share(this) }
        findViewById<TextView>(R.id.add_widget).setOnClickListener { WidgetPin.request(this) }
        mapOf(
            R.id.link_official to R.string.official_url, R.id.link_play to R.string.store_url,
            R.id.link_appstore to R.string.appstore_url, R.id.link_pwa to R.string.pwa_url, R.id.kofi to R.string.kofi_url,
        ).forEach { (id, url) -> findViewById<View>(id).setOnClickListener { GameLauncher.openUrl(this, getString(url)) } }
        NotifyScheduler.reschedule(this)
        UpdateBanner(this).bind()
        // Orari dal sito (max 1 volta ogni 24 h, in background): se cambiano, ridisegna stato e anteprime.
        ScheduleSync.maybeRun(this) { changed -> if (changed) runOnUiThread { if (!isDestroyed) refresh() } }
    }

    override fun onResume() {
        super.onResume()
        val now = Instant.now()
        // Modalità screenshot (adb, vedi ShotRenderer): salva i PNG dei widget e chiude.
        if (intent.hasExtra(ShotRenderer.EXTRA_MODES)) return ShotRenderer.run(this, intent, now).let { finish() }
        refresh(now)
        PinReceiver.resumed(this)
    }

    private fun refresh(now: Instant = Instant.now()) {
        renderStatus(now)
        preview.render(style, now)
        BurracoWidget.updateAll(this)
    }

    override fun onPause() {
        super.onPause()
        PinReceiver.paused(this)
    }

    /** Stato attuale con le stesse righe del widget "tutti" (countdown live). */
    private fun renderStatus(now: Instant) {
        val list = findViewById<LinearLayout>(R.id.status_list)
        list.removeAllViews()
        EventOrder.ordered(ScheduleRepo.engine(this).statusAll(now)).forEach { s ->
            val rv = RemoteViews(packageName, R.layout.row_event).also { WidgetBind.bind(this, it, s, now, style) }
            list.addView(rv.apply(this, list))
        }
    }

    override fun onRequestPermissionsResult(code: Int, perms: Array<out String>, results: IntArray) {
        super.onRequestPermissionsResult(code, perms, results)
        if (code == NotifySection.REQUEST && results.any { it != PackageManager.PERMISSION_GRANTED }) {
            Toast.makeText(this, R.string.notif_denied, Toast.LENGTH_LONG).show()
        }
    }
}
