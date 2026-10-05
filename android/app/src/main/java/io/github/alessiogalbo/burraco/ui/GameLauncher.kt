// Condivisione dell'app, apertura del gioco ufficiale (se installato) o della sua pagina Play Store; intent per il tocco sul widget.
package io.github.alessiogalbo.burraco.ui

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import io.github.alessiogalbo.burraco.R

object GameLauncher {
    const val GAME_PACKAGE = "com.WhatWapp.BurracoOnline"

    private fun gameIntent(c: Context): Intent? = c.packageManager.getLaunchIntentForPackage(GAME_PACKAGE)

    /** Apre il gioco o, se manca, la scheda Play Store. */
    fun open(c: Context) {
        val i = gameIntent(c) ?: Intent(Intent.ACTION_VIEW, Uri.parse(c.getString(R.string.store_url)))
        c.startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun openUrl(c: Context, url: String) {
        c.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Menu di condivisione di sistema (WhatsApp, Telegram, mail…) con testo e link della PWA. */
    fun share(c: Context) {
        val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, c.getString(R.string.share_text))
        c.startActivity(Intent.createChooser(send, c.getString(R.string.share_title)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Tocco sul widget: il gioco se installato, altrimenti questa app. */
    fun widgetClick(c: Context): PendingIntent {
        val i = (gameIntent(c) ?: Intent(c, MainActivity::class.java)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(c, 0, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }
}
