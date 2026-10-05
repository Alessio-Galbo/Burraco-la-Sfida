<!-- hub:map:start -->
# Mappa: tools/
Torna al [router](../../AGENTS.md) · 7 file
- [README.md](../../tools/README.md): Strumenti del progetto
- [build-ics.mjs](../../tools/build-ics.mjs): Genera web/calendar/*.ics da web/data/schedule.json e web/locales/it.json: node tools/build-ics.mjs
- [icons/jobs.json](../../tools/icons/jobs.json)
- [ics/events.mjs](../../tools/ics/events.mjs): Costruisce i VEVENT settimanali (uno per evento, uno per fase delle Torte) da schedule.json e it.js…
- [ics/index.mjs](../../tools/ics/index.mjs): Genera il testo dei calendari .ics (tutti + uno per evento) a partire da schedule.json e it.json.
- [ics/text.mjs](../../tools/ics/text.mjs): Utilità testuali RFC 5545: escape dei valori TEXT, folding a 75 ottetti, formato data locale, segna…
- [ics/vtimezone.mjs](../../tools/ics/vtimezone.mjs): Componente VTIMEZONE per Europe/Rome (CET/CEST, regole UE dal 1996) da includere in ogni calendario.
<!-- hub:map:end -->
