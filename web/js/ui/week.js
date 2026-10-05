// Panoramica settimanale (giorni in ora italiana): barre sottili per evento, fasi delle Torte, linea "adesso".
import { $, clone, fields } from "../core/dom.js";
import { t, raw } from "../core/i18n.js";
import { phaseLabel } from "../core/format.js";
import { fillEventName } from "./evicon.js";
import { WEEK, DAY, mod, weekOffset, spanOf, toWall } from "../schedule/index.js";

const dayName = (d) => raw("week.days")[d - 1];
const stamp = (p, est) => `${est ? `${t("status.estimatedMark")} ` : ""}${dayName(p.day)} ${p.time}`;

function addSeg(track, from, len, cls, title) {
  const parts = from + len > WEEK ? [[from, WEEK - from], [0, from + len - WEEK]] : [[from, len]];
  for (const [a, l] of parts) {
    const seg = clone("tpl-week-seg");
    seg.classList.add(...cls);
    seg.style.setProperty("--from", String(a / WEEK));
    seg.style.setProperty("--len", String(l / WEEK));
    if (title) seg.title = title;
    track.append(seg);
  }
}

/** Costruisce intestazione dei giorni e una riga per evento. */
export function buildWeek(schedule) {
  $("#week-head").replaceChildren(...raw("week.days").map((d) => {
    const el = clone("tpl-day");
    el.textContent = d;
    return el;
  }));
  $("#week-rows").replaceChildren(...schedule.events.map((ev) => {
    const row = clone("tpl-week-row");
    const f = fields(row);
    row.classList.add(`ev--${ev.id}`);
    fillEventName(f.name, ev.id, ev.icon);
    f.range.textContent = t("week.range", { start: stamp(ev.start), end: stamp(ev.end, ev.end.estimated) });
    const s = weekOffset(ev.start);
    const dur = spanOf(ev.start, ev.end);
    if (!ev.phases.length) addSeg(f.track, s, dur, ["week-seg--ev"]);
    ev.phases.forEach((p, i) => {
      const off = mod(weekOffset(p.start) - s, WEEK);
      const next = ev.phases[i + 1];
      const offEnd = next ? mod(weekOffset(next.start) - s, WEEK) : dur;
      addSeg(f.track, mod(s + off, WEEK), offEnd - off, ["week-seg--ev", "week-seg--phase"], phaseLabel(p));
    });
    // Riepilogo passivo (es. Torte): tratto grigio dopo l'evento, nascosto con l'interruttore "Riepilogo nella barra".
    if (ev.recap) addSeg(f.track, mod(s + dur, WEEK), spanOf(ev.end, ev.recap.end), ["week-seg--recap"], t("phases.riepilogo"));
    return row;
  }));
}

/** Sposta la linea del momento attuale (ora di Roma). */
export function updateWeek(now) {
  const wall = toWall(now);
  const dow = (new Date(wall).getUTCDay() + 6) % 7;
  $("#week-rows").style.setProperty("--now", ((dow * DAY + mod(wall, DAY)) / WEEK).toFixed(5));
}
