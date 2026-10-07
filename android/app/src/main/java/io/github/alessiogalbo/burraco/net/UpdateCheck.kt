// Controllo nuova versione su GitHub Releases (apertura max 1/ora, background 1/giorno, 2 h dopo un errore, manuale subito): sempre in un thread.
package io.github.alessiogalbo.burraco.net

import android.content.Context
import io.github.alessiogalbo.burraco.BuildConfig
import io.github.alessiogalbo.burraco.notify.UpdateNotifier
import org.json.JSONObject

object UpdateCheck {
    private const val API = "https://api.github.com/repos/Alessio-Galbo/Burraco-la-Sfida/releases/latest"
    private const val ACCEPT = "application/vnd.github+json"

    /** Esito del controllo manuale. */
    enum class Outcome { LATEST, NEW, FAILED }

    /** Versione da proporre (più nuova di quella installata e non rimandata con "Più tardi"), o null. */
    fun pending(c: Context, current: String = BuildConfig.VERSION_NAME): String? {
        val tag = NetPrefs.string(c, NetPrefs.LATEST) ?: return null
        if (tag == NetPrefs.string(c, NetPrefs.DISMISSED)) return null
        return tag.takeIf { Semver.newer(it, current) }
    }

    /** "Più tardi": nasconde il banner fino alla versione successiva. */
    fun dismiss(c: Context, tag: String) = NetPrefs.setString(c, NetPrefs.DISMISSED, tag)

    /**
     * Se dovuto ([everyMs] dopo un successo, 2 h dopo errore o 404) interroga GitHub in un thread e chiama [done] (dal thread)
     * con la versione da proporre o null. Ritorna falso se non serviva (in quel caso [done] non viene chiamato).
     */
    fun maybeRun(c: Context, everyMs: Long, done: (String?) -> Unit = {}): Boolean {
        val app = c.applicationContext
        if (!NetPrefs.claim(app, NetPrefs.UPDATE_AT, everyMs)) return false
        Thread { fetch(app); done(pending(app)) }.start()
        return true
    }

    /** "Controlla aggiornamenti": subito, ignorando gli intervalli; [done] arriva dal thread con esito e versione trovata. */
    fun runNow(c: Context, done: (Outcome, String) -> Unit) {
        val app = c.applicationContext
        NetPrefs.mark(app, NetPrefs.UPDATE_AT)
        Thread {
            val tag = fetch(app)
            val outcome = when {
                tag == null -> Outcome.FAILED
                Semver.newer(tag, BuildConfig.VERSION_NAME) -> Outcome.NEW
                else -> Outcome.LATEST
            }
            done(outcome, tag.orEmpty())
        }.start()
    }

    /** Chiede a GitHub l'ultima versione (fuori dal thread principale): la salva e la notifica se nuova; null se errore. */
    private fun fetch(app: Context): String? {
        val tag = runCatching { Http.get(API, ACCEPT)?.let { JSONObject(it).optString("tag_name", "") } }
            .getOrNull()?.takeIf { it.isNotBlank() }
        NetPrefs.result(app, NetPrefs.UPDATE_AT, tag != null)
        if (tag != null) {
            NetPrefs.setString(app, NetPrefs.LATEST, tag)
            UpdateNotifier.maybeNotify(app, tag)
        }
        return tag
    }
}
