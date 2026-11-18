package dev.redshift.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.redshift.client.config.ConfigManager;
import dev.redshift.client.cosmetics.CosmeticCatalog;
import dev.redshift.client.cosmetics.CosmeticManager;
import dev.redshift.client.gui.HudEditorScreen;
import dev.redshift.client.gui.ModulesScreen;
import dev.redshift.client.hud.HudManager;
import dev.redshift.client.profiles.ProfileManager;
import dev.redshift.client.utilities.ClickTracker;
import dev.redshift.client.utilities.NotificationManager;
import dev.redshift.client.utilities.SessionStats;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RedshiftClient implements ClientModInitializer {
    public static final String MOD_ID = "redshift";
    public static final Logger LOGGER = LoggerFactory.getLogger("REDSHIFT");

    private static KeyMapping menuKey, editorKey, hudKey;

    @Override
    public void onInitializeClient() {
        ConfigManager.load();
        CosmeticCatalog.load();
        HudManager.init();
        ProfileManager.init();

        menuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.redshift.menu",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, KeyMapping.Category.MISC));
        editorKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.redshift.editor",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KeyMapping.Category.MISC));
        hudKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.redshift.hud",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KeyMapping.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(RedshiftClient::tick);
        ClientLifecycleEvents.CLIENT_STOPPING.register(c -> ConfigManager.flush());
        LOGGER.info("REDSHIFT CLIENT initialized (visual-only client)");
    }

    private static void tick(Minecraft mc) {
        while (menuKey.consumeClick()) if (mc.screen == null) mc.setScreen(new ModulesScreen());
        while (editorKey.consumeClick()) if (mc.screen == null && mc.player != null) mc.setScreen(new HudEditorScreen());
        while (hudKey.consumeClick()) {
            ConfigManager.get().hudVisible = !ConfigManager.get().hudVisible;
            ConfigManager.markDirty();
        }
        if (mc.player == null || mc.level == null) {
            ConfigManager.tickSave();
            return;
        }
        ClickTracker.tick(mc);
        SessionStats.tick(mc);
        HudManager.tickModules(mc);
        NotificationManager.tick();
        CosmeticManager.tick(mc);
        ConfigManager.tickSave();
    }
}
