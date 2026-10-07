// Ordine degli eventi (uguale al widget Android): in corso per fine più vicina, poi in attesa per inizio più vicino.
import { test } from "node:test";
import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { computeAll, ordered } from "../web/js/schedule/index.js";

const schedule = JSON.parse(readFileSync(new URL("../web/data/schedule.json", import.meta.url), "utf8"));
const ids = (iso) => ordered(computeAll(schedule, Date.parse(iso))).map((s) => s.id);

test("venerdì 16:00:01: Baule (finisce sab 02:00) sopra Torte appena iniziate, poi Corsa", () => {
  assert.deepEqual(ids("2026-10-09T16:00:01+02:00"), ["baule", "torte", "corsa"]);
});

test("mercoledì 03:00: Corsa (finisce ven 00:00) sopra Baule (sab 02:00), poi Torte", () => {
  assert.deepEqual(ids("2026-10-07T03:00:00+02:00"), ["corsa", "baule", "torte"]);
});

test("lunedì 10:00: nessuno in corso, ordine per inizio più vicino", () => {
  assert.deepEqual(ids("2026-10-12T10:00:00+02:00"), ["corsa", "baule", "torte"]);
});
