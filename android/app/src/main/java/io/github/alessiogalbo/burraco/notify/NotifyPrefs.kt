// Preferenze notifiche (per evento, cambi fase Torte, minuti di anticipo) ricordate in SharedPreferences.
package io.github.alessiogalbo.burraco.notify

import android.content.Context

object NotifyPrefs {
    private const val FILE = "notify"
    private const val PHASES = "phases"
    private const val LEAD = "lead"
    val LEADS = intArrayOf(0, 5, 15, 30)

    private fun prefs(c: Context) = c.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun event(c: Context, id: String): Boolean = prefs(c).getBoolean("event_$id", false)
    fun setEvent(c: Context, id: String, on: Boolean) = prefs(c).edit().putBoolean("event_$id", on).apply()

    fun phases(c: Context): Boolean = prefs(c).getBoolean(PHASES, false)
    fun setPhases(c: Context, on: Boolean) = prefs(c).edit().putBoolean(PHASES, on).apply()

    fun leadMinutes(c: Context): Int = prefs(c).getInt(LEAD, 0)
    fun setLeadMinutes(c: Context, m: Int) = prefs(c).edit().putInt(LEAD, m).apply()
}
