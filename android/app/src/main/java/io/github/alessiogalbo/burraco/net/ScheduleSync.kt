// Orari aggiornati dal sito (max 1 volta ogni 24 h, in background, richiesta condizionale ETag): valida, salva, aggiorna widget e notifiche.
package io.github.alessiogalbo.burraco.net

import android.content.Context
import io.github.alessiogalbo.burraco.notify.NotifyScheduler
import io.github.alessiogalbo.burraco.schedule.ScheduleCheck
import io.github.alessiogalbo.burraco.schedule.ScheduleRepo
import io.github.alessiogalbo.burraco.widget.BurracoWidget

object ScheduleSync {
    private const val URL = "https://alessio-galbo.github.io/Burraco-la-Sfida/data/schedule.json"
    private const val EVERY_MS = 24 * 60 * 60 * 1000L

    /**
     * Se sono passate 24 h dall'ultimo successo (2 h da un fallimento) scarica gli orari in un thread; [done] arriva sempre (dal thread),
     * con vero se gli orari in uso sono cambiati. Ritorna falso se non serviva (in quel caso [done] non viene chiamato).
     */
    fun maybeRun(c: Context, done: (Boolean) -> Unit = {}): Boolean {
        val app = c.applicationContext
        if (!NetPrefs.claim(app, NetPrefs.SCHEDULE_AT, EVERY_MS)) return false
        Thread {
            val changed = runCatching { fetch(app) }.getOrNull()
            NetPrefs.result(app, NetPrefs.SCHEDULE_AT, changed != null)
            done(changed == true)
        }.start()
        return true
    }

    /** Vero se gli orari in uso cambiano, falso se già aggiornati (anche 304), null se il tentativo è fallito (riprova tra 2 h). */
    private fun fetch(c: Context): Boolean? {
        val file = ScheduleRepo.downloaded(c)
        val v = Conditional.Validators(NetPrefs.string(c, NetPrefs.SCHEDULE_ETAG), NetPrefs.string(c, NetPrefs.SCHEDULE_MODIFIED))
        val r = Http.request(URL, headers = Conditional.headers(v, file.exists()))
        val outcome = Conditional.apply(r, file) { ScheduleCheck.parseValid(it) != null }
        if (outcome == Conditional.Outcome.FAILED) return null
        if (outcome != Conditional.Outcome.NOT_MODIFIED && r != null) {
            NetPrefs.setString(c, NetPrefs.SCHEDULE_ETAG, r.etag)
            NetPrefs.setString(c, NetPrefs.SCHEDULE_MODIFIED, r.lastModified)
        }
        if (outcome != Conditional.Outcome.WRITTEN) return false
        val before = ScheduleRepo.schedule(c)
        ScheduleRepo.reset()
        if (ScheduleRepo.schedule(c) == before) return false
        BurracoWidget.updateAll(c)
        NotifyScheduler.reschedule(c)
        return true
    }
}
