// Aspetto scelto dall'utente: tema (Gioco/Chiaro/Sistema) e icone (Emoji/SVG) applicati come attributi di <html>.
export const THEMES = ["game", "light", "system"];
export const ICONS = ["emoji", "svg"];

/** Tema valido dalle preferenze (predefinito Gioco). */
export const themeOf = (prefs) => (THEMES.includes(prefs.theme) ? prefs.theme : "game");

/** Icone effettive: la scelta esplicita, altrimenti Emoji con Gioco e SVG con Chiaro/Sistema. */
export function iconsOf(prefs) {
  if (ICONS.includes(prefs.icons)) return prefs.icons;
  return themeOf(prefs) === "game" ? "emoji" : "svg";
}

/** Imposta data-theme e data-icons su <html> (i colori stanno in css/themes.css). */
export function applyLook(prefs) {
  const root = document.documentElement;
  root.dataset.theme = themeOf(prefs);
  root.dataset.icons = iconsOf(prefs);
}
