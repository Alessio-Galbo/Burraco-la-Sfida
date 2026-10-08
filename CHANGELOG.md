# Changelog

Modifiche tecniche di BlS Tracker (sito + app Android). Le novità per gli utenti sono in [NOVITA.md](NOVITA.md).
Versioni dell'app = tag `vX.Y.Z` (release su GitHub); il sito si aggiorna a ogni push su `main`.

## [Non rilasciato]
### Sito
- Versione dell'APK nella pagina Scarica: la cache dura al massimo 10 minuti invece dell'intera scheda
  (ricaricando la pagina si vedeva ancora la versione precedente).
- Messaggio «richiesta non mostrata» con i percorsi esatti di Chrome per Android (impostazioni sito, notifiche di Chrome,
  app del sito installata, a cui Chrome delega il permesso).

## [1.0.2] – 2026-10-07
### App Android
- Avviso nuova versione come notifica di sistema (canale "Aggiornamenti app", una sola volta per versione; tocco →
  download dell'APK), oltre al banner. Controllo all'apertura (max 1/h) e in background una volta al giorno
  (`net/DailyReceiver.kt`, riprogrammato a riavvio, cambio ora/fuso, aggiornamento app); regole in `net/UpdatePolicy.kt`.
- Pulsante «Controlla aggiornamenti» con la versione installata (`ui/UpdateSection.kt`). Test `UpdatePolicyTest`.
- Release da GitHub Actions verificata: APK 1.0.2 (versionCode 10002) firmato con la chiave stabile (SHA-256 db29…67e6).
### Sito
- Avvisi nel browser: lo stato del permesso si aggiorna da solo quando cambia dalle impostazioni del browser
  (`navigator.permissions` onchange, `visibilitychange`, `focus`): «Attiva» sparisce senza ricaricare.

## [1.0.1] – 2026-10-07
### App Android
- Sveglie esatte (`setExactAndAllowWhileIdle`, 1 s dopo ogni inizio/fine/cambio fase) per widget e notifiche:
  il countdown non va più in negativo all'inizio di un evento. Permessi `USE_EXACT_ALARM` e
  `SCHEDULE_EXACT_ALARM` (fino ad API 32), con ripiego sulle sveglie inesatte.
- Ordine eventi condiviso con il sito (`schedule/EventOrder.kt`): in corso per fine più vicina, poi in attesa per
  inizio più vicino; "prossimo o in corso" = primo dell'ordine. Test `TransitionTest`.
- Download orari condizionale (`If-None-Match` / `If-Modified-Since`): con 304 la copia locale resta. Test `ConditionalTest`.
### Sito
- Stesso ordine degli eventi del widget (`ordered()` in `web/js/schedule/index.js`, test `tests/order.test.mjs`).
- Sezione Calendari: guida per importare in un calendario dedicato (nascondibile/eliminabile in un tocco).
- Avvisi nel browser: messaggio quando il browser non mostra la richiesta di permesso (prompt silenziato).

## [1.0.0] – 2026-10-06
### Sito (PWA, GitHub Pages)
- Timer degli eventi settimanali (Corsa Pazza, Baule di Squadra, La Sfida Delle Torte con 4 fasi + riepilogo
  passivo) in ora di Roma con ora legale, da un'unica fonte `web/data/schedule.json`.
- Tab Timer / Settimana / Avvisi / Scarica / Info; righe espandibili con timeline inizio→fine, barra fasi Torte,
  vista settimanale; temi Gioco / Chiaro / Sistema, icone emoji o SVG; layout senza scroll orizzontale da 360px.
- Avvisi nel browser, calendari `.ics` generati (`tools/build-ics.mjs`) con promemoria, collegamento facoltativo.
- Condivisione (menu di sistema, WhatsApp, Telegram, Instagram, email, QR) e metadati Open Graph/Twitter.
- Galleria anteprime widget con visualizzatore; versione e peso dell'APK letti dall'ultima release.
- Service worker: precache con `cache: "reload"` e rivalidazione `no-cache` (niente file vecchi nella shell nuova).
- Download APK in nuova scheda (dalla PWA installata la stessa scheda bloccava il salvataggio al 100%).
### App Android (~113 KB, nessuna libreria esterna)
- Widget ridimensionabile senza limiti di colonne (struttura per fasce di altezza), modalità tutti / prossimo /
  singolo, stile e icone per singolo widget, riepilogo Torte attivabile nella barra.
- Notifiche locali, app edge-to-edge con condivisione, link ufficiali e Ko-fi.
- Orari scaricati dal sito (al massimo ogni 24 h, riprova dopo 2 h se fallisce) e banner di nuova versione.
### Infrastruttura
- Workflow Pages (test + calendari + deploy) e Android (test, firma dai secret, release sui tag `v*`).
- Chiave di firma stabile fuori dal repo (copia in `keys/`, esclusa da git); test condivisi `tests/schedule-cases.json`.
