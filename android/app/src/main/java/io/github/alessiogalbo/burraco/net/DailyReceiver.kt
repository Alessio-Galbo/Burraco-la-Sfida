// Sveglia inesatta giornaliera (anche senza widget): orari dal sito e controllo nuova versione se dovuti, poi si riprogramma.
package io.github.alessiogalbo.burraco.net

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.concurrent.atomic.AtomicInteger

class DailyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        schedule(context)
        val pending = goAsync()
        runDue(context) { pending.finish() }
    }

    companion object {
        private fun pending(c: Context, flag: Int): PendingIntent? =
            PendingIntent.getBroadcast(c, 4, Intent(c, DailyReceiver::class.java), flag or PendingIntent.FLAG_IMMUTABLE)

        /** Programma (sostituendo quella esistente) la sveglia tra 24 h: inesatta, anche in Doze. */
        fun schedule(c: Context) {
            val pi = pending(c, PendingIntent.FLAG_UPDATE_CURRENT) ?: return
            val at = System.currentTimeMillis() + UpdatePolicy.DAILY_MS
            c.getSystemService(AlarmManager::class.java).setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
        }

        /** Programma la sveglia solo se non esiste già (apertura dell'app). */
        fun ensure(c: Context) {
            if (pending(c, PendingIntent.FLAG_NO_CREATE) == null) schedule(c)
        }

        /** Orari e nuova versione (ciascuno al massimo ogni 24 h), in background; [finish] quando entrambi hanno finito. */
        fun runDue(c: Context, finish: () -> Unit) {
            val left = AtomicInteger(2)
            val one = { if (left.decrementAndGet() == 0) finish() }
            if (!ScheduleSync.maybeRun(c) { one() }) one()
            if (!UpdateCheck.maybeRun(c, UpdatePolicy.DAILY_MS) { one() }) one()
        }
    }
}
