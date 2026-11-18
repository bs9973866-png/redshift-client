package dev.redshift.client.cosmetics;

import dev.redshift.client.config.ConfigManager;
import dev.redshift.client.config.ProfileData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.RandomSource;

/** Клиентские частицы вокруг локального игрока. Только визуал. */
public final class CosmeticEffects {
    private static double lastX, lastZ, stepAccum;
    private static boolean hasLast;

    private CosmeticEffects() {}

    private static int count(float base, float dens, RandomSource r) {
        float f = base * dens;
        int n = (int) f;
        if (r.nextFloat() < f - n) n++;
        return n;
    }

    public static void tick(Minecraft mc) {
        LocalPlayer p = mc.player;
        double dx = hasLast ? p.getX() - lastX : 0, dz = hasLast ? p.getZ() - lastZ : 0;
        double moved = Math.sqrt(dx * dx + dz * dz);
        if (moved > 8) moved = 0;
        lastX = p.getX(); lastZ = p.getZ(); hasLast = true;

        ProfileData prof = ConfigManager.profile();
        if (prof.equipped.isEmpty()) return;

        if (p.onGround()) stepAccum += moved;
        boolean stepped = stepAccum > 0.7;
        if (stepped) stepAccum = 0;

        float dens = ConfigManager.get().effectDensity;
        if (ConfigManager.get().autoReduceEffects && mc.getFps() < 30) dens *= 0.5f;
        if (dens <= 0.01f) return;

        RandomSource r = mc.level.random;
        for (String id : prof.equipped) {
            Cosmetic c = CosmeticCatalog.byId(id);
            if (c == null || !CosmeticManager.unlocked(c) || c.category().mode == null) continue;
            int n = count(c.count(), dens, r);
            for (int i = 0; i < n; i++) {
                switch (c.category().mode) {
                    case TRAIL -> {
                        if (moved > 0.02)
                            mc.level.addParticle(c.particle(), p.getX() + (r.nextDouble() - 0.5) * 0.4,
                                    p.getY() + 0.1 + r.nextDouble() * 0.3, p.getZ() + (r.nextDouble() - 0.5) * 0.4, 0, 0.01, 0);
                    }
                    case AURA -> {
                        double ang = (p.tickCount + i * 2.1) * 0.25;
                        mc.level.addParticle(c.particle(), p.getX() + Math.cos(ang) * 0.7,
                                p.getY() + r.nextDouble() * 1.8, p.getZ() + Math.sin(ang) * 0.7, 0, 0.02, 0);
                    }
                    case PARTICLES -> mc.level.addParticle(c.particle(), p.getX() + (r.nextDouble() - 0.5),
                            p.getY() + r.nextDouble() * 2.2, p.getZ() + (r.nextDouble() - 0.5), 0, -0.01, 0);
                    case FOOTSTEPS -> {
                        if (stepped)
                            mc.level.addParticle(c.particle(), p.getX(), p.getY() + 0.05, p.getZ(), 0, 0.02, 0);
                    }
                    case AMBIENT -> mc.level.addParticle(c.particle(), p.getX() + (r.nextDouble() - 0.5) * 6,
                            p.getY() + (r.nextDouble() - 0.3) * 3, p.getZ() + (r.nextDouble() - 0.5) * 6, 0, 0.005, 0);
                }
            }
        }
    }
}
