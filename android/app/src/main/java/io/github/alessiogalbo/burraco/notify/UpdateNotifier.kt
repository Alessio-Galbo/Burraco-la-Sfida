// Notifica di sistema "Nuova versione disponibile" (canale Aggiornamenti app): una volta per versione, solo con permesso; tocco = scarica l'APK.
package io.github.alessiogalbo.burraco.notify

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import io.github.alessiogalbo.burraco.BuildConfig
import io.github.alessiogalbo.burraco.R
import io.github.alessiogalbo.burraco.net.NetPrefs
import io.github.alessiogalbo.burraco.net.UpdatePolicy

object UpdateNotifier {
    const val CHANNEL = "aggiornamenti"
    private const val ID = 9001

    /** Pubblica l'avviso per [tag] se è nuova e non ancora notificata; senza permesso nulla (resta il banner). Vero se pubblicata. */
    @Synchronized
    fun maybeNotify(c: Context, tag: String, current: String = BuildConfig.VERSION_NAME): Boolean {
        if (!UpdatePolicy.shouldNotify(tag, current, NetPrefs.string(c, NetPrefs.NOTIFIED))) return false
        val nm = c.getSystemService(NotificationManager::class.java)
        if (!allowed(c, nm)) return false
        val ch = NotificationChannel(CHANNEL, c.getString(R.string.update_channel_name), NotificationManager.IMPORTANCE_DEFAULT)
        ch.description = c.getString(R.string.update_channel_desc)
        nm.createNotificationChannel(ch)
        val open = Intent(Intent.ACTION_VIEW, Uri.parse(c.getString(R.string.update_url))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pi = PendingIntent.getActivity(c, 3, open, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val n = Notification.Builder(c, CHANNEL)
            .setSmallIcon(R.drawable.ic_notify)
            .setColor(c.getColor(R.color.gold))
            .setContentTitle(c.getString(R.string.update_text_fmt, tag))
            .setContentText(c.getString(R.string.update_notif_text))
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()
        nm.notify(ID, n)
        NetPrefs.setString(c, NetPrefs.NOTIFIED, tag)
        return true
    }

    private fun allowed(c: Context, nm: NotificationManager): Boolean {
        val denied = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            c.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        return !denied && nm.areNotificationsEnabled()
    }
}
