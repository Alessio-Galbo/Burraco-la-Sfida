// Elenco delle fasi delle Torte nel dettaglio espanso: fascia oraria di ognuna, corrente evidenziata, riepilogo passivo in grigio.
import { clone, fields, setText } from "../core/dom.js";
import { t } from "../core/i18n.js";
import { short, approx } from "../core/format.js";

/** Fasi dell'occorrenza più l'eventuale riepilogo (senza partecipazione) come ultima riga. */
const rowsOf = (s) => (s.recap ? [...s.phases, { ...s.recap, recap: true }] : s.phases);

export function renderPhaseList(list, s, now) {
  const rows = rowsOf(s);
  if (list.children.length !== rows.length) list.replaceChildren(...rows.map(() => clone("tpl-phase")));
  rows.forEach((p, i) => {
    const li = list.children[i];
    const f = fields(li);
    const current = p.recap ? Boolean(s.inRecap) : s.active && p.id === s.phase;
    const done = !p.recap && (Boolean(s.inRecap) || (s.active && p.end <= now));
    li.classList.toggle("phase--recap", Boolean(p.recap));
    li.classList.toggle("is-current", current);
    li.classList.toggle("is-done", done);
    setText(f.name, t(`phases.${p.id}`));
    const mode = p.recap ? t("phases.recapNote") : p.mode;
    setText(f.mode, mode || "");
    f.mode.hidden = !mode;
    const est = i === s.phases.length - 1 && s.estimatedEnd;
    setText(f.time, t("status.range", { from: short(p.start), to: approx(est) + short(p.end) }));
  });
}
