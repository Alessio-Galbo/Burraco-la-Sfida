// Diagnostica notifiche (tab Avvisi): mostra cosa vede davvero il browser, per capire perché il permesso non arriva.
import { $, clone, fields } from "../core/dom.js";
import { t } from "../core/i18n.js";

const extra = {};

async function rows() {
  const has = "Notification" in window;
  let query = "—";
  try { query = (await navigator.permissions.query({ name: "notifications" })).state; } catch { /* API assente */ }
  const ua = navigator.userAgent.match(/(Chrome|Firefox|Version|SamsungBrowser|Edg)\/[\d.]+/g) || [];
  return [
    ["permission", has ? Notification.permission : "—"],
    ["query", query],
    ["secure", t(isSecureContext ? "diag.yes" : "diag.no")],
    ["sw", t(navigator.serviceWorker?.controller ? "diag.yes" : "diag.no")],
    ["mode", matchMedia("(display-mode: standalone)").matches ? "standalone" : "browser"],
    ["browser", ua.join(" ") || navigator.userAgent.slice(0, 60)],
    ...Object.entries(extra),
  ];
}

async function render() {
  const list = await rows();
  $("#diag").replaceChildren(...list.map(([k, v]) => {
    const el = clone("tpl-diag");
    const f = fields(el);
    f.k.textContent = t(`diag.${k}`);
    f.v.textContent = String(v);
    return el;
  }));
}

async function ask() {
  const t0 = performance.now();
  try {
    const r = await Notification.requestPermission();
    extra.asked = t("diag.ms", { result: r, ms: Math.round(performance.now() - t0) });
  } catch (e) { extra.asked = String(e); }
  render();
}

async function test() {
  try {
    const reg = await navigator.serviceWorker.ready;
    await reg.showNotification(t("diag.testResult"), { body: location.host, tag: "diag" });
    extra.testResult = t("diag.ok");
  } catch (e) { extra.testResult = `${e.name}: ${e.message}`; }
  render();
}

export function initDiag() {
  $("#diag-box").addEventListener("toggle", render);
  $("#diag-ask").addEventListener("click", ask);
  $("#diag-test").addEventListener("click", test);
}
