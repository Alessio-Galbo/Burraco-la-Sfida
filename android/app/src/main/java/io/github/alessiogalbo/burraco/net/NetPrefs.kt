// Promemoria delle funzioni di rete in SharedPreferences: ultimi tentativi ed esito, validatori ETag degli orari, ultima versione vista, versione ignorata.
package io.github.alessiogalbo.burraco.net

import android.content.Context

object NetPrefs {
    private const val FILE = "net"
    const val SCHEDULE_AT = "schedule_at"
    const val SCHEDULE_ETAG = "schedule_etag"
    const val SCHEDULE_MODIFIED = "schedule_modified"
    const val UPDATE_AT = "update_at"
    const val LATEST = "latest_tag"
    const val DISMISSED = "dismissed_tag"

    private fun prefs(c: Context) = c.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    /** Vero (e segna l'ora) se il tentativo [key] è dovuto: [everyMs] dopo un successo, 2 h dopo un fallimento. */
    @Synchronized
    fun claim(c: Context, key: String, everyMs: Long, now: Long = System.currentTimeMillis()): Boolean {
        val p = prefs(c)
        if (!Retry.due(p.getLong(key, 0L), p.getBoolean("${key}_ok", false), now, everyMs)) return false
        p.edit().putLong(key, now).putBoolean("${key}_ok", false).apply()
        return true
    }

    /** Esito del tentativo [key]: solo un successo fa valere l'intervallo lungo. */
    fun result(c: Context, key: String, ok: Boolean) = prefs(c).edit().putBoolean("${key}_ok", ok).apply()

    fun string(c: Context, key: String): String? = prefs(c).getString(key, null)
    fun setString(c: Context, key: String, v: String?) = prefs(c).edit().putString(key, v).apply()
}
