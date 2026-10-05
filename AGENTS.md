# Burraco la Sfida – Timer eventi

Timer degli eventi settimanali di "Burraco Online: La Sfida": **PWA** consultabile online (anche da iOS)
e **app Android leggerissima** che contiene solo i widget per la schermata Home. Repo pubblica:
https://github.com/Alessio-Galbo/Burraco-la-Sfida

## Struttura

| Percorso | Contenuto |
|---|---|
| `web/data/schedule.json` | **Unica fonte degli orari** (ora italiana). La leggono PWA, generatore .ics e Android |
| `tests/schedule-cases.json` | Casi condivisi istante → stato atteso: PWA e Android devono passarli tutti |
| `web/` | PWA statica, senza build (ES modules, `locales/it.json`, service worker) |
| `web/calendar/*.ics` | Calendari generati da `tools/build-ics.mjs` (non modificarli a mano) |
| `web/img/widgets/` | Screenshot dei widget mostrati nella pagina download |
| `android/` | Progetto Gradle (Kotlin, nessuna libreria AndroidX): widget + notifiche locali |
| `.github/workflows/` | Pubblicazione su GitHub Pages e APK nelle GitHub Releases |
| `PROGRESS.md` | A che punto siamo |

## Regole

- Testi dell'interfaccia: in `web/locales/it.json` e `android/.../res/values/strings.xml`, mai nel codice.
  Niente CSS/JS inline, niente HTML nelle stringhe JS.
- File di codice ≤ 100 righe (avviso a 90, `.linelimits`); prima riga = commento che dice a cosa serve.
- Orari: settimanali in ora di Roma (`Europe/Rome`, il cambio dell'ora legale è gestito). Inizio incluso, fine esclusa.
  L'evento è attivo se l'istante cade in [inizio, fine) del ciclo settimanale (anche a cavallo della domenica).
- Preferenze dell'utente (vista, notifiche) sempre ricordate (localStorage / SharedPreferences).
- Server locale: `python -m http.server 8106 --directory web` → http://localhost:8106/ (porta fissa del progetto).
- Commit in inglese.
- Firma APK: chiave in `%USERPROFILE%\.bls-tracker` e copia in `keys/` (esclusa da git, MAI pubblicarla); release con `git tag vX.Y.Z` (vedi README).

## Comandi

- Test PWA: `node --test "tests/*.test.mjs"`
- Calendari: `node tools/build-ics.mjs`
- APK: `cd android && gradlew.bat assembleRelease` (JDK 17 e Android SDK 36)
- Test Android: `cd android && gradlew.bat test`
- Limiti righe: regole in `.linelimits` (≤100 righe, avviso a 90).

<!-- hub:map:start -->
## Mappe
| Area | Mappa | Contenuto |
|---|---|---|
| `tests/` | [tests](docs/maps/tests.md) | 4 file, es. ics.test.mjs, recap.test.mjs, schedule-cases.json |
| `tools/` | [tools](docs/maps/tools.md) | Strumenti del progetto |
| `web/` | [web](docs/maps/web.md) | 4 file in data/, locales/ |
| `web/css/` | [web-css](docs/maps/web-css.md) | 12 file, es. base.css, events.css, guide.css |
| `web/js/` | [web-js](docs/maps/web-js.md) | 30 file in core/, notify/, schedule/, ui/ |
| `web/partials/` | [web-partials](docs/maps/web-partials.md) | 3 file, es. guide.html, info.html, templates.html |
| `misc` | [misc](docs/maps/misc.md) | file nella root e cartelle piccole: docs/ |
## Avvio e test
- servizio `PWA locale`: porta 8106 · `python -m http.server 8106 --directory web`
## Regole
- globali: `~/.claude/CLAUDE.md` e `~/.gemini/GEMINI.md` (generate da AI-hub)
## Skill attive
- per tag: android-appwidget-fluid, android-cli, android-release-signing, app-icon-generation, headless-chrome-cdp, modern-web-guidance, pwa-service-worker-checklist, recurring-weekly-schedule, web-share-social-preview
## Dati
- [android/](android/): 128 file .xml/.kt, nome = nomi vari (es. AndroidManifest.xml), ultimo 2026-10-06
- [reference/](reference/): 307 file .png/.txt, nome = nomi vari (es. all_140x140.png), ultimo 2026-10-06
- [web/img/](web/img/): 27 file .svg/.png, nome = nomi vari (es. android.svg), ultimo 2026-10-06
## Non qui
- `.agents/`, `.claude/`, `.github/`: config dei tool AI
- `keys/`: ignorata da git
<!-- hub:map:end -->
