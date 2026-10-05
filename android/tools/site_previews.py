# Anteprime finali per il sito (web/img/widgets/): un widget per immagine su sfondo "wallpaper", più la schermata app.
# Uso: python android/tools/site_previews.py <cartella_uscita>   (emulatore Android 12+ via adb, ANDROID_SERIAL)
import io, os, shutil, subprocess, sys, time
from PIL import Image, ImageDraw
from widget_gallery import ADB, PKG, REMOTE, adb

AT = "2026-10-10T08:00:00Z"   # sabato 10:00 a Roma: Torte in corso (fase forno), Corsa in attesa
DENSITY, WIDTH = 2.625, 720
# file, modalità, stile, misura dp, notte, larghezza del widget in px
SHOTS = [("tutti-4x2.png", "all", "game:emoji", (280, 140), "no", 640),
         ("tutti-4x1.png", "all", "system:svg", (280, 70), "yes", 640),
         ("prossimo-4x2.png", "next", "game:emoji", (280, 140), "no", 640),
         ("prossimo-2x2.png", "next", "light:svg", (140, 140), "no", 400),
         ("singolo-4x1.png", "single:corsa", "game:emoji", (280, 70), "no", 640),
         ("singolo-1x1.png", "single:torte", "game:emoji", (70, 70), "no", 260)]

def render(mode, style, size, night, scale):
    adb("shell", "cmd", "uimode", "night", night)
    adb("shell", "rm", "-rf", REMOTE)
    adb("shell", "am", "force-stop", PKG)
    adb("shell", "am", "start", "-n", f"{PKG}/.ui.MainActivity", "--es", "shot_modes", mode, "--es", "shot_sizes",
        f"{size[0]}x{size[1]}", "--es", "shot_styles", style, "--es", "shot_at", AT, "--es", "shot_scale", f"{scale:.3f}")
    for _ in range(60):
        if adb("shell", "ls", f"{REMOTE}/done.txt").returncode == 0:
            break
        time.sleep(1)
    name = f"{mode.replace(':', '-')}_{style.replace(':', '-')}_{size[0]}x{size[1]}.png"
    return Image.open(io.BytesIO(adb("exec-out", "cat", f"{REMOTE}/{name}").stdout)).convert("RGBA")

def wallpaper(w, h):
    im = Image.new("RGB", (w, h))
    d = ImageDraw.Draw(im)
    for y in range(h):
        t = y / max(h - 1, 1)
        d.line([(0, y), (w, y)], fill=tuple(round(a + (b - a) * t) for a, b in zip((58, 80, 138), (74, 45, 87))))
    return im

def save(im, out):
    im.convert("RGB").quantize(colors=256, method=Image.Quantize.MEDIANCUT, dither=Image.Dither.NONE).save(out, optimize=True)
    print(out, im.size, os.path.getsize(out) // 1024, "KB")

def screencap():
    return Image.open(io.BytesIO(subprocess.run(ADB + ["exec-out", "screencap", "-p"], capture_output=True).stdout)).convert("RGB")

def app_shot(out):
    """Schermata principale (tema Gioco) alta 1,5 schermate: due catture unite dove si sovrappongono."""
    adb("shell", "cmd", "uimode", "night", "no")
    adb("shell", "am", "force-stop", PKG)
    adb("shell", "am", "start", "-n", f"{PKG}/.ui.MainActivity")
    time.sleep(4)
    top = screencap()
    adb("shell", "input", "swipe", "540", "1900", "540", "700", "2500")
    time.sleep(2)
    low = screencap()
    cut = int(top.height * 0.72)   # sotto restano barra gesti e bordo: si prende da qui la seconda cattura
    band = top.crop((0, cut - 120, top.width, cut)).resize((135, 15))
    best = min(range(0, cut), key=lambda y: sum(abs(a - b) for p, q in zip(band.getdata(), low.crop(
        (0, y, low.width, y + 120)).resize((135, 15)).getdata()) for a, b in zip(p, q)))
    total = int(top.height * 1.5) - 100   # senza la barra gesti in fondo
    im = Image.new("RGB", (top.width, total))
    im.paste(top.crop((0, 0, top.width, cut)), (0, 0))
    im.paste(low.crop((0, best + 120, low.width, best + 120 + total - cut)), (0, cut))
    save(im.resize((WIDTH, round(total * WIDTH / top.width)), Image.LANCZOS), out)

if __name__ == "__main__":
    out_dir = sys.argv[1]
    for f, mode, style, size, night, px in SHOTS:
        w = render(mode, style, size, night, px / (size[0] * DENSITY))
        bg = wallpaper(WIDTH, w.height + 80)
        bg.paste(w, ((WIDTH - w.width) // 2, 40), w)
        save(bg, os.path.join(out_dir, f))
    app_shot(os.path.join(out_dir, "app.png"))
    adb("shell", "cmd", "uimode", "night", "no")
