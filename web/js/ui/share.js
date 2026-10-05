// Condivisione del sito: menu di sistema (Web Share) se disponibile, altrimenti WhatsApp, Telegram, email, copia link.
import { $ } from "../core/dom.js";
import { t } from "../core/i18n.js";
import { SITE_URL } from "../config.js";

const enc = encodeURIComponent;

export function initShare() {
  const msg = t("share.message");
  $("#share-wa").href = `https://wa.me/?text=${enc(`${msg} ${SITE_URL}`)}`;
  $("#share-tg").href = `https://t.me/share/url?url=${enc(SITE_URL)}&text=${enc(msg)}`;
  $("#share-mail").href = `mailto:?subject=${enc(t("share.subject"))}&body=${enc(`${msg}\n${SITE_URL}`)}`;
  $("#share-native").hidden = !canShare();
  $("#share-native").addEventListener("click", nativeShare);
  $("#share-copy").addEventListener("click", () => copy("share.copied"));
  // Instagram non ha un link di condivisione web: menu di sistema sul telefono, altrimenti copia.
  $("#share-ig").addEventListener("click", () => (canShare() ? nativeShare() : copy("share.igCopied")));
  $("#share-top").addEventListener("click", () => (canShare() ? nativeShare() : openSection()));
}

function canShare() {
  return typeof navigator.share === "function";
}

async function nativeShare() {
  try {
    await navigator.share({ title: t("share.subject"), text: t("share.message"), url: SITE_URL });
  } catch { /* annullato dall'utente */ }
}

async function copy(key) {
  try {
    await navigator.clipboard.writeText(SITE_URL);
    const done = $("#share-done");
    done.textContent = t(key);
    done.hidden = false;
  } catch { /* appunti non disponibili */ }
}

function openSection() {
  document.querySelector('[data-tab="info"]')?.click();
  $("#share").scrollIntoView({ behavior: "smooth", block: "start" });
}
