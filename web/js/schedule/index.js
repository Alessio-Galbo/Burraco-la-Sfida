// Punto d'ingresso del motore orari (browser e Node): stato di tutti gli eventi e "prossimo" evento.
import { eventStatus } from "./engine.js";

export { eventStatus } from "./engine.js";
export { toWall, fromWall } from "./rome.js";
export { WEEK, DAY, MINUTE, mod, weekOffset, spanOf } from "./week.js";

/** Stati di tutti gli eventi della schedule all'istante `now` (ms). */
export function computeAll(schedule, now) {
  return schedule.events.map((ev) => eventStatus(ev, now));
}

/** Evento non attivo che inizia per primo (o null). */
export function nextUp(statuses) {
  return statuses.filter((s) => !s.active).sort((a, b) => a.startsAt - b.startsAt)[0] || null;
}

/** Istante del prossimo cambio di stato (fine evento, fine fase o inizio), per i ricalcoli. */
export function nextChange(statuses) {
  const t = statuses.map((s) => (s.active ? Math.min(s.until, s.phaseUntil ?? Infinity) : s.startsAt));
  return Math.min(...t);
}
