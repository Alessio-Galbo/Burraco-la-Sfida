// Banner "Nuova versione disponibile" in cima all'activity: Scarica apre l'APK nel browser, Più tardi lo nasconde.
package io.github.alessiogalbo.burraco.ui

import android.app.Activity
import android.view.View
import android.widget.TextView
import io.github.alessiogalbo.burraco.R
import io.github.alessiogalbo.burraco.net.UpdateCheck

class UpdateBanner(private val a: Activity) {
    private val root: View = a.findViewById(R.id.update_banner)

    /** Mostra subito l'ultima versione nota, poi (al massimo ogni 12 h) chiede a GitHub in background. */
    fun bind() {
        UpdateCheck.pending(a)?.let { show(it) }
        UpdateCheck.maybeRun(a) { tag -> a.runOnUiThread { if (!a.isDestroyed) show(tag) } }
    }

    private fun show(tag: String) {
        a.findViewById<TextView>(R.id.update_text).text = a.getString(R.string.update_text_fmt, tag)
        a.findViewById<View>(R.id.update_download).setOnClickListener {
            GameLauncher.openUrl(a, a.getString(R.string.update_url))
        }
        a.findViewById<View>(R.id.update_later).setOnClickListener {
            UpdateCheck.dismiss(a, tag)
            root.visibility = View.GONE
        }
        root.visibility = View.VISIBLE
    }
}
