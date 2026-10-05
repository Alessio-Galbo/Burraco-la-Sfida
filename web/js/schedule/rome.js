// Conversione tra istanti (ms UTC) e ora "da parete" di Europe/Rome, ora legale inclusa, senza librerie.
const fmt = new Intl.DateTimeFormat("en-GB", {
  timeZone: "Europe/Rome", hourCycle: "h23", year: "numeric", month: "2-digit", day: "2-digit",
  hour: "2-digit", minute: "2-digit", second: "2-digit",
});

/** Ora di Roma dell'istante, espressa come ms "finti UTC" (i campi del muro letti come se fossero UTC). */
export function toWall(ms) {
  const p = {};
  for (const { type, value } of fmt.formatToParts(new Date(ms))) p[type] = Number(value);
  const whole = Math.floor(ms / 1000) * 1000;
  return Date.UTC(p.year, p.month - 1, p.day, p.hour, p.minute, p.second) + (ms - whole);
}

/** Istante reale corrispondente a un'ora da parete di Roma (ms "finti UTC"). */
export function fromWall(wall) {
  const guess = wall - (toWall(wall) - wall);
  return wall - (toWall(guess) - guess);
}
