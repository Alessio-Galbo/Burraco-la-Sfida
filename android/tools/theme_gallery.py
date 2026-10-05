# Confronto temi × icone dei widget (4×1 e 4×2) e screenshot della configurazione; telefono/emulatore Android 12+ via adb.
# Uso: python android/tools/theme_gallery.py <cartella_uscita> [mode,...]  → themes-<modalità>.png, config-screen.png
import os, shutil, subprocess, sys, time
from PIL import Image, ImageDraw
from widget_gallery import ADB, PKG, REMOTE, MODES, adb

SIZES = [(280, 70), (280, 140)]
THEMES = [("Gioco", "game", "yes"), ("Chiaro", "light", "yes"), ("Sistema scuro", "system", "yes"),
          ("Sistema chiaro", "system", "no")]
ICONS = [("Emoji", "emoji"), ("SVG", "svg")]
SCALE, GAP, LABEL = 0.5, 14, 110

def shots(modes, styles, night, dest):
    """Renderizza con ShotRenderer nella modalità notte indicata e scarica i PNG in dest."""
    adb("shell", "cmd", "uimode", "night", night)
    adb("shell", "rm", "-rf", REMOTE)
    adb("shell", "am", "force-stop", PKG)
    sizes = ",".join(f"{w}x{h}" for w, h in SIZES)
    adb("shell", "am", "start", "-n", f"{PKG}/.ui.MainActivity", "--es", "shot_modes", ",".join(modes),
        "--es", "shot_sizes", sizes, "--es", "shot_styles", ",".join(styles))
    for _ in range(90):
        if adb("shell", "ls", f"{REMOTE}/done.txt").returncode == 0:
            break
        time.sleep(1)
    tmp = dest + "_tmp"
    shutil.rmtree(tmp, ignore_errors=True)
    adb("pull", REMOTE, tmp)
    os.makedirs(dest, exist_ok=True)
    got = os.path.join(tmp, "shots") if os.path.isdir(os.path.join(tmp, "shots")) else tmp
    for f in os.listdir(got):
        if f.endswith(".png") or f.endswith(".txt"):
            shutil.move(os.path.join(got, f), os.path.join(dest, f))
    shutil.rmtree(tmp, ignore_errors=True)

def cell(src, night, mode, style, w, h):
    im = Image.open(os.path.join(src, night, f"{mode.replace(':', '-')}_{style.replace(':', '-')}_{w}x{h}.png"))
    return im.resize((round(im.width * SCALE), round(im.height * SCALE)), Image.LANCZOS)

def sheet(mode, src, out):
    cols = [(ic, w, h) for ic in ICONS for (w, h) in SIZES]
    grid = [[cell(src, night, mode, f"{key}:{ic[1]}", w, h) for ic, w, h in cols] for _, key, night in THEMES]
    colw = [max(r[i].width for r in grid) for i in range(len(cols))]
    rowh = [max(im.height for im in r) for r in grid]
    W, H = LABEL + sum(colw) + GAP * (len(cols) + 1), 50 + sum(rowh) + GAP * (len(rowh) + 1)
    canvas = Image.new("RGB", (W, H))
    d = ImageDraw.Draw(canvas)
    for y in range(H):   # sfondo "Home" a gradiente
        t = y / H
        d.line([(0, y), (W, y)], fill=tuple(round(a + (b - a) * t) for a, b in zip((58, 80, 138), (74, 45, 87))))
    d.text((10, 8), f"{MODES.get(mode, mode)}: tema x icone (4x1 = 280x70 dp, 4x2 = 280x140 dp)", fill=(255, 255, 255))
    x = LABEL + GAP
    for i, (ic, w, h) in enumerate(cols):
        d.text((x, 28), f"{ic[0]} {w}x{h}", fill=(255, 230, 150))
        x += colw[i] + GAP
    y = 50 + GAP
    for r, (label, _, _) in enumerate(THEMES):
        d.text((10, y + rowh[r] // 2 - 6), label, fill=(255, 255, 255))
        x = LABEL + GAP
        for i, im in enumerate(grid[r]):
            canvas.paste(im, (x, y), im)
            x += colw[i] + GAP
        y += rowh[r] + GAP
    canvas.save(out, optimize=True)
    print(out, canvas.size)

def config_screen(out, night="no"):
    """Schermata di configurazione (id widget fittizio: non si salva nulla finché non si tocca Salva)."""
    adb("shell", "cmd", "uimode", "night", night)
    adb("shell", "am", "force-stop", PKG)
    adb("shell", "am", "start", "-n", f"{PKG}/.widget.WidgetConfigActivity", "--ei", "appWidgetId", "99999")
    time.sleep(4)
    with open(out, "wb") as f:
        f.write(subprocess.run(ADB + ["exec-out", "screencap", "-p"], capture_output=True).stdout)
    adb("shell", "am", "force-stop", PKG)
    print(out)

if __name__ == "__main__":
    out_dir = sys.argv[1]
    modes = sys.argv[2].split(",") if len(sys.argv) > 2 else ["all", "next"]
    src = os.path.join(out_dir, "theme-shots")
    for night in ("yes", "no"):
        styles = [f"{key}:{ic}" for _, key, n in THEMES if n == night for _, ic in ICONS]
        shots(modes, styles, night, os.path.join(src, night))
    for m in modes:
        sheet(m, src, os.path.join(out_dir, f"themes-{MODES.get(m, m)}.png"))
    config_screen(os.path.join(out_dir, "config-screen.png"))
