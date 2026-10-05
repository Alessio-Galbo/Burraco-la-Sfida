// Avvisi mentre la pagina è aperta: inizio eventi e fasi, X minuti prima, senza doppioni (registro in localStorage).
import { t } from "../core/i18n.js";
import { getPrefs, readJson, writeJson } from "../core/prefs.js";
import { timeOf, phaseLabel } from "../core/format.js";
import { FIRED_KEY as KEY } from "../config.js";

const MINUTE_WINDOW = 10 * 60000; // oltre 10 minuti di ritardo l'avviso non ha più senso

/** Momenti da notificare per uno stato: inizio dell'occorrenza e inizio di ogni fase successiva alla prima. */
function targets(s) {
  const name = t(`events.${s.id}.name`);
  const title = t("alerts.startTitle", { icon: s.icon, name });
  const list = [{ key: `${s.id}`, at: s.start, title, body: null }];
  for (const p of s.phases.slice(1)) {
    list.push({ key: `${s.id}-${p.id}`, at: p.start, title, body: t("alerts.phaseBody", { phase: phaseLabel(p), time: timeOf(p.start) }) });
  }
  return list;
}

async function show(title, body, tag) {
  const opts = { body, tag, icon: "icons/icon-192.png", badge: "icons/icon-192.png" };
  try {
    const reg = await navigator.serviceWorker?.getRegistration();
    if (reg) return await reg.showNotification(title, opts);
  } catch { /* si ripiega sulla Notification API */ }
  new Notification(title, opts);
}

/** Da chiamare a ogni tick: invia gli avvisi dovuti per gli eventi attivati. */
export function checkNotifications(statuses, now) {
  if (!("Notification" in window) || Notification.permission !== "granted") return;
  const { notify, lead } = getPrefs();
  const fired = readJson(KEY, {});
  let changed = false;
  for (const s of statuses.filter((x) => notify[x.id])) {
    for (const tg of targets(s)) {
      const due = tg.at - lead * 60000;
      const id = `${tg.key}@${tg.at}`;
      if (now < due || now >= due + MINUTE_WINDOW || fired[id]) continue;
      fired[id] = now;
      changed = true;
      const body = tg.body || t(lead > 0 ? "alerts.startBody" : "alerts.startedBody", { time: timeOf(tg.at) });
      show(tg.title, body, id);
    }
  }
  if (!changed) return;
  for (const k of Object.keys(fired)) if (now - fired[k] > 8 * 86400000) delete fired[k];
  writeJson(KEY, fired);
}
