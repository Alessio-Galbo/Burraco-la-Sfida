// Selettore della vista "Tutti" / "Solo prossimo", ricordato tra le sessioni.
import { getPrefs, setPrefs } from "../core/prefs.js";
import { nextUp } from "../schedule/index.js";

/** Collega i pulsanti [data-view]; onChange viene chiamata a ogni cambio. */
export function initView(onChange) {
  const buttons = [...document.querySelectorAll("[data-view]")];
  const mark = () => {
    for (const b of buttons) b.setAttribute("aria-pressed", String(b.dataset.view === getPrefs().view));
  };
  for (const b of buttons) {
    b.addEventListener("click", () => {
      setPrefs({ view: b.dataset.view });
      mark();
      onChange();
    });
  }
  mark();
}

/** Id delle card da mostrare con la vista corrente. */
export function visibleIds(statuses) {
  if (getPrefs().view !== "next") return new Set(statuses.map((s) => s.id));
  const next = nextUp(statuses);
  return new Set(next ? [next.id] : []);
}
