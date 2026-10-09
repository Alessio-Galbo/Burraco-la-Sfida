// Relative time ("3 minutes ago", "tra 2 giorni", "now") with Intl.RelativeTimeFormat, and durations split for countdowns.
// AI-hub shared/js/date-format/relative.js v1.0.0 (copia: modifica nel hub, poi `hub shared sync`)
import { relativeTimeFormat } from "./intl.js";
import { toDate } from "./parse.js";

/** @type {[Intl.RelativeTimeFormatUnit, number][]} unit and how many of it make the next one (month = 30 days). */
const STEPS = [["second", 60], ["minute", 60], ["hour", 24], ["day", 30], ["month", 12], ["year", Infinity]];
const SECONDS = { second: 1, minute: 60, hour: 3600, day: 86400, week: 604800, month: 2592000, quarter: 7776000, year: 31104000 };

/**
 * @typedef {object} RelativeOptions
 * @property {string} [locale]
 * @property {number|Date} [now] reference instant (default Date.now(); inject it in tests)
 * @property {"long"|"short"|"narrow"} [style] default "long"
 * @property {"auto"|"always"} [numeric] default "auto" ("yesterday" instead of "1 day ago")
 * @property {"trunc"|"round"} [rounding] how to cut the value in the chosen unit (default "trunc")
 * @property {number} [nowWithin] under this many seconds -> "now" (default 60)
 * @property {string} [justNow] text used instead of Intl's "now" ("adesso", "just now")
 * @property {number} [until] seconds: at this distance or more -> null (caller shows a plain date)
 * @property {Intl.RelativeTimeFormatUnit} [unit] force one unit ("day": "3 giorni fa" at any distance)
 */

/**
 * Relative text of `value` against `now` (past = "ago", future = "in"). Invalid date or past `until` -> null.
 * @param {Date|number|string|null|undefined} value
 * @param {RelativeOptions} [opts]
 * @returns {string|null}
 */
export function formatRelative(value, opts = {}) {
  const d = toDate(value);
  if (!d) return null;
  const { locale, style = "long", numeric = "auto", rounding = "trunc", nowWithin = 60, justNow, until, unit } = opts;
  const now = opts.now === undefined ? Date.now() : Number(opts.now);
  const sec = (d.getTime() - now) / 1000;
  const abs = Math.abs(sec);
  if (until !== undefined && abs >= until) return null;
  const cut = (/** @type {number} */ x) => Math.sign(x) * (rounding === "round" ? Math.round(Math.abs(x)) : Math.trunc(Math.abs(x))) || 0;
  const f = relativeTimeFormat(locale, { style, numeric });
  if (unit) return f.format(cut(sec / SECONDS[/** @type {keyof typeof SECONDS} */ (unit.replace(/s$/, ""))]), unit);
  if (abs < nowWithin) return justNow ?? f.format(0, "second");
  let v = sec;
  for (const [u, size] of STEPS) {
    if (Math.abs(v) < size) return f.format(cut(v), u);
    v /= size;
  }
  return null;
}

/**
 * Milliseconds -> whole {d, h, m, s} (negative -> all zero), e.g. for "2g 03:27:12" countdowns.
 * @param {number} ms
 * @returns {{d: number, h: number, m: number, s: number}}
 */
export function splitDuration(ms) {
  const total = Math.max(0, Math.floor(Number(ms) / 1000)) || 0;
  return { d: Math.floor(total / 86400), h: Math.floor(total / 3600) % 24, m: Math.floor(total / 60) % 60, s: total % 60 };
}

/** 7 -> "07". @param {number} n @returns {string} */
export const pad2 = (n) => String(n).padStart(2, "0");
