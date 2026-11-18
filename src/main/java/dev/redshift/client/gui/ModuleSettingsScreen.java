package dev.redshift.client.gui;

import dev.redshift.client.config.ModuleState;
import dev.redshift.client.hud.HudModule;
import dev.redshift.client.hud.OptionDef;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ModuleSettingsScreen extends RsScreen {
    private final HudModule module;

    public ModuleSettingsScreen(HudModule module) {
        super(module.name(), Page.MODULES);
        this.module = module;
    }

    @Override
    protected void build() {
        ModuleState s = module.state();
        int w = Math.min(cw, 260), y = top() + 14;
        addRenderableWidget(Widgets.toggle(cx, y, w, 20, "Enabled", () -> s.enabled, v -> s.enabled = v));
        y += 22;
        if (module.positionable()) {
            addRenderableWidget(new RsSlider(cx, y, w, 20, "Scale", 0.5f, 3f, s.scale, "%.2f", v -> s.scale = v));
            y += 22;
            addRenderableWidget(new RsSlider(cx, y, w, 20, "Opacity", 0.1f, 1f, s.opacity, "%.2f", v -> s.opacity = v));
            y += 22;
            addRenderableWidget(Widgets.toggle(cx, y, w, 20, "Background", () -> s.background, v -> s.background = v));
            y += 22;
        } else {
            addRenderableWidget(new RsSlider(cx, y, w, 20, "Opacity", 0.1f, 1f, s.opacity, "%.2f", v -> s.opacity = v));
            y += 22;
        }
        addRenderableWidget(Widgets.colorButton(cx, y, w, 20, s));
        y += 22;
        for (OptionDef d : module.options()) {
            float cur = s.options.getOrDefault(d.key(), d.def());
            addRenderableWidget(new RsSlider(cx, y, w, 20, d.label(), d.min(), d.max(), cur, d.fmt(),
                    v -> s.options.put(d.key(), v)));
            y += 22;
        }
        addRenderableWidget(new RsButton(cx, y + 4, 80, 20, Component.literal("Back"),
                b -> minecraft.setScreen(new ModulesScreen())));
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.render(g, mx, my, pt);
        g.drawString(font, module.name(), cx, top() + 2, 0xFFA79BB8, false);
    }
}
