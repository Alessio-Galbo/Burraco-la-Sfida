# Avanzamento

## Ora
- [x] [approvato per l'AI] Make the check Burraco la Sfida / limiti-righe pass
  - Prova: `burraco_widget_apk_guide.html` (mockup locale, ignorato da git) escluso in `.linelimits`;
    `check_line_limits.py --root .` → "104 file controllati: 0 oltre 100 righe, 1 in avviso", exit 0
  - Nota dell'utente: Choice: Diagnose and fix
  - Problem: OVER 666 burraco_widget_apk_guide.html
  - Hint: Run again with: hub check-project "Burraco la Sfida" limiti-righe
  - Log: — (last lines)
  - `OVER 666 burraco_widget_apk_guide.html`
- [x] [approvato per l'AI] Fix the maps: Maps of Burraco la Sfida need fixing
  - Prova: parte manuale di AGENTS.md accorciata (tolti 3 righe vuote e 2 duplicati: server, limiti righe);
    `wc -l AGENTS.md` → 58 righe (≤ 60), blocco `hub:map` invariato. `hub check-project` non eseguibile in automatico (permesso negato)
  - Nota dell'utente: Choice: Diagnose and fix
  - Problem: AGENTS.md: 64 righe > 60
  - Hint: A map error the generator cannot fix by itself: it needs a manual fix (e.g. shorten the manual part of AGENTS.md).

## Prossimi
- [ ] Da confermare sul telefono (v1.0.1): countdown mai negativo al cambio evento (sveglie esatte), ordine in corso→fine più vicina, orari con ETag
- [ ] Da confermare sul sito: guida calendario dedicato, messaggio quando il browser non mostra la richiesta notifiche

## In attesa (utente)

## Per il futuro
- [ ] Notifiche push vere anche con PWA chiusa (serve un piccolo server Web Push, es. Cloudflare Worker gratuito)

## Fatto
- [x] Pulizia della storia del repo pubblico (riferimenti esclusi con .gitignore)
- [x] Orari condivisi in `web/data/schedule.json` + casi di test comuni
- [x] Decisione: nessuna grafica ufficiale WhatWapp, solo grafica nostra (confermato dall'utente 2026-10-05)
- [x] Orari confermati dall'utente (2026-10-06): Corsa fino a venerdì 00:00, Baule fino a sabato 02:00, Torte fino a domenica 22:00 (riepilogo 22:00-13:00 escluso dai timer)
- [x] GitHub Pages attivato e 4 secret della chiave aggiunti (utente, 2026-10-06)
- [x] Chiave di firma creata (`%USERPROFILE%\.bls-tracker` + copia in `keys/`, esclusa da git); skill AI-hub `android-release-signing`
- [x] Sito online su GitHub Pages (2026-10-06) con metadati di anteprima (Open Graph/Twitter)
- [x] Release v1.0.0 pubblicata da GitHub Actions; APK scaricato dalla release con firma verificata (SHA-256 db29…67e6)
- [x] App: edge-to-edge, temi/icone per widget, riepilogo nella barra (attivabile), controllo aggiornamenti, orari scaricati dal sito
- [x] PWA: condivisione (WhatsApp/Telegram/Instagram/email/QR), galleria anteprime, timeline inizio-fine, riepilogo grigio + interruttore
- [x] PWA: tab compatte, righe evento espandibili, barra fasi Torte, avvisi nel browser, calendari .ics ("Aggiungi" + collegamento facoltativo)
- [x] PWA: icone SVG (Android, Google Play, App Store, sito, Ko-fi), visualizzatore anteprime con frasi/swipe, pulsante Ko-fi
- [x] PWA: titolo "Burraco la Sfida: Eventi del Circolo", layout mobile senza scroll orizzontale (verificato a 360/412px)
- [x] Workflow Pages (`.github/workflows/pages.yml`)
- [x] Prova su telefono reale della v1.0.0: tutto ok (confermato dall'utente 2026-10-06)
- [x] Release v1.0.1 pubblicata da GitHub Actions; APK scaricato dalla release con firma verificata (versionCode 10001, SHA-256 db29…67e6)
