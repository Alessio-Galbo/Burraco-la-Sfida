// Visualizzatore a schermo intero delle anteprime widget: <dialog> con frecce, tastiera, swipe e contatore.
import { $ } from "../core/dom.js";
import { t } from "../core/i18n.js";

let items = [];
let index = 0;

export function initViewer() {
  const dlg = $("#viewer");
  $("#viewer-prev").addEventListener("click", () => go(-1));
  $("#viewer-next").addEventListener("click", () => go(1));
  $(".viewer-close", dlg).addEventListener("click", () => dlg.close());
  dlg.addEventListener("click", (e) => { if (e.target === dlg) dlg.close(); });
  dlg.addEventListener("keydown", (e) => {
    if (e.key === "ArrowLeft") go(-1);
    if (e.key === "ArrowRight") go(1);
  });
  let startX = null;
  dlg.addEventListener("pointerdown", (e) => { startX = e.clientX; });
  dlg.addEventListener("pointerup", (e) => {
    if (startX === null) return;
    const dx = e.clientX - startX;
    startX = null;
    if (Math.abs(dx) > 40) go(dx < 0 ? 1 : -1);
  });
}

/** list = [{ src, caption }]; apre il visualizzatore sull'elemento i. */
export function openViewer(list, i) {
  items = list;
  index = i;
  show();
  $("#viewer").showModal();
}

function go(step) {
  if (items.length < 2) return;
  index = (index + step + items.length) % items.length;
  show();
}

function show() {
  const item = items[index];
  const img = $("#viewer-img");
  img.src = item.src;
  img.alt = item.caption;
  $("#viewer-cap").textContent = item.caption;
  $("#viewer-count").textContent = t("android.viewer.count", { n: index + 1, total: items.length });
  const single = items.length < 2;
  $("#viewer-prev").hidden = single;
  $("#viewer-next").hidden = single;
}
