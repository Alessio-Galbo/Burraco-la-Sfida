// Genera web/calendar/*.ics da web/data/schedule.json e web/locales/it.json: node tools/build-ics.mjs
import { readFileSync, writeFileSync, mkdirSync } from "node:fs";
import { buildCalendars } from "./ics/index.mjs";

const root = new URL("../web/", import.meta.url);
const read = (p) => JSON.parse(readFileSync(new URL(p, root), "utf8"));
const outDir = new URL("calendar/", root);
mkdirSync(outDir, { recursive: true });
for (const [name, text] of Object.entries(buildCalendars(read("data/schedule.json"), read("locales/it.json")))) {
  writeFileSync(new URL(name, outDir), text, "utf8");
  console.log(`web/calendar/${name}`);
}
