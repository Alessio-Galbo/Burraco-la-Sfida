// Edge-to-edge in tutte le activity: barre trasparenti, contenuto dietro barre e notch, padding dalle WindowInsets.
package io.github.alessiogalbo.burraco.ui

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import io.github.alessiogalbo.burraco.widget.WidgetTheme

object Insets {
    /**
     * Da chiamare dopo setContentView: barre trasparenti senza scrim, icone chiare/scure secondo il tema.
     * [blueHeader]: icone della barra di stato sempre chiare (stanno sopra l'header blu); la nav bar segue il tema.
     */
    @Suppress("DEPRECATION")
    fun edgeToEdge(a: Activity, theme: WidgetTheme, blueHeader: Boolean = false) {
        val w = a.window
        val night = a.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val lightBars = theme == WidgetTheme.LIGHT || (theme == WidgetTheme.SYSTEM && !night)
        val lightStatus = lightBars && !blueHeader
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        w.statusBarColor = Color.TRANSPARENT
        w.navigationBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= 29) {
            w.isNavigationBarContrastEnforced = false
            w.isStatusBarContrastEnforced = false
        }
        if (Build.VERSION.SDK_INT >= 28) {
            w.attributes = w.attributes.apply {
                layoutInDisplayCutoutMode = if (Build.VERSION.SDK_INT >= 30) {
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
                } else WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
        if (Build.VERSION.SDK_INT >= 30) {
            w.setDecorFitsSystemWindows(false)
            val status = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            val nav = WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            w.insetsController?.setSystemBarsAppearance((if (lightStatus) status else 0) or (if (lightBars) nav else 0), status or nav)
        } else {
            var flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            if (lightStatus) flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            if (lightBars) flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            w.decorView.systemUiVisibility = flags
        }
    }

    /** Riceve le insets di barre di sistema + notch (sinistra, alto, destra, basso) a ogni cambio. */
    fun listen(root: View, onInsets: (Int, Int, Int, Int) -> Unit) {
        root.setOnApplyWindowInsetsListener { _, insets ->
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= 30) {
                insets.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
                    .let { onInsets(it.left, it.top, it.right, it.bottom) }
            } else {
                onInsets(insets.systemWindowInsetLeft, insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
            }
            insets
        }
        root.requestApplyInsets()
    }

    /** Aggiunge le insets al padding originale di [view] (lo sfondo resta fino ai bordi). */
    fun pad(view: View, top: Boolean = true) {
        val l = view.paddingLeft
        val t = view.paddingTop
        val r = view.paddingRight
        val b = view.paddingBottom
        listen(view) { il, it, ir, ib -> view.setPadding(l + il, t + (if (top) it else 0), r + ir, b + ib) }
    }

    /** Header a gradiente dietro la barra di stato (alto quanto serve), scroll fino sotto la barra di navigazione. */
    fun headerAndScroll(root: View, header: View, scroll: View) {
        val base = header.layoutParams.height
        val start = header.paddingLeft
        listen(root) { l, t, r, b ->
            header.layoutParams = header.layoutParams.apply { height = base + t }
            header.setPadding(start + l, t, r, 0)
            scroll.setPadding(l, 0, r, b)
        }
    }
}
