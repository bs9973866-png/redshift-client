package dev.redshift.client.utilities;

import net.minecraft.client.Minecraft;

public final class SessionStats {
    private static final long START = System.currentTimeMillis();
    private static int deaths;
    private static boolean wasDead;

    private SessionStats() {}

    public static void tick(Minecraft mc) {
        boolean dead = mc.player != null && mc.player.isDeadOrDying();
        if (dead && !wasDead) deaths++;
        wasDead = dead;
    }

    public static int deaths() { return deaths; }

    public static String playtime() {
        long s = (System.currentTimeMillis() - START) / 1000;
        return String.format("%02d:%02d:%02d", s / 3600, (s / 60) % 60, s % 60);
    }
}
