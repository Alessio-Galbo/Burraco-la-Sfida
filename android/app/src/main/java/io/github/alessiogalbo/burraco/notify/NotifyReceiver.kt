// Riceve l'allarme: pubblica le notifiche dell'istante programmato (canale dedicato) e programma la successiva.
package io.github.alessiogalbo.burraco.notify

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.alessiogalbo.burraco.R
import io.github.alessiogalbo.burraco.ui.GameLauncher
import io.github.alessiogalbo.burraco.ui.Texts
import java.time.Duration
import java.time.Instant

class NotifyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val at = Instant.ofEpochMilli(intent.getLongExtra(NotifyScheduler.EXTRA_AT, System.currentTimeMillis()))
        val due = NotifyScheduler.planner(context).next(at.minusMillis(1))
        // Se l'allarme arriva molto in ritardo (telefono spento) non si avvisa di eventi ormai passati.
        val late = Duration.between(at, Instant.now()) > Duration.ofHours(1)
        if (due != null && due.first == at && !late) due.second.forEach { post(context, it) }
        val now = Instant.now()
        NotifyScheduler.reschedule(context, if (now.isAfter(at)) now else at)
    }

    private fun post(c: Context, item: NotifyItem) {
        val nm = c.getSystemService(NotificationManager::class.java)
        ensureChannel(c, nm)
        val title = if (item.phase == null) Texts.withIcon(c, item.event)
        else c.getString(R.string.notif_phase_title, item.event.icon, Texts.phaseName(c, item.phase))
        val lead = NotifyPrefs.leadMinutes(c)
        val text = if (lead == 0) c.getString(R.string.notif_now) else c.getString(R.string.notif_soon_fmt, lead)
        val n = Notification.Builder(c, CHANNEL)
            .setSmallIcon(R.drawable.ic_notify)
            .setColor(c.getColor(R.color.gold))
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setContentIntent(GameLauncher.widgetClick(c))
            .build()
        nm.notify((item.event.id + (item.phase?.id ?: "")).hashCode(), n)
    }

    companion object {
        const val CHANNEL = "eventi"

        fun ensureChannel(c: Context, nm: NotificationManager = c.getSystemService(NotificationManager::class.java)) {
            val ch = NotificationChannel(CHANNEL, c.getString(R.string.channel_name), NotificationManager.IMPORTANCE_HIGH)
            ch.description = c.getString(R.string.channel_desc)
            nm.createNotificationChannel(ch)
        }
    }
}
