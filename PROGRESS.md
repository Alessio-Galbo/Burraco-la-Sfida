# Avanzamento — BlS Tracker          (aggiornato: 2026-10-07)

Il lavoro concluso è in [CHANGELOG.md](CHANGELOG.md) (tecnico) e [NOVITA.md](NOVITA.md) (per gli utenti).

## Risposte e domande per te
- **R5** (2026-10-08) Ipotesi «app del sito installata» esclusa dall'utente (nessuna versione web installata) → aggiunta la diagnostica per leggere i valori reali invece di indovinare.
- **R4** (2026-10-08) «Attiva» non funziona in Chrome → la richiesta viene silenziata: controllare Chrome → Impostazioni sito → Notifiche → sito → Consenti, e Impostazioni Android → App → Chrome → Notifiche attive (il caso più probabile).

## Ora
- [ ] Sito: «Attiva» in una scheda Chrome Android resta (permesso «default»; impostazioni sito e Chrome attive, nessuna app del sito installata). Aggiunta «Diagnostica notifiche» in Avvisi — attende: valori della diagnostica dal telefono dell'utente

## Prossimi

## In attesa
- [ ] Conferma sul telefono della v1.0.1: ordine (in corso → fine più vicina) e orari aggiornati — attende: test dell'utente (countdown confermato il 2026-10-08)
- [ ] Conferma sul sito: guida al calendario dedicato, messaggio quando il browser non mostra la richiesta notifiche — attende: test dell'utente

## Per il futuro
- [ ] Notifiche push vere anche con PWA chiusa (serve un piccolo server Web Push, es. Cloudflare Worker gratuito) — parcheggiato il 2026-10-05: serve un server
