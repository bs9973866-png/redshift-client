package dev.redshift.client.gui;

import dev.redshift.client.config.ConfigManager;
import dev.redshift.client.config.ModuleState;
import dev.redshift.client.config.ProfileData;
import dev.redshift.client.hud.HudManager;
import dev.redshift.client.hud.HudModule;
import dev.redshift.client.utilities.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;

public class HudEditorScreen extends Screen {
    private HudModule selected, dragging;
    private float grabX, grabY;
    private boolean snapped;

    public HudEditorScreen() { super(Component.literal("HUD Editor")); }

    @Override
    protected void init() {
        ProfileData prof = ConfigManager.profile();
        int y = height - 26, x = Math.max(4, width / 2 - 250), h = 20;
        addRenderableWidget(Widgets.toggle(x, y, 80, h, "Grid", () -> prof.showGrid, v -> prof.showGrid = v));
        addRenderableWidget(new RsButton(x + 84, y, 50, h, Component.literal("Scale -"), b -> adjust(-0.1f, 0)));
        addRenderableWidget(new RsButton(x + 136, y, 50, h, Component.literal("Scale +"), b -> adjust(0.1f, 0)));
        addRenderableWidget(new RsButton(x + 190, y, 58, h, Component.literal("Alpha -"), b -> adjust(0, -0.1f)));
        addRenderableWidget(new RsButton(x + 250, y, 58, h, Component.literal("Alpha +"), b -> adjust(0, 0.1f)));
        addRenderableWidget(new RsButton(x + 312, y, 56, h, Component.literal("Reset"), b -> reset()));
        addRenderableWidget(new RsButton(x + 372, y, 60, h, Component.literal("Modules"),
                b -> minecraft.setScreen(new ModulesScreen())));
        addRenderableWidget(new RsButton(x + 436, y, 50, h, Component.literal("Done"), b -> onClose()));
    }

    private void adjust(float ds, float da) {
        if (selected == null) return;
        ModuleState s = selected.state();
        s.scale = Math.max(0.5f, Math.min(3f, s.scale + ds));
        s.opacity = Math.max(0.1f, Math.min(1f, s.opacity + da));
        ConfigManager.markDirty();
    }

    private void reset() {
        for (HudModule m : HudManager.all()) {
            ModuleState d = m.defaultState(), s = m.state();
            s.x = d.x; s.y = d.y; s.scale = 1f; s.opacity = 1f;
        }
        ConfigManager.markDirty();
    }

    private List<HudModule> mods() { return HudManager.all(); }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void renderBackground(GuiGraphics g, int mx, int my, float pt) {
        g.fill(0, 0, width, height, 0x88060309);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        ProfileData prof = ConfigManager.profile();
        super.render(g, mx, my, pt);
        if (prof.showGrid) {
            int gs = Math.max(4, prof.gridSize);
            for (int x = 0; x < width; x += gs) g.fill(x, 0, x + 1, height, 0x14FFFFFF);
            for (int y = 0; y < height; y += gs) g.fill(0, y, width, y + 1, 0x14FFFFFF);
        }
        g.fill(width / 2, 0, width / 2 + 1, height, 0x22FFFFFF);
        g.fill(0, height / 2, width, height / 2 + 1, 0x22FFFFFF);
        for (HudModule m : mods()) {
            if (!m.positionable() || !m.state().enabled) continue;
            m.render(g, font, width, height, true);
            float[] b = m.bounds(font, width, height);
            int col = m == selected ? Theme.accent() : 0x66FFFFFF;
            Theme.outline(g, (int) b[0], (int) b[1], Math.max(2, (int) b[2]), Math.max(2, (int) b[3]), col);
        }
        if (snapped && dragging != null) g.fill(width / 2, 0, width / 2 + 1, height, Theme.accent());
        g.drawCenteredString(font, "Drag modules | Scroll over selected = scale | " + prof.name + " profile",
                width / 2, 6, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
        if (super.mouseClicked(e, dbl)) return true;
        if (e.button() != 0) return false;
        List<HudModule> mods = mods();
        for (int i = mods.size() - 1; i >= 0; i--) {
            HudModule m = mods.get(i);
            if (!m.positionable() || !m.state().enabled) continue;
            float[] b = m.bounds(font, width, height);
            if (e.x() >= b[0] && e.x() <= b[0] + b[2] && e.y() >= b[1] && e.y() <= b[1] + b[3]) {
                selected = dragging = m;
                grabX = (float) e.x() - b[0];
                grabY = (float) e.y() - b[1];
                return true;
            }
        }
        selected = null;
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
        if (dragging == null) return super.mouseDragged(e, dx, dy);
        ProfileData prof = ConfigManager.profile();
        float[] b = dragging.bounds(font, width, height);
        float nx = (float) e.x() - grabX, ny = (float) e.y() - grabY;
        if (prof.showGrid) {
            int gs = Math.max(4, prof.gridSize);
            nx = Math.round(nx / gs) * gs;
            ny = Math.round(ny / gs) * gs;
        }
        snapped = Math.abs(nx + b[2] / 2 - width / 2f) < 4;
        if (snapped) nx = width / 2f - b[2] / 2;
        nx = Math.max(0, Math.min(width - b[2], nx));
        ny = Math.max(0, Math.min(height - b[3], ny));
        ModuleState s = dragging.state();
        s.x = nx / width;
        s.y = ny / height;
        ConfigManager.markDirty();
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent e) {
        dragging = null;
        snapped = false;
        return super.mouseReleased(e);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double sx, double sy) {
        if (selected != null) {
            adjust((float) sy * 0.05f, 0);
            return true;
        }
        return super.mouseScrolled(mx, my, sx, sy);
    }
}
