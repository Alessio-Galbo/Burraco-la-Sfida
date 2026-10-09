// Start-end ranges: event start/end in a zone (no repeated day, end shown only when useful) and year ranges ("2019 — Present").
// AI-hub shared/js/date-format/range.js v1.0.0 (copia: modifica nel hub, poi `hub shared sync`)
import { formatDate } from "./format.js";
import { dayKey } from "./parse.js";

/**
 * @typedef {object} RangeOptions
 * @property {string} [locale]
 * @property {string} [timeZone] the zone the range is shown in (also decides "same day")
 * @property {boolean} [startTime] show the start time (else only the date)
 * @property {boolean} [endTime] show the end time
 * @property {string} [invalid] text for an invalid date (default "Invalid Date", like toLocaleString)
 */

/**
 * {start, end?}: end omitted when there is none or it is the same day without an end time;
 * same day with an end time -> end is only the time ("9 Oct 2026, 14:00" + "16:00").
 * @param {Date|number|string} start
 * @param {Date|number|string|null|undefined} end
 * @param {RangeOptions} [opts]
 * @returns {{start: string, end?: string}}
 */
export function formatRangeParts(start, end, opts = {}) {
  const { locale, timeZone, startTime = false, endTime = false, invalid = "Invalid Date" } = opts;
  /** @param {Date|number|string} v @param {boolean} withDate @param {boolean} withTime */
  const fmt = (v, withDate, withTime) => formatDate(v, {
    locale, timeZone, fallback: invalid, preset: withDate && withTime ? "dateTime" : withTime ? "time" : "date",
  });
  const s = fmt(start, true, startTime);
  if (end === null || end === undefined || end === "") return { start: s };
  const sameDay = dayKey(start, timeZone) === dayKey(end, timeZone);
  if (sameDay && !endTime) return { start: s };
  return { start: s, end: fmt(end, !sameDay, endTime) };
}

/**
 * "start - end" or just "start".
 * @param {{start: string, end?: string}} parts
 * @param {string} [sep]
 * @returns {string}
 */
export const joinRange = (parts, sep = " - ") => (parts.end ? `${parts.start}${sep}${parts.end}` : parts.start);

/**
 * Year range for CVs/timelines: "2019 — 2023", "2019" (no/same end), "2019 — Present" (ongoing).
 * @param {string|number|null|undefined} startYear
 * @param {string|number|null|undefined} endYear
 * @param {{ongoing?: boolean, presentLabel?: string, separator?: string}} [opts]
 * @returns {string} "" without a start
 */
export function formatYearRange(startYear, endYear, opts = {}) {
  const { ongoing = false, presentLabel = "Present", separator = " — " } = opts;
  if (!startYear) return "";
  if (ongoing) return `${startYear}${separator}${presentLabel}`;
  if (!endYear || String(endYear) === String(startYear)) return `${startYear}`;
  return `${startYear}${separator}${endYear}`;
}
