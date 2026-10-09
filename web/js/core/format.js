// Formattazione di countdown e date nel fuso del dispositivo, con i testi presi da it.json.
import { t } from "./i18n.js";
import { formatDate, splitDuration, pad2 } from "../shared/date-format/index.js";

const IT = "it-IT";
const WHEN = { weekday: "short", day: "numeric", month: "short", hour: "2-digit", minute: "2-digit" };
const SHORT = { weekday: "short", hour: "2-digit", minute: "2-digit" };

/** Durata in ms -> "2g 03:27:12" oppure "03:27:12". */
export function countdown(ms) {
  const { d, h, m, s } = splitDuration(ms);
  return t(d > 0 ? "countdown.days" : "countdown.hours", { d, h: pad2(h), m: pad2(m), s: pad2(s) });
}

/** Istante -> "lun 5 ott, 20:33" nel fuso locale. */
export const when = (ms) => formatDate(ms, { locale: IT, intl: WHEN });

/** Istante -> "ven 16:00" nel fuso locale (entro la settimana basta il giorno). */
export const short = (ms) => formatDate(ms, { locale: IT, intl: SHORT }).replace(",", "");

/** Istante -> "20:33" nel fuso locale. */
export const timeOf = (ms) => formatDate(ms, { locale: IT, preset: "time" });

/** Prefisso "≈ " per gli orari stimati. */
export const approx = (estimated) => (estimated ? `${t("status.estimatedMark")} ` : "");

/** Nome della fase con modalità, es. "Torte in forno · 1vs1". */
export function phaseLabel(p) {
  const name = t(`phases.${p.id}`);
  return p.mode ? `${name} · ${p.mode}` : name;
}
