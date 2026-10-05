// Verifica i calendari .ics generati da tools/ics: CRLF, folding, VTIMEZONE, ricorrenza, UID, promemoria.
import { test } from "node:test";
import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { buildCalendars, fold } from "../tools/ics/index.mjs";

const read = (p) => JSON.parse(readFileSync(new URL(p, import.meta.url), "utf8"));
const cals = buildCalendars(read("../web/data/schedule.json"), read("../web/locales/it.json"));
const count = (text, re) => (text.match(re) || []).length;
const enc = new TextEncoder();

test("file attesi", () => {
  assert.deepEqual(Object.keys(cals).sort(),
    ["burraco-baule.ics", "burraco-corsa.ics", "burraco-torte.ics", "burraco-tutti.ics"]);
});

test("CRLF e righe da massimo 75 ottetti", () => {
  for (const [name, text] of Object.entries(cals)) {
    assert.ok(text.endsWith("\r\n"), name);
    assert.equal(count(text, /(?<!\r)\n/g), 0, `${name}: LF senza CR`);
    for (const line of text.split("\r\n")) assert.ok(enc.encode(line).length <= 75, `${name}: ${line}`);
  }
});

test("folding non spezza i caratteri multibyte e si ricompone", () => {
  const long = "SUMMARY:" + "🎂 Torta d'oro àèìòù ".repeat(10);
  const folded = fold(long);
  assert.equal(folded.replace(/\r\n /g, ""), long);
  for (const l of folded.split("\r\n")) assert.ok(enc.encode(l).length <= 75);
});

test("struttura: VTIMEZONE, eventi, ricorrenza, promemoria", () => {
  for (const [name, text] of Object.entries(cals)) {
    assert.ok(text.startsWith("BEGIN:VCALENDAR\r\nVERSION:2.0\r\n"), name);
    assert.equal(count(text, /^BEGIN:VTIMEZONE\r$/gm), 1, name);
    assert.match(text, /^TZID:Europe\/Rome\r$/m);
    const ev = count(text, /^BEGIN:VEVENT\r$/gm);
    assert.equal(count(text, /^RRULE:FREQ=WEEKLY\r$/gm), ev, name);
    assert.equal(count(text, /^BEGIN:VALARM\r$/gm), ev * 2, name);
    assert.equal(count(text, /^TRIGGER:-PT15M\r$/gm), ev, name);
    assert.equal(count(text, /^DTSTART;TZID=Europe\/Rome:202610(0[5-9]|1[01])T\d{6}\r$/gm), ev, name);
  }
  assert.equal(count(cals["burraco-torte.ics"], /^BEGIN:VEVENT\r$/gm), 4);
  assert.equal(count(cals["burraco-tutti.ics"], /^BEGIN:VEVENT\r$/gm), 6);
});

test("orari della settimana del 5 ottobre 2026", () => {
  const corsa = cals["burraco-corsa.ics"];
  assert.match(corsa, /DTSTART;TZID=Europe\/Rome:20261005T120000\r\nDTEND;TZID=Europe\/Rome:20261009T000000/);
  const torte = cals["burraco-torte.ics"];
  assert.match(torte, /DTSTART;TZID=Europe\/Rome:20261009T160000\r\nDTEND;TZID=Europe\/Rome:20261010T000000/);
  assert.match(torte, /DTSTART;TZID=Europe\/Rome:20261011T000000\r\nDTEND;TZID=Europe\/Rome:20261011T220000/);
});

test("UID stabili e unici", () => {
  const uids = cals["burraco-tutti.ics"].match(/^UID:.+$/gm);
  assert.equal(new Set(uids).size, uids.length);
  assert.ok(uids.includes("UID:torte-oro@burraco-la-sfida.alessio-galbo.github.io"));
  assert.equal(buildCalendars(read("../web/data/schedule.json"), read("../web/locales/it.json"))["burraco-tutti.ics"],
    cals["burraco-tutti.ics"]);
});
