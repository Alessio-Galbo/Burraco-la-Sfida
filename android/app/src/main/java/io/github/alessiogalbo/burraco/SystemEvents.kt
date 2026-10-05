// Riavvio, cambio ora/fuso o aggiornamento app: riprogramma widget e notifiche.
package io.github.alessiogalbo.burraco

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.alessiogalbo.burraco.notify.NotifyScheduler
import io.github.alessiogalbo.burraco.widget.BurracoWidget

class SystemEvents : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        BurracoWidget.updateAll(context)
        NotifyScheduler.reschedule(context)
    }
}
