// Riavvio, cambio ora/fuso o aggiornamento app: riprogramma widget, notifiche e sveglia giornaliera (orari e nuova versione).
package io.github.alessiogalbo.burraco

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.alessiogalbo.burraco.net.DailyReceiver
import io.github.alessiogalbo.burraco.notify.NotifyScheduler
import io.github.alessiogalbo.burraco.widget.BurracoWidget

class SystemEvents : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        BurracoWidget.updateAll(context)
        NotifyScheduler.reschedule(context)
        DailyReceiver.schedule(context)
    }
}
