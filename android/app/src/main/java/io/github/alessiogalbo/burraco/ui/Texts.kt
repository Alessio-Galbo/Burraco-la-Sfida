// Testi per eventi, fasi e orari presi da strings.xml (id di schedule.json → risorse).
package io.github.alessiogalbo.burraco.ui

import android.content.Context
import io.github.alessiogalbo.burraco.R
import io.github.alessiogalbo.burraco.schedule.EventDef
import io.github.alessiogalbo.burraco.schedule.EventStatus
import io.github.alessiogalbo.burraco.schedule.PhaseDef
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Texts {
    private val zone: ZoneId = ZoneId.of("Europe/Rome")

    fun eventName(c: Context, e: EventDef): String = when (e.id) {
        "corsa" -> c.getString(R.string.event_corsa)
        "baule" -> c.getString(R.string.event_baule)
        "torte" -> c.getString(R.string.event_torte)
        else -> e.id
    }

    fun shortName(c: Context, e: EventDef): String = when (e.id) {
        "corsa" -> c.getString(R.string.short_corsa)
        "baule" -> c.getString(R.string.short_baule)
        "torte" -> c.getString(R.string.short_torte)
        else -> e.id
    }

    fun withIcon(c: Context, e: EventDef): String = c.getString(R.string.icon_name, e.icon, eventName(c, e))

    fun phaseName(c: Context, p: PhaseDef): String {
        val name = when (p.id) {
            "mestolo" -> c.getString(R.string.phase_mestolo)
            "forno" -> c.getString(R.string.phase_forno)
            "crema" -> c.getString(R.string.phase_crema)
            "oro" -> c.getString(R.string.phase_oro)
            else -> p.id
        }
        return if (p.mode.isEmpty()) name else c.getString(R.string.phase_with_mode, name, p.mode)
    }

    /** Orario breve in ora italiana, es. "ven 16:00"; con ≈ davanti se stimato. */
    fun time(c: Context, t: Instant, estimated: Boolean = false): String {
        val s = DateTimeFormatter.ofPattern(c.getString(R.string.time_pattern), Locale.ITALIAN).format(t.atZone(zone))
        return if (estimated) c.getString(R.string.estimated_fmt, s) else s
    }

    fun status(c: Context, s: EventStatus): String =
        c.getString(if (s.active) R.string.status_live else R.string.status_next)

    /** "fino a ≈ven 00:00" oppure "inizia mer 02:00". */
    fun detail(c: Context, s: EventStatus): String =
        if (s.active) c.getString(R.string.until_fmt, time(c, s.until!!, s.estimated))
        else c.getString(R.string.starts_fmt, time(c, s.startsAt!!, s.estimated))

    /** Etichetta cortissima sotto il countdown: "fine tra" / "inizia tra". */
    fun label(c: Context, s: EventStatus): String =
        c.getString(if (s.active) R.string.label_end else R.string.label_start)

    /** Riga sotto il countdown: fase Torte se in corso, altrimenti "fine tra · ≈ven 00:00". */
    fun line(c: Context, s: EventStatus): String = phaseLine(c, s) ?: c.getString(
        R.string.label_line_fmt, label(c, s), time(c, if (s.active) s.until!! else s.startsAt!!, s.estimated),
    )

    /** Riga fase per le Torte ("Torta d'oro (2vs2) · fino a dom 22:00"), null se non in una fase. */
    fun phaseLine(c: Context, s: EventStatus): String? {
        val p = s.phase ?: return null
        return c.getString(R.string.phase_line_fmt, phaseName(c, p), time(c, s.phaseUntil!!, s.phaseEstimated))
    }

    /** Colore dell'evento (token di docs/design.md). */
    fun color(c: Context, e: EventDef): Int = c.getColor(
        when (e.id) {
            "corsa" -> R.color.corsa
            "baule" -> R.color.baule
            "torte" -> R.color.torte
            else -> R.color.gold
        },
    )
}
