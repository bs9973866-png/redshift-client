package dev.redshift.client.gui;

import dev.redshift.client.animations.SwingMode;
import dev.redshift.client.animations.SwingRenderer;
import dev.redshift.client.config.ConfigManager;
import dev.redshift.client.config.ProfileData;
import dev.redshift.client.config.SwingSettings;
import dev.redshift.client.utilities.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AnimationsScreen extends RsScreen {
    private static boolean sword = true;
    private static boolean advanced = false;
    private ItemStack icon;

    public AnimationsScreen() { super("Swing Animations", Page.ANIMATIONS); }

    private SwingSettings cur() {
        ProfileData p = ConfigManager.profile();
        return sword ? p.swingSword : p.swingOther;
    }

    @Override
    protected void build() {
        ProfileData p = ConfigManager.profile();
        SwingSettings s = cur();
        icon = new ItemStack(sword ? Items.DIAMOND_SWORD : Items.DIAMOND_PICKAXE);
        int colW = (cw - 8) / 2, lx = cx, rx = cx + colW + 8, y = top();
        addRenderableWidget(Widgets.toggle(lx, y, colW, 20, "Swing animations", () -> p.swingEnabled, v -> p.swingEnabled = v));
        y += 22;
        addRenderableWidget(new RsButton(lx, y, colW, 20, Component.literal("Editing: " + (sword ? "Swords" : "Other items")),
                b -> { sword = !sword; rebuildWidgets(); }));
        y += 22;
        addRenderableWidget(Widgets.toggle(lx, y, colW, 20, "Enabled for these", () -> s.enabled, v -> s.enabled = v));
        y += 22;
        addRenderableWidget(new RsButton(lx, y, colW, 20, Component.literal("Page: " + (advanced ? "Offsets / Custom" : "Basic")),
                b -> { advanced = !advanced; rebuildWidgets(); }));
        y += 22;
        int y0 = y;
        if (!advanced) {
            addRenderableWidget(new RsButton(lx, y, colW, 20, Component.literal("Mode: " + s.mode), b -> {
                SwingMode[] m = SwingMode.values();
                s.mode = m[(s.mode.ordinal() + 1) % m.length];
                b.setMessage(Component.literal("Mode: " + s.mode));
                ConfigManager.markDirty();
            }));
            y += 22;
            addRenderableWidget(new RsSlider(lx, y, colW, 20, "Swing speed", 1f, 3f, s.speed, "%.2fx", v -> s.speed = v)); y += 22;
            addRenderableWidget(new RsSlider(lx, y, colW, 20, "Angle %", 25f, 200f, s.angle, "%.0f", v -> s.angle = v)); y += 22;
            addRenderableWidget(new RsSlider(lx, y, colW, 20, "Smoothness", 0f, 1f, s.smoothness, "%.2f", v -> s.smoothness = v)); y += 22;
            addRenderableWidget(new RsSlider(lx, y, colW, 20, "Intensity", 0f, 2f, s.intensity, "%.2f", v -> s.intensity = v)); y += 22;
            addRenderableWidget(new RsButton(lx, y, colW, 20, Component.literal("Reset to vanilla"), b -> {
                if (sword) p.swingSword = SwingSettings.vanilla(); else p.swingOther = SwingSettings.vanilla();
                ConfigManager.markDirty();
                rebuildWidgets();
            }));
        } else {
            addRenderableWidget(new RsSlider(lx, y, colW, 20, "Hand X", -0.5f, 0.5f, s.handX, "%.2f", v -> s.handX = v)); y += 22;
            addRenderableWidget(new RsSlider(lx, y, colW, 20, "Hand Y", -0.5f, 0.5f, s.handY, "%.2f", v -> s.handY = v)); y += 22;
            addRenderableWidget(new RsSlider(lx, y, colW, 20, "Hand Z", -0.5f, 0.5f, s.handZ, "%.2f", v -> s.handZ = v)); y += 22;
            addRenderableWidget(new RsSlider(lx, y, colW, 20, "Item X", -0.5f, 0.5f, s.itemX, "%.2f", v -> s.itemX = v)); y += 22;
            addRenderableWidget(new RsSlider(lx, y, colW, 20, "Item Y", -0.5f, 0.5f, s.itemY, "%.2f", v -> s.itemY = v)); y += 22;
            addRenderableWidget(new RsSlider(lx, y, colW, 20, "Item Z", -0.5f, 0.5f, s.itemZ, "%.2f", v -> s.itemZ = v));
            int ry = y0;
            addRenderableWidget(new RsSlider(rx, ry, colW, 20, "Custom rot X", -180f, 180f, s.customRotX, "%.0f", v -> s.customRotX = v)); ry += 22;
            addRenderableWidget(new RsSlider(rx, ry, colW, 20, "Custom rot Y", -180f, 180f, s.customRotY, "%.0f", v -> s.customRotY = v)); ry += 22;
            addRenderableWidget(new RsSlider(rx, ry, colW, 20, "Custom rot Z", -180f, 180f, s.customRotZ, "%.0f", v -> s.customRotZ = v)); ry += 22;
            addRenderableWidget(new RsSlider(rx, ry, colW, 20, "Custom move X", -0.6f, 0.6f, s.customMoveX, "%.2f", v -> s.customMoveX = v)); ry += 22;
            addRenderableWidget(new RsSlider(rx, ry, colW, 20, "Custom move Y", -0.6f, 0.6f, s.customMoveY, "%.2f", v -> s.customMoveY = v)); ry += 22;
            addRenderableWidget(new RsSlider(rx, ry, colW, 20, "Custom move Z", -0.6f, 0.6f, s.customMoveZ, "%.2f", v -> s.customMoveZ = v));
        }
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.render(g, mx, my, pt);
        if (advanced) {
            g.drawString(font, "Custom values apply when Mode = CUSTOM", cx + (cw - 8) / 2 + 8, top() + 88 - 12, Theme.MUTED, false);
            return;
        }
        int colW = (cw - 8) / 2, px = cx + colW + 8, py = top(), ph = 110;
        g.fill(px, py, px + colW, py + ph, 0xFF0D0716);
        Theme.outline(g, px, py, colW, ph, Theme.alpha(Theme.PURPLE, 0.5f));
        g.drawString(font, "Preview (2D approximation)", px + 4, py + 4, Theme.MUTED, false);

        SwingSettings s = cur();
        float p = (System.currentTimeMillis() % 1400L) / 1400f;
        float q = SwingRenderer.shape(s, p);
        float sw = (float) Math.sin(q * Math.PI), g1 = (float) Math.sin(Math.sqrt(q) * Math.PI);
        float a = s.angle / 100f, in = s.intensity, dx = 0, dy = 0, rot;
        switch (s.mode) {
            case SPIN -> rot = 360f * Math.max(1, Math.round(a * in)) * q;
            case SLIDE -> { rot = -10f * sw; dx = sw * 22 * in; dy = -sw * 6; }
            case CUSTOM -> { rot = s.customRotZ * sw + s.customRotX * 0.4f * sw; dx = s.customMoveX * sw * 60; dy = s.customMoveZ * sw * 60; }
            case CLASSIC -> rot = -g1 * 70f;
            default -> rot = -g1 * 70f * a * in;
        }
        if (!ConfigManager.profile().swingEnabled || !s.enabled) rot = -g1 * 70f;
        g.pose().pushMatrix();
        g.pose().translate(px + colW / 2f + dx, py + ph / 2f + 8 + dy);
        g.pose().rotate((float) Math.toRadians(rot));
        g.pose().scale(3f, 3f);
        g.renderItem(icon, -8, -8);
        g.pose().popMatrix();
    }
}
