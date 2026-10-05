// Tab a segmenti: una sezione visibile alla volta, tab ricordata tra le sessioni (o scelta dall'ancora #id).
import { getPrefs, setPrefs } from "../core/prefs.js";

export function initTabs() {
  const buttons = [...document.querySelectorAll("[data-tab]")];
  const ids = buttons.map((b) => b.dataset.tab);
  const show = (id) => {
    for (const b of buttons) {
      const on = b.dataset.tab === id;
      b.setAttribute("aria-selected", String(on));
      document.getElementById(b.dataset.tab).hidden = !on;
    }
  };
  for (const b of buttons) {
    b.addEventListener("click", () => {
      setPrefs({ tab: b.dataset.tab });
      show(b.dataset.tab);
      window.scrollTo(0, 0);
    });
  }
  const fromHash = location.hash.slice(1);
  const first = ids.includes(fromHash) ? fromHash : getPrefs().tab;
  show(ids.includes(first) ? first : ids[0]);
}
