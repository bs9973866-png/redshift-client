package dev.redshift.client.hud;

import dev.redshift.client.config.ConfigManager;
import dev.redshift.client.gui.HudEditorScreen;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HudManager {
    public static final CrosshairModule CROSSHAIR = new CrosshairModule();
    private static final List<HudModule> MODULES = new ArrayList<>();

    static {
        MODULES.add(new Modules.Fps());
        MODULES.add(new Modules.Cps());
        MODULES.add(new Modules.Coords());
        MODULES.add(new Modules.Ping());
        MODULES.add(new Modules.Sprint());
        MODULES.add(new Modules.Direction_());
        MODULES.add(new Modules.Session());
        MODULES.add(new Modules.Items());
        MODULES.add(new Modules.Armor());
        MODULES.add(new Modules.Potions());
        MODULES.add(new Modules.Keystrokes());
        MODULES.add(new Modules.Notifications());
        MODULES.add(CROSSHAIR);
    }

    private HudManager() {}

    public static List<HudModule> all() { return Collections.unmodifiableList(MODULES); }

    public static void init() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("redshift", "hud"), (g, dt) -> render(g));
        HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, prev -> (g, dt) -> {
            if (CROSSHAIR.state().enabled) CROSSHAIR.draw(g);
            else prev.render(g, dt);
        });
    }

    private static void render(GuiGraphics g) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || !ConfigManager.get().hudVisible) return;
        if (mc.screen instanceof HudEditorScreen) return;
        int sw = g.guiWidth(), sh = g.guiHeight();
        for (HudModule m : MODULES) {
            if (m.positionable() && m.state().enabled) m.render(g, mc.font, sw, sh, false);
        }
    }

    public static void tickModules(Minecraft mc) {
        int interval = Math.max(1, ConfigManager.get().statsInterval);
        if (mc.player.tickCount % interval != 0) return;
        for (HudModule m : MODULES) if (m.positionable() && m.state().enabled) m.tick(mc);
    }
}
