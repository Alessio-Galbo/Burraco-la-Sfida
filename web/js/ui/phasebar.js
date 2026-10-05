// Barra delle Torte: segmenti proporzionali alle fasi (+ riepilogo tratteggiato), avanzamento, etichetta e countdown.
import { $, clone, setText } from "../core/dom.js";
import { t } from "../core/i18n.js";
import { countdown, short, approx, phaseLabel } from "../core/format.js";

const torteOf = (statuses) => statuses.find((s) => s.phases.length);
const partsOf = (s) => (s.recap ? [...s.phases, { ...s.recap, recap: true }] : s.phases);

export function buildPhaseBar(statuses) {
  const s = torteOf(statuses);
  $("#torte").hidden = !s;
  if (!s) return;
  $("#pbar").replaceChildren(...partsOf(s).map((p) => {
    const seg = clone("tpl-pseg");
    seg.style.flexGrow = String((p.end - p.start) / 3600000);
    seg.classList.toggle("pseg--recap", Boolean(p.recap));
    seg.title = p.recap ? t(`phases.${p.id}`) : phaseLabel(p);
    return seg;
  }));
}

function progress(seg, p, s, now) {
  const cur = p.recap ? Boolean(s.inRecap) : s.active && p.id === s.phase;
  seg.classList.toggle("is-done", !p.recap && (Boolean(s.inRecap) || (s.active && p.end <= now)));
  seg.classList.toggle("is-current", cur);
  seg.style.setProperty("--p", cur ? Math.min(1, (now - p.start) / (p.end - p.start)).toFixed(4) : "0");
}

export function updatePhaseBar(statuses, now) {
  const s = torteOf(statuses);
  if (!s) return;
  const parts = partsOf(s);
  [...$("#pbar").children].forEach((seg, i) => progress(seg, parts[i], s, now));
  setText($("#torte-days"), `${short(s.start)} → ${approx(s.estimatedEnd)}${short(s.end)}`);
  const ph = s.phases.find((p) => p.id === s.phase);
  if (s.active && ph) {
    setText($("#torte-phase"), phaseLabel(ph));
    setText($("#torte-left"), t("status.phaseEnds", { t: countdown(s.phaseUntil - now) }));
  } else if (s.inRecap) {
    setText($("#torte-phase"), t("status.recapUntil", { when: short(s.recap.end) }));
    setText($("#torte-left"), t("status.torteStarts", { t: countdown(s.startsAt - now) }));
  } else {
    setText($("#torte-phase"), t("status.torteFirst", { phase: phaseLabel(s.phases[0]) }));
    setText($("#torte-left"), t("status.torteStarts", { t: countdown(s.startsAt - now) }));
  }
  $("#torte").classList.toggle("is-active", s.active);
  $("#torte").classList.toggle("is-recap", Boolean(s.inRecap));
}
