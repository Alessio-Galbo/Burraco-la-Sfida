// Controllo nuova versione su GitHub Releases (ogni 12 h, 2 h dopo un fallimento, in background); 404 o errori = silenzio.
package io.github.alessiogalbo.burraco.net

import android.content.Context
import io.github.alessiogalbo.burraco.BuildConfig
import org.json.JSONObject

object UpdateCheck {
    private const val API = "https://api.github.com/repos/Alessio-Galbo/Burraco-la-Sfida/releases/latest"
    private const val ACCEPT = "application/vnd.github+json"
    private const val EVERY_MS = 12 * 60 * 60 * 1000L

    /** Versione da proporre (più nuova di quella installata e non rimandata con "Più tardi"), o null. */
    fun pending(c: Context, current: String = BuildConfig.VERSION_NAME): String? {
        val tag = NetPrefs.string(c, NetPrefs.LATEST) ?: return null
        if (tag == NetPrefs.string(c, NetPrefs.DISMISSED)) return null
        return tag.takeIf { Semver.newer(it, current) }
    }

    /** "Più tardi": nasconde il banner fino alla versione successiva. */
    fun dismiss(c: Context, tag: String) = NetPrefs.setString(c, NetPrefs.DISMISSED, tag)

    /** Se dovuto (12 h dopo un successo, 2 h dopo errore o 404), interroga GitHub in un thread e chiama [found] (dal thread) con la versione da proporre. */
    fun maybeRun(c: Context, found: (String) -> Unit) {
        val app = c.applicationContext
        if (!NetPrefs.claim(app, NetPrefs.UPDATE_AT, EVERY_MS)) return
        Thread {
            val tag = runCatching { Http.get(API, ACCEPT)?.let { JSONObject(it).optString("tag_name", "") } }.getOrNull()
            NetPrefs.result(app, NetPrefs.UPDATE_AT, !tag.isNullOrBlank())
            if (!tag.isNullOrBlank()) {
                NetPrefs.setString(app, NetPrefs.LATEST, tag)
                pending(app)?.let(found)
            }
        }.start()
    }
}
