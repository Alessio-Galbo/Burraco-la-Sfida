// Verifica il motore orari della PWA su tutti i casi condivisi di tests/schedule-cases.json.
import { test } from "node:test";
import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { computeAll } from "../web/js/schedule/index.js";

const read = (p) => JSON.parse(readFileSync(new URL(p, import.meta.url), "utf8"));
const schedule = read("../web/data/schedule.json");
const { cases } = read("./schedule-cases.json");
const INSTANTS = ["until", "startsAt", "phaseUntil"];

for (const c of cases) {
  test(c.name, () => {
    const got = Object.fromEntries(computeAll(schedule, Date.parse(c.now)).map((s) => [s.id, s]));
    for (const [id, exp] of Object.entries(c.expect)) {
      assert.equal(got[id].active, exp.active, `${id}.active`);
      for (const k of INSTANTS) {
        if (k in exp) assert.equal(new Date(got[id][k]).toISOString(), new Date(exp[k]).toISOString(), `${id}.${k}`);
      }
      if ("phase" in exp) assert.equal(got[id].phase, exp.phase, `${id}.phase`);
    }
  });
}

test("istante non allineato al secondo", () => {
  const s = computeAll(schedule, Date.parse("2026-10-05T20:33:11.500+02:00"))[0];
  assert.equal(new Date(s.until).toISOString(), "2026-10-08T22:00:00.000Z");
});
