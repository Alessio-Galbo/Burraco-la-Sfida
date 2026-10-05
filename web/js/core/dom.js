// Utilità DOM: clonazione dei <template> e accesso ai campi marcati con data-f (niente HTML nelle stringhe).
export const $ = (sel, root = document) => root.querySelector(sel);

/** Clona il primo elemento del template #id. */
export function clone(id) {
  return document.getElementById(id).content.firstElementChild.cloneNode(true);
}

/** Mappa nome -> elemento per tutti i [data-f] dentro root (root compreso). */
export function fields(root) {
  const map = {};
  if (root.dataset.f) map[root.dataset.f] = root;
  for (const el of root.querySelectorAll("[data-f]")) map[el.dataset.f] = el;
  return map;
}

/** Aggiorna il testo solo se cambiato (evita ridisegni inutili ogni secondo). */
export function setText(el, text) {
  if (el && el.textContent !== text) el.textContent = text;
}

/** Inserisce in target i nodi di un file HTML parziale (es. partials/templates.html). */
export async function loadPartial(url, target) {
  const res = await fetch(url);
  const doc = new DOMParser().parseFromString(await res.text(), "text/html");
  for (const node of [...doc.head.children, ...doc.body.children]) target.append(document.importNode(node, true));
}
