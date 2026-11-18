package dev.redshift.client.cosmetics;

import net.minecraft.core.particles.ParticleOptions;

public record Cosmetic(String id, String name, CosmeticCategory category, Rarity rarity,
                       ParticleOptions particle, int count) {}
