// Preferenze dell'utente ricordate in localStorage (vista, notifiche, anticipo, tema, icone, riepilogo nella barra), con fallback in memoria.
import { PREFS_KEY } from "../config.js";

const DEFAULTS = { tab: "timer", view: "all", lead: 15, theme: "game", icons: null, recapBar: true, notify: { corsa: false, baule: false, torte: false } };
let cache = null;

export function readJson(key, fallback) {
  try {
    const v = localStorage.getItem(key);
    return v ? JSON.parse(v) : fallback;
  } catch {
    return fallback;
  }
}

export function writeJson(key, value) {
  try { localStorage.setItem(key, JSON.stringify(value)); } catch { /* storage non disponibile */ }
}

export function getPrefs() {
  if (!cache) {
    const saved = readJson(PREFS_KEY, {});
    cache = { ...DEFAULTS, ...saved, notify: { ...DEFAULTS.notify, ...(saved.notify || {}) } };
  }
  return cache;
}

export function setPrefs(patch) {
  cache = { ...getPrefs(), ...patch };
  writeJson(PREFS_KEY, cache);
  return cache;
}
