// Anteprime dei widget da img/widgets/index.json: didascalia breve nella griglia, lunga nel visualizzatore (tocco).
import { $, clone, fields } from "../core/dom.js";
import { t } from "../core/i18n.js";
import { initViewer, openViewer } from "./viewer.js";

export async function initShots() {
  let shots = [];
  try {
    const res = await fetch("img/widgets/index.json", { cache: "no-cache" });
    if (res.ok) shots = (await res.json()).shots || [];
  } catch { /* offline: nessuna anteprima */ }
  initViewer();
  const list = shots.map(({ file, key, fit }) => ({
    src: `img/widgets/${file}`,
    fit,
    caption: t(String(key).includes(".") ? key : `android.shots.${key}`),
    short: t(`android.shotsShort.${key}`),
  }));
  $("#shots").replaceChildren(...list.map((item, i) => {
    const el = clone("tpl-shot");
    const f = fields(el);
    f.img.src = item.src;
    f.img.alt = item.caption;
    f.cap.textContent = item.short.startsWith("android.") ? item.caption : item.short;
    el.classList.toggle("shot--top", item.fit === "top");
    f.btn.setAttribute("aria-label", t("android.viewer.open", { name: item.caption }));
    f.btn.addEventListener("click", () => openViewer(list, i));
    return el;
  }));
  $("#shots").hidden = !shots.length;
  $("#shots-soon").hidden = shots.length > 0;
}
