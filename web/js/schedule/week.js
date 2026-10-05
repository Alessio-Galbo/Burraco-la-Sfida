// Aritmetica del ciclo settimanale: posizioni nella settimana (lunedì 00:00 = 0) in millisecondi.
export const MINUTE = 60000;
export const DAY = 86400000;
export const WEEK = 7 * DAY;

export const mod = (a, n) => ((a % n) + n) % n;

/** {day: 1..7, time: "HH:MM"} -> ms dall'inizio della settimana. */
export function weekOffset({ day, time }) {
  const [h, m] = time.split(":").map(Number);
  return (day - 1) * DAY + h * 3600000 + m * MINUTE;
}

/** Ora da parete ("finti UTC") -> ms dall'inizio della sua settimana (lunedì 00:00). */
export function wallWeekOffset(wall) {
  const dow = (new Date(wall).getUTCDay() + 6) % 7;
  return dow * DAY + mod(wall, DAY);
}

/** Durata dell'intervallo settimanale [start, end), anche a cavallo della domenica. */
export function spanOf(start, end) {
  return mod(weekOffset(end) - weekOffset(start), WEEK) || WEEK;
}
