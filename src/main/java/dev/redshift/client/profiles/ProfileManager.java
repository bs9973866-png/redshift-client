package dev.redshift.client.profiles;

import dev.redshift.client.config.ConfigManager;
import dev.redshift.client.config.ModuleState;
import dev.redshift.client.config.ProfileData;
import dev.redshift.client.config.RedshiftConfig;
import dev.redshift.client.hud.HudManager;
import dev.redshift.client.utilities.NotificationManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import java.util.Locale;

public final class ProfileManager {
    private static String manualBefore;
    private static boolean auto;

    private ProfileManager() {}

    public static void init() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> onJoin(client));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> onLeave());
    }

    public static void createDefaults(RedshiftConfig cfg) {
        make(cfg, "default", 0xE0243C, new String[]{}, new String[]{});
        make(cfg, "funtime", 0xFF4D6D, new String[]{"funtime"}, new String[]{"keystrokes", "cps"});
        make(cfg, "reallyworld", 0x8A3FFC, new String[]{"reallyworld"}, new String[]{"armor", "potions"});
        make(cfg, "holyworld", 0xFFD54A, new String[]{"holyworld"}, new String[]{"coords", "direction"});
        make(cfg, "twixworld", 0x00D1FF, new String[]{"twixworld"}, new String[]{"ping", "fps"});
    }

    private static void make(RedshiftConfig cfg, String name, int accent, String[] addrs, String[] extraOn) {
        if (cfg.profiles.containsKey(name)) {
            cfg.profiles.get(name).builtin = true;
            return;
        }
        ProfileData p = new ProfileData();
        p.name = name; p.builtin = true; p.accent = accent;
        for (String a : addrs) p.addresses.add(a);
        HudManager.all().forEach(m -> {
            ModuleState s = m.defaultState();
            for (String id : extraOn) if (id.equals(m.id())) s.enabled = true;
            p.modules.put(m.id(), s);
        });
        cfg.profiles.put(name, p);
    }

    public static void switchTo(String name) {
        RedshiftConfig cfg = ConfigManager.get();
        if (!cfg.profiles.containsKey(name)) return;
        cfg.activeProfile = name;
        ConfigManager.markDirty();
        NotificationManager.push("Profile: " + name);
    }

    private static void onJoin(Minecraft mc) {
        RedshiftConfig cfg = ConfigManager.get();
        ServerData sd = mc.getCurrentServer();
        if (sd == null || !cfg.autoDetect || sd.ip == null) return;
        String ip = sd.ip.toLowerCase(Locale.ROOT);
        for (ProfileData p : cfg.profiles.values()) {
            for (String a : p.addresses) {
                if (!a.isBlank() && ip.contains(a.toLowerCase(Locale.ROOT))) {
                    if (!auto) manualBefore = cfg.activeProfile;
                    auto = true;
                    switchTo(p.name);
                    return;
                }
            }
        }
    }

    private static void onLeave() {
        if (!auto) return;
        auto = false;
        if (manualBefore != null) switchTo(manualBefore);
    }
}
