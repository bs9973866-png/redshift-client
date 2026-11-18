package dev.redshift.client.cosmetics;

import dev.redshift.client.config.ConfigManager;
import dev.redshift.client.config.ProfileData;
import dev.redshift.client.config.RedshiftConfig;
import dev.redshift.client.utilities.NotificationManager;
import net.minecraft.client.Minecraft;

public final class CosmeticManager {
    private CosmeticManager() {}

    public static boolean unlocked(Cosmetic c) { return ConfigManager.get().owned.contains(c.id()); }
    public static boolean favorite(Cosmetic c) { return ConfigManager.get().favorites.contains(c.id()); }
    public static boolean equipped(Cosmetic c) { return ConfigManager.profile().equipped.contains(c.id()); }

    public static void toggleFavorite(Cosmetic c) {
        var fav = ConfigManager.get().favorites;
        if (!fav.remove(c.id())) fav.add(c.id());
        ConfigManager.markDirty();
    }

    public static void toggleEquip(Cosmetic c) {
        ProfileData p = ConfigManager.profile();
        if (p.equipped.remove(c.id())) { ConfigManager.markDirty(); return; }
        if (!unlocked(c)) return;
        p.equipped.removeIf(id -> {
            Cosmetic o = CosmeticCatalog.byId(id);
            return o != null && o.category() == c.category();
        });
        p.equipped.add(c.id());
        ConfigManager.markDirty();
    }

    public static void tick(Minecraft mc) {
        RedshiftConfig cfg = ConfigManager.get();
        if (mc.player.tickCount % 20 == 0) {
            cfg.playtimeSeconds++;
            for (Cosmetic c : CosmeticCatalog.all()) {
                if (!cfg.owned.contains(c.id()) && c.rarity().unlockMinutes * 60L <= cfg.playtimeSeconds) {
                    cfg.owned.add(c.id());
                    NotificationManager.push("Unlocked: " + c.name());
                }
            }
            ConfigManager.markDirty();
        }
        CosmeticEffects.tick(mc);
    }
}
