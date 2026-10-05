// Stato di un evento settimanale in un istante: attivo fino a / prossimo inizio, fase corrente (Torte).
import { toWall, fromWall } from "./rome.js";
import { WEEK, mod, weekOffset, wallWeekOffset, spanOf } from "./week.js";
import { recapOf } from "./recap.js";

function phaseList(ev, startWall, dur) {
  const s = weekOffset(ev.start);
  return ev.phases.map((p, i) => {
    const off = mod(weekOffset(p.start) - s, WEEK);
    const next = ev.phases[i + 1];
    const offEnd = next ? mod(weekOffset(next.start) - s, WEEK) : dur;
    return { id: p.id, mode: p.mode, off, start: fromWall(startWall + off), end: fromWall(startWall + offEnd) };
  });
}

/**
 * Stato dell'evento all'istante `now` (ms). Restituisce sempre start/end dell'occorrenza corrente
 * (se attivo) o della prossima, più {active, until} oppure {active:false, startsAt}; per le fasi
 * anche phase/phaseUntil quando attivo; con "recap" anche inRecap e recap {id, start, end} (riepilogo passivo).
 */
export function eventStatus(ev, now) {
  const wall = toWall(now);
  const dur = spanOf(ev.start, ev.end);
  const elapsed = mod(wallWeekOffset(wall) - weekOffset(ev.start), WEEK);
  const active = elapsed < dur;
  const startWall = active ? wall - elapsed : wall + (WEEK - elapsed);
  const res = {
    id: ev.id, icon: ev.icon, active, start: fromWall(startWall), end: fromWall(startWall + dur),
    estimatedEnd: Boolean(ev.end.estimated), phases: phaseList(ev, startWall, dur),
    ...recapOf(ev, wall, elapsed, dur, startWall),
  };
  if (!active) return { ...res, startsAt: res.start };
  res.until = res.end;
  let cur = null;
  for (const p of res.phases) if (p.off <= elapsed) cur = p;
  if (cur) { res.phase = cur.id; res.phaseUntil = cur.end; }
  return res;
}
