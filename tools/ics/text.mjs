// Utilità testuali RFC 5545: escape dei valori TEXT, folding a 75 ottetti, formato data locale, segnaposto.
const enc = new TextEncoder();

/** Escape di un valore TEXT (backslash, punto e virgola, virgola, a capo). */
export function escapeText(s) {
  return String(s).replace(/\\/g, "\\\\").replace(/;/g, "\\;").replace(/,/g, "\\,").replace(/\r?\n/g, "\\n");
}

/** Spezza una riga logica in righe da max 75 ottetti, senza tagliare i caratteri multibyte. */
export function fold(line) {
  const out = [];
  let cur = "";
  let bytes = 0;
  for (const ch of line) {
    const n = enc.encode(ch).length;
    const max = out.length ? 74 : 75; // le righe di continuazione iniziano con uno spazio
    if (bytes + n > max) { out.push(cur); cur = ""; bytes = 0; }
    cur += ch;
    bytes += n;
  }
  out.push(cur);
  return out.join("\r\n ");
}

/** Unisce le righe logiche con CRLF, applicando il folding; termina con CRLF. */
export function serialize(lines) {
  return lines.map(fold).join("\r\n") + "\r\n";
}

/** ms "finti UTC" (ora da parete) -> YYYYMMDDTHHMMSS. */
export function wallStamp(wall) {
  return new Date(wall).toISOString().slice(0, 19).replace(/[-:]/g, "");
}

/** Sostituisce {chiave} con i valori di vars. */
export function fill(tpl, vars = {}) {
  return tpl.replace(/\{(\w+)\}/g, (m, k) => (k in vars ? String(vars[k]) : m));
}
