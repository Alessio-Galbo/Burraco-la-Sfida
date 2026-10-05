// Righe evento compatte ed espandibili: icona, nome, sottoriga, stato e countdown; in corso in alto.
import { clone, fields, setText } from "../core/dom.js";
import { t, applyI18n } from "../core/i18n.js";
import { countdown, when, short, approx, phaseLabel } from "../core/format.js";
import { nextUp } from "../schedule/index.js";
import { renderPhaseList } from "./phaselist.js";
import { eventIcon } from "./evicon.js";

const rows = new Map();

export function buildRows(container, statuses) {
  for (const s of statuses) {
    const el = clone("tpl-event");
    el.classList.add(`ev--${s.id}`);
    applyI18n(el);
    const f = fields(el);
    f.icon.replaceChildren(eventIcon(s.id, s.icon));
    f.icon.setAttribute("role", "img");
    f.icon.setAttribute("aria-label", t(`events.${s.id}.iconLabel`));
    f.name.textContent = t(`events.${s.id}.name`);
    f.phases.hidden = !s.phases.length;
    container.append(el);
    rows.set(s.id, { el, f });
  }
}

function subLine(s) {
  const ph = s.active && s.phases.find((p) => p.id === s.phase);
  if (ph) return phaseLabel(ph);
  if (s.active) return t("status.endsAt", { when: approx(s.estimatedEnd) + short(s.until) });
  return t("status.startsAt", { when: short(s.startsAt) });
}

/** Aggiorna testi e ordine (in corso prima, poi per inizio); visible = id da mostrare. */
export function updateRows(container, statuses, now, visible) {
  const next = nextUp(statuses);
  const ordered = [...statuses].sort((a, b) => (b.active - a.active) || (a.start - b.start));
  const order = ordered.map((s) => s.id).join();
  if (container.dataset.order !== order) {
    for (const s of ordered) container.append(rows.get(s.id).el);
    container.dataset.order = order;
  }
  for (const s of statuses) {
    const { el, f } = rows.get(s.id);
    const isNext = !s.active && next && next.id === s.id;
    el.hidden = !visible.has(s.id);
    el.classList.toggle("is-active", s.active);
    el.classList.toggle("is-next", Boolean(isNext));
    setText(f.stateText, t(s.active ? "status.active" : isNext ? "status.next" : "status.waiting"));
    setText(f.sub, subLine(s));
    setText(f.count, countdown((s.active ? s.until : s.startsAt) - now));
    setText(f.start, when(s.start));
    setText(f.end, approx(s.estimatedEnd) + when(s.end));
    const p = s.active ? Math.min(1, Math.max(0, (now - s.start) / (s.end - s.start))) : 0;
    f.track.style.setProperty("--p", p.toFixed(4));
    if (s.phases.length && el.open) renderPhaseList(f.phases, s, now);
  }
}
