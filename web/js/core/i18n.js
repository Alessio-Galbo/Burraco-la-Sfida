// Testi dell'interfaccia da locales/it.json: t("chiave.annidata", {segnaposto}) e applicazione al DOM.
let dict = {};

export async function loadLocale(lang = "it") {
  const res = await fetch(`locales/${lang}.json`);
  dict = await res.json();
  document.documentElement.lang = lang;
  return dict;
}

/** Valore grezzo (stringa, array o oggetto) per una chiave "a.b.c". */
export function raw(key) {
  return key.split(".").reduce((o, k) => (o == null ? undefined : o[k]), dict);
}

/** Testo per la chiave, con i {segnaposto} sostituiti; se manca restituisce la chiave stessa. */
export function t(key, vars = {}) {
  const s = raw(key);
  if (typeof s !== "string") return key;
  return s.replace(/\{(\w+)\}/g, (m, k) => (k in vars ? String(vars[k]) : m));
}

const ATTRS = { i18nAria: "aria-label", i18nTitle: "title", i18nAlt: "alt", i18nContent: "content" };

/** Riempie gli elementi con data-i18n (testo) e data-i18n-aria/-title/-alt/-content (attributi). */
export function applyI18n(root = document) {
  for (const el of root.querySelectorAll("[data-i18n]")) el.textContent = t(el.dataset.i18n);
  for (const [prop, attr] of Object.entries(ATTRS)) {
    const sel = `[data-${prop.replace(/[A-Z]/g, (c) => "-" + c.toLowerCase())}]`;
    for (const el of root.querySelectorAll(sel)) el.setAttribute(attr, t(el.dataset[prop]));
  }
}
