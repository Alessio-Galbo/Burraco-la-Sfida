// Formattazione di countdown e date nel fuso del dispositivo, con i testi presi da it.json.
import { t } from "./i18n.js";

const pad = (n) => String(n).padStart(2, "0");
const whenFmt = new Intl.DateTimeFormat("it-IT", {
  weekday: "short", day: "numeric", month: "short", hour: "2-digit", minute: "2-digit",
});
const shortFmt = new Intl.DateTimeFormat("it-IT", { weekday: "short", hour: "2-digit", minute: "2-digit" });
const timeFmt = new Intl.DateTimeFormat("it-IT", { hour: "2-digit", minute: "2-digit" });

/** Durata in ms -> "2g 03:27:12" oppure "03:27:12". */
export function countdown(ms) {
  const total = Math.max(0, Math.floor(ms / 1000));
  const d = Math.floor(total / 86400);
  const vars = { d, h: pad(Math.floor(total / 3600) % 24), m: pad(Math.floor(total / 60) % 60), s: pad(total % 60) };
  return t(d > 0 ? "countdown.days" : "countdown.hours", vars);
}

/** Istante -> "lun 5 ott, 20:33" nel fuso locale. */
export const when = (ms) => whenFmt.format(new Date(ms));

/** Istante -> "ven 16:00" nel fuso locale (entro la settimana basta il giorno). */
export const short = (ms) => shortFmt.format(new Date(ms)).replace(",", "");

/** Istante -> "20:33" nel fuso locale. */
export const timeOf = (ms) => timeFmt.format(new Date(ms));

/** Prefisso "≈ " per gli orari stimati. */
export const approx = (estimated) => (estimated ? `${t("status.estimatedMark")} ` : "");

/** Nome della fase con modalità, es. "Torte in forno · 1vs1". */
export function phaseLabel(p) {
  const name = t(`phases.${p.id}`);
  return p.mode ? `${name} · ${p.mode}` : name;
}
