package dev.redshift.client.utilities;

import dev.redshift.client.config.ConfigManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public final class Theme {
    public static final int BG_TOP = 0xFF0A0510, BG_BOTTOM = 0xFF1E0B36;
    public static final int PANEL = 0xD0120820;
    public static final int RED = 0xE0243C, PURPLE = 0x8A3FFC;
    public static final int MUTED = 0xFFA79BB8;

    public static final int[] PRESET_RGB = {0, 0xFFFFFF, 0xE0243C, 0x8A3FFC, 0x00D1FF, 0x3DDC84, 0xFFD54A};
    public static final String[] PRESET_NAMES = {"Accent", "White", "Red", "Purple", "Cyan", "Green", "Yellow"};

    private Theme() {}

    public static int accentRgb() { return ConfigManager.profile().accent & 0xFFFFFF; }
    public static int accent() { return 0xFF000000 | accentRgb(); }
    public static int resolve(int rgb) { return rgb == 0 ? accentRgb() : rgb & 0xFFFFFF; }

    public static int alpha(int rgb, float a) {
        int al = Math.max(5, Math.min(255, (int) (a * 255)));
        return (al << 24) | (rgb & 0xFFFFFF);
    }

    public static int lerp(int c1, int c2, float t) {
        int a = (int) (((c1 >>> 24) & 255) * (1 - t) + ((c2 >>> 24) & 255) * t);
        int r = (int) (((c1 >> 16) & 255) * (1 - t) + ((c2 >> 16) & 255) * t);
        int g = (int) (((c1 >> 8) & 255) * (1 - t) + ((c2 >> 8) & 255) * t);
        int b = (int) ((c1 & 255) * (1 - t) + (c2 & 255) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int presetIndex(int rgb) {
        for (int i = 0; i < PRESET_RGB.length; i++) if (PRESET_RGB[i] == rgb) return i;
        return 0;
    }
    public static int nextPreset(int rgb) { return PRESET_RGB[(presetIndex(rgb) + 1) % PRESET_RGB.length]; }
    public static int nextAccent(int rgb) {
        int n = presetIndex(rgb) + 1;
        if (n >= PRESET_RGB.length) n = 1;
        return PRESET_RGB[Math.max(1, n)];
    }

    public static Style style(int rgb) { return Style.EMPTY.withColor(TextColor.fromRgb(rgb & 0xFFFFFF)); }

    public static void outline(GuiGraphics g, int x, int y, int w, int h, int c) {
        g.fill(x, y, x + w, y + 1, c);
        g.fill(x, y + h - 1, x + w, y + h, c);
        g.fill(x, y, x + 1, y + h, c);
        g.fill(x + w - 1, y, x + w, y + h, c);
    }
}
