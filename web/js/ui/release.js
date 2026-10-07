// Versione e peso dell'APK letti dall'ultima GitHub Release (se la rete o l'API non rispondono resta il testo statico).
import { $ } from "../core/dom.js";
import { t } from "../core/i18n.js";
import { RELEASE_API } from "../config.js";

const APK = "burraco-widget.apk";
const CACHE_KEY = "burraco.release.v2";
const MAX_AGE = 10 * 60 * 1000; // ricontrolla l'ultima release al massimo dopo 10 minuti

export async function initRelease() {
  const info = cached() || (await fetchLatest());
  if (!info) return;
  $("#apk-meta").textContent = t("android.meta", info);
}

function cached() {
  try {
    const c = JSON.parse(sessionStorage.getItem(CACHE_KEY) || "null");
    return c && Date.now() - c.at < MAX_AGE ? c.info : null;
  } catch {
    return null;
  }
}

async function fetchLatest() {
  try {
    const res = await fetch(RELEASE_API, { headers: { Accept: "application/vnd.github+json" } });
    if (!res.ok) return null;
    const rel = await res.json();
    const asset = (rel.assets || []).find((a) => a.name === APK);
    if (!asset) return null;
    const info = { version: rel.tag_name, size: Math.round(asset.size / 1024) };
    try { sessionStorage.setItem(CACHE_KEY, JSON.stringify({ info, at: Date.now() })); } catch { /* storage non disponibile */ }
    return info;
  } catch {
    return null;
  }
}
