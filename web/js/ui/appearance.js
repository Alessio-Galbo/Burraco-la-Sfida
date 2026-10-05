// Sezione "Aspetto" (tab Info): Stile, Icone e riepilogo Torte nella barra, applicati subito e ricordati.
import { $ } from "../core/dom.js";
import { getPrefs, setPrefs } from "../core/prefs.js";
import { applyLook, themeOf, iconsOf } from "../core/look.js";

function mark() {
  const prefs = getPrefs();
  for (const b of document.querySelectorAll("[data-look-theme]")) {
    b.setAttribute("aria-pressed", String(b.dataset.lookTheme === themeOf(prefs)));
  }
  for (const b of document.querySelectorAll("[data-look-icons]")) {
    b.setAttribute("aria-pressed", String(b.dataset.lookIcons === iconsOf(prefs)));
  }
}

function choose(patch) {
  applyLook(setPrefs(patch));
  mark();
}

/** Il 5° segmento (riepilogo) si nasconde via CSS: gli altri si ridistribuiscono; l'elenco fasi non cambia. */
function applyRecapBar(on) {
  document.documentElement.classList.toggle("no-recap-bar", !on);
}

/** Collega i pulsanti: scegliere lo stile non tocca le icone se l'utente le ha già scelte (iconsOf). */
export function initAppearance() {
  for (const b of document.querySelectorAll("[data-look-theme]")) {
    b.addEventListener("click", () => choose({ theme: b.dataset.lookTheme }));
  }
  for (const b of document.querySelectorAll("[data-look-icons]")) {
    b.addEventListener("click", () => choose({ icons: b.dataset.lookIcons }));
  }
  const box = $("#look-recap-bar");
  box.checked = getPrefs().recapBar !== false;
  box.addEventListener("change", () => applyRecapBar(setPrefs({ recapBar: box.checked }).recapBar));
  applyRecapBar(box.checked);
  applyLook(getPrefs());
  mark();
}
