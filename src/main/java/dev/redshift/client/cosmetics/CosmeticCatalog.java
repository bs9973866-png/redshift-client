package dev.redshift.client.cosmetics;

import com.google.gson.Gson;
import dev.redshift.client.RedshiftClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class CosmeticCatalog {
    private static final List<Cosmetic> ALL = new ArrayList<>();

    /** Формат записей в config/redshift/cosmetics.json */
    private static final class CustomDef {
        String id, name, category, rarity, particle;
        int count = 1;
    }

    private CosmeticCatalog() {}

    public static List<Cosmetic> all() { return ALL; }

    public static Cosmetic byId(String id) {
        for (Cosmetic c : ALL) if (c.id().equals(id)) return c;
        return null;
    }

    private static void add(String id, String name, CosmeticCategory cat, Rarity r, ParticleOptions p, int n) {
        ALL.add(new Cosmetic(id, name, cat, r, p, n));
    }

    public static void load() {
        ALL.clear();
        add("trail_flame", "Flame Trail", CosmeticCategory.TRAILS, Rarity.COMMON, ParticleTypes.FLAME, 1);
        add("trail_hearts", "Heart Trail", CosmeticCategory.TRAILS, Rarity.UNCOMMON, ParticleTypes.HEART, 1);
        add("trail_stardust", "Stardust Trail", CosmeticCategory.TRAILS, Rarity.RARE, ParticleTypes.END_ROD, 2);
        add("aura_soul", "Soul Aura", CosmeticCategory.AURAS, Rarity.RARE, ParticleTypes.SOUL_FIRE_FLAME, 2);
        add("aura_arcane", "Arcane Aura", CosmeticCategory.AURAS, Rarity.EPIC, ParticleTypes.ENCHANT, 4);
        add("part_cherry", "Cherry Petals", CosmeticCategory.PARTICLES, Rarity.UNCOMMON, ParticleTypes.CHERRY_LEAVES, 1);
        add("part_snow", "Snowfall", CosmeticCategory.PARTICLES, Rarity.RARE, ParticleTypes.SNOWFLAKE, 1);
        add("foot_cloud", "Cloud Steps", CosmeticCategory.FOOTSTEPS, Rarity.COMMON, ParticleTypes.CLOUD, 2);
        add("foot_note", "Melody Steps", CosmeticCategory.FOOTSTEPS, Rarity.RARE, ParticleTypes.NOTE, 1);
        add("amb_glow", "Glow Motes", CosmeticCategory.AMBIENT, Rarity.EPIC, ParticleTypes.GLOW, 1);
        loadCustom();
    }

    private static void loadCustom() {
        Path f = FabricLoader.getInstance().getConfigDir().resolve("redshift").resolve("cosmetics.json");
        try {
            if (!Files.exists(f)) {
                Files.createDirectories(f.getParent());
                Files.writeString(f, "[]");
                return;
            }
            CustomDef[] defs = new Gson().fromJson(Files.readString(f), CustomDef[].class);
            if (defs == null) return;
            for (CustomDef d : defs) {
                try {
                    CosmeticCategory cat = CosmeticCategory.valueOf(d.category.toUpperCase(Locale.ROOT));
                    if (!cat.isEffect()) throw new IllegalArgumentException("category is not an effect category");
                    Rarity r = Rarity.valueOf(d.rarity.toUpperCase(Locale.ROOT));
                    Identifier pid = Identifier.tryParse(d.particle);
                    Optional<ParticleType<?>> pt = pid == null ? Optional.empty() : BuiltInRegistries.PARTICLE_TYPE.getOptional(pid);
                    if (pt.isEmpty() || !(pt.get() instanceof SimpleParticleType simple))
                        throw new IllegalArgumentException("unknown or non-simple particle " + d.particle);
                    add("custom_" + d.id, d.name, cat, r, simple, Math.max(1, Math.min(8, d.count)));
                } catch (Exception e) {
                    RedshiftClient.LOGGER.warn("Skipping custom cosmetic: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            RedshiftClient.LOGGER.warn("Failed to read cosmetics.json", e);
        }
    }
}
