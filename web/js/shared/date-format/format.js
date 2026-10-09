// Format one date/time with Intl in a given language and time zone: named presets or any Intl options.
// AI-hub shared/js/date-format/format.js v1.0.0 (copia: modifica nel hub, poi `hub shared sync`)
import { dateTimeFormat } from "./intl.js";
import { toDate } from "./parse.js";

/** Named option sets (sample outputs for it / en-GB). */
export const PRESETS = Object.freeze({
  /** "9 ott 2026" / "9 Oct 2026" */
  date: { year: "numeric", month: "short", day: "numeric" },
  /** "09/10/2026" (day-first in it and en-GB) */
  numeric: { day: "2-digit", month: "2-digit", year: "numeric" },
  /** "9 ott" / "9 Oct" */
  dayMonth: { day: "numeric", month: "short" },
  /** "14:05" */
  time: { hour: "2-digit", minute: "2-digit" },
  /** "14:05:09" */
  timeSeconds: { hour: "2-digit", minute: "2-digit", second: "2-digit" },
  /** "9 ott 2026, 14:05" */
  dateTime: { year: "numeric", month: "short", day: "numeric", hour: "2-digit", minute: "2-digit" },
  /** "9 ott, 14:05" */
  dayMonthTime: { day: "numeric", month: "short", hour: "2-digit", minute: "2-digit" },
  /** "09/10/26, 14:05" */
  short: { dateStyle: "short", timeStyle: "short" },
  /** "ottobre" ("Ottobre" with capitalize) */
  month: { month: "long" },
});

/**
 * @typedef {object} FormatOptions
 * @property {string} [locale] BCP 47 tag; omitted = runtime default language
 * @property {string} [timeZone] IANA zone; omitted = device zone
 * @property {keyof typeof PRESETS} [preset] base option set (default "date"), overridden by `intl`
 * @property {Intl.DateTimeFormatOptions} [intl] extra/override Intl options (used alone when preset is omitted)
 * @property {boolean} [capitalize] first letter upper case ("ottobre" -> "Ottobre")
 * @property {string} [fallback] returned for a missing/invalid date (default "")
 */

/**
 * Format a date. Strings "YYYY-MM-DD" are local calendar days (see toDate).
 * @param {Date|number|string|null|undefined} value
 * @param {FormatOptions} [opts]
 * @returns {string}
 */
export function formatDate(value, opts = {}) {
  const d = toDate(value);
  if (!d) return opts.fallback ?? "";
  const base = opts.preset ? PRESETS[opts.preset] : opts.intl ? {} : PRESETS.date;
  const text = dateTimeFormat(opts.locale, { ...base, ...opts.intl }, opts.timeZone).format(d);
  return opts.capitalize && text ? text.charAt(0).toUpperCase() + text.slice(1) : text;
}
