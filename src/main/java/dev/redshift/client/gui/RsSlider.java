package dev.redshift.client.gui;

import dev.redshift.client.config.ConfigManager;
import dev.redshift.client.utilities.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.Locale;
import java.util.function.Consumer;

public class RsSlider extends AbstractSliderButton {
    private final String label, fmt;
    private final float min, max;
    private final Consumer<Float> setter;

    public RsSlider(int x, int y, int w, int h, String label, float min, float max, float cur, String fmt,
                    Consumer<Float> setter) {
        super(x, y, w, h, Component.empty(), (Math.max(min, Math.min(max, cur)) - min) / (max - min));
        this.label = label; this.fmt = fmt; this.min = min; this.max = max; this.setter = setter;
        updateMessage();
    }

    private float val() { return min + (float) value * (max - min); }

    @Override
    protected void updateMessage() {
        if (label == null) return;
        setMessage(Component.literal(label + ": " + String.format(Locale.ROOT, fmt, val())));
    }

    @Override
    protected void applyValue() {
        setter.accept(val());
        ConfigManager.markDirty();
    }

    @Override
    public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        g.fill(x, y, x + w, y + h, 0xFF140C20);
        int fill = (int) (value * (w - 2));
        g.fill(x + 1, y + 1, x + 1 + fill, y + h - 1, Theme.alpha(Theme.accentRgb(), 0.45f));
        g.fill(x + 1 + fill - 1, y, x + 1 + fill + 1, y + h, 0xFFFFFFFF);
        Theme.outline(g, x, y, w, h, Theme.alpha(Theme.PURPLE, isHoveredOrFocused() ? 0.8f : 0.35f));
        g.drawCenteredString(Minecraft.getInstance().font, getMessage(), x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
    }
}
