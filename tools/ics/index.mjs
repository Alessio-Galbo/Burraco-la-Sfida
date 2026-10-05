// Genera il testo dei calendari .ics (tutti + uno per evento) a partire da schedule.json e it.json.
import { VTIMEZONE, TZID } from "./vtimezone.mjs";
import { eventLines } from "./events.mjs";
import { escapeText, serialize, fill } from "./text.mjs";

export { fold, serialize, escapeText } from "./text.mjs";

function calendar(calName, events, schedule, t) {
  const stamp = `${schedule.updated.replace(/-/g, "")}T000000Z`;
  return serialize([
    "BEGIN:VCALENDAR", "VERSION:2.0", "PRODID:-//Burraco la Sfida//Timer eventi//IT", "CALSCALE:GREGORIAN",
    "METHOD:PUBLISH", `X-WR-CALNAME:${escapeText(calName)}`, `X-WR-TIMEZONE:${TZID}`,
    "REFRESH-INTERVAL;VALUE=DURATION:P1D", "X-PUBLISHED-TTL:P1D",
    ...VTIMEZONE,
    ...events.flatMap((ev) => eventLines(ev, t, stamp)),
    "END:VCALENDAR",
  ]);
}

/** { "burraco-tutti.ics": testo, "burraco-<id>.ics": testo, ... } */
export function buildCalendars(schedule, t) {
  const out = { "burraco-tutti.ics": calendar(t.ics.allName, schedule.events, schedule, t) };
  for (const ev of schedule.events) {
    out[`burraco-${ev.id}.ics`] = calendar(fill(t.ics.calName, { name: t.events[ev.id].name }), [ev], schedule, t);
  }
  return out;
}
