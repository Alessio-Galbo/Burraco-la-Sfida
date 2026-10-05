// Registra il service worker (cache offline e notifiche) quando la pagina è servita via http(s).
export function registerSW() {
  if (!("serviceWorker" in navigator) || !location.protocol.startsWith("http")) return;
  navigator.serviceWorker.register("sw.js").catch(() => { /* offline non disponibile: la pagina funziona lo stesso */ });
}
