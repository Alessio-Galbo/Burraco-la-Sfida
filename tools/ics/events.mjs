// Costruisce i VEVENT settimanali (uno per evento, uno per fase delle Torte) da schedule.json e it.json.
import { weekOffset, spanOf, mod, WEEK } from "../../web/js/schedule/week.js";
import { TZID } from "./vtimezone.mjs";
import { escapeText, wallStamp, fill } from "./text.mjs";

const WEEK_MONDAY = Date.UTC(2026, 9, 5); // settimana di riferimento: lunedì 5 ottobre 2026
const UID_DOMAIN = "burraco-la-sfida.alessio-galbo.github.io";

function alarm(desc, trigger) {
  return ["BEGIN:VALARM", "ACTION:DISPLAY", `DESCRIPTION:${escapeText(desc)}`, `TRIGGER:${trigger}`, "END:VALARM"];
}

function vevent({ uid, summary, desc, startWall, endWall, stamp, alarmName, t }) {
  return [
    "BEGIN:VEVENT", `UID:${uid}@${UID_DOMAIN}`, `DTSTAMP:${stamp}`,
    `DTSTART;TZID=${TZID}:${wallStamp(startWall)}`, `DTEND;TZID=${TZID}:${wallStamp(endWall)}`,
    "RRULE:FREQ=WEEKLY", `SUMMARY:${escapeText(summary)}`, `DESCRIPTION:${escapeText(desc)}`,
    "TRANSP:TRANSPARENT",
    ...alarm(fill(t.ics.alarmStart, { name: alarmName }), "PT0S"),
    ...alarm(fill(t.ics.alarmBefore, { name: alarmName }), "-PT15M"),
    "END:VEVENT",
  ];
}

/** Righe VEVENT di un evento (Torte: una per fase). */
export function eventLines(ev, t, stamp) {
  const name = t.events[ev.id].name;
  const startWall = WEEK_MONDAY + weekOffset(ev.start);
  const dur = spanOf(ev.start, ev.end);
  const notes = [t.ics.desc, ev.end.estimated ? t.ics.estimatedDesc : "", t.ics.source].filter(Boolean);
  if (!ev.phases.length) {
    return vevent({ uid: ev.id, summary: `${ev.icon} ${name}`, desc: notes.join("\n"), startWall,
      endWall: startWall + dur, stamp, alarmName: name, t });
  }
  const s = weekOffset(ev.start);
  return ev.phases.flatMap((p, i) => {
    const off = mod(weekOffset(p.start) - s, WEEK);
    const next = ev.phases[i + 1];
    const offEnd = next ? mod(weekOffset(next.start) - s, WEEK) : dur;
    const phase = t.phases[p.id];
    const label = p.mode ? `${phase} (${p.mode})` : phase;
    const extra = [`${name}.`, p.mode ? fill(t.ics.modeDesc, { mode: p.mode }) : ""].filter(Boolean);
    const isLast = !next;
    const desc = [...extra, ...notes.filter((n) => isLast || n !== t.ics.estimatedDesc)].join("\n");
    return vevent({ uid: `${ev.id}-${p.id}`, summary: `${ev.icon} ${label}`, desc, startWall: startWall + off,
      endWall: startWall + offEnd, stamp, alarmName: label, t });
  });
}
