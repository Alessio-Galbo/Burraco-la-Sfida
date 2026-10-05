// Avvio della PWA: carica testi, orari e sezioni, poi aggiorna righe evento, barra Torte, settimana e avvisi ogni secondo.
import { $, loadPartial } from "./core/dom.js";
import { loadLocale, applyI18n, t } from "./core/i18n.js";
import { computeAll } from "./schedule/index.js";
import * as ui from "./ui/index.js";
import { checkNotifications } from "./notify/notifier.js";
import { registerSW } from "./sw-register.js";

let schedule = null;

function tick() {
  const now = Date.now();
  const statuses = computeAll(schedule, now);
  ui.updateRows($("#events"), statuses, now, ui.visibleIds(statuses));
  ui.updatePhaseBar(statuses, now);
  ui.updateWeek(now);
  checkNotifications(statuses, now);
}

async function boot() {
  registerSW();
  await loadLocale("it");
  const [res] = await Promise.all([
    fetch("data/schedule.json"),
    loadPartial("partials/templates.html", $("#templates")),
    loadPartial("partials/guide.html", $("#guide")),
    loadPartial("partials/info.html", $("#info-part")),
  ]);
  schedule = await res.json();
  applyI18n();
  document.title = t("app.title");
  const [y, m, d] = schedule.updated.split("-");
  $("#updated").textContent = t("footer.updated", { date: `${d}/${m}/${y}` });
  const est = (ev) => [ev.start, ev.end, ...ev.phases.map((p) => p.start)].some((x) => x.estimated);
  $("#est-note").hidden = !schedule.events.some(est);
  const first = computeAll(schedule, Date.now());
  ui.buildRows($("#events"), first);
  ui.buildPhaseBar(first);
  ui.buildWeek(schedule);
  ui.initTabs();
  ui.initView(tick);
  ui.initAlerts(schedule);
  ui.initCalendars(schedule);
  ui.initAppearance();
  ui.initShots();
  ui.initShare();
  ui.initRelease();
  $("#events").addEventListener("toggle", tick, true);
  $("#loading").hidden = true;
  tick();
  setInterval(tick, 1000);
}

boot().catch((err) => {
  console.error(err);
  const el = $("#loading");
  el.textContent = t("app.loadError");
  el.classList.add("error");
});
