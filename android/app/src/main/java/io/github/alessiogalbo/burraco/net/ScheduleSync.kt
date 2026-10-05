// Orari aggiornati dal sito (max 1 volta ogni 24 h, in background): valida, salva in filesDir, aggiorna widget e notifiche.
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

    /** Vero se gli orari in uso cambiano, falso se già aggiornati, null se il tentativo è fallito (riprova tra 2 h). */
    private fun fetch(c: Context): Boolean? {
        val body = Http.get(URL) ?: return null
        ScheduleCheck.parseValid(body) ?: return null
        val file = ScheduleRepo.downloaded(c)
        if (file.exists() && file.readText(Charsets.UTF_8) == body) return false
        val tmp = java.io.File(file.parentFile, file.name + ".tmp")
        tmp.writeText(body, Charsets.UTF_8)
        if (!tmp.renameTo(file)) return null.also { tmp.delete() }
        val before = ScheduleRepo.schedule(c)
        ScheduleRepo.reset()
        if (ScheduleRepo.schedule(c) == before) return false
        BurracoWidget.updateAll(c)
        NotifyScheduler.reschedule(c)
        return true
    }
}
