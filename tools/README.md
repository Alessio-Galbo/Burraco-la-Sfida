# Strumenti del progetto

| Strumento | Uso |
|---|---|
| `tools/build-ics.mjs` (+ `tools/ics/`) | `node tools/build-ics.mjs` → rigenera `web/calendar/*.ics` da `web/data/schedule.json` e `web/locales/it.json` (testi). Da rilanciare dopo ogni modifica agli orari |
| `tools/icons/` | Sorgenti SVG delle icone (maskable, apple) e `jobs.json`; l'icona "any" è `web/icons/icon.svg`. Rigenera i PNG da `jobs.json` con un rasterizzatore SVG → PNG (es. Chrome headless) e verifica le maschere Android/iOS |
