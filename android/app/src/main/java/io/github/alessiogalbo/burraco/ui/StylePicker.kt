// Gruppi compatti "Stile", "Icone" e interruttore riepilogo Torte (layout part_style): avvisa a ogni cambio di stile.
package io.github.alessiogalbo.burraco.ui

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import io.github.alessiogalbo.burraco.R
import io.github.alessiogalbo.burraco.widget.IconStyle
import io.github.alessiogalbo.burraco.widget.WidgetStyle
import io.github.alessiogalbo.burraco.widget.WidgetTheme

class StylePicker(root: View, initial: WidgetStyle, private val onChange: (WidgetStyle) -> Unit) {
    var style: WidgetStyle = initial
        private set
    private val themes = root.findViewById<LinearLayout>(R.id.style_themes)
    private val icons = root.findViewById<LinearLayout>(R.id.style_icons)

    init {
        render()
        root.findViewById<Switch>(R.id.style_recap).apply {
            isChecked = style.recap
            setOnCheckedChangeListener { _, on -> style = style.copy(recap = on); changed() }
        }
    }

    private fun render() {
        // Cambiando tema si propongono le icone adatte (Gioco → Emoji, Chiaro/Sistema → SVG); restano modificabili.
        fill(themes, THEMES, style.theme) { style = style.withTheme(it); changed() }
        fill(icons, ICONS, style.icons) { style = style.copy(icons = it); changed() }
    }

    private fun changed() {
        render()
        onChange(style)
    }

    private fun <T> fill(box: LinearLayout, items: List<Pair<Int, T>>, current: T, pick: (T) -> Unit) {
        box.removeAllViews()
        items.forEach { (label, value) ->
            val chip = LayoutInflater.from(box.context).inflate(R.layout.item_chip, box, false) as TextView
            chip.setText(label)
            chip.isSelected = value == current
            chip.setOnClickListener { if (value != current) pick(value) }
            box.addView(chip)
        }
    }

    companion object {
        private val THEMES = listOf(
            R.string.theme_game to WidgetTheme.GAME,
            R.string.theme_light to WidgetTheme.LIGHT,
            R.string.theme_system to WidgetTheme.SYSTEM,
        )
        private val ICONS = listOf(R.string.icons_emoji to IconStyle.EMOJI, R.string.icons_svg to IconStyle.SVG)
    }
}
