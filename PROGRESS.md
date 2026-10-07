# Avanzamento — BlS Tracker          (aggiornato: 2026-10-07)

Il lavoro concluso è in [CHANGELOG.md](CHANGELOG.md) (tecnico) e [NOVITA.md](NOVITA.md) (per gli utenti).

## Risposte e domande per te
- **R1** (2026-10-07) Gli orari funzionano senza connessione? → Sì: l'app usa la copia locale (assets + ultimo
  file scaricato) e l'orologio del telefono; online chiede a GitHub solo se il file è cambiato (ETag, 304 = niente
  download). Il sito funziona offline dopo la prima visita (cache del service worker).
- **R2** (2026-10-07) Il calendario si intasa? → No: ogni file contiene al massimo 6 serie settimanali ricorrenti.
  Per toglierle in un tocco: importarle in un calendario dedicato «Burraco» (guida nella sezione Calendari) o usare «Collega».
- **R3** (2026-10-07) Il tasto «Attiva» delle notifiche non va → il codice funziona; il browser a volte non mostra la
  richiesta (prompt silenziato, browser interni di WhatsApp/Instagram): ora la pagina spiega lucchetto → Notifiche → Consenti.

## Ora

## Prossimi

## In attesa
- [ ] Conferma sul telefono della v1.0.1: countdown mai negativo al cambio evento, ordine (in corso → fine più vicina), orari aggiornati — attende: test dell'utente
- [ ] Conferma sul sito: guida al calendario dedicato, messaggio quando il browser non mostra la richiesta notifiche — attende: test dell'utente

## Per il futuro
- [ ] Notifiche push vere anche con PWA chiusa (serve un piccolo server Web Push, es. Cloudflare Worker gratuito) — parcheggiato il 2026-10-05: serve un server
