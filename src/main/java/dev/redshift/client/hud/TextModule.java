package dev.redshift.client.hud;

import dev.redshift.client.utilities.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public abstract class TextModule extends HudModule {
    protected List<String> lines = List.of();

    protected TextModule(String id, String name, float x, float y, boolean en) { super(id, name, x, y, en); }

    protected abstract List<String> compute(Minecraft mc);

    @Override public void tick(Minecraft mc) { lines = compute(mc); }

    private List<String> shown(boolean editing) {
        return lines.isEmpty() && editing ? List.of(name()) : lines;
    }

    @Override protected int contentWidth(Font f, boolean editing) {
        int w = 0;
        for (String s : shown(editing)) w = Math.max(w, f.width(s));
        return w;
    }

    @Override protected int contentHeight(Font f, boolean editing) {
        int n = shown(editing).size();
        return n == 0 ? 0 : n * (f.lineHeight + 1) - 1;
    }

    @Override protected void drawContent(GuiGraphics g, Font f, int x, int y, int rgb, float a, boolean editing) {
        int c = Theme.alpha(rgb, a), yy = y;
        for (String s : shown(editing)) {
            g.drawString(f, s, x, yy, c, true);
            yy += f.lineHeight + 1;
        }
    }
}
