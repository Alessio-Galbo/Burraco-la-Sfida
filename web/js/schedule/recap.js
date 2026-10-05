// Riepilogo passivo dopo un evento (es. Torte): finestra [fine evento, recap.end), non cambia stato attivo né countdown.
import { fromWall } from "./rome.js";
import { spanOf } from "./week.js";

/**
 * Campi {inRecap, recap:{id,start,end}} per lo stato: la finestra in corso se l'istante vi cade,
 * altrimenti quella che segue l'occorrenza [startWall, startWall + dur). Vuoto se l'evento non ha recap.
 */
export function recapOf(ev, wall, elapsed, dur, startWall) {
  if (!ev.recap) return {};
  const len = spanOf(ev.end, ev.recap.end);
  const inRecap = elapsed >= dur && elapsed - dur < len;
  const from = inRecap ? wall - (elapsed - dur) : startWall + dur;
  return { inRecap, recap: { id: ev.recap.id, start: fromWall(from), end: fromWall(from + len) } };
}
