# Avanzamento

## Ora
- [ ] (niente in corso)

## Prossimi

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
