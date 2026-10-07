// Impostazioni degli avvisi: permesso del browser, evento per evento, anticipo (0/5/15/30 min), ricordati.
import { $, clone, fields } from "../core/dom.js";
import { t } from "../core/i18n.js";
import { getPrefs, setPrefs } from "../core/prefs.js";
import { fillEventName } from "./evicon.js";
import { LEAD_OPTIONS } from "../config.js";

const supported = () => "Notification" in window;

function showState() {
  const btn = $("#notify-enable");
  const perm = supported() ? Notification.permission : "unsupported";
  const key = { default: "alerts.ask", granted: "alerts.granted", denied: "alerts.denied", unsupported: "alerts.unsupported" }[perm];
  $("#notify-state").textContent = key ? t(key) : "";
  btn.hidden = perm !== "default";
}

/** Costruisce i controlli degli avvisi per gli eventi della schedule. */
export function initAlerts(schedule) {
  const prefs = getPrefs();
  $("#notify-events").replaceChildren(...schedule.events.map((ev) => {
    const el = clone("tpl-alert-event");
    const f = fields(el);
    fillEventName(f.name, ev.id, ev.icon);
    f.box.checked = Boolean(prefs.notify[ev.id]);
    f.box.addEventListener("change", () => {
      setPrefs({ notify: { ...getPrefs().notify, [ev.id]: f.box.checked } });
      if (f.box.checked && supported() && Notification.permission === "default") Notification.requestPermission().then(showState);
    });
    return el;
  }));
  const sel = $("#notify-lead");
  sel.replaceChildren(...LEAD_OPTIONS.map((m) => {
    const o = clone("tpl-option");
    o.value = String(m);
    o.textContent = t(`alerts.leadOptions.${m}`);
    return o;
  }));
  sel.value = String(prefs.lead);
  sel.addEventListener("change", () => setPrefs({ lead: Number(sel.value) }));
  $("#notify-enable").addEventListener("click", async () => {
    if (supported()) await Notification.requestPermission();
    showState();
    // Richiesta non mostrata (prompt silenziato da Chrome, browser interni delle app): spiega cosa fare.
    if (supported() && Notification.permission === "default") $("#notify-state").textContent = t("alerts.noPrompt");
  });
  showState();
}
