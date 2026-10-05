// "Aggiungi widget": chiede al launcher di posizionarlo (requestPinAppWidget); la callback apre la configurazione.
package io.github.alessiogalbo.burraco.ui

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.widget.Toast
import io.github.alessiogalbo.burraco.R
import io.github.alessiogalbo.burraco.widget.BurracoWidget
import io.github.alessiogalbo.burraco.widget.PinReceiver

object WidgetPin {
    fun request(a: Activity) {
        val m = AppWidgetManager.getInstance(a)
        if (!m.isRequestPinAppWidgetSupported) {
            Toast.makeText(a, R.string.add_widget_manual, Toast.LENGTH_LONG).show()
            return
        }
        // Broadcast (mutabile: il launcher aggiunge l'id del widget) invece di un'activity, bloccata da Android 14+.
        val cb = PendingIntent.getBroadcast(
            a, 3, Intent(a, PinReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
        )
        m.requestPinAppWidget(ComponentName(a, BurracoWidget::class.java), null, cb)
    }
}
