// Modello degli orari settimanali (schedule.json) e parsing con org.json.
package io.github.alessiogalbo.burraco.schedule

import org.json.JSONObject
import java.time.ZoneId

/** Momento della settimana in ora locale: day 1=lunedì..7=domenica, minuti dalla mezzanotte. */
data class WeekTime(val day: Int, val minute: Int, val estimated: Boolean = false) {
    /** Minuti dall'inizio della settimana (lunedì 00:00). */
    val weekMinute: Int get() = (day - 1) * MINUTES_PER_DAY + minute

    companion object {
        const val MINUTES_PER_DAY = 24 * 60
        const val MINUTES_PER_WEEK = 7 * MINUTES_PER_DAY

        fun parse(o: JSONObject): WeekTime {
            val (h, m) = o.getString("time").split(":").map { it.toInt() }
            return WeekTime(o.getInt("day"), h * 60 + m, o.optBoolean("estimated", false))
        }
    }
}

data class PhaseDef(val id: String, val mode: String, val start: WeekTime)

/** Riepilogo passivo dopo la fine dell'evento fino a [end]: non cambia stato attivo né countdown. */
data class RecapDef(val id: String, val end: WeekTime)

data class EventDef(
    val id: String,
    val icon: String,
    val start: WeekTime,
    val end: WeekTime,
    val phases: List<PhaseDef>,
    val recap: RecapDef? = null,
)

data class Schedule(val zone: ZoneId, val events: List<EventDef>, val updated: String = "") {
    fun event(id: String): EventDef? = events.firstOrNull { it.id == id }

    companion object {
        fun parse(json: String): Schedule {
            val root = JSONObject(json)
            val arr = root.getJSONArray("events")
            val events = (0 until arr.length()).map { i ->
                val e = arr.getJSONObject(i)
                val ph = e.optJSONArray("phases")
                val phases = (0 until (ph?.length() ?: 0)).map { j ->
                    val p = ph!!.getJSONObject(j)
                    PhaseDef(p.getString("id"), p.optString("mode", ""), WeekTime.parse(p.getJSONObject("start")))
                }
                val recap = e.optJSONObject("recap")?.let { RecapDef(it.optString("id", ""), WeekTime.parse(it.getJSONObject("end"))) }
                EventDef(
                    e.getString("id"), e.optString("icon", ""),
                    WeekTime.parse(e.getJSONObject("start")), WeekTime.parse(e.getJSONObject("end")), phases, recap,
                )
            }
            return Schedule(ZoneId.of(root.optString("timezone", "Europe/Rome")), events, root.optString("updated", ""))
        }
    }
}
