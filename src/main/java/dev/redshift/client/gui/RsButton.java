package dev.redshift.client.gui;

import dev.redshift.client.utilities.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class RsButton extends Button {
    private float hover;

    public RsButton(int x, int y, int w, int h, Component msg, OnPress press) {
        super(x, y, w, h, msg, press, DEFAULT_NARRATION);
    }

    @Override
    public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
        boolean h = isHoveredOrFocused() && active;
        hover += ((h ? 1f : 0f) - hover) * 0.25f;
        int x = getX(), y = getY(), w = getWidth(), hh = getHeight();
        int base = Theme.lerp(0xFF1B1030, 0xFF3A1A66, hover);
        g.fill(x, y, x + w, y + hh, active ? base : 0xFF140C20);
        if (hover > 0.01f) g.fill(x, y + hh - 2, x + w, y + hh, Theme.alpha(Theme.accentRgb(), hover));
        Theme.outline(g, x, y, w, hh, Theme.alpha(Theme.PURPLE, 0.35f + 0.4f * hover));
        g.drawCenteredString(Minecraft.getInstance().font, getMessage(), x + w / 2, y + (hh - 8) / 2,
                active ? 0xFFFFFFFF : 0xFF777777);
    }
}
