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
| `tests/` | [tests](docs/maps/tests.md) | 3 file, es. ics.test.mjs, schedule-cases.json, schedule.test.mjs |
| `tools/` | [tools](docs/maps/tools.md) | 5 file in ics/ |
| `web/` | [web](docs/maps/web.md) | 6 file in data/, js/, locales/ |
| `misc` | [misc](docs/maps/misc.md) | file nella root |
## Avvio e test
- servizio `PWA locale`: porta 8106 · `python -m http.server 8106 --directory web`
## Regole
- globali: `~/.claude/CLAUDE.md` e `~/.gemini/GEMINI.md` (generate da AI-hub)
## Skill attive
- per tag: app-icon-generation, headless-chrome-cdp, modern-web-guidance, pwa-service-worker-checklist
## Dati
- [android/](android/): 16 file .kt/.kts, nome = nomi vari (es. Schedule.kt), ultimo 2026-10-05
## Non qui
- `.agents/`, `.claude/`: config dei tool AI
<!-- hub:map:end -->
