package dev.redshift.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.redshift.client.RedshiftClient;
import dev.redshift.client.hud.HudManager;
import dev.redshift.client.hud.HudModule;
import dev.redshift.client.hud.OptionDef;
import dev.redshift.client.profiles.ProfileManager;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("redshift.json");
    private static RedshiftConfig config = new RedshiftConfig();
    private static boolean dirty;
    private static int counter;

    private ConfigManager() {}

    public static RedshiftConfig get() { return config; }

    public static ProfileData profile() {
        ProfileData p = config.profiles.get(config.activeProfile);
        if (p == null) {
            config.activeProfile = "default";
            p = config.profiles.get("default");
        }
        return p;
    }

    public static ModuleState state(HudModule m) {
        return profile().modules.computeIfAbsent(m.id(), k -> m.defaultState());
    }

    public static ProfileData copyProfile(ProfileData src) {
        return GSON.fromJson(GSON.toJson(src), ProfileData.class);
    }

    public static void load() {
        if (Files.exists(FILE)) {
            try {
                RedshiftConfig loaded = GSON.fromJson(Files.readString(FILE), RedshiftConfig.class);
                if (loaded != null) config = loaded;
            } catch (Exception e) {
                RedshiftClient.LOGGER.error("Failed to read config, using defaults", e);
            }
        }
        ensureDefaults();
        flush();
    }

    private static void ensureDefaults() {
        ProfileManager.createDefaults(config);
        for (ProfileData p : config.profiles.values()) {
            if (p.swingSword == null) p.swingSword = new SwingSettings();
            if (p.swingOther == null) p.swingOther = new SwingSettings();
            for (HudModule m : HudManager.all()) {
                ModuleState s = p.modules.computeIfAbsent(m.id(), k -> m.defaultState());
                for (OptionDef d : m.options()) s.options.putIfAbsent(d.key(), d.def());
            }
        }
    }

    public static void markDirty() { dirty = true; }

    public static void tickSave() {
        if (dirty && ++counter >= 100) flush();
    }

    public static void flush() {
        counter = 0;
        dirty = false;
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(config));
        } catch (IOException e) {
            RedshiftClient.LOGGER.error("Failed to save config", e);
        }
    }
}
