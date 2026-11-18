package dev.redshift.client.gui;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphics;

public class AboutScreen extends RsScreen {
    private static final String[] LINES = {
            "REDSHIFT CLIENT - visual-only Fabric client.",
            "No cheats: no combat, movement or packet modifications.",
            "",
            "Implemented: HUD modules + editor, crosshair, swing animations,",
            "server profiles (auto-detect), particle cosmetics, local unlocks.",
            "Not implemented yet: 3D cosmetics (wings, capes, hats, pets...),",
            "custom hotbar/scoreboard/chat/bossbar/inventory/fonts, damage particles."
    };

    public AboutScreen() { super("About REDSHIFT", Page.ABOUT); }

    @Override protected void build() {}

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.render(g, mx, my, pt);
        String v = FabricLoader.getInstance().getModContainer("redshift")
                .map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("dev");
        g.drawString(font, "Version " + v, cx, top(), 0xFFFFFFFF, true);
        int y = top() + 16;
        for (String l : LINES) { g.drawString(font, l, cx, y, 0xFFA79BB8, false); y += 11; }
    }
}
