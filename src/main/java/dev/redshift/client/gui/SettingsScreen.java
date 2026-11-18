package dev.redshift.client.gui;

import dev.redshift.client.config.ConfigManager;
import dev.redshift.client.config.ProfileData;
import dev.redshift.client.config.RedshiftConfig;
import dev.redshift.client.utilities.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SettingsScreen extends RsScreen {
    private interface Factory { AbstractWidget make(int x, int y, int w, int h); }
    private record Entry(String category, String label, Factory factory) {}

    private final List<AbstractWidget> entryWidgets = new ArrayList<>();
    private String filter;
    private EditBox search;

    public SettingsScreen(Page page, String initial) {
        super(page == Page.PERFORMANCE ? "Performance" : "Settings", page);
        this.filter = initial;
    }

    private List<Entry> entries() {
        RedshiftConfig c = ConfigManager.get();
        ProfileData p = ConfigManager.profile();
        List<Entry> e = new ArrayList<>();
        e.add(new Entry("HUD", "Show HUD", (x, y, w, h) -> Widgets.toggle(x, y, w, h, "Show HUD", () -> c.hudVisible, v -> c.hudVisible = v)));
        e.add(new Entry("Interface", "Notifications", (x, y, w, h) -> Widgets.toggle(x, y, w, h, "Notifications", () -> c.notifications, v -> c.notifications = v)));
        e.add(new Entry("Profiles", "Auto-detect server", (x, y, w, h) -> Widgets.toggle(x, y, w, h, "Auto-detect server", () -> c.autoDetect, v -> c.autoDetect = v)));
        e.add(new Entry("HUD", "Editor grid", (x, y, w, h) -> Widgets.toggle(x, y, w, h, "Editor grid", () -> p.showGrid, v -> p.showGrid = v)));
        e.add(new Entry("HUD", "Grid size", (x, y, w, h) -> new RsSlider(x, y, w, h, "Grid size", 4, 40, p.gridSize, "%.0f", v -> p.gridSize = Math.round(v))));
        e.add(new Entry("Performance", "Cosmetic effect density", (x, y, w, h) -> new RsSlider(x, y, w, h, "Effect density", 0f, 1f, c.effectDensity, "%.2f", v -> c.effectDensity = v)));
        e.add(new Entry("Performance", "Auto-reduce effects below 30 FPS", (x, y, w, h) -> Widgets.toggle(x, y, w, h, "Auto-reduce <30 FPS", () -> c.autoReduceEffects, v -> c.autoReduceEffects = v)));
        e.add(new Entry("Performance", "HUD update interval (ticks)", (x, y, w, h) -> new RsSlider(x, y, w, h, "HUD update (ticks)", 1, 20, c.statsInterval, "%.0f", v -> c.statsInterval = Math.max(1, Math.round(v)))));
        e.add(new Entry("Controls", "Menu key", (x, y, w, h) -> {
            RsButton b = new RsButton(x, y, w, h, Component.literal("Keys: Options > Controls"), bb -> {});
            b.active = false;
            return b;
        }));
        return e;
    }

    @Override
    protected void build() {
        entryWidgets.clear();
        search = new EditBox(font, cx, top(), cw, 18, Component.literal("Search"));
        search.setHint(Component.literal("Search settings..."));
        search.setValue(filter);
        search.setResponder(v -> { filter = v; refreshEntries(); });
        addRenderableWidget(search);
        refreshEntries();
    }

    private void refreshEntries() {
        for (AbstractWidget w : entryWidgets) removeWidget(w);
        entryWidgets.clear();
        String f = filter == null ? "" : filter.toLowerCase(Locale.ROOT).trim();
        int colW = (cw - 8) / 2, i = 0;
        for (Entry e : entries()) {
            if (!f.isEmpty() && !e.label().toLowerCase(Locale.ROOT).contains(f)
                    && !e.category().toLowerCase(Locale.ROOT).contains(f)) continue;
            int x = cx + (i % 2) * (colW + 8), y = top() + 24 + (i / 2) * 22;
            AbstractWidget w = e.factory().make(x, y, colW, 20);
            entryWidgets.add(w);
            addRenderableWidget(w);
            i++;
        }
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.render(g, mx, my, pt);
        g.drawString(font, "FPS: " + minecraft.getFps(), cx, height - 22, Theme.MUTED, false);
    }
}
