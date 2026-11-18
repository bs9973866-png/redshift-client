package dev.redshift.client.gui;

import dev.redshift.client.config.ModuleState;
import dev.redshift.client.hud.HudManager;
import dev.redshift.client.hud.HudModule;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ModulesScreen extends RsScreen {
    public ModulesScreen() { super("Visual Modules", Page.MODULES); }

    @Override
    protected void build() {
        List<HudModule> mods = HudManager.all();
        int colW = (cw - 8) / 2;
        for (int i = 0; i < mods.size(); i++) {
            HudModule m = mods.get(i);
            int x = cx + (i % 2) * (colW + 8), y = top() + (i / 2) * 22;
            ModuleState s = m.state();
            addRenderableWidget(Widgets.toggle(x, y, colW - 48, 20, m.name(), () -> m.state().enabled,
                    v -> m.state().enabled = v));
            addRenderableWidget(new RsButton(x + colW - 44, y, 44, 20, Component.literal("Edit"),
                    b -> minecraft.setScreen(new ModuleSettingsScreen(m))));
        }
    }
}
