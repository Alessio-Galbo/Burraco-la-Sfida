# Fogli di confronto dei widget a misure in dp: l'app li renderizza fuori schermo (ShotRenderer), qui si compongono.
# Uso: python android/tools/widget_gallery.py <cartella_uscita> [mode,...]  (emulatore/telefono collegato con adb)
import os, subprocess, sys, time
from PIL import Image, ImageDraw

ADB = ["adb"] + (["-s", os.environ["ANDROID_SERIAL"]] if os.environ.get("ANDROID_SERIAL") else [])
PKG = "io.github.alessiogalbo.burraco"
REMOTE = f"/sdcard/Android/data/{PKG}/files/shots"
WIDTHS = [70, 140, 210, 280, 350, 420, 560, 800]
HEIGHTS = [70, 140, 210, 350]
MODES = {"all": "tutti", "next": "prossimo", "single:torte": "singolo-torte", "single:corsa": "singolo-corsa"}
SCALE = 0.5   # px del foglio per px del telefono (densità 2.625 → ~1.3 px per dp)
GAP = 16

def adb(*a):
    return subprocess.run(ADB + [str(x) for x in a], capture_output=True)

def render(modes, tmp):
    sizes = ",".join(f"{w}x{h}" for h in HEIGHTS for w in WIDTHS)
    adb("shell", "rm", "-rf", REMOTE)
    adb("shell", "am", "force-stop", PKG)
    adb("shell", "am", "start", "-n", f"{PKG}/.ui.MainActivity", "--es", "shot_modes", ",".join(modes), "--es", "shot_sizes", sizes)
    for _ in range(120):
        if adb("shell", "ls", f"{REMOTE}/done.txt").returncode == 0:
            break
        time.sleep(1)
    adb("pull", REMOTE, tmp)

def sheet(mode, src, out):
    cells = {}
    for h in HEIGHTS:
        for w in WIDTHS:
            p = os.path.join(src, f"{mode.replace(':', '-')}_{w}x{h}.png")
            im = Image.open(p)
            cells[(w, h)] = (im.resize((round(im.width * SCALE), round(im.height * SCALE)), Image.LANCZOS),
                             open(p + ".txt").read().strip())
    colw = [max(cells[(w, h)][0].width for h in HEIGHTS) for w in WIDTHS]
    rowh = [max(cells[(w, h)][0].height for w in WIDTHS) + 22 for h in HEIGHTS]
    W, H = sum(colw) + GAP * (len(WIDTHS) + 1) + 40, sum(rowh) + GAP * (len(HEIGHTS) + 1) + 40
    canvas = Image.new("RGB", (W, H))
    top, bot = (58, 80, 138), (74, 45, 87)
    for y in range(H):   # sfondo "Home" a gradiente, per vedere bordi e trasparenza
        t = y / H
        ImageDraw.Draw(canvas).line([(0, y), (W, y)], fill=tuple(round(a + (b - a) * t) for a, b in zip(top, bot)))
    d = ImageDraw.Draw(canvas)
    d.text((10, 10), f"{MODES.get(mode, mode)}  (larghezza x altezza in dp, taglia scelta)", fill=(255, 255, 255))
    y = 40 + GAP
    for hi, h in enumerate(HEIGHTS):
        x = 40 + GAP
        for wi, w in enumerate(WIDTHS):
            im, size = cells[(w, h)]
            d.text((x, y), f"{w}x{h} {size}", fill=(255, 230, 150))
            canvas.paste(im, (x, y + 18), im)
            x += colw[wi] + GAP
        y += rowh[hi] + GAP
    canvas.save(out, optimize=True)
    print(out, canvas.size)

if __name__ == "__main__":
    out_dir = sys.argv[1]
    modes = sys.argv[2].split(",") if len(sys.argv) > 2 else ["all", "next", "single:torte"]
    os.makedirs(out_dir, exist_ok=True)
    render(modes, out_dir)
    for m in modes:
        sheet(m, os.path.join(out_dir, "shots"), os.path.join(out_dir, f"fluid-{MODES.get(m, m)}.png"))
