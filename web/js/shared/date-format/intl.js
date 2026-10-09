// Cached Intl.DateTimeFormat / RelativeTimeFormat that never throw on a bad locale or time zone (fallback: en, device zone).
// AI-hub shared/js/date-format/intl.js v1.0.0 (copia: modifica nel hub, poi `hub shared sync`)
const dtf = new Map();
const rtf = new Map();

/**
 * One cached Intl.DateTimeFormat per (locale, options, timeZone).
 * Invalid locale (e.g. a corrupted preference "@@") -> "en"; invalid time zone -> device zone.
 * @param {string|undefined} locale BCP 47 tag ("it", "en-GB"); undefined = runtime default
 * @param {Intl.DateTimeFormatOptions} options
 * @param {string} [timeZone] IANA name ("Europe/Rome")
 * @returns {Intl.DateTimeFormat}
 */
export function dateTimeFormat(locale, options, timeZone) {
  const key = `${locale ?? ""}|${JSON.stringify(options)}|${timeZone ?? ""}`;
  let f = dtf.get(key);
  if (!f) {
    const zoned = timeZone ? { ...options, timeZone } : options;
    /** @type {[string|undefined, Intl.DateTimeFormatOptions][]} */
    const attempts = [[locale, zoned], ["en", zoned], [locale, options], ["en", options]];
    for (const [loc, opts] of attempts) {
      try { f = new Intl.DateTimeFormat(loc, opts); break; } catch { /* next attempt */ }
    }
    dtf.set(key, f);
  }
  return /** @type {Intl.DateTimeFormat} */ (f);
}

/**
 * One cached Intl.RelativeTimeFormat per (locale, options); invalid locale -> "en".
 * @param {string|undefined} locale
 * @param {Intl.RelativeTimeFormatOptions} options
 * @returns {Intl.RelativeTimeFormat}
 */
export function relativeTimeFormat(locale, options) {
  const key = `${locale ?? ""}|${JSON.stringify(options)}`;
  let f = rtf.get(key);
  if (!f) {
    try { f = new Intl.RelativeTimeFormat(locale, options); } catch { f = new Intl.RelativeTimeFormat("en", options); }
    rtf.set(key, f);
  }
  return f;
}
