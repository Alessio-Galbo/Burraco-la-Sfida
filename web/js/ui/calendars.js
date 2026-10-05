// Calendari .ics: "Aggiungi" (download con promemoria) per ognuno; abbonamento facoltativo al calendario completo.
import { $, clone, fields } from "../core/dom.js";
import { t, applyI18n } from "../core/i18n.js";
import { CALENDARS, WEBCAL_BASE, SITE_URL } from "../config.js";
import { fillEventName } from "./evicon.js";

const ALL_FILE = "burraco-tutti.ics";

export function initCalendars(schedule) {
  const icons = Object.fromEntries(schedule.events.map((ev) => [ev.id, ev.icon]));
  $("#cal-list").replaceChildren(...CALENDARS.map((id) => {
    const el = clone("tpl-calendar");
    applyI18n(el);
    const f = fields(el);
    const file = `burraco-${id}.ics`;
    const name = t(`calendars.items.${id}`);
    if (icons[id]) fillEventName(f.name, id, icons[id], name);
    else f.name.textContent = name;
    f.add.href = `calendar/${file}`;
    f.add.setAttribute("download", file);
    return el;
  }));
  $("#cal-webcal").href = WEBCAL_BASE + ALL_FILE;
  $("#cal-copy").addEventListener("click", copyLink);
}

async function copyLink() {
  try {
    await navigator.clipboard.writeText(`${SITE_URL}calendar/${ALL_FILE}`);
    $("#cal-copied").hidden = false;
  } catch { /* clipboard non disponibile: il link resta nel pulsante iPhone */ }
}
