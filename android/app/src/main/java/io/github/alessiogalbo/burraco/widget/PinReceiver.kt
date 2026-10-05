// Callback di requestPinAppWidget: apre la configurazione del nuovo widget dall'activity in primo piano.
package io.github.alessiogalbo.burraco.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PinReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (id == AppWidgetManager.INVALID_APPWIDGET_ID) return
        // Da Android 14 un receiver non può aprire activity: lo fa l'app visibile, o al prossimo onResume.
        val host = foreground
        if (host != null) open(host, id) else pending = id
    }

    companion object {
        /** Activity dell'app attualmente in primo piano (impostata da MainActivity). */
        @Volatile var foreground: Activity? = null
        @Volatile private var pending: Int = AppWidgetManager.INVALID_APPWIDGET_ID

        fun open(a: Activity, id: Int) {
            a.startActivity(Intent(a, WidgetConfigActivity::class.java).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id))
        }

        /** Da chiamare in onResume: registra l'activity e apre una configurazione rimasta in sospeso. */
        fun resumed(a: Activity) {
            foreground = a
            val id = pending
            pending = AppWidgetManager.INVALID_APPWIDGET_ID
            if (id != AppWidgetManager.INVALID_APPWIDGET_ID) open(a, id)
        }

        fun paused(a: Activity) {
            if (foreground === a) foreground = null
        }
    }
}
