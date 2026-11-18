package dev.redshift.client.hud;

import dev.redshift.client.config.ConfigManager;
import dev.redshift.client.config.ModuleState;
import dev.redshift.client.utilities.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public abstract class HudModule {
    public static final int PAD = 3;
    private final String id, name;
    private final float defX, defY;
    private final boolean defEnabled;

    protected HudModule(String id, String name, float defX, float defY, boolean defEnabled) {
        this.id = id; this.name = name; this.defX = defX; this.defY = defY; this.defEnabled = defEnabled;
    }

    public String id() { return id; }
    public String name() { return name; }
    public boolean positionable() { return true; }
    public List<OptionDef> options() { return List.of(); }
    public ModuleState state() { return ConfigManager.state(this); }

    public float opt(String key) {
        Float v = state().options.get(key);
        if (v != null) return v;
        for (OptionDef d : options()) if (d.key().equals(key)) return d.def();
        return 0;
    }

    public ModuleState defaultState() {
        ModuleState s = new ModuleState();
        s.enabled = defEnabled; s.x = defX; s.y = defY;
        for (OptionDef d : options()) s.options.put(d.key(), d.def());
        return s;
    }

    public void tick(Minecraft mc) {}

    protected abstract int contentWidth(Font f, boolean editing);
    protected abstract int contentHeight(Font f, boolean editing);
    protected abstract void drawContent(GuiGraphics g, Font f, int x, int y, int rgb, float alpha, boolean editing);

    /** Экранные границы {x, y, w, h} в режиме редактора. */
    public float[] bounds(Font f, int sw, int sh) {
        ModuleState s = state();
        float w = (contentWidth(f, true) + 2 * PAD) * s.scale;
        float h = (contentHeight(f, true) + 2 * PAD) * s.scale;
        float px = Math.max(0, Math.min(s.x * sw, Math.max(0, sw - w)));
        float py = Math.max(0, Math.min(s.y * sh, Math.max(0, sh - h)));
        return new float[]{px, py, w, h};
    }

    public void render(GuiGraphics g, Font f, int sw, int sh, boolean editing) {
        ModuleState s = state();
        int cw = contentWidth(f, editing), ch = contentHeight(f, editing);
        if (cw <= 0 || ch <= 0) return;
        float w = (cw + 2 * PAD) * s.scale, h = (ch + 2 * PAD) * s.scale;
        float px = Math.max(0, Math.min(s.x * sw, Math.max(0, sw - w)));
        float py = Math.max(0, Math.min(s.y * sh, Math.max(0, sh - h)));
        g.pose().pushMatrix();
        g.pose().translate(px, py);
        g.pose().scale(s.scale, s.scale);
        if (s.background) {
            g.fill(0, 0, cw + 2 * PAD, ch + 2 * PAD, Theme.alpha(0x000000, 0.42f * s.opacity));
            g.fill(0, 0, 1, ch + 2 * PAD, Theme.alpha(Theme.accentRgb(), s.opacity));
        }
        drawContent(g, f, PAD, PAD, Theme.resolve(s.color), s.opacity, editing);
        g.pose().popMatrix();
    }
}
