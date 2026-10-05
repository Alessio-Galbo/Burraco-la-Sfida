// Service worker: app shell in cache (offline), rete-prima per orari e calendari, apertura della pagina dagli avvisi.
const CACHE = "burraco-shell-v19";
const SHELL = [
  "./", "index.html", "partials/templates.html", "partials/guide.html", "partials/info.html", "locales/it.json", "data/schedule.json",
  "css/base.css", "css/shell.css", "css/events.css", "css/span.css", "css/torte.css", "css/week.css", "css/guide.css", "css/icons.css", "css/viewer.css", "css/share.css",
  "css/themes.css", "css/shots.css", "js/schedule/recap.js", "js/theme-boot.js", "js/core/look.js", "js/ui/appearance.js", "js/ui/evicon.js",
  "js/main.js", "js/config.js", "js/sw-register.js", "js/core/dom.js", "js/core/format.js", "js/core/i18n.js",
  "js/core/prefs.js", "js/schedule/index.js", "js/schedule/engine.js", "js/schedule/rome.js", "js/schedule/week.js",
  "js/ui/index.js", "js/ui/rows.js", "js/ui/phaselist.js", "js/ui/phasebar.js", "js/ui/tabs.js", "js/ui/week.js",
  "js/ui/view.js", "js/ui/alerts.js", "js/ui/calendars.js", "js/ui/shots.js", "js/ui/viewer.js", "js/ui/share.js", "js/ui/release.js", "js/notify/notifier.js",
  "icons/icon.svg", "icons/icon-192.png", "icons/icon-32.png",
  "img/icons/android.svg", "img/icons/googleplay.svg", "img/icons/appstore.svg", "img/icons/globe.svg",
  "img/icons/chevron-left.svg", "img/icons/chevron-right.svg", "img/icons/x.svg", "img/icons/kofi.svg", "img/qr-site.svg",
  "img/icons/whatsapp.svg", "img/icons/telegram.svg", "img/icons/instagram.svg", "img/icons/mail.svg", "img/icons/link.svg", "img/icons/share-2.svg",
  "img/icons/ev-rabbit.svg", "img/icons/ev-package.svg", "img/icons/ev-cake.svg",
];
const NETWORK_FIRST = [/\/data\/schedule\.json$/, /\/calendar\//, /\/locales\//, /\/img\/widgets\//];

self.addEventListener("install", (e) => {
  // cache: "reload" = scarica dalla rete, mai dalla cache HTTP del browser (altrimenti file vecchi nella shell nuova).
  const fresh = SHELL.map((u) => new Request(u, { cache: "reload" }));
  e.waitUntil(caches.open(CACHE).then((c) => c.addAll(fresh)).then(() => self.skipWaiting()));
});

self.addEventListener("activate", (e) => {
  e.waitUntil(caches.keys()
    .then((keys) => Promise.all(keys.filter((k) => k !== CACHE).map((k) => caches.delete(k))))
    .then(() => self.clients.claim()));
});

async function networkFirst(req) {
  try {
    const res = await fetch(req, { cache: "no-cache" });
    if (res.ok) (await caches.open(CACHE)).put(req, res.clone());
    return res;
  } catch {
    return (await caches.match(req)) || Response.error();
  }
}

async function staleWhileRevalidate(req) {
  const cached = await caches.match(req);
  const fresh = fetch(req, { cache: "no-cache" }).then(async (res) => {
    if (res.ok) (await caches.open(CACHE)).put(req, res.clone());
    return res;
  }).catch(() => cached || Response.error());
  return cached || fresh;
}

self.addEventListener("fetch", (e) => {
  const url = new URL(e.request.url);
  if (e.request.method !== "GET" || url.origin !== location.origin) return;
  if (url.pathname.endsWith(".webmanifest")) return; // il manifest non va in cache: le icone installate si aggiornano
  const net = NETWORK_FIRST.some((re) => re.test(url.pathname));
  e.respondWith(net ? networkFirst(e.request) : staleWhileRevalidate(e.request));
});

self.addEventListener("notificationclick", (e) => {
  e.notification.close();
  e.waitUntil(self.clients.matchAll({ type: "window", includeUncontrolled: true }).then((list) => {
    const open = list.find((c) => "focus" in c);
    return open ? open.focus() : self.clients.openWindow("./");
  }));
});
