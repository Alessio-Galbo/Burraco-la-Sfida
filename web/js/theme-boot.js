// Applica tema e icone salvati prima del primo disegno (script classico in <head>); stessa logica di js/core/look.js.
(function () {
  var p = {};
  try { p = JSON.parse(localStorage.getItem("burraco.prefs.v1")) || {}; } catch (e) { p = {}; }
  var theme = ["game", "light", "system"].indexOf(p.theme) >= 0 ? p.theme : "game";
  var icons = p.icons === "emoji" || p.icons === "svg" ? p.icons : (theme === "game" ? "emoji" : "svg");
  document.documentElement.setAttribute("data-theme", theme);
  document.documentElement.setAttribute("data-icons", icons);
})();
