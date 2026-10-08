# Avanzamento — BlS Tracker          (aggiornato: 2026-10-07)

Il lavoro concluso è in [CHANGELOG.md](CHANGELOG.md) (tecnico) e [NOVITA.md](NOVITA.md) (per gli utenti).

## Risposte e domande per te
- **R8** (2026-10-08) Chrome segnala l'app del sito come installata ma non c'è → app PWA fantasma: Chrome delega il permesso a lei e nega da solo. Cercare in Impostazioni → App (anche di sistema) i vecchi nomi «Eventi Circolo», «Burraco la Sfida: Eventi del Circolo», «Burraco Timer», «BlS Tracker» (non l'APK) e disinstallare; poi Chrome → Tutti i siti → sito → Cancella e reimposta; riavviare Chrome.
- **R7** (2026-10-08) Reset già fatto, sito senza autorizzazioni ma richiesta «denied» in 18 ms → probabile blocco generale di Chrome/telefono. Test di confronto su https://www.bennish.net/web-notifications.html; controllare Chrome → Impostazioni → Notifiche («messaggi più discreti») e Impostazioni sito → Notifiche («I siti possono chiedere»).
- **D1** (2026-10-08) Se il blocco è di Chrome sul telefono: parcheggiare «Attiva su Chrome Android» (restano i messaggi di aiuto; notifiche affidabili da app e calendario) — consiglio: A — opzioni: A) parcheggia B) continua a indagare
- **R6** (2026-10-08) Diagnostica «denied in 18 ms» → Chrome ha il sito bloccato: icona a sinistra dell'indirizzo → Autorizzazioni → Reimposta (o Impostazioni sito → Tutti i siti → sito → Cancella e reimposta); se non basta, Android → App → Chrome → Notifiche → canale del sito attivo.
- **R5** (2026-10-08) Ipotesi «app del sito installata» esclusa dall'utente (nessuna versione web installata) → aggiunta la diagnostica per leggere i valori reali invece di indovinare.
- **R4** (2026-10-08) «Attiva» non funziona in Chrome → la richiesta viene silenziata: controllare Chrome → Impostazioni sito → Notifiche → sito → Consenti, e Impostazioni Android → App → Chrome → Notifiche attive (il caso più probabile).

## Ora
- [ ] Sito: «Attiva» su Chrome Android — diagnostica: richiesta «denied» in 18 ms (sito bloccato da Chrome). Indicati reset delle autorizzazioni del sito e canale notifiche del sito in Android; testo «bloccate» aggiornato — causa probabile: PWA fantasma (R8) — attende: rimozione dell'app fantasma da parte dell'utente

## Prossimi

## In attesa
- [ ] Conferma sul telefono della v1.0.1: ordine (in corso → fine più vicina) e orari aggiornati — attende: test dell'utente (countdown confermato il 2026-10-08)
- [ ] Conferma sul sito: guida al calendario dedicato, messaggio quando il browser non mostra la richiesta notifiche — attende: test dell'utente

## Per il futuro
- [ ] Notifiche push vere anche con PWA chiusa (serve un piccolo server Web Push, es. Cloudflare Worker gratuito) — parcheggiato il 2026-10-05: serve un server
