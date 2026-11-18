package dev.redshift.client.hud;

import dev.redshift.client.config.ModuleState;
import dev.redshift.client.utilities.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public final class CrosshairModule extends HudModule {
    public CrosshairModule() { super("crosshair", "Custom Crosshair", 0.5f, 0.5f, false); }

    @Override public boolean positionable() { return false; }

    @Override public List<OptionDef> options() {
        return List.of(
                new OptionDef("style", "Style (0 cross,1 dot,2 square,3 cross+dot)", 0, 3, 0, "%.0f"),
                new OptionDef("size", "Size", 1, 20, 5, "%.0f"),
                new OptionDef("thickness", "Thickness", 1, 4, 1, "%.0f"),
                new OptionDef("gap", "Gap", 0, 10, 2, "%.0f"));
    }

    @Override protected int contentWidth(Font f, boolean e) { return 0; }
    @Override protected int contentHeight(Font f, boolean e) { return 0; }
    @Override protected void drawContent(GuiGraphics g, Font f, int x, int y, int rgb, float a, boolean e) {}

    public void draw(GuiGraphics g) {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.options.getCameraType().isFirstPerson()) return;
        ModuleState s = state();
        int sw = g.guiWidth(), sh = g.guiHeight();
        int cx = sw / 2, cy = sh / 2;
        int style = Math.round(opt("style")), size = Math.round(opt("size"));
        int t = Math.round(opt("thickness")), gap = Math.round(opt("gap"));
        int c = Theme.alpha(Theme.resolve(s.color), s.opacity);
        int h = t / 2;
        if (style == 0 || style == 3) {
            g.fill(cx - gap - size, cy - h, cx - gap, cy - h + t, c);
            g.fill(cx + gap, cy - h, cx + gap + size, cy - h + t, c);
            g.fill(cx - h, cy - gap - size, cx - h + t, cy - gap, c);
            g.fill(cx - h, cy + gap, cx - h + t, cy + gap + size, c);
        }
        if (style == 1 || style == 3) {
            int d = Math.max(t, 2);
            g.fill(cx - d / 2, cy - d / 2, cx - d / 2 + d, cy - d / 2 + d, c);
        }
        if (style == 2) {
            int r = size;
            g.fill(cx - r, cy - r, cx + r, cy - r + t, c);
            g.fill(cx - r, cy + r - t, cx + r, cy + r, c);
            g.fill(cx - r, cy - r, cx - r + t, cy + r, c);
            g.fill(cx + r - t, cy - r, cx + r, cy + r, c);
        }
    }
}
