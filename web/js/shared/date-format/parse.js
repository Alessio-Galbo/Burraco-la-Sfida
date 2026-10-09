// Turn dates/timestamps/ISO strings into Date and calendar-day keys (YYYY-MM-DD) in local time or in a time zone.
// AI-hub shared/js/date-format/parse.js v1.0.0 (copia: modifica nel hub, poi `hub shared sync`)
import { dateTimeFormat } from "./intl.js";

const DAY_ONLY = /^\d{4}-\d{2}-\d{2}$/;
const pad2 = (/** @type {number} */ n) => String(n).padStart(2, "0");

/**
 * Date | ms | ISO string -> Date, or null when missing/invalid.
 * "YYYY-MM-DD" alone is a LOCAL calendar day (new Date("2026-10-09") would be UTC midnight and shift the day);
 * "YYYY-MM-DD HH:MM" (space, as SQLite/Python write it) is read as local "YYYY-MM-DDTHH:MM".
 * @param {Date|number|string|null|undefined} value
 * @returns {Date|null}
 */
export function toDate(value) {
  if (value === null || value === undefined || value === "") return null;
  let d;
  if (value instanceof Date) d = new Date(value.getTime());
  else if (typeof value === "number") d = new Date(value);
  else {
    const s = String(value).trim();
    if (DAY_ONLY.test(s)) {
      const [y, m, day] = s.split("-").map(Number);
      d = new Date(y, m - 1, day);
    } else d = new Date(s.replace(/^(\d{4}-\d{2}-\d{2}) (\d)/, "$1T$2"));
  }
  return Number.isNaN(d.getTime()) ? null : d;
}

/**
 * Local calendar day "YYYY-MM-DD" (never toISOString, which is UTC). Invalid -> "".
 * @param {Date|number|string|null|undefined} value
 * @returns {string}
 */
export function toISODate(value) {
  const d = toDate(value);
  return d ? `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())}` : "";
}

/** Today as a local "YYYY-MM-DD". @param {Date} [now] @returns {string} */
export const todayISO = (now = new Date()) => toISODate(now);

/**
 * Calendar day "YYYY-MM-DD" of an instant in `timeZone` (IANA name; omitted or invalid -> device zone). Invalid -> "".
 * @param {Date|number|string|null|undefined} value
 * @param {string} [timeZone]
 * @returns {string}
 */
export function dayKey(value, timeZone) {
  const d = toDate(value);
  if (!d) return "";
  if (!timeZone) return toISODate(d);
  return dateTimeFormat("en-CA", { year: "numeric", month: "2-digit", day: "2-digit" }, timeZone).format(d);
}

/**
 * Whole calendar days from `from` to `to` (to - from), DST-safe: 0 = same day, 1 = `to` is the day after.
 * @param {Date|number|string|null|undefined} from
 * @param {Date|number|string} [to] default now
 * @param {string} [timeZone] days counted in this zone (default device zone)
 * @returns {number} NaN when a date is invalid
 */
export function dayDiff(from, to = new Date(), timeZone) {
  const a = dayKey(from, timeZone), b = dayKey(to, timeZone);
  if (!a || !b) return NaN;
  const utc = (/** @type {string} */ k) => Date.UTC(+k.slice(0, 4), +k.slice(5, 7) - 1, +k.slice(8, 10));
  return Math.round((utc(b) - utc(a)) / 86400000);
}
