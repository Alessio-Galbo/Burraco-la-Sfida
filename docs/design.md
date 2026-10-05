# Guida di stile (PWA + widget Android)

Obiettivo: **compatto, curato, "da app"**, non una pagina di card enormi. L'utente apre per un'occhiata di 3 secondi.

## Principi
- Densità: tutto lo stato della settimana sta in **una schermata di telefono senza scroll** (header + 3 righe evento + barra Torte).
- Gerarchia con peso e colore, non con dimensioni giganti. Niente testo esplicativo ripetuto: le note vanno in un'unica nota a piè di sezione o dietro un "i".
- Un colore per evento, usato con parsimonia (icona, punto "live", barra di avanzamento).

## Token
| Token | Valore | Uso |
|---|---|---|
| bg | `#0e1426` | fondo pagina / widget (widget: alpha ~E6) |
| surface | `#172038` | righe/pannelli |
| surface-2 | `#1e2945` | hover, elementi annidati |
| line | `#ffffff14` | bordi 1px |
| text | `#e9eefb` / muted `#8f9bbd` | testo |
| gold | `#ffc93c` | accento principale (tab attiva, "prossimo") |
| live | `#3ddc84` | stato "in corso" (punto pulsante 6px) |
| corsa | `#7ed957` | Corsa Pazza 🐰 |
| baule | `#f0a04b` | Baule di Squadra 📦 |
| torte | `#ff7eb6` | Sfida delle Torte 🎂 |
| header | gradiente `#2bb8f0 → #0b5fb0`, filo rosso `#d62828` 2px | solo la barra in alto |

Raggi: 12px pannelli, 8px righe interne, 999px chip. Ombre quasi assenti; separazione con bordo `line`.

## Tipografia (sito)
Font di sistema (`system-ui, "Segoe UI", Roboto, sans-serif`), numeri `font-variant-numeric: tabular-nums`.
| Ruolo | Mobile | Desktop |
|---|---|---|
| titolo app (header) | 17px/600 | 18px |
| nome evento | 15px/600 | 15px |
| countdown riga | 18px/700 | 20px |
| etichette, orari, chip | 12–13px | 13px |
**Mai scroll orizzontale su mobile (verifica a 360px: `scrollWidth <= clientWidth`).** Massimo assoluto: 28px (solo countdown principale nel dettaglio). Contenuto `max-width: 720px` centrato.

## Layout sito
- Header 52px: icona 28px + "Burraco la Sfida: Eventi del Circolo" (su una riga, si riduce se serve); niente sottotitolo lungo.
- Tab sticky a segmenti: **Timer · Settimana · Avvisi · Scarica · Info**, tutte visibili a 360px senza scroll orizzontale (etichette brevi, eventualmente icona + testo piccolo, o bottom bar stile app). Una sezione visibile alla volta (tab ricordata), non una pagina lunghissima.
- Timer: riga per evento (altezza ~64px): icona 28px | nome + sottoriga (es. "Torte in forno · 1vs1" o "ven 16:00") | a destra chip stato + countdown. Tocco → si espande con inizio/fine, fasi Torte. Gli eventi in corso stanno in alto.
- Fasi Torte: barra orizzontale a 5 segmenti proporzionali alla durata, segmento corrente pieno nel colore torte, avanzamento dentro il segmento; sotto, etichetta della fase corrente + tempo restante.
- "Stimato": piccolo `≈` davanti all'orario + una nota sola in fondo alla sezione.
- Settimana: griglia 7 colonne compatta (barre sottili 8px per evento, linea "adesso").

## Widget Android
- Padding 10dp, raggio 16dp, fondo `bg` alpha E6, bordo 1dp `line`.
- Riga evento: icona 18dp, nome 13sp/medium, sotto stato 11sp muted; countdown 14sp bold a destra (Chronometer). Altezza riga ~36dp.
- 1×1 / 2×1: icona + countdown (+ punto live). 2×2+: nome + fase. 4×2 "Tutti": 3 righe + mini barra Torte.
- Niente testi più grandi di 16sp.

## Temi e icone (scelta dell'utente)
Tre temi, stessa struttura e stesse dimensioni: cambiano solo colori e icone.
| Token | Gioco (predefinito) | Chiaro | Sistema |
|---|---|---|---|
| bg | `#0e1426` | `#f6f7fb` | Android 12+: `system_neutral1_900` (scuro) / `_50` (chiaro); web: segue `prefers-color-scheme` usando Gioco o Chiaro |
| surface | `#172038` | `#ffffff` | `system_neutral1_800` / `system_neutral1_10` |
| text / muted | `#e9eefb` / `#8f9bbd` | `#1a2133` / `#5b6580` | `system_neutral1_50`/`_400` (scuro), `_900`/`_600` (chiaro) |
| accento (prossimo, tab) | gold `#ffc93c` | `#b7791f` | `system_accent1_200` (scuro) / `_600` (chiaro) |
| live | `#3ddc84` | `#1e9e5a` | `system_accent2_*` o verde live |
| corsa / baule / torte | `#7ed957` / `#f0a04b` / `#ff7eb6` | `#3f9a1f` / `#c26a12` / `#d63d84` | come Chiaro/Gioco secondo chiaro-scuro (i colori evento restano riconoscibili) |
Sotto Android 12 "Sistema" ricade su Gioco (scuro) o Chiaro secondo il tema del telefono.

Icone: **Emoji** (🐰 📦 🎂) oppure **SVG** Lucide (`web/img/icons/ev-rabbit.svg`, `ev-package.svg`, `ev-cake.svg`, ISC), monocromatiche nel colore dell'evento. Predefinito: Emoji con tema Gioco, SVG con Sistema/Chiaro (l'utente può cambiare).
Scelte ricordate: PWA in localStorage; widget per singola istanza (configurazione) + predefinito nell'app.
