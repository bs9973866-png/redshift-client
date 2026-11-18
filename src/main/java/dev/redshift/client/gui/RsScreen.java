package dev.redshift.client.gui;

import dev.redshift.client.utilities.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

/** Базовый экран REDSHIFT: тёмный фон, боковое меню, контентная панель. */
public abstract class RsScreen extends Screen {
    public static final int SIDE_W = 116;

    public enum Page {
        MODULES("Visual Modules", ModulesScreen::new),
        COSMETICS("Cosmetics", CosmeticsScreen::new),
        ANIMATIONS("Animations", AnimationsScreen::new),
        HUD_EDITOR("HUD Editor", HudEditorScreen::new),
        PROFILES("Server Profiles", ProfilesScreen::new),
        SETTINGS("Settings", () -> new SettingsScreen(Page.SETTINGS, "")),
        PERFORMANCE("Performance", () -> new SettingsScreen(Page.PERFORMANCE, "Performance")),
        ABOUT("About REDSHIFT", AboutScreen::new);

        final String label;
        final Supplier<Screen> factory;

        Page(String label, Supplier<Screen> factory) { this.label = label; this.factory = factory; }
    }

    private final Page page;
    protected int cx, cw;

    protected RsScreen(String title, Page page) {
        super(Component.literal(title));
        this.page = page;
    }

    protected abstract void build();

    protected int top() { return 44; }

    @Override
    protected void init() {
        cx = SIDE_W + 14;
        cw = width - cx - 12;
        Page[] ps = Page.values();
        for (int i = 0; i < ps.length; i++) {
            Page p = ps[i];
            addRenderableWidget(new RsButton(8, 44 + i * 24, SIDE_W - 14, 20, Component.literal(p.label), b -> {
                if (p == Page.HUD_EDITOR && minecraft.player == null) return;
                minecraft.setScreen(p.factory.get());
            }));
        }
        build();
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void renderBackground(GuiGraphics g, int mx, int my, float pt) {
        g.fillGradient(0, 0, width, height, Theme.BG_TOP, Theme.BG_BOTTOM);
        // мягкое свечение
        g.fillGradient(0, height - 60, width, height, 0x00000000, Theme.alpha(Theme.PURPLE, 0.18f));
        g.fill(SIDE_W, 0, SIDE_W + 1, height, Theme.alpha(Theme.PURPLE, 0.5f));
        g.fill(cx - 6, 36, width - 6, height - 8, Theme.PANEL);
        Theme.outline(g, cx - 6, 36, width - 6 - (cx - 6), height - 8 - 36, Theme.alpha(Theme.PURPLE, 0.4f));
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.render(g, mx, my, pt);
        g.pose().pushMatrix();
        g.pose().translate(12, 10);
        g.pose().scale(1.6f, 1.6f);
        g.drawString(font, "RED", 0, 0, 0xFF000000 | Theme.RED, true);
        g.drawString(font, "SHIFT", font.width("RED"), 0, 0xFF000000 | Theme.PURPLE, true);
        g.pose().popMatrix();
        float pulse = 0.5f + 0.5f * (float) Math.sin(System.currentTimeMillis() / 400.0);
        g.fill(12, 28, 12 + (int) (50 + 40 * pulse), 30, Theme.alpha(Theme.RED, 0.6f + 0.4f * pulse));
        g.drawString(font, page.label, cx, 22, 0xFFFFFFFF, true);
        Page[] ps = Page.values();
        for (int i = 0; i < ps.length; i++) {
            if (ps[i] == page) g.fill(3, 44 + i * 24, 6, 64 + i * 24, Theme.accent());
        }
    }
}
