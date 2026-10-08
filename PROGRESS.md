# Avanzamento — BlS Tracker          (aggiornato: 2026-10-07)

Il lavoro concluso è in [CHANGELOG.md](CHANGELOG.md) (tecnico) e [NOVITA.md](NOVITA.md) (per gli utenti).

## Risposte e domande per te
- **R5** (2026-10-08) Impostazioni già a posto ma «Attiva» resta → se il sito è installato come app, Chrome delega il permesso all'app del sito: Impostazioni → App → «BlS Tracker» del sito (ce ne possono essere due) → Notifiche; oppure disinstallare l'icona e riprovare in Chrome.
- **R4** (2026-10-08) «Attiva» non funziona in Chrome → la richiesta viene silenziata: controllare Chrome → Impostazioni sito → Notifiche → sito → Consenti, e Impostazioni Android → App → Chrome → Notifiche attive (il caso più probabile).

## Ora
- [ ] Sito: «Attiva» in una scheda Chrome Android resta (permesso «default» con impostazioni sito e Chrome già attive). Ipotesi: sito installato come app → permesso delegato all'app del sito in Android. Messaggio aggiornato con il terzo percorso — attende: test dell'utente (Impostazioni → App → BlS Tracker del sito → Notifiche)

## Prossimi

## In attesa
- [ ] Conferma sul telefono della v1.0.1: ordine (in corso → fine più vicina) e orari aggiornati — attende: test dell'utente (countdown confermato il 2026-10-08)
- [ ] Conferma sul sito: guida al calendario dedicato, messaggio quando il browser non mostra la richiesta notifiche — attende: test dell'utente

## Per il futuro
- [ ] Notifiche push vere anche con PWA chiusa (serve un piccolo server Web Push, es. Cloudflare Worker gratuito) — parcheggiato il 2026-10-05: serve un server
