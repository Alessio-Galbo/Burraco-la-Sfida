// Riga "Versione installata" e pulsante "Controlla aggiornamenti" (subito, ignora gli intervalli); assicura la sveglia giornaliera.
package io.github.alessiogalbo.burraco.ui

import android.app.Activity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import io.github.alessiogalbo.burraco.BuildConfig
import io.github.alessiogalbo.burraco.R
import io.github.alessiogalbo.burraco.net.DailyReceiver
import io.github.alessiogalbo.burraco.net.UpdateCheck
import io.github.alessiogalbo.burraco.net.UpdateCheck.Outcome

class UpdateSection(private val a: Activity, private val banner: UpdateBanner) {
    fun bind() {
        DailyReceiver.ensure(a)
        a.findViewById<TextView>(R.id.version_installed).text =
            a.getString(R.string.version_installed_fmt, BuildConfig.VERSION_NAME)
        val button = a.findViewById<Button>(R.id.check_update)
        button.setOnClickListener {
            button.isEnabled = false
            UpdateCheck.runNow(a) { outcome, tag -> a.runOnUiThread { if (!a.isDestroyed) shown(button, outcome, tag) } }
        }
    }

    private fun shown(button: Button, outcome: Outcome, tag: String) {
        button.isEnabled = true
        val msg = when (outcome) {
            Outcome.NEW -> a.getString(R.string.update_text_fmt, tag).also { banner.show(tag) }
            Outcome.LATEST -> a.getString(R.string.update_latest)
            Outcome.FAILED -> a.getString(R.string.update_failed)
        }
        Toast.makeText(a, msg, Toast.LENGTH_LONG).show()
    }
}
