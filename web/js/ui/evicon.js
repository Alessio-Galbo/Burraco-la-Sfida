// Icona di un evento (emoji oppure SVG monocromatico secondo data-icons) e nome con icona, da template.
import { clone, fields } from "../core/dom.js";
import { t } from "../core/i18n.js";

/** Elemento icona per l'evento id con l'emoji indicata (in modalità SVG l'emoji resta nascosta). */
export function eventIcon(id, emoji) {
  const el = clone("tpl-evi");
  el.classList.add(`evi--${id}`);
  fields(el).e.textContent = emoji;
  return el;
}

/** Riempie el con icona + nome dell'evento (sostituisce il vecchio testo "emoji nome"). */
export function fillEventName(el, id, emoji, name = t(`events.${id}.name`)) {
  el.replaceChildren(eventIcon(id, emoji), " ", name);
}
