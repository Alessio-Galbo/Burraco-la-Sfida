// Riepilogo passivo delle Torte: finestra dom 22:00 -> lun 13:00, non cambia stato attivo né countdown.
import { test } from "node:test";
import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { computeAll } from "../web/js/schedule/index.js";

const schedule = JSON.parse(readFileSync(new URL("../web/data/schedule.json", import.meta.url), "utf8"));
const torte = (iso) => computeAll(schedule, Date.parse(iso)).find((s) => s.id === "torte");
const iso = (ms) => new Date(ms).toISOString();

test("durante il riepilogo: non attivo, prossimo inizio invariato", () => {
  const s = torte("2026-10-12T10:00:00+02:00");
  assert.equal(s.active, false);
  assert.equal(s.inRecap, true);
  assert.equal(iso(s.recap.start), "2026-10-11T20:00:00.000Z");
  assert.equal(iso(s.recap.end), "2026-10-12T11:00:00.000Z");
  assert.equal(iso(s.startsAt), "2026-10-16T14:00:00.000Z");
});

test("durante l'evento il riepilogo segue la fine dell'occorrenza", () => {
  const s = torte("2026-10-11T21:00:00+02:00");
  assert.equal(s.active, true);
  assert.equal(s.inRecap, false);
  assert.equal(iso(s.recap.start), iso(s.end));
});

test("dopo il riepilogo: nessun riepilogo in corso", () => {
  assert.equal(torte("2026-10-12T13:00:00+02:00").inRecap, false);
});
