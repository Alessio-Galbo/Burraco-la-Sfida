// Sezione notifiche: interruttori per evento, cambi fase Torte e anticipo; salva e riprogramma a ogni modifica.
package io.github.alessiogalbo.burraco.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import io.github.alessiogalbo.burraco.R
import io.github.alessiogalbo.burraco.notify.NotifyPrefs
import io.github.alessiogalbo.burraco.notify.NotifyScheduler
import io.github.alessiogalbo.burraco.schedule.ScheduleRepo

class NotifySection(private val a: Activity) {
    fun bind() {
        val list = a.findViewById<LinearLayout>(R.id.notif_list)
        list.removeAllViews()
        ScheduleRepo.schedule(a).events.forEach { e ->
            val sw = a.layoutInflater.inflate(R.layout.item_switch, list, false) as Switch
            sw.text = a.getString(R.string.notif_event_fmt, Texts.withIcon(a, e))
            sw.isChecked = NotifyPrefs.event(a, e.id)
            sw.setOnCheckedChangeListener { _, on -> changed(on) { NotifyPrefs.setEvent(a, e.id, on) } }
            list.addView(sw)
        }
        a.findViewById<Switch>(R.id.notif_phases).apply {
            isChecked = NotifyPrefs.phases(a)
            setOnCheckedChangeListener { _: CompoundButton, on: Boolean -> changed(on) { NotifyPrefs.setPhases(a, on) } }
        }
        val group = a.findViewById<RadioGroup>(R.id.lead_group)
        group.removeAllViews()
        val current = NotifyPrefs.leadMinutes(a)
        NotifyPrefs.LEADS.forEach { m ->
            val rb = a.layoutInflater.inflate(R.layout.item_radio, group, false) as RadioButton
            rb.text = if (m == 0) a.getString(R.string.lead_0) else a.getString(R.string.lead_fmt, m)
            rb.id = 1000 + m
            group.addView(rb)
            if (m == current) group.check(rb.id)
        }
        group.setOnCheckedChangeListener { _, id ->
            NotifyPrefs.setLeadMinutes(a, id - 1000)
            NotifyScheduler.reschedule(a)
        }
    }

    private fun changed(on: Boolean, save: () -> Unit) {
        save()
        NotifyScheduler.reschedule(a)
        if (on) askPermission()
    }

    private fun askPermission() {
        if (Build.VERSION.SDK_INT < 33) return
        if (a.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) return
        a.requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST)
    }

    companion object {
        const val REQUEST = 7
    }
}
